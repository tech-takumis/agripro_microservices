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

        <!-- Personal Information Section -->
        <div class="bg-white shadow-md rounded-xl p-6 border border-gray-200">
        <!-- Section Title -->
        <div class="flex items-center gap-3 pb-4 border-b border-gray-200">
            <User class="h-7 w-7 text-green-600" />
            <h2 class="text-lg font-semibold text-gray-900">Personal Information</h2>
        </div>

        <!-- Grid Layout -->
        <div class="grid grid-cols-1 md:grid-cols-2 gap-5 mt-5">

            <!-- Name Fields: 3 in One Row -->
        <div class="grid grid-cols-1 md:grid-cols-3 gap-5 md:col-span-2">

        <div class="flex items-start gap-3 bg-gray-50 rounded-lg p-3">
            <div>
            <p class="text-xs text-green-600">First Name</p>
            <p class="text-l font-medium text-gray-900">{{ fields.first_name }}</p>
            </div>
        </div>

        <div class="flex items-start gap-3 bg-gray-50 rounded-lg p-3">
            <div>
            <p class="text-xs text-green-600">Middle Name</p>
            <p class="text-l font-medium text-gray-900">{{ fields.middle_name }}</p>
            </div>
        </div>

        <div class="flex items-start gap-3 bg-gray-50 rounded-lg p-3">
            <div>
            <p class="text-xs text-green-600">Last Name</p>
            <p class="text-l font-medium text-gray-900">{{ fields.last_name }}</p>
            </div>
        </div>

        </div>

            <div class="flex items-start gap-3 bg-gray-50 rounded-lg p-3">
            <div>
                <p class="text-xs text-green-600">Date of Birth</p>
                <p class="text-l font-medium text-gray-900">{{ formatDate(fields.date_of_birth) }}</p>
            </div>
            </div>

            <div class="flex items-start gap-3 bg-gray-50 rounded-lg p-3">
            <div>
                <p class="text-xs text-green-600">Age</p>
                <p class="text-l font-medium text-gray-900">{{ fields.age }}</p>
            </div>
            </div>

            <div class="flex items-start gap-3 bg-gray-50 rounded-lg p-3">
            <div>
                <p class="text-xs text-green-600">Sex</p>
                <p class="text-l font-medium text-gray-900">{{ fields.sex }}</p>
            </div>
            </div>

            <div class="flex items-start gap-3 bg-gray-50 rounded-lg p-3">
            <div>
                <p class="text-xs text-green-600">Civil Status</p>
                <p class="text-l font-medium text-gray-900">{{ fields.civil_status }}</p>
            </div>
            </div>

            <div class="flex items-start gap-3 bg-gray-50 rounded-lg p-3">
            <div>
                <p class="text-xs text-green-600">Spouse Name</p>
                <p class="text-l font-medium text-gray-900">{{ fields.spouse_name }}</p>
            </div>
            </div>

            <div class="flex items-start gap-3 bg-gray-50 rounded-lg p-3">
            <div>
                <p class="text-xs text-green-600">Tribe</p>
                <p class="text-l font-medium text-gray-900">{{ fields.tribe }}</p>
            </div>
            </div>

            <div class="flex items-start gap-3 bg-gray-50 rounded-lg p-3">
            <div>
                <p class="text-xs text-green-600">Indigenous People</p>
                <p class="text-l font-medium text-gray-900">
                {{ fields.indigenous_people ? 'Yes' : 'No' }}
                </p>
            </div>
            </div>

            <div class="flex items-start gap-3 bg-gray-50 rounded-lg p-3">
            <div>
                <p class="text-xs text-green-600">Cell Phone Number</p>
                <p class="text-l font-medium text-gray-900">{{ fields.cell_phone_number }}</p>
            </div>
            </div>

            <!-- Address: Full Width -->
            <div class="flex items-start gap-3 bg-gray-50 rounded-lg p-3 md:col-span-2">
            <div>
                <p class="text-xs text-green-600">Address</p>
                <p class="text-l font-medium text-gray-900">{{ fields.address }}</p>
            </div>
            </div>

        </div>
        </div>

                <!-- Insurance Information Section -->
                <div class="bg-white shadow-md rounded-xl p-6 border border-gray-200 mt-6">
                <!-- Section Title -->
                <div class="pb-4 border-b border-gray-200 flex items-center gap-2">
                <FileText class="h-5 w-5 text-green-600" />
                <h2 class="text-lg font-semibold text-gray-900">Insurance Information</h2>
                </div>

                <!-- Details Grid -->
                <div class="grid grid-cols-1 md:grid-cols-2 gap-5 mt-5">

                    <div class="flex bg-gray-50 rounded-lg p-3">
                    <div>
                        <p class="text-xs text-green-600">Crop Type</p>
                        <p class="text-base font-medium text-gray-900">{{ fields.crop_type }}</p>
                    </div>
                    </div>

                    <div class="flex bg-gray-50 rounded-lg p-3">
                    <div>
                        <p class="text-xs text-green-600">Cover Type</p>
                        <p class="text-base font-medium text-gray-900">{{ fields.cover_type }}</p>
                    </div>
                    </div>

                    <div class="flex bg-gray-50 rounded-lg p-3">
                    <div>
                        <p class="text-xs text-green-600">Amount of Cover</p>
                        <p class="text-base font-medium text-gray-900">₱{{ formatAmount(fields.amount_of_cover) }}</p>
                    </div>
                    </div>

                    <div class="flex bg-gray-50 rounded-lg p-3">
                    <div>
                        <p class="text-xs text-green-600">Primary Beneficiary</p>
                        <p class="text-base font-medium text-gray-900">{{ fields.primary_beneficiary }}</p>
                    </div>
                    </div>

                    <div class="flex bg-gray-50 rounded-lg p-3">
                    <div>
                        <p class="text-xs text-green-600">Secondary Beneficiary</p>
                        <p class="text-base font-medium text-gray-900">{{ fields.secondary_beneficiary }}</p>
                    </div>
                    </div>
                </div>
                </div>


    <!-- Lot Information Section -->
<div class="bg-white shadow-md rounded-xl p-6 border border-gray-200">

  <!-- Section Title -->
  <div class="pb-4 border-b border-gray-200 flex items-center gap-2">
    <MapPin class="h-5 w-5 text-green-600" />
    <h2 class="text-lg font-semibold text-gray-900">Lot 1 Information</h2>
  </div>

  <!-- Main Fields -->
  <div class="grid grid-cols-1 md:grid-cols-2 gap-5 mt-5">

    <div class="bg-gray-50 rounded-lg p-3">
      <DetailField label="Area (hectares)" :value="fields.lot_1_area" />
    </div>

    <div class="bg-gray-50 rounded-lg p-3">
      <DetailField label="Variety" :value="fields.lot_1_variety" />
    </div>

    <div class="bg-gray-50 rounded-lg p-3">
      <DetailField label="Soil Type" :value="fields.lot_1_soil_type" />
    </div>

    <div class="bg-gray-50 rounded-lg p-3">
      <DetailField label="Topography" :value="fields.lot_1_topography" />
    </div>

    <div class="bg-gray-50 rounded-lg p-3">
      <DetailField label="Land Category" :value="fields.lot_1_land_category" />
    </div>

    <div class="bg-gray-50 rounded-lg p-3">
      <DetailField label="Tenurial Status" :value="fields.lot_1_tenurial_status" />
    </div>

    <div class="bg-gray-50 rounded-lg p-3">
      <DetailField label="Planting Method" :value="fields.lot_1_planting_method" />
    </div>

    <div class="bg-gray-50 rounded-lg p-3">
      <DetailField label="Irrigation Source" :value="fields.lot_1_irrigation_source" />
    </div>

    <div class="bg-gray-50 rounded-lg p-3">
      <DetailField label="Date of Sowing" :value="formatDate(fields.lot_1_date_sowing)" />
    </div>

    <div class="bg-gray-50 rounded-lg p-3">
      <DetailField label="Date of Planting" :value="formatDate(fields.lot_1_date_planting)" />
    </div>

    <div class="bg-gray-50 rounded-lg p-3">
      <DetailField label="Date of Harvest" :value="formatDate(fields.lot_1_date_harvest)" />
    </div>

  </div>

  <!-- Location -->
  <div class="mt-6">
    <h3 class="text-sm font-semibold text-green-700 mb-2">Location</h3>
    <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
      <div class="bg-gray-50 rounded-lg p-3">
        <DetailField label="Sitio" :value="fields.lot_1_location?.sitio" />
      </div>

      <div class="bg-gray-50 rounded-lg p-3">
        <DetailField label="Barangay" :value="fields.lot_1_location?.barangay" />
      </div>

      <div class="bg-gray-50 rounded-lg p-3">
        <DetailField label="Municipality/City"
                     :value="fields.lot_1_location?.municipality || fields.lot_1_location?.city" />
      </div>

      <div class="bg-gray-50 rounded-lg p-3">
        <DetailField label="Province" :value="fields.lot_1_location?.province" />
      </div>
    </div>
  </div>

  <!-- Boundaries -->
  <div class="mt-6">
    <h3 class="text-sm font-semibold text-green-700 mb-2">Boundaries</h3>
    <div class="grid grid-cols-2 md:grid-cols-4 gap-5">

      <div class="bg-gray-50 rounded-lg p-3">
        <DetailField label="North" :value="fields.lot_1_boundaries?.north" />
      </div>

      <div class="bg-gray-50 rounded-lg p-3">
        <DetailField label="South" :value="fields.lot_1_boundaries?.south" />
      </div>

      <div class="bg-gray-50 rounded-lg p-3">
        <DetailField label="East" :value="fields.lot_1_boundaries?.east" />
      </div>

      <div class="bg-gray-50 rounded-lg p-3">
        <DetailField label="West" :value="fields.lot_1_boundaries?.west" />
      </div>

    </div>
  </div>
</div>



            <!-- Farmer Signature section if it exists -->
            <div v-if="farmerSignatureDocId" class="bg-white shadow-sm rounded-lg p-6">
                <h2 class="text-lg font-semibold text-gray-900 mb-4 flex items-center gap-2">
                    <FileText class="h-5 w-5" />
                    Farmer Signature
                </h2>
                <div class="border border-gray-200 rounded-lg overflow-hidden inline-block">
                    <img
                        :src="farmerSignatureDocId"
                        alt="Farmer Signature"
                        class="max-w-md h-auto"
                        @error="handleImageError"
                    />
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
                    <DetailField label="Application Name" :value="application.applicationName" class="md:col-span-2" />
                    <DetailField label="Batch" :value="application.batchName" class="md:col-span-2" />
                    <DetailField label="Submitted At" :value="formatDateTime(application.submittedAt)" />
                    <DetailField label="Updated At" :value="formatDateTime(application.updatedAt)" />
                </div>
            </div>
        </div>
    </AuthenticatedLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {useApplicationStore} from '@/stores/applications'
import { useAuthStore } from '@/stores/auth'
import AuthenticatedLayout from '@/layouts/AuthenticatedLayout.vue'
import DetailField from '@/components/tables/DetailField.vue'
import { ArrowLeft, Edit, Trash2, User, FileText, MapPin, Calendar } from 'lucide-vue-next'
import {
    ADMIN_NAVIGATION,
    MUNICIPAL_AGRICULTURIST_NAVIGATION,
    AGRICULTURAL_EXTENSION_WORKER_NAVIGATION
} from '@/lib/navigation'

const router = useRouter()
const route = useRoute()
const { fetchApplicationById, deleteApplication, updateApplication } = useApplicationStore()
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
    const role = authStore.userData?.roles?.[0].name
    return role || 'Staff Portal'
})

const fields = computed(() => application.value?.dynamicFields || {})

const farmerSignatureDocId = computed(() => {
    return application.value?.fileUploads?.[0] || null
})


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

const handleImageError = (event) => {
    event.target.src = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="200" height="200"%3E%3Crect fill="%23ddd" width="200" height="200"/%3E%3Ctext fill="%23999" x="50%25" y="50%25" text-anchor="middle" dy=".3em"%3EImage not found%3C/text%3E%3C/svg%3E'
}

const handleEdit = () => {
    // Navigate to edit page (to be implemented)
    console.log('Edit application:', application.value.id)
}

// Methods
const fetchApplication = async () => {
    loading.value = true
    error.value = null

    const result = await fetchApplicationById(route.params.id)

    if (result.success) {
        application.value = result.data
    } else {
        error.value = result.error?.message || 'Failed to fetch application'
    }

    loading.value = false
}

const handleDelete = async () => {
    if (!confirm('Are you sure you want to delete this application?')) {
        return
    }

    loading.value = true
    const result = await deleteApplication(application.value.id)

    if (result.success) {
        await router.push({ name: 'applications-list' })
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
