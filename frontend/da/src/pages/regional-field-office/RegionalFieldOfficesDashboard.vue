<template>
    <AuthLayout 
      :navigation="navigation" 
      :role-title="'Regional Field Office'"
      :page-title="'Regional Dashboard'"
    >
      <template #header>
        <div class="flex items-center justify-between">
          <div>
            <h1 class="text-2xl font-bold text-gray-900">Regional Field Offices Dashboard</h1>
            <p class="text-gray-600">Monitor and approve insurance claims in your region</p>
          </div>
          <div class="flex items-center space-x-2">
            <div class="px-3 py-1 bg-blue-100 text-blue-800 rounded-full text-sm font-medium">
              Regional Field Office
            </div>
          </div>
        </div>
      </template>
  
      <div class="space-y-6">
        <!-- Stats Grid -->
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div class="bg-white p-6 rounded-lg shadow-sm border border-gray-200">
            <div class="flex items-center">
              <div class="p-2 bg-yellow-100 rounded-lg">
                <Clock class="h-6 w-6 text-yellow-600" />
              </div>
              <div class="ml-4">
                <p class="text-sm font-medium text-gray-600">Pending Approvals</p>
                <p class="text-2xl font-bold text-gray-900">{{ stats.pendingApprovals }}</p>
              </div>
            </div>
          </div>
          
          <div class="bg-white p-6 rounded-lg shadow-sm border border-gray-200">
            <div class="flex items-center">
              <div class="p-2 bg-green-100 rounded-lg">
                <CheckCircle class="h-6 w-6 text-green-600" />
              </div>
              <div class="ml-4">
                <p class="text-sm font-medium text-gray-600">Approved This Month</p>
                <p class="text-2xl font-bold text-gray-900">{{ stats.approvedThisMonth }}</p>
              </div>
            </div>
          </div>
          
          <div class="bg-white p-6 rounded-lg shadow-sm border border-gray-200">
            <div class="flex items-center">
              <div class="p-2 bg-blue-100 rounded-lg">
                <MapPin class="h-6 w-6 text-blue-600" />
              </div>
              <div class="ml-4">
                <p class="text-sm font-medium text-gray-600">Regional Coverage</p>
                <p class="text-2xl font-bold text-gray-900">{{ stats.regionalCoverage }}%</p>
              </div>
            </div>
          </div>
        </div>
        
        <!-- Claims Management -->
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <div class="bg-white p-6 rounded-lg shadow-sm border border-gray-200">
            <div class="flex items-center justify-between mb-4">
              <h3 class="text-lg font-semibold text-gray-900">Claims Requiring Approval</h3>
              <PermissionGuard :permissions="['CAN_APPROVE_CLAIM']">
                <button class="bg-blue-600 hover:bg-blue-700 text-white px-3 py-1 rounded-md text-sm transition-colors">
                  View All Claims
                </button>
              </PermissionGuard>
            </div>
            
            <div class="space-y-3">
              <div v-for="claim in pendingClaims" :key="claim.id" class="border border-gray-200 rounded-lg p-4">
                <div class="flex items-center justify-between">
                  <div>
                    <p class="font-medium text-gray-900">Claim #{{ claim.id }}</p>
                    <p class="text-sm text-gray-600">{{ claim.farmerName }} - {{ claim.location }}</p>
                    <p class="text-sm text-gray-500">Damage: {{ claim.damageType }}</p>
                  </div>
                  <div class="text-right">
                    <p class="text-lg font-bold text-gray-900">₱{{ claim.amount.toLocaleString() }}</p>
                    <PermissionGuard :permissions="['CAN_APPROVE_CLAIM']">
                      <button class="bg-green-600 hover:bg-green-700 text-white px-3 py-1 rounded-md text-sm mt-2 transition-colors">
                        Approve
                      </button>
                    </PermissionGuard>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <div class="bg-white p-6 rounded-lg shadow-sm border border-gray-200">
            <h3 class="text-lg font-semibold text-gray-900 mb-4">Regional Statistics</h3>
            <div class="space-y-4">
              <div class="flex justify-between items-center">
                <span class="text-gray-600">Total Farmers Covered</span>
                <span class="font-semibold">{{ stats.totalFarmers.toLocaleString() }}</span>
              </div>
              <div class="flex justify-between items-center">
                <span class="text-gray-600">Hectares Insured</span>
                <span class="font-semibold">{{ stats.hectaresInsured.toLocaleString() }}</span>
              </div>
              <div class="flex justify-between items-center">
                <span class="text-gray-600">Average Processing Time</span>
                <span class="font-semibold">{{ stats.avgProcessingTime }} days</span>
              </div>
              <div class="flex justify-between items-center">
                <span class="text-gray-600">Approval Rate</span>
                <span class="font-semibold text-green-600">{{ stats.approvalRate }}%</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </AuthLayout>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue'
  import { Clock, CheckCircle, MapPin, LayoutDashboard, User, FileCheck } from 'lucide-vue-next'
  import AuthLayout from '@/layouts/AuthLayout.vue'
  import PermissionGuard from '@/components/PermissionGuard.vue'
  
  const navigation = ref([
    {
      title: 'Dashboard',
      to: { name: 'RegionalFieldOfficesDashboard' },
      icon: LayoutDashboard
    },
    {
      title: 'Users',
      to: { name: 'Users' },
      icon: User,
      permission: 'CAN_VIEW_USER'
    },
    {
      title: 'Claims',
      to: { name: 'Claims' },
      icon: FileCheck,
      permission: 'CAN_APPROVE_CLAIM'
    }
  ])
  
  const stats = ref({
    pendingApprovals: 0,
    approvedThisMonth: 0,
    regionalCoverage: 0,
    totalFarmers: 0,
    hectaresInsured: 0,
    avgProcessingTime: 0,
    approvalRate: 0
  })
  
  const pendingClaims = ref([])
  
  onMounted(async () => {
    // Load dashboard data
    try {
      // Mock data - replace with actual API calls
      stats.value = {
        pendingApprovals: 12,
        approvedThisMonth: 45,
        regionalCoverage: 87,
        totalFarmers: 2340,
        hectaresInsured: 15600,
        avgProcessingTime: 5,
        approvalRate: 92
      }
      
      pendingClaims.value = [
        {
          id: '12345',
          farmerName: 'Juan dela Cruz',
          location: 'Barangay San Jose',
          damageType: 'Flood damage',
          amount: 25000
        },
        {
          id: '12346',
          farmerName: 'Maria Santos',
          location: 'Barangay Poblacion',
          damageType: 'Drought damage',
          amount: 18000
        }
      ]
    } catch (error) {
      console.error('Failed to load dashboard data:', error)
    }
  })
  </script>
  