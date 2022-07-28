import "bootstrap/dist/css/bootstrap.min.css"
import 'bootstrap-icons/font/bootstrap-icons.css'
import "bootstrap"

import "primevue/resources/themes/saga-blue/theme.css"       //theme
import "primevue/resources/primevue.min.css"                 //core css
import "primeicons/primeicons.css"                           //icons
import 'primeflex/primeflex.css';

import Toaster from "@meforma/vue-toaster"
import axios from 'axios'
import VueGoogleMaps from '@fawmi/vue-google-maps'
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import PrimeVue from 'primevue/config';
import ConfirmationService from 'primevue/confirmationservice';



let toastOptions = {
    position: 'top',
    timeout: 3000,
    pauseOnHover: true
}

const app = createApp(App)
            .use(store).use(router)
            .use(Toaster, toastOptions)
            .use(PrimeVue)
            .use(ConfirmationService)
            
app.use(VueGoogleMaps, {
    load: {
        key: 'AIzaSyDaoUX0GkumMWqpcwKq3cQ-XqT51wR51kM',
    },
})
const flaskURL = ""

axios.defaults.baseURL = flaskURL + '/api'

app.config.globalProperties.$axios = axios
app.config.globalProperties.$userId = 1
app.config.globalProperties.$serverUrl = flaskURL
app.mount('#app')