import { createStore } from 'vuex'

import axios from 'axios'
import router from '../router'

export default createStore({
  state: {
    loggedInUser: null,
    admins: [],
    users: [],
    events: [],
    garbageSpots: [],
  },
  mutations: {
    //LOGGED USER
    resetUser(state) {
      state.loggedInUser = null
    },
    setUser(state, loggedInUser) {
      state.loggedInUser = loggedInUser
    },
    //Users
    setUsers(state, users) {
      state.users = users
    },
    resetUsers(state) {
      state.users = null
    },
    updateUser(state, updateUser) {
      let idx = state.users.findIndex((t) => t.id === updateUser.id)
      if (idx >= 0) {
        state.users[idx] = updateUser
      }
    },
    deleteUser(state, deleteUser) {
      let idx = state.users.findIndex((t) => t.id === deleteUser.id)
      if (idx >= 0) {
        state.users.splice(idx, 1)
      }
    },
    //GarbageSpotss
    setGarbageSpotss(state, garbageSpots) {
      state.garbageSpots = garbageSpots
    },
    resetGarbageSpotss(state) {
      state.garbageSpots = null
    },
    insertGarbageSpots(state, newGarbageSpots) {
      state.garbageSpots.push(newGarbageSpots)
    },
    updateGarbageSpots(state, updateGarbageSpots) {
      let idx = state.garbageSpots.findIndex((t) => t.id === updateGarbageSpots.id)
      if (idx >= 0) {
        state.garbageSpots[idx] = updateGarbageSpots
      }
    },
    //Events
    setEvents(state, events) {
      state.events = events
    },
    resetEvents(state) {
      state.events = null
    },
    insertEvent(state, newEvent) {
      state.events.push(newEvent)
    },
    updateEvent(state, updateEvent) {
      let idx = state.events.findIndex((t) => t.id === updateEvent.id)
      if (idx >= 0) {
        state.events[idx] = updateEvent
      }
    },

  },
  getters: {
    loggedInUser: (state) => {
      return state.loggedInUser
    },
    users: (state) => {
      return state.users
    },
    totalUsers: (state) => {
      return state.users.length
    },
    garbageSpots: (state) => {
      return state.garbageSpots
    },
    events: (state) => {
      return state.events
    },

  },
  actions: {
    async login(context, credentials) {
      try {
        let response = await axios.post('loginBackOffice', credentials)
        axios.defaults.headers.common.Authorization = "Bearer " + response.data.access_token
        sessionStorage.setItem('token', response.data.access_token)
        
      } catch (error) {
        delete axios.defaults.headers.common.Authorization
        sessionStorage.removeItem('token')
        context.commit('resetUser', null)
        throw error
      }
      await context.dispatch('refresh')
    },
    async logout(context) {
      delete axios.defaults.headers.common.Authorization
      sessionStorage.removeItem('token')
      context.commit('resetUser', null)
      console.log("loggedout")
      router.push({ path: '/' })

    },
    async restoreToken(context) {
      let storedToken = sessionStorage.getItem('token')
      if (storedToken) {
        axios.defaults.headers.common.Authorization = "Bearer " + storedToken
        return storedToken
      }
      delete axios.defaults.headers.common.Authorization
      context.commit('resetUser', null)
      return null
    },
    async loadUsers(context) {
      try {
        let response = await axios.get('users')
        context.commit('setUsers', response.data.data)
        return response.data.data
      } catch (error) {
        context.commit('resetUsers', null)
        throw error
      }
    },
    async loadLoggedInUser(context) {
      try {
        let response = await axios.get('users/me')
        console.log(response.data.data + " get me")
        context.commit('setUser', response.data.data)
      } catch (error) {
        delete axios.defaults.headers.common.Authorization
        context.commit('resetUser', null)
        throw error
      }
    },
    async loadGarbageSpots(context) {
      try {
        let response = await axios.get('garbageSpots')
      
        context.commit('setGarbageSpotss', response.data.data)
        response.data.data.forEach(element => {
          console.log("garbage spots retrieved" + element.approved)
        });
        return response.data.data
      } catch (error) {
        context.commit('resetGarbageSpotss', null)
        throw error
      }
    },
    async loadEvents(context) {
      try {
        let response = await axios.get('events')
        context.commit('setEvents', response.data.data)
        return response.data.data
      } catch (error) {
        context.commit('resetEvents', null)
        throw error
      }
    },
    async aprovarGarbageSpot(context, garbageSpot) {
      let response = await axios.patch('garbageSpots/' + garbageSpot.id + '/approve', { 'approved': garbageSpot.approved })
      context.commit('updateGarbageSpots', garbageSpot)
      return response.data
    },
    async updateStatusGarbageSpots(context, garbageSpot) {
      let response = await axios.patch('garbageSpots/' + garbageSpot.id + '/updateGarbageSpotStatus', { 'status': garbageSpot.estado })
      context.commit('updateGarbageSpots', garbageSpot)
      return response.data
    },
    async deleteUser(context, user) {
      let response = await axios.delete("users/" + user.id)
      context.commit('deleteUser', user)
      console.log(response.data.data)
      return response.data.data
    },
    async blockUser(context, user) {

      let response = await axios.patch("users/" + user.id + '/block', { "blocked": true })
      user.blocked = true
      context.commit('updateUser', user)
      return response.data.data
    },
    async unblockUser(context, user) {
      let response = await axios.patch("users/" + user.id + '/block', { "blocked": false })
      user.blocked = false
      context.commit('updateUser', user)
      return response.data.data
    },

    async refresh(context) {
      let userPromise = context.dispatch('loadLoggedInUser')
      let usersPromise = context.dispatch('loadUsers')
      let garbageSpotsPromise = context.dispatch('loadGarbageSpots')
      let eventsPromise = context.dispatch('loadEvents')
      await userPromise
      await usersPromise
      await garbageSpotsPromise
      await eventsPromise

    },
  },
  modules: {
  }
})
