import { createApp, markRaw } from 'vue'
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
import App from './App.vue'
import router from './router'
import { useAuthStore } from './stores/auth'
import './index.css'

const app = createApp(App)
const pinia = createPinia()

// Add persistence plugin
pinia.use(piniaPluginPersistedstate)

app.use(
    pinia.use(({ store }) => {
        store.router = markRaw(router)
    }),
)
app.use(router)

// Initialize auth store
const authStore = useAuthStore()
// authStore.getAuthenticated().catch(error => {
//     console.error('Failed to fetch authenticated user:', error)
// })

app.mount('#app')
