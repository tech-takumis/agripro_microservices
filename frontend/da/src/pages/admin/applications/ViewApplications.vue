<template>
  <AuthenticatedLayout 
    :navigation="adminNavigation" 
    role-title="Admin Portal"
    page-title="All Application Types"
  >
    <div class="h-full flex flex-col overflow-hidden">
      <!-- Header with Search and Actions -->
      <div class="flex-shrink-0 flex items-center justify-between gap-4 mb-4">
        <div class="flex-1 flex items-center gap-3">
          <div class="relative flex-1 max-w-md">
            <Search class="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
            <input
              v-model="searchQuery"
              type="text"
              placeholder="Search by name, description..."
              class="w-full pl-10 pr-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
            />
          </div>
          <button
            @click="filterModalOpen = true"
            class="inline-flex items-center px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50"
          >
            <Filter class="h-4 w-4 mr-2" />
            Filters
          </button>
        </div>
        <div class="flex items-center gap-3">
          <!-- Conditional Action Buttons -->
          <button
            v-if="selectedApplications.length === 1"
            @click="editSelectedApplication"
            class="inline-flex items-center px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50"
          >
            <Edit class="h-4 w-4 mr-2" />
            Edit
          </button>
          <button
            v-if="selectedApplications.length > 0"
            @click="deleteSelectedApplications"
            class="inline-flex items-center px-4 py-2 border border-red-300 rounded-md shadow-sm text-sm font-medium text-red-700 bg-white hover:bg-red-50"
          >
            <Trash2 class="h-4 w-4 mr-2" />
            Delete ({{ selectedApplications.length }})
          </button>
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

      <!-- Applications Table -->
      <div class="flex-1 bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden flex flex-col">
        <!-- Loading State -->
        <div v-if="loading" class="flex-1 flex items-center justify-center">
          <div class="text-center">
            <Loader2 class="h-8 w-8 animate-spin mx-auto text-gray-400" />
            <p class="mt-2 text-gray-500">Loading applications...</p>
          </div>
        </div>

        <!-- Error State -->
        <div v-else-if="error" class="flex-1 flex items-center justify-center p-6">
          <div class="bg-red-50 border border-red-200 rounded-md p-4 max-w-md">
            <div class="flex">
              <AlertCircle class="h-5 w-5 text-red-400" />
              <div class="ml-3">
                <h3 class="text-sm font-medium text-red-800">Error</h3>
                <p class="mt-1 text-sm text-red-700">{{ error }}</p>
              </div>
            </div>
          </div>
        </div>

        <!-- Applications Table -->
        <div v-else-if="filteredApplications.length > 0" class="flex-1 overflow-auto">
          <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50 sticky top-0">
              <tr>
                <th scope="col" class="px-6 py-3 text-left">
                  <input
                    type="checkbox"
                    :checked="isAllSelected"
                    @change="toggleSelectAll"
                    class="h-4 w-4 text-indigo-600 border-gray-300 rounded focus:ring-indigo-500"
                  />
                </th>
                <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Name
                </th>
                <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Description
                </th>
                <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Layout
                </th>
                <th scope="col" class="px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Sections
                </th>
                <th scope="col" class="px-6 py-3 text-center text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Fields
                </th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              <tr 
                v-for="application in filteredApplications" 
                :key="application.id"
                :class="[
                  'hover:bg-gray-50 cursor-pointer transition-colors',
                  isSelected(application.id) ? 'bg-indigo-50' : ''
                ]"
                @click="viewApplication(application)"
              >
                <td class="px-6 py-4 whitespace-nowrap" @click.stop>
                  <input
                    type="checkbox"
                    :checked="isSelected(application.id)"
                    @change="toggleSelection(application.id)"
                    class="h-4 w-4 text-indigo-600 border-gray-300 rounded focus:ring-indigo-500"
                  />
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="text-sm font-medium text-gray-900">{{ application.name }}</div>
                </td>
                <td class="px-6 py-4">
                  <div class="text-sm text-gray-500 max-w-xs truncate">
                    {{ application.description || 'No description' }}
                  </div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                    {{ formatLayout(application.layout) }}
                  </span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-center">
                  <span class="text-sm font-semibold text-gray-900">{{ application.sections.length }}</span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-center">
                  <span class="text-sm font-semibold text-gray-900">{{ getTotalFields(application) }}</span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Empty State -->
        <div v-else class="flex-1 flex items-center justify-center">
          <div class="text-center py-12">
            <FileText class="h-12 w-12 mx-auto text-gray-300 mb-4" />
            <h3 class="text-lg font-medium text-gray-900 mb-2">
              {{ searchQuery ? 'No applications found' : 'No applications yet' }}
            </h3>
            <p class="text-gray-500 mb-6">
              {{ searchQuery 
                ? 'Try adjusting your search criteria.' 
                : 'Get started by creating your first application type.' 
              }}
            </p>
            <router-link
              v-if="!searchQuery"
              to="/admin/applications/new"
              class="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700"
            >
              <Plus class="h-4 w-4 mr-2" />
              Create First Application
            </router-link>
          </div>
        </div>
      </div>
    </div>

    <!-- Filter Modal -->
    <FilterModal
      :is-open="filterModalOpen"
      :selected-layout="selectedLayout"
      :selected-section-count="selectedSectionCount"
      :selected-field-count="selectedFieldCount"
      :sort-by="sortBy"
      @close="filterModalOpen = false"
      @apply="applyFilters"
      @clear="clearFilters"
    />

    <!-- Application Detail Modal -->
    <ApplicationDetailModal
      :is-open="detailModalOpen"
      :application="selectedApplication"
      @close="closeDetailModal"
      @edit="editApplication"
      @delete="deleteApplication"
    />
  </AuthenticatedLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { 
  Plus, RefreshCw, Loader2, AlertCircle, FileText,
  Search, Filter, Edit, Copy, Trash2
} from 'lucide-vue-next'
import AuthenticatedLayout from '@/layouts/AuthenticatedLayout.vue'
import ApplicationDetailModal from '@/components/modals/ApplicationDetailModal.vue'
import FilterModal from '@/components/modals/FilterModal.vue'
import { ADMIN_NAVIGATION } from '@/lib/navigation'
import { useApplicationTypeStore } from '@/stores/applications'

const router = useRouter()
const applicationTypeStore = useApplicationTypeStore()

const adminNavigation = ADMIN_NAVIGATION

// State - use store state
const applications = computed(() => applicationTypeStore.applicationTypes)
const loading = computed(() => applicationTypeStore.loading)
const error = computed(() => applicationTypeStore.error)

// Search and Filter State
const searchQuery = ref('')
const selectedLayout = ref('')
const selectedSectionCount = ref('')
const selectedFieldCount = ref('')
const sortBy = ref('name')
const filterModalOpen = ref(false)

// Selection State
const selectedApplications = ref([])

// Modal State
const detailModalOpen = ref(false)
const selectedApplication = ref(null)


// Selection Computed
const isAllSelected = computed(() => {
  return filteredApplications.value.length > 0 && 
         selectedApplications.value.length === filteredApplications.value.length
})

const filteredApplications = computed(() => {
  let filtered = [...applications.value]

  // Search filter
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(app => 
      app.name.toLowerCase().includes(query) ||
      app.description?.toLowerCase().includes(query)
    )
  }

  // Layout filter
  if (selectedLayout.value) {
    filtered = filtered.filter(app => app.layout === selectedLayout.value)
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
        return new Date(b.createdAt || 0) - new Date(a.createdAt || 0)
      default:
        return 0
    }
  })

  return filtered
})

// Methods
const fetchApplications = async () => {
  const result = await applicationTypeStore.fetchApplicationTypes()
  
  if (result.success) {
    console.log('Application types fetched:', result.data)
  } else {
    console.error('Error fetching application types:', result.error)
  }
}

const refreshApplications = () => {
  fetchApplications()
}

const getTotalFields = (application) => {
  return application.sections.reduce((total, section) => total + section.fields.length, 0)
}

const formatLayout = (layout) => {
  return layout.split('-').map(word => 
    word.charAt(0).toUpperCase() + word.slice(1)
  ).join(' ')
}

const applyFilters = (filters) => {
  selectedLayout.value = filters.layout
  selectedSectionCount.value = filters.sectionCount
  selectedFieldCount.value = filters.fieldCount
  sortBy.value = filters.sortBy
  filterModalOpen.value = false
}

const clearFilters = () => {
  selectedLayout.value = ''
  selectedSectionCount.value = ''
  selectedFieldCount.value = ''
  sortBy.value = 'name'
  filterModalOpen.value = false
}

// Selection Methods
const isSelected = (id) => {
  return selectedApplications.value.includes(id)
}

const toggleSelection = (id) => {
  const index = selectedApplications.value.indexOf(id)
  if (index > -1) {
    selectedApplications.value.splice(index, 1)
  } else {
    selectedApplications.value.push(id)
  }
}

const toggleSelectAll = () => {
  if (isAllSelected.value) {
    selectedApplications.value = []
  } else {
    selectedApplications.value = filteredApplications.value.map(app => app.id)
  }
}

const clearSelection = () => {
  selectedApplications.value = []
}

const viewApplication = (application) => {
  selectedApplication.value = application
  detailModalOpen.value = true
}

const closeDetailModal = () => {
  detailModalOpen.value = false
  selectedApplication.value = null
}

const editSelectedApplication = () => {
  if (selectedApplications.value.length === 1) {
    const application = applications.value.find(app => app.id === selectedApplications.value[0])
    if (application) {
      editApplication(application)
    }
  }
}

const editApplication = (application) => {
  // Navigate to edit page (you can implement this later)
  console.log('Edit application:', application.id)
  // router.push(`/admin/applications/${application.id}/edit`)
  closeDetailModal()
}

const duplicateApplication = async (application) => {
  if (confirm(`Are you sure you want to duplicate "${application.name}"?`)) {
    const duplicatedApp = {
      name: `${application.name} (Copy)`,
      description: application.description,
      providerName: 'Agriculture',
      layout: application.layout,
      sections: application.sections.map(section => ({
        title: section.title,
        fields: section.fields.map(field => ({
          key: field.key,
          fieldName: field.fieldName,
          fieldType: field.fieldType,
          required: field.required,
          defaultValue: field.defaultValue,
          choices: field.choices,
          validationRegex: field.validationRegex
        }))
      }))
    }
    
    const result = await applicationTypeStore.createApplicationType(duplicatedApp)
    
    if (result.success) {
      console.log('Application type duplicated:', result.data)
      await fetchApplications()
      alert('Application type duplicated successfully!')
    } else {
      console.error('Error duplicating application type:', result.error)
      alert('Failed to duplicate application type. Please try again.')
    }
  }
}

const deleteSelectedApplications = async () => {
  const count = selectedApplications.value.length
  const appNames = selectedApplications.value
    .map(id => applications.value.find(app => app.id === id)?.name)
    .filter(Boolean)
    .join(', ')
  
  if (confirm(`Are you sure you want to delete ${count} application type(s)? (${appNames})\n\nThis action cannot be undone.`)) {
    let successCount = 0
    let failCount = 0
    
    for (const id of selectedApplications.value) {
      const result = await applicationTypeStore.deleteApplicationType(id)
      if (result.success) {
        successCount++
      } else {
        failCount++
        console.error('Error deleting application type:', id, result.error)
      }
    }
    
    clearSelection()
    
    if (failCount === 0) {
      alert(`Successfully deleted ${successCount} application type(s)!`)
    } else {
      alert(`Deleted ${successCount} application type(s). Failed to delete ${failCount}.`)
    }
  }
}

const deleteApplication = async (application) => {
  if (confirm(`Are you sure you want to delete "${application.name}"? This action cannot be undone.`)) {
    const result = await applicationTypeStore.deleteApplicationType(application.id)
    
    if (result.success) {
      console.log('Application type deleted:', application.id)
      
      // Close modal if it's open
      if (detailModalOpen.value && selectedApplication.value?.id === application.id) {
        closeDetailModal()
      }
      
      // Remove from selection if selected
      const index = selectedApplications.value.indexOf(application.id)
      if (index > -1) {
        selectedApplications.value.splice(index, 1)
      }
      
      alert('Application type deleted successfully!')
    } else {
      console.error('Error deleting application type:', result.error)
      alert('Failed to delete application type. Please try again.')
    }
  }
}

// Initialize
onMounted(() => {
  fetchApplications()
})
</script>
