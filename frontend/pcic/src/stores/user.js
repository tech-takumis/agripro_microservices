import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    user: null,
    // Add more user-related state as needed
  }),
  actions: {
    setUser(user) {
      this.user = user
    },
    clearUser() {
      this.user = null
    },
    // Add more user-related actions as needed
  },
  getters: {
    isAuthenticated: (state) => !!state.user,
    // Add more getters as needed
  },
})

