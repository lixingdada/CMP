<script setup>
import { ref, inject } from 'vue'
import { useRouter } from 'vue-router'
import { api, auth } from '../api'

const router = useRouter()
const toast = inject('toast')
const refreshCart = inject('refreshCart')
const refreshUser = inject('refreshUser')

const form = ref({
  username: '',
  password: '',
  confirm: '',
  virtualName: '',
  email: '',
  phone: ''
})
const loading = ref(false)
const errorMsg = ref('')

async function submit() {
  errorMsg.value = ''
  if (!/^[A-Za-z0-9_]{3,20}$/.test(form.value.username)) {
    errorMsg.value = '用户名需为 3~20 位字母、数字或下划线'
    return
  }
  if (form.value.password.length < 6) {
    errorMsg.value = '密码至少 6 位'
    return
  }
  if (form.value.password !== form.value.confirm) {
    errorMsg.value = '两次输入的密码不一致'
    return
  }

  loading.value = true
  try {
    const data = await api.register({
      username: form.value.username,
      password: form.value.password,
      virtualName: form.value.virtualName || undefined,
      email: form.value.email || undefined,
      phone: form.value.phone || undefined
    })
    // 注册接口直接返回 token，省去再登录一次
    auth.save(data)
    refreshUser()
    await refreshCart()
    toast('注册成功，已自动登录', 'success')
    router.replace('/')
  } catch (e) {
    errorMsg.value = e.message
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="auth-wrap">
    <div class="card auth-card">
      <h1>注册</h1>
      <p class="muted sub">注册即自动创建购物车</p>

      <form @submit.prevent="submit">
        <div class="field">
          <label>用户名 <span class="req">*</span></label>
          <input v-model="form.username" type="text" placeholder="3~20 位字母、数字或下划线" />
        </div>
        <div class="row">
          <div class="field">
            <label>密码 <span class="req">*</span></label>
            <input v-model="form.password" type="password" placeholder="至少 6 位" />
          </div>
          <div class="field">
            <label>确认密码 <span class="req">*</span></label>
            <input v-model="form.confirm" type="password" placeholder="再输一次" />
          </div>
        </div>
        <div class="field">
          <label>昵称</label>
          <input v-model="form.virtualName" type="text" placeholder="不填则默认使用用户名" />
        </div>
        <div class="row">
          <div class="field">
            <label>邮箱</label>
            <input v-model="form.email" type="text" placeholder="选填" />
          </div>
          <div class="field">
            <label>手机号</label>
            <input v-model="form.phone" type="text" placeholder="选填" />
          </div>
        </div>

        <p v-if="errorMsg" class="err-box">{{ errorMsg }}</p>

        <button type="submit" class="btn btn-primary full" :disabled="loading">
          <span v-if="loading" class="spinner"></span>
          <span>{{ loading ? '提交中…' : '注册并登录' }}</span>
        </button>
      </form>

      <div class="auth-foot">
        <span class="muted">已有账号？</span>
        <router-link to="/login" class="link">去登录</router-link>
      </div>
    </div>
  </div>
</template>

<style scoped>
.auth-wrap {
  display: flex;
  justify-content: center;
  padding: 30px 0 60px;
}

.auth-card {
  width: 100%;
  max-width: 480px;
  padding: 30px 28px 24px;
}

h1 {
  font-size: 22px;
  margin: 0 0 6px;
}

.sub {
  margin: 0 0 20px;
  font-size: 13px;
}

.row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.req {
  color: var(--danger);
}

.full {
  width: 100%;
}

.err-box {
  margin: 0 0 14px;
  padding: 9px 12px;
  border-radius: 8px;
  background: #fdecec;
  color: var(--danger);
  font-size: 13px;
}

.auth-foot {
  margin-top: 16px;
  text-align: center;
  font-size: 13px;
}

.link {
  color: var(--primary);
  margin-left: 4px;
}

@media (max-width: 560px) {
  .row {
    grid-template-columns: 1fr;
  }
}
</style>
