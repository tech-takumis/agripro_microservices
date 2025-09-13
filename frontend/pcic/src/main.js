import { createApp, markRaw } from 'vue'
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
import App from './App.vue'
import router from './router'
import { useUserStore } from './stores/user'
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

// Initialize user store with roles and permissions
const userStore = useUserStore()
userStore.initialize().then(() => {
    // After initialization, check authentication status
    return userStore.checkAuthStatus()
}).catch(error => {
    console.error('Failed to initialize user store:', error)
})

app.mount('#app')
