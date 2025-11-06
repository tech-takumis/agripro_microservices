<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import GuestLayout from '@/layouts/GuestLayout.vue'
import TextInput from '@/components/TextInput.vue'
import InputLabel from '@/components/InputLabel.vue'
import ValidationErrors from '@/components/ValidationErrors.vue'
import { useAuthStore } from '@/stores/auth'
import { useNotificationStore } from '@/stores/notification'
import { usePsgcStore } from '@/stores/psgc'
import { Eye, EyeOff } from 'lucide-vue-next'

const authStore = useAuthStore()
const notificationStore = useNotificationStore()
const psgcStore = usePsgcStore()
const route = useRoute()
const router = useRouter()

const form = ref({
    firstName: '',
    lastName: '',
    email: '',
    phoneNumber: '',
    address: '',
    roleNames: [],
    headquartersAddress: '',
    publicAffairsEmail: '',
    street: '',
    country: 'Philippines',
    postalCode: ''
})
const password = ref('')
const password_confirmation = ref('')
const setErrors = ref([])
const processing = ref(false)
const successMessage = ref('')
const errorMessage = ref('')
const passwordMatchError = ref('')

const errors = computed(() => setErrors.value)

const roleNamesInput = ref('')
const staticRoles = [
    'Agricultural Extension Workers',
    'ADMIN',
    'Municipal Agriculturists'
]
const selectedRoles = ref([])

const phonePrefixes = [
    '+63', // Philippines
    '+1', // USA/Canada
    '+44', // UK
    '+61', // Australia
    '+81', // Japan
    '+86', // China
    '+91', // India
    '+49', // Germany
    '+33', // France
    '+39', // Italy
    '+7',  // Russia
    '+82', // South Korea
    '+34', // Spain
    '+971', // UAE
    '+852', // Hong Kong
    '+65', // Singapore
    '+62', // Indonesia
    '+55', // Brazil
    '+27', // South Africa
    '+20', // Egypt
]
const selectedPrefix = ref(phonePrefixes[0])

const requiredFields = [
    'firstName', 'lastName', 'email', 'phoneNumber', 'headquartersAddress', 'publicAffairsEmail', 'street', 'country', 'postalCode'
]

function formatPhoneNumber(phone) {
    let cleaned = phone.replace(/[^\d+]/g, '').replace(/\s+/g, '');
    if (cleaned.startsWith('+63') && cleaned.length === 13) {
        return cleaned;
    }
    if (cleaned.startsWith('63') && cleaned.length === 12) {
        return '+' + cleaned;
    }
    return cleaned;
}

function isValidPassword(password) {
    // At least 8 chars, uppercase, lowercase, number, special char
    return /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[@$!%*?&#])[A-Za-z\d@$!%*?&#]{8,}$/.test(password);
}

onMounted(async () => {
    await psgcStore.initialize();
    roleNamesInput.value = (form.value.roleNames || []).join(', ')
})

watch([password, password_confirmation], ([newPassword, newConfirm]) => {
    if (newPassword && newConfirm && newPassword !== newConfirm) {
        passwordMatchError.value = 'Passwords do not match.'
    } else {
        passwordMatchError.value = ''
    }
})

const submitRegister = async () => {
    setErrors.value = []
    errorMessage.value = ''
    successMessage.value = ''
    processing.value = true

    console.log('[Register] Submit button clicked');
    const validationErrors = [];
    if (!password.value) validationErrors.push('Password: This field is required.');
    if (!password_confirmation.value) validationErrors.push('Confirm Password: This field is required.');
    if (password.value !== password_confirmation.value) validationErrors.push('Passwords do not match.');

    // PSGC required fields validation - use the actual getter values, not .value
    const missingFields = [];
    if (!psgcStore.getSelectedRegion) missingFields.push('Region');
    if (!psgcStore.getSelectedProvince) missingFields.push('Province');
    if (!psgcStore.getSelectedMunicipality) missingFields.push('City/Municipality');
    if (!psgcStore.getSelectedBarangay) missingFields.push('Barangay');
    if (missingFields.length > 0) {
        validationErrors.push(`Please select: ${missingFields.join(', ')}`);
    }

    if (validationErrors.length > 0) {
        setErrors.value = validationErrors
        errorMessage.value = 'Please fix the errors above before submitting.'
        console.warn('[Register] Validation failed:', validationErrors)
        processing.value = false
        return
    }

    const token = route.query.token
    if (!token) {
        errorMessage.value = 'Invalid or missing invitation token.'
        setErrors.value = ['Invitation token is required for registration.']
        console.warn('[Register] Missing token in URL:', route.fullPath)
        processing.value = false
        return
    }

    const userData = {
        ...form.value,
        password: password.value,
        password_confirmation: password_confirmation.value,
        region: psgcStore.getSelectedRegion?.name || '',
        province: psgcStore.getSelectedProvince?.name || '',
        city: psgcStore.getSelectedMunicipality?.name || '',
        barangay: psgcStore.getSelectedBarangay?.name || '',
        address: [
            form.value.street,
            psgcStore.getSelectedBarangay?.name,
            psgcStore.getSelectedMunicipality?.name,
            psgcStore.getSelectedProvince?.name,
            psgcStore.getSelectedRegion?.name,
            form.value.country
        ].filter(Boolean).join(', '),
        phoneNumber: selectedPrefix.value + form.value.phoneNumber.replace(/[^\d]/g, ''),
        roleNames: Array.isArray(selectedRoles.value) ? selectedRoles.value : [selectedRoles.value]
    }
    console.log('[Register] Registration payload:', userData)

    console.log('[Register] PSGC state before submit:', {
        selectedRegion: psgcStore.getSelectedRegion,
        selectedRegionCode: psgcStore.getSelectedRegionCode,
        selectedProvince: psgcStore.getSelectedProvince,
        selectedProvinceCode: psgcStore.getSelectedProvinceCode,
        selectedMunicipality: psgcStore.getSelectedMunicipality,
        selectedMunicipalityCode: psgcStore.getSelectedMunicipalityCode,
        selectedBarangay: psgcStore.getSelectedBarangay,
        selectedBarangayCode: psgcStore.getSelectedBarangayCode,
        regionsList: psgcStore.getRegionsList,
        provincesList: psgcStore.getProvincesList,
        municipalitiesList: psgcStore.getMunicipalitiesList,
        barangaysList: psgcStore.getBarangaysList,
    });

    try {
        const result = await authStore.register(userData, token)
        console.log('[Register] Registration API result:', result)
        if (result.success) {
            notificationStore.showSuccess(result.message || 'Registration successful!')
            successMessage.value = result.message || 'Registration successful!'
            router.push({name: 'login'})
        } else {
            notificationStore.showError(result.message || 'Registration failed.')
            errorMessage.value = result.message || 'Registration failed.'
            setErrors.value = [result.message || 'Registration failed.']
            console.error('[Register] Registration failed:', result.message)
        }
    } catch (err) {
        errorMessage.value = err.message || 'Registration failed.'
        setErrors.value = [err.message]
        console.error('[Register] Registration exception:', err)
    } finally {
        processing.value = false
    }
}

const showPassword = ref(false)
const showConfirmPassword = ref(false)
</script>

<template>
    <GuestLayout :full-page="true">
    <form class="flex flex-col h-screen bg-gray-50" @submit.prevent="submitRegister">
        <!-- Header -->
        <div class="px-8 py-8 bg-white border-b border-gray-200">
            <div class="max-w-7xl mx-auto">
                <h1 class="text-2xl font-bold text-gray-900">Create account</h1>
                <p class="mt-2 text-sm text-gray-600">Complete your registration to get started</p>
            </div>
        </div>

        <!-- Content -->
        <div class="flex-1 overflow-y-auto">
            <div class="px-8 py-8">
                <div class="max-w-7xl mx-auto">
                    <!-- Alerts -->
                    <div class="mb-6">
                        <ValidationErrors :errors="errors" />
                        <div v-if="errorMessage" class="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded text-sm">
                            {{ errorMessage }}
                        </div>
                    </div>

                    <!-- Form Sections Grid -->
                    <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
                        <!-- Personal Information -->
                        <section class="space-y-5">
                            <div class="pb-4 border-b border-gray-200">
                                <h2 class="text-sm font-semibold text-gray-900 uppercase tracking-wider">Personal</h2>
                            </div>
                            <div>
                                <InputLabel for="firstName" value="First Name" />
                                <TextInput
                                    id="firstName"
                                    v-model="form.firstName"
                                    type="text"
                                    required
                                    autocomplete="given-name"
                                />
                            </div>
                            <div>
                                <InputLabel for="lastName" value="Last Name" />
                                <TextInput
                                    id="lastName"
                                    v-model="form.lastName"
                                    type="text"
                                    required
                                    autocomplete="family-name"
                                />
                            </div>
                            <div>
                                <InputLabel for="email" value="Email" />
                                <TextInput
                                    id="email"
                                    v-model="form.email"
                                    type="email"
                                    required
                                    autocomplete="email"
                                />
                            </div>
                            <div>
                                <InputLabel for="phoneNumber" value="Phone Number" />
                                <div class="flex gap-2">
                                    <select
                                        id="phonePrefix"
                                        v-model="selectedPrefix"
                                        class="w-1/4 border-gray-300 rounded-md shadow-sm focus:border-indigo-500 focus:ring-indigo-500 bg-white"
                                        required
                                    >
                                        <option v-for="prefix in phonePrefixes" :key="prefix" :value="prefix">
                                            {{ prefix }}
                                        </option>
                                    </select>
                                    <TextInput
                                        id="phoneNumber"
                                        v-model="form.phoneNumber"
                                        type="tel"
                                        required
                                        autocomplete="tel"
                                        class="w-3/4"
                                    />
                                </div>
                            </div>
                            <div>
                                <InputLabel for="password" value="Password" />
                                <div class="relative">
                                    <TextInput
                                        id="password"
                                        v-model="password"
                                        :type="showPassword ? 'text' : 'password'"
                                        required
                                        autocomplete="new-password"
                                        placeholder="••••••••"
                                    />
                                    <button
                                        type="button"
                                        class="absolute inset-y-0 right-0 pr-3 flex items-center text-sm"
                                        tabindex="-1"
                                        @click="showPassword = !showPassword"
                                    >
                                        <span v-if="showPassword" class="text-gray-500">
                                            <EyeOff class="h-5 w-5" />
                                        </span>
                                        <span v-else class="text-gray-500">
                                            <Eye class="h-5 w-5" />
                                        </span>
                                    </button>
                                </div>
                            </div>
                            <div>
                                <InputLabel for="password_confirmation" value="Confirm Password" />
                                <div class="relative">
                                    <TextInput
                                        id="password_confirmation"
                                        v-model="password_confirmation"
                                        :type="showConfirmPassword ? 'text' : 'password'"
                                        required
                                        autocomplete="new-password"
                                        placeholder="••••••••"
                                    />
                                    <button
                                        type="button"
                                        class="absolute inset-y-0 right-0 pr-3 flex items-center text-sm"
                                        tabindex="-1"
                                        @click="showConfirmPassword = !showConfirmPassword"
                                    >
                                        <span v-if="showConfirmPassword" class="text-gray-500">
                                            <EyeOff class="h-5 w-5" />
                                        </span>
                                        <span v-else class="text-gray-500">
                                            <Eye class="h-5 w-5" />
                                        </span>
                                    </button>
                                </div>
                            </div>
                            <div v-if="passwordMatchError" class="text-red-600 text-sm mt-2">
                                {{ passwordMatchError }}
                            </div>
                        </section>

                        <!-- Organization Information -->
                        <section class="space-y-5">
                            <div class="pb-4 border-b border-gray-200">
                                <h2 class="text-sm font-semibold text-gray-900 uppercase tracking-wider">Organization</h2>
                            </div>
                            <div>
                                <InputLabel for="roleNames" value="Role(s)" />
                                <select
                                    id="roleNames"
                                    v-model="selectedRoles"
                                    class="w-full border-gray-300 rounded-md shadow-sm focus:border-indigo-500 focus:ring-indigo-500 bg-white"
                                    required
                                >
                                    <option value="" disabled>Select Role</option>
                                    <option v-for="role in staticRoles" :key="role" :value="role">
                                        {{ role }}
                                    </option>
                                </select>
                                <p class="mt-1.5 text-xs text-gray-500">Select a role</p>
                            </div>
                            <div>
                                <InputLabel for="headquartersAddress" value="Headquarters Address" />
                                <TextInput
                                    id="headquartersAddress"
                                    v-model="form.headquartersAddress"
                                    type="text"
                                    autocomplete="street-address"
                                />
                            </div>
                            <div>
                                <InputLabel for="publicAffairsEmail" value="Public Affairs Email" />
                                <TextInput
                                    id="publicAffairsEmail"
                                    v-model="form.publicAffairsEmail"
                                    type="email"
                                />
                            </div>
                        </section>

                        <!-- Address Information -->
                        <section class="space-y-5">
                            <div class="pb-4 border-b border-gray-200">
                                <h2 class="text-sm font-semibold text-gray-900 uppercase tracking-wider">Address</h2>
                            </div>
                            <div>
                                <InputLabel for="region" value="Region" />
                                <select
                                    id="region"
                                    v-model="psgcStore.selectedRegionCode"
                                    class="w-full border-gray-300 rounded-md shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
                                    required
                                    @change="psgcStore.selectRegion(psgcStore.selectedRegionCode)"
                                >
                                    <option value="" disabled>Select Region</option>
                                    <option v-for="region in psgcStore.getRegionsList" :key="region.code" :value="region.code">
                                        {{ region.name }}
                                    </option>
                                </select>
                            </div>
                            <div>
                                <InputLabel for="province" value="Province" />
                                <select
                                    id="province"
                                    v-model="psgcStore.selectedProvinceCode"
                                    class="w-full border-gray-300 rounded-md shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
                                    required
                                    @change="psgcStore.selectProvince(psgcStore.selectedProvinceCode)"
                                >
                                    <option value="" disabled>Select Province</option>
                                    <option v-for="province in psgcStore.getProvincesList" :key="province.code" :value="province.code">
                                        {{ province.name }}
                                    </option>
                                </select>
                            </div>
                            <div>
                                <InputLabel for="city" value="City/Municipality" />
                                <select
                                    id="city"
                                    v-model="psgcStore.selectedMunicipalityCode"
                                    class="w-full border-gray-300 rounded-md shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
                                    required
                                    @change="psgcStore.selectMunicipality(psgcStore.selectedMunicipalityCode)"
                                >
                                    <option value="" disabled>Select City/Municipality</option>
                                    <option v-for="city in psgcStore.getMunicipalitiesList" :key="city.code" :value="city.code">
                                        {{ city.name }}
                                    </option>
                                </select>
                            </div>
                            <div>
                                <InputLabel for="barangay" value="Barangay" />
                                <select
                                    id="barangay"
                                    v-model="psgcStore.selectedBarangayCode"
                                    class="w-full border-gray-300 rounded-md shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
                                    required
                                    @change="psgcStore.selectBarangay(psgcStore.selectedBarangayCode)"
                                >
                                    <option value="" disabled>Select Barangay</option>
                                    <option v-for="barangay in psgcStore.getBarangaysList" :key="barangay.code" :value="barangay.code">
                                        {{ barangay.name }}
                                    </option>
                                </select>
                            </div>
                            <div>
                                <InputLabel for="street" value="Street" />
                                <TextInput
                                    id="street"
                                    v-model="form.street"
                                    type="text"
                                    autocomplete="address-line1"
                                />
                            </div>
                            <div>
                                <InputLabel for="country" value="Country" />
                                <TextInput
                                    id="country"
                                    v-model="form.country"
                                    type="text"
                                    autocomplete="country-name"
                                />
                            </div>
                            <div>
                                <InputLabel for="postalCode" value="Postal Code" />
                                <TextInput
                                    id="postalCode"
                                    v-model="form.postalCode"
                                    type="text"
                                    autocomplete="postal-code"
                                />
                            </div>
                            <div>
                                <InputLabel for="address" value="Additional Notes" />
                                <TextInput
                                    id="address"
                                    v-model="form.address"
                                    type="text"
                                    placeholder="Optional"
                                />
                            </div>
                        </section>
                    </div>
                </div>
            </div>
        </div>

        <!-- Footer -->
        <div class="px-8 py-6 bg-white border-t border-gray-200">
            <div class="max-w-7xl mx-auto flex items-center justify-between gap-4">
                <router-link
                    to="/login"
                    class="text-sm text-gray-600 hover:text-gray-900 font-medium"
                >
                    Already registered? Sign in
                </router-link>
                <button
                    type="submit"
                    class="px-4 py-2 bg-blue-600 text-white rounded-md font-medium hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 transition disabled:opacity-50 disabled:cursor-not-allowed"
                    :disabled="processing"
                >
                    Create account
                </button>
            </div>
        </div>
    </form>
</GuestLayout>
</template>
