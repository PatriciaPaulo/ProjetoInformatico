<template>
  <form
      class="row g-3 needs-validation"
      novalidate
      @submit.prevent="register"
  >
    <h3 class="mt-5 mb-3">Register user</h3>
    <hr>
    <div class="mb-3">
      <div class="mb-3">
        <label
            for="inputPhoneNumber"
            class="form-label"
        >Phone Number</label>
        <input
            type="number"
            class="form-control"
            id="inputPhoneNumber"
            required
            v-model="credentials.phone_number"
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
            for="inputName"
            class="form-label"
        >Name</label>
        <input
            type="text"
            class="form-control"
            id="inputName"
            required
            v-model="credentials.name"
        >
        <field-error-message
            :errors="errors"
            fieldName="password"
        ></field-error-message>
      </div>
    </div>
    <div class="mb-3">
      <div class="mb-3">
        <label
            for="inputEmail"
            class="form-label"
        >Email</label>
        <input
            type="email"
            class="form-control"
            id="inputEmail"
            required
            v-model="credentials.email"
        >
        <field-error-message
            :errors="errors"
            fieldName="email"
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

    <div class="mb-3">
      <div class="mb-3">
        <label
            for="inputConfirmationCode"
            class="form-label"
        >Confirmation code</label>
        <input
            type="password"
            class="form-control"
            id="inputConfirmationCode"
            required
            v-model="credentials.confirmation_code"
        >
        <field-error-message
            :errors="errors"
            fieldName="confirmation_code"
        ></field-error-message>
      </div>
    </div>
    <div class="mb-3 d-flex justify-content-center">
      <button
          type="button"
          class="btn btn-primary px-5"
          @click="register"
      >Register</button>
    </div>
  </form>
</template>

<script>
import axios from "axios";

export default {
  name: 'Register',
  data () {
    return {
      credentials: {
        phone_number: null,
        name: '',
        password: '',
        email: '',
        photo: '',
        confirmation_code: null,
      },
      errors: null,
    }
  },
  emits: [
    'register',
  ],
  methods: {
    async register () {
      await axios.post('vcards', this.credentials)
          .then(() => {
            this.$toast.success(`${this.credentials.name}'s VCard was created with this phone number: ${this.credentials.phone_number}.`)
            this.$emit('register')
            this.$router.push({ name: 'Login' , params: { username: this.credentials.phone_number,password:this.credentials.password}})
          })
          .catch(() => {
            this.credentials.password = ''
            this.$toast.error('Cant register with these credentials!')
          })
    },
    onFileChange(e) {
      var files = e.target.files || e.dataTransfer.files;
      if (!files.length) return;
      this.file = files[0]
      this.createImage(this.file);
    },
  }
}
</script>

<style scoped>
</style>
