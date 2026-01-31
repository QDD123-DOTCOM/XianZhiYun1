<template>
  <div class="login-page" style="display:flex;align-items:center;justify-content:center;height:100vh;">
    <el-card style="width:360px;">
      <h3 style="text-align:center;margin-bottom:20px;">管理端登录</h3>
      <el-form :model="form" ref="formRef" label-position="top">
        <el-form-item label="用户名">
          <el-input v-model="form.username" autocomplete="username" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input type="password" v-model="form.password" autocomplete="current-password" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" block @click="onSubmit">登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script lang="ts">
import { defineComponent, reactive, ref } from 'vue';
import { adminLogin } from './api/admin';
import { useAdminStore } from './store/admin';
import { useRouter } from 'vue-router';

export default defineComponent({
  name: 'Login',
  setup() {
    const form = reactive({ username: '', password: '' });
    const formRef = ref(null);
    const adminStore = useAdminStore();
    const router = useRouter();

    const onSubmit = async () => {
      if (!form.username || !form.password) return;
      try {
        // adminLogin 接受一个对象 payload
        const res: any = await adminLogin({ username: form.username, password: form.password });
        // 假设后端返回的数据结构是 { token, admin }
        const token = res?.token || '';
        const admin = res?.admin || res || null;
        // 保存 token 到 localStorage 并同步到 pinia
        if (token) {
          localStorage.setItem('admin_token', token);
          adminStore.setAuth(token, admin);
        } else {
          // 若后端未返回 token，仅保存 admin 信息（开发阶段）
          adminStore.setAuth('', admin);
        }
        router.push('/');
      } catch (err) {
        // 错误由拦截器或这里处理
        console.error('登录失败', err);
      }
    };

    return { form, formRef, onSubmit };
  },
});
</script>