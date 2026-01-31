<template>
  <div>
    <el-card>
      <el-form :model="form" ref="formRef" label-width="90px" :rules="rules" v-if="loaded">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" />
        </el-form-item>

        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker
              v-model="form.startTime"
              type="datetime"
              placeholder="选择开始时间"
              style="width:320px;"
          />
        </el-form-item>

        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker
              v-model="form.endTime"
              type="datetime"
              placeholder="选择结束时间"
              style="width:320px;"
          />
        </el-form-item>

        <el-form-item label="地点" prop="location">
          <el-input v-model="form.location" />
        </el-form-item>

        <el-form-item label="简介" prop="description">
          <el-input type="textarea" v-model="form.description" rows="6" />
        </el-form-item>

        <el-form-item label="封面">
          <el-upload
              :action="uploadUrl"
              :show-file-list="false"
              :on-success="handleUploadSuccess"
              :before-upload="beforeUpload"
              accept="image/*"
              list-type="picture"
          >
            <el-button size="small">上传封面</el-button>
          </el-upload>
          <div v-if="form.imageUrl" style="margin-top:8px;">
            <el-image :src="form.imageUrl" style="width:160px;height:90px" fit="cover" />
          </div>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="onSubmit" size="small">保存并发布</el-button>
          <el-button @click="onSaveDraft" size="small">保存为草稿</el-button>
          <el-button @click="onCancel" size="small">取消</el-button>
        </el-form-item>
      </el-form>
      <div v-else style="text-align:center;padding:40px;">加载中...</div>
    </el-card>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import dayjs from 'dayjs';
import { getAdminEvent, createEvent, updateEvent } from '@/api/admin';

export default defineComponent({
  name: 'ActivityEdit',
  setup() {
    const route = useRoute();
    const router = useRouter();
    const id = route.params.id ? Number(route.params.id) : null;
    const loaded = ref(false);
    const formRef: any = ref(null);
    const form = ref<any>({
      title: '',
      startTime: null,
      endTime: null,
      location: '',
      description: '',
      imageUrl: '',
      status: 'DRAFT',
    });

    const rules = {
      title: [{ required: true, message: '请填写标题', trigger: 'blur' }],
      startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
      endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
    };

    const uploadUrl = '/api/upload';

    const handleUploadSuccess = (res: any) => {
      // upload 接口返回 { code, msg, data:{ url } } 或类似结构
      const url = res?.data?.url || res?.url || (res && res.url) || (res && res.data && res.data.url);
      if (url) form.value.imageUrl = url;
    };

    const beforeUpload = (file: File) => {
      const isImg = file.type.startsWith('image/');
      if (!isImg) {
        (window as any).ElMessage && (window as any).ElMessage.error('只能上传图片');
        return false;
      }
      return true;
    };

    const load = async () => {
      if (id) {
        try {
          const data: any = await getAdminEvent(id);
          if (data) {
            form.value.title = data.title || '';
            form.value.description = data.description || '';
            form.value.startTime = data.startTime ? new Date(data.startTime) : null;
            form.value.endTime = data.endTime ? new Date(data.endTime) : null;
            form.value.location = data.location || '';
            form.value.imageUrl = data.imageUrl || data.coverUrl || '';
            form.value.status = data.status || 'DRAFT';
          }
        } catch (e) {
          console.error('getAdminEvent', e);
        }
      }
      loaded.value = true;
    };

    const onSubmit = () => {
      formRef.value?.validate(async (valid: boolean) => {
        if (!valid) return;
        // 设置为已发布
        form.value.status = 'PUBLISHED';
        await submitToServer();
      });
    };

    const onSaveDraft = () => {
      form.value.status = 'DRAFT';
      submitToServer();
    };

    const submitToServer = async () => {
      try {
        const payload = {
          title: form.value.title,
          description: form.value.description,
          startTime: form.value.startTime ? dayjs(form.value.startTime).toISOString() : null,
          endTime: form.value.endTime ? dayjs(form.value.endTime).toISOString() : null,
          location: form.value.location,
          imageUrl: form.value.imageUrl,
          status: form.value.status,
        };
        if (id) {
          await updateEvent(id, payload);
          (window as any).ElMessage && (window as any).ElMessage.success('更新成功');
        } else {
          const r: any = await createEvent(payload);
          (window as any).ElMessage && (window as any).ElMessage.success('创建成功');
          // 跳转到详情页
          const newId = r?.id || (r && r.data && r.data.id);
          if (newId) router.push({ name: 'activityDetail', params: { id: String(newId) } });
        }
        router.push({ name: 'activities' });
      } catch (e) {
        console.error('submitToServer', e);
        (window as any).ElMessage && (window as any).ElMessage.error('保存失败');
      }
    };

    const onCancel = () => {
      router.back();
    };

    onMounted(() => {
      load();
    });

    return {
      formRef, form, loaded, uploadUrl, rules, handleUploadSuccess, beforeUpload,
      onSubmit, onSaveDraft, onCancel,
    };
  },
});
</script>

<style scoped>
/* 可按需调整 */
</style>