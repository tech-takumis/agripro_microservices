<template>
  <AuthenticatedLayout 
    :navigation="adminNavigation" 
    role-title="Admin Portal"
    page-title="Dashboard"
  >
  <template #header>
    <div class="flex items-center justify-between bg-gray-100 px-6 py-4 border-b border-gray-200 rounded-lg">
    
<!-- Left: Logo + Title -->
<div class="flex items-center space-x-3">
  <!-- Logo -->
  <img 
    src="@/assets/pcic-logo.png" 
    alt="PCIC Logo" 
    class="h-10 w-auto"
  />

  <!-- Title -->
  <div>
    <h1 class="text-2xl font-bold text-green-800">PCIC Dashboard</h1>
    <p class="text-sm text-black-600">
      Centralized management of users, applications, and system settings.
    </p>
  </div>
</div>
    <!-- Right: Search + Actions -->
    <div class="flex items-center gap-4">

      <!-- Notification Bell -->
      <button class="relative p-2 rounded-full hover:bg-green-100 transition">
        <svg
          class="w-6 h-6 text-gray-600"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
          viewBox="0 0 24 24"
        >
          <path stroke-linecap="round" stroke-linejoin="round"
            d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 
            00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 
            11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 
            3 0 11-6 0v-1m6 0H9" />
        </svg>
        <!-- Badge -->
        <span class="absolute top-1 right-1 block w-2 h-2 bg-red-500 rounded-full"></span>
      </button>

      <!-- Profile Dropdown -->
      <div class="relative">
        <button class="flex items-center gap-2 focus:outline-none">
          <img
            class="w-8 h-8 rounded-full border border-gray-200"
            src="https://ui-avatars.com/api/?name=Admin+User&background=16a34a&color=fff"
            alt="Profile"
          />
          <span class="hidden md:block text-sm font-medium text-gray-700">Admin User</span>
          <svg class="w-4 h-4 text-gray-500" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" d="M19 9l-7 7-7-7" />
          </svg>
        </button>

        <!-- Dropdown Menu -->
        <div
          class="absolute right-0 mt-2 w-48 bg-white border border-gray-200 rounded-lg shadow-lg py-2 hidden group-hover:block"
        >
          <a href="#" class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-50">Profile</a>
          <a href="#" class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-50">Settings</a>
          <div class="border-t my-1"></div>
          <a href="#" class="block px-4 py-2 text-sm text-red-600 hover:bg-red-50">Logout</a>
        </div>
      </div>
    </div>
  </div>
</template>
<!-- StatsCards -->
<div class="space-y-8">
  <div class="grid gap-4 grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4">
    <div
      v-for="card in [
        { 
          title: 'Total Staff Accounts', 
          value: totalStaffAccounts, 
          change: '+3 this week', 
          positive: true, 
          icon: Users, 
          color: 'text-blue-600',
          sparkline: [20, 25, 30, 28, 35, 40, 43] 
        },
        { 
          title: 'New Applications', 
          value: newApplicationsCreated, 
          change: '+10% this month', 
          positive: true, 
          icon: FileText,
          color: 'text-green-600',
          sparkline: [5, 10, 7, 12, 15, 18, 20] 
        },
        { 
          title: 'Pending Approvals', 
          value: pendingApprovals, 
          change: '-2% this week', 
          positive: false, 
          icon: ClipboardList,
          color: 'text-yellow-600',
          sparkline: [12, 14, 13, 15, 10, 8, 9] 
        },
        { 
          title: 'System Uptime', 
          value: '99.9%', 
          change: '+0.1%', 
          positive: true, 
          icon: Activity,
          color: 'text-red-600',
          sparkline: [98.5, 99.0, 99.5, 99.7, 99.8, 99.9, 99.9] 
        }
      ]"
      :key="card.title"
      class="bg-gray-100 rounded-xl border border-gray-200 p-4 shadow-sm flex flex-col justify-between"
    >
      <!-- Top: Icon + Title -->
      <div class="flex items-center gap-2 text-black-200 text-sm font-semibold mb-2">
        <component :is="card.icon":class="['w-5 h-5', card.color]"/>
        <span>{{ card.title }}</span>
      </div>

      <!-- Value -->
      <div class="flex items-end justify-between">
        <span class="text-2xl font-bold text-gray-900">{{ card.value }}</span>
        <span
          :class="[ 
            'text-xs flex items-center gap-1 font-medium',
            card.positive ? 'text-green-600' : 'text-red-600'
          ]"
        >
          <svg v-if="card.positive" class="w-4 h-4" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" d="M5 10l7-7 7 7" />
          </svg>
          <svg v-else class="w-4 h-4" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" d="M19 14l-7 7-7-7" />
          </svg>
          {{ card.change }}
        </span>
      </div>

      <!-- Sparkline Chart -->
      <div class="mt-3">
        <SparklineChart :data="card.sparkline" :positive="card.positive" />
      </div>
    </div>
  </div>
  
<!-- Recent Activity Logs -->
<div class="bg-gray-100 rounded-2xl shadow-md border border-gray-200 overflow-hidden">
  <div class="px-6 py-4 border-b border-gray-200 flex justify-between items-center">
    <h2 class="text-lg font-semibold text-green-900 flex items-center gap-2">
       Recent System Activity
    </h2>
    <router-link 
      to="/admin/settings/audit-logs"
      class="text-sm font-medium text-green-600 hover:text-green-700 transition"
    >
      View all logs →
    </router-link>
  </div>

  <div class="overflow-x-auto">
    <table class="min-w-full divide-y divide-gray-200 text-sm">
      <thead class="bg-gray-50">
        <tr>
          <th class="px-6 py-3 text-center font-medium text-gray-500 uppercase">
            <div class="flex items-center justify-center gap-2">
              <Calendar class="w-4 h-4 text-yellow-500" />
              Timestamp
            </div>
          </th>
          <th class="px-6 py-3 text-center font-medium text-gray-500 uppercase">
            <div class="flex items-center justify-center gap-2">
              <User class="w-4 h-4 text-blue-700" />
              User
            </div>
          </th>
          <th class="px-6 py-3 text-center font-medium text-gray-500 uppercase">
            <div class="flex items-center justify-center gap-2">
              <Activity class="w-4 h-4 text-red-500" />
              Action
            </div>
          </th>
          <th class="px-6 py-3 text-center font-medium text-gray-500 uppercase">
            <div class="flex items-center justify-center gap-2">
              <FileText class="w-4 h-4 text-green-600" />
              Details
            </div>
          </th>
        </tr>
      </thead>

      <tbody class="bg-white divide-y divide-gray-100">
        <tr 
          v-for="log in recentActivityLogs" 
          :key="log.id"
          class="transition cursor-pointer hover:bg-green-50 hover:text-green-700"
        >
          <!-- Timestamp -->
          <td class="px-6 py-4 text-center">{{ log.timestamp }}</td>

          <!-- User -->
          <td class="px-6 py-4 text-center">{{ log.user }}</td>
          
          <!-- Action -->
          <td class="px-6 py-4 text-center">{{ log.action }}</td>
          
          <!-- Details -->
          <td class="px-6 py-4 truncate text-black-600 text-center">{{ log.details }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</div>

<!-- Pending Approvals / Tasks -->
<div class="bg-gray-100 rounded-2xl shadow-md border border-gray-200 p-6">
  <!-- Header -->
  <h2 class="text-lg font-semibold text-green-900 mb-4 flex items-center gap-2">
    <svg class="w-5 h-5 text-yellow-500" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
      <path stroke-linecap="round" stroke-linejoin="round" d="M12 9v2m0 4h.01M21 12c0 4.97-4.03 9-9 9s-9-4.03-9-9 4.03-9 9-9 9 4.03 9 9z" />
    </svg>
    Pending Approvals
  </h2>

  <!-- Tasks List -->
  <ul class="space-y-3">
    <!-- Example Item -->
    <li
      v-for="task in pendingTasks"
      :key="task.id"
      class="flex items-center justify-between p-3 rounded-xl bg-gray-50 hover:bg-yellow-50 transition cursor-pointer"
    >
      <div class="flex items-center gap-2 text-gray-700">
        <svg v-if="task.type === 'policy'" class="w-4 h-4 text-yellow-600" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" d="M12 20h9" />
          <path stroke-linecap="round" stroke-linejoin="round" d="M12 4H3v16h18V8l-6-4z" />
        </svg>
        <svg v-else-if="task.type === 'staff'" class="w-4 h-4 text-blue-600" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" d="M5.121 17.804A9 9 0 1118.364 4.56M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
        </svg>
        <span class="font-medium">{{ task.label }}</span>
      </div>
      <span class="text-sm font-semibold text-yellow-700 bg-yellow-100 px-2 py-1 rounded-lg">
        {{ task.count }}
      </span>
    </li>
  </ul>

  <!-- View All -->
  <div class="mt-4 text-right">
    <router-link 
      to="/admin/approvals"
      class="text-sm font-medium text-green-600 hover:text-green-700 transition"
    >
      View all →
    </router-link>
  </div>
</div>
    </div>
  </AuthenticatedLayout>
</template>


<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { 
  Users, FileText, ClipboardList, Activity, UserPlus, Settings, BarChart3, Calendar, User
} from 'lucide-vue-next'
import AuthenticatedLayout from '../../layouts/AuthenticatedLayout.vue'
import UnderwriterQuickActionButton from '@/components/underwriter/UnderwriterQuickActionButton.vue' // Reusing for quick actions
import { useUserStore } from '@/stores/user'
import { ADMIN_NAVIGATION } from '@/lib/constants'

const store = useUserStore()
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
