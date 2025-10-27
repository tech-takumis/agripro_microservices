<template>
    <div class="bg-white rounded-lg shadow overflow-hidden">
        <!-- Table Header -->
        <div class="px-6 py-4 border-b border-gray-200 bg-gray-50">
            <div class="flex items-center gap-4">
                <input
                    type="checkbox"
                    :checked="isAllSelected"
                    :indeterminate="isPartiallySelected"
                    @change="toggleSelectAll"
                    class="w-4 h-4 text-blue-600 rounded border-gray-300 focus:ring-blue-500"
                />
                <span class="text-sm font-medium text-gray-700">
          {{ selectedApplications.length }} of {{ applications.length }} selected
        </span>
                <button
                  v-if="selectedApplications.length > 0"
                  @click="handleForwardToPCIC"
                  class="ml-4 px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
                  :disabled="isForwarding"
                >
                  Forward to PCIC
                </button>
            </div>
        </div>

        <!-- Table Content -->
        <div class="overflow-x-auto">
            <table class="min-w-full divide-y divide-gray-200">
                <thead class="bg-gray-50">
                <tr>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Select
                    </th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Applicant
                    </th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Crop Details
                    </th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Coverage
                    </th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Status
                    </th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Submitted
                    </th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Actions
                    </th>
                </tr>
                </thead>
                <tbody class="bg-white divide-y divide-gray-200">
                <ApplicationRow
                    v-for="application in applications"
                    :key="application.id"
                    :application="application"
                    :is-selected="selectedApplications.includes(application.id)"
                    @toggle-select="toggleApplicationSelection"
                    @view-details="$emit('viewDetails', application)"
                />
                </tbody>
            </table>
        </div>

        <!-- Empty State -->
        <div v-if="applications.length === 0" class="text-center py-12">
            <div class="text-gray-400 mb-4">
                <FileTextIcon class="w-12 h-12 mx-auto" />
            </div>
            <h3 class="text-lg font-medium text-gray-900 mb-2">No applications found</h3>
            <p class="text-gray-500">Try adjusting your filters to see more results.</p>
        </div>
    </div>
</template>

<script setup>
import { computed } from 'vue'
import { FileTextIcon } from 'lucide-vue-next'
import ApplicationRow from './ApplicationRow.vue'
import { useVerificationStore } from '../../stores/verification.js'
import { ref } from 'vue'

// Props
const props = defineProps({
    applications: {
        type: Array,
        required: true
    },
    selectedApplications: {
        type: Array,
        required: true
    }
})

// Emits
const emit = defineEmits(['update:selected', 'viewDetails'])

// Verification Store
const { isForwarding, forwardApplicationToPCIC } = useVerificationStore()

// Computed
const isAllSelected = computed(() =>
    props.applications.length > 0 && props.selectedApplications.length === props.applications.length
)

const isPartiallySelected = computed(() =>
    props.selectedApplications.length > 0 && props.selectedApplications.length < props.applications.length
)

// Methods
const toggleSelectAll = () => {
    if (isAllSelected.value) {
        emit('update:selected', [])
    } else {
        emit('update:selected', props.applications.map(app => app.id))
    }
}

const toggleApplicationSelection = (applicationId) => {
    const currentSelection = [...props.selectedApplications]
    const index = currentSelection.indexOf(applicationId)

    if (index > -1) {
        currentSelection.splice(index, 1)
    } else {
        currentSelection.push(applicationId)
    }

    emit('update:selected', currentSelection)
}

const handleForwardToPCIC = async () => {
  if (selectedApplications.length === 0) return
  const success = await forwardApplicationToPCIC(selectedApplications)
  if (success) {
    // Optionally emit an event or refresh the list
    // emit('forwarded')
    alert('Applications forwarded to PCIC successfully!')
  } else {
    alert('Failed to forward applications. Please try again.')
  }
}
</script>