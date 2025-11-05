<template>
  <AuthenticatedLayout 
    :navigation="adminNavigation" 
    role-title="Admin Portal"
    page-title="New Application Type"
  >
    <template #header>
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-2xl font-bold text-gray-900">Create New Application Type</h1>
          <p class="text-gray-600">Define the structure and sections for a new insurance application</p>
        </div>
      </div>
    </template>

    <div class="max-w-6xl mx-auto">
      <form @submit.prevent="submitApplication" class="space-y-6">
        <!-- Application Type Details -->
        <div class="bg-white rounded-lg shadow-sm border border-gray-200">
          <div class="px-6 py-4 border-b border-gray-200">
            <h2 class="text-lg font-semibold text-gray-900">Application Type Information</h2>
            <p class="text-sm text-gray-500">Define the basic details of this new insurance application type.</p>
          </div>
          <div class="p-6 space-y-4">
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
            <div class="flex items-center">
              <input
                v-model="applicationType.requiredAIAnalysis"
                type="checkbox"
                id="requiredAIAnalysis"
                class="h-4 w-4 text-indigo-600 border-gray-300 rounded focus:ring-indigo-500"
              />
              <label for="requiredAIAnalysis" class="ml-2 block text-sm text-gray-900">
                Requires AI Analysis (e.g., for satellite imagery, yield prediction)
              </label>
            </div>
          </div>
        </div>

        <!-- Application Sections -->
        <div class="bg-white rounded-lg shadow-sm border border-gray-200">
          <div class="px-6 py-4 border-b border-gray-200 flex items-center justify-between">
            <div>
              <h2 class="text-lg font-semibold text-gray-900">Application Sections</h2>
              <p class="text-sm text-gray-500">Organize fields into logical sections for better agriculture experience.</p>
            </div>
            <button
              type="button"
              @click="openSectionModal()"
              class="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
            >
              <Plus class="h-4 w-4 mr-2" />
              Add Section
            </button>
          </div>
          
          <div class="p-6">
            <div v-if="sections.length === 0" class="text-center text-gray-500 py-8">
              <Layers class="h-12 w-12 mx-auto text-gray-300 mb-4" />
              <p class="text-lg font-medium">No sections added yet</p>
              <p class="text-sm">Click "Add Section" to start organizing your application form.</p>
            </div>

            <!-- Sections List -->
            <div v-else class="space-y-6">
              <div 
                v-for="(section, sectionIndex) in sections" 
                :key="sectionIndex"
                class="border border-gray-200 rounded-lg"
              >
                <!-- Section Header -->
                <div class="px-4 py-3 bg-gray-50 border-b border-gray-200 flex items-center justify-between">
                  <div class="flex items-center">
                    <Layers class="h-5 w-5 text-gray-400 mr-2" />
                    <h3 class="text-md font-semibold text-gray-900">{{ section.title }}</h3>
                    <span class="ml-2 inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                      {{ section.fields.length }} fields
                    </span>
                  </div>
                  <div class="flex items-center space-x-2">
                    <button
                      type="button"
                      @click="openFieldModal(null, sectionIndex)"
                      class="text-sm text-indigo-600 hover:text-indigo-700"
                    >
                      Add Field
                    </button>
                    <button
                      type="button"
                      @click="editSection(section, sectionIndex)"
                      class="text-gray-400 hover:text-gray-600"
                    >
                      <Edit class="h-4 w-4" />
                    </button>
                    <button
                      type="button"
                      @click="removeSection(sectionIndex)"
                      class="text-red-400 hover:text-red-600"
                    >
                      <Trash2 class="h-4 w-4" />
                    </button>
                  </div>
                </div>

                <!-- Section Fields -->
                <div class="p-4">
                  <div v-if="section.fields.length === 0" class="text-center text-gray-400 py-4">
                    <FileText class="h-8 w-8 mx-auto mb-2" />
                    <p class="text-sm">No fields in this section</p>
                  </div>
                  <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-3">
                    <div 
                      v-for="(field, fieldIndex) in section.fields" 
                      :key="fieldIndex"
                      @click="editField(field, sectionIndex, fieldIndex)"
                      class="relative p-3 border border-gray-200 rounded-lg bg-gray-50 hover:bg-gray-100 cursor-pointer transition-colors"
                    >
                      <div class="flex items-start justify-between">
                        <div class="flex-1 min-w-0">
                          <h4 class="text-sm font-semibold text-gray-900 truncate">
                            {{ field.label }}
                          </h4>
                          <p class="text-xs text-gray-500 truncate mt-1">
                            {{ field.key }}
                          </p>
                          <div class="flex items-center mt-2">
                            <span class="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                              {{ formatFieldType(field.fieldType) }}
                            </span>
                            <span v-if="field.required" class="ml-2 inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-red-100 text-red-800">
                              Required
                            </span>
                          </div>
                        </div>
                        <button
                          @click.stop="removeField(sectionIndex, fieldIndex)"
                          class="p-1 rounded-full text-gray-400 hover:text-red-600 hover:bg-red-100 transition-colors"
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

        <!-- Submit Button -->
        <div class="flex justify-end space-x-4 pt-6">
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
            class="px-6 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-green-600 hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500 disabled:opacity-50"
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
import axios from '@/lib/axios'

const router = useRouter()

const adminNavigation = ADMIN_NAVIGATION

const applicationType = ref({
  name: '',
  layout: '',
  description: '',
  requiredAIAnalysis: false,
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
    const response = await axios.get('/api/v1/fields/types', {
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        'X-Tenant-ID': 'pcic'
      },
      withCredentials: true,
    })
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
  
  const payload = {
    name: applicationType.value.name,
    layout: applicationType.value.layout,
    description: applicationType.value.description,
    requiredAIAnalysis: applicationType.value.requiredAIAnalysis,
    sections: sections.value.map(section => ({
      title: section.title,
      fields: section.fields.map(field => ({
        key: field.key,
        fieldName: field.fieldName,
        label: field.label,
        fieldType: field.fieldType,
        choices: field.choices || null,
        required: field.required || false,
        defaultValue: field.defaultValue || null,
        validationRegex: field.validationRegex || null
      }))
    }))
  }

  try {
    console.log('Submitting application payload:', JSON.stringify(payload, null, 2))
    
    const response = await axios.post('/api/v1/insurances', payload, {
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        'X-Tenant-ID': 'pcic'
      },
      withCredentials: true,
    })
    
    console.log('Application created successfully:', response.data)
    alert('New application type created successfully!')
    router.push('/admin/dashboard')
  } catch (error) {
    console.error('Error creating application type:', error.response?.data || error.message)
    alert(`Failed to create application type: ${error.response?.data?.message || error.message}. Please check console for details.`)
  } finally {
    processing.value = false
  }
}

const resetForm = () => {
  applicationType.value = {
    name: '',
    layout: '',
    description: '',
    requiredAIAnalysis: false,
  }
  sections.value = []
}
</script>
