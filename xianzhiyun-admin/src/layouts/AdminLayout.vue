<template>
  <el-container style="height:100vh">
    <el-aside width="200px">
      <!-- 菜单：使用 $route.path 保持激活状态，并开启路由模式 -->
      <el-menu :default-active="$route.path" router>
        <el-menu-item index="/dashboard">仪表盘</el-menu-item>
        <el-menu-item index="/users">用户管理</el-menu-item>

        <!-- 商品管理菜单项 -->
        <el-menu-item index="/goods">商品管理</el-menu-item>
        <el-menu-item index="/goods/review">商品审核</el-menu-item> <!-- ✅ 新增：商品审核 -->

        <!-- 活动管理菜单项 -->
        <el-menu-item index="/activities">活动管理</el-menu-item>
        <el-menu-item index="/activities/pending">活动审核</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header style="display:flex;justify-content:flex-end;align-items:center;">
        <el-button type="text" @click="logout">退出</el-button>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import { useAdminStore } from '../store/admin';
import { useRouter } from 'vue-router';

export default defineComponent({
  name: 'AdminLayout',
  setup() {
    const adminStore = useAdminStore();
    const router = useRouter();
    const logout = () => {
      adminStore.clearAuth();
      router.push('/login');
    };
    return { logout };
  },
});
</script>
