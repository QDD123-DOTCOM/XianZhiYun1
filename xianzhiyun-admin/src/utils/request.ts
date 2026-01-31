import axios, { type AxiosRequestConfig } from 'axios';
import { ElMessage } from 'element-plus';

const baseURL = import.meta.env.VITE_API_BASE_URL || '';

const service = axios.create({
    baseURL,
    timeout: 10000,
    headers: { 'Content-Type': 'application/json' },
});

// 请求拦截器：从 localStorage 读取 token，避免静态引用 store 导致循环依赖或导出问题
service.interceptors.request.use((config: any) => {
    try {
        const token = localStorage.getItem('admin_token');
        if (token && config.headers) {
            config.headers.Authorization = `Bearer ${token}`;
        }
    } catch (e) {
        // ignore
    }
    return config;
});

// 响应拦截器
service.interceptors.response.use(
    (response: any) => {
        const data = response.data;
        // 适配后端 JsonResult { code, msg, data }
        if (data && typeof data.code !== 'undefined') {
            if (data.code === 0) {
                // 成功时返回 data 字段的内容，即解包后的数据 (T)
                return data.data;
            } else {
                ElMessage.error(data.msg || '请求错误');
                return Promise.reject(data);
            }
        }
        return data;
    },
    (error: any) => {
        // 修正 401 权限验证失败的逻辑，避免 ReferenceError/循环依赖
        if (error.response && error.response.status === 401) {
            ElMessage.error('权限验证失败或登录过期，正在跳转登录页...');
            localStorage.removeItem('admin_token');
            // 使用原生跳转避免 Vue Router 循环依赖
            setTimeout(() => {
                window.location.href = '/login';
            }, 500);
        } else {
            ElMessage.error(error.message || '网络错误');
        }
        return Promise.reject(error);
    }
);

// 封装 request 对象，以便在 Vue 组件中以 request.get/post 方式调用
// 返回类型为 Promise<T>，因为拦截器已返回 data.data (解包后的数据)
export const request = {
    // 强制断言最终返回类型为 T (解包后的数据)
    get: <T = any>(url: string, config?: AxiosRequestConfig): Promise<T> => service.get(url, config) as Promise<T>,
    post: <T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> => service.post(url, data, config) as Promise<T>,
    put: <T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> => service.put(url, data, config) as Promise<T>,
    delete: <T = any>(url: string, config?: AxiosRequestConfig): Promise<T> => service.delete(url, config) as Promise<T>,
};

export default service;
