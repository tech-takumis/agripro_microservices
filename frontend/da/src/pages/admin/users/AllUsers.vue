<template>
  <AuthenticatedLayout 
    :navigation="adminNavigation" 
    role-title="Admin Portal"
    page-title="All Users"
  >
    <!-- Users Content -->
    <div class="space-y-6">
      <!-- Search and Filter -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200">
        <div class="p-3 flex items-center justify-between gap-4">
          <div class="relative w-64">
            <Search class="absolute left-2 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
            <input
              v-model="searchTerm"
              type="text"
              placeholder="Search users..."
              class="w-full pl-8 pr-3 py-1.5 text-sm border border-gray-300 rounded-md focus:outline-none focus:ring-1 focus:ring-purple-500 focus:border-purple-500"
              @input="handleSearch"
            />
          </div>
          <select
            v-model="selectedRole"
            class="text-sm px-3 py-1.5 border border-gray-300 rounded-md focus:outline-none focus:ring-1 focus:ring-purple-500 focus:border-purple-500 min-w-[160px]"
            @change="handleRoleFilter"
          >
            <option value="">All Roles</option>
            <option
              v-for="role in roleOptions"
              :key="role.id"
              :value="role.name"
            >
              {{ role.name }}
            </option>
          </select>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="userStore.isLoading && (!userStore.allAgricultureUsers || userStore.allAgricultureUsers.length === 0)" class="bg-white rounded-lg shadow-sm border border-gray-200">
        <div class="p-6">
          <div class="text-center py-12">
            <Loader2 class="mx-auto h-12 w-12 text-purple-600 animate-spin" />
            <h3 class="mt-2 text-sm font-medium text-gray-900">Loading Users</h3>
            <p class="mt-1 text-sm text-gray-500">Please wait while we fetch all users...</p>
          </div>
        </div>
      </div>
      <!-- Error State -->
      <div v-else-if="userStore.getError" class="bg-white rounded-lg shadow-sm border border-red-200">
        <div class="p-6">
          <div class="text-center py-12">
            <AlertCircle class="mx-auto h-12 w-12 text-red-500" />
            <h3 class="mt-2 text-sm font-medium text-red-900">Error Loading Users</h3>
            <p class="mt-1 text-sm text-red-600">{{ userStore.getError }}</p>
            <button
              class="mt-4 bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded-md text-sm font-medium transition-colors"
              @click="refreshUsers"
            >
              Try Again
            </button>
          </div>
        </div>
      </div>

      <!-- Users Table -->
      <div v-else-if="filteredUsers.length > 0" class="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
        <div class="px-6 py-4 border-b border-gray-200">
          <div class="flex items-center justify-between">
            <h2 class="text-lg font-semibold text-gray-900">
              System Users ({{ filteredUsers.length }})
            </h2>
            <div class="flex items-center space-x-3">
              <button
                v-if="selectedUsers.length > 0"
                class="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-red-600 hover:bg-red-700"
                @click="deleteSelectedUsers"
              >
                <Trash2 class="h-4 w-4 mr-2" />
                Delete Selected ({{ selectedUsers.length }})
              </button>
              <!-- Add New User Button triggers modal -->
              <button
                class="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-purple-600 hover:bg-purple-700"
                @click="showInviteModal = true"
              >
                <UserPlus class="h-4 w-4 mr-2" />
                Add New User
              </button>
            </div>
          </div>
        </div>
        
        <div class="overflow-x-auto">
          <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-6 py-3 text-left">
                  <input
                    type="checkbox"
                    :checked="isAllSelected"
                    class="h-4 w-4 text-purple-600 border-gray-300 rounded focus:ring-purple-500"
                    @change="toggleAllSelection"
                  />
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  User
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Contact Information
                </th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Address
                </th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              <tr
                v-for="agriculture in filteredUsers"
                :key="agriculture.id"
                class="hover:bg-gray-50 cursor-pointer"
                @click="viewUser(agriculture)"
              >
                <td class="px-6 py-4 whitespace-nowrap" @click.stop>
                  <input
                    type="checkbox"
                    :checked="isUserSelected(agriculture)"
                    class="h-4 w-4 text-purple-600 border-gray-300 rounded focus:ring-purple-500"
                    @change="toggleUserSelection(agriculture)"
                  />
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="flex items-center">
                    <div class="flex-shrink-0 h-10 w-10">
                      <div class="h-10 w-10 rounded-full bg-purple-100 flex items-center justify-center">
                        <span class="text-sm font-medium text-purple-600">
                          {{ getUserInitials(agriculture) }}
                        </span>
                      </div>
                    </div>
                    <div class="ml-4">
                      <div class="text-sm font-medium text-gray-900">
                        {{ getUserFullName(agriculture) }}
                      </div>
                      <div class="text-sm text-gray-500">
                        @{{ agriculture.username }}
                      </div>
                    </div>
                  </div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="text-sm text-gray-900">
                    <div class="flex items-center mb-1">
                      <Mail class="h-4 w-4 text-gray-400 mr-2" />
                      {{ agriculture.email || 'No email' }}
                    </div>
                    <div class="flex items-center">
                      <Phone class="h-4 w-4 text-gray-400 mr-2" />
                      {{ agriculture.phoneNumber || 'No phone' }}
                    </div>
                  </div>
                </td>
                <td class="px-6 py-4">
                  <div class="text-sm text-gray-900 max-w-xs">
                    <div class="flex items-start">
                      <MapPin class="h-4 w-4 text-gray-400 mr-2 mt-0.5 flex-shrink-0" />
                      <span class="break-words">{{ agriculture.address || 'No address provided' }}</span>
                    </div>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Pagination -->
        <div v-if="userStore.getPagination.totalPages > 1" class="px-6 py-4 border-t border-gray-200">
          <div class="flex items-center justify-between">
            <div class="text-sm text-gray-700">
              Showing {{ ((userStore.getPagination.currentPage - 1) * userStore.getPagination.itemsPerPage) + 1 }} 
              to {{ Math.min(userStore.getPagination.currentPage * userStore.getPagination.itemsPerPage, userStore.getPagination.totalItems) }} 
              of {{ userStore.getPagination.totalItems }} results
            </div>
            <div class="flex space-x-2">
              <button
                :disabled="userStore.getPagination.currentPage <= 1"
                class="px-3 py-1 border border-gray-300 rounded-md text-sm disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
                @click="changePage(userStore.getPagination.currentPage - 1)"
              >
                Previous
              </button>
              <button
                :disabled="userStore.getPagination.currentPage >= userStore.getPagination.totalPages"
                class="px-3 py-1 border border-gray-300 rounded-md text-sm disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-50"
                @click="changePage(userStore.getPagination.currentPage + 1)"
              >
                Next
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Empty State -->
      <div v-else class="bg-white rounded-lg shadow-sm border border-gray-200">
        <div class="p-6">
          <div class="text-center py-12">
            <Users class="mx-auto h-12 w-12 text-gray-400" />
            <h3 class="mt-2 text-sm font-medium text-gray-900">No Users Found</h3>
            <p class="mt-1 text-sm text-gray-500">
              {{ searchTerm || selectedRole ? 'No users match your search criteria.' : 'No users have been added to the system yet.' }}
            </p>

          </div>
        </div>
      </div>
    </div>

    <!-- Add New User Modal -->
    <template v-if="showInviteModal">
      <div class="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-30">
        <div class="bg-white rounded-lg shadow-lg p-6 max-w-sm w-full">
          <h2 class="text-lg font-semibold mb-2 text-purple-700">Invite New User</h2>
          <label class="block mb-2 text-sm font-medium text-gray-700">Email Address</label>
          <input v-model="inviteEmail" type="email" placeholder="Enter staff email" class="w-full px-3 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-purple-500 mb-4" />
          <div v-if="inviteError" class="text-red-600 text-sm mb-2">{{ inviteError }}</div>
          <div v-if="inviteSuccess" class="text-green-600 text-sm mb-2">{{ inviteSuccess }}</div>
          <div class="flex justify-end gap-2">
            <button @click="showInviteModal = false" class="px-4 py-2 bg-gray-200 rounded hover:bg-gray-300">Cancel</button>
            <button @click="handleInvite" :disabled="inviteLoading" class="px-4 py-2 bg-purple-600 text-white rounded hover:bg-purple-700 disabled:opacity-50">{{ inviteLoading ? 'Inviting...' : 'Send Invite' }}</button>
          </div>
        </div>
      </div>
    </template>
  </AuthenticatedLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
    Users, UserPlus, RefreshCw, Search, Loader2, AlertCircle,
    Mail, Phone, MapPin, Eye, Edit, Trash2
} from 'lucide-vue-next'
import AuthenticatedLayout from '@/layouts/AuthenticatedLayout.vue'
import { useAgricultureStore } from '@/stores/agriculture'
import { useRoleStore } from '@/stores/role'
import { useAuthStore } from '@/stores/auth'
import { useNotificationStore } from '@/stores/notification'
import { ADMIN_NAVIGATION } from '@/lib/navigation'

const router = useRouter()
const userStore = useAgricultureStore()
const roleStore = useRoleStore()
const authStore = useAuthStore()
const notificationStore = useNotificationStore()
const adminNavigation = ADMIN_NAVIGATION

// Reactive variables
const searchTerm = ref('')
const selectedRole = ref('')
const isInitialized = ref(false)
const showInviteModal = ref(false)
const inviteEmail = ref('')
const inviteLoading = ref(false)
const inviteError = ref('')
const inviteSuccess = ref('')

// Computed properties
const roleOptions = computed(() => {
    return roleStore.allRoles.map(role => ({
        id: role.id,
        name: role.name,
        slug: role.slug
    }))
})

const filteredUsers = computed(() => {
    let users = userStore.allAgricultureUsers || []

    // Filter by search term
    if (searchTerm.value) {
        const search = searchTerm.value.toLowerCase()
        users = users.filter(agriculture => {
            const fullName = getUserFullName(agriculture).toLowerCase()
            const email = (agriculture.email || '').toLowerCase()
            const phone = (agriculture.phoneNumber || '').toLowerCase()
            const username = (agriculture.username || '').toLowerCase()

            return fullName.includes(search) ||
                email.includes(search) ||
                phone.includes(search) ||
                username.includes(search)
        })
    }

    // Filter by role
    if (selectedRole.value) {
        users = users.filter(agriculture =>
            agriculture.roles && agriculture.roles.some(role => role.name.toLowerCase() === selectedRole.value.toLowerCase())
        )
    }

    return users
})

// Add selectedUsers state
const selectedUsers = ref([])

// Add selection computed property
const isAllSelected = computed(() => {
  return filteredUsers.value.length > 0 &&
    filteredUsers.value.every(user => selectedUsers.value.includes(user.id))
})

// Data fetching functions
const fetchUsers = async () => {
    try {
        await userStore.fetchAgricultureUsers({
            search: searchTerm.value,
            page: userStore.getPagination.page,
            size: userStore.getPagination.size
        })
        isInitialized.value = true
    } catch (error) {
        console.error('Failed to fetch users:', error)
    }
}

const refreshUsers = async () => {
    userStore.clearError()
    await fetchUsers()
}

// Search and filter handlers
const handleSearch = () => {
    // Search is handled reactively by the computed property
}

const handleRoleFilter = () => {
    // Role filtering is handled reactively by the filteredUsers computed property
    // This function can be used for additional role filtering logic if needed
    console.log('Role filter changed:', selectedRole.value)
}

// Helper functions
const getUserFullName = (agriculture) => {
    if (!agriculture) return 'Unknown User'
    if (agriculture.firstName && agriculture.lastName) {
        return `${agriculture.firstName} ${agriculture.lastName}`
    }
    return agriculture.username || 'Unknown User'
}

const getUserInitials = (agriculture) => {
    const fullName = getUserFullName(agriculture)
    if (fullName === 'Unknown User') return 'U'

    return fullName
        .split(' ')
        .map(name => name[0])
        .join('')
        .toUpperCase()
        .substring(0, 2)
}

// Pagination functions
const changePage = async (page) => {
    if (page >= 1 && page <= userStore.getPagination.totalPages) {
        await userStore.fetchAgricultureUsers({ page })
    }
}

// User management functions
const viewUser = (agriculture) => {
    router.push(`/admin/users/${agriculture.id}`)
}

const editUser = (agriculture) => {
    router.push(`/admin/users/${agriculture.id}/edit`)
}

const deleteUser = async (agriculture) => {
    if (confirm(`Are you sure you want to delete user "${getUserFullName(agriculture)}"? This action cannot be undone.`)) {
        try {
            const result = await userStore.deleteAgricultureUser(agriculture.id)
            if (result.success) {
                alert('User deleted successfully!')
                await refreshUsers()
            } else {
                alert(`Failed to delete user: ${result.error}`)
            }
        } catch (error) {
            console.error('Error deleting user:', error)
            alert('An error occurred while deleting the user.')
        }
    }
}

// Add selection methods
const isUserSelected = (user) => selectedUsers.value.includes(user.id)

const toggleAllSelection = () => {
  if (isAllSelected.value) {
    selectedUsers.value = []
  } else {
    selectedUsers.value = filteredUsers.value.map(user => user.id)
  }
}

const toggleUserSelection = (user) => {
  const index = selectedUsers.value.indexOf(user.id)
  if (index === -1) {
    selectedUsers.value.push(user.id)
  } else {
    selectedUsers.value.splice(index, 1)
  }
}

const deleteSelectedUsers = async () => {
  if (confirm(`Are you sure you want to delete ${selectedUsers.value.length} selected users? This action cannot be undone.`)) {
    try {
      for (const userId of selectedUsers.value) {
        await userStore.deleteAgricultureUser(userId)
      }
      selectedUsers.value = []
      await refreshUsers()
      alert('Selected users deleted successfully!')
    } catch (error) {
      console.error('Error deleting users:', error)
      alert('An error occurred while deleting the selected users.')
    }
  }
}

// Invite new user function
const handleInvite = async () => {
  inviteError.value = ''
  inviteSuccess.value = ''
  inviteLoading.value = true
  try {
    const result = await authStore.inviteStaff(inviteEmail.value)
    if (result.success) {
      notificationStore.showSuccess(result.message || 'Invitation sent successfully!')
      inviteSuccess.value = 'Invitation sent successfully!'
      inviteEmail.value = ''
      showInviteModal.value = false
    } else {
      notificationStore.showError(result.message || 'Failed to send invitation.')
      inviteError.value = result.message || 'Failed to send invitation.'
    }
  } catch (err) {
    const errorMessage = err.message || 'Failed to send invitation.'
    notificationStore.showError(errorMessage)
    inviteError.value = errorMessage
  } finally {
    inviteLoading.value = false
  }
}

// Lifecycle hooks
onMounted(async () => {
    await Promise.all([
        roleStore.fetchRoles(),
        fetchUsers()
    ])
})
</script>