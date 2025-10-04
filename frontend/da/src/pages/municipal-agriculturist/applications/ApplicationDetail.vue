<template>
    <AuthenticatedLayout
        :navigation="navigation"
        :role-title="roleTitle"
        page-title="Application Detail"
    >
        <template #header>
            <div class="flex items-center justify-between">
                <div class="flex items-center gap-4">
                    <button
                        @click="router.back()"
                        class="p-2 hover:bg-gray-100 rounded-lg transition-colors"
                    >
                        <ArrowLeft class="h-5 w-5 text-gray-600" />
                    </button>
                    <h1 class="text-2xl font-semibold text-gray-900">Application Details</h1>
                </div>
                <div class="flex items-center gap-3">
                    <button
                        @click="handleEdit"
                        class="inline-flex items-center px-4 py-2 border border-gray-300 rounded-lg text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
                    >
                        <Edit class="h-4 w-4 mr-2" />
                        Edit
                    </button>
                    <button
                        @click="handleDelete"
                        class="inline-flex items-center px-4 py-2 border border-transparent rounded-lg text-sm font-medium text-white bg-red-600 hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500"
                    >
                        <Trash2 class="h-4 w-4 mr-2" />
                        Delete
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

        <!-- Application details -->
        <div v-else-if="application" class="space-y-6">
            <!-- Personal Information -->
            <div class="bg-white shadow-sm rounded-lg p-6">
                <h2 class="text-lg font-semibold text-gray-900 mb-4 flex items-center gap-2">
                    <User class="h-5 w-5" />
                    Personal Information
                </h2>
                <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <DetailField label="First Name" :value="fields.first_name" />
                    <DetailField label="Middle Name" :value="fields.middle_name" />
                    <DetailField label="Last Name" :value="fields.last_name" />
                    <DetailField label="Date of Birth" :value="formatDate(fields.date_of_birth)" />
                    <DetailField label="Age" :value="fields.age" />
                    <DetailField label="Sex" :value="fields.sex" />
                    <DetailField label="Civil Status" :value="fields.civil_status" />
                    <DetailField label="Spouse Name" :value="fields.spouse_name" />
                    <DetailField label="Tribe" :value="fields.tribe" />
                    <DetailField label="Indigenous People" :value="fields.indigenous_people ? 'Yes' : 'No'" />
                    <DetailField label="Cell Phone Number" :value="fields.cell_phone_number" />
                    <DetailField label="Address" :value="fields.address" class="md:col-span-2" />
                </div>
            </div>

            <!-- Insurance Information -->
            <div class="bg-white shadow-sm rounded-lg p-6">
                <h2 class="text-lg font-semibold text-gray-900 mb-4 flex items-center gap-2">
                    <FileText class="h-5 w-5" />
                    Insurance Information
                </h2>
                <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <DetailField label="Crop Type" :value="fields.crop_type" />
                    <DetailField label="Cover Type" :value="fields.cover_type" />
                    <DetailField label="Amount of Cover" :value="`₱${formatAmount(fields.amount_of_cover)}`" />
                    <DetailField label="Primary Beneficiary" :value="fields.primary_beneficiary" />
                    <DetailField label="Secondary Beneficiary" :value="fields.secondary_beneficiary" />
                </div>
            </div>

            <!-- Lot Information -->
            <div class="bg-white shadow-sm rounded-lg p-6">
                <h2 class="text-lg font-semibold text-gray-900 mb-4 flex items-center gap-2">
                    <MapPin class="h-5 w-5" />
                    Lot 1 Information
                </h2>
                <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <DetailField label="Area (hectares)" :value="fields.lot_1_area" />
                    <DetailField label="Variety" :value="fields.lot_1_variety" />
                    <DetailField label="Soil Type" :value="fields.lot_1_soil_type" />
                    <DetailField label="Topography" :value="fields.lot_1_topography" />
                    <DetailField label="Land Category" :value="fields.lot_1_land_category" />
                    <DetailField label="Tenurial Status" :value="fields.lot_1_tenurial_status" />
                    <DetailField label="Planting Method" :value="fields.lot_1_planting_method" />
                    <DetailField label="Irrigation Source" :value="fields.lot_1_irrigation_source" />
                    <DetailField label="Date of Sowing" :value="formatDate(fields.lot_1_date_sowing)" />
                    <DetailField label="Date of Planting" :value="formatDate(fields.lot_1_date_planting)" />
                    <DetailField label="Date of Harvest" :value="formatDate(fields.lot_1_date_harvest)" />
                </div>

                <!-- Location -->
                <div class="mt-4">
                    <h3 class="text-sm font-medium text-gray-700 mb-2">Location</h3>
                    <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                        <DetailField label="Sitio" :value="fields.lot_1_location?.sitio" />
                        <DetailField label="Barangay" :value="fields.lot_1_location?.barangay" />
                        <DetailField label="Municipality/City" :value="fields.lot_1_location?.municipality || fields.lot_1_location?.city" />
                        <DetailField label="Province" :value="fields.lot_1_location?.province" />
                        <DetailField label="Region" :value="fields.lot_1_location?.region" />
                    </div>
                </div>

                <!-- Boundaries -->
                <div class="mt-4">
                    <h3 class="text-sm font-medium text-gray-700 mb-2">Boundaries</h3>
                    <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
                        <DetailField label="North" :value="fields.lot_1_boundaries?.north" />
                        <DetailField label="South" :value="fields.lot_1_boundaries?.south" />
                        <DetailField label="East" :value="fields.lot_1_boundaries?.east" />
                        <DetailField label="West" :value="fields.lot_1_boundaries?.west" />
                    </div>
                </div>
            </div>

            <!-- Submission Information -->
            <div class="bg-white shadow-sm rounded-lg p-6">
                <h2 class="text-lg font-semibold text-gray-900 mb-4 flex items-center gap-2">
                    <Calendar class="h-5 w-5" />
                    Submission Information
                </h2>
                <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <DetailField label="Application ID" :value="application.id" class="md:col-span-2" />
                    <DetailField label="Submitted At" :value="formatDateTime(application.submittedAt)" />
                    <DetailField label="Updated At" :value="formatDateTime(application.updatedAt)" />
                    <DetailField label="Version" :value="application.version" />
                </div>
            </div>
        </div>
    </AuthenticatedLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useApplicationStore } from '@/stores/application'
import { useAuthStore } from '@/stores/auth'
import AuthenticatedLayout from '@/layouts/AuthenticatedLayout.vue'
import DetailField from '@/components/tables/DetailField.vue'
import { ArrowLeft, Edit, Trash2, User, FileText, MapPin, Calendar } from 'lucide-vue-next'
import {
    ADMIN_NAVIGATION,
    MUNICIPAL_AGRICULTURIST_NAVIGATION,
    AGRICULTURAL_EXTENSION_WORKER_NAVIGATION
} from '@/lib/constants'

const router = useRouter()
const route = useRoute()
const applicationStore = useApplicationStore()
const authStore = useAuthStore()

// State
const loading = ref(false)
const error = ref(null)
const application = ref(null)

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

const fields = computed(() => application.value?.dynamicFields || {})

// Methods
const fetchApplication = async () => {
    loading.value = true
    error.value = null

    const result = await applicationStore.fetchApplicationById(route.params.id)

    if (result.success) {
        application.value = result.data
    } else {
        error.value = result.error?.message || 'Failed to fetch application'
    }

    loading.value = false
}

const formatDate = (dateString) => {
    if (!dateString) return 'N/A'
    return new Date(dateString).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    })
}

const formatDateTime = (dateString) => {
    if (!dateString) return 'N/A'
    return new Date(dateString).toLocaleString('en-US', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    })
}

const formatAmount = (amount) => {
    return new Intl.NumberFormat('en-PH').format(amount)
}

const handleEdit = () => {
    // Navigate to edit page (to be implemented)
    console.log('Edit application:', application.value.id)
}

const handleDelete = async () => {
    if (!confirm('Are you sure you want to delete this application?')) {
        return
    }

    loading.value = true
    const result = await applicationStore.deleteApplication(application.value.id)

    if (result.success) {
        router.push({ name: 'applications-list' })
    } else {
        error.value = result.error?.message || 'Failed to delete application'
        loading.value = false
    }
}

// Lifecycle
onMounted(() => {
    fetchApplication()
})
</script>

