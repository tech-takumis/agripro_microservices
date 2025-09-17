<template>
  <AuthenticatedLayout :navigation="navigation" :role-title="'Municipal Agriculturist'" :page-title="'Municipal Dashboard'">
    <template #header>
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-2xl font-bold text-gray-900">Municipal Agriculturists Dashboard</h1>
          <p class="text-gray-600">Manage local agricultural programs and financial operations</p>
        </div>
        <div class="flex items-center space-x-2">
          <div class="px-3 py-1 bg-teal-100 text-teal-800 rounded-full text-sm font-medium">
            Municipal Agriculturist
          </div>
        </div>
      </div>
    </template>

    <div class="space-y-6">
      <!-- Stats Grid -->
      <div class="grid grid-cols-1 md:grid-cols-4 gap-6">
        <div class="bg-white p-6 rounded-lg shadow-sm border border-gray-200">
          <div class="flex items-center">
            <div class="p-2 bg-green-100 rounded-lg">
              <Sprout class="h-6 w-6 text-green-600" />
            </div>
            <div class="ml-4">
              <p class="text-sm font-medium text-gray-600">Local Farmers</p>
              <p class="text-2xl font-bold text-gray-900">{{ stats.localFarmers.toLocaleString() }}</p>
            </div>
          </div>
        </div>

        <div class="bg-white p-6 rounded-lg shadow-sm border border-gray-200">
          <div class="flex items-center">
            <div class="p-2 bg-blue-100 rounded-lg">
              <DollarSign class="h-6 w-6 text-blue-600" />
            </div>
            <div class="ml-4">
              <p class="text-sm font-medium text-gray-600">Municipal Budget</p>
              <p class="text-2xl font-bold text-gray-900">₱{{ stats.municipalBudget.toLocaleString() }}K</p>
            </div>
          </div>
        </div>

        <div class="bg-white p-6 rounded-lg shadow-sm border border-gray-200">
          <div class="flex items-center">
            <div class="p-2 bg-yellow-100 rounded-lg">
              <TrendingUp class="h-6 w-6 text-yellow-600" />
            </div>
            <div class="ml-4">
              <p class="text-sm font-medium text-gray-600">Disbursements</p>
              <p class="text-2xl font-bold text-gray-900">₱{{ stats.disbursements.toLocaleString() }}K</p>
            </div>
          </div>
        </div>

        <div class="bg-white p-6 rounded-lg shadow-sm border border-gray-200">
          <div class="flex items-center">
            <div class="p-2 bg-purple-100 rounded-lg">
              <Users class="h-6 w-6 text-purple-600" />
            </div>
            <div class="ml-4">
              <p class="text-sm font-medium text-gray-600">Active Programs</p>
              <p class="text-2xl font-bold text-gray-900">{{ stats.activePrograms }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Financial Management -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div class="bg-white p-6 rounded-lg shadow-sm border border-gray-200">
          <div class="flex items-center justify-between mb-4">
            <h3 class="text-lg font-semibold text-gray-900">Financial Transactions</h3>
            <PermissionGuard :permissions="['CAN_MANAGE_FINANCE']">
              <button class="bg-blue-600 hover:bg-blue-700 text-white px-3 py-1 rounded-md text-sm transition-colors">
                New Transaction
              </button>
            </PermissionGuard>
          </div>

          <div class="space-y-3">
            <div v-for="transaction in recentTransactions" :key="transaction.id"
              class="border border-gray-200 rounded-lg p-4">
              <div class="flex items-center justify-between">
                <div>
                  <p class="font-medium text-gray-900">{{ transaction.description }}</p>
                  <p class="text-sm text-gray-600">{{ transaction.date }} - {{ transaction.category }}</p>
                </div>
                <div class="text-right">
                  <p class="text-lg font-bold"
                    :class="transaction.type === 'income' ? 'text-green-600' : 'text-red-600'">
                    {{ transaction.type === 'income' ? '+' : '-' }}₱{{ transaction.amount.toLocaleString() }}
                  </p>
                  <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                    :class="getTransactionStatusClass(transaction.status)">
                    {{ transaction.status }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="bg-white p-6 rounded-lg shadow-sm border border-gray-200">
          <h3 class="text-lg font-semibold text-gray-900 mb-4">Municipal Programs</h3>
          <div class="space-y-4">
            <div v-for="program in municipalPrograms" :key="program.id" class="border border-gray-200 rounded-lg p-4">
              <div class="flex items-center justify-between mb-2">
                <h4 class="font-medium text-gray-900">{{ program.name }}</h4>
                <PermissionGuard :permissions="['CAN_MANAGE_ROLES']">
                  <button
                    class="bg-gray-600 hover:bg-gray-700 text-white px-3 py-1 rounded-md text-sm transition-colors">
                    Manage
                  </button>
                </PermissionGuard>
              </div>
              <div class="grid grid-cols-2 gap-4 text-sm">
                <div>
                  <p class="text-gray-600">Beneficiaries</p>
                  <p class="font-semibold">{{ program.beneficiaries }}</p>
                </div>
                <div>
                  <p class="text-gray-600">Budget</p>
                  <p class="font-semibold">₱{{ program.budget.toLocaleString() }}K</p>
                </div>
              </div>
              <div class="mt-2">
                <div class="w-full bg-gray-200 rounded-full h-2">
                  <div class="bg-blue-600 h-2 rounded-full" :style="{ width: program.progress + '%' }"></div>
                </div>
                <p class="text-xs text-gray-500 mt-1">{{ program.progress }}% Complete</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </AuthenticatedLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Sprout, DollarSign, TrendingUp, Users } from 'lucide-vue-next'
import AuthenticatedLayout from '../../layouts/AuthenticatedLayout.vue'
import PermissionGuard from '../../components/others/PermissionGuard.vue'
import { MUNICIPAL_AGRICULTURIST_NAVIGATION } from '../../lib/constants.js'

const navigation = ref(MUNICIPAL_AGRICULTURIST_NAVIGATION)

const stats = ref({
  localFarmers: 0,
  municipalBudget: 0,
  disbursements: 0,
  activePrograms: 0
})

const recentTransactions = ref([])
const municipalPrograms = ref([])

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

onMounted(async () => {
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
</script>