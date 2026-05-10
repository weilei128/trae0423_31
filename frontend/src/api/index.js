import request from '@/utils/request'

export const drugApi = {
  listDict: (params) => request.get('/drug/dict/page', { params }),
  listAllDict: () => request.get('/drug/dict/list'),
  getDict: (id) => request.get(`/drug/dict/${id}`),
  saveDict: (data) => request.post('/drug/dict', data),
  updateDict: (data) => request.put('/drug/dict', data),
  deleteDict: (id) => request.delete(`/drug/dict/${id}`),
  listCategory: () => request.get('/drug/category/list'),
  saveCategory: (data) => request.post('/drug/category', data),
  updateCategory: (data) => request.put('/drug/category', data),
  deleteCategory: (id) => request.delete(`/drug/category/${id}`)
}

export const purchaseApi = {
  list: (params) => request.get('/purchase/page', { params }),
  get: (id) => request.get(`/purchase/${id}`),
  getItems: (id) => request.get(`/purchase/${id}/items`),
  save: (data) => request.post('/purchase', data),
  update: (data) => request.put('/purchase', data),
  submit: (id) => request.post(`/purchase/${id}/submit`),
  audit: (id, status) => request.post(`/purchase/${id}/audit?status=${status}`),
  cancel: (id) => request.post(`/purchase/${id}/cancel`),
  delete: (id) => request.delete(`/purchase/${id}`)
}

export const stockApi = {
  list: (params) => request.get('/stock/page', { params }),
  getByDrug: (drugId) => request.get(`/stock/drug/${drugId}`),
  listIn: (params) => request.get('/stock/in/page', { params }),
  getIn: (id) => request.get(`/stock/in/${id}`),
  getInItems: (id) => request.get(`/stock/in/${id}/items`),
  saveIn: (data) => request.post('/stock/in', data),
  confirmIn: (id) => request.post(`/stock/in/${id}/confirm`),
  cancelIn: (id) => request.post(`/stock/in/${id}/cancel`),
  listOut: (params) => request.get('/stock/out/page', { params }),
  getOut: (id) => request.get(`/stock/out/${id}`),
  getOutItems: (id) => request.get(`/stock/out/${id}/items`),
  saveOut: (data) => request.post('/stock/out', data),
  reviewOut: (id, status, reviewer) => request.post(`/stock/out/${id}/review?status=${status}&reviewer=${reviewer}`),
  confirmOut: (id) => request.post(`/stock/out/${id}/confirm`)
}

export const inventoryApi = {
  list: (params) => request.get('/inventory/page', { params }),
  get: (id) => request.get(`/inventory/${id}`),
  create: (data) => request.post('/inventory', data),
  start: (id) => request.post(`/inventory/${id}/start`),
  complete: (id) => request.post(`/inventory/${id}/complete`),
  getItems: (taskId) => request.get(`/inventory/${taskId}/items`),
  listItems: (params) => request.get('/inventory/items/page', { params }),
  updateItem: (data) => request.put('/inventory/item', data)
}

export const expiryApi = {
  list: (params) => request.get('/expiry/page', { params }),
  getPending: () => request.get('/expiry/pending'),
  generate: () => request.post('/expiry/generate'),
  handle: (id, data) => request.post(`/expiry/${id}/handle`, data)
}

export const reportApi = {
  getConsumption: (params) => request.get('/report/consumption', { params }),
  getPurchase: (params) => request.get('/report/purchase', { params }),
  getTurnover: () => request.get('/report/turnover')
}

export const commonApi = {
  listSupplierPage: (params) => request.get('/common/supplier/page', { params }),
  listSupplier: () => request.get('/common/supplier/list'),
  saveSupplier: (data) => request.post('/common/supplier', data),
  updateSupplier: (data) => request.put('/common/supplier', data),
  deleteSupplier: (id) => request.delete(`/common/supplier/${id}`),
  listDepartment: () => request.get('/common/department/list'),
  saveDepartment: (data) => request.post('/common/department', data),
  updateDepartment: (data) => request.put('/common/department', data),
  deleteDepartment: (id) => request.delete(`/common/department/${id}`),
  listLocation: () => request.get('/common/location/list'),
  saveLocation: (data) => request.post('/common/location', data),
  updateLocation: (data) => request.put('/common/location', data),
  deleteLocation: (id) => request.delete(`/common/location/${id}`)
}
