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
  }

</script>
