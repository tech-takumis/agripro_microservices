import { createApp, markRaw } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import './index.css'
<<<<<<< HEAD:frontend/department_of_agriculture/src/main.js
import { createPersistedStatePlugin } from 'pinia-plugin-persistedstate-2'
=======
>>>>>>> origin/master:frontend/da/src/main.js

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