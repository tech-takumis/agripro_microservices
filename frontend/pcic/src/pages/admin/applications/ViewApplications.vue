<template>
  <AuthenticatedLayout 
    :navigation="adminNavigation" 
    role-title="Admin Portal"
    page-title="All Application Types"
  >
    <template #header>
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-2xl font-bold text-gray-900">All Application Types</h1>
          <p class="text-gray-600">Manage and view all insurance application types in the system</p>
        </div>
        <div class="flex items-center space-x-3">
          <button
            @click="refreshApplications"
            :disabled="loading"
            class="inline-flex items-center px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 disabled:opacity-50"
          >
            <RefreshCw :class="['h-4 w-4 mr-2', loading ? 'animate-spin' : '']" />
            Refresh
          </button>
          <router-link
            to="/admin/applications/new"
            class="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700"
          >
            <Plus class="h-4 w-4 mr-2" />
            Create New
          </router-link>
        </div>
      </div>
    </template>

    <div class="space-y-6">
      <!-- Statistics Cards -->
      <div class="grid grid-cols-1 md:grid-cols-4 gap-6">
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <div class="flex items-center">
            <div class="p-3 rounded-lg bg-blue-100">
              <FileText class="h-6 w-6 text-blue-600" />
            </div>
            <div class="ml-4">
              <h3 class="text-lg font-semibold text-gray-900">{{ applications.length }}</h3>
              <p class="text-sm text-gray-500">Total Applications</p>
            </div>
          </div>
        </div>
        
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <div class="flex items-center">
            <div class="p-3 rounded-lg bg-green-100">
              <Layers class="h-6 w-6 text-green-600" />
            </div>
            <div class="ml-4">
              <h3 class="text-lg font-semibold text-gray-900">{{ totalSections }}</h3>
              <p class="text-sm text-gray-500">Total Sections</p>
            </div>
          </div>
        </div>
        
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <div class="flex items-center">
            <div class="p-3 rounded-lg bg-purple-100">
              <Settings class="h-6 w-6 text-purple-600" />
            </div>
            <div class="ml-4">
              <h3 class="text-lg font-semibold text-gray-900">{{ totalFields }}</h3>
              <p class="text-sm text-gray-500">Total Fields</p>
            </div>
          </div>
        </div>
        
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <div class="flex items-center">
            <div class="p-3 rounded-lg bg-yellow-100">
              <Brain class="h-6 w-6 text-yellow-600" />
            </div>
            <div class="ml-4">
              <h3 class="text-lg font-semibold text-gray-900">{{ aiEnabledCount }}</h3>
              <p class="text-sm text-gray-500">AI-Enabled</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Filters and Search -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200">
        <div class="px-6 py-4 border-b border-gray-200">
          <h2 class="text-lg font-semibold text-gray-900">Filter & Search</h2>
        </div>
        <div class="p-6">
          <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
            <!-- Search -->
            <div class="md:col-span-2">
              <label for="search" class="block text-sm font-medium text-gray-700 mb-1">
                Search Applications
              </label>
              <div class="relative">
                <Search class="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
                <input
                  v-model="searchQuery"
                  type="text"
                  id="search"
                  placeholder="Search by name, description..."
                  class="w-full pl-10 pr-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                />
              </div>
            </div>

            <!-- Layout Filter -->
            <div>
              <label for="layoutFilter" class="block text-sm font-medium text-gray-700 mb-1">
                Layout Type
              </label>
              <select
                v-model="selectedLayout"
                id="layoutFilter"
                class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
              >
                <option value="">All Layouts</option>
                <option value="single-step">Single Step</option>
                <option value="two-step">Two Step</option>
                <option value="multi-step">Multi Step</option>
              </select>
            </div>

            <!-- AI Analysis Filter -->
            <div>
              <label for="aiFilter" class="block text-sm font-medium text-gray-700 mb-1">
                AI Analysis
              </label>
              <select
                v-model="selectedAIFilter"
                id="aiFilter"
                class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
              >
                <option value="">All Types</option>
                <option value="true">AI Enabled</option>
                <option value="false">Standard</option>
              </select>
            </div>
          </div>

          <!-- Advanced Filters -->
          <div class="mt-4 pt-4 border-t border-gray-200">
            <div class="flex items-center justify-between">
              <button
                @click="showAdvancedFilters = !showAdvancedFilters"
                class="flex items-center text-sm text-indigo-600 hover:text-indigo-700"
              >
                <Filter class="h-4 w-4 mr-1" />
                Advanced Filters
                <ChevronDown :class="['h-4 w-4 ml-1 transition-transform', showAdvancedFilters ? 'rotate-180' : '']" />
              </button>
              <button
                @click="clearFilters"
                class="text-sm text-gray-600 hover:text-gray-700"
              >
                Clear All Filters
              </button>
            </div>

            <div v-show="showAdvancedFilters" class="mt-4 grid grid-cols-1 md:grid-cols-3 gap-4">
              <!-- Section Count Filter -->
              <div>
                <label for="sectionCount" class="block text-sm font-medium text-gray-700 mb-1">
                  Number of Sections
                </label>
                <select
                  v-model="selectedSectionCount"
                  id="sectionCount"
                  class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                >
                  <option value="">Any</option>
                  <option value="1">1 Section</option>
                  <option value="2">2 Sections</option>
                  <option value="3">3+ Sections</option>
                </select>
              </div>

              <!-- Field Count Filter -->
              <div>
                <label for="fieldCount" class="block text-sm font-medium text-gray-700 mb-1">
                  Total Fields
                </label>
                <select
                  v-model="selectedFieldCount"
                  id="fieldCount"
                  class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                >
                  <option value="">Any</option>
                  <option value="1-5">1-5 Fields</option>
                  <option value="6-10">6-10 Fields</option>
                  <option value="11+">11+ Fields</option>
                </select>
              </div>

              <!-- Sort By -->
              <div>
                <label for="sortBy" class="block text-sm font-medium text-gray-700 mb-1">
                  Sort By
                </label>
                <select
                  v-model="sortBy"
                  id="sortBy"
                  class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                >
                  <option value="name">Name (A-Z)</option>
                  <option value="name-desc">Name (Z-A)</option>
                  <option value="sections">Section Count</option>
                  <option value="fields">Field Count</option>
                  <option value="recent">Recently Created</option>
                </select>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="text-center py-12">
        <Loader2 class="h-8 w-8 animate-spin mx-auto text-gray-400" />
        <p class="mt-2 text-gray-500">Loading applications...</p>
      </div>

      <!-- Error State -->
      <div v-else-if="error" class="bg-red-50 border border-red-200 rounded-md p-4">
        <div class="flex">
          <AlertCircle class="h-5 w-5 text-red-400" />
          <div class="ml-3">
            <h3 class="text-sm font-medium text-red-800">Error</h3>
            <p class="mt-1 text-sm text-red-700">{{ error }}</p>
          </div>
        </div>
      </div>

      <!-- Applications Grid -->
      <div v-else-if="filteredApplications.length > 0" class="grid grid-cols-1 lg:grid-cols-2 xl:grid-cols-3 gap-6">
        <div 
          v-for="application in filteredApplications" 
          :key="application.id"
          class="bg-white rounded-lg shadow-sm border border-gray-200 hover:shadow-md hover:border-indigo-300 transition-all duration-200 cursor-pointer"
          @click="viewApplication(application)"
        >
          <!-- Application Header -->
          <div class="px-6 py-4 border-b border-gray-200">
            <div class="flex items-start justify-between">
              <div class="flex-1 min-w-0">
                <h3 class="text-lg font-semibold text-gray-900 truncate">
                  {{ application.name }}
                </h3>
                <p class="text-sm text-gray-500 mt-1 line-clamp-2">
                  {{ application.description || 'No description provided' }}
                </p>
              </div>
              <div class="ml-4 flex-shrink-0">
                <span :class="[
                  'inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium',
                  application.requiredAIAnalysis 
                    ? 'bg-yellow-100 text-yellow-800' 
                    : 'bg-gray-100 text-gray-800'
                ]">
                  {{ application.requiredAIAnalysis ? 'AI Enabled' : 'Standard' }}
                </span>
              </div>
            </div>
          </div>

          <!-- Application Stats -->
          <div class="px-6 py-4">
            <div class="grid grid-cols-3 gap-4">
              <div class="text-center">
                <div class="text-2xl font-bold text-indigo-600">
                  {{ application.sections.length }}
                </div>
                <div class="text-xs text-gray-500">Sections</div>
              </div>
              <div class="text-center">
                <div class="text-2xl font-bold text-green-600">
                  {{ getTotalFields(application) }}
                </div>
                <div class="text-xs text-gray-500">Fields</div>
              </div>
              <div class="text-center">
                <div class="text-2xl font-bold text-purple-600">
                  {{ getRequiredFields(application) }}
                </div>
                <div class="text-xs text-gray-500">Required</div>
              </div>
            </div>
          </div>

          <!-- Layout Badge -->
          <div class="px-6 py-3 bg-gray-50 border-t border-gray-200">
            <div class="flex items-center justify-between">
              <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                {{ formatLayout(application.layout) }}
              </span>
              <div class="flex items-center space-x-2">
                <button
                  @click.stop="editApplication(application)"
                  class="p-1 rounded-md text-gray-400 hover:text-indigo-600 hover:bg-indigo-100"
                  title="Edit Application"
                >
                  <Edit class="h-4 w-4" />
                </button>
                <button
                  @click.stop="duplicateApplication(application)"
                  class="p-1 rounded-md text-gray-400 hover:text-green-600 hover:bg-green-100"
                  title="Duplicate Application"
                >
                  <Copy class="h-4 w-4" />
                </button>
                <button
                  @click.stop="deleteApplication(application)"
                  class="p-1 rounded-md text-gray-400 hover:text-red-600 hover:bg-red-100"
                  title="Delete Application"
                >
                  <Trash2 class="h-4 w-4" />
                </button>
              </div>
            </div>
          </div>

          <!-- Sections Preview -->
          <div class="px-6 py-3 border-t border-gray-100">
            <div class="flex flex-wrap gap-1">
              <span 
                v-for="section in application.sections.slice(0, 3)" 
                :key="section.id"
                class="inline-flex items-center px-2 py-1 rounded text-xs font-medium bg-gray-100 text-gray-700"
              >
                {{ section.title }}
              </span>
              <span 
                v-if="application.sections.length > 3"
                class="inline-flex items-center px-2 py-1 rounded text-xs font-medium bg-gray-200 text-gray-600"
              >
                +{{ application.sections.length - 3 }} more
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- Empty State -->
      <div v-else class="text-center py-12">
        <FileText class="h-12 w-12 mx-auto text-gray-300 mb-4" />
        <h3 class="text-lg font-medium text-gray-900 mb-2">
          {{ searchQuery || selectedLayout || selectedAIFilter ? 'No applications found' : 'No applications yet' }}
        </h3>
        <p class="text-gray-500 mb-6">
          {{ searchQuery || selectedLayout || selectedAIFilter 
            ? 'Try adjusting your search criteria or filters.' 
            : 'Get started by creating your first insurance application type.' 
          }}
        </p>
        <router-link
          v-if="!searchQuery && !selectedLayout && !selectedAIFilter"
          to="/admin/applications/new"
          class="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700"
        >
          <Plus class="h-4 w-4 mr-2" />
          Create First Application
        </router-link>
      </div>
    </div>

    <!-- Application Detail Modal -->
    <ApplicationDetailModal
      :is-open="detailModalOpen"
      :application="selectedApplication"
      @close="closeDetailModal"
      @edit="editApplication"
      @duplicate="duplicateApplication"
      @delete="deleteApplication"
    />
  </AuthenticatedLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { 
  Plus, RefreshCw, Loader2, AlertCircle, FileText, Layers, Settings, Brain,
  Search, Filter, ChevronDown, Edit, Copy, Trash2
} from 'lucide-vue-next'
import AuthenticatedLayout from '@/layouts/AuthenticatedLayout.vue'
import ApplicationDetailModal from '@/components/modals/ApplicationDetailModal.vue'
import { ADMIN_NAVIGATION } from '@/lib/navigation'
import axios from '@/lib/axios'

const adminNavigation = ADMIN_NAVIGATION

// State
const applications = ref([])
const loading = ref(false)
const error = ref(null)

// Search and Filter State
const searchQuery = ref('')
const selectedLayout = ref('')
const selectedAIFilter = ref('')
const selectedSectionCount = ref('')
const selectedFieldCount = ref('')
const sortBy = ref('name')
const showAdvancedFilters = ref(false)

// Modal State
const detailModalOpen = ref(false)
const selectedApplication = ref(null)

// Computed Properties
const totalSections = computed(() => {
  return applications.value.reduce((total, app) => total + app.sections.length, 0)
})

const totalFields = computed(() => {
  return applications.value.reduce((total, app) => {
    return total + app.sections.reduce((sectionTotal, section) => {
      return sectionTotal + section.fields.length
    }, 0)
  }, 0)
})

const aiEnabledCount = computed(() => {
  return applications.value.filter(app => app.requiredAIAnalysis).length
})

const filteredApplications = computed(() => {
  let filtered = [...applications.value]

  // Search filter
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(app => 
      app.name.toLowerCase().includes(query) ||
      app.description?.toLowerCase().includes(query) ||
      app.sections.some(section => 
        section.title.toLowerCase().includes(query) ||
        section.fields.some(field => 
          field.label.toLowerCase().includes(query) ||
          field.fieldName.toLowerCase().includes(query)
        )
      )
    )
  }

  // Layout filter
  if (selectedLayout.value) {
    filtered = filtered.filter(app => app.layout === selectedLayout.value)
  }

  // AI Analysis filter
  if (selectedAIFilter.value !== '') {
    const aiFilter = selectedAIFilter.value === 'true'
    filtered = filtered.filter(app => app.requiredAIAnalysis === aiFilter)
  }

  // Section count filter
  if (selectedSectionCount.value) {
    filtered = filtered.filter(app => {
      const sectionCount = app.sections.length
      switch (selectedSectionCount.value) {
        case '1': return sectionCount === 1
        case '2': return sectionCount === 2
        case '3': return sectionCount >= 3
        default: return true
      }
    })
  }

  // Field count filter
  if (selectedFieldCount.value) {
    filtered = filtered.filter(app => {
      const fieldCount = getTotalFields(app)
      switch (selectedFieldCount.value) {
        case '1-5': return fieldCount >= 1 && fieldCount <= 5
        case '6-10': return fieldCount >= 6 && fieldCount <= 10
        case '11+': return fieldCount >= 11
        default: return true
      }
    })
  }

  // Sort
  filtered.sort((a, b) => {
    switch (sortBy.value) {
      case 'name':
        return a.name.localeCompare(b.name)
      case 'name-desc':
        return b.name.localeCompare(a.name)
      case 'sections':
        return b.sections.length - a.sections.length
      case 'fields':
        return getTotalFields(b) - getTotalFields(a)
      case 'recent':
        return b.id - a.id // Assuming higher ID means more recent
      default:
        return 0
    }
  })

  return filtered
})

// Methods
const fetchApplications = async () => {
  loading.value = true
  error.value = null
  
  try {
    const response = await axios.get('/api/v1/insurances', {
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        'X-Tenant-ID': 'pcic'
      },
      withCredentials: true,
    })
    applications.value = response.data
    console.log('Applications fetched:', response.data)
  } catch (err) {
    error.value = err.response?.data?.message || err.message || 'Failed to fetch applications'
    console.error('Error fetching applications:', err)
  } finally {
    loading.value = false
  }
}

const refreshApplications = () => {
  fetchApplications()
}

const getTotalFields = (application) => {
  return application.sections.reduce((total, section) => total + section.fields.length, 0)
}

const getRequiredFields = (application) => {
  return application.sections.reduce((total, section) => {
    return total + section.fields.filter(field => field.required).length
  }, 0)
}

const formatLayout = (layout) => {
  return layout.split('-').map(word => 
    word.charAt(0).toUpperCase() + word.slice(1)
  ).join(' ')
}

const clearFilters = () => {
  searchQuery.value = ''
  selectedLayout.value = ''
  selectedAIFilter.value = ''
  selectedSectionCount.value = ''
  selectedFieldCount.value = ''
  sortBy.value = 'name'
}

const viewApplication = (application) => {
  selectedApplication.value = application
  detailModalOpen.value = true
}

const closeDetailModal = () => {
  detailModalOpen.value = false
  selectedApplication.value = null
}

const editApplication = (application) => {
  // Navigate to edit page (you can implement this later)
  console.log('Edit application:', application.id)
  // router.push(`/admin/applications/${application.id}/edit`)
}

const duplicateApplication = async (application) => {
  if (confirm(`Are you sure you want to duplicate "${application.name}"?`)) {
    try {
      const duplicatedApp = {
        ...application,
        name: `${application.name} (Copy)`,
        sections: application.sections.map(section => ({
          title: section.title,
          fields: section.fields.map(field => ({
            key: field.key,
            fieldName: field.fieldName,
            label: field.label,
            fieldType: field.fieldType,
            choices: field.choices,
            required: field.required,
            defaultValue: field.defaultValue,
            validationRegex: field.validationRegex
          }))
        }))
      }
      
      delete duplicatedApp.id // Remove ID so it creates a new one
      
      const response = await axios.post('/api/v1/insurances', duplicatedApp, {
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json',
          'X-Tenant-ID': 'pcic'
        },
        withCredentials: true,
      })
      console.log('Application duplicated:', response.data)
      
      // Refresh the list
      await fetchApplications()
      alert('Application duplicated successfully!')
    } catch (err) {
      console.error('Error duplicating application:', err)
      alert('Failed to duplicate application. Please try again.')
    }
  }
}

const deleteApplication = async (application) => {
  if (confirm(`Are you sure you want to delete "${application.name}"? This action cannot be undone.`)) {
    try {
      await axios.delete(`/api/v1/insurances/${application.id}`, {
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json',
          'X-Tenant-ID': 'pcic'
        },
        withCredentials: true,
      })
      console.log('Application deleted:', application.id)
      
      // Remove from local state
      applications.value = applications.value.filter(app => app.id !== application.id)
      
      // Close modal if it's open
      if (detailModalOpen.value && selectedApplication.value?.id === application.id) {
        closeDetailModal()
      }
      
      alert('Application deleted successfully!')
    } catch (err) {
      console.error('Error deleting application:', err)
      alert('Failed to delete application. Please try again.')
    }
  }
}

// Initialize
onMounted(() => {
  fetchApplications()
})
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
