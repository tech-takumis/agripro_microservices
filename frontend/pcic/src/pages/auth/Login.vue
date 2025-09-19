<template>
  <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-white to-gray-100 px-8">
    <!-- Transition wrapper -->
    <transition
      enter-active-class="duration-700 ease-out"
      enter-from-class="opacity-0 translate-y-8 scale-95"
      enter-to-class="opacity-100 translate-y-0 scale-100"
    >
      <!-- Login Card -->
      <div
        v-if="showCard"
        class="w-full max-w-md bg-white/80 backdrop-blur-md rounded-2xl p-8 transition-all"
      >
        <!-- Header -->
        <div class="text-center mb-6">
          <!-- PCIC Logo -->
          <div class="flex justify-center mb-4">
            <img src="@/assets/pcic-logo.png" alt="PCIC Logo" class="h-20 w-auto object-contain" />
          </div>

          <h1 class="text-3xl font-extrabold text-gray-900 tracking-tight">
            PCIC Staff Portal
          </h1>
          <p class="text-sm text-gray-500 mt-1">Secure access for authorized staff</p>
        </div>

        <!-- Status Message -->
        <div v-if="status" class="mb-5 bg-green-50 border border-green-200 rounded-lg p-3">
          <div class="flex items-center gap-2">
            <CheckCircle class="h-5 w-5 text-green-500" />
            <p class="text-sm text-green-700">{{ status }}</p>
          </div>
        </div>

        <!-- Errors -->
        <div v-if="errors && errors.length > 0" class="mb-5 bg-red-50 border border-red-200 rounded-lg p-3">
          <div class="flex items-start gap-2">
            <AlertCircle class="h-5 w-5 text-red-500 mt-0.5" />
            <div>
              <h3 class="text-sm font-semibold text-red-800">Please correct:</h3>
              <ul class="list-disc pl-5 mt-1 text-sm text-red-700 space-y-1">
                <li v-for="error in errors" :key="error">{{ error }}</li>
              </ul>
            </div>
          </div>
        </div>

        <!-- Login Form -->
        <form @submit.prevent="submitLogin">
          <!-- Username -->
          <div class="mb-5">
            <label for="username" class="block text-sm font-semibold text-gray-700 mb-1">Username</label>
            <div class="relative">
              <input
                id="username"
                v-model="form.username"
                type="text"
                required
                autocomplete="username"
                class="w-full px-4 py-3 pl-10 border border-gray-300 rounded-lg shadow-sm text-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-green-500 focus:border-green-500 transition duration-200"
                :class="{ 'border-red-300 focus:ring-red-500 focus:border-red-500': hasFieldError('username') }"
                placeholder="Enter username"
                :disabled="processing"
              />
              <User class="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-gray-400" />
            </div>
          </div>

          <!-- Password -->
          <div class="mb-5">
            <label for="password" class="block text-sm font-semibold text-gray-700 mb-1">Password</label>
            <div class="relative">
              <input
                id="password"
                v-model="form.password"
                :type="showPassword ? 'text' : 'password'"
                required
                autocomplete="current-password"
                class="w-full px-4 py-3 pl-10 pr-10 border border-gray-300 rounded-lg shadow-sm text-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-green-500 focus:border-green-500 transition duration-200"
                :class="{ 'border-red-300 focus:ring-red-500 focus:border-red-500': hasFieldError('password') }"
                placeholder="Enter password"
                :disabled="processing"
              />
              <Lock class="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-gray-400" />
              <button
                type="button"
                @click="showPassword = !showPassword"
                class="absolute right-3 top-1/2 -translate-y-1/2"
                :disabled="processing"
              >
                <Eye v-if="!showPassword" class="h-4 w-4 text-gray-400 hover:text-gray-600 transition duration-200" />
                <EyeOff v-else class="h-4 w-4 text-gray-400 hover:text-gray-600 transition duration-200" />
              </button>
            </div>
          </div>

          <!-- Options -->
          <div class="flex items-center justify-between text-sm mt-6 mb-6">
            <label class="flex items-center gap-2 cursor-pointer">
              <input
                id="remember"
                v-model="form.rememberMe"
                type="checkbox"
                class="h-4 w-4 text-green-600 focus:ring-green-500 border-gray-300 rounded"
                :disabled="processing"
              />
              <span class="text-gray-600">Remember me</span>
            </label>
            <router-link
              to="/forgot-password"
              class="text-green-600 hover:text-green-700 font-medium transition duration-200"
            >
              Forgot password?
            </router-link>
          </div>

          <!-- Submit -->
          <button
            type="submit"
            :disabled="processing"
            class="w-full flex justify-center py-3 px-4 rounded-lg shadow-md text-sm font-semibold text-white bg-green-600 hover:bg-green-700 hover:scale-[1.02] focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500 disabled:opacity-50 transition-all duration-200"
          >
            <Loader2 v-if="processing" class="animate-spin h-4 w-4 mr-2" />
            {{ processing ? 'Signing in...' : 'Sign In' }}
          </button>
        </form>

        <!-- Footer -->
        <div class="mt-8 text-center text-xs text-gray-500">
          <p>© 2024 Philippine Crop Insurance Corporation</p>
          <p class="mt-1 text-gray-400">Authorized Staff Access Only</p>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { useRoute } from 'vue-router'
import { computed, ref, onMounted } from 'vue'
import { User, Lock, Eye, EyeOff, Loader2, AlertCircle, CheckCircle } from 'lucide-vue-next'
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
const showCard = ref(false) // animation flag

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
  setErrors.value = []
  store.login(form, setErrors, processing)
}

// Fade-in animation on mount
onMounted(() => {
  setTimeout(() => {
    showCard.value = true
  }, 100)
})

// Check for reset status from query params
const resetStatus = route.query.reset
if (resetStatus?.length > 0) {
  status.value = atob(resetStatus)
}
</script>
