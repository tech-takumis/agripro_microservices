import axios from '@/lib/axios'
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useProgramStore = defineStore('program', () => {
    // State
    const programs = ref([])
    const loading = ref(false)
    const error = ref(null)

    // Getters as computed
    const allPrograms = computed(() => programs.value)
    const workshopPrograms = computed(() =>
        programs.value.filter(program => program.type === 'WORKSHOP')
    )
    const trainingPrograms = computed(() =>
        programs.value.filter(program => program.type === 'TRAINING')
    )
    const activePrograms = computed(() =>
        programs.value.filter(program => program.status === 'ACTIVE')
    )
    const completedPrograms = computed(() =>
        programs.value.filter(program => program.status === 'COMPLETED')
    )
    const inactivePrograms = computed(() =>
        programs.value.filter(program => program.status === 'INACTIVE')
    )
    const highCompletionPrograms = computed(() =>
        programs.value.filter(program => program.completion >= 80)
    )
    const mediumCompletionPrograms = computed(() =>
        programs.value.filter(program => program.completion >= 50 && program.completion < 80)
    )
    const lowCompletionPrograms = computed(() =>
        programs.value.filter(program => program.completion < 50)
    )
    const recentPrograms = computed(() =>
        programs.value
            .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
            .slice(0, 10)
    )
    const programsStats = computed(() => {
        const total = programs.value.length
        const active = programs.value.filter(program => program.status === 'ACTIVE').length
        const completed = programs.value.filter(program => program.status === 'COMPLETED').length
        const workshops = programs.value.filter(program => program.type === 'WORKSHOP').length
        const trainings = programs.value.filter(program => program.type === 'TRAINING').length
        const averageCompletion = total > 0
            ? programs.value.reduce((sum, program) => sum + program.completion, 0) / total
            : 0
        return { total, active, completed, workshops, trainings, averageCompletion }
    })

    // Methods that were getters with parameters
    const programsByType = (type) =>
        programs.value.filter(program => program.type === type)

    const programsByStatus = (status) =>
        programs.value.filter(program => program.status === status)

    // Actions as functions
    async function fetchPrograms() {
        loading.value = true
        error.value = null

        try {
            const response = await axios.get('/api/v1/programs')
            programs.value = response.data
        } catch (err) {
            error.value = err.response?.data?.message || err.message || 'Failed to fetch programs'
            console.error('Error fetching programs:', err)
        } finally {
            loading.value = false
        }
    }

    async function createProgram(programData) {
        loading.value = true
        error.value = null

        try {
            const response = await axios.post('/api/v1/programs', programData)
            programs.value.push(response.data)
            return response.data
        } catch (err) {
            error.value = err.response?.data?.message || err.message || 'Failed to create program'
            console.error('Error creating program:', err)
            throw err
        } finally {
            loading.value = false
        }
    }

    async function updateProgram(id, programData) {
        loading.value = true
        error.value = null

        try {
            const response = await axios.put(`/api/v1/programs/${id}`, programData)
            const index = programs.value.findIndex(program => program.id === id)
            if (index !== -1) {
                programs.value[index] = response.data
            }
            return response.data
        } catch (err) {
            error.value = err.response?.data?.message || err.message || 'Failed to update program'
            console.error('Error updating program:', err)
            throw err
        } finally {
            loading.value = false
        }
    }

    async function deleteProgram(id) {
        loading.value = true
        error.value = null

        try {
            await axios.delete(`/api/v1/programs/${id}`)
            programs.value = programs.value.filter(program => program.id !== id)
            return true
        } catch (err) {
            error.value = err.response?.data?.message || err.message || 'Failed to delete program'
            console.error('Error deleting program:', err)
            throw err
        } finally {
            loading.value = false
        }
    }

    return {
        // State
        programs,
        loading,
        error,

        // Getters
        allPrograms,
        workshopPrograms,
        trainingPrograms,
        activePrograms,
        completedPrograms,
        inactivePrograms,
        highCompletionPrograms,
        mediumCompletionPrograms,
        lowCompletionPrograms,
        recentPrograms,
        programsStats,

        // Methods
        programsByType,
        programsByStatus,

        // Actions
        fetchPrograms,
        createProgram,
        updateProgram,
        deleteProgram
    }
})
