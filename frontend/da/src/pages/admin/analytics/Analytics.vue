<template>
  <AuthenticatedLayout 
    :navigation="adminNavigation" 
    role-title="Admin Portal"
    page-title="Analytics"
  >
    <template #header>
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-2xl font-bold text-gray-900">Analytics</h1>
          <p class="text-gray-600">System performance and usage analytics</p>
        </div>
        <div class="flex items-center space-x-2">
          <span class="text-sm text-gray-500">{{ currentDate }}</span>
        </div>
      </div>
    </template>

    <!-- Analytics Content -->
    <div class="space-y-6">
      <!-- Analytics Dashboard -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200">
        <div class="px-6 py-4 border-b border-gray-200">
          <h2 class="text-lg font-semibold text-gray-900">System Analytics</h2>
        </div>
        <div class="p-6">
          <div class="text-center py-12">
            <BarChart3 class="mx-auto h-12 w-12 text-gray-400" />
            <h3 class="mt-2 text-sm font-medium text-gray-900">Analytics Dashboard</h3>
            <p class="mt-1 text-sm text-gray-500">
              This page will display system analytics and performance metrics.
            </p>
          </div>
        </div>
      </div>
    </div>
  </AuthenticatedLayout>
</template>

<script setup>
import { computed } from 'vue'
import { BarChart3 } from 'lucide-vue-next'
import AuthenticatedLayout from '@/layouts/AuthenticatedLayout.vue'
import { ADMIN_NAVIGATION } from '@/lib/navigation'

const adminNavigation = ADMIN_NAVIGATION

const currentDate = computed(() => {
  return new Date().toLocaleDateString('en-US', {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
})
</script>
