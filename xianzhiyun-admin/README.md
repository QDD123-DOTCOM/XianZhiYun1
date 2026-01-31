# xianzhiyun-admin

前端管理后台（Vue 3 + TypeScript + Element Plus）

运行：
1. 安装依赖：
   npm install

2. 启动开发：
   npm run dev

配置：
- 在 .env.development 中设置 VITE_API_BASE_URL 为后端地址（例如 http://localhost:8080）。
- 后端登录接口 /api/admin/login 建议返回 JsonResult.data 为 { token, admin }。

注意：
- 如果出现 “Cannot find module” 等错误，确认已安装依赖：
  npm install vue-router@4 pinia axios element-plus dayjs
- 若 IDE 未识别类型，重启编辑器让 TypeScript/Vol ar 重新索引 node_modules。