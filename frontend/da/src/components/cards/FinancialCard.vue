<template>
    <div class="bg-white rounded-2xl shadow-md border border-gray-100 p-6">
        <div class="flex items-center justify-between mb-6">
            <div class="flex items-center space-x-3">
                <div class="p-2 bg-red-50 rounded-lg">
                    <CreditCard class="h-6 w-6 text-red-600" />
                </div>
                <h3 class="text-lg font-semibold text-red-600">Latest Transactions</h3>
            </div>
            <PermissionGuard :permission="['CAN_MANAGE_FINANCE']">
                <button
                    class="flex items-center bg-blue-600 hover:bg-blue-700 text-white px-3 py-1.5 rounded-md text-sm transition-colors"
                >
                    <Plus class="h-4 w-4 mr-1" />
                    New
                </button>
            </PermissionGuard>
        </div>

        <div class="space-y-3">
            <div
                v-for="transaction in latestTransactions"
                :key="transaction.id"
                class="flex items-center justify-between border border-gray-200 rounded-lg p-3 hover:bg-gray-50 transition"
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
                        <p class="font-medium text-gray-900 text-sm">{{ transaction.description }}</p>
                        <p class="text-xs text-gray-500">{{ transaction.date }}</p>
                    </div>
                </div>
                <div class="text-right">
                    <p
                        class="text-sm font-bold"
                        :class="transaction.type === 'income' ? 'text-green-600' : 'text-red-600'"
                    >
                        {{ transaction.type === 'income' ? '+' : '-' }}₱{{ transaction.amount.toLocaleString() }}
                    </p>
                    <span
                        class="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium"
                        :class="getStatusClass(transaction.status)"
                    >
            {{ transaction.status }}
          </span>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { computed } from 'vue'
import { useDashboardStore } from '@/stores/dashboard'
import { CreditCard, Plus, ArrowDownCircle, ArrowUpCircle } from 'lucide-vue-next'
import PermissionGuard from '@/components/others/PermissionGuard.vue'

const dashboardStore = useDashboardStore()

const latestTransactions = computed(() =>
    (dashboardStore.municipalDashboard.transactions || [])
        .map(t => ({
            id: t.transactionId,
            description: t.name || (t.type === 'INCOME' ? 'Budget Allocation' : 'Program Disbursement'),
            date: new Date(t.date).toLocaleDateString(),
            amount: t.amount,
            type: t.type.toLowerCase(),
            status: t.status || 'Completed'
        }))
        .slice(0, 4)
)

const getStatusClass = (status) => {
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
</script>
