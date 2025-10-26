<template>
  <AuthenticatedLayout 
    :navigation="adminNavigation" 
    role-title="Admin Portal"
    page-title="Roles & Permissions"
  >
    <template #header>
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-2xl font-bold text-gray-900">Roles & Permissions</h1>
          <p class="text-gray-600">Manage user roles and assign permissions</p>
        </div>
        <div class="flex items-center space-x-3">
          <button
            @click="refreshData"
            :disabled="loading"
            class="inline-flex items-center px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 disabled:opacity-50"
          >
            <RefreshCw :class="['h-4 w-4 mr-2', loading ? 'animate-spin' : '']" />
            Refresh
          </button>
          <button
            @click="openRoleModal()"
            class="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700"
          >
            <Plus class="h-4 w-4 mr-2" />
            Create Role
          </button>
        </div>
      </div>
    </template>

    <div class="space-y-6">
      <!-- Loading State -->
      <div v-if="loading && roles.length === 0" class="text-center py-12">
        <Loader2 class="h-8 w-8 animate-spin mx-auto text-gray-400" />
        <p class="mt-2 text-gray-500">Loading roles and permissions...</p>
      </div>

      <!-- Error State -->
      <div v-else-if="error" class="bg-red-50 border border-red-200 rounded-md p-4">
        <div class="flex">
          <AlertCircle class="h-5 w-5 text-red-400" />
          <div class="ml-3">
            <h3 class="text-sm font-medium text-red-800">Error</h3>
            <p class="mt-1 text-sm text-red-700">{{ error }}</p>
          </div>
        </div>
      </div>

      <!-- Statistics Cards -->
      <div v-else class="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <div class="flex items-center">
            <div class="p-3 rounded-lg bg-indigo-100">
              <Shield class="h-6 w-6 text-indigo-600" />
            </div>
            <div class="ml-4">
              <h3 class="text-lg font-semibold text-gray-900">{{ roles.length }}</h3>
              <p class="text-sm text-gray-500">Total Roles</p>
            </div>
          </div>
        </div>
        
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <div class="flex items-center">
            <div class="p-3 rounded-lg bg-green-100">
              <Key class="h-6 w-6 text-green-600" />
            </div>
            <div class="ml-4">
              <h3 class="text-lg font-semibold text-gray-900">{{ authorities.length }}</h3>
              <p class="text-sm text-gray-500">Available Permissions</p>
            </div>
          </div>
        </div>
        
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <div class="flex items-center">
            <div class="p-3 rounded-lg bg-blue-100">
              <Users class="h-6 w-6 text-blue-600" />
            </div>
            <div class="ml-4">
              <h3 class="text-lg font-semibold text-gray-900">{{ Object.keys(groupedAuthorities).length }}</h3>
              <p class="text-sm text-gray-500">Permission Categories</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Roles Table -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200">
        <div class="px-6 py-4 border-b border-gray-200">
          <div class="flex items-center justify-between">
            <h2 class="text-lg font-semibold text-gray-900">System Roles</h2>
            <div class="flex items-center space-x-3">
              <!-- Bulk Actions -->
              <div v-if="selectedRoles.length > 0" class="flex items-center space-x-2">
                <span class="text-sm text-gray-500">{{ selectedRoles.length }} selected</span>
                <button
                  @click="editSelectedRole"
                  :disabled="selectedRoles.length !== 1"
                  class="inline-flex items-center px-3 py-1.5 border border-blue-300 rounded-md shadow-sm text-sm font-medium text-blue-700 bg-blue-50 hover:bg-blue-100 disabled:opacity-50 disabled:cursor-not-allowed"
                  title="Edit selected role"
                >
                  <Edit class="h-4 w-4 mr-1" />
                  Edit
                </button>
                <button
                  @click="deleteSelectedRoles"
                  class="inline-flex items-center px-3 py-1.5 border border-red-300 rounded-md shadow-sm text-sm font-medium text-red-700 bg-red-50 hover:bg-red-100"
                  title="Delete selected roles"
                >
                  <Trash2 class="h-4 w-4 mr-1" />
                  Delete
                </button>
              </div>
              <!-- Search Bar -->
              <div class="relative">
                <Search class="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
                <input
                  v-model="searchQuery"
                  type="text"
                  placeholder="Search roles..."
                  class="pl-10 pr-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
                />
              </div>
            </div>
          </div>
        </div>
        
        <div class="overflow-x-auto">
          <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  <input
                    type="checkbox"
                    :checked="isAllSelected"
                    @change="toggleSelectAll"
                    class="h-4 w-4 text-indigo-600 focus:ring-indigo-500 border-gray-300 rounded"
                  />
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Role Name
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Permissions Count
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Key Permissions
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Created Date
                </th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              <tr 
                v-for="role in filteredRoles" 
                :key="role.id" 
                class="hover:bg-gray-50"
                :class="{ 'bg-indigo-50': isRoleSelected(role.id) }"
              >
                <td class="px-6 py-4 whitespace-nowrap">
                  <input
                    type="checkbox"
                    :checked="isRoleSelected(role.id)"
                    @change="toggleRoleSelection(role.id)"
                    class="h-4 w-4 text-indigo-600 focus:ring-indigo-500 border-gray-300 rounded"
                  />
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="flex items-center">
                    <div class="p-2 rounded-lg bg-indigo-100 mr-3">
                      <Shield class="h-4 w-4 text-indigo-600" />
                    </div>
                    <div>
                      <div class="text-sm font-medium text-gray-900">{{ role.name }}</div>
                      <div class="text-sm text-gray-500">ID: {{ role.id }}</div>
                    </div>
                  </div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                    {{ role.permissions ? role.permissions.length : 0 }} permissions
                  </span>
                </td>
                <td class="px-6 py-4">
                  <div class="flex flex-wrap gap-1">
                    <span 
                      v-for="permission in (role.permissions || []).slice(0, 3)" 
                      :key="permission.id"
                      class="inline-flex items-center px-2 py-1 rounded text-xs font-medium bg-gray-100 text-gray-800"
                    >
                      {{ formatPermissionName(permission.name) }}
                    </span>
                    <span 
                      v-if="role.permissions && role.permissions.length > 3"
                      class="inline-flex items-center px-2 py-1 rounded text-xs font-medium bg-gray-200 text-gray-600"
                    >
                      +{{ role.permissions.length - 3 }} more
                    </span>
                  </div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                  {{ formatDate(role.createdAt) }}
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- Role Modal -->
    <RoleModal
      :is-open="roleModalOpen"
      :role="selectedRole"
      :authorities="authorities"
      :is-editing="isEditingRole"
      :action-type="modalActionType"
      @close="closeRoleModal"
      @save="saveRole"
      @delete="deleteRole"
    />
  </AuthenticatedLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { 
  Plus, RefreshCw, Loader2, AlertCircle, Shield, Key, Users, 
  Search, Eye, Edit, Trash2 
} from 'lucide-vue-next'
import AuthenticatedLayout from '@/layouts/AuthenticatedLayout.vue'
import RoleModal from '@/components/modals/RoleModal.vue'
import { useRoleStore, usePermissionStore } from '@/stores/authorization'
import { ADMIN_NAVIGATION } from '@/lib/navigation'

const roleStore = useRoleStore()
const permissionStore = usePermissionStore()
const adminNavigation = ADMIN_NAVIGATION

// Reactive state
const searchQuery = ref('')
const roleModalOpen = ref(false)
const selectedRole = ref(null)
const isEditingRole = ref(false)
const selectedRoles = ref([])
const modalActionType = ref('create') // 'create', 'edit', 'delete'

// Computed properties
const roles = computed(() => roleStore.allRoles || [])
const authorities = computed(() => permissionStore.availablePermissions || [])
const groupedAuthorities = computed(() => permissionStore.groupedPermissions || {})
const loading = computed(() => roleStore.loading || permissionStore.loading)
const error = computed(() => roleStore.error || permissionStore.error)

const filteredRoles = computed(() => {
  if (!searchQuery.value) return roles.value || []
  return (roles.value || []).filter(role => 
    role.name && role.name.toLowerCase().includes(searchQuery.value.toLowerCase())
  )
})

const isAllSelected = computed(() => {
  return filteredRoles.value.length > 0 && selectedRoles.value.length === filteredRoles.value.length
})

// Methods
const refreshData = async () => {
  await Promise.all([
    roleStore.fetchRoles(),
    permissionStore.fetchPermissions()
  ])
}

const openRoleModal = (role = null, actionType = 'create') => {
  selectedRole.value = role
  isEditingRole.value = !!role
  modalActionType.value = actionType
  roleModalOpen.value = true
}

const closeRoleModal = () => {
  roleModalOpen.value = false
  selectedRole.value = null
  isEditingRole.value = false
  modalActionType.value = 'create'
}

const editRole = (role) => {
  openRoleModal(role, 'edit')
}

const viewRoleDetails = (role) => {
  openRoleModal(role, 'edit')
}

const saveRole = async (roleData) => {
  let result
  if (isEditingRole.value) {
    result = await roleStore.updateRole(selectedRole.value.id, roleData)
  } else {
    result = await roleStore.createRole(roleData)
  }
  if (result.success) {
    closeRoleModal()
    alert(`Role ${isEditingRole.value ? 'updated' : 'created'} successfully!`)
  } else {
    alert(`Failed to ${isEditingRole.value ? 'update' : 'create'} role: ${result.error}`)
  }
}

const deleteRole = async () => {
  if (!selectedRole.value) return
  const result = await roleStore.updateRole(selectedRole.value.id)
  if (result.success) {
    closeRoleModal()
    selectedRoles.value = selectedRoles.value.filter(id => id !== selectedRole.value.id)
    alert('Role deleted successfully!')
  } else {
    alert(`Failed to delete role: ${result.error}`)
  }
}

const confirmDeleteRole = (role) => {
  if (confirm(`Are you sure you want to delete the role "${role.name}"? This action cannot be undone.`)) {
    roleStore.deleteRole(role.id).then(result => {
      if (result.success) {
        alert('Role deleted successfully!')
      } else {
        alert(`Failed to delete role: ${result.error}`)
      }
    })
  }
}

const toggleRoleSelection = (roleId) => {
  const index = selectedRoles.value.indexOf(roleId)
  if (index > -1) {
    selectedRoles.value.splice(index, 1)
  } else {
    selectedRoles.value.push(roleId)
  }
}

const toggleSelectAll = () => {
  if (isAllSelected.value) {
    selectedRoles.value = []
  } else {
    selectedRoles.value = filteredRoles.value.map(role => role.id)
  }
}

const isRoleSelected = (roleId) => {
  return selectedRoles.value.includes(roleId)
}

const editSelectedRole = () => {
  if (selectedRoles.value.length === 1) {
    const role = roles.value.find(r => r.id === selectedRoles.value[0])
    if (role) {
      editRole(role)
    }
  }
}

const deleteSelectedRoles = async () => {
  if (selectedRoles.value.length === 0) return
  if (selectedRoles.value.length === 1) {
    const role = roles.value.find(r => r.id === selectedRoles.value[0])
    if (role) {
      openRoleModal(role, 'delete')
    }
  } else {
    const roleNames = selectedRoles.value.map(id => {
      const role = roles.value.find(r => r.id === id)
      return role ? role.name : 'Unknown'
    }).join(', ')
    if (confirm(`Are you sure you want to delete the selected roles: ${roleNames}? This action cannot be undone.`)) {
      const deletePromises = selectedRoles.value.map(id => roleStore.deleteRole(id))
      try {
        const results = await Promise.all(deletePromises)
        const failedDeletes = results.filter(result => !result.success)
        if (failedDeletes.length === 0) {
          alert('All selected roles deleted successfully!')
          selectedRoles.value = []
        } else {
          alert(`Some roles could not be deleted. Please check the console for details.`)
        }
      } catch (error) {
        alert(`Failed to delete roles: ${error.message}`)
      }
    }
  }
}

const formatPermissionName = (name) => {
  return name.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, l => l.toUpperCase())
}

const formatDate = (dateString) => {
  if (!dateString) return 'N/A'
  return new Date(dateString).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}

onMounted(() => {
  refreshData()
})
</script>
