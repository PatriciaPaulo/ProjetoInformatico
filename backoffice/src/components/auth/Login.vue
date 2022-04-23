<template>
  <form
    class="row g-3 needs-validation"
    novalidate
    @submit.prevent="login"
  >
    <h3 class="mt-5 mb-3">Login</h3>
    <hr>
    <div class="mb-3">
      <div class="mb-3">
        <label
          for="inputUsername"
          class="form-label"
        >Username</label>
        <input
          type="text"
          class="form-control"
          id="inputUsername"
          required
          v-model="credentials.username"
        >
        <field-error-message
          :errors="errors"
          fieldName="username"
        ></field-error-message>
      </div>
    </div>
    <div class="mb-3">
      <div class="mb-3">
        <label
          for="inputPassword"
          class="form-label"
        >Password</label>
        <input
          type="password"
          class="form-control"
          id="inputPassword"
          required
          v-model="credentials.password"
        >
        <field-error-message
          :errors="errors"
          fieldName="password"
        ></field-error-message>
      </div>
    </div>
    <div class="mb-3 d-flex justify-content-center">
      <button
        type="button"
        class="btn btn-primary px-5"
        @click="login"
      >Login</button>
    </div>
  </form>
</template>

<script>
export default {
  name: 'Login',
  data () {
    return {
      credentials:{
        username: '',
        password: ''
      },
      errors: null,
    }
  },
  methods: {
    login () {
      this.$store.dispatch('login', this.credentials)
        .then(() => {
          this.$toast.success('User ' + this.$store.state.loggedInUser.name + ' has entered the application.')
          this.$router.push({ name: 'Dashboard' })  
        })
        .catch((error) => { 
          this.credentials.password = ''
          if(error.response.status == 403){
            this.$toast.error('USER IS BLOCKED!')
          } else if(error.response.status == 409 ){
            this.$toast.error('USER IS DELETED!')
          }
            else{
            this.$toast.error('User credentials are invalid!')
          }
        })
    },
  },
  mounted() {
    this.credentials.username = this.$route.params.username;
    this.credentials.password = this.$route.params.password;
  }
}
</script>

<style scoped>
</style>
