<template>
  <div>
    <el-card>
      <h1 class="text-3xl font-semibold text-gray-800 mb-2">活动商品列表</h1>
      <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:12px;">
        <div style="display:flex;gap:8px;align-items:center;">
          <el-input v-model="keyword" placeholder="按标题搜索" clearable style="width:260px;" @keyup.enter="fetchList" />
          <el-select v-model="status" placeholder="状态" clearable style="width:160px;">
            <el-option label="全部" :value="''" />
            <el-option label="草稿" value="DRAFT" />
            <el-option label="已发布" value="PUBLISHED" />
            <el-option label="已结束" value="FINISHED" />
            <el-option label="待审核" value="PENDING" />
          </el-select>
          <el-button type="primary" @click="fetchList" size="small">搜索</el-button>
        </div>

        <div>
          <el-button type="primary" @click="onNew" size="small">新建活动</el-button>
        </div>
      </div>

      <el-table :data="list" stripe>
        <el-table-column prop="title" label="标题" />
        <el-table-column label="封面" width="140">
          <template #default="{ row }">
            <el-image :src="row.imageUrl || row.coverUrl || ''" style="width:96px;height:56px" fit="cover" />
          </template>
        </el-table-column>
        <el-table-column label="时间" width="240">
          <template #default="{ row }">
            {{ formatDate(row.startTime) }} <span style="color:#999">-</span> {{ formatDate(row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="location" label="地点" width="180" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="tagType(row.status)">{{ row.status || 'UNKNOWN' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="160">
          <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="320">
          <template #default="{ row }">
            <el-button size="small" @click="onView(row)">查看</el-button>
            <el-button size="small" type="primary" @click="onEdit(row)">编辑</el-button>
            <el-button size="small" type="warning" v-if="row.status === 'PENDING'" @click="onReview(row)">审核</el-button>
            <el-button size="small" type="danger" @click="onDelete(row)">删除</el-button>
            <el-button size="small" @click="onStats(row)">统计</el-button>
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
import { useRouter } from 'vue-router';
import { getAdminEvents, deleteEvent } from '@/api/admin';

export default defineComponent({
  name: 'ActivityList',
  setup() {
    const router = useRouter();
    const list = ref<any[]>([]);
    const page = ref(1);
    const pageSize = ref(10);
    const total = ref(0);
    const keyword = ref('');
    const status = ref<string>('');

    const formatDate = (v: any) => {
      if (!v) return '-';
      return dayjs(v).isValid() ? dayjs(v).format('YYYY-MM-DD HH:mm') : String(v);
    };

    const tagType = (s: string) => {
      if (!s) return 'info';
      switch (s) {
        case 'DRAFT': return 'info';
        case 'PUBLISHED': return 'success';
        case 'FINISHED': return 'warning';
        case 'PENDING': return 'danger';
        default: return 'info';
      }
    };

    const fetchList = async () => {
      try {
        const params = { page: page.value, pageSize: pageSize.value, keyword: keyword.value, status: status.value };
        const res: any = await getAdminEvents(params);
        list.value = res?.items || [];
        total.value = res?.total || 0;
      } catch (e) {
        console.error('getAdminEvents error', e);
      }
    };

    const onPageChange = (p: number) => {
      page.value = p;
      fetchList();
    };

    const onNew = () => {
      router.push({ name: 'activityNew' });
    };

    const onEdit = (row: any) => {
      router.push({ name: 'activityEdit', params: { id: String(row.id) } });
    };

    const onView = (row: any) => {
      // 跳转到新的查看页面 (activityDetailView)
      router.push({ name: 'activityDetailView', params: { id: String(row.id) } });
    };

    const onReview = (row: any) => {
      // 跳转到审核页并传入活动 id
      router.push({ name: 'activityReview', params: { id: String(row.id) } });
    };

    const onDelete = (row: any) => {
      (window as any).ElMessageBox && (window as any).ElMessageBox.confirm
          ? (window as any).ElMessageBox.confirm('确认删除该活动？', '删除', { type: 'warning' })
              .then(async () => {
                try {
                  await deleteEvent(row.id);
                  (window as any).ElMessage.success('删除成功');
                  fetchList();
                } catch (err) {
                  (window as any).ElMessage.error('删除失败');
                }
              }).catch(() => {})
          : (async () => { if (confirm('确认删除该活动？')) { await deleteEvent(row.id); fetchList(); } })();
    };

    const onStats = (row: any) => {
      router.push({ name: 'activityStats', params: { id: String(row.id) } });
    };

    onMounted(() => {
      fetchList();
    });

    return {
      list, page, pageSize, total, keyword, status,
      fetchList, onPageChange, onNew, onEdit, onView, onReview, onDelete, onStats,
      formatDate, tagType,
    };
  },
});
</script>

<style scoped>
/* 可按需微调 */
</style>