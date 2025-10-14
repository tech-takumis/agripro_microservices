<template>
    <AuthenticatedLayout
        :navigation="adminNavigation"
        role-title="Admin Portal"
        page-title="Register Staff"
    >
        <template #header>
            <div class="flex items-center justify-between">
                <div>
                    <h1 class="text-3xl font-semibold text-gray-900">Register New Staff</h1>
                    <p class="mt-1 text-sm text-gray-500">Create a new agriculture account for PCIC staff members</p>
                </div>
            </div>
        </template>

        <div class="max-w-5xl mx-auto">
            <div class="bg-white rounded-xl shadow-sm border border-gray-100">
                <form class="p-8 space-y-8" @submit.prevent="submitRegistration">
                    <!-- Personal Information Section -->
                    <div class="space-y-6">
                        <div class="pb-3 border-b border-gray-100">
                            <h2 class="text-lg font-semibold text-gray-900">Personal Information</h2>
                            <p class="mt-0.5 text-sm text-gray-500">Basic details about the staff member</p>
                        </div>

                        <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
                            <div>
                                <label for="firstname" class="block text-sm font-medium text-gray-700 mb-1.5">
                                    First Name <span class="text-red-500">*</span>
                                </label>
                                <input
                                    id="firstname"
                                    v-model="form.firstname"
                                    type="text"
                                    required
                                    class="w-full px-3.5 py-2.5 text-sm border border-gray-200 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
                                    placeholder="Juan"
                                />
                                <p v-if="errors.firstname" class="mt-1.5 text-xs text-red-600">{{ errors.firstname }}</p>
                            </div>

                            <div>
                                <label for="lastname" class="block text-sm font-medium text-gray-700 mb-1.5">
                                    Last Name <span class="text-red-500">*</span>
                                </label>
                                <input
                                    id="lastname"
                                    v-model="form.lastname"
                                    type="text"
                                    required
                                    class="w-full px-3.5 py-2.5 text-sm border border-gray-200 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
                                    placeholder="Dela Cruz"
                                />
                                <p v-if="errors.lastname" class="mt-1.5 text-xs text-red-600">{{ errors.lastname }}</p>
                            </div>

                            <div>
                                <label for="email" class="block text-sm font-medium text-gray-700 mb-1.5">
                                    Email Address <span class="text-red-500">*</span>
                                </label>
                                <input
                                    id="email"
                                    v-model="form.email"
                                    type="email"
                                    required
                                    class="w-full px-3.5 py-2.5 text-sm border border-gray-200 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
                                    placeholder="juan.delacruz@pcic.gov.ph"
                                />
                                <p v-if="errors.email" class="mt-1.5 text-xs text-red-600">{{ errors.email }}</p>
                            </div>

                            <div>
                                <label for="phoneNumber" class="block text-sm font-medium text-gray-700 mb-1.5">
                                    Contact Number
                                </label>
                                <input
                                    id="phoneNumber"
                                    v-model="form.phoneNumber"
                                    type="text"
                                    class="w-full px-3.5 py-2.5 text-sm border border-gray-200 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
                                    placeholder="09123456789"
                                />
                                <p v-if="errors.phoneNumber" class="mt-1.5 text-xs text-red-600">{{ errors.phoneNumber }}</p>
                            </div>

                            <div>
                                <label for="civilStatus" class="block text-sm font-medium text-gray-700 mb-1.5">
                                    Civil Status
                                </label>
                                <select
                                    id="civilStatus"
                                    v-model="form.civilStatus"
                                    class="w-full px-3.5 py-2.5 text-sm border border-gray-200 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
                                >
                                    <option value="">Select Status</option>
                                    <option value="Single">Single</option>
                                    <option value="Married">Married</option>
                                    <option value="Widowed">Widowed</option>
                                    <option value="Separated">Separated</option>
                                </select>
                            </div>
                        </div>
                    </div>

                    <!-- Organizational Information Section -->
                    <div class="space-y-6">
                        <div class="pb-3 border-b border-gray-100">
                            <h2 class="text-lg font-semibold text-gray-900">Organizational Information</h2>
                            <p class="mt-0.5 text-sm text-gray-500">Position and role assignment</p>
                        </div>

                        <div class="grid grid-cols-1 md:grid-cols-2 gap-5">

                            <div>
                                <label for="role" class="block text-sm font-medium text-gray-700 mb-1.5">
                                    Primary Role <span class="text-red-500">*</span>
                                </label>
                                <select
                                    id="role"
                                    v-model="form.roleId"
                                    required
                                    class="w-full px-3.5 py-2.5 text-sm border border-gray-200 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
                                >
                                    <option value="">Select Role</option>
                                    <option v-for="roleOption in roleStore.allRoles" :key="roleOption?.id" :value="roleOption.id">
                                        {{ roleOption.name }}
                                    </option>
                                </select>
                                <p v-if="errors.roleId" class="mt-1.5 text-xs text-red-600">{{ errors.roleId }}</p>
                            </div>
                        </div>
                    </div>

                    <!-- Work Location Section -->
                    <div class="space-y-6">
                        <div class="pb-3 border-b border-gray-100">
                            <h2 class="text-lg font-semibold text-gray-900">Work Location</h2>
                            <p class="mt-0.5 text-sm text-gray-500">Assigned work location</p>
                        </div>

                        <PsgcLocationSelector
                            v-model="form.workLocation"
                            :required="true"
                            @update:model-value="handleWorkLocationUpdate"
                        />
                        <p v-if="errors.workLocation" class="mt-1.5 text-xs text-red-600">{{ errors.workLocation }}</p>
                    </div>

                    <!-- Residential Address Section -->
                    <div class="space-y-6">
                        <div class="pb-3 border-b border-gray-100">
                            <h2 class="text-lg font-semibold text-gray-900">Residential Address</h2>
                            <p class="mt-0.5 text-sm text-gray-500">Current home address</p>
                        </div>

                        <div class="grid grid-cols-1 gap-5">
                            <div>
                                <label for="street" class="block text-sm font-medium text-gray-700 mb-1.5">
                                    Street Address <span class="text-red-500">*</span>
                                </label>
                                <input
                                    id="street"
                                    v-model="form.street"
                                    type="text"
                                    required
                                    class="w-full px-3.5 py-2.5 text-sm border border-gray-200 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
                                    placeholder="P-7"
                                />
                            </div>

                            <div>
                                <PsgcLocationSelector
                                    v-model="form.residentialLocation"
                                    :required="true"
                                    @update:model-value="handleResidentialLocationUpdate"
                                />
                                <p v-if="errors.residentialLocation" class="mt-1.5 text-xs text-red-600">{{ errors.residentialLocation }}</p>
                            </div>

                            <div>
                                <label for="postalCode" class="block text-sm font-medium text-gray-700 mb-1.5">
                                    Postal Code <span class="text-red-500">*</span>
                                </label>
                                <input
                                    id="postalCode"
                                    v-model="form.postalCode"
                                    type="text"
                                    required
                                    class="w-full px-3.5 py-2.5 text-sm border border-gray-200 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
                                    placeholder="8600"
                                />
                            </div>
                        </div>
                    </div>

                    <!-- Additional Contact Section -->
                    <div class="space-y-6">
                        <div class="pb-3 border-b border-gray-100">
                            <h2 class="text-lg font-semibold text-gray-900">Additional Contact</h2>
                            <p class="mt-0.5 text-sm text-gray-500">Optional contact information</p>
                        </div>

                        <div>
                            <label for="publicAffairsEmail" class="block text-sm font-medium text-gray-700 mb-1.5">
                                Public Affairs Email
                            </label>
                            <input
                                id="publicAffairsEmail"
                                v-model="form.publicAffairsEmail"
                                type="email"
                                class="w-full px-3.5 py-2.5 text-sm border border-gray-200 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
                                placeholder="public.affairs@agriculture.gov.ph"
                            />
                        </div>
                    </div>

                    <!-- Success/Error Messages -->
                    <div v-if="successMessage" class="flex items-center gap-2 p-4 text-sm text-green-700 bg-green-50 border border-green-200 rounded-lg">
                        <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                            <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"/>
                        </svg>
                        {{ successMessage }}
                    </div>

                    <div v-if="errors.submit" class="flex items-center gap-2 p-4 text-sm text-red-700 bg-red-50 border border-red-200 rounded-lg">
                        <AlertCircle class="w-5 h-5 flex-shrink-0" />
                        {{ errors.submit }}
                    </div>

                    <!-- Action Buttons -->
                    <div class="flex justify-end gap-3 pt-6 border-t border-gray-100">
                        <button
                            type="button"
                            class="px-5 py-2.5 text-sm font-medium text-gray-700 bg-white border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors"
                            @click="resetForm"
                        >
                            Reset Form
                        </button>
                        <button
                            type="submit"
                            :disabled="processing"
                            class="px-6 py-2.5 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed transition-all flex items-center gap-2"
                        >
                            <Loader2 v-if="processing" class="animate-spin h-4 w-4" />
                            {{ processing ? 'Registering...' : 'Register Staff' }}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </AuthenticatedLayout>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { onMounted, ref, computed } from 'vue'
import { Loader2, AlertCircle } from 'lucide-vue-next'
import AuthenticatedLayout from '@/layouts/AuthenticatedLayout.vue'
import { useAuthStore } from '@/stores/auth'
import { useRoleStore } from '@/stores/role'
import { usePermissionStore } from '@/stores/permission'
import { usePsgcStore } from '@/stores/psgc'
import PsgcLocationSelector from '@/components/forms/PsgcLocationSelector.vue'
import { ADMIN_NAVIGATION } from '@/lib/navigation'
import { formatAddress } from '@/utils/formatAddress'

const router = useRouter()
const authStore = useAuthStore()
const roleStore = useRoleStore()
const permissionStore = usePermissionStore()
const psgcStore = usePsgcStore()

const adminNavigation = ADMIN_NAVIGATION

const form = ref({
    firstname: '',
    lastname: '',
    email: '',
    phoneNumber: '',
    civilStatus: '',
    publicAffairsEmail: '',
    street: '',
    country: 'Philippines',
    postalCode: '',
    workLocation: {
        region: '',
        province: '',
        municipality: '',
        barangay: '',
        locationString: ''
    },
    residentialLocation: {
        region: '',
        province: '',
        municipality: '',
        barangay: '',
        locationString: ''
    },
    roleId: [],
})

const errors = ref({})
const processing = ref(false)
const successMessage = ref('')

// Computed properties for formatted addresses
const fullHeadquartersAddress = computed(() => formatAddress(form.value.workLocation))
const fullResidentialAddress = computed(() => formatAddress(form.value.residentialLocation))

function handleWorkLocationUpdate(newLocation) {
    form.value.workLocation = { ...newLocation }
}

function handleResidentialLocationUpdate(newLocation) {
    form.value.residentialLocation = { ...newLocation }
}

const getLocationName = (code, locationList) => {
    if (!code) return '';
    const location = locationList.find(item => item.code === code);
    return location ? location.name : code;
};

const validateForm = () => {
    const newErrors = {}

    const requiredFields = {
        firstname: 'First Name',
        lastname: 'Last Name',
        email: 'Email',
        roleId: 'Role'
    }

    Object.entries(requiredFields).forEach(([field, label]) => {
        if (!form.value[field]) {
            newErrors[field] = `${label} is required`
        }
    })

    if (form.value.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.value.email)) {
        newErrors.email = 'Please enter a valid email address'
    }

    if (form.value.phoneNumber && !/^(09|\+639)\d{9}$/.test(form.value.phoneNumber)) {
        newErrors.phoneNumber = 'Please enter a valid Philippine mobile number'
    }

    errors.value = newErrors
    return Object.keys(newErrors).length === 0
}

const submitRegistration = async () => {
    if (!validateForm()) {
        return
    }
    processing.value = true
    successMessage.value = ''
    try {
        const transformedData = {
            firstName: form.value.firstname,
            lastName: form.value.lastname,
            email: form.value.email.toLowerCase(),
            phoneNumber: form.value.phoneNumber,
            civilStatus: form.value.civilStatus,
            address: fullResidentialAddress.value,
            rolesId: Array.isArray(form.value.roleId) ? form.value.roleId : [form.value.roleId],
            headquartersAddress: fullHeadquartersAddress.value,
            publicAffairsEmail: form.value.publicAffairsEmail,
            street: form.value.street,
            barangay: getLocationName(form.value.residentialLocation.barangay, psgcStore.getBarangays),
            city: getLocationName(form.value.residentialLocation.municipality, psgcStore.getMunicipalities),
            province: getLocationName(form.value.residentialLocation.province, psgcStore.getProvinces),
            region: getLocationName(form.value.residentialLocation.region, psgcStore.getRegions),
            country: form.value.country,
            postalCode: form.value.postalCode
        }

        console.log('Transformed Registration Data:', transformedData)
        // Call the auth store's register function
        const result = await authStore.register(transformedData)
        if (result.success) {
            successMessage.value = 'Staff registered successfully!'
            resetForm()
            setTimeout(() => {
                router.push({ name: 'admin-users' })
            }, 2000)
        } else {
            errors.value = { submit: result.error || 'Failed to register staff' }
        }
    } catch (error) {
        console.error('Registration error:', error)
        errors.value = { submit: 'An unexpected error occurred. Please try again.' }
    } finally {
        processing.value = false
    }
}

const resetForm = () => {
    form.value = {
        firstname: '',
        lastname: '',
        email: '',
        phoneNumber: '',
        civilStatus: '',
        publicAffairsEmail: '',
        street: '',
        country: 'Philippines',
        postalCode: '',
        workLocation: {
            region: '',
            province: '',
            municipality: '',
            barangay: '',
            locationString: ''
        },
        residentialLocation: {
            region: '',
            province: '',
            municipality: '',
            barangay: '',
            locationString: ''
        },
        roleId: [],
    }
    errors.value = {}
    successMessage.value = ''
}

onMounted(async () => {
    await Promise.all([
        roleStore.fetchRoles(),
        permissionStore.fetchPermissions()
    ])
})
</script>