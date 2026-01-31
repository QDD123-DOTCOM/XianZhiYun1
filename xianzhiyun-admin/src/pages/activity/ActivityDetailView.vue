<template>
  <div>
    <el-card>
      <div style="display:flex;gap:16px;align-items:flex-start;">
        <div style="flex:1;">
          <h2 style="margin:0 0 8px;">{{ event?.title || '-' }}</h2>
          <div style="color:#666;margin-bottom:8px;">
            时间：{{ formatDate(event?.startTime) }} - {{ formatDate(event?.endTime) }}
            <span style="margin:0 8px;color:#ccc;">|</span>
            地点：{{ event?.location || '-' }}
          </div>

          <div style="margin-bottom:12px;">
            <el-image v-if="event?.imageUrl" :src="event.imageUrl" style="max-width:480px;max-height:260px" fit="cover" />
            <div v-else style="width:480px;height:260px;background:#f5f7fa;display:flex;align-items:center;justify-content:center;color:#999;">
              无封面
            </div>
          </div>

          <div style="margin-bottom:16px;">
            <h3>活动简介</h3>
            <div style="white-space:pre-wrap;color:#333;">{{ event?.description || '-' }}</div>
          </div>

          <div style="display:flex;gap:8px;">
            <el-button size="small" type="primary" @click="onEdit">编辑</el-button>
            <el-button size="small" @click="onBack">返回</el-button>
          </div>
        </div>

        <div style="width:320px;">
          <el-card shadow="never">
            <div style="font-size:14px;color:#666;margin-bottom:8px;">活动统计</div>
            <div style="display:flex;flex-direction:column;gap:10px;">
              <div style="display:flex;justify-content:space-between;align-items:center;">
                <div>参与人数</div>
                <div style="font-weight:600;">{{ stats?.participantCount ?? 0 }}</div>
              </div>
              <div style="display:flex;justify-content:space-between;align-items:center;">
                <div>物品交换数量</div>
                <div style="font-weight:600;">{{ stats?.exchangeCount ?? 0 }}</div>
              </div>
              <div style="display:flex;justify-content:space-between;align-items:center;">
                <div>反馈数</div>
                <div style="font-weight:600;">{{ feedbacks.length }}</div>
              </div>
            </div>
          </el-card>
        </div>
      </div>

      <el-divider style="margin:16px 0;">已通过商品</el-divider>

      <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:12px;">
        <div style="display:flex;gap:8px;align-items:center;">
          <el-input v-model="keyword" placeholder="按商品标题/描述搜索" clearable style="width:260px;" @keyup.enter="fetchApprovedItems" />
          <el-button type="primary" size="small" @click="fetchApprovedItems">搜索</el-button>
          <el-button size="small" @click="fetchApprovedItems">刷新</el-button>
        </div>
        <div style="color:#666;">总条数：{{ total }}</div>
      </div>

      <el-table :data="items" stripe>
        <el-table-column label="缩略" width="120">
          <template #default="{ row }">
            <el-image :src="row.imageUrl || row.image_url || row.coverUrl || ''" style="width:96px;height:64px" fit="cover" />
          </template>
        </el-table-column>

        <el-table-column prop="title" label="标题" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="donorNickname" label="发布者" width="140">
          <template #default="{ row }">{{ row.donorNickname || row.donorId || row.donor_id || '-' }}</template>
        </el-table-column>
        <el-table-column prop="createdAt" label="发布时间" width="160">
          <template #default="{ row }">{{ formatDate(row.createdAt || row.created_at) }}</template>
        </el-table-column>

        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <el-button size="small" @click="viewItem(row)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div style="margin-top:12px;text-align:right;">
        <el-pagination
            background
            :current-page="page"
            :page-size="pageSize"
            :total="total"
            @current-change="onPageChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted } from 'vue';
import dayjs from 'dayjs';
import { getAdminEvent, getEventItemsForAdmin, getEventStats, getEventFeedbacks } from '@/api/admin';
import { useRoute, useRouter } from 'vue-router';

export default defineComponent({
  name: 'ActivityDetailView',
  setup() {
    const route = useRoute();
    const router = useRouter();
    const id = route.params.id ? Number(route.params.id) : null;

    const event = ref<any>(null);
    const stats = ref<any>({});
    const feedbacks = ref<any[]>([]);

    const items = ref<any[]>([]);
    const page = ref(1);
    const pageSize = ref(10);
    const total = ref(0);
    const keyword = ref('');

    const formatDate = (v: any) => {
      if (!v) return '-';
      return dayjs(v).isValid() ? dayjs(v).format('YYYY-MM-DD HH:mm') : String(v);
    };

    const loadEvent = async () => {
      if (!id) return;
      try {
        const res: any = await getAdminEvent(id);
        event.value = res || null;
      } catch (e) {
        console.error('getAdminEvent error', e);
      }
    };

    const loadStats = async () => {
      if (!id) return;
      try {
        const s: any = await getEventStats(id);
        stats.value = s || {};
      } catch (e) {
        console.error('getEventStats error', e);
      }
    };

    const loadFeedbacks = async () => {
      if (!id) return;
      try {
        const r: any = await getEventFeedbacks(id);
        feedbacks.value = r?.items || [];
      } catch (e) {
        console.error('getEventFeedbacks error', e);
      }
    };

    const fetchApprovedItems = async () => {
      if (!id) return;
      try {
        const params = { page: page.value, pageSize: pageSize.value, keyword: keyword.value, status: 'ON_SHELF' };
        const res: any = await getEventItemsForAdmin(id, params);
        items.value = res?.items || [];
        total.value = res?.total || 0;
      } catch (e) {
        console.error('getEventItemsForAdmin error', e);
      }
    };

    const onPageChange = (p: number) => {
      page.value = p;
      fetchApprovedItems();
    };

    const viewItem = (row: any) => {
      router.push({ path: `/goods/${row.id}` });
    };

    const onEdit = () => {
      if (!id) return;
      router.push({ name: 'activityEdit', params: { id: String(id) } });
    };

    const onBack = () => {
      router.push({ name: 'activities' });
    };

    onMounted(() => {
      loadEvent();
      loadStats();
      loadFeedbacks();
      fetchApprovedItems();
    });

    return {
      event, stats, feedbacks,
      items, page, pageSize, total, keyword,
      loadEvent, fetchApprovedItems, onPageChange, viewItem,
      formatDate, onEdit, onBack
    };
  }
});
</script>

<style scoped>
/* 可按需微调 */
</style>