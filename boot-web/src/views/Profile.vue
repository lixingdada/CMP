<script setup>
import { ref, onMounted, inject } from 'vue'
import { api } from '../api'

const toast = inject('toast')

const loading = ref(true)
const saving = ref(false)
const errorMsg = ref('')
const profile = ref({})
const pwd = ref({ oldPassword: '', newPassword: '', confirm: '' })
const pwdLoading = ref(false)
const pwdError = ref('')
const logs = ref([])

async function load() {
  loading.value = true
  try {
    profile.value = await api.profile()
    logs.value = await api.logs(10)
  } catch (e) {
    errorMsg.value = e.message
  } finally {
    loading.value = false
  }
}

async function save() {
  saving.value = true
  errorMsg.value = ''
  try {
    profile.value = await api.updateProfile({
      virtualName: profile.value.virtualName || undefined,
      birthday: profile.value.birthday || undefined,
      email: profile.value.email || undefined,
      phone: profile.value.phone || undefined,
      receiverName: profile.value.receiverName || undefined,
      receiverPhone: profile.value.receiverPhone || undefined,
      receiverAddr: profile.value.receiverAddr || undefined
    })
    toast('资料已保存', 'success')
  } catch (e) {
    errorMsg.value = e.message
    toast(e.message, 'error')
  } finally {
    saving.value = false
  }
}

async function changePassword() {
  pwdError.value = ''
  if (pwd.value.newPassword.length < 6) {
    pwdError.value = '新密码至少 6 位'
    return
  }
  if (pwd.value.newPassword !== pwd.value.confirm) {
    pwdError.value = '两次输入的新密码不一致'
    return
  }
  pwdLoading.value = true
  try {
    await api.changePassword({
      oldPassword: pwd.value.oldPassword,
      newPassword: pwd.value.newPassword
    })
    pwd.value = { oldPassword: '', newPassword: '', confirm: '' }
    toast('密码已修改', 'success')
  } catch (e) {
    pwdError.value = e.message
  } finally {
    pwdLoading.value = false
  }
}

function formatTime(t) {
  return t ? String(t).replace('T', ' ').slice(0, 19) : ''
}

onMounted(load)
</script>

<template>
  <div v-if="loading" class="card panel">
    <div class="skeleton" style="height: 200px"></div>
  </div>

  <div v-else class="grid">
    <div class="card panel">
      <h2>个人资料</h2>
      <p class="muted sub">用户名 {{ profile.userId }}（不可修改）</p>

      <div class="row">
        <div class="field">
          <label>昵称</label>
          <input v-model="profile.virtualName" type="text" placeholder="显示名称" />
        </div>
        <div class="field">
          <label>生日</label>
          <input v-model="profile.birthday" type="date" />
        </div>
      </div>
      <div class="row">
        <div class="field">
          <label>邮箱</label>
          <input v-model="profile.email" type="text" />
        </div>
        <div class="field">
          <label>手机号</label>
          <input v-model="profile.phone" type="text" />
        </div>
      </div>

      <h2 class="mt">默认收货信息</h2>
      <div class="field">
        <label>收货人</label>
        <input v-model="profile.receiverName" type="text" />
      </div>
      <div class="field">
        <label>收货电话</label>
        <input v-model="profile.receiverPhone" type="text" />
      </div>
      <div class="field">
        <label>收货地址</label>
        <textarea v-model="profile.receiverAddr" rows="2"></textarea>
      </div>

      <p v-if="errorMsg" class="err-box">{{ errorMsg }}</p>
      <button class="btn btn-primary" :disabled="saving" @click="save">
        {{ saving ? '保存中…' : '保存资料' }}
      </button>
    </div>

    <div class="side">
      <div class="card panel">
        <h2>修改密码</h2>
        <div class="field">
          <label>原密码</label>
          <input v-model="pwd.oldPassword" type="password" placeholder="上次登录用的密码" />
        </div>
        <div class="field">
          <label>新密码</label>
          <input v-model="pwd.newPassword" type="password" placeholder="至少 6 位" />
        </div>
        <div class="field">
          <label>确认新密码</label>
          <input v-model="pwd.confirm" type="password" />
        </div>
        <p v-if="pwdError" class="err-box">{{ pwdError }}</p>
        <button class="btn btn-ghost full" :disabled="pwdLoading" @click="changePassword">
          {{ pwdLoading ? '提交中…' : '修改密码' }}
        </button>
      </div>

      <div class="card panel">
        <h2>最近操作</h2>
        <ul v-if="logs.length" class="logs">
          <li v-for="log in logs" :key="log.logId">
            <span class="tag">{{ log.action }}</span>
            <span class="log-time muted">{{ formatTime(log.actionTime) }}</span>
          </li>
        </ul>
        <p v-else class="muted small">暂无记录</p>
        <p class="muted small tip">日志由 AOP 切面在方法返回后自动写入，业务代码里没有一行日志相关逻辑。</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.grid {
  display: grid;
  grid-template-columns: 1fr 340px;
  gap: 16px;
  align-items: start;
}

.panel {
  padding: 22px;
}

h2 {
  font-size: 15px;
  margin: 0 0 4px;
}

h2.mt {
  margin-top: 8px;
}

.sub {
  margin: 0 0 18px;
  font-size: 12px;
}

.row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.side {
  display: flex;
  flex-direction: column;
  gap: 16px;
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

.logs {
  list-style: none;
  padding: 0;
  margin: 10px 0 0;
}

.logs li {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 7px 0;
  border-bottom: 1px solid var(--line);
  font-size: 13px;
}

.logs li:last-child {
  border-bottom: none;
}

.log-time {
  font-size: 12px;
}

.tip {
  margin: 14px 0 0;
  line-height: 1.5;
}

.small {
  font-size: 12px;
}

@media (max-width: 900px) {
  .grid {
    grid-template-columns: 1fr;
  }
  .row {
    grid-template-columns: 1fr;
  }
}
</style>
