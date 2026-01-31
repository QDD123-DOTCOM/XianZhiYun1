<template>
  <div>
    <el-card>
      <!-- 活动选择与搜索 -->
      <h1 class="text-3xl font-semibold text-gray-800 mb-2">活动审核页面</h1>
      <div class="toolbar" style="display:flex;gap:12px;align-items:center;margin-bottom:16px;">
        <el-autocomplete
            v-model="eventQuery"
            :fetch-suggestions="searchEvents"
            placeholder="搜索并选择活动（按标题）"
            clearable
            @select="onSelectEvent"
            style="width:360px;"
        />
        <el-button type="primary" @click="openCreateEvent">新建活动</el-button>
        <el-button v-if="selectedEvent" @click="clearSelection">清除选择</el-button>
        <div style="margin-left:auto;color:#666;">
          <span v-if="selectedEvent">已选：{{ selectedEvent.title }}</span>
          <span v-else>未选择活动，请先选择活动以查看该活动下的待审核商品</span>
        </div>
      </div>

      <!-- 商品搜索与刷新（仅在选中活动时启用搜索） -->
      <div style="display:flex;gap:8px;align-items:center;margin-bottom:12px;">
        <el-input
            v-model="keyword"
            placeholder="按商品标题/描述搜索"
            clearable
            style="width:260px;"
            @keyup.enter="fetchItems"
            :disabled="!selectedEvent"
        />
        <el-button type="primary" size="small" @click="fetchItems" :disabled="!selectedEvent">搜索</el-button>
        <el-button size="small" @click="fetchItems" :disabled="!selectedEvent">刷新</el-button>
        <div style="margin-left:auto;color:#999;">总条数：{{ total }}</div>
      </div>

      <!-- 未选择活动时显示的大提示（替代 No Data） -->
      <div v-if="!selectedEvent" class="no-event-placeholder" style="padding:24px;text-align:center;color:#666;background:#fafafa;border-radius:6px;margin-bottom:12px;">
        <div style="font-size:16px;font-weight:600;margin-bottom:8px;">未选择活动</div>
        <div style="margin-bottom:12px;">请在上方搜索并选择一个活动，系统才能显示该活动的待审核商品。</div>
        <el-button type="primary" @click="openCreateEvent">新建活动</el-button>
      </div>

      <!-- 已选择活动，但无商品时显示定制的空状态 -->
      <div v-else-if="selectedEvent && items.length === 0" class="empty-placeholder" style="padding:36px;text-align:center;color:#999;background:#fafafa;border-radius:6px;margin-bottom:12px;">
        <el-empty description="该活动下暂无待审核商品">
          <el-button type="primary" @click="fetchItems" :disabled="!selectedEvent">刷新</el-button>
        </el-empty>
      </div>

      <!-- 已选择活动且有商品时显示表格 -->
      <div v-else>
        <el-table :data="items" stripe style="width:100%;">
          <el-table-column label="缩略" width="120">
            <template #default="{ row }">
              <el-image :src="row.imageUrl || row.image_url || row.coverUrl || ''" style="width:96px;height:64px" fit="cover" />
            </template>
          </el-table-column>

          <el-table-column prop="title" label="标题" />
          <el-table-column prop="description" label="描述" />
          <el-table-column prop="donorNickname" label="发布者" width="140">
            <template #default="{ row }">{{ row.donorNickname || row.donor_id || row.donorId || '-' }}</template>
          </el-table-column>
          <el-table-column prop="createdAt" label="发布时间" width="160">
            <template #default="{ row }">{{ formatDate(row.createdAt || row.created_at) }}</template>
          </el-table-column>

          <el-table-column label="操作" width="300">
            <template #default="{ row }">
              <el-button size="small" @click="viewItem(row)">查看</el-button>
              <el-button size="small" type="success" @click="openReviewDialog(row, 'APPROVE')">通过</el-button>
              <el-button size="small" type="danger" @click="openReviewDialog(row, 'REJECT')">驳回</el-button>
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
      </div>

      <!-- 审核对话框（保持原样） -->
      <el-dialog title="审核商品" v-model:visible="dialogVisible" width="520px">
        <div v-if="currentItem">
          <p><strong>商品：</strong> {{ currentItem.title }}</p>
          <p><strong>发布者：</strong> {{ currentItem.donorNickname || currentItem.donor_id || currentItem.donorId }}</p>
          <p><strong>时间：</strong> {{ formatDate(currentItem.createdAt || currentItem.created_at) }}</p>
          <el-form label-position="top">
            <el-form-item label="审核意见">
              <el-input type="textarea" v-model="reviewComment" rows="4" />
            </el-form-item>
            <el-form-item>
              <el-button type="success" size="small" @click="submitReview('APPROVE')">通过</el-button>
              <el-button type="danger" size="small" @click="submitReview('REJECT')">驳回</el-button>
              <el-button size="small" @click="dialogVisible = false">取消</el-button>
            </el-form-item>
          </el-form>
        </div>
        <div v-else>加载中...</div>
      </el-dialog>
    </el-card>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref } from 'vue';
import dayjs from 'dayjs';
import {
  listAdminEvents,
  getAdminEvent,
  getEventItemsForAdmin,
  reviewItem
} from '@/api/admin';
import { useRouter } from 'vue-router';

export default defineComponent({
  name: 'ActivityReview',
  setup() {
    const router = useRouter();

    // 活动选择
    const eventQuery = ref('');
    const eventOptions = ref<any[]>([]);
    const selectedEvent = ref<any | null>(null);

    // 商品列表
    const items = ref<any[]>([]);
    const page = ref(1);
    const pageSize = ref(10);
    const total = ref(0);
    const keyword = ref('');

    // 审核对话框
    const dialogVisible = ref(false);
    const currentItem = ref<any | null>(null);
    const reviewComment = ref('');
    const pendingAction = ref<'APPROVE' | 'REJECT' | null>(null);

    const formatDate = (v: any) => {
      if (!v) return '-';
      return dayjs(v).isValid() ? dayjs(v).format('YYYY-MM-DD HH:mm') : String(v);
    };

    // 搜索活动建议（el-autocomplete 使用）
    const searchEvents = async (queryString: string, cb: (results: any[]) => void) => {
      try {
        const res: any = await listAdminEvents({ page: 1, pageSize: 50, keyword: queryString });
        const arr = res?.items || [];
        eventOptions.value = arr;
        // el-autocomplete 需要 [{value, ...}] 格式
        cb(arr.map((it: any) => ({ value: it.title, ...it })));
      } catch (e) {
        console.error('listAdminEvents error', e);
        cb([]);
      }
    };

    const onSelectEvent = async (item: any) => {
      if (!item || !item.id) return;
      // item 来自 autocomplete 的选择项，可能直接包含完整对象
      const id = item.id;
      console.log('selected event id =', id);
      try {
        const ev: any = await getAdminEvent(id);
        selectedEvent.value = ev || null;
        // 重置分页/关键字
        page.value = 1;
        keyword.value = '';
        await fetchItems();
      } catch (e) {
        console.error('getAdminEvent error', e);
      }
    };

    const clearSelection = () => {
      selectedEvent.value = null;
      eventQuery.value = '';
      items.value = [];
      total.value = 0;
    };

    const openCreateEvent = () => {
      router.push({ name: 'activityNew' });
    };

    const fetchItems = async () => {
      // 仅在已选择活动时请求
      if (!selectedEvent.value || !selectedEvent.value.id) {
        console.warn('fetchItems skipped: no selected event');
        items.value = [];
        total.value = 0;
        return;
      }
      const eventId = selectedEvent.value.id;
      console.log('fetchItems for eventId=', eventId, 'page=', page.value, 'pageSize=', pageSize.value, 'keyword=', keyword.value);
      try {
        const params = { page: page.value, pageSize: pageSize.value, keyword: keyword.value, status: 'PENDING' };
        const res: any = await getEventItemsForAdmin(eventId, params);
        // 注意后端返回的字段可能是 items/total 或 items/total/page/pageSize
        items.value = res?.items || [];
        total.value = res?.total || 0;
      } catch (e) {
        console.error('getEventItemsForAdmin error', e);
      }
    };

    const onPageChange = (p: number) => {
      page.value = p;
      fetchItems();
    };

    const viewItem = (row: any) => {
      router.push({ path: `/goods/${row.id}` });
    };

    const openReviewDialog = (row: any, action: 'APPROVE' | 'REJECT') => {
      currentItem.value = row;
      reviewComment.value = '';
      pendingAction.value = action;
      dialogVisible.value = true;
    };

    const submitReview = async (action?: 'APPROVE' | 'REJECT' | null) => {
      if (!currentItem.value || !action) return;
      try {
        const body = { action: action === 'APPROVE' ? 'APPROVED' : 'REJECTED', comment: reviewComment.value || '' };
        await reviewItem(currentItem.value.id, body);
        dialogVisible.value = false;
        currentItem.value = null;
        pendingAction.value = null;
        await fetchItems();
        (window as any).ElMessage && (window as any).ElMessage.success('审核已提交');
      } catch (e) {
        console.error('reviewItem error', e);
        (window as any).ElMessage && (window as any).ElMessage.error('审核失败');
      }
    };

    // 暴露调试方法（调试完成后可移除）
    ;(window as any).__ActivityReview = {
      fetchItems,
      onSelectEvent,
      clearSelection
    };

    return {
      eventQuery,
      eventOptions,
      selectedEvent,
      items, page, pageSize, total, keyword,
      dialogVisible, currentItem, reviewComment,
      formatDate, searchEvents, onSelectEvent, fetchItems, onPageChange, viewItem, openReviewDialog, submitReview,
      clearSelection, openCreateEvent
    };
  }
});
</script>

<style scoped>
/* 可按需自定义样式 */
</style>