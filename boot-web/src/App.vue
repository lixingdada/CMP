<script setup>
import { ref, provide, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { api, auth, setUnauthorizedHandler } from './api'

const router = useRouter()
const route = useRoute()

const toasts = ref([])
const cartCount = ref(0)
const user = ref(auth.user)
const keyword = ref('')

function toast(message, type = 'info') {
  const id = Date.now() + Math.random()
  toasts.value.push({ id, message, type })
  setTimeout(() => {
    toasts.value = toasts.value.filter((t) => t.id !== id)
  }, 2800)
}

async function refreshCart() {
  if (!auth.isLogin) {
    cartCount.value = 0
    return
  }
  try {
    const cart = await api.getCart()
    cartCount.value = cart?.totalQuantity || 0
  } catch {
    cartCount.value = 0
  }
}

function refreshUser() {
  user.value = auth.user
}

function logout() {
  auth.clear()
  user.value = null
  cartCount.value = 0
  toast('已退出登录')
  router.push('/')
}

function onSearch() {
  router.push({ name: 'home', query: keyword.value ? { keyword: keyword.value } : {} })
}

// 提供给所有子页面使用
provide('toast', toast)
provide('refreshCart', refreshCart)
// 登录 / 退出后通知顶栏重新读取本地用户信息
provide('refreshUser', refreshUser)

onMounted(() => {
  setUnauthorizedHandler(() => {
    user.value = null
    cartCount.value = 0
    toast('登录已过期，请重新登录', 'warn')
    if (route.name !== 'login') {
      router.push({ name: 'login', query: { redirect: route.fullPath } })
    }
  })
  refreshCart()
})
</script>

<template>
  <div class="layout">
    <header class="topbar">
      <div class="topbar-inner">
        <router-link to="/" class="logo">
          <span class="logo-mark">CMP</span>
          <span class="logo-text">码农天堂</span>
        </router-link>

        <form class="search" @submit.prevent="onSearch">
          <input v-model="keyword" type="text" placeholder="搜索商品名称或描述…" />
          <button type="submit" class="btn btn-primary">搜索</button>
        </form>

        <nav class="nav">
          <router-link to="/cart" class="nav-item">
            购物车
            <span v-if="cartCount > 0" class="badge">{{ cartCount }}</span>
          </router-link>
          <router-link v-if="user" to="/orders" class="nav-item">我的订单</router-link>

          <template v-if="user">
            <router-link to="/profile" class="nav-user">
              <span class="avatar">{{ (user.virtualName || user.username || 'U').slice(0, 1) }}</span>
              <span>{{ user.virtualName || user.username }}</span>
            </router-link>
            <button class="btn btn-ghost btn-sm" @click="logout">退出</button>
          </template>
          <template v-else>
            <router-link to="/login" class="nav-item">登录</router-link>
            <router-link to="/register" class="btn btn-primary btn-sm">注册</router-link>
          </template>
        </nav>
      </div>
    </header>

    <main class="main">
      <router-view />
    </main>

    <footer class="footer">
      <div class="footer-inner">
        <span>码农天堂 · Spring Boot 3 + MyBatis + Vue 3 重构版</span>
        <span class="muted">前后端分离 · JWT 鉴权 · Docker + Nginx 部署</span>
      </div>
    </footer>

    <div class="toasts">
      <transition-group name="toast">
        <div v-for="t in toasts" :key="t.id" class="toast" :class="t.type">{{ t.message }}</div>
      </transition-group>
    </div>
  </div>
</template>

<style scoped>
.layout {
  min-height: 100%;
  display: flex;
  flex-direction: column;
}

.topbar {
  position: sticky;
  top: 0;
  z-index: 30;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid var(--line);
}

.topbar-inner {
  max-width: 1180px;
  margin: 0 auto;
  height: 62px;
  padding: 0 20px;
  display: flex;
  align-items: center;
  gap: 22px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 9px;
  flex-shrink: 0;
}

.logo-mark {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: linear-gradient(135deg, var(--primary), #6f9bff);
  color: #fff;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.5px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-text {
  font-weight: 600;
  letter-spacing: 0.3px;
}

.search {
  flex: 1;
  max-width: 420px;
  display: flex;
  gap: 8px;
}

.nav {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 16px;
}

.nav-item {
  position: relative;
  color: var(--text-2);
  font-size: 14px;
}

.nav-item:hover {
  color: var(--primary);
}

.badge {
  position: absolute;
  top: -8px;
  right: -14px;
  min-width: 17px;
  height: 17px;
  padding: 0 4px;
  border-radius: 999px;
  background: var(--danger);
  color: #fff;
  font-size: 11px;
  line-height: 17px;
  text-align: center;
}

.nav-user {
  display: flex;
  align-items: center;
  gap: 7px;
  color: var(--text);
  font-size: 13px;
}

.avatar {
  width: 26px;
  height: 26px;
  border-radius: 50%;
  background: var(--primary-soft);
  color: var(--primary);
  font-size: 12px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-sm {
  height: 32px;
  padding: 0 13px;
  font-size: 13px;
}

.main {
  flex: 1;
  width: 100%;
  max-width: 1180px;
  margin: 0 auto;
  padding: 26px 20px 48px;
}

.footer {
  border-top: 1px solid var(--line);
  background: #fff;
}

.footer-inner {
  max-width: 1180px;
  margin: 0 auto;
  padding: 18px 20px;
  display: flex;
  justify-content: space-between;
  gap: 12px;
  font-size: 12px;
  color: var(--text-3);
  flex-wrap: wrap;
}

.toasts {
  position: fixed;
  top: 76px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 100;
  display: flex;
  flex-direction: column;
  gap: 8px;
  align-items: center;
}

.toast {
  padding: 9px 18px;
  border-radius: 8px;
  background: #1f2630;
  color: #fff;
  font-size: 13px;
  box-shadow: 0 8px 24px rgba(16, 24, 40, 0.18);
  white-space: nowrap;
}

.toast.warn {
  background: var(--warn);
}

.toast.error {
  background: var(--danger);
}

.toast.success {
  background: var(--success);
}

.toast-enter-active,
.toast-leave-active {
  transition: opacity 0.22s, transform 0.22s;
}

.toast-enter-from,
.toast-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}

@media (max-width: 820px) {
  .search {
    display: none;
  }
  .logo-text {
    display: none;
  }
}
</style>
