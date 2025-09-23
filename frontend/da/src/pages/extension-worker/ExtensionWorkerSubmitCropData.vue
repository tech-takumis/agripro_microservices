<template>
    <div class="p-6 max-w-7xl mx-auto">
        Header with Actions
        <div class="flex justify-between items-center mb-6">
            <div>
                <h1 class="text-2xl font-bold text-gray-900">Application Management</h1>
                <p class="text-gray-600 mt-1">{{ filteredApplications.length }} applications found</p>
            </div>

            <div class="flex gap-3">
                <button
                    @click="showFilterModal = true"
                    class="flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
                >
                    <FilterIcon class="w-4 h-4" />
                    Filter Applications
                </button>

                <button
                    v-if="selectedApplications.length > 0"
                    @click="handleBulkUpdate"
                    class="flex items-center gap-2 px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors"
                >
                    <EditIcon class="w-4 h-4" />
                    Update Selected ({{ selectedApplications.length }})
                </button>

                <button
                    v-if="selectedApplications.length > 0"
                    @click="handleBulkDelete"
                    class="flex items-center gap-2 px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors"
                >
                    <TrashIcon class="w-4 h-4" />
                    Delete Selected ({{ selectedApplications.length }})
                </button>
            </div>
        </div>

        Applications Table
        <ApplicationTable
            :applications="filteredApplications"
            :selected-applications="selectedApplications"
            @update:selected="selectedApplications = $event"
            @view-details="handleViewDetails"
        />

        Filter Modal
        <FilterModal
            v-if="showFilterModal"
            :filters="filters"
            :available-options="filterOptions"
            @update:filters="filters = $event"
            @close="showFilterModal = false"
        />

        Application Details Modal
        <ApplicationDetailsModal
            v-if="selectedApplication"
            :application="selectedApplication"
            @close="selectedApplication = null"
        />
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { FilterIcon, EditIcon, TrashIcon } from 'lucide-vue-next'
import ApplicationTable from '../../components/tables/ApplicationTable.vue'
import FilterModal from '@/components/modals/FilterModal.vue'
import ApplicationDetailsModal from '@/components/modals/ApplicationDetailsModal.vue'
import { useApplicationFilters } from '@/utils/useApplicationFilters'
import { useApplicationActions } from '@/utils/useApplicationActions'

// Sample data - in real app, this would come from an API
const sampleApplications = [
    {
        "id": "2bbe779c-6e07-46b2-b84d-a7a6d6b6b178",
        "applicationTypeId": "6be9e0e9-4044-4b05-b778-f71736c48a02",
        "userId": "ad752128-81bd-4c46-bff9-085a14225f61",
        "dynamicFields": {
            "first_name": "Albert john",
            "last_name": "Pracullos",
            "middle_name": "Baguio",
            "age": 45,
            "sex": "Male",
            "crop_type": "Rice",
            "cover_type": "Multi-Risk",
            "premium": 2500.0,
            "amount_of_cover": 50000.0,
            "status": "SUBMITTED",
            "address": "Purok 7, Barangay Charito, Bayugan City, Agusan del Sur",
            "cell_phone_number": "09601985109"
        },
        "status": "SUBMITTED",
        "submittedAt": "2025-09-23T23:42:13.454747",
        "updatedAt": "2025-09-23T23:42:13.454747"
    },
    {
        "id": "e68f6d86-f8a7-40d1-9a57-3dd435567bb3",
        "applicationTypeId": "6be9e0e9-4044-4b05-b778-f71736c48a02",
        "userId": "ad752128-81bd-4c46-bff9-085a14225f61",
        "dynamicFields": {
            "first_name": "John Paul",
            "last_name": "Pracullos",
            "middle_name": "Baguio",
            "age": 45,
            "sex": "Male",
            "crop_type": "Rice",
            "cover_type": "Multi-Risk",
            "premium": 2500.0,
            "amount_of_cover": 50000.0,
            "address": "Purok 7, Barangay Charito, Bayugan City, Agusan del Sur",
            "cell_phone_number": "09601985109"
        },
        "status": "SUBMITTED",
        "submittedAt": "2025-09-23T23:42:30.554598",
        "updatedAt": "2025-09-23T23:42:30.554598"
    }
]

// Component state
const applications = ref([])
const selectedApplications = ref([])
const selectedApplication = ref(null)
const showFilterModal = ref(false)

// Composables for clean separation of concerns
const { filters, filteredApplications, filterOptions } = useApplicationFilters(applications)
const { handleBulkUpdate, handleBulkDelete } = useApplicationActions()

// Methods
const handleViewDetails = (application) => {
    selectedApplication.value = application
}

// Lifecycle
onMounted(() => {
    // In real app, fetch from API
    applications.value = sampleApplications
})
</script>