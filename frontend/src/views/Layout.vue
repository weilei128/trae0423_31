<template>
  <el-container class="layout-container">
    <el-aside width="220px" class="aside">
      <div class="logo">医药库存管理系统</div>
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/dashboard">
          <el-icon><House /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-sub-menu index="drug">
          <template #title>
            <el-icon><MedicineBox /></el-icon>
            <span>药品管理</span>
          </template>
          <el-menu-item index="/drug-dict">药品字典</el-menu-item>
          <el-menu-item index="/drug-category">药品分类</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="purchase">
          <template #title>
            <el-icon><ShoppingCart /></el-icon>
            <span>采购管理</span>
          </template>
          <el-menu-item index="/purchase-plan">采购计划</el-menu-item>
          <el-menu-item index="/supplier">供应商管理</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="stock">
          <template #title>
            <el-icon><Box /></el-icon>
            <span>库存管理</span>
          </template>
          <el-menu-item index="/stock-in">入库管理</el-menu-item>
          <el-menu-item index="/stock-out">出库管理</el-menu-item>
          <el-menu-item index="/stock-list">库存查询</el-menu-item>
        </el-sub-menu>
        <el-menu-item index="/inventory-task">
          <el-icon><DocumentChecked /></el-icon>
          <span>库存盘点</span>
        </el-menu-item>
        <el-menu-item index="/expiry-alert">
          <el-icon><Warning /></el-icon>
          <span>近效期预警</span>
        </el-menu-item>
        <el-sub-menu index="report">
          <template #title>
            <el-icon><DataAnalysis /></el-icon>
            <span>统计报表</span>
          </template>
          <el-menu-item index="/report-consumption">消耗量排行</el-menu-item>
          <el-menu-item index="/report-purchase">采购金额分析</el-menu-item>
          <el-menu-item index="/report-turnover">库存周转率</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="common">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>基础设置</span>
          </template>
          <el-menu-item index="/department">科室管理</el-menu-item>
          <el-menu-item index="/location">货位管理</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <span>{{ pageTitle }}</span>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const activeMenu = computed(() => route.path)
const pageTitle = computed(() => route.meta.title || '首页')
</script>

<style scoped>
.layout-container {
  height: 100vh;
}
.aside {
  background-color: #304156;
}
.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid #1f2d3d;
}
.header {
  background-color: #fff;
  border-bottom: 1px solid #e4e7ed;
  font-size: 18px;
  font-weight: bold;
  display: flex;
  align-items: center;
}
.main {
  background-color: #f5f7fa;
  padding: 20px;
}
</style>
