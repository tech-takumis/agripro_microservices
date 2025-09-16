<template>
  <div class="min-h-screen bg-gray-50 flex">
    <!-- Left Column - PCIC Image (Hidden on mobile) -->
    <div class="hidden lg:flex lg:w-1/2 bg-gradient-to-br from-green-600 to-green-800 items-center justify-center p-8">
      <div class="max-w-md text-center">
        <img 
          src="https://hebbkx1anhila5yf.public.blob.vercel-storage.com/pcic.jpg-ymdsA0RBXJ1O58Wx4oDrmGSD8rRBY0.jpeg" 
          alt="Philippine Crop Insurance Corporation Logo" 
          class="w-64 h-auto mx-auto mb-8 bg-white rounded-lg p-6 shadow-lg"
        />
        <h1 class="text-3xl font-bold text-white mb-4">
          Philippine Crop Insurance Corporation
        </h1>
        <p class="text-green-100 text-lg">
          Staff Portal - Managing crop insurance for Filipino farmers
        </p>
      </div>
    </div>

    <!-- Right Column - Login Form -->
    <div class="w-full lg:w-1/2 flex items-center justify-center p-8">
      <div class="w-full max-w-md">
        <!-- Mobile Logo (Visible only on small screens) -->
        <div class="lg:hidden text-center mb-8">
          <img 
            src="https://hebbkx1anhila5yf.public.blob.vercel-storage.com/pcic.jpg-ymdsA0RBXJ1O58Wx4oDrmGSD8rRBY0.jpeg" 
            alt="PCIC Logo" 
            class="w-32 h-auto mx-auto mb-4"
          />
          <h2 class="text-xl font-bold text-gray-900">PCIC Staff Portal</h2>
        </div>

        <!-- Desktop Header -->
        <div class="hidden lg:block mb-8">
          <h2 class="text-3xl font-bold text-gray-900 mb-2">Staff Login</h2>
          <p class="text-gray-600">Access your PCIC staff dashboard</p>
        </div>

        <!-- Status Message -->
        <div v-if="status" class="mb-6 bg-green-50 border border-green-200 rounded-md p-4">
          <div class="flex">
            <CheckCircle class="h-5 w-5 text-green-400" />
            <div class="ml-3">
              <p class="text-sm text-green-800">{{ status }}</p>
            </div>
          </div>
        </div>

        <!-- Error Messages -->
        <div v-if="errors && errors.length > 0" class="mb-6">
          <div class="bg-red-50 border border-red-200 rounded-md p-4">
            <div class="flex">
              <AlertCircle class="h-5 w-5 text-red-400" />
              <div class="ml-3">
                <h3 class="text-sm font-medium text-red-800">
                  Please correct the following errors:
                </h3>
                <div class="mt-2 text-sm text-red-700">
                  <ul class="list-disc pl-5 space-y-1">
                    <li v-for="error in errors" :key="error">
                      {{ error }}
                    </li>
                  </ul>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Login Form -->
        <form @submit.prevent="submitLogin" class="space-y-6">
          <!-- Username Field -->
          <div>
            <label for="username" class="block text-sm font-medium text-gray-700 mb-1">
              Username
            </label>
            <div class="relative">
              <input
                id="username"
                v-model="form.username"
                type="text"
                required
                autofocus
                autocomplete="username"
                class="w-full px-4 py-3 pl-10 border border-gray-300 rounded-lg shadow-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-green-500 focus:border-green-500 transition-colors"
                :class="{ 'border-red-300 focus:ring-red-500 focus:border-red-500': hasFieldError('username') }"
                placeholder="Enter your username"
                :disabled="processing"
              />
              <User class="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
            </div>
          </div>

          <!-- Password Field -->
          <div>
            <label for="password" class="block text-sm font-medium text-gray-700 mb-1">
              Password
            </label>
            <div class="relative">
              <input
                id="password"
                v-model="form.password"
                :type="showPassword ? 'text' : 'password'"
                required
                autocomplete="current-password"
                class="w-full px-4 py-3 pl-10 pr-10 border border-gray-300 rounded-lg shadow-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-green-500 focus:border-green-500 transition-colors"
                :class="{ 'border-red-300 focus:ring-red-500 focus:border-red-500': hasFieldError('password') }"
                placeholder="Enter your password"
                :disabled="processing"
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
          <div class="flex items-center justify-between">
            <div class="flex items-center">
              <input
                id="remember"
                v-model="form.rememberMe"
                type="checkbox"
                class="h-4 w-4 text-green-600 focus:ring-green-500 border-gray-300 rounded"
                :disabled="processing"
              />
              <label for="remember" class="ml-2 block text-sm text-gray-700">
                Remember me
              </label>
            </div>
            <router-link
              to="/forgot-password"
              class="text-sm text-green-600 hover:text-green-500 underline transition-colors"
            >
              Forgot password?
            </router-link>
          </div>

          <!-- Submit Button -->
          <button
            type="submit"
            :disabled="processing"
            class="w-full flex justify-center py-3 px-4 border border-transparent rounded-lg shadow-sm text-sm font-medium text-white bg-green-600 hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
          >
            <Loader2 v-if="processing" class="animate-spin h-4 w-4 mr-2" />
            {{ processing ? 'Signing in...' : 'Sign In' }}
          </button>
        </form>

        <!-- Footer -->
        <div class="mt-8 text-center">
          <p class="text-xs text-gray-500">
            © 2024 Philippine Crop Insurance Corporation. All rights reserved.
          </p>
          <p class="text-xs text-gray-400 mt-1">
            Staff Portal Access Only
          </p>
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
import { useUserStore } from '@/stores/user'

const route = useRoute()
const store = useUserStore()

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