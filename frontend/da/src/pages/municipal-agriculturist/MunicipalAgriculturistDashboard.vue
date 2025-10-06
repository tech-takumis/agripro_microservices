<template>
  <AuthenticatedLayout :navigation="navigation" :role-title="'Municipal Agriculturist'" :page-title="'Municipal Dashboard'">
<!-- Navbar -->
<nav class="w-full bg-transparent border-0 shadow-none px-6 py-3 mb-6 flex items-center justify-between">
      <h4 class="text-3xl font-bold text-black-700">Dashboard</h4>
      <!-- Right Side: Notifications + Profile -->
      <div class="flex items-center space-x-6">
        
        <!-- Notifications -->
        <div class="relative notifications-dropdown">
          <button
            @click="toggleNotificationsDropdown"
            class="relative p-2 text-gray-600 hover:text-gray-900 hover:bg-gray-50 rounded-lg transition-colors"
          >
            <Bell class="h-5 w-5" />
            <!-- Notification Badge -->
            <span
              class="absolute -top-1 -right-1 h-5 w-5 bg-red-500 text-white text-xs rounded-full flex items-center justify-center"
            >
              {{ notificationCount }}
            </span>
          </button>

          <!-- Notifications Dropdown -->
          <div
            v-show="showNotificationsDropdown"
            class="absolute right-0 mt-2 w-80 bg-white rounded-md shadow-lg border border-gray-200 z-50"
          >
            <div class="p-4 border-b border-gray-200 flex justify-between items-center">
              <h3 class="text-lg font-semibold text-gray-900">Notifications</h3>
              <button class="text-sm text-green-600 hover:text-green-700 font-medium">
                Mark all as read
              </button>
            </div>

            <div class="max-h-96 overflow-y-auto">
              <div
                v-for="notification in notifications"
                :key="notification.id"
                class="p-4 border-b border-gray-100 hover:bg-gray-50 cursor-pointer"
              >
                <div class="flex items-start space-x-3">
                  <div v-if="!notification.read" class="h-2 w-2 bg-red-500 rounded-full mt-2"></div>
                  <div class="flex-1">
                    <p class="text-sm font-medium text-gray-900">{{ notification.title }}</p>
                    <p class="text-sm text-gray-600">{{ notification.message }}</p>
                    <p class="text-xs text-gray-500">{{ notification.time }}</p>
                  </div>
                </div>
              </div>

              <!-- Empty State -->
              <div v-if="notifications.length === 0" class="p-8 text-center">
                <Bell class="h-12 w-12 text-gray-300 mx-auto mb-3" />
                <p class="text-sm text-gray-500">No new notifications</p>
              </div>
            </div>

            <div class="p-4 border-t border-gray-200">
              <button class="w-full text-center text-sm text-green-600 hover:text-green-700 font-medium">
                View all notifications
              </button>
            </div>
          </div>
        </div>

        <!-- Profile Dropdown -->
        <div class="relative profile-dropdown">
<!-- Profile Wrapper -->
<div class="relative group">
  <!-- Profile Button -->
  <button
    class="flex items-center space-x-3 p-2 rounded-lg hover:bg-gray-50 transition-colors"
  >
    <div class="h-8 w-8 rounded-full bg-green-500 flex items-center justify-center">
      <span class="text-sm font-medium text-white">{{ userInitials }}</span>
    </div>
    <div class="hidden md:block text-left">
      <p class="text-sm font-medium text-gray-900">{{ userFullName }}</p>
      <p class="text-xs text-gray-500">{{ userEmail }}</p>
    </div>
    <ChevronDown class="h-4 w-4 text-gray-400" />
  </button>

  <!-- Profile Dropdown Menu -->
  <div
    class="absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg border border-gray-200 z-50 opacity-0 pointer-events-none group-hover:opacity-100 group-hover:pointer-events-auto transition"
  >
    <div class="py-1">
      <button
        @click="handleProfileClick"
        class="flex items-center w-full px-4 py-2 text-sm text-gray-700 hover:bg-gray-50"
      >
        <User class="h-4 w-4 mr-3 text-gray-400" />
        View Profile
      </button>
      <button
        @click="handleSettingsClick"
        class="flex items-center w-full px-4 py-2 text-sm text-gray-700 hover:bg-gray-50"
      >
        <Settings class="h-4 w-4 mr-3 text-gray-400" />
        Settings
      </button>
      <button
        @click="handlePreferencesClick"
        class="flex items-center w-full px-4 py-2 text-sm text-gray-700 hover:bg-gray-50"
      >
        <Sliders class="h-4 w-4 mr-3 text-gray-400" />
        Preferences
      </button>
      <hr class="my-1 border-gray-200" />
      <button
        @click="handleLogoutClick"
        class="flex items-center w-full px-4 py-2 text-sm text-red-600 hover:bg-red-50"
      >
        <LogOut class="h-4 w-4 mr-3 text-red-400" />
        Logout
      </button>
    </div>
  </div>
</div>
        </div>
      </div>
    </nav>

    
    <div class="space-y-6">
      <!-- Stats Grid -->
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
        <!-- Local Farmers -->
        <div class="bg-white rounded-xl shadow-sm border border-gray-200 hover:shadow-md transition-shadow">
          <div class="p-5">
            <div class="flex items-center justify-between">
              <p class="text-2xs font-medium text-green-700">Local Farmers</p>
              <div
                :class="[
                  farmersChange > 0 ? 'text-green-700 bg-green-50' : farmersChange < 0 ? 'text-red-700 bg-red-50' : 'text-gray-700 bg-gray-50',
                  'inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium'
                ]"
              >
                <ArrowUpRight v-if="farmersChange > 0" class="h-3.5 w-3.5 mr-1" />
                <ArrowDownRight v-else-if="farmersChange < 0" class="h-3.5 w-3.5 mr-1" />
                {{ Math.abs(farmersChange).toFixed(1) }}%
              </div>
            </div>
            <div class="mt-2 flex items-center justify-between">
              <div class="p-2 rounded-lg bg-green-50">
                <Sprout class="h-6 w-6 text-green-600" />
              </div>
              <p class="text-3xl font-semibold text-gray-900">{{ stats.localFarmers.toLocaleString() }}</p>
            </div>
            
          </div>
        </div>

        <!-- Disbursements -->
        <div class="bg-white rounded-xl shadow-sm border border-gray-200 hover:shadow-md transition-shadow">
          <div class="p-5">
            <div class="flex items-center justify-between">
              <p class="text-2xs font-medium text-green-700">Disbursements</p>
              <div
                :class="[
                  disbursementsChange > 0 ? 'text-green-700 bg-green-50' : disbursementsChange < 0 ? 'text-red-700 bg-red-50' : 'text-gray-700 bg-gray-50',
                  'inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium'
                ]"
              >
                <ArrowUpRight v-if="disbursementsChange > 0" class="h-3.5 w-3.5 mr-1" />
                <ArrowDownRight v-else-if="disbursementsChange < 0" class="h-3.5 w-3.5 mr-1" />
                {{ Math.abs(disbursementsChange).toFixed(1) }}%
              </div>
            </div>
            <div class="mt-2 flex items-center justify-between">
              <div class="p-2 rounded-lg bg-green-50">
                <TrendingUp class="h-6 w-6 text-green-600" />
              </div>
              <p class="text-3xl font-semibold text-gray-900">₱{{ stats.disbursements.toLocaleString() }}K</p>
            </div>
            
          </div>
        </div>

        <!-- Active Programs -->
        <div class="bg-green-600 rounded-xl shadow-sm border border-gray-200 hover:shadow-md transition-shadow">
          <div class="p-5">
            <div class="flex items-center justify-between">
              <p class="text-2xs font-medium text-white">Active Programs</p>
              <div
                :class="[
                  programsChange > 0 ? 'text-green-700 bg-green-50' : programsChange < 0 ? 'text-red-700 bg-red-50' : 'text-gray-700 bg-gray-50',
                  'inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium'
                ]"
              >
                <ArrowUpRight v-if="programsChange > 0" class="h-3.5 w-3.5 mr-1" />
                <ArrowDownRight v-else-if="programsChange < 0" class="h-3.5 w-3.5 mr-1" />
                {{ Math.abs(programsChange).toFixed(1) }}%
              </div>
            </div>
            <div class="mt-2 flex items-center justify-between">
              <div class="p-2 rounded-lg bg-green-600">
                <Users class="h-7 w-7 text-green-900 bg-green-600" />
              </div>
              <p class="text-3xl font-semibold text-white">{{ stats.activePrograms }}</p>
            </div>
            
          </div>
        </div>
      </div>

<!-- Financial Management + Municipal Programs -->
<div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
  <!-- Financial Transactions Card -->
  <div class="bg-white p-6 rounded-2xl shadow-md border border-gray-100">
    <div class="flex items-center justify-between mb-6">
      <div class="flex items-center space-x-3">
        <div class="p-2 bg-red-50 rounded-lg">
          <CreditCard class="h-6 w-6 text-red-600" />
        </div>
        <h3 class="text-lg font-semibold text-red-600">Financial Transactions</h3>
      </div>
      <PermissionGuard :permissions="['CAN_MANAGE_FINANCE']">
        <button
          class="flex items-center bg-blue-600 hover:bg-blue-700 text-white px-3 py-1.5 rounded-md text-sm transition-colors"
        >
          <Plus class="h-4 w-4 mr-1" />
          New
        </button>
      </PermissionGuard>
    </div>

    <div class="space-y-4">
      <div
        v-for="transaction in recentTransactions"
        :key="transaction.id"
        class="flex items-center justify-between border border-black-100 rounded-lg p-4 hover:bg-green-50 transition"
      >
        <div class="flex items-center space-x-3">
          <div
            class="p-2 rounded-full"
            :class="transaction.type === 'income' ? 'bg-green-100' : 'bg-red-100'"
          >
            <ArrowDownCircle v-if="transaction.type === 'income'" class="h-5 w-5 text-green-600" />
            <ArrowUpCircle v-else class="h-5 w-5 text-red-600" />
          </div>
          <div>
            <p class="font-medium text-gray-900">{{ transaction.description }}</p>
            <p class="text-xs text-gray-500">{{ transaction.date }} - {{ transaction.category }}</p>
          </div>
        </div>
        <div class="text-right">
          <p
            class="text-lg font-bold"
            :class="transaction.type === 'income' ? 'text-green-600' : 'text-red-600'"
          >
            {{ transaction.type === 'income' ? '+' : '-' }}₱{{ transaction.amount.toLocaleString() }}
          </p>
          <span
            class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
            :class="getTransactionStatusClass(transaction.status)"
          >
            {{ transaction.status }}
          </span>
        </div>
      </div>
    </div>
  </div>

  <!-- Municipal Programs Card -->
  <div class="bg-white p-6 rounded-2xl shadow-md border border-gray-100">
    <div class="flex items-center space-x-3 mb-6">
      <div class="p-2 bg-green-50 rounded-lg">
        <Briefcase class="h-6 w-6 text-green-600" />
      </div>
      <h3 class="text-lg font-semibold text-green-700">Municipal Programs</h3>
    </div>

    <div class="space-y-4">
      <div
        v-for="program in municipalPrograms"
        :key="program.id"
        class="border border-black-100 rounded-lg p-4 hover:bg-green-50 transition"
      >
        <div class="flex items-center justify-between mb-2">
          <div class="flex items-center space-x-2">
            <ClipboardList class="h-5 w-5 text-gray-500" />
            <h4 class="font-medium text-gray-900">{{ program.name }}</h4>
          </div>
          <PermissionGuard :permissions="['CAN_MANAGE_ROLES']">
            <button
              class="flex items-center bg-gray-600 hover:bg-gray-700 text-white px-3 py-1 rounded-md text-xs transition-colors"
            >
              <Settings class="h-4 w-4 mr-1" />
              Manage
            </button>
          </PermissionGuard>
        </div>

        <div class="grid grid-cols-2 gap-4 text-sm mb-2">
          <div class="flex items-center space-x-2">
            <Users class="h-4 w-4 text-green-600" />
            <p class="font-semibold">{{ program.beneficiaries }}</p>
            <span class="text-gray-500">beneficiaries</span>
          </div>
          <div class="flex items-center space-x-2">
            <Wallet class="h-4 w-4 text-yellow-600" />
            <p class="font-semibold">₱{{ program.budget.toLocaleString() }}K</p>
            <span class="text-gray-500">budget</span>
          </div>
        </div>

        <div class="mt-2">
          <div class="w-full bg-gray-200 rounded-full h-2">
            <div class="bg-green-600 h-2 rounded-full" :style="{ width: program.progress + '%' }"></div>
          </div>
          <p class="text-xs text-green-600 mt-1">{{ program.progress }}% Complete</p>
        </div>
      </div>
    </div>
  </div>
</div>
    </div>
  </AuthenticatedLayout>
</template>

<script setup>
import { ref, onMounted, computed, onUnmounted } from 'vue'
import { Sprout, CreditCard, TrendingUp, Users, ChevronDown, User, Settings, Sliders, LogOut,ArrowUpCircle, ArrowDownCircle,ClipboardList,Briefcase, Bell, ArrowUpRight, ArrowDownRight } from 'lucide-vue-next'
import AuthenticatedLayout from '../../layouts/AuthenticatedLayout.vue'
import PermissionGuard from '../../components/others/PermissionGuard.vue'
import { MUNICIPAL_AGRICULTURIST_NAVIGATION } from '../../lib/constants.js'
import { useAuthStore } from '../../stores/auth'

const navigation = ref(MUNICIPAL_AGRICULTURIST_NAVIGATION)
const authStore = useAuthStore()

// Profile dropdown state
const showProfileDropdown = ref(false)

// Filters dropdown state
const showFiltersDropdown = ref(false)

// Notifications dropdown state
const showNotificationsDropdown = ref(false)
const notificationCount = ref(3)

// User data from auth store
const userFullName = computed(() => authStore.user?.fullName || 'Municipal Agriculturist')
const userEmail = computed(() => authStore.user?.email || 'municipal@agripro.com')
const userInitials = computed(() => {
  const name = userFullName.value
  return name.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2)
})

const stats = ref({
  localFarmers: 0,
  municipalBudget: 0,
  disbursements: 0,
  activePrograms: 0
})

const recentTransactions = ref([])
const municipalPrograms = ref([])

// Notifications data
const notifications = ref([
  {
    id: 1,
    title: 'New Claim Submitted',
    message: 'Farmer Juan Dela Cruz submitted a new insurance claim for rice crop damage',
    time: '2 minutes ago',
    read: false
  },
  {
    id: 2,
    title: 'Program Update',
    message: 'Seed Distribution Program has reached 75% completion',
    time: '1 hour ago',
    read: false
  },
  {
    id: 3,
    title: 'Budget Alert',
    message: 'Fertilizer Subsidy budget is running low (15% remaining)',
    time: '3 hours ago',
    read: true
  }
])

const getTransactionStatusClass = (status) => {
  switch (status) {
    case 'Completed':
      return 'bg-green-100 text-green-800'
    case 'Pending':
      return 'bg-yellow-100 text-yellow-800'
    case 'Processing':
      return 'bg-blue-100 text-blue-800'
    default:
      return 'bg-gray-100 text-gray-800'
  }
}

// Profile dropdown methods
const toggleProfileDropdown = () => {
  showProfileDropdown.value = !showProfileDropdown.value
}

// Filters dropdown methods
const toggleFiltersDropdown = () => {
  showFiltersDropdown.value = !showFiltersDropdown.value
  // Close notifications when opening filters
  if (showFiltersDropdown.value) {
    showNotificationsDropdown.value = false
  }
}

// Notifications dropdown methods
const toggleNotificationsDropdown = () => {
  showNotificationsDropdown.value = !showNotificationsDropdown.value
  // Close filters when opening notifications
  if (showNotificationsDropdown.value) {
    showFiltersDropdown.value = false
  }
}

const handleProfileClick = () => {
  showProfileDropdown.value = false
  // Navigate to profile page or show profile modal
  console.log('Navigate to profile page')
}

const handleSettingsClick = () => {
  showProfileDropdown.value = false
  // Navigate to settings page
  console.log('Navigate to settings page')
}

const handlePreferencesClick = () => {
  showProfileDropdown.value = false
  // Navigate to preferences page
  console.log('Navigate to preferences page')
}

const handleLogoutClick = () => {
  showProfileDropdown.value = false
  // Handle logout
  authStore.logout()
}

// Click outside handler for dropdowns
const handleClickOutside = (event) => {
  const profileDropdown = event.target.closest('.profile-dropdown')
  const filtersDropdown = event.target.closest('.filters-dropdown')
  const notificationsDropdown = event.target.closest('.notifications-dropdown')
  
  if (!profileDropdown && showProfileDropdown.value) {
    showProfileDropdown.value = false
  }
  if (!filtersDropdown && showFiltersDropdown.value) {
    showFiltersDropdown.value = false
  }
  if (!notificationsDropdown && showNotificationsDropdown.value) {
    showNotificationsDropdown.value = false
  }
}

onMounted(async () => {
  // Add click outside listener
  document.addEventListener('click', handleClickOutside)
  
  // Load dashboard data
  try {
    // Mock data - replace with actual API calls
    stats.value = {
      localFarmers: 1250,
      municipalBudget: 2500,
      disbursements: 1800,
      activePrograms: 6
    }

    recentTransactions.value = [
      {
        id: 1,
        description: 'Farmer Subsidy Payment',
        date: '2024-01-15',
        category: 'Subsidy',
        amount: 150000,
        type: 'expense',
        status: 'Completed'
      },
      {
        id: 2,
        description: 'Provincial Budget Allocation',
        date: '2024-01-14',
        category: 'Budget',
        amount: 500000,
        type: 'income',
        status: 'Completed'
      }
    ]

    municipalPrograms.value = [
      {
        id: 1,
        name: 'Seed Distribution Program',
        beneficiaries: 450,
        budget: 300,
        progress: 75
      },
      {
        id: 2,
        name: 'Fertilizer Subsidy',
        beneficiaries: 320,
        budget: 250,
        progress: 60
      }
    ]
  } catch (error) {
    console.error('Failed to load dashboard data:', error)
  }
})

onUnmounted(() => {
  // Remove click outside listener
  document.removeEventListener('click', handleClickOutside)
})

// ----- Trends & Sparklines -----
// Example trend data for the last 12 periods (e.g., weeks)
const farmersTrend = ref([900, 920, 915, 930, 945, 950, 960, 980, 990, 1005, 1010, 1025])
const disbursementsTrend = ref([1200, 1180, 1210, 1225, 1230, 1240, 1255, 1260, 1275, 1280, 1290, 1300])
const programsTrend = ref([4, 4, 5, 5, 5, 6, 6, 6, 6, 6, 6, 6])

const computePercentChange = (arr) => {
  if (!arr || arr.length < 2) return 0
  const prev = arr[arr.length - 2]
  const curr = arr[arr.length - 1]
  if (prev === 0) return 0
  return ((curr - prev) / prev) * 100
}

const farmersChange = computed(() => computePercentChange(farmersTrend.value))
const disbursementsChange = computed(() => computePercentChange(disbursementsTrend.value))
const programsChange = computed(() => computePercentChange(programsTrend.value))

// Generate polyline points for a 120x32 viewBox sparkline
const generateSparklinePoints = (arr) => {
  if (!arr || arr.length === 0) return ''
  const width = 120
  const height = 32
  const max = Math.max(...arr)
  const min = Math.min(...arr)
  const range = max - min || 1
  const stepX = width / Math.max(1, arr.length - 1)
  const points = arr.map((v, i) => {
    const x = Math.round(i * stepX)
    const y = Math.round(height - ((v - min) / range) * height)
    return `${x},${y}`
  })
  return points.join(' ')
}
</script>