<template>
  <div>
    <el-card>
      <h1 class="text-3xl font-semibold text-gray-800 mb-2">商品管理页面</h1>
      <div style="display:flex;gap:12px;align-items:center;margin-bottom:16px;">
        <el-input v-model="keyword" placeholder="按标题搜索" clearable style="width:320px;" @keyup.enter="fetchList" />
        <el-select v-model="status" placeholder="状态" clearable style="width:150px;">
          <el-option label="全部" value="" />
          <el-option label="已上架" value="ON_SALE" />
          <el-option label="待审核" value="PENDING" />
          <el-option label="已下架" value="REJECTED" />
        </el-select>
        <el-button type="primary" size="small" @click="fetchList">搜索</el-button>
        <el-button size="small" @click="resetFilters">重置</el-button>
        <div style="margin-left:auto">
          <el-button type="primary" @click="onNew">新增商品</el-button>
        </div>
      </div>

      <el-table :data="list" stripe style="width:100%">
        <el-table-column label="缩略" width="120">
          <template #default="{ row }">
            <el-image
                :src="firstCover(row.coverUrls || row.imageUrl)"
                style="width:96px;height:64px"
                fit="cover" />
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="220" />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">¥{{ Number(row.price).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="300">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="onEdit(row)">编辑</el-button>
            <el-button v-if="row.status !== 'ON_SALE'" size="small" type="success" @click="changeStatus(row, 'ON_SALE')">上架</el-button>
            <el-button v-else size="small" type="warning" @click="changeStatus(row, 'REJECTED')">下架</el-button>
            <el-button size="small" type="danger" @click="onDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div style="margin-top:12px;text-align:right;">
        <el-pagination background :current-page="page" :page-size="pageSize" :total="total" @current-change="onPageChange" />
      </div>
    </el-card>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted } from 'vue';
import dayjs from 'dayjs';
import { getAdminGoods, updateAdminGoodStatus, deleteAdminGood } from '@/api/admin';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';

export default defineComponent({
  setup() {
    const router = useRouter();
    const list = ref<any[]>([]);
    const page = ref(1);
    const pageSize = ref(10);
    const total = ref(0);
    const keyword = ref('');
    const status = ref('');

    // 【关键修改点】动态修正图片 IP
    const firstCover = (coverUrls: string) => {
      if (!coverUrls) return '';
      let url = coverUrls.split(',')[0];
      return url.replace(/http:\/\/192\.168\.\d+\.\d+:8088/, 'http://localhost:8088');
    };

    const formatDate = (v: any) => v ? dayjs(v).format('YYYY-MM-DD HH:mm') : '-';
    const statusTag = (s: string) => {
      if (s === 'ON_SALE') return 'success';
      if (s === 'PENDING') return 'warning';
      return 'danger';
    };

    const fetchList = async () => {
      const res: any = await getAdminGoods({ page: page.value, pageSize: pageSize.value, keyword: keyword.value, status: status.value });
      list.value = res?.items || [];
      total.value = res?.total || 0;
    };

    const onPageChange = (p: number) => { page.value = p; fetchList(); };
    const onEdit = (row: any) => router.push({ name: 'goodsEdit', params: { id: String(row.id) }});
    const onNew = () => router.push({ name: 'goodsNew' });

    const changeStatus = async (row: any, newStatus: string) => {
      await updateAdminGoodStatus(row.id, { status: newStatus });
      ElMessage.success('操作成功');
      fetchList();
    };

    const onDelete = async (row: any) => {
      ElMessageBox.confirm('确认删除？').then(async () => {
        await deleteAdminGood(row.id);
        ElMessage.success('删除成功');
        fetchList();
      });
    };

    const resetFilters = () => { keyword.value = ''; status.value = ''; fetchList(); };

    onMounted(fetchList);
    return { list, page, pageSize, total, keyword, status, fetchList, onPageChange, onEdit, changeStatus, onDelete, resetFilters, onNew, formatDate, firstCover, statusTag };
  }
});
</script>
