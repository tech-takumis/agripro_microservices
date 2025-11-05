<template>
    <div class="bg-white rounded-2xl border border-gray-100 p-6">
        <div class="flex items-center space-x-3 mb-6">
            <div class="p-2 bg-yellow-50 rounded-lg">
                <Briefcase class="h-6 w-6 text-yellow-500" />
            </div>
            <h3 class="text-lg font-semibold text-yellow-500">Latest Programs</h3>
        </div>

        <div class="space-y-3">
            <div
                v-for="program in latestPrograms"
                :key="program.id"
                class="border border-gray-200 rounded-lg p-3 hover:bg-gray-50 transition"
            >
                <div class="flex items-center justify-between mb-2">
                    <div class="flex items-center space-x-2">
                        <ClipboardList class="h-5 w-5 text-gray-500" />
                        <h4 class="font-medium text-gray-900 text-sm">{{ program.name }}</h4>
                    </div>
                    <PermissionGuard :permission="['CAN_MANAGE_PROGRAM']">
                        <button
                            class="flex items-center bg-gray-600 hover:bg-gray-700 text-white px-2 py-1 rounded-md text-xs transition-colors"
                        >
                            <Settings class="h-3 w-3 mr-1" />
                            Manage
                        </button>
                    </PermissionGuard>
                </div>

                <div class="grid grid-cols-2 gap-2 text-xs mb-2">
                    <div class="flex items-center space-x-1">
                        <Users class="h-4 w-4 text-green-600" />
                        <span class="font-semibold">{{ program.beneficiaries.length }}</span>
                        <span class="text-gray-500">beneficiaries</span>
                    </div>
                    <div class="flex items-center space-x-1">
                        <Wallet class="h-4 w-4 text-yellow-600" />
                        <span class="font-semibold">₱{{ program.budget }}K</span>
                    </div>
                </div>

                <div class="mt-2">
                    <div class="w-full bg-gray-200 rounded-full h-1.5">
                        <div class="bg-green-600 h-1.5 rounded-full" :style="{ width: program.progress + '%' }"></div>
                    </div>
                    <p class="text-xs text-green-600 mt-1">{{ program.progress }}% Complete</p>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { computed } from 'vue'
import { useDashboardStore } from '@/stores/dashboard'
import { Briefcase, ClipboardList, Settings, Users, Wallet } from 'lucide-vue-next'
import PermissionGuard from '@/components/others/PermissionGuard.vue'

const dashboardStore = useDashboardStore()

const latestPrograms = computed(() =>
    (dashboardStore.municipalDashboard.programs || [])
        .map(p => ({
            id: p.programId,
            name: p.programName,
            beneficiaries: p.beneficiaries || 0,
            budget: dashboardStore.formatCurrency(p.budget || 0),
            progress: p.completedPercentage || 0,
            status: p.status
        }))
        .slice(0, 4)
)
</script>
