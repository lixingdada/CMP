<script setup>
import { ref, onMounted, computed, inject } from 'vue'
import { useRouter } from 'vue-router'
import { api } from '../api'

const router = useRouter()
const toast = inject('toast')
const refreshCart = inject('refreshCart')

const cart = ref({ items: [], totalQuantity: 0, totalAmount: 0 })
const loading = ref(true)
const errorMsg = ref('')
const busyId = ref('')
const showCheckout = ref(false)
const submitting = ref(false)
const form = ref({ orderName: '', orderAddress: '', orderTel: '' })
const formError = ref('')

const isEmpty = computed(() => !cart.value.items || cart.value.items.length === 0)

async function load() {
  loading.value = true
  errorMsg.value = ''
  try {
    cart.value = await api.getCart()
  } catch (e) {
    errorMsg.value = e.message
  } finally {
    loading.value = false
  }
}

async function changeQuantity(line, delta) {
  const next = line.quantity + delta
  if (next < 1) return
  if (line.stock != null && next > line.stock) {
    toast(`库存仅剩 ${line.stock} 件`, 'warn')
    return
  }
  busyId.value = line.itemId
  try {
    await api.updateCartItem(line.itemId, next)
    await load()
    await refreshCart()
  } catch (e) {
    toast(e.message, 'error')
  } finally {
    busyId.value = ''
  }
}

async function remove(line) {
  busyId.value = line.itemId
  try {
    await api.removeCartItem(line.itemId)
    toast('已移除')
    await load()
    await refreshCart()
  } catch (e) {
    toast(e.message, 'error')
  } finally {
    busyId.value = ''
  }
}

async function clearAll() {
  try {
    await api.clearCart()
    toast('购物车已清空')
    await load()
    await refreshCart()
  } catch (e) {
    toast(e.message, 'error')
  }
}

function openCheckout() {
  formError.value = ''
  // 用个人中心里的默认地址预填，减少重复输入
  api
    .profile()
    .then((p) => {
      form.value.orderName = p.receiverName || p.virtualName || ''
      form.value.orderTel = p.receiverPhone || p.phone || ''
      form.value.orderAddress = p.receiverAddr || ''
    })
    .catch(() => {})
  showCheckout.value = true
}

async function submitOrder() {
  formError.value = ''
  if (!form.value.orderName.trim()) {
    formError.value = '请填写收货人'
    return
  }
  if (!/^1[3-9]\d{9}$/.test(form.value.orderTel)) {
    formError.value = '请填写正确的手机号'
    return
  }
  if (!form.value.orderAddress.trim()) {
    formError.value = '请填写收货地址'
    return
  }

  submitting.value = true
  try {
    const order = await api.createOrder(form.value)
    showCheckout.value = false
    await refreshCart()
    toast(`下单成功，订单号 ${order.orderId}`, 'success')
    router.push('/orders')
  } catch (e) {
    formError.value = e.message
  } finally {
    submitting.value = false
  }
}

onMounted(load)
</script>

<template>
  <div>
    <div class="page-head">
      <h1>购物车</h1>
      <button v-if="!isEmpty" class="btn-text danger" @click="clearAll">清空购物车</button>
    </div>

    <div v-if="loading" class="card panel">
      <div class="skeleton" style="height: 40px"></div>
      <div class="skeleton" style="height: 40px; margin-top: 10px"></div>
    </div>

    <div v-else-if="errorMsg" class="card panel">
      <div class="empty">
        <p>{{ errorMsg }}</p>
        <button class="btn btn-ghost" @click="load">重试</button>
      </div>
    </div>

    <div v-else-if="isEmpty" class="card panel">
      <div class="empty">
        <p>购物车还是空的</p>
        <router-link to="/" class="btn btn-primary">去逛逛</router-link>
      </div>
    </div>

    <template v-else>
      <div class="card">
        <table class="table">
          <thead>
            <tr>
              <th>商品</th>
              <th style="width: 110px">单价</th>
              <th style="width: 160px">数量</th>
              <th style="width: 110px">小计</th>
              <th style="width: 80px"></th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="line in cart.items" :key="line.itemId">
              <td>
                <div class="name">{{ line.itemName }}</div>
                <div class="muted small">{{ line.itemId }}</div>
              </td>
              <td class="price">{{ line.itemPrice }}</td>
              <td>
                <div class="stepper">
                  <button :disabled="line.quantity <= 1 || busyId === line.itemId" @click="changeQuantity(line, -1)">−</button>
                  <span>{{ line.quantity }}</span>
                  <button :disabled="busyId === line.itemId" @click="changeQuantity(line, 1)">+</button>
                </div>
                <div v-if="line.stock != null" class="muted small">库存 {{ line.stock }}</div>
              </td>
              <td class="price">{{ line.subtotal }}</td>
              <td>
                <button class="btn-text danger" :disabled="busyId === line.itemId" @click="remove(line)">
                  移除
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="card summary">
        <div class="summary-info">
          共 <b>{{ cart.totalQuantity }}</b> 件商品，合计
          <span class="price total">{{ cart.totalAmount }}</span>
        </div>
        <button class="btn btn-primary" @click="openCheckout">去结算</button>
      </div>
    </template>

    <!-- 结算弹层 -->
    <div v-if="showCheckout" class="mask" @click.self="showCheckout = false">
      <div class="dialog card">
        <h2>填写收货信息</h2>
        <div class="field">
          <label>收货人</label>
          <input v-model="form.orderName" type="text" placeholder="请输入收货人姓名" />
        </div>
        <div class="field">
          <label>联系电话</label>
          <input v-model="form.orderTel" type="text" placeholder="11 位手机号" />
        </div>
        <div class="field">
          <label>收货地址</label>
          <textarea v-model="form.orderAddress" rows="3" placeholder="省 / 市 / 区 详细地址"></textarea>
        </div>

        <p v-if="formError" class="err-box">{{ formError }}</p>

        <div class="dialog-foot">
          <button class="btn btn-ghost" @click="showCheckout = false">取消</button>
          <button class="btn btn-primary" :disabled="submitting" @click="submitOrder">
            {{ submitting ? '提交中…' : `确认下单 ¥${cart.totalAmount}` }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.page-head h1 {
  font-size: 19px;
  margin: 0;
}

.panel {
  padding: 20px;
}

.name {
  font-weight: 500;
}

.small {
  font-size: 12px;
}

.stepper {
  display: inline-flex;
  align-items: center;
  border: 1px solid var(--line);
  border-radius: 8px;
  overflow: hidden;
}

.stepper button {
  width: 30px;
  height: 30px;
  color: var(--text-2);
  font-size: 15px;
}

.stepper button:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.stepper span {
  min-width: 36px;
  text-align: center;
  border-left: 1px solid var(--line);
  border-right: 1px solid var(--line);
  line-height: 30px;
  font-size: 13px;
}

.summary {
  margin-top: 16px;
  padding: 16px 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.summary-info {
  color: var(--text-2);
}

.total {
  font-size: 20px;
  margin-left: 4px;
}

.mask {
  position: fixed;
  inset: 0;
  background: rgba(16, 24, 40, 0.42);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 60;
  padding: 20px;
}

.dialog {
  width: 100%;
  max-width: 420px;
  padding: 24px;
}

.dialog h2 {
  font-size: 17px;
  margin: 0 0 18px;
}

.dialog-foot {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.err-box {
  margin: 0;
  padding: 9px 12px;
  border-radius: 8px;
  background: #fdecec;
  color: var(--danger);
  font-size: 13px;
}
</style>
