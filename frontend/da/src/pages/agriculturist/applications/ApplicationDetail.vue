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
                        class="p-2 hover:bg-gray-100 rounded-lg transition-colors"
                        @click="router.back()"
                    >
                        <ArrowLeft class="h-5 w-5 text-gray-600" />
                    </button>
                    <h1 class="text-2xl font-semibold text-gray-900">Application Details</h1>
                </div>
                <div class="flex items-center gap-3">
                    <button
                        class="inline-flex items-center px-4 py-2 border border-gray-300 rounded-lg text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
                        @click="handleEdit"
                    >
                        <Edit class="h-4 w-4 mr-2" />
                        Edit
                    </button>
                    <button
                        class="inline-flex items-center px-4 py-2 border border-transparent rounded-lg text-sm font-medium text-white bg-red-600 hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500"
                        @click="handleDelete"
                    >
                        <Trash2 class="h-4 w-4 mr-2" />
                        Delete
                    </button>
                </div>
            </div>
        </template>

        <div class="overflow-y-auto max-h-[calc(200vh-8rem)] px-6 pb-10">
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
            <User class="h-5 w-5 text-green-600" />
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

                    <div class="flex bg-gray-50 rounded-lg p-3">
                    <div>
                        <p class="text-xs text-green-600">Cover Type</p>
                        <p class="text-l font-medium text-gray-900">{{ fields.lot_1_area}}</p>
                    </div>
                    </div>
                    <div class="flex bg-gray-50 rounded-lg p-3">
                    <div>
                        <p class="text-xs text-green-600">Cover Type</p>
                        <p class="text-l font-medium text-gray-900">{{ fields.lot_1_variety}}</p>
                    </div>
                    </div>
                    <div class="flex bg-gray-50 rounded-lg p-3">
                    <div>
                        <p class="text-xs text-green-600">Cover Type</p>
                        <p class="text-l font-medium text-gray-900">{{ fields.lot_1_soil_type}}</p>
                    </div>
                    </div>
                    <div class="flex bg-gray-50 rounded-lg p-3">
                    <div>
                        <p class="text-xs text-green-600">Cover Type</p>
                        <p class="text-l font-medium text-gray-900">{{ fields.lot_1_topography}}</p>
                    </div>
                    </div>
                    <div class="flex bg-gray-50 rounded-lg p-3">
                    <div>
                        <p class="text-xs text-green-600">Cover Type</p>
                        <p class="text-l font-medium text-gray-900">{{ fields.lot_1_land_category}}</p>
                    </div>
                    </div>
                    <div class="flex bg-gray-50 rounded-lg p-3">
                    <div>
                        <p class="text-xs text-green-600">Cover Type</p>
                        <p class="text-l font-medium text-gray-900">{{ fields.lot_1_tenurial_status}}</p>
                    </div>
                    </div>
                                        <div class="flex bg-gray-50 rounded-lg p-3">
                    <div>
                        <p class="text-xs text-green-600">Cover Type</p>
                        <p class="text-l font-medium text-gray-900">{{ fields.lot_1_planting_method}}</p>
                    </div>
                    </div>
                    <div class="flex bg-gray-50 rounded-lg p-3">
                    <div>
                        <p class="text-xs text-green-600">Irrigation Source</p>
                        <p class="text-l font-medium text-gray-900">{{ fields.lot_1_irrigation_source}}</p>
                    </div>
                    </div>
                    <div class="flex bg-gray-50 rounded-lg p-3">
                    <div>
                        <p class="text-xs text-green-600">Date of Sowing</p>
                        <p class="text-l font-medium text-gray-900">{{ fields.lot_1_date_sowing}}</p>
                    </div>
                    </div>
                    <div class="flex bg-gray-50 rounded-lg p-3">
                    <div>
                        <p class="text-xs text-green-600">Date of Planting</p>
                        <p class="text-l font-medium text-gray-900">{{ fields.lot_1_date_planting}}</p>
                    </div>
                    </div>
                    <div class="flex bg-gray-50 rounded-lg p-3">
                    <div>
                        <p class="text-xs text-green-600">Date of Harvest</p>
                        <p class="text-l font-medium text-gray-900">{{ fields.lot_1_date_harvest}}</p>
                    </div>
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
        <DetailField
label="Municipality/City"
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
<!-- Documents Section -->
<div v-if="application?.fileUploads && application.fileUploads.length > 0" class="bg-white rounded-xl border border-gray-200 p-6 shadow-sm">
  <div class="flex items-center justify-between mb-4">
    <div class="flex items-center gap-2">
      <FileText class="h-5 w-5 text-green-600" />
      <h2 class="text-lg font-semibold text-gray-900">Application Documents</h2>
    </div>
    <button
      class="inline-flex items-center px-3 py-2 border border-green-300 rounded-lg text-sm font-medium text-green-700 bg-green-50 hover:bg-green-100 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500 transition-colors"
      @click="showUpdateDocumentsModal = true"
    >
      <Edit class="h-4 w-4 mr-2" />
      Update Documents
    </button>
  </div>

  <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
    <div
      v-for="(fileUpload, index) in application.fileUploads"
      :key="index"
      class="bg-white rounded-lg border border-gray-200 overflow-hidden shadow-sm hover:shadow-md transition-shadow"
    >
      <div class="relative group">
        <div
          class="cursor-pointer"
          @click="handleViewDocument(getDocumentId(fileUpload))"
        >
          <img
            v-if="isImageFile(fileUpload)"
            :src="fileUpload"
            alt="Document"
            class="w-full h-48 object-cover group-hover:opacity-90 transition-opacity"
            @error="handleImageError"
          />
          <div v-else class="flex items-center justify-center h-48 bg-gray-100 group-hover:bg-gray-200 transition-colors">
            <div class="text-center">
              <FileText class="h-12 w-12 text-gray-400 mx-auto mb-2 group-hover:text-gray-500 transition-colors" />
              <p class="text-sm text-gray-500 group-hover:text-gray-600 transition-colors">Click to view document</p>
            </div>
          </div>
        </div>

        <!-- Overlay for click indication -->
        <div class="absolute inset-0 bg-black bg-opacity-0 group-hover:bg-opacity-10 transition-all duration-200 flex items-center justify-center">
          <div class="opacity-0 group-hover:opacity-100 transition-opacity bg-white bg-opacity-90 rounded-full p-2">
            <FileText class="h-6 w-6 text-gray-700" />
          </div>
        </div>
      </div>
    </div>
  </div>

  <p class="text-xs text-gray-500 mt-4">
    These documents were uploaded by the farmer during application submission.
  </p>
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
                    <DetailField label="Submitted At" :value="formatDateTime(application.submittedAt)" />
                    <DetailField label="Updated At" :value="formatDateTime(application.updatedAt)" />
                </div>
            </div>
        </div>
        </div>

        <!-- Update Documents Modal -->
        <div v-if="showUpdateDocumentsModal" class="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
            <div class="relative top-20 mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">
                <div class="mt-3">
                    <div class="flex items-center justify-between mb-4">
                        <h3 class="text-lg font-medium text-gray-900">Update Application Documents</h3>
                        <button
                            class="text-gray-400 hover:text-gray-600"
                            @click="closeUpdateModal"
                        >
                            <svg class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                            </svg>
                        </button>
                    </div>

                    <div class="mb-4">
                        <label class="block text-sm font-medium text-gray-700 mb-2">
                            Select New Documents
                        </label>
                        <input
                            ref="fileInput"
                            type="file"
                            multiple
                            accept=".pdf,.doc,.docx,.jpg,.jpeg,.png"
                            class="block w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-green-50 file:text-green-700 hover:file:bg-green-100"
                            @change="handleFileSelection"
                        />
                        <p class="text-xs text-gray-500 mt-1">
                            Supported formats: PDF, DOC, DOCX, JPG, JPEG, PNG. Max size: 20MB per file.
                        </p>
                    </div>

                    <!-- Selected Files Preview -->
                    <div v-if="selectedFiles.length > 0" class="mb-4">
                        <h4 class="text-sm font-medium text-gray-700 mb-2">Selected Files:</h4>
                        <div class="space-y-2">
                            <div
                                v-for="(file, index) in selectedFiles"
                                :key="index"
                                class="flex items-center justify-between p-2 bg-gray-50 rounded border"
                            >
                                <div class="flex items-center gap-2">
                                    <FileText class="h-4 w-4 text-gray-600" />
                                    <span class="text-sm text-gray-900 truncate">{{ file.name }}</span>
                                    <span class="text-xs text-gray-500">({{ formatFileSize(file.size) }})</span>
                                </div>
                                <button
                                    class="text-red-500 hover:text-red-700"
                                    @click="removeFile(index)"
                                >
                                    <svg class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                                    </svg>
                                </button>
                            </div>
                        </div>
                    </div>

                    <!-- Error Messages -->
                    <div v-if="updateError" class="mb-4 p-3 bg-red-50 border border-red-200 rounded">
                        <p class="text-sm text-red-800">{{ updateError }}</p>
                    </div>

                    <!-- Action Buttons -->
                    <div class="flex justify-end gap-3">
                        <button
                            :disabled="isUpdating"
                            class="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-gray-500 disabled:opacity-50"
                            @click="closeUpdateModal"
                        >
                            Cancel
                        </button>
                        <button
                            :disabled="selectedFiles.length === 0 || isUpdating"
                            class="px-4 py-2 text-sm font-medium text-white bg-green-600 border border-transparent rounded-md hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500 disabled:opacity-50 disabled:cursor-not-allowed"
                            @click="updateDocuments"
                        >
                            <span v-if="isUpdating" class="flex items-center">
                                <svg class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                                    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                                </svg>
                                Updating...
                            </span>
                            <span v-else>Update Documents</span>
                        </button>
                    </div>
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
import { useNotificationStore } from '@/stores/notification'
import { useDocumentStore } from '@/stores/document'
import AuthenticatedLayout from '@/layouts/AuthenticatedLayout.vue'
import DetailField from '@/components/tables/DetailField.vue'
import { ArrowLeft, Edit, Trash2, User, FileText, MapPin, Calendar, Download } from 'lucide-vue-next'
import {
    ADMIN_NAVIGATION,
    MUNICIPAL_AGRICULTURIST_NAVIGATION,
    AGRICULTURAL_EXTENSION_WORKER_NAVIGATION
} from '@/lib/navigation'

const router = useRouter()
const route = useRoute()
const { fetchApplicationById, deleteApplication, updateApplication, updateApplicationDocuments } = useApplicationStore()
const authStore = useAuthStore()
const notificationStore = useNotificationStore()
const documentStore = useDocumentStore()

// State
const loading = ref(false)
const error = ref(null)
const application = ref(null)

// Document update state
const showUpdateDocumentsModal = ref(false)
const selectedFiles = ref([])
const isUpdating = ref(false)
const updateError = ref(null)
const fileInput = ref(null)

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

// Utility functions
const isImageFile = (url) => {
    if (!url) return false
    const imageExtensions = ['.jpg', '.jpeg', '.png', '.gif', '.bmp', '.webp']
    const urlLower = url.toLowerCase()
    return imageExtensions.some(ext => urlLower.includes(ext))
}

const formatFileSize = (bytes) => {
    if (bytes === 0) return '0 Bytes'
    const k = 1024
    const sizes = ['Bytes', 'KB', 'MB', 'GB']
    const i = Math.floor(Math.log(bytes) / Math.log(k))
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// Document methods
const getDocumentId = (fileUpload) => {
    // Extract document ID from the file upload URL
    // Assuming the URL format is something like: /api/v1/documents/{documentId}/preview
    // or contains the document ID in some way
    if (typeof fileUpload === 'string') {
        const urlParts = fileUpload.split('/')
        // Find the document ID (usually after 'documents' in the URL)
        const documentsIndex = urlParts.findIndex(part => part === 'documents')
        if (documentsIndex !== -1 && documentsIndex + 1 < urlParts.length) {
            return urlParts[documentsIndex + 1]
        }
        // If not found in URL, try to extract from query params or other patterns
        const match = fileUpload.match(/documentId=([^&]+)/) || fileUpload.match(/documents\/([^/?]+)/)
        if (match) {
            return match[1]
        }
    }
    // Fallback: return the fileUpload itself if it's already an ID
    return fileUpload
}

const handleViewDocument = async (documentId) => {
    try {
        loading.value = true
        const result = await documentStore.fetchDocumentPreviewUrl(documentId)

        if (result.success === "true" && result.data) {
            // Open the preview URL in a new tab
            window.open(result.data, '_blank')
        } else {
            // Fallback: open the original URL if preview fails
            const originalUrl = application.value.fileUploads.find(upload =>
                getDocumentId(upload) === documentId
            )
            if (originalUrl) {
                window.open(originalUrl, '_blank')
            } else {
                notificationStore.showError('Unable to preview document')
            }
        }
    } catch (error) {
        console.error('Error viewing document:', error)
        notificationStore.showError('Failed to open document preview')
    } finally {
        loading.value = false
    }
}

const handleDownloadDocument = async (documentId) => {
    try {
        loading.value = true
        const result = await documentStore.downloadDocument(documentId)

        if (result.success === "true") {
            notificationStore.showSuccess('Document downloaded successfully')
        } else {
            notificationStore.showError(result.message || 'Failed to download document')
        }
    } catch (error) {
        console.error('Error downloading document:', error)
        notificationStore.showError('Failed to download document')
    } finally {
        loading.value = false
    }
}

// Document update methods
const handleFileSelection = (event) => {
    const files = Array.from(event.target.files)
    updateError.value = null

    // Validate files
    const maxSize = 20 * 1024 * 1024 // 20MB
    const allowedTypes = [
        'application/pdf',
        'application/msword',
        'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
        'image/jpeg',
        'image/jpg',
        'image/png'
    ]

    const validFiles = []

    for (const file of files) {
        if (file.size > maxSize) {
            updateError.value = `File "${file.name}" is too large. Maximum size is 20MB.`
            return
        }

        if (!allowedTypes.includes(file.type)) {
            updateError.value = `File "${file.name}" has an unsupported format. Please use PDF, DOC, DOCX, JPG, JPEG, or PNG files.`
            return
        }

        validFiles.push(file)
    }

    selectedFiles.value = validFiles
}

const removeFile = (index) => {
    selectedFiles.value.splice(index, 1)
    if (selectedFiles.value.length === 0 && fileInput.value) {
        fileInput.value.value = ''
    }
}

const closeUpdateModal = () => {
    showUpdateDocumentsModal.value = false
    selectedFiles.value = []
    updateError.value = null
    if (fileInput.value) {
        fileInput.value.value = ''
    }
}

const updateDocuments = async () => {
    if (selectedFiles.value.length === 0) {
        updateError.value = 'Please select at least one file to upload.'
        return
    }

    isUpdating.value = true
    updateError.value = null

    try {
        const result = await updateApplicationDocuments(application.value.id, selectedFiles.value)

        if (result.success) {
            notificationStore.showSuccess(result.message || 'Documents updated successfully!')
            closeUpdateModal()
            // Refresh the application data to show updated documents
            await fetchApplication()
        } else {
            updateError.value = result.message || 'Failed to update documents. Please try again.'
            notificationStore.showError(result.message || 'Failed to update documents.')
        }
    } catch (err) {
        const errorMessage = err.message || 'An unexpected error occurred.'
        updateError.value = errorMessage
        notificationStore.showError(errorMessage)
    } finally {
        isUpdating.value = false
    }
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
