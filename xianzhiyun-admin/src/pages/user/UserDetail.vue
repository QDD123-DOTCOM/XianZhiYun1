<template>
  <div>
    <el-card>
      <div style="display:flex;align-items:center;gap:12px;">
        <el-avatar :src="user?.avatarUrl" size="large" v-if="user && user.avatarUrl" />
        <div>
          <div style="font-size:18px;font-weight:600;">{{ user?.nickname || '-' }}</div>
          <div style="color:#888;">
            学号/账号：{{ user?.openid || '-' }} &nbsp; 联系：{{ user?.phone || '-' }}
          </div>
        </div>
        <div style="margin-left:auto;color:#666;">注册：{{ formatDate(user?.createTime) }}</div>
      </div>

      <el-divider />

      <div style="margin-bottom:12px;display:flex;gap:8px;align-items:center;">
        <el-input
            v-model="keyword"
            placeholder="按商品标题搜索"
            style="width:320px;"
            @keyup.enter.native="onSearch"
        />
        <el-button type="primary" size="small" @click="onSearch">搜索</el-button>
      </div>

      <el-table :data="goodsList" style="width:100%">
        <el-table-column label="缩略" width="100">
          <template #default="{ row }">
            <el-image :src="row.coverUrl || ''" style="width:64px;height:64px" fit="cover" />
          </template>
        </el-table-column>

        <el-table-column prop="title" label="标题" />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">¥{{ formatPrice(row.price) }}</template>
        </el-table-column>
        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column prop="status" label="状态" width="120" />
        <el-table-column label="发布时间" width="160">
          <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
        </el-table-column>

        <el-table-column label="操作" width="140">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="viewGoods(row)">查看</el-button>
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
import { useRoute, useRouter } from 'vue-router';
import dayjs from 'dayjs';
import { getUserById, getUserGoodsBySeller } from '../../api/admin';

export default defineComponent({
  name: 'UserDetail',
  setup() {
    const route = useRoute();
    const router = useRouter();
    const userId = Number(route.params.id);
    const user = ref<any>(null);

    const goodsList = ref<any[]>([]);
    const page = ref<number>(1);
    const pageSize = ref<number>(5); // 固定为 5
    const total = ref<number>(0);
    const keyword = ref<string>('');

    const formatDate = (v: any) => {
      if (!v) return '-';
      return dayjs(v).isValid() ? dayjs(v).format('YYYY-MM-DD HH:mm') : String(v);
    };

    const formatPrice = (p: any) => {
      if (p == null) return '-';
      const n = Number(p);
      if (Number.isNaN(n)) return String(p);
      return n.toFixed(2);
    };

    const fetchUser = async () => {
      try {
        const data: any = await getUserById(userId); // 返回后端 JsonResult.data
        user.value = data || null;
      } catch (e) {
        console.error('getUserById error', e);
      }
    };

    const fetchGoods = async () => {
      try {
        // 注意：当前 /api/goods/bySeller 可能未实现 keyword 搜索，keyword 会被忽略，需后端扩展以支持搜索
        const res: any = await getUserGoodsBySeller(userId, page.value, pageSize.value);
        goodsList.value = Array.isArray(res?.items) ? res.items : [];
        total.value = typeof res?.total === 'number' ? res.total : 0;
      } catch (e) {
        console.error('getUserGoodsBySeller error', e);
      }
    };

    const onPageChange = (p: number) => {
      page.value = p;
      fetchGoods();
    };

    const viewGoods = (row: any) => {
      if (!row || !row.id) return;
      router.push({ path: `/goods/${row.id}` });
    };

    const onSearch = () => {
      // 目前后端接口可能不支持 keyword；若支持，请将 keyword 传到 getUserGoodsBySeller 或扩展后端
      page.value = 1;
      fetchGoods();
    };

    onMounted(() => {
      fetchUser();
      fetchGoods();
    });

    return {
      user,
      goodsList,
      page,
      pageSize,
      total,
      keyword,
      fetchGoods,
      onPageChange,
      viewGoods,
      formatDate,
      formatPrice,
      onSearch,
    };
  },
});
</script>

<style scoped>
/* 根据需要添加样式 */
</style>