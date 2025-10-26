<template>
  <AuthenticatedLayout 
    :navigation="adminNavigation" 
    role-title="Admin Portal"
    page-title="Dashboard"
  >
    <template #header>
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-2xl font-bold text-gray-900">Admin Dashboard</h1>
          <p class="text-gray-600">Manage users, application types, and system settings</p>
        </div>
        <div class="flex items-center space-x-2">
          <span class="text-sm text-gray-500">{{ currentDate }}</span>
          <div class="h-4 w-px bg-gray-300"></div>
          <span class="text-sm font-medium text-purple-600">{{ store.userLocation }}</span>
        </div>
      </div>
    </template>

    <!-- Dashboard Content -->
    <div class="space-y-6">
      <!-- Admin Stats Cards -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <ApplicationCard
          title="Total Staff Accounts"
          description="Active users in the system"
          :value="totalStaffAccounts"
          change="+3 this week"
          :icon="Users"
          variant="primary"
        />
        <ApplicationCard
          title="New Applications Created"
          description="This month"
          :value="newApplicationsCreated"
          change="+10%"
          :icon="FileText"
          variant="info"
        />
        <ApplicationCard
          title="Pending Approvals"
          description="System-level approvals"
          :value="pendingApprovals"
          change="2 critical"
          :icon="ClipboardList"
          variant="warning"
        />
        <ApplicationCard
          title="System Uptime"
          description="Last 30 days"
          value="99.9%"
          change="+0.1%"
          :icon="Activity"
          variant="success"
        />
      </div>

      <!-- Quick Actions -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <h2 class="text-lg font-semibold text-gray-900 mb-4">Quick Actions</h2>
        <div class="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-6 gap-4">
          <UnderwriterQuickActionButton
            title="Register New Staff"
            description="Add a new user account"
            :icon="UserPlus"
            variant="primary"
            @click="navigateTo('/admin/staff/register')"
          />
          <UnderwriterQuickActionButton
            title="Create App Type"
            description="Define new insurance product"
            :icon="FileText"
            variant="secondary"
            @click="navigateTo('/admin/applications/new')"
          />
          <UnderwriterQuickActionButton
            title="Manage Staff"
            description="Edit user roles & access"
            :icon="Users"
            variant="default"
            @click="navigateTo('/admin/staff/manage')"
          />
          <UnderwriterQuickActionButton
            title="View Audit Logs"
            description="Monitor system activity"
            :icon="ClipboardList"
            variant="default"
            @click="navigateTo('/admin/settings/audit-logs')"
          />
          <UnderwriterQuickActionButton
            title="System Settings"
            description="Configure application"
            :icon="Settings"
            variant="default"
            @click="navigateTo('/admin/settings/general')"
          />
          <UnderwriterQuickActionButton
            title="Generate Reports"
            description="Access admin reports"
            :icon="BarChart3"
            variant="default"
            @click="navigateTo('/admin/reports/overall')"
          />
        </div>
      </div>

      <!-- Recent System Activity / Audit Logs -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200">
        <div class="px-6 py-4 border-b border-gray-200">
          <div class="flex items-center justify-between">
            <h2 class="text-lg font-semibold text-gray-900">Recent System Activity</h2>
            <router-link 
              to="/admin/settings/audit-logs"
              class="text-sm text-purple-600 hover:text-purple-700 font-medium"
            >
              View all logs
            </router-link>
          </div>
        </div>
        <div class="overflow-x-auto">
          <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Timestamp
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  User
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Action
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Details
                </th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              <tr v-for="log in recentActivityLogs" :key="log.id">
                <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                  {{ log.timestamp }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                  {{ log.user }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                  {{ log.action }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                  {{ log.details }}
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </AuthenticatedLayout>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { Activity, BarChart3, ClipboardList, FileText, Settings, UserPlus, Users } from 'lucide-vue-next'
import AuthenticatedLayout from '../../layouts/AuthenticatedLayout.vue'
import ApplicationCard from '@/components/underwriter/ApplicationCard.vue'
import UnderwriterQuickActionButton from '@/components/underwriter/UnderwriterQuickActionButton.vue'
import { useAuthStore } from '@/stores/auth'
import { ADMIN_NAVIGATION } from '@/lib/navigation'

const store = useAuthStore()
const router = useRouter()

const adminNavigation = ADMIN_NAVIGATION

// Sample data for stats
const totalStaffAccounts = ref(45)
const newApplicationsCreated = ref(120)
const pendingApprovals = ref(7)

// Sample data for recent activity logs
const recentActivityLogs = ref([
  {
    id: 1,
    timestamp: '2024-07-18 14:30',
    user: 'Admin User',
    action: 'Created Application Type',
    details: 'Rice Crop Insurance V2'
  },
  {
    id: 2,
    timestamp: '2024-07-18 10:15',
    user: 'Underwriter A',
    action: 'Approved Policy',
    details: 'Policy #PCIC-2024-005'
  },
  {
    id: 3,
    timestamp: '2024-07-17 16:00',
    user: 'Admin User',
    action: 'Registered New Staff',
    details: 'John Doe (Teller)'
  },
  {
    id: 4,
    timestamp: '2024-07-17 09:00',
    user: 'Claims Processor B',
    action: 'Processed Claim',
    details: 'Claim #CLM-2024-012'
  }
])

const currentDate = computed(() => {
  return new Date().toLocaleDateString('en-US', {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
})

const navigateTo = (path) => {
  router.push(path)
}
</script>
