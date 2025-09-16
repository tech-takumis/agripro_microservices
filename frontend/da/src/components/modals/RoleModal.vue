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
      class="absolute right-0 top-0 h-full w-full max-w-2xl bg-white shadow-xl transform transition-transform duration-300 ease-in-out"
      :class="isOpen ? 'translate-x-0' : 'translate-x-full'"
      @click.stop
    >
      <div class="flex flex-col h-full">
        <!-- Modal Header -->
        <div class="px-6 py-4 border-b border-gray-200 bg-gray-50">
          <div class="flex items-center justify-between">
            <h3 class="text-lg font-semibold text-gray-900">
              {{ isEditing ? 'Edit Role' : 'Create New Role' }}
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
          <form @submit.prevent="saveRole" class="space-y-6">
            <!-- Role Name -->
            <div>
              <label for="roleName" class="block text-sm font-medium text-gray-700 mb-1">
                Role Name <span class="text-red-500">*</span>
              </label>
              <input
                v-model="roleData.name"
                type="text"
                id="roleName"
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                placeholder="e.g., Content Manager"
              />
            </div>

            <!-- Permissions Section -->
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-3">
                Permissions <span class="text-red-500">*</span>
              </label>
              
              <!-- Search Permissions -->
              <div class="mb-4">
                <div class="relative">
                  <Search class="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
                  <input
                    v-model="searchQuery"
                    type="text"
                    placeholder="Search permissions..."
                    class="w-full pl-10 pr-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                  />
                </div>
              </div>

              <!-- Select All/None -->
              <div class="flex items-center justify-between mb-4">
                <div class="flex items-center space-x-4">
                  <button
                    type="button"
                    @click="selectAllPermissions"
                    class="text-sm text-indigo-600 hover:text-indigo-700"
                  >
                    Select All
                  </button>
                  <button
                    type="button"
                    @click="clearAllPermissions"
                    class="text-sm text-gray-600 hover:text-gray-700"
                  >
                    Clear All
                  </button>
                </div>
                <span class="text-sm text-gray-500">
                  {{ selectedPermissions.length }} of {{ filteredAuthorities.length }} selected
                </span>
              </div>

              <!-- Permissions by Category -->
              <div class="space-y-4 max-h-96 overflow-y-auto">
                <div 
                  v-for="(authorities, category) in groupedFilteredAuthorities" 
                  :key="category"
                  class="border border-gray-200 rounded-lg"
                >
                  <div class="px-4 py-3 bg-gray-50 border-b border-gray-200">
                    <div class="flex items-center justify-between">
                      <h4 class="text-sm font-medium text-gray-900">
                        {{ formatCategory(category) }}
                      </h4>
                      <div class="flex items-center space-x-2">
                        <button
                          type="button"
                          @click="toggleCategorySelection(category, authorities)"
                          class="text-xs text-indigo-600 hover:text-indigo-700"
                        >
                          {{ isCategoryFullySelected(authorities) ? 'Deselect All' : 'Select All' }}
                        </button>
                        <span class="text-xs text-gray-500">
                          {{ getCategorySelectedCount(authorities) }}/{{ authorities.length }}
                        </span>
                      </div>
                    </div>
                  </div>
                  <div class="p-4 space-y-2">
                    <div 
                      v-for="authority in authorities" 
                      :key="authority.id"
                      class="flex items-center"
                    >
                      <input
                        :id="`permission-${authority.id}`"
                        v-model="selectedPermissions"
                        :value="authority.id"
                        type="checkbox"
                        class="h-4 w-4 text-indigo-600 border-gray-300 rounded focus:ring-indigo-500"
                      />
                      <label 
                        :for="`permission-${authority.id}`" 
                        class="ml-3 text-sm text-gray-900 cursor-pointer"
                      >
                        {{ formatPermissionName(authority.name) }}
                      </label>
                    </div>
                  </div>
                </div>
              </div>
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
              @click="deleteRole"
              class="px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-red-600 hover:bg-red-700"
            >
              Delete Role
            </button>
            <button
              @click="saveRole"
              :disabled="!roleData.name || selectedPermissions.length === 0"
              class="px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 disabled:opacity-50"
            >
              {{ isEditing ? 'Update' : 'Create' }} Role
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import { X, Search } from 'lucide-vue-next'

const props = defineProps({
  isOpen: Boolean,
  role: Object,
  authorities: Array,
  isEditing: Boolean
})

const emit = defineEmits(['close', 'save', 'delete'])

const roleData = ref({
  name: '',
})

const selectedPermissions = ref([])
const searchQuery = ref('')

// Watch for role prop changes (when editing)
watch(() => props.role, (newRole) => {
  if (newRole) {
    roleData.value = { name: newRole.name }
    selectedPermissions.value = newRole.permissions ? newRole.permissions.map(p => p.id) : []
  }
}, { immediate: true })

// Watch for modal open/close to reset form
watch(() => props.isOpen, (isOpen) => {
  if (!isOpen) {
    resetForm()
  }
})

// Computed properties
const filteredAuthorities = computed(() => {
  if (!searchQuery.value) return props.authorities
  return props.authorities.filter(authority => 
    authority.name.toLowerCase().includes(searchQuery.value.toLowerCase())
  )
})

const groupedFilteredAuthorities = computed(() => {
  const grouped = {}
  filteredAuthorities.value.forEach(authority => {
    const category = authority.name.split('_')[1] || 'Other'
    if (!grouped[category]) {
      grouped[category] = []
    }
    grouped[category].push(authority)
  })
  return grouped
})

// Helper functions
const formatCategory = (category) => {
  return category.charAt(0).toUpperCase() + category.slice(1).toLowerCase()
}

const formatPermissionName = (name) => {
  return name.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, l => l.toUpperCase())
}

const selectAllPermissions = () => {
  selectedPermissions.value = filteredAuthorities.value.map(auth => auth.id)
}

const clearAllPermissions = () => {
  selectedPermissions.value = []
}

const toggleCategorySelection = (category, authorities) => {
  const categoryIds = authorities.map(auth => auth.id)
  const allSelected = categoryIds.every(id => selectedPermissions.value.includes(id))
  
  if (allSelected) {
    // Remove all category permissions
    selectedPermissions.value = selectedPermissions.value.filter(id => !categoryIds.includes(id))
  } else {
    // Add all category permissions
    const newIds = categoryIds.filter(id => !selectedPermissions.value.includes(id))
    selectedPermissions.value = [...selectedPermissions.value, ...newIds]
  }
}

const isCategoryFullySelected = (authorities) => {
  const categoryIds = authorities.map(auth => auth.id)
  return categoryIds.every(id => selectedPermissions.value.includes(id))
}

const getCategorySelectedCount = (authorities) => {
  const categoryIds = authorities.map(auth => auth.id)
  return categoryIds.filter(id => selectedPermissions.value.includes(id)).length
}

const saveRole = () => {
  const role = {
    name: roleData.value.name,
    permissionIds: selectedPermissions.value
  }
  
  emit('save', role)
}

const deleteRole = () => {
  emit('delete')
}

const closeModal = () => {
  emit('close')
}

const resetForm = () => {
  roleData.value = { name: '' }
  selectedPermissions.value = []
  searchQuery.value = ''
}
</script>
