<template>
  <!-- Modal Overlay -->
  <div 
    v-if="isOpen" 
    class="fixed inset-0 z-50 overflow-hidden"
    @click="closeModal"
  >
    <div class="absolute inset-0 bg-black bg-opacity-30"></div>
    
    <!-- Modal Panel (Center) -->
    <div class="flex items-start justify-center min-h-screen p-4 pt-16">
      <div 
        class="bg-white rounded-lg shadow-xl max-w-4xl w-full max-h-[80vh] overflow-hidden relative z-10"
        @click.stop
      >
        <div class="flex flex-col max-h-[80vh]">
          <!-- Modal Header -->
          <div class="px-6 py-4 border-b border-gray-200 bg-white flex-shrink-0">
            <div class="flex items-center justify-between">
              <div>
                <h3 class="text-xl font-semibold text-gray-900">
                  {{ application?.name }}
                </h3>
                <p class="text-sm text-gray-500 mt-1">
                  Application Type Details
                </p>
              </div>
              <div class="flex items-center space-x-2">
                <button
                  @click="$emit('edit', application)"
                  class="p-2 rounded-md text-gray-400 hover:text-indigo-600 hover:bg-indigo-100 transition-colors"
                  title="Edit Application"
                >
                  <Edit class="h-5 w-5" />
                </button>
                <button
                  @click="$emit('duplicate', application)"
                  class="p-2 rounded-md text-gray-400 hover:text-green-600 hover:bg-green-100 transition-colors"
                  title="Duplicate Application"
                >
                  <Copy class="h-5 w-5" />
                </button>
                <button
                  @click="$emit('delete', application)"
                  class="p-2 rounded-md text-gray-400 hover:text-red-600 hover:bg-red-100 transition-colors"
                  title="Delete Application"
                >
                  <Trash2 class="h-5 w-5" />
                </button>
                <button
                  @click="closeModal"
                  class="p-2 rounded-md text-gray-400 hover:text-gray-600 hover:bg-gray-100 transition-colors"
                >
                  <X class="h-5 w-5" />
                </button>
              </div>
            </div>
          </div>

          <!-- Modal Body - Scrollable -->
          <div class="flex-1 overflow-y-auto">
            <div class="p-6">
              <div v-if="application" class="space-y-6">
                <!-- Basic Information -->
                <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div>
                    <h4 class="text-sm font-medium text-gray-700 mb-2">Description</h4>
                    <p class="text-sm text-gray-900 bg-gray-50 p-3 rounded-md">
                      {{ application.description || 'No description provided' }}
                    </p>
                  </div>
                  <div class="space-y-4">
                    <div>
                      <h4 class="text-sm font-medium text-gray-700 mb-2">Layout Type</h4>
                      <span class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-blue-100 text-blue-800">
                        {{ formatLayout(application.layout) }}
                      </span>
                    </div>
                    <div>
                      <h4 class="text-sm font-medium text-gray-700 mb-2">AI Analysis</h4>
                      <span :class="[
                        'inline-flex items-center px-3 py-1 rounded-full text-sm font-medium',
                        application.requiredAIAnalysis 
                          ? 'bg-yellow-100 text-yellow-800' 
                          : 'bg-gray-100 text-gray-800'
                      ]">
                        {{ application.requiredAIAnalysis ? 'Enabled' : 'Disabled' }}
                      </span>
                    </div>
                  </div>
                </div>

                <!-- Statistics -->
                <div class="grid grid-cols-3 gap-4">
                  <div class="text-center p-4 bg-indigo-50 rounded-lg">
                    <div class="text-2xl font-bold text-indigo-600">
                      {{ application.sections.length }}
                    </div>
                    <div class="text-sm text-indigo-700">Sections</div>
                  </div>
                  <div class="text-center p-4 bg-green-50 rounded-lg">
                    <div class="text-2xl font-bold text-green-600">
                      {{ getTotalFields(application) }}
                    </div>
                    <div class="text-sm text-green-700">Total Fields</div>
                  </div>
                  <div class="text-center p-4 bg-purple-50 rounded-lg">
                    <div class="text-2xl font-bold text-purple-600">
                      {{ getRequiredFields(application) }}
                    </div>
                    <div class="text-sm text-purple-700">Required Fields</div>
                  </div>
                </div>

                <!-- Sections Detail -->
                <div>
                  <h4 class="text-lg font-semibold text-gray-900 mb-4">Sections & Fields</h4>
                  <div class="space-y-4">
                    <div 
                      v-for="(section, sectionIndex) in application.sections" 
                      :key="section.id"
                      class="border border-gray-200 rounded-lg"
                    >
                      <!-- Section Header -->
                      <div class="px-4 py-3 bg-gray-50 border-b border-gray-200">
                        <div class="flex items-center justify-between">
                          <div class="flex items-center">
                            <Layers class="h-5 w-5 text-gray-400 mr-2" />
                            <h5 class="text-md font-semibold text-gray-900">{{ section.title }}</h5>
                            <span class="ml-2 inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                              {{ section.fields.length }} fields
                            </span>
                          </div>
                        </div>
                      </div>

                      <!-- Section Fields -->
                      <div class="p-4">
                        <div class="grid grid-cols-1 md:grid-cols-2 gap-3">
                          <div 
                            v-for="field in section.fields" 
                            :key="field.id"
                            class="p-3 border border-gray-200 rounded-lg bg-gray-50"
                          >
                            <div class="flex items-start justify-between">
                              <div class="flex-1 min-w-0">
                                <h6 class="text-sm font-semibold text-gray-900 truncate">
                                  {{ field.label }}
                                </h6>
                                <p class="text-xs text-gray-500 truncate mt-1">
                                  Key: {{ field.key }}
                                </p>
                                <div class="flex items-center mt-2 space-x-2">
                                  <span class="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                                    {{ formatFieldType(field.fieldType) }}
                                  </span>
                                  <span v-if="field.required" class="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-red-100 text-red-800">
                                    Required
                                  </span>
                                </div>
                                <div v-if="field.choices && field.choices.length > 0" class="mt-2">
                                  <p class="text-xs text-gray-600 mb-1">Choices:</p>
                                  <div class="flex flex-wrap gap-1">
                                    <span 
                                      v-for="choice in field.choices.slice(0, 3)" 
                                      :key="choice"
                                      class="inline-flex items-center px-1.5 py-0.5 rounded text-xs bg-gray-200 text-gray-700"
                                    >
                                      {{ choice }}
                                    </span>
                                    <span 
                                      v-if="field.choices.length > 3"
                                      class="inline-flex items-center px-1.5 py-0.5 rounded text-xs bg-gray-300 text-gray-600"
                                    >
                                      +{{ field.choices.length - 3 }}
                                    </span>
                                  </div>
                                </div>
                                <div v-if="field.defaultValue" class="mt-2">
                                  <p class="text-xs text-gray-600">Default: <span class="font-medium">{{ field.defaultValue }}</span></p>
                                </div>
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Modal Footer -->
          <div class="px-6 py-4 border-t border-gray-200 bg-white flex-shrink-0">
            <div class="flex justify-end space-x-3">
              <button
                @click="closeModal"
                class="px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 transition-colors"
              >
                Close
              </button>
              <button
                @click="$emit('edit', application)"
                class="px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 transition-colors"
              >
                Edit Application
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { X, Edit, Copy, Trash2, Layers } from 'lucide-vue-next'

const props = defineProps({
  isOpen: Boolean,
  application: Object
})

const emit = defineEmits(['close', 'edit', 'duplicate', 'delete'])

const closeModal = () => {
  emit('close')
}

const formatLayout = (layout) => {
  return layout.split('-').map(word => 
    word.charAt(0).toUpperCase() + word.slice(1)
  ).join(' ')
}

const formatFieldType = (type) => {
  return type.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, l => l.toUpperCase())
}

const getTotalFields = (application) => {
  return application.sections.reduce((total, section) => total + section.fields.length, 0)
}

const getRequiredFields = (application) => {
  return application.sections.reduce((total, section) => {
    return total + section.fields.filter(field => field.required).length
  }, 0)
}
</script>
