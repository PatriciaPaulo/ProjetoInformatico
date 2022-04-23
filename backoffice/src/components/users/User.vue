<template>
  <user-detail
    :user="user"
    @save="save"
    @cancel="cancel"
  ></user-detail>
</template>

<script>
import UserDetail from "./UserDetail.vue"

export default {
  name: 'User',
  components: {
    UserDetail
  },
  props: {
    id: {
      type: Number,
      default: null
    },
  },
  data () {
    return {
      user: this.newUser(),
    }
  },
  watch: {
    // beforeRouteUpdate was not fired correctly
    // Used this watcher instead to update the ID
    id: {
      immediate: true,
      handler (newValue) {
        this.loadUser(newValue)
      }
    },
  },
  methods: {
    newUser () {
      return {
        id: null,
        name: '',
        email: '',
        gender: 'M',
        photo_url: null
      }
    },
    loadUser (id) {
      if (!id || (id < 0)) {
        this.user = this.newUser()
      } else {
        this.$axios.get('users/' + id)
          .then((response) => {
            this.user = response.data.data
          })
          .catch((error) => {
            console.log(error)
          })
      }
    },
    save () {
      this.$axios.put('users/' + this.id, this.user)
        .then((response) => {
          console.log('User Updated')
          console.dir(response.data.data)
        })
        .catch((error) => {
          console.dir(error)
        })
    },
    cancel () {
      // Replace this code to navigate back
      this.loadUser(this.id)
    }
  },
  mounted () {
  }
}
</script>
