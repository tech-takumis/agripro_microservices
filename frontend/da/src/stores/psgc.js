import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import psgcAxios from '@/lib/psgcAxios'

export const usePsgcStore = defineStore('psgc', () => {
    // State
    const regionsList = ref([])
    const provincesList = ref([])
    const municipalitiesList = ref([])
    const barangaysList = ref([])
    const selectedRegion = ref(null)
    const selectedRegionCode = ref('')
    const selectedProvince = ref(null)
    const selectedProvinceCode = ref('')
    const selectedMunicipality = ref(null)
    const selectedMunicipalityCode = ref('')
    const selectedBarangay = ref(null)
    const selectedBarangayCode = ref('')
    const loading = ref(false)
    const error = ref(null)

    // Getters
    const getRegions = computed(() => regionsList.value)
    const getProvinces = computed(() => provincesList.value)
    const getMunicipalities = computed(() => municipalitiesList.value)
    const getBarangays = computed(() => barangaysList.value)

    const getSelectedLocation = computed(() => ({
        region: selectedRegion.value,
        province: selectedProvince.value,
        municipality: selectedMunicipality.value,
        barangay: selectedBarangay.value
    }))

    // Getters for all state
    const getSelectedRegion = computed(() => selectedRegion.value)
    const getSelectedRegionCode = computed(() => selectedRegionCode.value)
    const getSelectedProvince = computed(() => selectedProvince.value)
    const getSelectedProvinceCode = computed(() => selectedProvinceCode.value)
    const getSelectedMunicipality = computed(() => selectedMunicipality.value)
    const getSelectedMunicipalityCode = computed(() => selectedMunicipalityCode.value)
    const getSelectedBarangay = computed(() => selectedBarangay.value)
    const getSelectedBarangayCode = computed(() => selectedBarangayCode.value)
    const getRegionsList = computed(() => regionsList.value)
    const getProvincesList = computed(() => provincesList.value)
    const getMunicipalitiesList = computed(() => municipalitiesList.value)
    const getBarangaysList = computed(() => barangaysList.value)

    // Actions
    const initialize = async () => {
        loading.value = true
        error.value = null
        try {
            const response = await psgcAxios.get('/regions/') // trailing slash
            regionsList.value = response.map(region => ({
                code: region.code,
                name: region.regionName
            }))
        } catch (err) {
            console.error('Failed to initialize PSGC:', err)
            error.value = err.message || 'Failed to load regions. Please try again.'
        } finally {
            loading.value = false
        }
    }

    const selectRegion = async (regionCode) => {
        console.log('[PSGC] selectRegion called with:', regionCode, regionsList.value);
        loading.value = true
        error.value = null
        try {
            selectedRegionCode.value = regionCode
            selectedRegion.value = regionsList.value.find(r => r.code === regionCode)
            console.log('[PSGC] selectedRegion:', selectedRegion.value)
            const response = await psgcAxios.get(`/regions/${regionCode}/provinces/`)
            provincesList.value = response.map(province => ({
                code: province.code,
                name: province.name
            }))
            selectedProvince.value = null
            selectedProvinceCode.value = ''
            selectedMunicipality.value = null
            selectedMunicipalityCode.value = ''
            selectedBarangay.value = null
            selectedBarangayCode.value = ''
            municipalitiesList.value = []
            barangaysList.value = []
        } catch (err) {
            console.error('Failed to load provinces:', err)
            error.value = err.message || 'Failed to load provinces. Please try again.'
        } finally {
            loading.value = false
        }
    }

    const selectProvince = async (provinceCode) => {
        console.log('[PSGC] selectProvince called with:', provinceCode, provincesList.value);
        loading.value = true
        error.value = null
        try {
            selectedProvinceCode.value = provinceCode
            selectedProvince.value = provincesList.value.find(p => p.code === provinceCode)
            console.log('[PSGC] selectedProvince:', selectedProvince.value)
            const [citiesResponse, municipalitiesResponse] = await Promise.all([
                psgcAxios.get(`/provinces/${provinceCode}/cities/`),
                psgcAxios.get(`/provinces/${provinceCode}/municipalities/`)
            ])
            municipalitiesList.value = [
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
            selectedMunicipality.value = null
            selectedMunicipalityCode.value = ''
            selectedBarangay.value = null
            selectedBarangayCode.value = ''
            barangaysList.value = []
        } catch (err) {
            console.error('Failed to load municipalities:', err)
            error.value = err.message || 'Failed to load municipalities. Please try again.'
        } finally {
            loading.value = false
        }
    }

    const selectMunicipality = async (municipalityCode) => {
        console.log('[PSGC] selectMunicipality called with:', municipalityCode, municipalitiesList.value);
        loading.value = true
        error.value = null
        try {
            selectedMunicipalityCode.value = municipalityCode
            selectedMunicipality.value = municipalitiesList.value.find(m => m.code === municipalityCode)
            console.log('[PSGC] selectedMunicipality:', selectedMunicipality.value)
            const path = selectedMunicipality.value.type === 'city'
                ? `/cities/${municipalityCode}/barangays/`
                : `/municipalities/${municipalityCode}/barangays/`
            const response = await psgcAxios.get(path)
            barangaysList.value = response.map(barangay => ({
                code: barangay.code,
                name: barangay.name
            })).sort((a, b) => a.name.localeCompare(b.name))
            selectedBarangay.value = null
            selectedBarangayCode.value = ''
        } catch (err) {
            console.error('Failed to load barangays:', err)
            error.value = err.message || 'Failed to load barangays. Please try again.'
        } finally {
            loading.value = false
        }
    }

    const selectBarangay = async (barangayCode) => {
        console.log('[PSGC] selectBarangay called with:', barangayCode, barangaysList.value);
        try {
            selectedBarangayCode.value = barangayCode
            selectedBarangay.value = barangaysList.value.find(b => b.code === barangayCode)
            console.log('[PSGC] selectedBarangay:', selectedBarangay.value)
        } catch (err) {
            console.error('Failed to select barangay:', err)
            error.value = 'Failed to select barangay'
        }
    }

    const reset = () => {
        regionsList.value = []
        provincesList.value = []
        municipalitiesList.value = []
        barangaysList.value = []
        selectedRegion.value = null
        selectedRegionCode.value = ''
        selectedProvince.value = null
        selectedProvinceCode.value = ''
        selectedMunicipality.value = null
        selectedMunicipalityCode.value = ''
        selectedBarangay.value = null
        selectedBarangayCode.value = ''
        error.value = null
    }

    const getLocationString = computed(() => {
        const parts = []
        if (selectedBarangay.value) parts.push(selectedBarangay.value.name)
        if (selectedMunicipality.value) parts.push(selectedMunicipality.value.name)
        if (selectedProvince.value) parts.push(selectedProvince.value.name)
        if (selectedRegion.value) parts.push(selectedRegion.value.name)
        return parts.join(', ')
    })

    return {
        // Writable refs for v-model
        selectedRegionCode,
        selectedProvinceCode,
        selectedMunicipalityCode,
        selectedBarangayCode,

        // Getters only
        getRegions,
        getProvinces,
        getMunicipalities,
        getBarangays,
        getSelectedLocation,
        getLocationString,
        getSelectedRegion,
        getSelectedRegionCode,
        getSelectedProvince,
        getSelectedProvinceCode,
        getSelectedMunicipality,
        getSelectedMunicipalityCode,
        getSelectedBarangay,
        getSelectedBarangayCode,
        getRegionsList,
        getProvincesList,
        getMunicipalitiesList,
        getBarangaysList,

        // Actions
        initialize,
        selectRegion,
        selectProvince,
        selectMunicipality,
        selectBarangay,
        reset
    }
})