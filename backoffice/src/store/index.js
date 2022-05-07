import { createStore } from 'vuex'

import axios from 'axios'
import router from '../router'

export default createStore({
  state: {
    loggedInUser: null,
    admins: [],
    users: [],
    eventos: [],
    lixeiras: [],
    atividades: []
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
    //ADMINS
    setAdmins(state, users) {
      state.admins = users
    },
    resetAdmins(state) {
      state.admins = null
    },
    insertAdmin(state, newAdmin) {
      state.admins.push(newAdmin)
    },
    updateAdmin(state, updateAdmin) {
      let idx = state.admins.findIndex((t) => t.id === updateAdmin.id)
      if (idx >= 0) {
        state.admins[idx] = updateAdmin
      }
    },
    deleteAdmin(state, deleteAdmin) {
      let idx = state.admins.findIndex((t) => t.id === deleteAdmin.id)
      if (idx >= 0) {
        state.admins.splice(idx, 1)
      }
    },
    //Lixeiras
    setLixeiras(state, lixeiras) {
      state.lixeiras = lixeiras
    },
    resetLixeiras(state) {
      state.lixeiras = null
    },
    insertLixeira(state, newLixeira) {
      state.lixeiras.push(newLixeira)
    },
    updateLixeira(state, updateLixeira) {
      let idx = state.lixeiras.findIndex((t) => t.id === updateLixeira.id)
      if (idx >= 0) {
        state.lixeiras[idx] = updateLixeira
      }
    },
  },
  getters: {
    loggedInUser: (state) => {
      return state.loggedInUser
    },
    admins: (state) => {
      return state.admins
    },
    users: (state) => {
      return state.users
    },
    totalAdmins: (state) => {
      return state.admins.length
    },
  },
  actions: {
    async login(context, credentials) {
      try {
        let response = await axios.post('loginBackOffice', credentials)
        axios.defaults.headers.common.Authorization = "Bearer " + response.data.access_token
        sessionStorage.setItem('token', response.data.access_token)
        console.log(response)
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
        console.log(response)
        context.commit('setUser', response.data.user)
      } catch (error) {
        delete axios.defaults.headers.common.Authorization
        context.commit('resetUser', null)
        throw error
      }
    },
    async loadLixeiras(context) {
      try {
        let response = await axios.get('lixeiras')
        context.commit('setLixeiras', response.data)
        return response.data
      } catch (error) {
        context.commit('resetLixeiras', null)
        throw error
      }
    },
    async aprovarLixeira(context, lixeira) {
      let response = await axios.patch('lixeiras/' + lixeira.id + '/aprovar',{'aprovado':lixeira.aprovado})
      context.commit('updateLixeira', lixeira)
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
      context.commit('updateUser',user)
      return response.data.data
    },
    async unblockUser(context, user) {
      let response = await axios.patch("users/" + user.id + '/block', { "blocked": false })
      user.blocked = false
      context.commit('updateUser',user)
      return response.data.data
    },

    async refresh(context) {
      let userPromise = context.dispatch('loadLoggedInUser')
      let usersPromise = context.dispatch('loadUsers')
      let lixeirasPromise = context.dispatch('loadLixeiras')
      await userPromise
      await usersPromise
      await lixeirasPromise

    },
  },
  modules: {
  }
})
