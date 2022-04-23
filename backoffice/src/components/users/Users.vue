<template>
  <h3 class="mt-5 mb-3">Team Members</h3>
  <hr>
  <user-table
    :users="users"
    :showId="false"
    @edit="editUser"
  ></user-table>
</template>

<script>
import UserTable from "./UserTable.vue"

export default {
  name: 'Users',
  components: {
    UserTable
  },
  data () {
    return {
      users: []
    }
  },
  computed: {
    totalUsers () {
      return this.users.length
    }
  },
  methods: {
    loadUsers () {
      this.$axios.get('users')
        .then((response) => {
          this.users = response.data.data
        })
        .catch((error) => {
          console.log(error)
        })
    },
    editUser (user) {
      console.log('Navigate to Edit User with ID = ' + user.id)
    },
  },
  mounted () {
    this.loadUsers()
  }
}
</script>

<style scoped>
.filter-div {
  min-width: 12rem;
}
.total-filtro {
  margin-top: 2.3rem;
}
</style>
