<script setup>
import { ref, onMounted, inject } from 'vue'
import { api } from '../api'

const toast = inject('toast')

const orders = ref([])
const pageInfo = ref({ total: 0, pageNum: 1, pages: 1, pageSize: 5 })
const loading = ref(true)
const errorMsg = ref('')

async function load(page = 1) {
  loading.value = true
  errorMsg.value = ''
  try {
    const data = await api.listOrders({ page, size: pageInfo.value.pageSize || 5 })
    orders.value = data.list || []
    pageInfo.value = data
  } catch (e) {
    errorMsg.value = e.message
    orders.value = []
  } finally {
    loading.value = false
  }
}

function formatTime(t) {
  if (!t) return ''
  return String(t).replace('T', ' ').slice(0, 19)
}

function goPage(page) {
  if (page < 1 || page > pageInfo.value.pages) return
  load(page)
}

onMounted(() => load(1))
</script>

<template>
  <div>
    <div class="page-head">
      <h1>我的订单</h1>
      <span class="muted">共 {{ pageInfo.total }} 笔</span>
    </div>

    <div v-if="loading" class="card panel">
      <div class="skeleton" style="height: 90px"></div>
    </div>

    <div v-else-if="errorMsg" class="card panel">
      <div class="empty">
        <p>{{ errorMsg }}</p>
        <button class="btn btn-ghost" @click="load(1)">重试</button>
      </div>
    </div>

    <div v-else-if="orders.length === 0" class="card panel">
      <div class="empty">
        <p>还没有订单</p>
        <router-link to="/" class="btn btn-primary">去购物</router-link>
      </div>
    </div>

    <template v-else>
      <div v-for="order in orders" :key="order.orderId" class="card order">
        <div class="order-head">
          <div>
            <span class="order-id">订单号 {{ order.orderId }}</span>
            <span class="muted time">{{ formatTime(order.orderDate) }}</span>
          </div>
          <span class="price amount">{{ order.totalAmount }}</span>
        </div>

        <div class="order-body">
          <table class="table">
            <thead>
              <tr>
                <th>商品</th>
                <th style="width: 90px">单价</th>
                <th style="width: 70px">数量</th>
                <th style="width: 100px">小计</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(it, idx) in order.items" :key="idx">
                <td>
                  <div>{{ it.itemName }}</div>
                  <div class="muted small">{{ it.itemId }}</div>
                </td>
                <td class="price">{{ it.price }}</td>
                <td>{{ it.quantity }}</td>
                <td class="price">{{ it.subtotal }}</td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="order-foot muted small">
          收货人 {{ order.orderName }} · {{ order.orderTel }} · {{ order.orderAddress }}
        </div>
      </div>

      <div v-if="pageInfo.pages > 1" class="pager">
        <button :disabled="pageInfo.pageNum <= 1" @click="goPage(pageInfo.pageNum - 1)">上一页</button>
        <button
          v-for="p in pageInfo.pages"
          :key="p"
          :class="{ current: p === pageInfo.pageNum }"
          @click="goPage(p)"
        >
          {{ p }}
        </button>
        <button :disabled="pageInfo.pageNum >= pageInfo.pages" @click="goPage(pageInfo.pageNum + 1)">
          下一页
        </button>
      </div>
    </template>
  </div>
</template>

<style scoped>
.page-head {
  display: flex;
  align-items: baseline;
  gap: 12px;
  margin-bottom: 16px;
}

.page-head h1 {
  font-size: 19px;
  margin: 0;
}

.panel {
  padding: 20px;
}

.order {
  margin-bottom: 14px;
  overflow: hidden;
}

.order-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 13px 18px;
  background: #fafbfd;
  border-bottom: 1px solid var(--line);
}

.order-id {
  font-weight: 600;
  margin-right: 12px;
}

.time {
  font-size: 12px;
}

.amount {
  font-size: 17px;
}

.order-body {
  padding: 0 6px;
}

.order-foot {
  padding: 11px 18px;
  border-top: 1px dashed var(--line);
}

.small {
  font-size: 12px;
}
</style>
