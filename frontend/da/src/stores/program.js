import axios from '@/lib/axios'
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useProgramStore = defineStore('program', {
    state: () => ({
        programs: [],
        loading: false,
        error: null
    }),
  getters: {
    allPrograms: (state) => state.programs,
    programsByType: (state) => (type) => state.programs.filter(program => program.type === type),
    programsByStatus: (state) => (status) => state.programs.filter(program => program.status === status),
    workshopPrograms: (state) => state.programs.filter(program => program.type === 'WORKSHOP'),
    trainingPrograms: (state) => state.programs.filter(program => program.type === 'TRAINING'),
    activePrograms: (state) => state.programs.filter(program => program.status === 'ACTIVE'),
    completedPrograms: (state) => state.programs.filter(program => program.status === 'COMPLETED'),
    inactivePrograms: (state) => state.programs.filter(program => program.status === 'INACTIVE'),
    highCompletionPrograms: (state) => state.programs.filter(program => program.completion >= 80),
    mediumCompletionPrograms: (state) => state.programs.filter(program => program.completion >= 50 && program.completion < 80),
    lowCompletionPrograms: (state) => state.programs.filter(program => program.completion < 50),
    recentPrograms: (state) => state.programs.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt)).slice(0, 10),
    programsStats: (state) => {
        const total = state.programs.length
        const active = state.programs.filter(program => program.status === 'ACTIVE').length
        const completed = state.programs.filter(program => program.status === 'COMPLETED').length
        const workshops = state.programs.filter(program => program.type === 'WORKSHOP').length
        const trainings = state.programs.filter(program => program.type === 'TRAINING').length
        const averageCompletion = total > 0 ? state.programs.reduce((sum, program) => sum + program.completion, 0) / total : 0
        return { total, active, completed, workshops, trainings, averageCompletion }
      }
    },
    actions: {
        async fetchPrograms() {
            this.loading = true
            this.error = null

            try {
                const response = await axios.get('/api/v1/programs')
                this.programs = response.data
            } catch (error) {
                this.error = error.response?.data?.message || error.message || 'Failed to fetch programs'
                console.error('Error fetching programs:', error)
            } finally {
                this.loading = false
            }
        },

        async createProgram(programData) {
            this.loading = true
            this.error = null

            try {
                const response = await axios.post('/api/v1/programs', programData)
                this.programs.push(response.data)
                return response.data
            } catch (error) {
                this.error = error.response?.data?.message || error.message || 'Failed to create program'
                console.error('Error creating program:', error)
                throw error
            } finally {
                this.loading = false
            }
        },

        async updateProgram(id, programData) {
            this.loading = true
            this.error = null

            try {
                const response = await axios.put(`/api/v1/programs/${id}`, programData)
                const index = this.programs.findIndex(program => program.id === id)
                if (index !== -1) {
                    this.programs[index] = response.data
                }
                return response.data
            } catch (error) {
                this.error = error.response?.data?.message || error.message || 'Failed to update program'
                console.error('Error updating program:', error)
                throw error
            } finally {
                this.loading = false
            }
        },

        async deleteProgram(id) {
            this.loading = true
            this.error = null

            try {
                await axios.delete(`/api/v1/programs/${id}`)
                const index = this.programs.findIndex(program => program.id === id)
                if (index !== -1) {
                    this.programs.splice(index, 1)
                }
                return true
            } catch (error) {
                this.error = error.response?.data?.message || error.message || 'Failed to delete program'
                console.error('Error deleting program:', error)
                throw error
            } finally {
                this.loading = false
            }
        },

        async updateProgramCompletion(id, completion) {
            return this.updateProgram(id, { completion })
        },

        async updateProgramStatus(id, status) {
            return this.updateProgram(id, { status })
        },

        getProgramById(id) {
            return this.programs.find(program => program.id === id)
        },

        clearError() {
            this.error = null
        },

        $reset() {
            this.programs = []
            this.loading = false
            this.error = null
        }
    }
  }
)