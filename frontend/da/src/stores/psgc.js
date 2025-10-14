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
    const selectedProvince = ref(null)
    const selectedMunicipality = ref(null)
    const selectedBarangay = ref(null)
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

    // Actions
    const initialize = async () => {
        loading.value = true
        error.value = null
        try {
            const response = await psgcAxios.get('/regions')
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
        loading.value = true
        error.value = null
        try {
            selectedRegion.value = regionsList.value.find(r => r.code === regionCode)
            const response = await psgcAxios.get(`/regions/${regionCode}/provinces`)
            provincesList.value = response.map(province => ({
                code: province.code,
                name: province.name
            }))

            // Reset lower-level selections
            selectedProvince.value = null
            selectedMunicipality.value = null
            selectedBarangay.value = null
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
        loading.value = true
        error.value = null
        try {
            selectedProvince.value = provincesList.value.find(p => p.code === provinceCode)
            const [citiesResponse, municipalitiesResponse] = await Promise.all([
                psgcAxios.get(`/provinces/${provinceCode}/cities`),
                psgcAxios.get(`/provinces/${provinceCode}/municipalities`)
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

            // Reset lower-level selections
            selectedMunicipality.value = null
            selectedBarangay.value = null
            barangaysList.value = []
        } catch (err) {
            console.error('Failed to load municipalities:', err)
            error.value = err.message || 'Failed to load municipalities. Please try again.'
        } finally {
            loading.value = false
        }
    }

    const selectMunicipality = async (municipalityCode) => {
        loading.value = true
        error.value = null
        try {
            selectedMunicipality.value = municipalitiesList.value.find(m => m.code === municipalityCode)
            const path = selectedMunicipality.value.type === 'city'
                ? `/cities/${municipalityCode}/barangays`
                : `/municipalities/${municipalityCode}/barangays`
            const response = await psgcAxios.get(path)
            barangaysList.value = response.map(barangay => ({
                code: barangay.code,
                name: barangay.name
            })).sort((a, b) => a.name.localeCompare(b.name))
            selectedBarangay.value = null
        } catch (err) {
            console.error('Failed to load barangays:', err)
            error.value = err.message || 'Failed to load barangays. Please try again.'
        } finally {
            loading.value = false
        }
    }

    const selectBarangay = async (barangayCode) => {
        try {
            selectedBarangay.value = barangaysList.value.find(b => b.code === barangayCode)
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
        selectedProvince.value = null
        selectedMunicipality.value = null
        selectedBarangay.value = null
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
        // State
        loading,
        error,

        // Getters
        getRegions,
        getProvinces,
        getMunicipalities,
        getBarangays,
        getSelectedLocation,
        getLocationString,

        // Actions
        initialize,
        selectRegion,
        selectProvince,
        selectMunicipality,
        selectBarangay,
        reset
    }
})