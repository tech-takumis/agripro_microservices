<template>
  <AuthenticatedLayout 
    :navigation="tellerNavigation" 
    role-title="Teller Portal"
    page-title="Dashboard"
  >
    <template #header>
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-2xl font-bold text-gray-900">Teller Dashboard</h1>
          <p class="text-gray-600">Manage financial transactions and customer payments</p>
        </div>
        <div class="flex items-center space-x-2">
          <span class="text-sm text-gray-500">{{ currentDate }}</span>
          <div class="h-4 w-px bg-gray-300"></div>
          <span class="text-sm font-medium text-green-600">{{ store.userLocation }}</span>
        </div>
      </div>
    </template>

    <!-- Dashboard Content -->
    <div class="space-y-6">
      <!-- Stats Cards -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <TransactionCard
          title="Today's Transactions"
          description="Total processed today"
          value="₱125,450.00"
          change="+12.5%"
          :icon="DollarSign"
          variant="success"
        />
        <TransactionCard
          title="Completed Today"
          description="Successfully processed"
          value="87"
          change="+8.1%"
          :icon="CheckCircle"
          variant="success"
        />
        <TransactionCard
          title="Average Transaction"
          description="Per transaction amount"
          value="₱1,442.00"
          change="+3.2%"
          :icon="TrendingUp"
          variant="info"
        />
      </div>

      <!-- Quick Actions -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <h2 class="text-lg font-semibold text-gray-900 mb-4">Quick Actions</h2>
        <div class="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-6 gap-4">
          <QuickActionButton
            title="New Payment"
            description="Process payment"
            :icon="CreditCard"
            variant="primary"
            @click="navigateTo('/teller/payments/new')"
          />
          <QuickActionButton
            title="Cash Deposit"
            description="Record deposit"
            :icon="PiggyBank"
            variant="primary"
            @click="navigateTo('/teller/deposits/new')"
          />
          <QuickActionButton
            title="Withdrawal"
            description="Process withdrawal"
            :icon="Banknote"
            variant="secondary"
            @click="navigateTo('/teller/withdrawals/new')"
          />
          <QuickActionButton
            title="Balance Inquiry"
            description="Check balance"
            :icon="Search"
            variant="secondary"
            @click="navigateTo('/teller/balance-inquiry')"
          />
          <QuickActionButton
            title="Transaction History"
            description="View history"
            :icon="FileText"
            variant="default"
            @click="navigateTo('/teller/transactions')"
          />
          <QuickActionButton
            title="Daily Report"
            description="Generate report"
            :icon="BarChart3"
            variant="default"
            @click="navigateTo('/teller/reports/daily')"
          />
        </div>
      </div>

      <!-- Recent Transactions -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200">
        <div class="px-6 py-4 border-b border-gray-200">
          <div class="flex items-center justify-between">
            <h2 class="text-lg font-semibold text-gray-900">Recent Transactions</h2>
            <router-link 
              to="/teller/transactions"
              class="text-sm text-green-600 hover:text-green-700 font-medium"
            >
              View all
            </router-link>
          </div>
        </div>
        <div class="overflow-x-auto">
          <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Transaction ID
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Customer
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Type
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Amount
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Status
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Time
                </th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              <tr v-for="transaction in recentTransactions" :key="transaction.id">
                <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                  {{ transaction.id }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                  {{ transaction.customer }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                  {{ transaction.type }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                  ₱{{ transaction.amount.toLocaleString() }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <span :class="[
                    'inline-flex px-2 py-1 text-xs font-semibold rounded-full',
                    transaction.status === 'Completed' ? 'bg-green-100 text-green-800' :
                    transaction.status === 'Pending' ? 'bg-yellow-100 text-yellow-800' :
                    'bg-red-100 text-red-800'
                  ]">
                    {{ transaction.status }}
                  </span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                  {{ transaction.time }}
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
import { 
  DollarSign, Clock, CheckCircle, TrendingUp, CreditCard, 
  PiggyBank, Banknote, Search, FileText, BarChart3, Users
} from 'lucide-vue-next'
import AuthenticatedLayout from '../../layouts/AuthenticatedLayout.vue'
import TransactionCard from '@/components/teller/TransactionCard.vue'
import QuickActionButton from '@/components/teller/QuickActionButton.vue'
import { useAuthStore } from '@/stores/auth'

const store = useAuthStore()
const router = useRouter()

// Navigation for teller role
const tellerNavigation = [
  {
    name: 'Dashboard',
    href: '/teller/dashboard',
    icon: BarChart3
  },
  {
    name: 'Transactions',
    icon: DollarSign,
    children: [
      { name: 'New Payment', href: '/teller/payments/new' },
      { name: 'Cash Deposit', href: '/teller/deposits/new' },
      { name: 'Withdrawal', href: '/teller/withdrawals/new' },
      { name: 'Transaction History', href: '/teller/transactions' }
    ]
  },
  {
    name: 'Customer Services',
    icon: Users,
    children: [
      { name: 'Balance Inquiry', href: '/teller/balance-inquiry' },
      { name: 'Account Information', href: '/teller/account-info' },
      { name: 'Statement Request', href: '/teller/statements' }
    ]
  },
  {
    name: 'Reports',
    icon: FileText,
    children: [
      { name: 'Daily Report', href: '/teller/reports/daily' },
      { name: 'Monthly Summary', href: '/teller/reports/monthly' },
      { name: 'Transaction Report', href: '/teller/reports/transactions' }
    ]
  }
]

// Sample data
const recentTransactions = ref([
  {
    id: 'TXN-001',
    customer: 'Juan Dela Cruz',
    type: 'Premium Payment',
    amount: 5000,
    status: 'Completed',
    time: '10:30 AM'
  },
  {
    id: 'TXN-002',
    customer: 'Maria Santos',
    type: 'Claim Payment',
    amount: 15000,
    status: 'Pending',
    time: '10:15 AM'
  },
  {
    id: 'TXN-003',
    customer: 'Pedro Garcia',
    type: 'Premium Payment',
    amount: 3500,
    status: 'Completed',
    time: '09:45 AM'
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
