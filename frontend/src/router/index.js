import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/views/Layout.vue'

const routes = [
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('@/views/Dashboard.vue'), meta: { title: '首页' } },
      { path: 'drug-dict', name: 'DrugDict', component: () => import('@/views/drug/DrugDict.vue'), meta: { title: '药品字典' } },
      { path: 'drug-category', name: 'DrugCategory', component: () => import('@/views/drug/DrugCategory.vue'), meta: { title: '药品分类' } },
      { path: 'purchase-plan', name: 'PurchasePlan', component: () => import('@/views/purchase/PurchasePlan.vue'), meta: { title: '采购计划' } },
      { path: 'supplier', name: 'Supplier', component: () => import('@/views/common/Supplier.vue'), meta: { title: '供应商管理' } },
      { path: 'stock-in', name: 'StockIn', component: () => import('@/views/stock/StockIn.vue'), meta: { title: '入库管理' } },
      { path: 'stock-out', name: 'StockOut', component: () => import('@/views/stock/StockOut.vue'), meta: { title: '出库管理' } },
      { path: 'stock-list', name: 'StockList', component: () => import('@/views/stock/StockList.vue'), meta: { title: '库存查询' } },
      { path: 'inventory-task', name: 'InventoryTask', component: () => import('@/views/inventory/InventoryTask.vue'), meta: { title: '盘点任务' } },
      { path: 'expiry-alert', name: 'ExpiryAlert', component: () => import('@/views/expiry/ExpiryAlert.vue'), meta: { title: '近效期预警' } },
      { path: 'report-consumption', name: 'ReportConsumption', component: () => import('@/views/report/ConsumptionReport.vue'), meta: { title: '消耗量排行' } },
      { path: 'report-purchase', name: 'ReportPurchase', component: () => import('@/views/report/PurchaseReport.vue'), meta: { title: '采购金额分析' } },
      { path: 'report-turnover', name: 'ReportTurnover', component: () => import('@/views/report/TurnoverReport.vue'), meta: { title: '库存周转率' } },
      { path: 'department', name: 'Department', component: () => import('@/views/common/Department.vue'), meta: { title: '科室管理' } },
      { path: 'location', name: 'Location', component: () => import('@/views/common/Location.vue'), meta: { title: '货位管理' } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
