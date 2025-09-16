import { createApp, markRaw } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import './index.css'

const app = createApp(App)
const pinia = createPinia()

// Add the router plugin to Pinia
pinia.use(({ store }) => {
    store.router = markRaw(router)
})

// Register Pinia with the app
app.use(pinia)
app.use(router)
app.mount('#app')