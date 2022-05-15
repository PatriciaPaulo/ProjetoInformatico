import { createRouter, createWebHashHistory } from 'vue-router'

import Dashboard from "../components/Dashboard.vue"
import Login from "../components/auth/Login.vue"
import Register from "../components/auth/Register.vue"
import ChangePassword from "../components/auth/ChangePassword.vue"
import User from "../components/users/User.vue"
import Users from "../components/users/Users.vue"
import Lixeiras from "../components/lixeiras/Lixeiras.vue"
import Lixeira from "../components/lixeiras/Lixeira.vue"
import Eventos from "../components/eventos/Eventos.vue"
import Evento from "../components/eventos/Evento.vue"

const routes = [
  {
    path: '/',
    name: 'Dashboard',
    component: Dashboard
  },
  {
    path: '/register',
    name: 'Register',
    component: Register
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/password',
    name: 'ChangePassword',
    component: ChangePassword
  },
  {
    path: '/users/',
    name: 'Users',
    component: Users
  },
  {
    path: '/users/:id',
    name: 'User',
    component: User,
    // props: true,
    // Replaced with the following line to ensure that id is a number
    props: route => ({ id: parseInt(route.params.id) })
  },
  {
    path: '/lixeiras',
    name: 'Lixeiras',
    component: Lixeiras
  },
  {
    path: '/lixeiras/:id',
    name: 'Lixeira',
    component: Lixeira,
    // props: true,
    // Replaced with the following line to ensure that id is a number
    props: route => ({ id: parseInt(route.params.id)})
  },
  {
    path: '/eventos',
    name: 'Eventos',
    component: Eventos
  },
  {
    path: '/eventos/:id',
    name: 'Evento',
    component: Evento,
    // props: true,
    // Replaced with the following line to ensure that id is a number
    props: route => ({ id: parseInt(route.params.id)})
  },
  {
    path: '/about',
    name: 'About',
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import(/* webpackChunkName: "about" */ '../views/About.vue')
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

export default router
