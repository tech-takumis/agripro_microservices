<template>
  <div v-if="show" class="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-40">
    <div class="bg-white rounded-lg shadow-lg w-full max-w-lg p-6 relative">
      <button class="absolute top-2 right-2 text-gray-500 hover:text-gray-700" @click="close">&times;</button>
      <h2 class="text-xl font-bold mb-4">Create New Batch</h2>
      <form @submit.prevent="handleSubmit">
        <div class="mb-3">
          <label class="block text-sm font-medium mb-1">Batch Name</label>
          <input v-model="form.name" type="text" class="w-full border rounded px-2 py-1" required />
        </div>
        <div class="mb-3">
          <label class="block text-sm font-medium mb-1">Description</label>
          <textarea v-model="form.description" class="w-full border rounded px-2 py-1" required></textarea>
        </div>
        <div class="mb-3 flex gap-2">
          <div class="flex-1">
            <label class="block text-sm font-medium mb-1">Start Date</label>
            <input v-model="form.startDate" type="datetime-local" class="w-full border rounded px-2 py-1" required />
          </div>
          <div class="flex-1">
            <label class="block text-sm font-medium mb-1">End Date</label>
            <input v-model="form.endDate" type="datetime-local" class="w-full border rounded px-2 py-1" required />
          </div>
        </div>
        <div class="mb-3">
          <label class="block text-sm font-medium mb-1">Max Applications</label>
          <input v-model.number="form.maxApplications" type="number" min="1" class="w-full border rounded px-2 py-1" required />
        </div>
        <div class="mb-3">
          <label class="block text-sm font-medium mb-1">Available</label>
          <select v-model="form.isAvailable" class="w-full border rounded px-2 py-1">
            <option :value="true">Yes</option>
            <option :value="false">No</option>
          </select>
        </div>
        <div class="mb-3">
          <label class="block text-sm font-medium mb-1">Application Type</label>
          <select v-model="form.applicationTypeId" class="w-full border rounded px-2 py-1" required>
            <option value="" disabled>Select Application Type</option>
            <option v-for="type in applicationTypes" :key="type.id" :value="type.id">
              {{ type.name }}
            </option>
          </select>
        </div>
        <div v-if="error" class="text-red-600 text-sm mb-2">{{ error }}</div>
        <div class="flex justify-end gap-2 mt-4">
          <button type="button" class="px-4 py-2 bg-gray-200 rounded" @click="close">Cancel</button>
          <button type="submit" class="px-4 py-2 bg-blue-600 text-white rounded">Create Batch</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { useApplicationBatchStore } from '@/stores/applications'
import { useApplicationTypeStore } from '@/stores/applications'

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

