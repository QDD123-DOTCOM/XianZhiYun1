// src/api/admin.ts
import request from '../utils/request';

/**
 * 管理端 API 集中管理（会话/活动/审核/商品等）
 *
 * 说明：
 * - 这里直接返回 request(...) 的结果，utils/request 已在响应拦截器中做了 data 解包。
 */

// ---------------- 管理登录 ----------------
export async function adminLogin(payload: { username: string; password: string }) {
    return request.post('/api/admin/login', payload);
}

// ---------------- 活动（Admin）相关 ----------------
export async function getAdminEvents(params: { page?: number; pageSize?: number; keyword?: string; status?: string }) {
    return request.get('/api/admin/events', { params });
}

// 兼容前端示例中的 listAdminEvents 名称（封装 getAdminEvents）
export async function listAdminEvents(params: { page?: number; pageSize?: number; keyword?: string; status?: string }) {
    return getAdminEvents(params);
}

export async function getAdminEvent(id: number | string) {
    return request.get(`/api/admin/events/${id}`);
}

export async function createEvent(payload: any) {
    return request.post('/api/admin/events', payload);
}

export async function updateEvent(id: number | string, payload: any) {
    return request.put(`/api/admin/events/${id}`, payload);
}

export async function deleteEvent(id: number | string) {
    return request.delete(`/api/admin/events/${id}`);
}

// 若后端提供 /api/admin/events/pending 路由（列出待审核活动）
export async function listPendingEvents(params: { page?: number; pageSize?: number; keyword?: string }) {
    return request.get('/api/admin/events/pending', { params });
}

export async function reviewEvent(id: number | string, body: { action: string; comment?: string }) {
    return request.post(`/api/admin/events/${id}/review`, body);
}

export async function getEventStats(id: number | string) {
    return request.get(`/api/admin/events/${id}/stats`);
}

export async function getEventFeedbacks(id: number | string, params?: { limit?: number }) {
    return request.get(`/api/admin/events/${id}/feedbacks`, { params });
}

// ---------------- 活动下的待审核商品（donation_item） ----------------
/**
 * 获取某活动下（eventId）捐赠物品（管理员视角）
 * params: { page, pageSize, keyword, status }
 */
export async function getEventItemsForAdmin(eventId: number | string, params: { page?: number; pageSize?: number; keyword?: string; status?: string }) {
    return request.get(`/api/admin/events/${eventId}/items`, { params });
}

/**
 * 管理员对单个 item 审核
 * body: { action: 'APPROVED' | 'REJECTED', comment?: string }
 */
export async function reviewItem(itemId: number | string, body: { action: string; comment?: string }) {
    return request.post(`/api/admin/items/${itemId}/review`, body);
}

// 如果需要一个在未选活动时列出所有待审核 item 的接口（后端需提供）
// 示例：GET /api/admin/items/pending?page=...&pageSize=...&keyword=...
export async function listAllPendingItems(params: { page?: number; pageSize?: number; keyword?: string }) {
    return request.get('/api/admin/items/pending', { params });
}

// ---------------- 用户管理（示例） ----------------
export async function fetchUsers(params: any) {
    return request.get('/api/admin/users', { params });
}

export async function banUser(userId: number | string, reason?: string) {
    return request.post(`/api/admin/users/${userId}/ban`, { reason });
}

export async function unbanUser(userId: number | string) {
    return request.post(`/api/admin/users/${userId}/unban`);
}
// 管理端：商品管理
export async function getAdminGoods(params: { page?: number; pageSize?: number; keyword?: string; status?: string; sellerId?: number; sellerKeyword?: string; category?: string }) {
    return request.get('/api/admin/goods', { params });
}

export async function getAdminGood(id: number | string) {
    return request.get(`/api/admin/goods/${id}`);
}

export async function updateAdminGoodStatus(id: number | string, body: { status: string }) {
    return request.post(`/api/admin/goods/${id}/status`, body);
}

export async function deleteAdminGood(id: number | string) {
    return request.delete(`/api/admin/goods/${id}`);
}

// ---------------- 其他 admin API 占位（按需扩展） ----------------
// 例如：商品审核日志、活动导出、统计报表等
// export async function exportEvents(params: any) {
//   return request.get('/api/admin/events/export', { params, responseType: 'blob' });
//}