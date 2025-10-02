<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Header -->
    <header class="bg-white shadow-sm border-b">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between items-center h-16">
          <div class="flex items-center">
            <img 
              src="https://hebbkx1anhila5yf.public.blob.vercel-storage.com/pcic.jpg-ymdsA0RBXJ1O58Wx4oDrmGSD8rRBY0.jpeg" 
              alt="PCIC Logo" 
              class="h-10 w-auto"
            />
            <div class="ml-4">
              <h1 class="text-xl font-semibold text-gray-900">PCIC Staff Portal</h1>
              <p class="text-sm text-gray-500">Multi-Role Access</p>
            </div>
          </div>
          <div class="flex items-center space-x-4">
            <span class="text-sm text-gray-700">{{ store.userFullName }}</span>
            <button
              @click="handleLogout"
              class="bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded-md text-sm font-medium transition-colors"
            >
              Logout
            </button>
          </div>
        </div>
      </div>
    </header>

    <!-- Main Content -->
    <main class="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
      <div class="px-4 py-6 sm:px-0">
        <!-- Welcome Section -->
        <div class="bg-white overflow-hidden shadow rounded-lg mb-6">
          <div class="px-4 py-5 sm:p-6">
            <h2 class="text-2xl font-bold text-gray-900 mb-2">
              Welcome, {{ store.userFullName }}!
            </h2>
            <p class="text-gray-600 mb-4">
              You have multiple roles in the system. Choose your preferred dashboard below.
            </p>
            
            <!-- User Roles Display -->
            <div class="mb-6">
              <h3 class="text-lg font-medium text-gray-900 mb-3">Your Roles:</h3>
              <div class="flex flex-wrap gap-2">
                <span 
                  v-for="role in store.userRoles" 
                  :key="role"
                  class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium"
                  :class="getRoleColor(role)"
                >
                  {{ formatRoleName(role) }}
                </span>
              </div>
            </div>

            <!-- User Info Grid -->
            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 text-sm">
              <div>
                <span class="font-medium text-gray-700">Department:</span>
                <span class="ml-2 text-gray-600">{{ store.userDepartment }}</span>
              </div>
              <div>
                <span class="font-medium text-gray-700">Position:</span>
                <span class="ml-2 text-gray-600">{{ store.userPosition }}</span>
              </div>
              <div>
                <span class="font-medium text-gray-700">Location:</span>
                <span class="ml-2 text-gray-600">{{ store.userLocation }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Role-Based Dashboard Options -->
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-6">
          <!-- Admin Dashboard -->
          <div v-if="store.isAdmin" class="bg-white overflow-hidden shadow rounded-lg hover:shadow-md transition-shadow cursor-pointer" @click="navigateTo('admin-dashboard')">
            <div class="p-6">
              <div class="flex items-center">
                <div class="flex-shrink-0">
                  <Settings class="h-8 w-8 text-purple-600" />
                </div>
                <div class="ml-4">
                  <h3 class="text-lg font-medium text-gray-900">Admin Dashboard</h3>
                  <p class="text-sm text-gray-500">System administration and agriculture management</p>
                </div>
              </div>
            </div>
          </div>

          <!-- Teller Dashboard -->
          <div v-if="store.isTeller" class="bg-white overflow-hidden shadow rounded-lg hover:shadow-md transition-shadow cursor-pointer" @click="navigateTo('teller-dashboard')">
            <div class="p-6">
              <div class="flex items-center">
                <div class="flex-shrink-0">
                  <DollarSign class="h-8 w-8 text-green-600" />
                </div>
                <div class="ml-4">
                  <h3 class="text-lg font-medium text-gray-900">Teller Dashboard</h3>
                  <p class="text-sm text-gray-500">Financial transactions and payments</p>
                </div>
              </div>
            </div>
          </div>

          <!-- Specialist Dashboard -->
          <div v-if="store.isSpecialist" class="bg-white overflow-hidden shadow rounded-lg hover:shadow-md transition-shadow cursor-pointer" @click="navigateTo('specialist-dashboard')">
            <div class="p-6">
              <div class="flex items-center">
                <div class="flex-shrink-0">
                  <FileText class="h-8 w-8 text-blue-600" />
                </div>
                <div class="ml-4">
                  <h3 class="text-lg font-medium text-gray-900">Specialist Dashboard</h3>
                  <p class="text-sm text-gray-500">Policy management and customer service</p>
                </div>
              </div>
            </div>
          </div>

          <!-- Underwriter Dashboard -->
          <div v-if="store.isUnderwriter" class="bg-white overflow-hidden shadow rounded-lg hover:shadow-md transition-shadow cursor-pointer" @click="navigateTo('underwriter-dashboard')">
            <div class="p-6">
              <div class="flex items-center">
                <div class="flex-shrink-0">
                  <Shield class="h-8 w-8 text-indigo-600" />
                </div>
                <div class="ml-4">
                  <h3 class="text-lg font-medium text-gray-900">Underwriter Dashboard</h3>
                  <p class="text-sm text-gray-500">Risk assessment and policy approval</p>
                </div>
              </div>
            </div>
          </div>

          <!-- Claims Processor Dashboard -->
          <div v-if="store.isClaimsProcessor" class="bg-white overflow-hidden shadow rounded-lg hover:shadow-md transition-shadow cursor-pointer" @click="navigateTo('claims-processor-dashboard')">
            <div class="p-6">
              <div class="flex items-center">
                <div class="flex-shrink-0">
                  <ClipboardList class="h-8 w-8 text-orange-600" />
                </div>
                <div class="ml-4">
                  <h3 class="text-lg font-medium text-gray-900">Claims Processor Dashboard</h3>
                  <p class="text-sm text-gray-500">Process and manage insurance claims</p>
                </div>
              </div>
            </div>
          </div>

          <!-- Adjuster Dashboard -->
          <div v-if="store.isAdjuster" class="bg-white overflow-hidden shadow rounded-lg hover:shadow-md transition-shadow cursor-pointer" @click="navigateTo('adjuster-dashboard')">
            <div class="p-6">
              <div class="flex items-center">
                <div class="flex-shrink-0">
                  <Search class="h-8 w-8 text-red-600" />
                </div>
                <div class="ml-4">
                  <h3 class="text-lg font-medium text-gray-900">Adjuster Dashboard</h3>
                  <p class="text-sm text-gray-500">Investigate and assess claims</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Combined Responsibilities -->
        <div class="bg-white overflow-hidden shadow rounded-lg">
          <div class="px-4 py-5 sm:p-6">
            <h3 class="text-lg font-medium text-gray-900 mb-4">Your Combined Responsibilities</h3>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <ul class="space-y-2">
                <li v-for="(responsibility, index) in responsibilities.slice(0, Math.ceil(responsibilities.length / 2))" :key="index" class="flex items-start">
                  <CheckCircle class="h-4 w-4 text-green-500 mt-0.5 mr-2 flex-shrink-0" />
                  <span class="text-sm text-gray-700">{{ responsibility }}</span>
                </li>
              </ul>
              <ul class="space-y-2">
                <li v-for="(responsibility, index) in responsibilities.slice(Math.ceil(responsibilities.length / 2))" :key="index" class="flex items-start">
                  <CheckCircle class="h-4 w-4 text-green-500 mt-0.5 mr-2 flex-shrink-0" />
                  <span class="text-sm text-gray-700">{{ responsibility }}</span>
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { 
  Settings, DollarSign, FileText, Shield, ClipboardList, Search, CheckCircle 
} from 'lucide-vue-next'
import { useUserStore } from '@/stores/agriculture'

const store = useUserStore()
const router = useRouter()

const responsibilities = computed(() => store.getAllResponsibilities())

const handleLogout = () => {
  store.logout()
}

const navigateTo = (routeName) => {
  router.push({ name: routeName })
}

const formatRoleName = (role) => {
  return role.replace(/_/g, ' ')
}

const getRoleColor = (role) => {
  const colors = {
    'Admin': 'bg-purple-100 text-purple-800',
    'Teller': 'bg-green-100 text-green-800',
    'Specialist': 'bg-blue-100 text-blue-800',
    'Underwriter': 'bg-indigo-100 text-indigo-800',
    'Division_Chief': 'bg-gray-100 text-gray-800',
    'Claims_Processor': 'bg-orange-100 text-orange-800',
    'Adjuster': 'bg-red-100 text-red-800'
  }
  return colors[role] || 'bg-gray-100 text-gray-800'
}
</script>