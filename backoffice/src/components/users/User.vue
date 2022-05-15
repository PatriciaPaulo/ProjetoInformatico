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
      user: this.$store.state.loggedInUser,
    }
  },
  methods: {
    save () {
      this.$axios.put('users/' + this.user.id, this.user)
        .then(() => {
           this.$toast.success(
            "User " + this.user.username + " has been updates."
          );
        })
        .catch((error) => {
          console.log(error);
        })
    },
    cancel () {
      // Replace this code to navigate back
      this.loadUser(this.id)
    }
  }
}
</script>
