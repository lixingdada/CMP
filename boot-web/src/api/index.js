import axios from 'axios'

const TOKEN_KEY = 'cmp_token'
const USER_KEY = 'cmp_user'

export const auth = {
  get token() {
    return localStorage.getItem(TOKEN_KEY)
  },
  get user() {
    try {
      return JSON.parse(localStorage.getItem(USER_KEY) || 'null')
    } catch {
      return null
    }
  },
  save(data) {
    localStorage.setItem(TOKEN_KEY, data.token)
    localStorage.setItem(USER_KEY, JSON.stringify({
      username: data.username,
      virtualName: data.virtualName,
      avatar: data.avatar
    }))
  },
  clear() {
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(USER_KEY)
  },
  get isLogin() {
    return !!localStorage.getItem(TOKEN_KEY)
  }
}

const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE || '/api',
  timeout: 15000
})

let onUnauthorized = null
export function setUnauthorizedHandler(fn) {
  onUnauthorized = fn
}

http.interceptors.request.use((config) => {
  if (auth.token) {
    config.headers.Authorization = `Bearer ${auth.token}`
  }
  return config
})

// 统一拆包：后端固定返回 {code, message, data, traceId}
// 业务码非 0 视为失败，直接抛错，页面里只写 try/catch，不再判断 code
http.interceptors.response.use(
  (response) => {
    const body = response.data
    if (body && typeof body.code === 'number' && body.code !== 0) {
      if (body.code === 1002) {
        auth.clear()
        onUnauthorized && onUnauthorized()
      }
      return Promise.reject(new Error(body.message || '请求失败'))
    }
    return body ? body.data : null
  },
  (error) => {
    const status = error.response?.status
    const body = error.response?.data
    if (status === 401) {
      auth.clear()
      onUnauthorized && onUnauthorized()
    }
    const message = body?.message || error.message || '网络异常，请稍后重试'
    return Promise.reject(new Error(message))
  }
)

export const api = {
  register: (payload) => http.post('/auth/register', payload),
  login: (payload) => http.post('/auth/login', payload),
  me: () => http.get('/auth/me'),
  changePassword: (payload) => http.put('/auth/password', payload),

  categories: () => http.get('/categories'),
  products: (params) => http.get('/products', { params }),
  productDetail: (productId) => http.get(`/products/${productId}`),
  item: (itemId) => http.get(`/items/${itemId}`),

  getCart: () => http.get('/cart'),
  addToCart: (payload) => http.post('/cart/items', payload),
  updateCartItem: (itemId, quantity) => http.put(`/cart/items/${itemId}`, { quantity }),
  removeCartItem: (itemId) => http.delete(`/cart/items/${itemId}`),
  clearCart: () => http.delete('/cart'),

  createOrder: (payload) => http.post('/orders', payload),
  listOrders: (params) => http.get('/orders', { params }),
  orderDetail: (orderId) => http.get(`/orders/${orderId}`),

  profile: () => http.get('/profile'),
  updateProfile: (payload) => http.put('/profile', payload),
  logs: (limit = 20) => http.get('/logs', { params: { limit } })
}

export default http
