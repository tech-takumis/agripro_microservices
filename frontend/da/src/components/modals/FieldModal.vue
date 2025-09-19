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
              {{ isEditing ? 'Edit Field' : 'Add New Field' }}
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
          <form @submit.prevent="saveField" class="space-y-4">
            <!-- Key -->
            <div>
              <label for="key" class="block text-sm font-medium text-gray-700 mb-1">
                Key <span class="text-red-500">*</span>
              </label>
              <input
                v-model="fieldData.key"
                type="text"
                id="key"
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                placeholder="e.g., farmer_name"
              />
            </div>

            <!-- Field Name -->
            <div>
              <label for="fieldName" class="block text-sm font-medium text-gray-700 mb-1">
                Field Name <span class="text-red-500">*</span>
              </label>
              <input
                v-model="fieldData.fieldName"
                type="text"
                id="fieldName"
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                placeholder="e.g., Farmer's name"
              />
            </div>

            <!-- Label -->
            <div>
              <label for="label" class="block text-sm font-medium text-gray-700 mb-1">
                Label <span class="text-red-500">*</span>
              </label>
              <input
                v-model="fieldData.label"
                type="text"
                id="label"
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                placeholder="e.g., Farmer's Full Name"
              />
            </div>

            <!-- Field Type -->
            <div>
              <label for="fieldType" class="block text-sm font-medium text-gray-700 mb-1">
                Field Type <span class="text-red-500">*</span>
              </label>
              <select
                v-model="fieldData.fieldType"
                id="fieldType"
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
              >
                <option value="">Select a type</option>
                <option v-for="type in availableFieldTypes" :key="type" :value="type">
                  {{ formatFieldType(type) }}
                </option>
              </select>
            </div>

            <!-- Choices (for SELECT and MULTI_SELECT) -->
            <div v-if="fieldData.fieldType === 'SELECT' || fieldData.fieldType === 'MULTI_SELECT'">
              <label class="block text-sm font-medium text-gray-700 mb-1">
                Choices <span class="text-red-500">*</span>
              </label>
              <div class="space-y-2">
                <div 
                  v-for="(choice, index) in choices" 
                  :key="index"
                  class="flex items-center space-x-2"
                >
                  <input
                    v-model="choices[index]"
                    type="text"
                    class="flex-1 px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                    placeholder="Choice option"
                  />
                  <button
                    type="button"
                    @click="removeChoice(index)"
                    class="p-2 text-red-500 hover:text-red-700"
                  >
                    <Trash2 class="h-4 w-4" />
                  </button>
                </div>
                <button
                  type="button"
                  @click="addChoice"
                  class="flex items-center text-sm text-indigo-600 hover:text-indigo-700"
                >
                  <Plus class="h-4 w-4 mr-1" />
                  Add Choice
                </button>
              </div>
            </div>

            <!-- Required -->
            <div class="flex items-center">
              <input
                v-model="fieldData.required"
                type="checkbox"
                id="required"
                class="h-4 w-4 text-indigo-600 border-gray-300 rounded focus:ring-indigo-500"
              />
              <label for="required" class="ml-2 block text-sm text-gray-900">
                Required Field
              </label>
            </div>

            <!-- Default Value -->
            <div>
              <label for="defaultValue" class="block text-sm font-medium text-gray-700 mb-1">
                Default Value
              </label>
              <input
                v-model="fieldData.defaultValue"
                type="text"
                id="defaultValue"
                class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                placeholder="Optional default value"
              />
            </div>

            <!-- Validation Regex -->
            <div>
              <label for="validationRegex" class="block text-sm font-medium text-gray-700 mb-1">
                Validation Regex
              </label>
              <input
                v-model="fieldData.validationRegex"
                type="text"
                id="validationRegex"
                class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                placeholder="e.g., ^[0-9]+$"
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
              @click="deleteField"
              class="px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-red-600 hover:bg-red-700"
            >
              Delete
            </button>
            <button
              @click="saveField"
              class="px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700"
            >
              {{ isEditing ? 'Update' : 'Add' }} Field
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { X, Plus, Trash2 } from 'lucide-vue-next'

const props = defineProps({
  isOpen: Boolean,
  field: Object,
  availableFieldTypes: Array,
  isEditing: Boolean
})

const emit = defineEmits(['close', 'save', 'delete'])

const fieldData = ref({
  key: '',
  fieldName: '',
  label: '',
  fieldType: '',
  required: false,
  defaultValue: '',
  validationRegex: ''
})

const choices = ref([''])

// Watch for field prop changes (when editing)
watch(() => props.field, (newField) => {
  if (newField) {
    fieldData.value = { ...newField }
    if (newField.choices && Array.isArray(newField.choices)) {
      choices.value = [...newField.choices]
    } else {
      choices.value = ['']
    }
  }
}, { immediate: true })

// Watch for modal open/close to reset form
watch(() => props.isOpen, (isOpen) => {
  if (!isOpen) {
    resetForm()
  }
})

const formatFieldType = (type) => {
  return type.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, l => l.toUpperCase())
}

const addChoice = () => {
  choices.value.push('')
}

const removeChoice = (index) => {
  if (choices.value.length > 1) {
    choices.value.splice(index, 1)
  }
}

const saveField = () => {
  const field = { ...fieldData.value }
  
  // Add choices for SELECT and MULTI_SELECT types
  if (field.fieldType === 'SELECT' || field.fieldType === 'MULTI_SELECT') {
    field.choices = choices.value.filter(choice => choice.trim() !== '')
  }
  
  emit('save', field)
}

const deleteField = () => {
  emit('delete')
}

const closeModal = () => {
  emit('close')
}

const resetForm = () => {
  fieldData.value = {
    key: '',
    fieldName: '',
    label: '',
    fieldType: '',
    required: false,
    defaultValue: '',
    validationRegex: ''
  }
  choices.value = ['']
}
</script>
