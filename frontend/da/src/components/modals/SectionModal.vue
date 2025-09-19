<template>
  <!-- Modal Overlay -->
  <div 
    v-if="isOpen" 
    class="fixed inset-0 z-50 overflow-hidden"
    @click="closeModal"
  >
    <div class="absolute inset-0 bg-black bg-opacity-50"></div>
    
    <!-- Modal Panel (Right Side) -->
    <div 
      class="absolute right-0 top-0 h-full w-full max-w-md bg-white shadow-xl transform transition-transform duration-300 ease-in-out"
      :class="isOpen ? 'translate-x-0' : 'translate-x-full'"
      @click.stop
    >
      <div class="flex flex-col h-full">
        <!-- Modal Header -->
        <div class="px-6 py-4 border-b border-gray-200 bg-gray-50">
          <div class="flex items-center justify-between">
            <h3 class="text-lg font-semibold text-gray-900">
              {{ isEditing ? 'Edit Section' : 'Add New Section' }}
            </h3>
            <button
              @click="closeModal"
              class="p-2 rounded-md text-gray-400 hover:text-gray-600 hover:bg-gray-100"
            >
              <X class="h-5 w-5" />
            </button>
          </div>
        </div>

        <!-- Modal Body -->
        <div class="flex-1 overflow-y-auto p-6">
          <form @submit.prevent="saveSection" class="space-y-4">
            <!-- Section Title -->
            <div>
              <label for="sectionTitle" class="block text-sm font-medium text-gray-700 mb-1">
                Section Title <span class="text-red-500">*</span>
              </label>
              <input
                v-model="sectionData.title"
                type="text"
                id="sectionTitle"
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                placeholder="e.g., Farmer Information"
              />
            </div>
          </form>
        </div>

        <!-- Modal Footer -->
        <div class="px-6 py-4 border-t border-gray-200 bg-gray-50">
          <div class="flex justify-end space-x-3">
            <button
              type="button"
              @click="closeModal"
              class="px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50"
            >
              Cancel
            </button>
            <button
              v-if="isEditing"
              type="button"
              @click="deleteSection"
              class="px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-red-600 hover:bg-red-700"
            >
              Delete
            </button>
            <button
              @click="saveSection"
              class="px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700"
            >
              {{ isEditing ? 'Update' : 'Add' }} Section
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { X } from 'lucide-vue-next'

const props = defineProps({
  isOpen: Boolean,
  section: Object,
  isEditing: Boolean
})

const emit = defineEmits(['close', 'save', 'delete'])

const sectionData = ref({
  title: '',
})

// Watch for section prop changes (when editing)
watch(() => props.section, (newSection) => {
  if (newSection) {
    sectionData.value = { ...newSection }
  }
}, { immediate: true })

// Watch for modal open/close to reset form
watch(() => props.isOpen, (isOpen) => {
  if (!isOpen) {
    resetForm()
  }
})

const saveSection = () => {
  emit('save', { ...sectionData.value })
}

const deleteSection = () => {
  emit('delete')
}

const closeModal = () => {
  emit('close')
}

const resetForm = () => {
  sectionData.value = {
    title: '',
  }
}
</script>
