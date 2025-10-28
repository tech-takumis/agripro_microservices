<template>
  <AuthenticatedLayout 
    :navigation="adminNavigation" 
    role-title="Admin Portal"
    page-title="Register Staff"
  >
    <template #header>
      <div class="flex items-center justify-between">
        <div>
          <h1 class="text-2xl font-bold text-gray-900">Register New Staff</h1>
          <p class="text-gray-600">Create a new agriculture account for PCIC staff members</p>
        </div>
      </div>
    </template>

    <div class="max-w-4xl mx-auto">
      <div class="bg-white rounded-lg shadow-sm border border-gray-200">
        <div class="px-6 py-4 border-b border-gray-200">
          <h2 class="text-lg font-semibold text-gray-900">Staff Information</h2>
          <p class="text-sm text-gray-500">Fill in the details to create a new staff account.</p>
        </div>
        
        <form @submit.prevent="submitRegistration" class="p-6 space-y-6">
          <!-- Personal Information -->
          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label for="fullname" class="block text-sm font-medium text-gray-700 mb-1">
                Full Name <span class="text-red-500">*</span>
              </label>
              <input
                v-model="form.fullname"
                type="text"
                id="fullname"
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-purple-500 focus:border-purple-500"
                placeholder="e.g., Juan Dela Cruz"
              />
            </div>
            <div>
              <label for="email" class="block text-sm font-medium text-gray-700 mb-1">
                Email <span class="text-red-500">*</span>
              </label>
              <input
                v-model="form.email"
                type="email"
                id="email"
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-purple-500 focus:border-purple-500"
                placeholder="e.g., juan.delacruz@pcic.gov.ph"
              />
            </div>
            <div>
              <label for="gender" class="block text-sm font-medium text-gray-700 mb-1">
                Gender <span class="text-red-500">*</span>
              </label>
              <select
                v-model="form.gender"
                id="gender"
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-purple-500 focus:border-purple-500"
              >
                <option value="">Select Gender</option>
                <option value="Male">Male</option>
                <option value="Female">Female</option>
                <option value="Other">Other</option>
              </select>
            </div>
            <div>
              <label for="contactNumber" class="block text-sm font-medium text-gray-700 mb-1">
                Contact Number
              </label>
              <input
                v-model="form.contactNumber"
                type="text"
                id="contactNumber"
                class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-purple-500 focus:border-purple-500"
                placeholder="e.g., 09123456789"
              />
            </div>
            <div>
              <label for="civilStatus" class="block text-sm font-medium text-gray-700 mb-1">
                Civil Status
              </label>
              <input
                v-model="form.civilStatus"
                type="text"
                id="civilStatus"
                class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-purple-500 focus:border-purple-500"
                placeholder="e.g., Single, Married"
              />
            </div>
            <div class="md:col-span-2">
              <label for="address" class="block text-sm font-medium text-gray-700 mb-1">
                Address
              </label>
              <textarea
                v-model="form.address"
                id="address"
                rows="2"
                class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-purple-500 focus:border-purple-500"
                placeholder="Full address"
              ></textarea>
            </div>
          </div>

          <!-- Organizational Information -->
          <div class="grid grid-cols-1 md:grid-cols-2 gap-6 border-t border-gray-200 pt-6">
            <div>
              <label for="position" class="block text-sm font-medium text-gray-700 mb-1">
                Position <span class="text-red-500">*</span>
              </label>
              <input
                v-model="form.position"
                type="text"
                id="position"
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-purple-500 focus:border-purple-500"
                placeholder="e.g., Customer Service Officer"
              />
            </div>
            <div>
              <label for="department" class="block text-sm font-medium text-gray-700 mb-1">
                Department <span class="text-red-500">*</span>
              </label>
              <input
                v-model="form.department"
                type="text"
                id="department"
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-purple-500 focus:border-purple-500"
                placeholder="e.g., Agriculture Insurance"
              />
            </div>
            <div>
              <label for="location" class="block text-sm font-medium text-gray-700 mb-1">
                Location <span class="text-red-500">*</span>
              </label>
              <input
                v-model="form.location"
                type="text"
                id="location"
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-purple-500 focus:border-purple-500"
                placeholder="e.g., Bayugan City, Agusan del Sur"
              />
            </div>
            <div>
              <label for="role" class="block text-sm font-medium text-gray-700 mb-1">
                Role <span class="text-red-500">*</span>
              </label>
              <select
                v-model="form.roleId"
                id="role"
                required
                class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-purple-500 focus:border-purple-500"
              >
                <option value="">Select Role</option>
                <option v-for="roleOption in rolePermission?.roles" :key="roleOption?.id" :value="roleOption.id">
                  {{ roleOption.name }}
                </option>
              </select>
            </div>
          </div>

          <!-- Action Buttons -->
          <div class="flex justify-end space-x-4 pt-6 border-t border-gray-200">
            <button
              type="button"
              @click="resetForm"
              class="px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50"
            >
              Reset Form
            </button>
            <button
              type="submit"
              :disabled="processing"
              class="px-6 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-purple-600 hover:bg-purple-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-purple-500 disabled:opacity-50"
            >
              <Loader2 v-if="processing" class="animate-spin h-4 w-4 mr-2" />
              {{ processing ? 'Registering...' : 'Register Staff' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </AuthenticatedLayout>
</template>

<script setup>
import { onMounted, ref, computed } from 'vue'
import { ArrowLeft, Loader2 } from 'lucide-vue-next'
import AuthenticatedLayout from '../../../layouts/AuthenticatedLayout.vue'
import { useRoleStore, usePermissionStore } from '@/stores/authorization'
import { useAuthStore } from '@/stores/auth'
import { ADMIN_NAVIGATION } from '@/lib/navigation'

const roleStore = useRoleStore()
const permissionStore = usePermissionStore()
const authStore = useAuthStore()

const adminNavigation = ADMIN_NAVIGATION

const form = ref({
  fullname: '',
  email: '',
  gender: '',
  contactNumber: '',
  civilStatus: '',
  address: '',
  position: '',
  department: '',
  location: '',
  roleId: '',
})

const processing = ref(false)

const roles = computed(() => roleStore.allRoles || [])
const permissions = computed(() => permissionStore.availablePermissions || [])
const groupedPermissions = computed(() => permissionStore.groupedPermissions || {})
const loading = computed(() => roleStore.loading || permissionStore.loading)
const error = computed(() => roleStore.error || permissionStore.error)

const submitRegistration = async () => {
  processing.value = true
  
  const result = await authStore.registerStaff(form.value)

  if (result.success) {
    alert('Staff registered successfully!')
    resetForm() // Clear form after successful registration
  } else {
    alert(`Failed to register staff: ${result.error?.message || 'Unknown error'}. Please check console for details.`)
  }
  processing.value = false
}

const resetForm = () => {
  form.value = {
    fullname: '',
    email: '',
    gender: '',
    contactNumber: '',
    civilStatus: '',
    address: '',
    position: '',
    department: '',
    location: '',
    role: '',
  }
}

onMounted(() => {
  roleStore.fetchRoles()
  permissionStore.fetchPermissions()
})
</script>
