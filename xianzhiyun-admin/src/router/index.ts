import { createRouter, createWebHistory } from 'vue-router';
import type { RouteLocationNormalized } from 'vue-router';
import Login from '../Login.vue';
import AdminLayout from '../layouts/AdminLayout.vue';
import Dashboard from '../Dashboard.vue';
import UserList from '../pages/user/UserList.vue';
const GoodsReview = () => import('../pages/goods/GoodsReview.vue');

const UserDetail = () => import('../pages/user/UserDetail.vue');

// 活动相关页面（按需懒加载）
const ActivityList = () => import('../pages/activity/ActivityList.vue');
const ActivityDetail = () => import('../pages/activity/ActivityDetail.vue');
const ActivityDetailView = () => import('../pages/activity/ActivityDetailView.vue');
const ActivityEdit = () => import('../pages/activity/ActivityEdit.vue');
const ActivityReview = () => import('../pages/activity/ActivityReview.vue');


// 商品相关页面
const GoodsManage = () => import('../pages/goods/GoodsManage.vue');
// 如果你未来添加编辑页，可以在那时引入 GoodsEdit.vue

const routes = [
    { path: '/login', component: Login },
    {
        path: '/',
        component: AdminLayout,
        children: [
            { path: 'dashboard', name: 'dashboard', component: Dashboard },
            { path: 'activities', name: 'activities', component: ActivityList },
            { path: 'activities/new', name: 'activityNew', component: ActivityEdit },
            { path: 'activities/:id', name: 'activityDetail', component: ActivityDetail, props: true },
            { path: 'activities/:id/view', name: 'activityDetailView', component: ActivityDetailView, props: true },
            { path: 'activities/:id/edit', name: 'activityEdit', component: ActivityEdit, props: true },
            { path: 'activities/:id/review', name: 'activityReview', component: ActivityReview, props: true },
            { path: 'activities/:id/stats', name: 'activityStats', component: ActivityDetail, props: true },
            { path: 'goods/review', name: 'goodsReview', component: GoodsReview },

            // 商品管理路由
            { path: 'goods', name: 'goods', component: GoodsManage },

            { path: 'users', name: 'users', component: UserList },
            { path: 'users/:id', name: 'userDetail', component: UserDetail, props: true },

            { path: '', redirect: '/dashboard' },
        ],
    },
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

router.beforeEach((to: RouteLocationNormalized) => {
    if (to.path !== '/login') {
        const token = localStorage.getItem('admin_token');
        if (!token) return '/login';
    }
    return true;
});

export default router;