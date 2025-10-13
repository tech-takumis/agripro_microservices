<script setup>
import AuthenticatedLayout from '@/layouts/AuthenticatedLayout.vue'
import PermissionGuard from '@/components/others/PermissionGuard.vue'
import { AGRICULTURAL_EXTENSION_WORKER_NAVIGATION } from "@/lib/navigation"
import { useAuthStore } from '@/stores/auth'
import { useProgramStore } from '@/stores/program'
import { useScheduleStore } from '@/stores/schedule'
import { computed, ref, onMounted } from 'vue'
import { Users, MapPin, BookOpen, DollarSign } from 'lucide-vue-next'

const navigation = AGRICULTURAL_EXTENSION_WORKER_NAVIGATION
const authStore = useAuthStore()
const programStore = useProgramStore()
const scheduleStore = useScheduleStore()

// Computed properties from stores
const stats = computed(() => {
  const programs = programStore.allPrograms || []
  const schedules = scheduleStore.allSchedules || []
  
  // Calculate total budget from active programs
  const totalBudget = programs
    .filter(p => p.status === 'ACTIVE')
    .reduce((sum, program) => {
      const budget = program.extraFields?.budget 
        ? parseFloat(program.extraFields.budget.replace(/[^0-9.]/g, '')) / 1000000 // Convert to millions
        : 0
      return sum + budget
    }, 0)
  
  // Calculate utilized budget based on completion percentage
  const utilizedBudget = programs
    .filter(p => p.status === 'ACTIVE')
    .reduce((sum, program) => {
      const budget = program.extraFields?.budget 
        ? parseFloat(program.extraFields.budget.replace(/[^0-9.]/g, '')) * (program.completion / 100) / 1000000
        : 0
      return sum + budget
    }, 0)
  
  return {
    farmersAssisted: programs.reduce((sum, p) => {
      const farmers = parseInt(p.extraFields?.target_farmers?.replace(/[^0-9]/g, '') || '0')
      return sum + (p.status === 'ACTIVE' ? farmers : 0)
    }, 0),
    fieldVisits: schedules.filter(s => s.type === 'VISIT').length,
    trainingSessions: programs.filter(p => p.type === 'TRAINING' && p.status === 'ACTIVE').length,
    programBudget: Math.round(totalBudget * 100) / 100, // Round to 2 decimal places
    budgetUtilized: Math.round(utilizedBudget * 100) / 100 // Round to 2 decimal places
  }
})

const scheduledVisits = computed(() => {
  return (scheduleStore.upcomingSchedules || []).slice(0, 5).map(schedule => ({
    id: schedule.id,
    farmerName: schedule.metaData?.farmer_name || 'Farmer',
    location: schedule.metaData?.location || 'Location not specified',
    date: new Date(schedule.scheduleDate).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    }),
    purpose: schedule.metaData?.purpose || 'No specific purpose provided',
    priority: schedule.priority,
    type: schedule.type
  }))
})

const extensionPrograms = computed(() => {
  return (programStore.activePrograms || []).slice(0, 3).map(program => ({
    id: program.id,
    name: program.name,
    type: program.type,
    participants: program.extraFields?.target_farmers || 'N/A',
    status: program.status,
    completion: program.completion,
    location: program.extraFields?.location || 'Multiple locations',
    duration: program.extraFields?.duration || program.extraFields?.implementation_period || 'Ongoing'
  }))
})

const getVisitPriorityClass = (priority) => {
  switch (priority) {
    case 'HIGH':
      return 'bg-red-100 text-red-800'
    case 'MEDIUM':
      return 'bg-yellow-100 text-yellow-800'
    case 'LOW':
      return 'bg-green-100 text-green-800'
    default:
      return 'bg-gray-100 text-gray-800'
  }
}

// Loading states
const isLoading = computed(() => programStore.loading || scheduleStore.loading)
const hasError = computed(() => programStore.error || scheduleStore.error)

onMounted(async () => {
  try {
    // Fetch data from stores
    await Promise.all([
      programStore.fetchPrograms(),
      scheduleStore.fetchSchedules()
    ])
  } catch (error) {
    console.error('Failed to load dashboard data:', error)
  }
})

// Filter top 5 scheduled visits with priority "High" or "Medium"
const topPriorityVisits = computed(() => {
  return (scheduleStore.upcomingSchedules || [])
    .filter(schedule => schedule.priority === 'HIGH' || schedule.priority === 'MEDIUM')
    .slice(0, 5)
    .map(schedule => ({
      id: schedule.id,
      farmerName: schedule.metaData?.farmer_name || 'Farmer',
      location: schedule.metaData?.location || 'Location not specified',
      date: new Date(schedule.scheduleDate).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      }),
      purpose: schedule.metaData?.purpose || 'No specific purpose provided',
      priority: schedule.priority,
      type: schedule.type
    }));
});
</script>

<template>
    <AuthenticatedLayout :navigation="navigation" role-title="MUNICIPAL AGRICULTURIST PORTAL" page-title="Dashboard">
        <template #header>
            <div class="flex items-center justify-between">
                <div>
                    <h1 class="text-2xl font-bold text-gray-900">Agricultural Extension Workers Dashboard</h1>
                    <p class="text-gray-600">Field operations, farmer support, and program management</p>
                </div>
                <div class="flex items-center space-x-2">
                    <div class="px-3 py-1 bg-green-100 text-green-800 rounded-full text-sm font-medium">
                        Extension Worker
                    </div>
                </div>
            </div>
        </template>

        <div class="space-y-6">
            <!-- Loading State -->
            <div v-if="isLoading" class="flex justify-center items-center py-8">
                <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
                <span class="ml-2 text-gray-600">Loading dashboard data...</span>
            </div>

            <!-- Error State -->
            <div v-else-if="hasError" class="bg-red-50 border border-red-200 rounded-lg p-4 mb-6">
                <div class="flex">
                    <div class="text-red-400">
                        <svg class="h-5 w-5" fill="currentColor" viewBox="0 0 20 20">
                            <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd" />
                        </svg>
                    </div>
                    <div class="ml-3">
                        <h3 class="text-sm font-medium text-red-800">Error loading dashboard data</h3>
                        <p class="text-sm text-red-700 mt-1">{{ hasError }}</p>
                    </div>
                </div>
            </div>

            <!-- Stats Grid -->
            <div v-else class="grid grid-cols-1 md:grid-cols-4 gap-6">
                <div class="card">
                    <div class="flex items-center">
                        <div class="p-2 bg-green-100 rounded-lg">
                            <Users class="h-6 w-6 text-green-600" />
                        </div>
                        <div class="ml-4">
                            <p class="text-sm font-medium text-gray-600">Farmers Assisted</p>
                            <p class="text-2xl font-bold text-gray-900">{{ stats.farmersAssisted.toLocaleString() }}</p>
                        </div>
                    </div>
                </div>

                <div class="card">
                    <div class="flex items-center">
                        <div class="p-2 bg-blue-100 rounded-lg">
                            <MapPin class="h-6 w-6 text-blue-600" />
                        </div>
                        <div class="ml-4">
                            <p class="text-sm font-medium text-gray-600">Field Visits</p>
                            <p class="text-2xl font-bold text-gray-900">{{ stats.fieldVisits }}</p>
                        </div>
                    </div>
                </div>

                <div class="card">
                    <div class="flex items-center">
                        <div class="p-2 bg-yellow-100 rounded-lg">
                            <BookOpen class="h-6 w-6 text-yellow-600" />
                        </div>
                        <div class="ml-4">
                            <p class="text-sm font-medium text-gray-600">Training Sessions</p>
                            <p class="text-2xl font-bold text-gray-900">{{ stats.trainingSessions }}</p>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Field Operations -->
            <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
                <div class="card">
                    <div class="flex items-center justify-between mb-4">
                        <h3 class="text-lg font-semibold text-gray-900">Scheduled Field Visits</h3>
                    </div>

                    <div class="space-y-3">
                        <!-- Display filtered visits -->
                        <div v-for="visit in topPriorityVisits" :key="visit.id" class="border border-gray-200 rounded-lg p-4">
                          <div class="flex items-center justify-between mb-2">
                            <div>
                              <h4 class="font-medium text-gray-900">{{ visit.farmerName }}</h4>
                              <p class="text-sm text-gray-600">{{ visit.location }}</p>
                            </div>
                            <span
                              class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                              :class="getVisitPriorityClass(visit.priority)"
                            >
                              {{ visit.priority }}
                            </span>
                          </div>
                          <div class="grid grid-cols-2 gap-4 text-sm">
                            <div>
                              <p class="text-gray-600">Date</p>
                              <p class="font-semibold">{{ visit.date }}</p>
                            </div>
                            <div>
                              <p class="text-gray-600">Purpose</p>
                              <p class="font-semibold">{{ visit.purpose }}</p>
                            </div>
                          </div>
                          <div class="mt-2 flex space-x-2">
                            <BaseButton variant="primary" size="small">
                              Start Visit
                            </BaseButton>
                            <BaseButton variant="secondary" size="small">
                              Reschedule
                            </BaseButton>
                          </div>
                        </div>

                        <!-- Browse More Button -->
                        <div class="mt-4 text-center">
                          <BaseButton variant="outline" size="medium" @click="() => $router.push('/schedules')">
                            Browse More
                          </BaseButton>
                        </div>
                      </div>
                    </div>

                    <!-- Extension Programs Section -->
                    <div class="card">
                      <h3 class="text-lg font-semibold text-gray-900 mb-4">Extension Programs</h3>
                      <div class="space-y-4">
                        <div v-for="program in extensionPrograms" :key="program.id" class="border border-gray-200 rounded-lg p-4">
                          <div class="flex items-center justify-between mb-2">
                            <h4 class="font-medium text-gray-900">{{ program.name }}</h4>
                            <PermissionGuard :permissions="['CAN_MANAGE_ROLES']">
                              <BaseButton variant="secondary" size="small">
                                Manage
                              </BaseButton>
                            </PermissionGuard>
                          </div>
                          <div class="grid grid-cols-2 gap-4 text-sm">
                            <div>
                              <p class="text-gray-600">Participants</p>
                              <p class="font-semibold">{{ program.participants }}</p>
                            </div>
                            <div>
                              <p class="text-gray-600">Status</p>
                              <p class="font-semibold">{{ program.status }}</p>
                            </div>
                          </div>
                          <div class="mt-2">
                            <div class="w-full bg-gray-200 rounded-full h-2">
                              <div class="bg-primary-600 h-2 rounded-full" :style="{ width: program.completion + '%' }"></div>
                            </div>
                            <p class="text-xs text-gray-500 mt-1">{{ program.completion }}% Complete</p>
                          </div>
                        </div>
                      </div>

                      <PermissionGuard :permissions="['CAN_MANAGE_FINANCE']">
                        <div class="mt-4 pt-4 border-t border-gray-200">
                          <h4 class="font-medium text-gray-900 mb-2">Financial Overview</h4>
                          <div class="space-y-2 text-sm">
                            <div class="flex justify-between">
                              <span class="text-gray-600">Total Budget</span>
                              <span class="font-semibold">₱{{ stats.programBudget.toLocaleString() }}K</span>
                            </div>
                            <div class="flex justify-between">
                              <span class="text-gray-600">Utilized</span>
                              <span class="font-semibold">₱{{ stats.budgetUtilized.toLocaleString() }}K</span>
                            </div>
                            <div class="flex justify-between">
                              <span class="text-gray-600">Remaining</span>
                              <span class="font-semibold text-success-600">₱{{ (stats.programBudget - stats.budgetUtilized).toLocaleString() }}K</span>
                            </div>
                            <div class="flex justify-between">
                              <span class="text-gray-600">Utilization Rate</span>
                              <span class="font-semibold">{{ Math.round((stats.budgetUtilized / stats.programBudget) * 100) }}%</span>
                            </div>
                          </div>
                        </div>
                      </PermissionGuard>
                    </div>
                  </div>
                </div>
              </AuthenticatedLayout>
            </template>

            <style scoped></style>
