<template>
  <AuthenticatedLayout 
    :navigation="adminNavigation" 
    role-title="Admin Portal"
    page-title="Register Staff"
  >
  <template #header>
    <div class="flex items-center justify-between bg-gray-100 px-6 py-4 border-b border-gray-200 rounded-lg">
    
<!-- Left: Logo + Title -->
<div class="flex items-center space-x-3">
  <!-- Logo -->
  <img 
    src="@/assets/pcic-logo.png" 
    alt="PCIC Logo" 
    class="h-10 w-auto"
  />

  <!-- Title -->
  <div>
    <h1 class="text-2xl font-bold text-green-800">Register Staff</h1>
    <p class="text-sm text-black-600">
      Fill out the form below to onboard a new staff member.
    </p>
  </div>
</div>


    <!-- Right: Search + Actions -->
    <div class="flex items-center gap-4">

      <!-- Notification Bell -->
      <button class="relative p-2 rounded-full hover:bg-green-100 transition">
        <svg
          class="w-6 h-6 text-gray-600"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
          viewBox="0 0 24 24"
        >
          <path stroke-linecap="round" stroke-linejoin="round"
            d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 
            00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 
            11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 
            3 0 11-6 0v-1m6 0H9" />
        </svg>
        <!-- Badge -->
        <span class="absolute top-1 right-1 block w-2 h-2 bg-red-500 rounded-full"></span>
      </button>

      <!-- Profile Dropdown -->
      <div class="relative">
        <button class="flex items-center gap-2 focus:outline-none">
          <img
            class="w-8 h-8 rounded-full border border-gray-200"
            src="https://ui-avatars.com/api/?name=Admin+User&background=16a34a&color=fff"
            alt="Profile"
          />
          <span class="hidden md:block text-sm font-medium text-gray-700">Admin User</span>
          <svg class="w-4 h-4 text-gray-500" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" d="M19 9l-7 7-7-7" />
          </svg>
        </button>

        <!-- Dropdown Menu -->
        <div
          class="absolute right-0 mt-2 w-48 bg-white border border-gray-200 rounded-lg shadow-lg py-2 hidden group-hover:block"
        >
          <a href="#" class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-50">Profile</a>
          <a href="#" class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-50">Settings</a>
          <div class="border-t my-1"></div>
          <a href="#" class="block px-4 py-2 text-sm text-red-600 hover:bg-red-50">Logout</a>
        </div>
      </div>
    </div>
  </div>
</template>

 
<!-- Main Form Cards -->
<div class="max-w-7xl mx-auto mt-2 grid grid-cols-1 lg:grid-cols-2 gap-6">
  <!-- Personal Information Card -->
  <div class="bg-white rounded-xl shadow-md border border-gray-100">
    <div class="px-6 py-4 border-b border-gray-200 bg-gray-50 rounded-t-xl">
      <h2 class="text-lg font-semibold text-green-800">Personal Information</h2>
      <p class="text-sm text-black-600">Fill in staff personal details.</p>
    </div>

    <div class="p-6 space-y-4">
      <!-- Full Name -->
      <div>
        <label for="fullname" class="block text-sm font-medium text-green-700 mb-1">
          Full Name <span class="text-red-500">*</span>
        </label>
        <div class="relative">
          <User class="absolute left-3 top-2.5 h-5 w-5 text-gray-400" />
          <input
            v-model="form.fullname"
            type="text"
            id="fullname"
            required
            class="w-full pl-10 pr-3 py-2 border border-gray-300 rounded-lg shadow-sm focus:ring-green-500 focus:border-green-500"
            placeholder="e.g., Juan Dela Cruz"
          />
        </div>
      </div>

      <!-- Email -->
      <div>
        <label for="email" class="block text-sm font-medium text-green-700 mb-1">
          Email <span class="text-red-500">*</span>
        </label>
        <div class="relative">
          <Mail class="absolute left-3 top-2.5 h-5 w-5 text-gray-400" />
          <input
            v-model="form.email"
            type="email"
            id="email"
            required
            class="w-full pl-10 pr-3 py-2 border border-gray-300 rounded-lg shadow-sm focus:ring-green-500 focus:border-green-500"
            placeholder="e.g., juan.delacruz@pcic.gov.ph"
          />
        </div>
      </div>

      <!-- Gender -->
      <div>
        <label for="gender" class="block text-sm font-medium text-green-700 mb-1">
          Gender <span class="text-red-500">*</span>
        </label>
        <div class="relative">
          <Heart class="absolute left-3 top-2.5 h-5 w-5 text-gray-400" />
          <select
            v-model="form.gender"
            id="gender"
            required
            class="w-full pl-10 pr-3 py-2 border border-gray-300 rounded-lg shadow-sm focus:ring-green-500 focus:border-green-500"
          >
            <option value="">Select Gender</option>
            <option value="Male">Male</option>
            <option value="Female">Female</option>
            <option value="Other">Other</option>
          </select>
        </div>
      </div>

      <!-- Contact Number -->
      <div>
        <label for="contactNumber" class="block text-sm font-medium text-green-700 mb-1">
          Contact Number
        </label>
        <div class="relative">
          <Phone class="absolute left-3 top-2.5 h-5 w-5 text-gray-400" />
          <input
            v-model="form.contactNumber"
            type="text"
            id="contactNumber"
            class="w-full pl-10 pr-3 py-2 border border-gray-300 rounded-lg shadow-sm focus:ring-green-500 focus:border-green-500"
            placeholder="e.g., 09123456789"
          />
        </div>
      </div>

      <!-- Civil Status -->
      <div>
        <label for="civilStatus" class="block text-sm font-medium text-green-700 mb-1">
          Civil Status
        </label>
        <div class="relative">
          <Heart class="absolute left-3 top-2.5 h-5 w-5 text-gray-400" />
          <input
            v-model="form.civilStatus"
            type="text"
            id="civilStatus"
            class="w-full pl-10 pr-3 py-2 border border-gray-300 rounded-lg shadow-sm focus:ring-green-500 focus:border-green-500"
            placeholder="e.g., Single, Married"
          />
        </div>
      </div>
    </div>
  </div>

  <!-- Organizational Information Card -->
  <div class="bg-white rounded-xl shadow-md border border-gray-100 flex flex-col">
    <div class="px-6 py-4 border-b border-gray-200 bg-gray-50 rounded-t-xl">
      <h2 class="text-lg font-semibold text-green-800">Organizational Information</h2>
      <p class="text-sm text-black-600">Assign role and department details.</p>
    </div>

    <div class="p-6 space-y-4 flex-1">
      <!-- Position -->
      <div>
        <label for="position" class="block text-sm font-medium text-green-700 mb-1">
          Position <span class="text-red-500">*</span>
        </label>
        <div class="relative">
          <Briefcase class="absolute left-3 top-2.5 h-5 w-5 text-gray-400" />
          <input
            v-model="form.position"
            type="text"
            id="position"
            required
            class="w-full pl-10 pr-3 py-2 border border-gray-300 rounded-lg shadow-sm focus:ring-green-500 focus:border-green-500"
            placeholder="e.g., Customer Service Officer"
          />
        </div>
      </div>

      <!-- Department -->
      <div>
        <label for="department" class="block text-sm font-medium text-green-700 mb-1">
          Department <span class="text-red-500">*</span>
        </label>
        <div class="relative">
          <Building2 class="absolute left-3 top-2.5 h-5 w-5 text-gray-400" />
          <input
            v-model="form.department"
            type="text"
            id="department"
            required
            class="w-full pl-10 pr-3 py-2 border border-gray-300 rounded-lg shadow-sm focus:ring-green-500 focus:border-green-500"
            placeholder="e.g., Agriculture Insurance"
          />
        </div>
      </div>

      <!-- Location -->
      <div>
        <label for="location" class="block text-sm font-medium text-green-700 mb-1">
          Location <span class="text-red-500">*</span>
        </label>
        <div class="relative">
          <MapPin class="absolute left-3 top-2.5 h-5 w-5 text-gray-400" />
          <input
            v-model="form.location"
            type="text"
            id="location"
            required
            class="w-full pl-10 pr-3 py-2 border border-gray-300 rounded-lg shadow-sm focus:ring-green-500 focus:border-green-500"
            placeholder="e.g., Bayugan City, Agusan del Sur"
          />
        </div>
      </div>

      <!-- Role -->
      <div>
        <label for="role" class="block text-sm font-medium text-green-700 mb-1">
          Role <span class="text-red-500">*</span>
        </label>
        <div class="relative">
          <Shield class="absolute left-3 top-2.5 h-5 w-5 text-gray-400" />
          <select
            v-model="form.roleId"
            id="role"
            required
            class="w-full pl-10 pr-3 py-2 border border-gray-300 rounded-lg shadow-sm focus:ring-green-500 focus:border-green-500"
          >
            <option value="">Select Role</option>
            <option v-for="roleOption in rolePermission?.roles" :key="roleOption?.id" :value="roleOption.id">
              {{ roleOption.name }}
            </option>
          </select>
        </div>
      </div>
    </div>

    <!-- Actions -->
    <div class="flex justify-end space-x-4 p-6 border-t border-gray-200">
      <button
        type="button"
        @click="resetForm"
        class="px-4 py-2 border border-gray-300 rounded-lg text-sm font-medium text-gray-700 bg-white hover:bg-gray-50"
      >
        Reset Form
      </button>
      <button
        type="submit"
        :disabled="processing"
        class="flex items-center justify-center px-6 py-2 rounded-lg text-sm font-medium text-white bg-green-600 hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500 disabled:opacity-50"
      >
        <Loader2 v-if="processing" class="animate-spin h-4 w-4 mr-2" />
        {{ processing ? 'Registering...' : 'Register Staff' }}
      </button>
    </div>
  </div>
</div>
  </AuthenticatedLayout>
</template>


<script setup>
import { onMounted, ref } from 'vue'
import { User, Mail, Phone, Heart, Briefcase, Building2, MapPin, Shield } from 'lucide-vue-next'
import { ArrowLeft, Loader2 } from 'lucide-vue-next'
import AuthenticatedLayout from '../../../layouts/AuthenticatedLayout.vue'
import { useUserStore } from '@/stores/user'
import { ADMIN_NAVIGATION } from '@/lib/constants'
import { useRolePermissionStore } from '../../../stores/rolePermission'

const userStore = useUserStore()
const rolePermission = useRolePermissionStore()

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

const submitRegistration = async () => {
  processing.value = true
  
  const result = await userStore.registerStaff(form.value)

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
  rolePermission.fetchRoles()
})
</script>
