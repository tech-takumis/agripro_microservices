import { ref, computed } from 'vue'

export function useApplicationFilters(applications) {
    // Filter state
    const filters = ref({
        status: '',
        cropType: '',
        minCoverage: null,
        maxCoverage: null,
        startDate: '',
        endDate: '',
        searchName: ''
    })

    // Computed filtered applications
    const filteredApplications = computed(() => {
        return applications.value.filter(app => {
            // Status filter
            if (filters.value.status && app.status !== filters.value.status) {
                return false
            }

            // Crop type filter
            if (filters.value.cropType && app.dynamicFields.crop_type !== filters.value.cropType) {
                return false
            }

            // Coverage amount range
            if (filters.value.minCoverage && app.dynamicFields.amount_of_cover < filters.value.minCoverage) {
                return false
            }
            if (filters.value.maxCoverage && app.dynamicFields.amount_of_cover > filters.value.maxCoverage) {
                return false
            }

            // Date range
            if (filters.value.startDate) {
                const appDate = new Date(app.submittedAt)
                const startDate = new Date(filters.value.startDate)
                if (appDate < startDate) return false
            }
            if (filters.value.endDate) {
                const appDate = new Date(app.submittedAt)
                const endDate = new Date(filters.value.endDate)
                if (appDate > endDate) return false
            }

            // Name search
            if (filters.value.searchName) {
                const fullName = `${app.dynamicFields.first_name} ${app.dynamicFields.middle_name} ${app.dynamicFields.last_name}`.toLowerCase()
                if (!fullName.includes(filters.value.searchName.toLowerCase())) {
                    return false
                }
            }

            return true
        })
    })

    // Available filter options
    const filterOptions = computed(() => {
        const statuses = [...new Set(applications.value.map(app => app.status))]
        const cropTypes = [...new Set(applications.value.map(app => app.dynamicFields.crop_type))]

        return {
            statuses,
            cropTypes
        }
    })

    return {
        filters,
        filteredApplications,
        filterOptions
    }
}