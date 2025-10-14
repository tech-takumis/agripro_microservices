<template>
  <div class="space-y-4">
    <!-- Region Selection -->
    <div>
      <label for="psgc-region" class="block text-sm font-medium text-gray-700 mb-1">
        Region <span v-if="required" class="text-red-500">*</span>
      </label>
      <select
        id="psgc-region"
        :value="localState.region"
        :disabled="localLoading"
        :required="required"
        class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-purple-500 focus:border-purple-500"
        @change="handleRegionChange"
      >
        <option value="">Select Region</option>
        <option
          v-for="region in localData.regions"
          :key="region.code"
          :value="region.code"
        >
          {{ region.name }}
        </option>
      </select>
    </div>

    <!-- Province Selection -->
    <div v-if="localState.region">
      <label for="province" class="block text-sm font-medium text-gray-700 mb-1">
        Province <span v-if="required" class="text-red-500">*</span>
      </label>
      <select
        id="province"
        :value="localState.province"
        :disabled="localLoading"
        :required="required"
        class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-purple-500 focus:border-purple-500"
        @change="handleProvinceChange"
      >
        <option value="">Select Province</option>
        <option
          v-for="province in localData.provinces"
          :key="province.code"
          :value="province.code"
        >
          {{ province.name }}
        </option>
      </select>
    </div>

    <!-- Municipality/City Selection -->
    <div v-if="localState.province">
      <label for="municipality" class="block text-sm font-medium text-gray-700 mb-1">
        City/Municipality <span v-if="required" class="text-red-500">*</span>
      </label>
      <select
        id="municipality"
        :value="localState.municipality"
        :disabled="localLoading"
        :required="required"
        class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-purple-500 focus:border-purple-500"
        @change="handleMunicipalityChange"
      >
        <option value="">Select City/Municipality</option>
        <option
          v-for="municipality in localData.municipalities"
          :key="municipality.code"
          :value="municipality.code"
        >
          {{ municipality.name }}
        </option>
      </select>
    </div>

    <!-- Barangay Selection -->
    <div v-if="localState.municipality && includeBarangay">
      <label for="barangay" class="block text-sm font-medium text-gray-700 mb-1">
        Barangay <span v-if="required" class="text-red-500">*</span>
      </label>
      <select
        id="barangay"
        :value="localState.barangay"
        :disabled="localLoading"
        :required="required"
        class="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-purple-500 focus:border-purple-500"
        @change="handleBarangayChange"
      >
        <option value="">Select Barangay</option>
        <option
          v-for="barangay in localData.barangays"
          :key="barangay.code"
          :value="barangay.code"
        >
          {{ barangay.name }}
        </option>
      </select>
    </div>

    <!-- Loading State -->
    <div v-if="localLoading" class="flex items-center justify-center text-sm text-gray-500">
      <Loader2 class="animate-spin h-4 w-4 mr-2" />
      Loading...
    </div>

    <!-- Error Message -->
    <div v-if="psgcStore.error" class="flex items-center text-sm text-red-600">
      <AlertCircle class="h-4 w-4 mr-2" />
      {{ psgcStore.error }}
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { Loader2, AlertCircle } from 'lucide-vue-next'
import { usePsgcStore } from '@/stores/psgc'
import psgcAxios from '@/lib/psgcAxios'

const props = defineProps({
  modelValue: {
    type: Object,
    default: () => ({})
  },
  required: {
    type: Boolean,
    default: false
  },
  includeBarangay: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['update:modelValue'])

const psgcStore = usePsgcStore()

// Local state for this component instance only
const localState = ref({
  region: '',
  province: '',
  municipality: '',
  barangay: ''
})

// Local data arrays for this component instance
const localData = ref({
  regions: [],
  provinces: [],
  municipalities: [],
  barangays: []
})

// Loading state for this component instance
const localLoading = ref(false)

// Watch for external modelValue changes and sync local state
watch(() => props.modelValue, (newValue) => {
  if (newValue && Object.keys(newValue).length > 0) {
    const newLocalState = {
      region: newValue.region || '',
      province: newValue.province || '',
      municipality: newValue.municipality || '',
      barangay: newValue.barangay || ''
    }

    // Only update if there's actually a change to prevent infinite loops
    if (JSON.stringify(localState.value) !== JSON.stringify(newLocalState)) {
      localState.value = newLocalState
    }
  }
}, { immediate: true, deep: true })

// Watch local state changes and emit updates (with debouncing to prevent excessive updates)
let updateTimeout = null
watch(localState, (newState) => {
  // Clear previous timeout
  if (updateTimeout) {
    clearTimeout(updateTimeout)
  }

  // Debounce the update to prevent rapid successive updates
  updateTimeout = setTimeout(() => {
    const locationData = {
      region: newState.region,
      province: newState.province,
      municipality: newState.municipality,
      barangay: newState.barangay,
      locationString: getLocationString()
    }
    emit('update:modelValue', locationData)
  }, 10) // Small delay to batch rapid changes
}, { deep: true })

// Event handlers for selection changes
const handleRegionChange = async (event) => {
  const regionCode = event.target.value
  localState.value.region = regionCode

  if (regionCode) {
    await loadProvinces(regionCode)
    // Reset dependent fields
    localState.value.province = ''
    localState.value.municipality = ''
    localState.value.barangay = ''
  }
}

const handleProvinceChange = async (event) => {
  const provinceCode = event.target.value
  localState.value.province = provinceCode

  if (provinceCode) {
    await loadMunicipalities(provinceCode)
    // Reset dependent fields
    localState.value.municipality = ''
    localState.value.barangay = ''
  }
}

const handleMunicipalityChange = async (event) => {
  const municipalityCode = event.target.value
  localState.value.municipality = municipalityCode

  if (municipalityCode) {
    await loadBarangays(municipalityCode)
    // Reset dependent fields
    localState.value.barangay = ''
  }
}

const handleBarangayChange = (event) => {
  const barangayCode = event.target.value
  localState.value.barangay = barangayCode
  // No additional loading needed for barangay selection
}

// Helper functions to load location data independently for this component instance
const loadProvinces = async (regionCode) => {
  localLoading.value = true
  try {
    const response = await psgcAxios.get(`/regions/${regionCode}/provinces`)
    localData.value.provinces = response.map(province => ({
      code: province.code,
      name: province.name
    }))
  } catch (error) {
    console.error('Failed to load provinces:', error)
    localData.value.provinces = []
  } finally {
    localLoading.value = false
  }
}

const loadMunicipalities = async (provinceCode) => {
  localLoading.value = true
  try {
    const [citiesResponse, municipalitiesResponse] = await Promise.all([
      psgcAxios.get(`/provinces/${provinceCode}/cities`),
      psgcAxios.get(`/provinces/${provinceCode}/municipalities`)
    ])

    localData.value.municipalities = [
      ...citiesResponse.map(city => ({
        code: city.code,
        name: city.name,
        type: 'city'
      })),
      ...municipalitiesResponse.map(municipality => ({
        code: municipality.code,
        name: municipality.name,
        type: 'municipality'
      }))
    ].sort((a, b) => a.name.localeCompare(b.name))
  } catch (error) {
    console.error('Failed to load municipalities:', error)
    localData.value.municipalities = []
  } finally {
    localLoading.value = false
  }
}

const loadBarangays = async (municipalityCode) => {
  localLoading.value = true
  try {
    // Determine if it's a city or municipality to use correct API endpoint
    const municipality = localData.value.municipalities.find(m => m.code === municipalityCode)
    const endpoint = municipality?.type === 'city' ? 'cities' : 'municipalities'

    const response = await psgcAxios.get(`/${endpoint}/${municipalityCode}/barangays`)
    localData.value.barangays = response.map(barangay => ({
      code: barangay.code,
      name: barangay.name
    })).sort((a, b) => a.name.localeCompare(b.name))
  } catch (error) {
    console.error('Failed to load barangays:', error)
    localData.value.barangays = []
  } finally {
    localLoading.value = false
  }
}

// Helper function to generate location string using local data
const getLocationString = () => {
  const parts = []

  if (localState.value.barangay) {
    const barangay = localData.value.barangays.find(b => b.code === localState.value.barangay)
    if (barangay) parts.push(barangay.name)
  }

  if (localState.value.municipality) {
    const municipality = localData.value.municipalities.find(m => m.code === localState.value.municipality)
    if (municipality) parts.push(municipality.name)
  }

  if (localState.value.province) {
    const province = localData.value.provinces.find(p => p.code === localState.value.province)
    if (province) parts.push(province.name)
  }

  if (localState.value.region) {
    const region = localData.value.regions.find(r => r.code === localState.value.region)
    if (region) parts.push(region.name)
  }

  return parts.join(', ')
}

// Initialize local regions data
onMounted(async () => {
  localLoading.value = true
  try {
    const response = await psgcAxios.get('/regions')
    localData.value.regions = response.map(region => ({
      code: region.code,
      name: region.regionName
    }))
  } catch (error) {
    console.error('Failed to load regions:', error)
    localData.value.regions = []
  } finally {
    localLoading.value = false
  }
})
</script>
