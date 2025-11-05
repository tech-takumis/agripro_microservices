<template>
  <!-- Overlay -->
  <div
    v-if="show"
    class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm transition-all duration-300"
  >
    <!-- Modal Container -->
    <div
      class="bg-white rounded-2xl shadow-2xl w-full max-w-lg p-6 relative transform transition-all duration-300 scale-100 animate-fadeIn"
    >
      <!-- Close Button -->
      <button
        @click="close"
        class="absolute top-3 right-3 text-gray-500 hover:text-gray-700 transition-colors"
        title="Close"
      >
        <X class="w-5 h-5" />
      </button>

      <!-- Header -->
      <div class="flex items-center gap-2 mb-5 border-b pb-3">
        <h2 class="text-xl font-semibold text-gray-900">Create New Batch</h2>
      </div>

      <!-- Form -->
      <form @submit.prevent="handleSubmit" class="space-y-4">
        <!-- Batch Name -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Batch Name</label>
          <div class="relative">
            <Tag class="absolute left-3 top-2.5 h-4 w-4 text-gray-400" />
            <input
              v-model="form.name"
              type="text"
              class="w-full pl-9 pr-3 py-2 text-sm border border-gray-300 rounded-lg placeholder-gray-400 
                     focus:ring-2 focus:ring-green-400 focus:border-green-50 transition"
              placeholder="Enter batch name"
              required
            />
          </div>
        </div>

        <!-- Description -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Description</label>
          <div class="relative">
            <FileText class="absolute left-3 top-2.5 h-4 w-4 text-gray-400" />
            <textarea
              v-model="form.description"
              rows="2"
              class="w-full pl-9 pr-3 py-2 text-sm border border-gray-300 rounded-lg placeholder-gray-400 
                     focus:ring-2 focus:ring-green-400 focus:border-green-50 transition resize-none"
              placeholder="Briefly describe the batch"
              required
            ></textarea>
          </div>
        </div>

<!-- Dates -->
<div class="flex items-center justify-between gap-4 text-center">
  <!-- Start Date -->
  <div class="flex flex-col flex-1">
    <label class="text-xs font-medium text-gray-700 mb-1 text-center">
      Start Date
    </label>
    <input
      v-model="form.startDate"
      type="datetime-local"
      class="w-full text-center text-xs border border-gray-300 rounded-md 
             px-2 py-1.5 focus:ring-2 focus:ring-green-400 focus:border-green-400 transition-all"
      required
    />
  </div>

  <!-- End Date -->
  <div class="flex flex-col flex-1">
    <label class="text-xs font-medium text-gray-700 mb-1 text-center">
      End Date
    </label>
    <input
      v-model="form.endDate"
      type="datetime-local"
      class="w-full text-center text-xs border border-gray-300 rounded-md 
             px-2 py-1.5 focus:ring-2 focus:ring-green-400 focus:border-green-400 transition-all"
      required
    />
  </div>
</div>



        <!-- Max Applications -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Max Applications</label>
          <div class="relative">
            <Users class="absolute left-3 top-2.5 h-4 w-4 text-gray-400" />
            <input
              v-model.number="form.maxApplications"
              type="number"
              min="1"
              class="w-full pl-9 pr-3 py-2 text-sm border border-gray-300 rounded-lg 
                     focus:ring-2 focus:ring-green-400 focus:border-green-50 transition"
              placeholder="Enter maximum number"
              required
            />
          </div>
        </div>

        <!-- Availability -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Available</label>
          <div class="relative">
            <ToggleLeft class="absolute left-3 top-2.5 h-4 w-4 text-gray-400" />
            <select
              v-model="form.isAvailable"
              class="w-full pl-9 pr-3 py-2 text-sm border border-gray-300 rounded-lg 
                     focus:ring-2 focus:ring-green-400 focus:border-green-50 transition"
            >
              <option :value="true">Yes</option>
              <option :value="false">No</option>
            </select>
          </div>
        </div>

        <!-- Application Type -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Application Type</label>
          <div class="relative">
            <Layers class="absolute left-3 top-2.5 h-4 w-4 text-gray-400" />
            <select
              v-model="form.applicationTypeId"
              class="w-full pl-9 pr-3 py-2 text-sm border border-gray-300 rounded-lg 
                     focus:ring-2 focus:ring-green-400 focus:border-green-50 transition"
              required
            >
              <option value="" disabled>Select Application Type</option>
              <option v-for="type in applicationTypes" :key="type.id" :value="type.id">
                {{ type.name }}
              </option>
            </select>
          </div>
        </div>

        <!-- Error Message -->
        <div v-if="error" class="text-red-600 text-sm">{{ error }}</div>

        <!-- Actions -->
        <div class="flex justify-end gap-3 pt-4 border-t">
          <button
            type="button"
            class="px-4 py-2 rounded-lg text-sm font-medium text-gray-700 bg-gray-100 border border-gray-300 
                   hover:bg-gray-200 focus:ring-2 focus:ring-offset-2 focus:ring-gray-400 transition"
            @click="close"
          >
           Cancel
          </button>
          <button
            type="submit"
            class="px-4 py-2 rounded-lg text-sm font-medium text-white bg-green-600 hover:bg-green-700 
                   focus:ring-2 focus:ring-offset-2 focus:ring-green-500 transition flex items-center"
          >
           Create Batch
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { useApplicationBatchStore } from '@/stores/applications'
import { useApplicationTypeStore } from '@/stores/applications'
import {
  X,
  Tag,
  FileText,
  Users,
  ToggleLeft,
  Layers,
} from 'lucide-vue-next'
const props = defineProps({
  show: Boolean
})
const emit = defineEmits(['close', 'created'])

const form = ref({
  name: '',
  description: '',
  startDate: '',
  endDate: '',
  maxApplications: 1,
  isAvailable: true,
  applicationTypeId: ''
})
const error = ref('')
const applicationTypes = ref([])
const applicationTypeStore = useApplicationTypeStore()
const batchStore = useApplicationBatchStore()

const fetchTypes = async () => {
  const result = await applicationTypeStore.fetchApplicationTypes()
  if (result.success) {
    applicationTypes.value = result.data
  }
}

onMounted(fetchTypes)

watch(() => props.show, (val) => {
  if (val) {
    error.value = ''
    form.value = {
      name: '',
      description: '',
      startDate: '',
      endDate: '',
      maxApplications: 1,
      isAvailable: true,
      applicationTypeId: ''
    }
    fetchTypes()
  }
})

const close = () => emit('close')

const handleSubmit = async () => {
  error.value = ''
  if (!form.value.name || !form.value.description || !form.value.startDate || !form.value.endDate || !form.value.applicationTypeId) {
    error.value = 'Please fill in all required fields.'
    return
  }
  const payload = {
    name: form.value.name,
    description: form.value.description,
    startDate: form.value.startDate,
    endDate: form.value.endDate,
    maxApplications: form.value.maxApplications,
    isAvailable: form.value.isAvailable,
    applicationTypeId: form.value.applicationTypeId
  }
  const result = await batchStore.createApplicationBatch(payload)
  if (result && result.success) {
    emit('created')
    close()
  } else {
    error.value = result?.error || 'Failed to create batch.'
  }
}
</script>

