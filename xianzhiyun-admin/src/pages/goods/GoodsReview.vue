<template>
  <div class="goods-review-page p-6 bg-gray-50 min-h-screen">
    <div class="max-w-full mx-auto">
      <header class="mb-6">
        <h1 class="text-3xl font-semibold text-gray-800 mb-2">商品审核列表</h1>
      </header>

      <!-- 搜索与筛选 -->
      <section class="filter-card bg-white rounded-lg shadow-sm p-4 mb-6">
        <div class="filter-grid">
          <el-input v-model="query.keyword" placeholder="搜索商品标题..." class="input-flex" @keyup.enter="handleSearch" />
          <el-input v-model="query.sellerKeyword" placeholder="搜索卖家昵称/ID..." class="input-flex" @keyup.enter="handleSearch" />
          <div class="btn-group">
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="handleReset">重置</el-button>
          </div>
          <div class="text-muted">
            <span class="text-sm text-gray-500">共 <strong class="text-gray-700">{{ total }}</strong> 条</span>
          </div>
        </div>
      </section>

      <!-- 商品列表卡片 -->
      <section class="bg-white rounded-lg shadow-lg p-4">
        <div class="table-wrap overflow-x-auto">
          <el-table :data="goodsList" :row-key="row => row.id" v-loading="loading" stripe style="width: 100%; min-width: 900px;">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column label="商品信息" min-width="360">
              <template #default="scope">
                <div class="item-info">
                  <div class="thumb-wrap">
                    <img v-if="scope.row && scope.row.coverUrls" :src="getCoverUrl(scope.row.coverUrls)" alt="封面" class="thumb" />
                    <div v-else class="thumb placeholder">无图</div>
                  </div>
                  <div class="meta">
                    <div class="title">{{ scope.row?.title || '-' }}</div>
                    <div class="meta-sub text-sm text-gray-500">
                      <span class="mr-3">分类：{{ scope.row?.category || '-' }}</span>
                      <span>价格：¥{{ scope.row?.price ?? '-' }}</span>
                    </div>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="sellerNickname" label="卖家信息" width="220">
              <template #default="scope">
                <div class="seller text-sm text-gray-600">
                  <div>{{ scope.row?.sellerNickname || 'N/A' }}</div>
                  <div class="text-xs text-gray-400">ID: {{ scope.row?.sellerId ?? '-' }}</div>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="发布时间" width="180">
              <template #default="scope"><span>{{ formatDate(scope.row?.createTime) }}</span></template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="120">
              <template #default="scope">
                <el-tag :type="statusTag(scope.row?.status)">{{ scope.row?.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="220" fixed="right">
              <template #default="scope">
                <div class="op-group">
                  <el-button type="success" size="small" :disabled="scope.row?.isOperating" @click="handleReview(scope.row, 'ON_SALE')">通过</el-button>
                  <el-button type="danger" size="small" :disabled="scope.row?.isOperating" @click="handleReview(scope.row, 'REJECTED')">拒绝</el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div class="mt-4 flex justify-end">
          <el-pagination :total="total" :page-size="query.pageSize" :current-page="query.page" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange" @current-change="handlePageChange" />
        </div>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { request } from '../../utils/request';
import dayjs from 'dayjs';

interface GoodsItem {
  id: number; title: string; price: number; category: string; coverUrls: string;
  sellerId: number; sellerNickname?: string; createTime: string; status: string;
  isDeleted?: boolean | null; isOperating?: boolean;
}

// 【关键修改点】动态修正图片 IP
const getCoverUrl = (urls: string | null | undefined) => {
  if (!urls) return '/placeholder.png';
  let url = String(urls).split(',')[0];
  // 将数据库中任何 192.168.x.x 的 IP 替换为 localhost
  return url.replace(/http:\/\/192\.168\.\d+\.\d+:8088/, 'http://localhost:8088');
};

const goodsList = ref<GoodsItem[]>([]);
const total = ref(0);
const loading = ref(false);
const query = reactive({ page: 1, pageSize: 10, keyword: '', sellerKeyword: '', status: 'PENDING' });

const formatDate = (v: string) => {
  if (!v) return '-';
  const d = dayjs(v);
  return d.isValid() ? d.format('YYYY-MM-DD HH:mm') : String(v);
};

const statusTag = (s: string) => {
  switch (s) {
    case 'ON_SALE': return 'success';
    case 'PENDING': return 'warning';
    case 'REJECTED': case 'DELETED': case 'REMOVED': return 'danger';
    default: return 'info';
  }
};

const fetchGoodsList = async () => {
  loading.value = true;
  try {
    const res: any = await request.get('/api/admin/goods', { params: query });
    const payload = res?.data ?? res;
    let finalPayload = payload;
    if (payload?.code === 0 && payload.data) finalPayload = payload.data;
    goodsList.value = (finalPayload?.items || []).map((item: any) => ({
      ...item,
      coverUrls: item.coverUrls ?? item.coverUrl ?? '',
      isOperating: false
    }));
    total.value = Number(finalPayload?.total ?? 0);
  } catch (error: any) {
    ElMessage.error('获取列表失败');
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => { query.page = 1; fetchGoodsList(); };
const handleReset = () => { query.keyword = ''; query.sellerKeyword = ''; query.page = 1; fetchGoodsList(); };
const handleSizeChange = (val: number) => { query.pageSize = val; query.page = 1; fetchGoodsList(); };
const handlePageChange = (val: number) => { query.page = val; fetchGoodsList(); };

const handleReview = async (row: GoodsItem, newStatus: 'ON_SALE' | 'REJECTED') => {
  const actionText = newStatus === 'ON_SALE' ? '通过' : '拒绝';
  ElMessageBox.confirm(`确定要${actionText} 商品 "${row.title}" 吗？`, '确认操作', {
    confirmButtonText: actionText, cancelButtonText: '取消',
    type: newStatus === 'ON_SALE' ? 'warning' : 'error',
  }).then(async () => {
    row.isOperating = true;
    try {
      await request.post(`/api/admin/goods/${row.id}/status`, { status: newStatus });
      ElMessage.success(`${actionText}成功!`);
      if (query.status === 'PENDING') {
        goodsList.value = goodsList.value.filter(i => i.id !== row.id);
        total.value = Math.max(0, total.value - 1);
      } else {
        row.status = newStatus;
      }
    } catch (error: any) {
      ElMessage.error('操作失败');
    } finally {
      row.isOperating = false;
    }
  }).catch(() => {});
};

onMounted(fetchGoodsList);
</script>

<style scoped>
.filter-card .filter-grid { display: grid; grid-template-columns: 1fr 1fr auto minmax(160px, 1fr); gap: 12px; align-items: center; }
.item-info { display: flex; align-items: center; gap: 12px; }
.thumb { width: 72px; height: 72px; object-fit: cover; border-radius: 6px; display: block; }
.thumb.placeholder { display: flex; align-items: center; justify-content: center; background: #f3f4f6; color: #9ca3af; font-size: 12px; width: 72px; height: 72px; border-radius: 6px; }
.meta .title { font-weight: 600; color: #2d3748; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.op-group { display: flex; gap: 8px; }
@media (max-width: 768px) { .filter-card .filter-grid { grid-template-columns: 1fr; } }
</style>
