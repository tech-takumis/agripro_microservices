<template>
  <AuthenticatedLayout 
    :navigation="underwriterNavigation" 
    role-title="Underwriter Portal"
    page-title="Dashboard"
  >
    <template #header>
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-2xl font-bold text-gray-900">Underwriter Dashboard</h1>
          <p class="text-gray-600">Evaluate risks and manage insurance applications for rice farmers</p>
        </div>
        <div class="flex items-center space-x-2">
          <span class="text-sm text-gray-500">{{ currentDate }}</span>
          <div class="h-4 w-px bg-gray-300"></div>
          <span class="text-sm font-medium text-indigo-600">{{ store.userLocation }}</span>
        </div>
      </div>
    </template>

    <!-- Dashboard Content -->
    <div class="space-y-6">
      <!-- Stats Cards -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <ApplicationCard
          title="Applications Pending"
          description="Awaiting your review"
          :value="pendingApplications"
          change="+5 new"
          :icon="ClipboardList"
          variant="primary"
        />
        <ApplicationCard
          title="Applications Approved"
          description="This month"
          :value="approvedApplications"
          change="+15%"
          :icon="CheckCircle"
          variant="success"
        />
        <ApplicationCard
          title="Applications Rejected"
          description="This month"
          :value="rejectedApplications"
          change="-2%"
          :icon="XCircle"
          variant="danger"
        />
        <ApplicationCard
          title="Average Processing Time"
          description="Per application"
          value="2.5 days"
          change="-0.3 days"
          :icon="Clock"
          variant="info"
        />
      </div>

      <!-- Quick Actions -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <h2 class="text-lg font-semibold text-gray-900 mb-4">Quick Actions</h2>
        <div class="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-6 gap-4">
          <UnderwriterQuickActionButton
            title="Review New Application"
            description="Start underwriting process"
            :icon="FileText"
            variant="primary"
            @click="navigateTo('/underwriter/applications/pending')"
          />
          <UnderwriterQuickActionButton
            title="Search Policy"
            description="Find existing policies"
            :icon="Search"
            variant="secondary"
            @click="navigateTo('/underwriter/policies/search')"
          />
          <UnderwriterQuickActionButton
            title="Eligibility Criteria"
            description="View guidelines"
            :icon="ClipboardCheck"
            variant="default"
            @click="navigateTo('/underwriter/guidelines/eligibility')"
          />
          <UnderwriterQuickActionButton
            title="Risk Assessment Tools"
            description="Access analysis tools"
            :icon="Target"
            variant="default"
            @click="navigateTo('/underwriter/tools/risk-assessment')"
          />
          <UnderwriterQuickActionButton
            title="Premium Calculator"
            description="Estimate premiums"
            :icon="Calculator"
            variant="default"
            @click="navigateTo('/underwriter/tools/premium-calculator')"
          />
          <UnderwriterQuickActionButton
            title="Generate Report"
            description="Create underwriting reports"
            :icon="BarChart3"
            variant="default"
            @click="navigateTo('/underwriter/reports')"
          />
        </div>
      </div>

      <!-- Recent Applications for Review -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200">
        <div class="px-6 py-4 border-b border-gray-200">
          <div class="flex items-center justify-between">
            <h2 class="text-lg font-semibold text-gray-900">Recent Applications for Review</h2>
            <router-link 
              to="/underwriter/applications/pending"
              class="text-sm text-indigo-600 hover:text-indigo-700 font-medium"
            >
              View all pending
            </router-link>
          </div>
        </div>
        <div class="overflow-x-auto">
          <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
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
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Actions
                </th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              <tr v-for="app in recentApplications" :key="app.id">
                <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
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
                <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                  <button 
                    @click="viewApplication(app.id)"
                    class="text-indigo-600 hover:text-indigo-900"
                  >
                    Review
                  </button>
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
  ClipboardList, CheckCircle, XCircle, Clock, FileText, Search, 
  ClipboardCheck, Target, Calculator, BarChart3, LayoutDashboard, Shield
} from 'lucide-vue-next'
import AuthenticatedLayout from '../../layouts/AuthenticatedLayout.vue'
import ApplicationCard from '@/components/underwriter/ApplicationCard.vue'
import UnderwriterQuickActionButton from '@/components/underwriter/UnderwriterQuickActionButton.vue'
import { useUserStore } from '@/stores/user'
import { UNDERWRITER_NAVIGATION } from '@/lib/constants' // Updated import

const store = useUserStore()
const router = useRouter()

const underwriterNavigation = UNDERWRITER_NAVIGATION // Use the imported constant

// Sample data for stats
const pendingApplications = ref(12)
const approvedApplications = ref(85)
const rejectedApplications = ref(5)

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
  // Implement logic to view specific application details
  console.log('Viewing application:', appId)
  router.push(`/underwriter/applications/${appId}/review`)
}
</script>
