<template>
    <div class="min-h-screen flex flex-col md:flex-row bg-gray-100">
        <!-- Left Section -->
        <div
            class="flex-1 flex flex-col justify-center items-center p-10 animate-fade-in-left">
            <img
                src="@/assets/da_image.png"
                alt="Department of Agriculture Logo"
                class="w-40 h-auto mb-6 drop-shadow-lg" />
            <h1 class="text-3xl font-bold text-gray-700 mb-2 text-center">
                Department of Agriculture
            </h1>
            <h2 class="text-xl font-semibold text-gray-600 mb-4 text-center">
                Bayugan City
            </h2>
            <p
                class="text-sm text-gray-600 text-center max-w-md leading-relaxed">
                Welcome to the official Department of Agriculture Portal. Please
                sign in to access your dashboard and manage agricultural
                programs and resources.
            </p>
        </div>

        <!-- Right Section (Login Form) -->
        <div
            class="flex-1 flex items-center justify-center p-8 animate-fade-in-right">
            <div
                class="w-full max-w-md bg-white shadow-lg rounded-2xl p-8 border border-gray-100">
                <h2
                    class="text-2xl font-semibold text-gray-800 text-center mb-6">
                    Sign in to your account
                </h2>

                <!-- Status Message -->
                <div
                    v-if="status"
                    class="mb-6 bg-green-50 border border-green-200 rounded-md p-4 flex items-center">
                    <CheckCircle class="h-5 w-5 text-green-500 mr-2" />
                    <p class="text-sm text-green-800">{{ status }}</p>
                </div>

                <!-- Error Messages -->
                <div
                    v-if="errors && errors.length > 0"
                    class="mb-6 bg-red-50 border border-red-200 rounded-md p-4">
                    <div class="flex">
                        <AlertCircle class="h-5 w-5 text-red-500 mr-2" />
                        <div>
                            <h3 class="text-sm font-medium text-red-800 mb-1">
                                Please fix the following:
                            </h3>
                            <ul
                                class="list-disc pl-5 text-sm text-red-700 space-y-1">
                                <li v-for="error in errors" :key="error">
                                    {{ error }}
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>

                <!-- Login Form -->
                <form
                    class="space-y-5 max-w-sm mx-auto"
                    @submit.prevent="submitLogin">
                    <!-- Username -->
                    <div>
                        <label
                            for="username"
                            class="block text-sm font-medium text-gray-700 mb-1">
                            Username
                        </label>
                        <div class="relative">
                            <User
                                class="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 h-4 w-4" />
                            <input
                                id="username"
                                v-model="form.username"
                                type="text"
                                required
                                autocomplete="username"
                                placeholder="Enter your username"
                                :disabled="processing"
                                class="w-full pl-9 pr-3 py-2.5 text-sm border border-gray-300 rounded-md placeholder-gray-400 focus:border-green-400 focus:ring-2 focus:ring-green-400/40 transition duration-200 disabled:opacity-50" />
                        </div>
                    </div>

                    <!-- Password -->
                    <div>
                        <label
                            for="password"
                            class="block text-sm font-medium text-gray-700 mb-1">
                            Password
                        </label>
                        <div class="relative">
                            <Lock
                                class="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 h-4 w-4" />
                            <input
                                id="password"
                                v-model="form.password"
                                :type="showPassword ? 'text' : 'password'"
                                required
                                autocomplete="current-password"
                                placeholder="Enter your password"
                                :disabled="processing"
                                class="w-full pl-9 pr-3 py-2.5 text-sm border border-gray-300 rounded-md placeholder-gray-400 focus:border-green-400 focus:ring-2 focus:ring-green-400/40 transition duration-200 disabled:opacity-50" />
                            <button
                                type="button"
                                class="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600"
                                :disabled="processing"
                                @click="showPassword = !showPassword">
                                <Eye v-if="!showPassword" class="h-4 w-4" />
                                <EyeOff v-else class="h-4 w-4" />
                            </button>
                        </div>
                    </div>

                    <!-- Remember Me & Forgot Password -->
                    <div class="flex items-center justify-between">
                        <label
                            class="flex items-center space-x-2 text-xs text-gray-700">
                            <input
                                id="remember"
                                v-model="form.rememberMe"
                                type="checkbox"
                                class="rounded border-gray-300 text-green-600 focus:ring-green-500"
                                :disabled="processing" />
                            <span>Remember me</span>
                        </label>
                        <router-link
                            to="/forgot-password"
                            class="text-xs text-green-600 hover:text-green-500 font-medium">
                            Forgot password?
                        </router-link>
                    </div>

                    <!-- Submit Button -->
                    <button
                        type="button"
                        class="w-full py-2.5 rounded-md bg-green-600 hover:bg-green-700 text-white text-sm font-semibold shadow-sm flex items-center justify-center transition disabled:opacity-50 disabled:cursor-not-allowed"
                        :disabled="processing"
                        @click="submitLogin">
                        <Loader2
                            v-if="processing"
                            class="animate-spin h-4 w-4 mr-2" />
                        {{ processing ? 'Signing in...' : 'Sign In' }}
                    </button>
                </form>

                <!-- Footer -->
                <div class="mt-8 text-center text-xs text-gray-500">
                    <p>© 2024 Department of Agriculture – Bayugan City</p>
                    <p class="mt-1">For authorized personnel only</p>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { useRoute } from 'vue-router'
import { computed, ref, onMounted } from 'vue'
import {
    User,
    Lock,
    Eye,
    EyeOff,
    Loader2,
    AlertCircle,
    CheckCircle,
} from 'lucide-vue-next'
import { useAuthStore } from '@/stores/auth'
import { useNotificationStore } from '@/stores/notification'

const route = useRoute()
const authStore = useAuthStore()
const notificationStore = useNotificationStore()

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

onMounted(() => {
    console.log('[Login.vue] Login page mounted')
})

const submitLogin = async (event) => {
    console.log('submitLogin called', event)

    // Prevent default form submission
    if (event) {
        event.preventDefault()
        event.stopPropagation()
    }

    // Clear previous errors
    setErrors.value = []
    processing.value = true

    try {
        const result = await authStore.login(form, setErrors, processing)

        if (result?.success) {
            notificationStore.showSuccess(result.message || 'Login successful!')
        } else {
            notificationStore.showError(result?.message || 'Login failed. Please check your credentials.')
            setErrors.value = [result?.message || 'Login failed. Please check your credentials.']
        }
    } catch (error) {
        console.error('Login error:', error)
        const errorMessage = error.response?.data?.message || error.message || 'Login failed. Please try again.'
        notificationStore.showError(errorMessage)
        setErrors.value = [errorMessage]
    } finally {
        processing.value = false
    }
}

// Check for reset status from query params
const resetStatus = route.query.reset
if (resetStatus?.length > 0) {
    status.value = atob(resetStatus)
}
</script>

<style scoped>
@keyframes fade-in-left {
    from {
        opacity: 0;
        transform: translateX(-20px);
    }
    to {
        opacity: 1;
        transform: translateX(0);
    }
}

@keyframes fade-in-right {
    from {
        opacity: 0;
        transform: translateX(20px);
    }
    to {
        opacity: 1;
        transform: translateX(0);
    }
}

.animate-fade-in-left {
    animation: fade-in-left 0.8s ease-in-out;
}

.animate-fade-in-right {
    animation: fade-in-right 0.8s ease-in-out;
}
</style>
