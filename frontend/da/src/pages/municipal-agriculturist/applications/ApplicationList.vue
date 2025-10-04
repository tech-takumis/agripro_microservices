<template>
    <AuthenticatedLayout
        :navigation="navigation"
        :role-title="roleTitle"
        page-title="Applications"
    >
        <template #header>
            <div class="flex items-center justify-between">
                <h1 class="text-2xl font-semibold text-gray-900">Farmer Applications</h1>
                <div class="flex items-center gap-3">
                    <!-- Action buttons (shown when checkboxes are selected) -->
                    <div v-if="selectedApplications.length > 0" class="flex items-center gap-2">
                        <button
                            v-if="selectedApplications.length === 1"
                            @click="handleUpdate"
                            class="inline-flex items-center px-4 py-2 border border-gray-300 rounded-lg text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
                        >
                            <Edit class="h-4 w-4 mr-2" />
                            Update
                        </button>
                        <button
                            @click="handleDelete"
                            class="inline-flex items-center px-4 py-2 border border-transparent rounded-lg text-sm font-medium text-white bg-red-600 hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500"
                        >
                            <Trash2 class="h-4 w-4 mr-2" />
                            Delete ({{ selectedApplications.length }})
                        </button>
                    </div>

                    <!-- Filter button -->
                    <button
                        @click="showFilterModal = true"
                        class="inline-flex items-center px-4 py-2 border border-gray-300 rounded-lg text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
                    >
                        <Filter class="h-4 w-4 mr-2" />
                        Filter
                    </button>
                </div>
            </div>
        </template>

        <!-- Loading state -->
        <div v-if="loading" class="flex items-center justify-center py-12">
            <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
        </div>

        <!-- Error state -->
        <div v-else-if="error" class="bg-red-50 border border-red-200 rounded-lg p-4">
            <p class="text-red-800">{{ error }}</p>
        </div>

        <!-- Applications table -->
        <div v-else class="bg-white shadow-sm rounded-lg overflow-hidden">
            <div class="overflow-x-auto">
                <table class="min-w-full divide-y divide-gray-200">
                    <thead class="bg-gray-50">
                    <tr>
                        <th scope="col" class="w-12 px-6 py-3">
                            <input
                                type="checkbox"
                                :checked="isAllSelected"
                                @change="toggleSelectAll"
                                class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
                            />
                        </th>
                        <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            Farmer Name
                        </th>
                        <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            Crop Type
                        </th>
                        <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            Cover Type
                        </th>
                        <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            Amount
                        </th>
                        <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            Location
                        </th>
                        <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                            Submitted Date
                        </th>
                    </tr>
                    </thead>
                    <tbody class="bg-white divide-y divide-gray-200">
                    <tr
                        v-for="application in filteredApplications"
                        :key="application.id"
                        @click="handleRowClick(application.id, $event)"
                        class="hover:bg-gray-50 cursor-pointer transition-colors"
                    >
                        <td class="px-6 py-4 whitespace-nowrap" @click.stop>
                            <input
                                type="checkbox"
                                :checked="isSelected(application.id)"
                                @change="toggleSelection(application.id)"
                                class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
                            />
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap">
                            <div class="text-sm font-medium text-gray-900">
                                {{ getFullName(application.dynamicFields) }}
                            </div>
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap">
                            <div class="text-sm text-gray-900">{{ application.dynamicFields.crop_type }}</div>
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap">
                            <div class="text-sm text-gray-900">{{ application.dynamicFields.cover_type }}</div>
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap">
                            <div class="text-sm text-gray-900">₱{{ formatAmount(application.dynamicFields.amount_of_cover) }}</div>
                        </td>
                        <td class="px-6 py-4">
                            <div class="text-sm text-gray-900">{{ getLocation(application.dynamicFields.lot_1_location) }}</div>
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap">
                            <div class="text-sm text-gray-900">{{ formatDate(application.submittedAt) }}</div>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>

            <!-- Empty state -->
            <div v-if="filteredApplications.length === 0" class="text-center py-12">
                <FileText class="mx-auto h-12 w-12 text-gray-400" />
                <h3 class="mt-2 text-sm font-medium text-gray-900">No applications found</h3>
                <p class="mt-1 text-sm text-gray-500">No farmer applications match your current filters.</p>
            </div>
        </div>

        <!-- Filter Modal -->
        <ApplicationFilterModal
            v-model:show="showFilterModal"
            :filters="filters"
            @apply-filters="applyFilters"
            @reset-filters="resetFilters"
        />
    </AuthenticatedLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useApplicationStore } from '@/stores/application'
import { useAuthStore } from '@/stores/auth'
import AuthenticatedLayout from '@/layouts/AuthenticatedLayout.vue'
import ApplicationFilterModal from '@/components/modals/ApplicationFilterModal.vue'
import { Filter, Edit, Trash2, FileText } from 'lucide-vue-next'
import {
    ADMIN_NAVIGATION,
    MUNICIPAL_AGRICULTURIST_NAVIGATION,
    AGRICULTURAL_EXTENSION_WORKER_NAVIGATION
} from '@/lib/constants'

const router = useRouter()
const applicationStore = useApplicationStore()
const authStore = useAuthStore()

// State
const loading = ref(false)
const error = ref(null)
const selectedApplications = ref([])
const showFilterModal = ref(false)
const filters = ref({
    cropType: '',
    coverType: '',
    location: '',
    dateFrom: '',
    dateTo: '',
    amountMin: '',
    amountMax: ''
})

// Computed
const navigation = computed(() => {
    const role = authStore.userData?.roles?.[0]
    if (role === 'Admin') return ADMIN_NAVIGATION
    if (role === 'Municipal Agriculturists') return MUNICIPAL_AGRICULTURIST_NAVIGATION
    if (role === 'Agricultural Extension Workers') return AGRICULTURAL_EXTENSION_WORKER_NAVIGATION
    return []
})

const roleTitle = computed(() => {
    const role = authStore.userData?.roles?.[0]
    return role || 'Staff Portal'
})

const filteredApplications = computed(() => {
    let apps = applicationStore.applications

    // Apply filters
    if (filters.value.cropType) {
        apps = apps.filter(app => app.dynamicFields.crop_type === filters.value.cropType)
    }
    if (filters.value.coverType) {
        apps = apps.filter(app => app.dynamicFields.cover_type === filters.value.coverType)
    }
    if (filters.value.location) {
        apps = apps.filter(app => {
            const location = getLocation(app.dynamicFields.lot_1_location).toLowerCase()
            return location.includes(filters.value.location.toLowerCase())
        })
    }
    if (filters.value.dateFrom) {
        apps = apps.filter(app => new Date(app.submittedAt) >= new Date(filters.value.dateFrom))
    }
    if (filters.value.dateTo) {
        apps = apps.filter(app => new Date(app.submittedAt) <= new Date(filters.value.dateTo))
    }
    if (filters.value.amountMin) {
        apps = apps.filter(app => app.dynamicFields.amount_of_cover >= parseFloat(filters.value.amountMin))
    }
    if (filters.value.amountMax) {
        apps = apps.filter(app => app.dynamicFields.amount_of_cover <= parseFloat(filters.value.amountMax))
    }

    return apps
})

const isAllSelected = computed(() => {
    return filteredApplications.value.length > 0 &&
        selectedApplications.value.length === filteredApplications.value.length
})

// Methods
const fetchApplications = async () => {
    loading.value = true
    error.value = null

    const result = await applicationStore.fetchAgricultureApplications()

    if (!result.success) {
        error.value = result.error?.message || 'Failed to fetch applications'
    }

    loading.value = false
}

const getFullName = (fields) => {
    const parts = [fields.first_name, fields.middle_name, fields.last_name].filter(Boolean)
    return parts.join(' ')
}

const getLocation = (location) => {
    if (!location) return 'N/A'
    const parts = [
        location.barangay,
        location.municipality || location.city,
        location.province
    ].filter(Boolean)
    return parts.join(', ')
}

const formatAmount = (amount) => {
    return new Intl.NumberFormat('en-PH').format(amount)
}

const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    })
}

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

const handleRowClick = (id, event) => {
    // Don't navigate if clicking on checkbox
    if (event.target.type === 'checkbox') return

    router.push({ name: 'municipal-agriculturist-submit-crop-data-detail', params: { id } })
}

const handleUpdate = () => {
    if (selectedApplications.value.length === 1) {
        router.push({ name: 'municipal-agriculturist-submit-crop-data-detail', params: { id: selectedApplications.value[0] } })
    }
}

const handleDelete = async () => {
    if (!confirm(`Are you sure you want to delete ${selectedApplications.value.length} application(s)?`)) {
        return
    }

    loading.value = true

    for (const id of selectedApplications.value) {
        await applicationStore.deleteApplication(id)
    }

    selectedApplications.value = []
    await fetchApplications()
}

const applyFilters = (newFilters) => {
    filters.value = { ...newFilters }
    showFilterModal.value = false
}

const resetFilters = () => {
    filters.value = {
        cropType: '',
        coverType: '',
        location: '',
        dateFrom: '',
        dateTo: '',
        amountMin: '',
        amountMax: ''
    }
    showFilterModal.value = false
}

// Lifecycle
onMounted(() => {
    fetchApplications()
})
</script>

