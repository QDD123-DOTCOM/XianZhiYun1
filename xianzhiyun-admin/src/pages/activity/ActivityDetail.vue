<template>
  <div>
    <el-card>
      <div style="display:flex;gap:12px;align-items:center;margin-bottom:16px;">
        <div style="min-width:360px;">
          <el-autocomplete
              v-model="eventQuery"
              :fetch-suggestions="searchEvents"
              placeholder="搜索并选择活动（按标题）"
              clearable
              @select="onSelectEvent"
              style="width:100%;"
          />
        </div>

        <el-button type="primary" @click="openCreateEvent">新建活动</el-button>
        <el-button v-if="selectedEvent" @click="clearSelection">清除选择</el-button>

        <div style="margin-left:auto;color:#666;">
          <span v-if="selectedEvent">已选：{{ selectedEvent.title }}</span>
          <span v-else>未选择活动，显示所有待审核商品</span>
        </div>
      </div>

      <div v-if="selectedEvent" style="margin-bottom:16px;">
        <el-row :gutter="12">
          <el-col :span="16">
            <h3 style="margin:0 0 8px;">{{ selectedEvent.title }}</h3>
            <div style="color:#666;margin-bottom:8px;">
              时间：{{ formatDate(selectedEvent.startTime) }} - {{ formatDate(selectedEvent.endTime) }}
              <span style="margin:0 8px;color:#ccc;">|</span>
              地点：{{ selectedEvent.location || '-' }}
            </div>
            <div style="white-space:pre-wrap;color:#333;">{{ selectedEvent.description || '-' }}</div>
          </el-col>
          <el-col :span="8">
            <el-image v-if="selectedEvent.imageUrl" :src="selectedEvent.imageUrl" style="width:100%;height:120px" fit="cover" />
          </el-col>
        </el-row>
      </div>

      <div style="margin-bottom:12px;display:flex;align-items:center;justify-content:space-between;">
        <div style="display:flex;gap:8px;align-items:center;">
          <el-input v-model="keyword" placeholder="按商品标题/描述搜索" clearable style="width:260px;" @keyup.enter="fetchItems" />
          <el-button type="primary" size="small" @click="fetchItems">搜索</el-button>
          <el-button size="small" @click="fetchItems">刷新</el-button>
        </div>
        <div style="color:#666;">总条数：{{ total }}</div>
      </div>

      <el-table :data="items" stripe style="width:100%;">
        <el-table-column label="缩略" width="120">
          <template #default="{ row }">
            <el-image :src="row.imageUrl || row.coverUrl || ''" style="width:96px;height:64px" fit="cover" />
          </template>
        </el-table-column>

        <el-table-column prop="title" label="标题" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="donorNickname" label="发布者" width="140" />
        <el-table-column prop="createdAt" label="发布时间" width="160">
          <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
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

      <el-dialog title="审核商品" v-model:visible="dialogVisible" width="520px">
        <div v-if="currentItem">
          <p><strong>商品：</strong> {{ currentItem.title }}</p>
          <p><strong>发布者：</strong> {{ currentItem.donorNickname || currentItem.donorId }}</p>
          <p><strong>时间：</strong> {{ formatDate(currentItem.createdAt) }}</p>
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
  // 以下函数需在 src/xianzhiyun-admin/api/admin.ts 中存在并导出
  listAdminEvents, // 用于下拉搜索事件（分页或关键词）
  getAdminEvent,
  getEventItemsForAdmin,
  reviewItem
} from '../../api/admin';
import { useRouter } from 'vue-router';

export default defineComponent({
  name: 'ActivityReview',
  setup() {
    const router = useRouter();

    const eventQuery = ref('');
    const eventOptions = ref<any[]>([]);
    const selectedEvent = ref<any | null>(null);

    const items = ref<any[]>([]);
    const page = ref(1);
    const pageSize = ref(10);
    const total = ref(0);
    const keyword = ref('');
    const dialogVisible = ref(false);
    const currentItem = ref<any | null>(null);
    const reviewComment = ref('');
    const pendingAction = ref<'APPROVE' | 'REJECT' | null>(null);

    const formatDate = (v: any) => {
      if (!v) return '-';
      return dayjs(v).isValid() ? dayjs(v).format('YYYY-MM-DD HH:mm') : String(v);
    };

    // 搜索活动建议（用于 el-autocomplete）
    const searchEvents = async (queryString: string, cb: (results: any[]) => void) => {
      try {
        // 调用后端列表接口，按标题或关键字搜索。接口名示例 listAdminEvents(page, pageSize, keyword)
        const res: any = await listAdminEvents({ page: 1, pageSize: 50, keyword: queryString });
        const arr = res?.items || [];
        eventOptions.value = arr;
        cb(arr.map((it: any) => ({ value: it.title, ...it })));
      } catch (e) {
        console.error('listAdminEvents error', e);
        cb([]);
      }
    };

    const onSelectEvent = async (item: any) => {
      // item 来自 el-autocomplete 的选中项（我们在 cb 中把活动对象放进去）
      // item 结构至少包含 id
      if (!item || !item.id) return;
      selectedEvent.value = await loadEvent(item.id);
      page.value = 1;
      await fetchItems();
    };

    const loadEvent = async (id: number) => {
      try {
        const data: any = await getAdminEvent(id);
        return data || null;
      } catch (e) {
        console.error('getAdminEvent error', e);
        return null;
      }
    };

    const fetchItems = async () => {
      try {
        const eventId = selectedEvent.value?.id ?? null;
        // 如果没有选择活动，则可选择展示所有待审核商品或提示先选择活动
        if (!eventId) {
          // 如果你希望在未选活动时仍显示所有待审核商品，可以调用 list all items 接口
          items.value = [];
          total.value = 0;
          return;
        }
        const params = { page: page.value, pageSize: pageSize.value, keyword: keyword.value, status: 'PENDING' };
        const res: any = await getEventItemsForAdmin(eventId, params);
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

    const clearSelection = () => {
      selectedEvent.value = null;
      eventQuery.value = '';
      items.value = [];
      total.value = 0;
      page.value = 1;
    };

    const openCreateEvent = () => {
      router.push({ name: 'activityNew' });
    };

    return {
      eventQuery,
      eventOptions,
      selectedEvent,
      items, page, pageSize, total, keyword, dialogVisible, currentItem, reviewComment,
      formatDate, searchEvents, onSelectEvent, fetchItems, onPageChange, viewItem, openReviewDialog, submitReview,
      clearSelection, openCreateEvent
    };
  }
});
</script>

<style scoped>
/* 如需自定义样式可在此处添加 */
</style>