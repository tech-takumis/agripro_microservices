<template>
  <AuthenticatedLayout 
    :navigation="adminNavigation" 
    role-title="Admin Portal"
    page-title="New Application Type"
  >
    <div class="h-full flex flex-col overflow-hidden">
      <form @submit.prevent="submitApplication" class="flex-1 flex flex-col overflow-hidden">
        <!-- Two Column Layout: 8/4 ratio -->
        <div class="flex-1 grid grid-cols-12 gap-4 overflow-hidden">
          <!-- Left Column: Application Type Details (8 columns) -->
          <div class="col-span-8 flex flex-col overflow-hidden">
            <div class="bg-white rounded-lg shadow-sm border border-gray-200 flex flex-col h-full">
              <div class="px-6 py-4 border-b border-gray-200 flex-shrink-0">
                <h2 class="text-lg font-semibold text-gray-900">Application Type Information</h2>
                <p class="text-sm text-gray-500">Define the basic details of this new insurance application type.</p>
              </div>
              <div class="p-6 space-y-4 flex-1 overflow-y-auto">
                <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <label for="name" class="block text-sm font-medium text-gray-700 mb-1">
                      Name <span class="text-red-500">*</span>
                    </label>
                    <input
                      v-model="applicationType.name"
                      type="text"
                      id="name"
                      required
                      class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                      placeholder="e.g., Crop Insurance"
                    />
                  </div>
                  <div>
                    <label for="layout" class="block text-sm font-medium text-gray-700 mb-1">
                      Layout <span class="text-red-500">*</span>
                    </label>
                    <select
                      v-model="applicationType.layout"
                      id="layout"
                      required
                      class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                    >
                      <option value="">Select Layout</option>
                      <option value="single-step">Single Step</option>
                      <option value="two-step">Two Step</option>
                      <option value="multi-step">Multi Step</option>
                    </select>
                  </div>
                </div>
                <div>
                  <label for="description" class="block text-sm font-medium text-gray-700 mb-1">
                    Description
                  </label>
                  <textarea
                    v-model="applicationType.description"
                    id="description"
                    rows="3"
                    class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                    placeholder="A brief description of what this application type is for."
                  ></textarea>
                </div>
              </div>
            </div>
          </div>

          <!-- Right Column: Application Sections (4 columns) -->
          <div class="col-span-4 flex flex-col overflow-hidden">
            <div class="bg-white rounded-lg shadow-sm border border-gray-200 flex flex-col h-full">
              <div class="px-6 py-4 border-b border-gray-200 flex-shrink-0">
                <div class="flex items-center justify-between">
                  <div>
                    <h2 class="text-lg font-semibold text-gray-900">Sections</h2>
                    <p class="text-sm text-gray-500">Organize fields into sections</p>
                  </div>
                  <button
                    type="button"
                    @click="openSectionModal()"
                    class="inline-flex items-center px-3 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
                  >
                    <Plus class="h-4 w-4 mr-1" />
                    Add
                  </button>
                </div>
              </div>
              
              <!-- Scrollable Sections List -->
              <div class="flex-1 overflow-y-auto p-4">
                <div v-if="sections.length === 0" class="text-center text-gray-500 py-8">
                  <Layers class="h-12 w-12 mx-auto text-gray-300 mb-4" />
                  <p class="text-sm font-medium">No sections yet</p>
                  <p class="text-xs">Click "Add" to create a section</p>
                </div>

                <!-- Sections List -->
                <div v-else class="space-y-3">
                  <div 
                    v-for="(section, sectionIndex) in sections" 
                    :key="sectionIndex"
                    class="border border-gray-200 rounded-lg"
                  >
                    <!-- Section Header -->
                    <div class="px-3 py-2 bg-gray-50 border-b border-gray-200">
                      <div class="flex items-center justify-between mb-2">
                        <div class="flex items-center min-w-0 flex-1">
                          <Layers class="h-4 w-4 text-gray-400 mr-2 flex-shrink-0" />
                          <h3 class="text-sm font-semibold text-gray-900 truncate">{{ section.title }}</h3>
                        </div>
                        <div class="flex items-center space-x-1 flex-shrink-0">
                          <button
                            type="button"
                            @click="editSection(section, sectionIndex)"
                            class="text-gray-400 hover:text-gray-600 p-1"
                          >
                            <Edit class="h-3 w-3" />
                          </button>
                          <button
                            type="button"
                            @click="removeSection(sectionIndex)"
                            class="text-red-400 hover:text-red-600 p-1"
                          >
                            <Trash2 class="h-3 w-3" />
                          </button>
                        </div>
                      </div>
                      <div class="flex items-center justify-between">
                        <span class="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                          {{ section.fields.length }} fields
                        </span>
                        <button
                          type="button"
                          @click="openFieldModal(null, sectionIndex)"
                          class="text-xs text-indigo-600 hover:text-indigo-700 font-medium"
                        >
                          + Add Field
                        </button>
                      </div>
                    </div>

                    <!-- Section Fields -->
                    <div class="p-2">
                      <div v-if="section.fields.length === 0" class="text-center text-gray-400 py-2">
                        <FileText class="h-6 w-6 mx-auto mb-1" />
                        <p class="text-xs">No fields</p>
                      </div>
                      <div v-else class="space-y-2">
                        <div 
                          v-for="(field, fieldIndex) in section.fields" 
                          :key="fieldIndex"
                          @click="editField(field, sectionIndex, fieldIndex)"
                          class="relative p-2 border border-gray-200 rounded-lg bg-gray-50 hover:bg-gray-100 cursor-pointer transition-colors"
                        >
                          <div class="flex items-start justify-between">
                            <div class="flex-1 min-w-0">
                              <h4 class="text-xs font-semibold text-gray-900 truncate">
                                {{ field.label }}
                              </h4>
                              <p class="text-xs text-gray-500 truncate">
                                {{ field.key }}
                              </p>
                              <div class="flex items-center mt-1 space-x-1">
                                <span class="inline-flex items-center px-1.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                                  {{ formatFieldType(field.fieldType) }}
                                </span>
                                <span v-if="field.required" class="inline-flex items-center px-1.5 py-0.5 rounded-full text-xs font-medium bg-red-100 text-red-800">
                                  Required
                                </span>
                              </div>
                            </div>
                            <button
                              @click.stop="removeField(sectionIndex, fieldIndex)"
                              class="p-1 rounded-full text-gray-400 hover:text-red-600 hover:bg-red-100 transition-colors flex-shrink-0"
                              title="Remove Field"
                            >
                              <Trash2 class="h-3 w-3" />
                            </button>
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

        <!-- Submit Button -->
        <div class="flex-shrink-0 flex justify-end space-x-4 pt-4">
          <button
            type="button"
            @click="resetForm"
            class="px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50"
          >
            Reset Form
          </button>
          <button
            type="submit"
            :disabled="processing || sections.length === 0"
            class="inline-flex items-center px-6 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-green-600 hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500 disabled:opacity-50"
          >
            <Loader2 v-if="processing" class="animate-spin h-4 w-4 mr-2" />
            {{ processing ? 'Creating...' : 'Create Application Type' }}
          </button>
        </div>
      </form>
    </div>

    <!-- Section Modal -->
    <SectionModal
      :is-open="sectionModalOpen"
      :section="selectedSection"
      :is-editing="isEditingSection"
      @close="closeSectionModal"
      @save="saveSection"
      @delete="deleteSection"
    />

    <!-- Field Modal -->
    <FieldModal
      :is-open="fieldModalOpen"
      :field="selectedField"
      :available-field-types="availableFieldTypes"
      :is-editing="isEditingField"
      @close="closeFieldModal"
      @save="saveField"
      @delete="deleteField"
    />
  </AuthenticatedLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft, Plus, Trash2, Loader2, FileText, Layers, Edit } from 'lucide-vue-next'
import AuthenticatedLayout from '@/layouts/AuthenticatedLayout.vue'
import SectionModal from '@/components/modals/SectionModal.vue'
import FieldModal from '@/components/modals/FieldModal.vue'
import { ADMIN_NAVIGATION } from '@/lib/navigation'
import { useApplicationTypeStore } from '@/stores/applications'
import axios from '@/lib/axios'

const router = useRouter()
const applicationTypeStore = useApplicationTypeStore()

const adminNavigation = ADMIN_NAVIGATION

const applicationType = ref({
  name: '',
  layout: '',
  description: '',
})

const sections = ref([])
const availableFieldTypes = ref([])
const processing = ref(false)

// Section modal state
const sectionModalOpen = ref(false)
const selectedSection = ref(null)
const selectedSectionIndex = ref(-1)
const isEditingSection = ref(false)

// Field modal state
const fieldModalOpen = ref(false)
const selectedField = ref(null)
const selectedFieldIndex = ref(-1)
const currentSectionIndex = ref(-1)
const isEditingField = ref(false)

// Fetch available field types on mount
const fetchFieldTypes = async () => {
  try {
    const response = await axios.get('/fields/types')
    availableFieldTypes.value = response.data
    console.log('Available field types:', availableFieldTypes.value)
  } catch (error) {
    console.error('Error fetching field types:', error)
    // Fallback to default types if API fails
    availableFieldTypes.value = [
      'LOCATION', 'DATE', 'NUMBER', 'TEXT', 'MULTI_SELECT', 
      'FILE', 'SIGNATURE', 'BOOLEAN', 'SELECT'
    ]
  }
}

onMounted(() => {
  fetchFieldTypes()
})

const formatFieldType = (type) => {
  return type.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, l => l.toUpperCase())
}

// Section management
const openSectionModal = (section = null, index = -1) => {
  if (section) {
    selectedSection.value = { ...section }
    selectedSectionIndex.value = index
    isEditingSection.value = true
  } else {
    selectedSection.value = null
    selectedSectionIndex.value = -1
    isEditingSection.value = false
  }
  sectionModalOpen.value = true
}

const closeSectionModal = () => {
  sectionModalOpen.value = false
  selectedSection.value = null
  selectedSectionIndex.value = -1
  isEditingSection.value = false
}

const editSection = (section, index) => {
  openSectionModal(section, index)
}

const saveSection = (sectionData) => {
  if (isEditingSection.value) {
    // Update existing section
    sections.value[selectedSectionIndex.value] = {
      ...sections.value[selectedSectionIndex.value],
      title: sectionData.title
    }
  } else {
    // Add new section
    sections.value.push({
      title: sectionData.title,
      fields: []
    })
  }
  closeSectionModal()
}

const deleteSection = () => {
  if (isEditingSection.value && selectedSectionIndex.value >= 0) {
    sections.value.splice(selectedSectionIndex.value, 1)
  }
  closeSectionModal()
}

const removeSection = (index) => {
  if (confirm('Are you sure you want to remove this section and all its fields?')) {
    sections.value.splice(index, 1)
  }
}

// Field management
const openFieldModal = (field = null, sectionIndex = -1, fieldIndex = -1) => {
  currentSectionIndex.value = sectionIndex
  if (field) {
    selectedField.value = { ...field }
    selectedFieldIndex.value = fieldIndex
    isEditingField.value = true
  } else {
    selectedField.value = null
    selectedFieldIndex.value = -1
    isEditingField.value = false
  }
  fieldModalOpen.value = true
}

const closeFieldModal = () => {
  fieldModalOpen.value = false
  selectedField.value = null
  selectedFieldIndex.value = -1
  currentSectionIndex.value = -1
  isEditingField.value = false
}

const editField = (field, sectionIndex, fieldIndex) => {
  openFieldModal(field, sectionIndex, fieldIndex)
}

const saveField = (fieldData) => {
  if (isEditingField.value) {
    // Update existing field
    sections.value[currentSectionIndex.value].fields[selectedFieldIndex.value] = fieldData
  } else {
    // Add new field to section
    if (currentSectionIndex.value >= 0) {
      sections.value[currentSectionIndex.value].fields.push(fieldData)
    }
  }
  closeFieldModal()
}

const deleteField = () => {
  if (isEditingField.value && currentSectionIndex.value >= 0 && selectedFieldIndex.value >= 0) {
    sections.value[currentSectionIndex.value].fields.splice(selectedFieldIndex.value, 1)
  }
  closeFieldModal()
}

const removeField = (sectionIndex, fieldIndex) => {
  sections.value[sectionIndex].fields.splice(fieldIndex, 1)
}

const submitApplication = async () => {
  processing.value = true
  
  // Build payload matching ApplicationTypeRequestDto
  const payload = {
    name: applicationType.value.name,
    description: applicationType.value.description,
    providerName: 'Agriculture', // Static value as requested
    layout: applicationType.value.layout,
    sections: sections.value.map(section => ({
      title: section.title,
      fields: section.fields.map(field => ({
        key: field.key,
        fieldName: field.fieldName,
        fieldType: field.fieldType,
        required: field.required || false,
        defaultValue: field.defaultValue || null,
        choices: field.choices || null,
        validationRegex: field.validationRegex || null
      }))
    }))
  }

  try {
    console.log('Submitting application type payload:', JSON.stringify(payload, null, 2))
    
    const result = await applicationTypeStore.createApplicationType(payload)
    
    if (result.success) {
      console.log('Application type created successfully:', result.data)
      alert('New application type created successfully!')
      router.push('/admin/applications')
    } else {
      throw new Error(result.error?.message || 'Failed to create application type')
    }
  } catch (error) {
    console.error('Error creating application type:', error)
    alert(`Failed to create application type: ${error.message}. Please check console for details.`)
  } finally {
    processing.value = false
  }
}

const resetForm = () => {
  applicationType.value = {
    name: '',
    layout: '',
    description: '',
  }
  sections.value = []
}
</script>
