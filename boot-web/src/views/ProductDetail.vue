<script setup>
import { ref, onMounted, inject } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { api, auth } from '../api'

const route = useRoute()
const router = useRouter()
const toast = inject('toast')
const refreshCart = inject('refreshCart')

const detail = ref(null)
const loading = ref(true)
const errorMsg = ref('')
const addingId = ref('')
const quantities = ref({})

async function load() {
  loading.value = true
  errorMsg.value = ''
  try {
    const data = await api.productDetail(route.params.productId)
    detail.value = data
    const q = {}
    ;(data.items || []).forEach((it) => {
      q[it.itemId] = 1
    })
    quantities.value = q
  } catch (e) {
    errorMsg.value = e.message
  } finally {
    loading.value = false
  }
}

function specOf(item) {
  const parts = [item.attr1, item.attr2].filter((s) => s && s.trim())
  if (parts.length) return parts.join(' · ')
  return item.attr3 || item.itemId
}

async function addToCart(item) {
  if (!auth.isLogin) {
    toast('请先登录后再加购', 'warn')
    router.push({ name: 'login', query: { redirect: route.fullPath } })
    return
  }
  const quantity = Number(quantities.value[item.itemId]) || 1
  addingId.value = item.itemId
  try {
    await api.addToCart({ itemId: item.itemId, quantity })
    await refreshCart()
    toast('已加入购物车', 'success')
  } catch (e) {
    toast(e.message, 'error')
  } finally {
    addingId.value = ''
  }
}

onMounted(load)
</script>

<template>
  <div v-if="loading" class="card panel">
    <div class="skeleton" style="height: 28px; width: 40%"></div>
    <div class="skeleton" style="height: 14px; width: 70%; margin-top: 14px"></div>
    <div class="skeleton" style="height: 120px; margin-top: 26px"></div>
  </div>

  <div v-else-if="errorMsg" class="card panel">
    <div class="empty">
      <p>{{ errorMsg }}</p>
      <router-link to="/" class="btn btn-ghost">返回首页</router-link>
    </div>
  </div>

  <div v-else-if="detail">
    <nav class="crumb">
      <router-link to="/">首页</router-link>
      <span>/</span>
      <router-link :to="{ name: 'home', query: { category: detail.product.category } }">
        {{ detail.product.category }}
      </router-link>
      <span>/</span>
      <span class="muted">{{ detail.product.name }}</span>
    </nav>

    <div class="card panel">
      <h1>{{ detail.product.name }}</h1>
      <p class="descn">{{ detail.product.descn }}</p>
      <div class="meta">
        <span class="tag">商品编号 {{ detail.product.productId }}</span>
        <span class="tag">共 {{ detail.items.length }} 个规格</span>
      </div>
    </div>

    <div class="card panel">
      <h2>选择规格</h2>

      <table v-if="detail.items.length" class="table">
        <thead>
          <tr>
            <th style="width: 90px">SKU</th>
            <th>规格</th>
            <th style="width: 110px">单价</th>
            <th style="width: 100px">库存</th>
            <th style="width: 200px">数量 / 操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in detail.items" :key="item.itemId">
            <td class="muted">{{ item.itemId }}</td>
            <td>{{ specOf(item) }}</td>
            <td class="price">{{ item.listPrice }}</td>
            <td>
              <span v-if="item.qty > 0" class="stock">{{ item.qty }}</span>
              <span v-else class="out">无货</span>
            </td>
            <td>
              <div class="buy">
                <input
                  v-model.number="quantities[item.itemId]"
                  type="number"
                  min="1"
                  :max="item.qty || 1"
                  class="qty"
                />
                <button
                  class="btn btn-primary"
                  :disabled="item.qty <= 0 || addingId === item.itemId"
                  @click="addToCart(item)"
                >
                  {{ item.qty <= 0 ? '已售罄' : addingId === item.itemId ? '加入中…' : '加入购物车' }}
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>

      <div v-else class="empty">该商品暂无可用规格</div>
    </div>
  </div>
</template>

<style scoped>
.crumb {
  display: flex;
  gap: 8px;
  font-size: 13px;
  color: var(--text-3);
  margin-bottom: 14px;
}

.crumb a {
  color: var(--text-3);
}

.crumb a:hover {
  color: var(--primary);
}

.panel {
  padding: 22px 24px;
  margin-bottom: 16px;
}

.panel h1 {
  font-size: 22px;
  margin: 0 0 8px;
}

.panel h2 {
  font-size: 15px;
  margin: 0 0 12px;
}

.descn {
  color: var(--text-2);
  margin: 0 0 14px;
}

.meta {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.stock {
  color: var(--success);
}

.out {
  color: var(--danger);
}

.buy {
  display: flex;
  gap: 8px;
}

.qty {
  width: 66px;
  text-align: center;
  padding: 8px 6px;
}

.buy .btn {
  flex: 1;
}
</style>
