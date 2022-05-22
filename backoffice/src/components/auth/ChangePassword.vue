<template>
  <h3 class="mt-5 mb-3">Change Credentials</h3>
  <hr>

  <div>
  <form
      class="row g-3 needs-validation"
      novalidate
      @submit.prevent="changePassword"
  >

      <h4 class="mt-5 mb-3">Change Password</h4>
      <hr>

      <div class="mb-3">
        <div class="mb-3">
          <label
              for="inputCurrentPassword"
              class="form-label"
          >Current Password</label>
          <input
              type="password"
              class="form-control"
              id="inputCurrentPassword"
              v-model="passwords.current_password"
              required
          >
        </div>
      </div>
      <div class="mb-3">
        <div class="mb-3">
          <label
              for="inputPassword"
              class="form-label"
          >New Password</label>
          <input
              type="password"
              class="form-control"
              id="inputPassword"
              required
              v-model="passwords.password"
          >
        </div>
      </div>
      <div class="mb-3">
        <div class="mb-3">
          <label
              for="inputPasswordConfirmation"
              class="form-label"
          >Password Confirmation</label>
          <input
              type="password"
              class="form-control"
              id="inputPasswordConfirmation"
              required
              v-model="passwords.password_confirm"
          >
        </div>
      </div>

      <div class="mb-3 d-flex justify-content-center">
        <button
            type="button"
            class="btn btn-primary px-5"
            @click="changePassword"
            :disabled="!passwords.current_password || !passwords.password || !passwords.password_confirm"
        >Change Password</button>
      </div>
  </form>

    </div>
<div  v-if="$store.state.loggedInUser.user_type == 'V'">
  <form
      class="row g-3 needs-validation"
      novalidate
      @submit.prevent="changeConfirmationCode"

  >

    <h4 class="mt-5 mb-3">Change Confirmation code</h4>
    <hr>

    <div class="mb-3">
      <div class="mb-3">
        <label
            for="inputConfirmationCode"
            class="form-label"
        >New Confirmation code</label>
        <input
            type="password"
            class="form-control"
            id="inputConfirmationCode"
            required
            v-model="passwords.confirmation_code"
        >
      </div>
    </div>
    <div class="mb-3">
      <div class="mb-3">
        <label
            for="inputCurrentPas"
            class="form-label"
        >Current Password</label>
        <input
            type="password"
            class="form-control"
            id="inputCurrentPas"
            required
            v-model="passwords.current_password"
        >
      </div>
    </div>
    <div class="mb-3 d-flex justify-content-center">
      <button
          type="button"
          class="btn btn-primary px-5"
          @click="changeConfirmationCode"
          :disabled="!passwords.current_password || !passwords.confirmation_code"
      >Change Confirmation Code</button>
    </div>
  </form>


</div>



</template>

<script>
import axios from "axios";

export default {
  name: 'ChangePassword',
  data () {
    return {
      passwords: {
        current_password: '',
        password: '',
        password_confirm: '',
        confirmation_code: ''
      },
      errors: null,
    }
  },
  emits: [
    'changedPassword',
  ],
  methods: {
    //todo
    async changePassword () {
      const route = this.$store.state.loggedInUser.user_type === 'V' ? 'vcards/' + this.$store.state.loggedInUser.id : 'users/' + this.$store.state.loggedInUser.id
      console.log(this.passwords);
      await axios.patch(route +'/password', this.passwords)
        .then(() => {
          this.$toast.success('User ' + this.$store.getters.loggedInUser.name + ' has changed his password')
          // this.$emit('login')
          this.$router.push({ name: 'Profile' })

        })
        .catch((error) => {
          if(error.response.status == 401){
            this.$toast.error('User ' + this.$store.getters.loggedInUser.name + ' has not changed his password due to wrong password')
          }
          this.errors = error.response.data.errors
          this.credentials.password = ''
          this.$toast.error('User credentials are invalid!')
        })
    },
   


  }
}
</script>

<style scoped>
</style>
