import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import path from 'path';

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src'),
    },
  },
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8088',
        changeOrigin: true,
        // 保留 /api 前缀，rewrite 可根据需要修改，当前不做路径替换
        rewrite: (p) => p.replace(/^\/api/, '/api'),
      },
    },
  },
});