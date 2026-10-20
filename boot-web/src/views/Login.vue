<script setup>
import { ref, inject } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { api, auth } from '../api'

const router = useRouter()
const route = useRoute()
const toast = inject('toast')
const refreshCart = inject('refreshCart')
const refreshUser = inject('refreshUser')

const form = ref({ username: '', password: '' })
const loading = ref(false)
const errorMsg = ref('')
const useDemo = () => {
  form.value.username = 'test'
  form.value.password = '123456'
}

async function submit() {
  errorMsg.value = ''
  if (!form.value.username.trim() || !form.value.password) {
    errorMsg.value = '请填写用户名和密码'
    return
  }
  loading.value = true
  try {
    const data = await api.login({
      username: form.value.username.trim(),
      password: form.value.password
    })
    auth.save(data)
    refreshUser()
    await refreshCart()
    toast(`欢迎回来，${data.virtualName || data.username}`, 'success')
    router.replace(route.query.redirect || '/')
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
      <h1>登录</h1>
      <p class="muted sub">登录后可以加购、下单、查看订单</p>

      <form @submit.prevent="submit">
        <div class="field">
          <label>用户名</label>
          <input v-model="form.username" type="text" placeholder="请输入用户名" autocomplete="username" />
        </div>
        <div class="field">
          <label>密码</label>
          <input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            autocomplete="current-password"
          />
        </div>

        <p v-if="errorMsg" class="err-box">{{ errorMsg }}</p>

        <button type="submit" class="btn btn-primary full" :disabled="loading">
          <span v-if="loading" class="spinner"></span>
          <span>{{ loading ? '登录中…' : '登录' }}</span>
        </button>
      </form>

      <div class="auth-foot">
        <span class="muted">还没有账号？</span>
        <router-link to="/register" class="link">立即注册</router-link>
      </div>
      <button class="btn btn-ghost full demo" @click="useDemo">使用演示账号 test / 123456</button>
    </div>
  </div>
</template>

<style scoped>
.auth-wrap {
  display: flex;
  justify-content: center;
  padding: 40px 0 60px;
}

.auth-card {
  width: 100%;
  max-width: 400px;
  padding: 30px 28px 24px;
}

h1 {
  font-size: 22px;
  margin: 0 0 6px;
}

.sub {
  margin: 0 0 22px;
  font-size: 13px;
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

.demo {
  margin-top: 12px;
  color: var(--text-3);
  font-size: 13px;
}
</style>
