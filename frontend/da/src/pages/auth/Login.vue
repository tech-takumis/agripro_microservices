<template>
  <div class="min-h-screen bg-gray-50 flex items-center justify-center p-6">
    <!-- Login Card -->
    <div class="w-full max-w-md bg-white shadow-lg rounded-2xl p-8">
      <!-- Logo -->
      <div class="text-center mb-8">
        <img 
          src="@/assets/da_image.png" 
          alt="PCIC Logo" 
          class="w-24 h-auto mx-auto mb-3"
        />
        <h2 class="text-2xl font-bold text-gray-900">Department of Agriculture</h2>
        <h1 class="text-2xl font-bold text-gray-900">Bayugan City</h1>
        <p class="text-sm text-gray-500 mt-1">Sign in to access your dashboard</p>
      </div>

      <!-- Status Message -->
      <div v-if="status" class="mb-6 bg-green-50 border border-green-200 rounded-md p-4 flex items-center">
        <CheckCircle class="h-5 w-5 text-green-500 mr-2" />
        <p class="text-sm text-green-800">{{ status }}</p>
      </div>

      <!-- Error Messages -->
      <div v-if="errors && errors.length > 0" class="mb-6 bg-red-50 border border-red-200 rounded-md p-4">
        <div class="flex">
          <AlertCircle class="h-5 w-5 text-red-500 mr-2" />
          <div>
            <h3 class="text-sm font-medium text-red-800 mb-1">Please fix the following:</h3>
            <ul class="list-disc pl-5 text-sm text-red-700 space-y-1">
              <li v-for="error in errors" :key="error">{{ error }}</li>
            </ul>
          </div>
        </div>
      </div>

      <!-- Login Form -->
      <form @submit.prevent="submitLogin" class="space-y-6">
        <!-- Username -->
        <div>
          <label for="username" class="block text-sm font-medium text-gray-700 mb-1">
            Username
          </label>
          <div class="relative">
            <User class="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 h-5 w-5" />
            <input
              id="username"
              v-model="form.username"
              type="text"
              required
              autocomplete="username"
              placeholder="Enter your username"
              :disabled="processing"
              class="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg shadow-sm placeholder-gray-400 focus:ring-2 focus:ring-green-500 focus:border-green-500 transition disabled:opacity-50"
            />
          </div>
        </div>

        <!-- Password -->
        <div>
          <label for="password" class="block text-sm font-medium text-gray-700 mb-1">
            Password
          </label>
          <div class="relative">
            <Lock class="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 h-5 w-5" />
            <input
              id="password"
              v-model="form.password"
              :type="showPassword ? 'text' : 'password'"
              required
              autocomplete="current-password"
              placeholder="Enter your password"
              :disabled="processing"
              class="w-full pl-10 pr-10 py-3 border border-gray-300 rounded-lg shadow-sm placeholder-gray-400 focus:ring-2 focus:ring-green-500 focus:border-green-500 transition disabled:opacity-50"
            />
            <button
              type="button"
              @click="showPassword = !showPassword"
              class="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600"
              :disabled="processing"
            >
              <Eye v-if="!showPassword" class="h-5 w-5" />
              <EyeOff v-else class="h-5 w-5" />
            </button>
          </div>
        </div>

        <!-- Remember Me & Forgot -->
        <div class="flex items-center justify-between">
          <label class="flex items-center space-x-2 text-sm text-gray-700">
            <input
              id="remember"
              v-model="form.rememberMe"
              type="checkbox"
              class="rounded border-gray-300 text-green-600 focus:ring-green-500"
              :disabled="processing"
            />
            <span>Remember me</span>
          </label>
          <router-link
            to="/forgot-password"
            class="text-sm text-green-600 hover:text-green-500 font-medium"
          >
            Forgot password?
          </router-link>
        </div>

        <!-- Submit -->
        <button
          type="submit"
          :disabled="processing"
          class="w-full py-3 rounded-lg bg-green-600 hover:bg-green-700 text-white font-medium shadow-md flex items-center justify-center transition disabled:opacity-50 disabled:cursor-not-allowed"
        >
          <Loader2 v-if="processing" class="animate-spin h-4 w-4 mr-2" />
          {{ processing ? 'Signing in...' : 'Sign In' }}
        </button>
      </form>

      <!-- Footer -->
      <div class="mt-8 text-center text-xs text-gray-500">
        <p>© 2024 Philippine Crop Insurance Corporation</p>
        <p class="mt-1">For authorized staff only</p>
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
const authStore = useAuthStore()

const form = ref({
  username: '',
  password: '',
    tenantKey: "agriculture",
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

const submitLogin = async () => {
  // Clear previous errors
  setErrors.value = [];
  
  try {
     await authStore.login(form, setErrors, processing);
  } catch (error) {
    console.error('Login error:', error);
    setErrors.value = [error.message || 'Login failed. Please try again.'];
  } finally {
    if (processing.value) processing.value = false;
  }
}

// Check for reset status from query params
const resetStatus = route.query.reset
if (resetStatus?.length > 0) {
  status.value = atob(resetStatus)
}
</script>