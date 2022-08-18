import { createRouter, createWebHashHistory } from 'vue-router'

import Dashboard from "../components/Dashboard.vue"
import Home from "../components/Home.vue"
import Login from "../components/auth/Login.vue"
import Admin from "../components/users/Admin.vue"
import ChangePassword from "../components/auth/ChangePassword.vue"
import User from "../components/users/User.vue"
import Users from "../components/users/Users.vue"
import GarbageSpots from "../components/garbageSpots/GarbageSpots.vue"
import GarbageSpot from "../components/garbageSpots/GarbageSpot.vue"
import Events from "../components/events/Events.vue"
import Event from "../components/events/Event.vue"

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: Dashboard
  },
  {
    path: '/admin',
    name: 'Admin',
    component: Admin
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
    path: '/garbageSpots',
    name: 'GarbageSpots',
    component: GarbageSpots
  },
  {
    path: '/garbageSpots/:id',
    name: 'GarbageSpot',
    component: GarbageSpot,
    // props: true,
    // Replaced with the following line to ensure that id is a number
    props: route => ({ id: parseInt(route.params.id)})
  },
  {
    path: '/events',
    name: 'Events',
    component: Events
  },
  {
    path: '/events/:id',
    name: 'Event',
    component: Event,
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


import store from '../store'

router.beforeEach((to, from, next) => {

  //
  if (store.state.loggedInUser && (to.name === 'Home') ) {
    next({ name: 'Dashboard' })
    return
  }
  if ((to.name === 'Login') || (to.name === 'Home') ) {
    next()
    return
  }

  if (!store.getters.loggedInUser) {
    next({ name: 'Login' })
    return
  }

  next()
})


export default router
