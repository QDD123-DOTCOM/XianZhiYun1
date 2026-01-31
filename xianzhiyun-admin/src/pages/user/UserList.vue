<template>
  <div>
    <el-card>
      <h1 class="text-3xl font-semibold text-gray-800 mb-2">用户管理页面</h1>
      <div style="display:flex;gap:10px;margin-bottom:12px;">
        <el-input v-model="keyword" placeholder="按学号/昵称搜索" style="width:300px;" />
        <el-button type="primary" @click="fetchList">搜索</el-button>
      </div>

      <el-table :data="list" style="width:100%">
        <el-table-column prop="nickname" label="昵称" />
        <el-table-column prop="openid" label="学号/账号" />
        <el-table-column prop="phone" label="联系方式" />
        <!-- 使用后端返回的 createTime 字段并格式化 -->
        <el-table-column label="注册时间">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>

        <!-- 状态列：若后端返回 status 则显示其值并根据值着色；否则显示“正常” -->
        <el-table-column label="状态">
          <template #default="{ row }">
            <el-tag
                v-if="row && row.status"
                :type="row.status === 'BANNED' ? 'danger' : (row.status === 'INACTIVE' ? 'warning' : 'success')"
            >
              {{ row.status }}
            </el-tag>
            <el-tag v-else type="success">正常</el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="240">
          <template #default="{ row }">
            <el-button size="mini" @click="viewDetail(row)">详情</el-button>
            <el-button size="mini" type="danger" v-if="!isBanned(row)" @click="ban(row)">封禁</el-button>
            <el-button size="mini" v-else @click="unban(row)">解封</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div style="margin-top:12px;text-align:right;">
        <el-pagination
            background
            :page-size="pageSize"
            :current-page="page"
            :total="total"
            @current-change="onPageChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref } from 'vue';
import dayjs from 'dayjs';
import { fetchUsers, banUser, unbanUser } from '../../api/admin';
import { ElMessageBox, ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';

export default defineComponent({
  name: 'UserList',
  setup() {
    const list = ref<any[]>([]);
    const page = ref(1);
    const pageSize = ref(10);
    const total = ref(0);
    const keyword = ref('');
    const router = useRouter();

    const formatDate = (v: string | null | undefined) => {
      if (!v) return '-';
      // 兼容 ISO 字符串与时间戳
      return dayjs(v).isValid() ? dayjs(v).format('YYYY-MM-DD HH:mm') : String(v);
    };

    const isBanned = (row: any) => {
      if (!row) return false;
      // 如果后端使用 'status' 字段且值为 'BANNED' 表示被封禁
      return row.status === 'BANNED' || row.status === 'BANNED';
    };

    const fetchList = async () => {
      try {
        const params = { page: page.value, size: pageSize.value, keyword: keyword.value };
        // fetchUsers 假设返回 data = { list, total }
        const res: any = await fetchUsers(params);
        // 后端返回 JsonResult.data，data 应包含 { total, list } 或类似结构
        // 兼容几种可能命名
        if (!res) {
          list.value = [];
          total.value = 0;
          return;
        }
        if (Array.isArray(res.list)) {
          list.value = res.list;
          total.value = res.total || 0;
        } else if (Array.isArray(res.items)) {
          list.value = res.items;
          total.value = res.total || 0;
        } else if (Array.isArray(res)) {
          list.value = res;
          total.value = res.length;
        } else {
          list.value = [];
          total.value = 0;
        }
      } catch (err: any) {
        // 如果后端还没实现接口或返回 404，你可以在这里临时 mock（可选）
        console.error('fetchUsers error', err);
        // 可选：临时回退 mock 数据，便于开发 UI（取消注释以启用）
        // list.value = [{ id:1, openid:'stu01', nickname:'测试用户', phone:'10086', createTime:'2025-10-17T12:00:00', status:'ACTIVE' }];
        // total.value = 1;
      }
    };

    const onPageChange = (p: number) => {
      page.value = p;
      fetchList();
    };

    const ban = (row: any) => {
      ElMessageBox.prompt('请输入封禁原因', '封禁用户', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
      })
          .then(({ value }) => {
            banUser(row.id, value)
                .then(() => {
                  ElMessage.success('已封禁');
                  fetchList();
                })
                .catch((e) => {
                  ElMessage.error('封禁失败');
                  console.error(e);
                });
          })
          .catch(() => {});
    };

    const unban = (row: any) => {
      ElMessageBox.confirm('确认解封该用户？', '解封', { type: 'warning' })
          .then(() => {
            unbanUser(row.id)
                .then(() => {
                  ElMessage.success('已解封');
                  fetchList();
                })
                .catch((e) => {
                  ElMessage.error('解封失败');
                  console.error(e);
                });
          })
          .catch(() => {});
    };

    const viewDetail = (row: any) => {
      if (!row || !row.id) return;
      router.push({ name: 'userDetail', params: { id: String(row.id) } });
    };

    // 初次加载
    fetchList();

    return {
      list,
      page,
      pageSize,
      total,
      keyword,
      fetchList,
      onPageChange,
      ban,
      unban,
      viewDetail,
      formatDate,
      isBanned,
    };
  },
});
</script>

<style scoped>
/* 可根据需要添加样式 */
</style>