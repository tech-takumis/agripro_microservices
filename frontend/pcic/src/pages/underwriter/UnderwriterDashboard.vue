<template>
  <AuthenticatedLayout 
    :navigation="underwriterNavigation" 
    role-title="Underwriter Portal"
    page-title="Dashboard"
  >
<template #header>
  <div class="flex items-center justify-between w-full">
    <!-- Left: Logo + Title -->
    <div class="flex items-center space-x-3">
      <div>
        <h1 class="text-2xl font-bold text-black">Underwriter Dashboard</h1>
        <p class="text-sm text-gray-600">
          Evaluate risks and manage insurance applications for rice farmers.
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
      <div class="relative group">
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


    <!-- Dashboard Content -->
    <div class="space-y-6">
     <!-- Underwriter StatsCards -->
<div class="space-y-8">
  <div class="grid gap-4 grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4">
    <!-- Applications Pending -->
    <div
      class="bg-yellow-100 rounded-xl border border-gray-200 p-4 shadow-sm flex flex-col justify-between"
    >
      <!-- Top: Icon + Title -->
      <div class="flex items-center gap-2 text-black-200 text-sm font-semibold mb-2">
        <ClipboardList class="w-5 h-5 text-yellow-600" />
        <span>Applications Pending</span>
      </div>

      <!-- Value + Change -->
      <div class="flex items-end justify-between">
        <span class="text-2xl font-bold text-gray-900">{{ pendingApplications }}</span>
        <span class="text-xs flex items-center gap-1 font-medium text-indigo-600">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" d="M5 10l7-7 7 7" />
          </svg>
          +5 new
        </span>
      </div>

      <!-- Sparkline -->
      <div class="mt-3">
        <SparklineChart :data="[5, 7, 10, 9, 12, 14, 15]" :positive="true" />
      </div>
    </div>

    <!-- Applications Approved -->
    <div
      class="bg-green-100 rounded-xl border border-gray-200 p-4 shadow-sm flex flex-col justify-between"
    >
      <div class="flex items-center gap-2 text-black-200 text-sm font-semibold mb-2">
        <CheckCircle class="w-5 h-5 text-green-600" />
        <span>Applications Approved</span>
      </div>

      <div class="flex items-end justify-between">
        <span class="text-2xl font-bold text-gray-900">{{ approvedApplications }}</span>
        <span class="text-xs flex items-center gap-1 font-medium text-green-600">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" d="M5 10l7-7 7 7" />
          </svg>
          +15%
        </span>
      </div>

      <div class="mt-3">
        <SparklineChart :data="[10, 12, 14, 13, 15, 17, 20]" :positive="true" />
      </div>
    </div>

    <!-- Applications Rejected -->
    <div
      class="bg-red-100 rounded-xl border border-gray-200 p-4 shadow-sm flex flex-col justify-between"
    >
      <div class="flex items-center gap-2 text-black-200 text-sm font-semibold mb-2">
        <XCircle class="w-5 h-5 text-red-600" />
        <span>Applications Rejected</span>
      </div>

      <div class="flex items-end justify-between">
        <span class="text-2xl font-bold text-gray-900">{{ rejectedApplications }}</span>
        <span class="text-xs flex items-center gap-1 font-medium text-red-600">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" d="M19 14l-7 7-7-7" />
          </svg>
          -2%
        </span>
      </div>

      <div class="mt-3">
        <SparklineChart :data="[8, 7, 6, 9, 5, 4, 6]" :positive="false" />
      </div>
    </div>

    <!-- Average Processing Time -->
    <div
      class="bg-blue-100 rounded-xl border border-gray-200 p-4 shadow-sm flex flex-col justify-between"
    >
      <div class="flex items-center gap-2 text-black-200 text-sm font-semibold mb-2">
        <Clock class="w-5 h-5 text-blue-600" />
        <span>Average Processing Time</span>
      </div>

      <div class="flex items-end justify-between">
        <span class="text-2xl font-bold text-gray-900">2.5 days</span>
        <span class="text-xs flex items-center gap-1 font-medium text-blue-600">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" d="M19 14l-7 7-7-7" />
          </svg>
          -0.3 days
        </span>
      </div>

      <div class="mt-3">
        <SparklineChart :data="[3.0, 2.8, 2.9, 2.7, 2.6, 2.5, 2.5]" :positive="false" />
      </div>
    </div>
  </div>
</div>

<!-- Recent Applications for Review -->
<div class="bg-white rounded-lg shadow-sm border border-gray-200">
  <!-- Header -->
  <div class="px-6 py-4 border-b border-gray-200">
    <div class="flex items-center justify-between">
      <!-- Left: Title with Icon -->
      <div class="flex items-center gap-2">
        <ClipboardList class="w-5 h-5 text-green-700" />
        <h2 class="text-lg font-semibold text-gray-900">
          Recent Applications for Review
        </h2>
      </div>
<!-- Right: Dropdown Filter + Delete Action -->
<div class="flex items-center gap-3" ref="filterRef">
  <!-- Action buttons (show only if checkbox selected) -->
  <div v-if="selectedApps.length > 0" class="flex items-center gap-2">
    <!-- Delete box button -->
    <button
      @click="deleteSelected"
      class="flex items-center justify-center w-9 h-9 rounded-md bg-red-50 text-red-600 border border-red-200 hover:bg-red-100 transition"
      title="Delete selected"
    >
      <Trash2 class="w-5 h-5" />
    </button>

    <!-- View box button (blue eye icon only) -->
    <button
      @click="viewSelected"
      class="flex items-center justify-center w-9 h-9 rounded-md bg-blue-50 text-blue-600 border border-blue-200 hover:bg-blue-100 transition"
      title="View selected"
    >
      <Eye class="w-5 h-5" />
    </button>
  </div>

  <!-- Filter dropdown -->
  <div class="relative">
    <button
      @click="showFilter = !showFilter"
      class="flex items-center gap-3 px-3 py-2 text-sm font-medium text-gray-700 bg-white rounded-lg shadow-sm border border-gray-300 hover:bg-gray-100 transition"
    >
      <Filter class="w-4 h-4 text-gray-600" />
      <span>{{ filterStatus || 'Filter' }}</span>
      <svg
        class="w-4 h-4 text-gray-600"
        fill="none"
        stroke="currentColor"
        stroke-width="2"
        viewBox="0 0 24 24"
      >
        <path stroke-linecap="round" stroke-linejoin="round" d="M19 9l-7 7-7-7" />
      </svg>
    </button>

    <!-- Dropdown Menu -->
    <div
      v-if="showFilter"
      class="absolute right-0 mt-2 w-44 bg-white border border-gray-200 rounded-lg shadow-lg py-2 z-10"
    >
      <button
        @click="applyFilter('Approved')"
        class="flex items-center w-full px-4 py-2 text-sm text-green-700 hover:bg-green-50"
      >
        <CheckCircle class="w-4 h-4 mr-2 text-green-600" />
        Approved
      </button>
      <button
        @click="applyFilter('Pending')"
        class="flex items-center w-full px-4 py-2 text-sm text-yellow-700 hover:bg-yellow-50"
      >
        <ClipboardList class="w-4 h-4 mr-2 text-yellow-600" />
        Pending
      </button>
      <button
        @click="applyFilter('Rejected')"
        class="flex items-center w-full px-4 py-2 text-sm text-red-700 hover:bg-red-50"
      >
        <XCircle class="w-4 h-4 mr-2 text-red-600" />
        Rejected
      </button>
    </div>
  </div>
</div>


    </div>
  </div>

  <!-- Table -->
  <div class="overflow-x-auto">
    <table class="min-w-full divide-y divide-gray-200">
      <thead class="bg-white">
        <tr>
          <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
            Application ID
          </th>
          <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
            Farmer Name
          </th>
          <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
            Crop Type
          </th>
          <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
            Location
          </th>
          <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
            Status
          </th>
          <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
            Applied Date
          </th>
        </tr>
      </thead>
      <tbody class="bg-white divide-y divide-gray-200">
        <tr 
  v-for="app in filteredApplications" 
  :key="app.id"
  class="transition cursor-pointer"
  :class="[
    selectedApps.includes(app.id) 
      ? 'bg-green-50 text-green-700' 
      : 'hover:bg-green-50 hover:text-green-700'
  ]"
>
  <!-- Application ID with checkbox -->
  <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
    <input 
      type="checkbox" 
      class="mr-2 rounded border-gray-300 accent-green-600 transition-colors duration-200 ease-in-out"
      v-model="selectedApps" 
      :value="app.id" 
    /> 
    {{ app.id }}
  </td>

  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
    {{ app.farmerName }}
  </td>
  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
    {{ app.cropType }}
  </td>
  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
    {{ app.location }}
  </td>
  <td class="px-6 py-4 whitespace-nowrap">
    <span :class="[ 
      'inline-flex px-2 py-1 text-xs font-semibold rounded-full',
      app.status === 'Pending' ? 'bg-yellow-100 text-yellow-800' :
      app.status === 'Approved' ? 'bg-green-100 text-green-800' :
      'bg-red-100 text-red-800'
    ]">
      {{ app.status }}
    </span>
  </td>
  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
    {{ app.appliedDate }}
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
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { CheckCircle, ClipboardList, Eye, Filter, Trash2, XCircle } from 'lucide-vue-next'

import AuthenticatedLayout from '../../layouts/AuthenticatedLayout.vue'
import SparklineChart from '@/components/SparklineChart.vue'
import Clock from '@/components/Clock.vue'

import { useAuthStore } from '@/stores/auth'
import { UNDERWRITER_NAVIGATION } from '@/lib/navigation'

const store = useAuthStore()
const router = useRouter()
const underwriterNavigation = UNDERWRITER_NAVIGATION

// Sample data for stats
const pendingApplications = ref(12)
const approvedApplications = ref(85)
const rejectedApplications = ref(5)

// Filter state
const showFilter = ref(false)
const filterStatus = ref(null) // "Approved", "Pending", "Rejected" or null
const selectedApps = ref([])

// Dropdown ref for click-outside handling
const filterRef = ref(null)

const viewSelected = () => {
  console.log("Viewing apps:", selectedApps)
  // Add your view logic here (e.g., route or modal)
}


const deleteSelected = () => {
  recentApplications.value = recentApplications.value.filter(
    app => !selectedApps.value.includes(app.id)
  )
  selectedApps.value = [] // clear selection after delete
}


// Toggle dropdown
const toggleFilter = () => {
  showFilter.value = !showFilter.value
}

// Apply filter and close dropdown
const applyFilter = (status) => {
  filterStatus.value = status
  showFilter.value = false
}

// Click-outside handler
const onClickOutside = (e) => {
  if (!filterRef.value) return
  if (!filterRef.value.contains(e.target)) {
    showFilter.value = false
  }
}
onMounted(() => {
  window.addEventListener('click', onClickOutside)
})
onBeforeUnmount(() => {
  window.removeEventListener('click', onClickOutside)
})

// Sample data for recent applications
const recentApplications = ref([
  {
    id: 'APP-001',
    farmerName: 'Mang Tonyo',
    cropType: 'Rice (IR-64)',
    location: 'Nueva Ecija',
    status: 'Pending',
    appliedDate: '2024-07-10'
  },
  {
    id: 'APP-002',
    farmerName: 'Aling Nena',
    cropType: 'Rice (NSIC Rc 222)',
    location: 'Isabela',
    status: 'Pending',
    appliedDate: '2024-07-09'
  },
  {
    id: 'APP-003',
    farmerName: 'Ka Pedro',
    cropType: 'Rice (Hybrid)',
    location: 'Pangasinan',
    status: 'Pending',
    appliedDate: '2024-07-08'
  },
  {
    id: 'APP-004',
    farmerName: 'Luzviminda Reyes',
    cropType: 'Rice (IR-64)',
    location: 'Tarlac',
    status: 'Approved',
    appliedDate: '2024-07-07'
  },
  {
    id: 'APP-005',
    farmerName: 'Benito Cruz',
    cropType: 'Rice (NSIC Rc 160)',
    location: 'Bulacan',
    status: 'Rejected',
    appliedDate: '2024-07-06'
  }
])

// Computed: apply filter
const filteredApplications = computed(() => {
  if (!filterStatus.value) return recentApplications.value
  return recentApplications.value.filter(app => app.status === filterStatus.value)
})

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

const viewApplication = (appId) => {
  console.log('Viewing application:', appId)
  router.push(`/underwriter/applications/${appId}/review`)
}
</script>
