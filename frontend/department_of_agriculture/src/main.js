import { createApp, markRaw } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import './index.css'
import { createPersistedStatePlugin } from 'pinia-plugin-persistedstate'

const app = createApp(App)
const pinia = createPinia()

app.use(
    pinia.use(createPersistedStatePlugin()),
    pinia.use(({ store }) => {
        store.router = markRaw(router)
    }),
)
app.use(router)
app.mount('#app')
