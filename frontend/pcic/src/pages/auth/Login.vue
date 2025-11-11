<template>
  <div class="min-h-screen bg-gray-100 flex">
<!-- Left Column - PCIC Image (Hidden on mobile) -->
<div class="hidden lg:flex lg:w-1/2 bg-gray-50 items-center justify-center p-8">
  <div class="max-w-md text-center">
    <img 
      src="@/assets/pcic.svg" 
      alt="Philippine Crop Insurance Corporation Logo" 
      class="w-28 h-auto mx-auto mb-6" 
    />

    <h1 class="text-3xl font-bold text-gray-900 mb-2">
      Philippine Crop Insurance Corporation
    </h1>
    <p class="text-gray-600 text-lg">
      Staff Portal - Managing crop insurance for Filipino farmers
    </p>
  </div>
</div>



   <!-- Right Column - Login Form -->
<div class="w-full lg:w-1/2 flex items-center justify-center p-8 bg-gray-50 min-h-screen">
  <div class="w-full max-w-md bg-white rounded-xl shadow-lg p-8">
    
    <!-- Logo (mobile & desktop) -->
    <div class="text-center mb-6">
      <h2 class="text-2xl font-bold text-gray-900 mb-1 lg:hidden">PCIC Staff Portal</h2>
      <h2 class="text-3xl font-bold text-gray-900 mb-1 hidden lg:block">Staff Login</h2>
      <p class="text-gray-600 text-sm lg:text-base">Access your PCIC staff dashboard</p>
    </div>

    <!-- Status / Error Messages -->
    <div v-if="status" class="mb-4 bg-green-50 border border-green-200 rounded-md p-3 flex items-center">
      <CheckCircle class="h-5 w-5 text-green-400" />
      <p class="ml-2 text-sm text-green-800">{{ status }}</p>
    </div>

    <div v-if="errors && errors.length > 0" class="mb-4">
      <div class="bg-red-50 border border-red-200 rounded-md p-3">
        <div class="flex items-start">
          <AlertCircle class="h-5 w-5 text-red-400 mt-0.5" />
          <div class="ml-2">
            <h3 class="text-sm font-medium text-red-800">Please correct the following errors:</h3>
            <ul class="mt-1 list-disc pl-5 text-sm text-red-700 space-y-0.5">
              <li v-for="error in errors" :key="error">{{ error }}</li>
            </ul>
          </div>
        </div>
      </div>
    </div>

    <!-- Login Form -->
    <form @submit.prevent="submitLogin" class="space-y-4">
      
      <!-- Username Field -->
      <div>
        <label for="username" class="block text-sm font-medium text-gray-700 mb-1">Username</label>
        <div class="relative">
          <input
            id="username"
            v-model="form.username"
            type="text"
            required
            autofocus
            autocomplete="username"
            :disabled="processing"
            placeholder="Enter your username"
            class="w-full pl-10 pr-3 py-2 border border-gray-300 rounded-lg shadow-sm text-sm placeholder-gray-400 focus:border-green-400 focus:ring-2 focus:ring-green-400/40 transition duration-200 disabled:opacity-50"
            :class="{ 'border-red-300 focus:ring-red-500 focus:border-red-500': hasFieldError('username') }"
          />
          <User class="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
        </div>
      </div>

      <!-- Password Field -->
      <div>
        <label for="password" class="block text-sm font-medium text-gray-700 mb-1">Password</label>
        <div class="relative">
          <input
            id="password"
            v-model="form.password"
            :type="showPassword ? 'text' : 'password'"
            required
            autocomplete="current-password"
            :disabled="processing"
            placeholder="Enter your password"
            class="w-full pl-10 pr-10 py-2 border border-gray-300 rounded-lg shadow-sm text-sm placeholder-gray-400 focus:border-green-400 focus:ring-2 focus:ring-green-400/40 transition duration-200 disabled:opacity-50"
            :class="{ 'border-red-300 focus:ring-red-500 focus:border-red-500': hasFieldError('password') }"
          />
          <Lock class="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
          <button
            type="button"
            @click="showPassword = !showPassword"
            class="absolute right-3 top-1/2 transform -translate-y-1/2"
            :disabled="processing"
          >
            <Eye v-if="!showPassword" class="h-5 w-5 text-gray-400 hover:text-gray-600" />
            <EyeOff v-else class="h-5 w-5 text-gray-400 hover:text-gray-600" />
          </button>
        </div>
      </div>

      <!-- Remember Me & Forgot Password -->
      <div class="flex items-center justify-between text-sm">
        <label class="flex items-center gap-2">
          <input type="checkbox" v-model="form.rememberMe" :disabled="processing" class="h-4 w-4 text-green-600 border-gray-300 rounded focus:ring-green-500" />
          Remember me
        </label>
        <router-link to="/forgot-password" class="text-green-600 hover:text-green-500 underline transition-colors">
          Forgot password?
        </router-link>
      </div>

      <!-- Submit Button -->
      <button
        type="submit"
        :disabled="processing"
        class="w-full flex justify-center py-2 px-4 border border-transparent rounded-lg shadow-sm text-sm font-medium text-white bg-green-600 hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
      >
        <Loader2 v-if="processing" class="animate-spin h-4 w-4 mr-2" />
        {{ processing ? 'Signing in...' : 'Sign In' }}
      </button>
    </form>

    <!-- Footer -->
    <div class="mt-6 text-center text-xs text-gray-500 space-y-1">
      <p>© 2024 Philippine Crop Insurance Corporation. All rights reserved.</p>
      <p class="text-gray-400">Staff Portal Access Only</p>
    </div>
  </div>
</div>
  </div>
</template>

<script setup>
import { useRoute } from 'vue-router'
import { computed, ref } from 'vue'
import { 
  User, Lock, Eye, EyeOff, Loader2, AlertCircle, CheckCircle 
} from 'lucide-vue-next'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const store = useAuthStore()

const form = ref({
  username: '',
  password: '',
  rememberMe: false,
})

const processing = ref(false)
const setErrors = ref([])
const showPassword = ref(false)

const errors = computed(() => setErrors.value)
const status = ref(null)

// Helper function to check if field has error
const hasFieldError = (field) => {
  if (!errors.value || !Array.isArray(errors.value)) return false
  return errors.value.some(error => 
    error.toLowerCase().includes(field.toLowerCase())
  )
}

const submitLogin = () => {
  // Clear previous errors
  setErrors.value = []
  
  // Call store login method with the same signature as your existing store
  store.login(form, setErrors, processing)
}

// Check for reset status from query params
const resetStatus = route.query.reset
if (resetStatus?.length > 0) {
  status.value = atob(resetStatus)
}
</script>