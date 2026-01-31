import { defineStore } from 'pinia';

export const useAdminStore = defineStore('admin', {
    state: () => ({
        token: localStorage.getItem('admin_token') || '',
        admin: null as any,
    }),
    actions: {
        setAuth(token: string, admin: any) {
            this.token = token || '';
            this.admin = admin || null;
            if (token) localStorage.setItem('admin_token', token);
            else localStorage.removeItem('admin_token');
        },
        clearAuth() {
            this.token = '';
            this.admin = null;
            localStorage.removeItem('admin_token');
        },
    },
});