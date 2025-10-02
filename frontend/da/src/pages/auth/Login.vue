<template>
  <div class="min-h-screen flex flex-col lg:flex-row bg-gray-50">
    <!-- Left Column (Branding) -->
    <div class="hidden lg:flex lg:w-1/2 bg-gradient-to-br from-green-700 to-green-900 items-center justify-center p-10 relative overflow-hidden">
      <!-- Background pattern -->
      <div class="absolute inset-0 opacity-10 bg-[url('https://www.toptal.com/designers/subtlepatterns/patterns/leaf.png')]"></div>

      <div class="relative z-10 max-w-lg text-center text-white">
        <img
          src="https://hebbkx1anhila5yf.public.blob.vercel-storage.com/pcic.jpg-ymdsA0RBXJ1O58Wx4oDrmGSD8rRBY0.jpeg"
          alt="PCIC Logo"
          class="w-40 h-auto mx-auto mb-8 bg-white/90 rounded-xl p-5 shadow-xl"
        />
        <h1 class="text-4xl font-extrabold mb-4">Philippine Crop Insurance Corporation</h1>
        <p class="text-green-100 text-lg leading-relaxed">
          Empowering Filipino farmers with secure crop insurance and reliable support.
        </p>
      </div>
    </div>

    <!-- Right Column (Login Form) -->
    <div class="w-full lg:w-1/2 flex items-center justify-center p-6 lg:p-12">
      <div class="w-full max-w-md bg-white rounded-2xl shadow-xl p-8 lg:p-10">
        <!-- Mobile Branding -->
        <div class="lg:hidden text-center mb-8">
          <img
            src="https://hebbkx1anhila5yf.public.blob.vercel-storage.com/pcic.jpg-ymdsA0RBXJ1O58Wx4oDrmGSD8rRBY0.jpeg"
            alt="PCIC Logo"
            class="w-24 h-auto mx-auto mb-3"
          />
          <h2 class="text-xl font-bold text-gray-900">PCIC Staff Portal</h2>
        </div>

        <!-- Header -->
        <div class="mb-8 text-center lg:text-left">
          <h2 class="text-2xl lg:text-3xl font-bold text-gray-900">Welcome Back</h2>
          <p class="text-gray-500">Sign in to access your staff dashboard</p>
        </div>

        <!-- Status / Errors -->
        <div v-if="status" class="mb-6 bg-green-50 border border-green-200 text-green-800 text-sm rounded-lg p-4 flex items-start">
          <CheckCircle class="h-5 w-5 text-green-500 mr-2 mt-0.5" />
          {{ status }}
        </div>

        <div v-if="errors && errors.length" class="mb-6 bg-red-50 border border-red-200 text-red-800 text-sm rounded-lg p-4">
          <h3 class="font-medium mb-2">Please fix the following:</h3>
          <ul class="list-disc list-inside space-y-1">
            <li v-for="error in errors" :key="error">{{ error }}</li>
          </ul>
        </div>

        <!-- Login Form -->
        <form @submit.prevent="submitLogin" class="space-y-5">
          <!-- Username -->
          <div>
            <label for="username" class="block text-sm font-medium text-gray-700 mb-1">Username</label>
            <div class="relative">
              <User class="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 h-5 w-5" />
              <input
                id="username"
                v-model="form.username"
                type="text"
                placeholder="Enter username"
                autocomplete="username"
                required
                :disabled="processing"
                class="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg shadow-sm text-gray-900 placeholder-gray-400 focus:ring-2 focus:ring-green-500 focus:border-green-500 transition disabled:opacity-50"
              />
            </div>
          </div>

          <!-- Password -->
          <div>
            <label for="password" class="block text-sm font-medium text-gray-700 mb-1">Password</label>
            <div class="relative">
              <Lock class="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 h-5 w-5" />
              <input
                id="password"
                v-model="form.password"
                :type="showPassword ? 'text' : 'password'"
                placeholder="Enter password"
                autocomplete="current-password"
                required
                :disabled="processing"
                class="w-full pl-10 pr-10 py-3 border border-gray-300 rounded-lg shadow-sm text-gray-900 placeholder-gray-400 focus:ring-2 focus:ring-green-500 focus:border-green-500 transition disabled:opacity-50"
              />
              <button
                type="button"
                @click="showPassword = !showPassword"
                class="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600"
              >
                <Eye v-if="!showPassword" class="h-5 w-5" />
                <EyeOff v-else class="h-5 w-5" />
              </button>
            </div>
          </div>

          <!-- Remember Me / Forgot -->
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
        <div class="mt-8 text-center text-xs text-gray-400">
          <p>© 2024 Philippine Crop Insurance Corporation</p>
          <p class="mt-1">For authorized staff only</p>
        </div>
      </div>
    </div>
  </div>
</template>


<script setup>
import { useRoute, useRouter } from 'vue-router'
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

const router = useRouter();

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