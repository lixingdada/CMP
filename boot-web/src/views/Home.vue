<script setup>
import { ref, onMounted, watch, inject } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { api } from '../api'

const route = useRoute()
const router = useRouter()
const toast = inject('toast')

const categories = ref([])
const products = ref([])
const pageInfo = ref({ total: 0, pageNum: 1, pages: 1, pageSize: 12 })
const activeCategory = ref('')
const loading = ref(true)
const errorMsg = ref('')

async function loadCategories() {
  try {
    categories.value = await api.categories()
  } catch (e) {
    toast(e.message, 'error')
  }
}

async function loadProducts(page = 1) {
  loading.value = true
  errorMsg.value = ''
  try {
    const data = await api.products({
      categoryId: activeCategory.value || undefined,
      keyword: route.query.keyword || undefined,
      page,
      size: pageInfo.value.pageSize || 12
    })
    products.value = data.list || []
    pageInfo.value = data
  } catch (e) {
    errorMsg.value = e.message
    products.value = []
  } finally {
    loading.value = false
  }
}

function selectCategory(catId) {
  activeCategory.value = catId
  loadProducts(1)
}

function goPage(page) {
  if (page < 1 || page > pageInfo.value.pages) return
  loadProducts(page)
}

onMounted(async () => {
  await loadCategories()
  await loadProducts(1)
})

watch(() => route.query.keyword, () => loadProducts(1))
</script>

<template>
  <div class="home">
    <aside class="sidebar card">
      <div class="sidebar-title">商品分类</div>
      <button class="cat" :class="{ active: activeCategory === '' }" @click="selectCategory('')">
        全部商品
      </button>
      <button
        v-for="cat in categories"
        :key="cat.catId"
        class="cat"
        :class="{ active: activeCategory === cat.catId }"
        @click="selectCategory(cat.catId)"
      >
        {{ cat.name }}
      </button>
    </aside>

    <section class="content">
      <div class="content-head">
        <h1>
          {{ route.query.keyword ? `搜索「${route.query.keyword}」` : '全部商品' }}
        </h1>
        <span class="muted">共 {{ pageInfo.total }} 件</span>
      </div>

      <div v-if="loading" class="grid">
        <div v-for="n in 6" :key="n" class="card sku-card">
          <div class="skeleton cover"></div>
          <div class="skeleton line" style="width: 60%"></div>
          <div class="skeleton line" style="width: 40%"></div>
        </div>
      </div>

      <div v-else-if="errorMsg" class="empty">
        <p>{{ errorMsg }}</p>
        <button class="btn btn-ghost" @click="loadProducts(1)">重试</button>
      </div>

      <div v-else-if="products.length === 0" class="empty">没有找到符合条件的商品</div>

      <div v-else class="grid">
        <router-link
          v-for="p in products"
          :key="p.productId"
          :to="`/products/${p.productId}`"
          class="card sku-card"
        >
          <div class="cover">
            <span class="cover-text">{{ p.name.slice(0, 1) }}</span>
          </div>
          <div class="sku-body">
            <div class="sku-name">{{ p.name }}</div>
            <div class="sku-desc">{{ p.descn }}</div>
            <div class="sku-foot">
              <span v-if="p.minPrice != null" class="price">{{ p.minPrice }}</span>
              <span v-else class="muted">暂无报价</span>
              <span class="tag">{{ p.category }}</span>
            </div>
          </div>
        </router-link>
      </div>

      <div v-if="!loading && pageInfo.pages > 1" class="pager">
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
    </section>
  </div>
</template>

<style scoped>
.home {
  display: grid;
  grid-template-columns: 210px 1fr;
  gap: 20px;
  align-items: start;
}

.sidebar {
  padding: 16px 12px;
  position: sticky;
  top: 86px;
}

.sidebar-title {
  font-size: 12px;
  color: var(--text-3);
  padding: 0 10px 10px;
  letter-spacing: 0.5px;
}

.cat {
  display: block;
  width: 100%;
  text-align: left;
  padding: 9px 12px;
  border-radius: 8px;
  color: var(--text-2);
  font-size: 14px;
  margin-bottom: 2px;
}

.cat:hover {
  background: #f3f6fb;
  color: var(--text);
}

.cat.active {
  background: var(--primary-soft);
  color: var(--primary);
  font-weight: 500;
}

.content-head {
  display: flex;
  align-items: baseline;
  gap: 12px;
  margin-bottom: 16px;
}

.content-head h1 {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
}

.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(230px, 1fr));
  gap: 16px;
}

.sku-card {
  overflow: hidden;
  display: flex;
  flex-direction: column;
  transition: transform 0.16s, box-shadow 0.16s;
}

.sku-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 24px rgba(16, 24, 40, 0.1);
}

.cover {
  height: 132px;
  background: linear-gradient(135deg, #eef3ff, #f7f9ff);
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid var(--line);
}

.cover-text {
  font-size: 34px;
  font-weight: 700;
  color: #c3d3f7;
}

.sku-body {
  padding: 13px 14px 15px;
  display: flex;
  flex-direction: column;
  gap: 6px;
  flex: 1;
}

.sku-name {
  font-weight: 600;
  font-size: 14px;
}

.sku-desc {
  font-size: 12px;
  color: var(--text-3);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  min-height: 34px;
}

.sku-foot {
  margin-top: auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 6px;
}

.sku-foot .price {
  font-size: 16px;
}

.skeleton.line {
  height: 12px;
  margin: 10px 14px;
}

@media (max-width: 820px) {
  .home {
    grid-template-columns: 1fr;
  }
  .sidebar {
    position: static;
    display: flex;
    overflow-x: auto;
    gap: 4px;
  }
  .cat {
    white-space: nowrap;
    width: auto;
  }
  .sidebar-title {
    display: none;
  }
}
</style>
