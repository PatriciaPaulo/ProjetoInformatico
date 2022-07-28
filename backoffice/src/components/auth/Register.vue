<template>
  <form class="row g-3 needs-validation" novalidate @submit.prevent="register">
    <h3 class="mt-5 mb-3">Create Admin</h3>
    <hr />
    <div class="mb-3">
      <div class="mb-3">
        <label for="inputUsername" class="form-label text-light">Username</label>
        <input
          type="text"
          class="form-control"
          id="inputUsername"
          required
          v-model="credentials.username"
        />
      </div>
      <div class="mb-3">
        <label for="inputPassword" class="form-label text-light">Password</label>
        <input
          type="password"
          class="form-control"
          id="inputPassword"
          required
          v-model="credentials.password"
        />
      </div>
      <div class="mb-3">
        <label for="inputPasswordConfirmation" class="form-label text-light"
          >Password Confirmation</label
        >
        <input
          type="password"
          class="form-control"
          id="inputPasswordConfirmation"
          required
          v-model="credentials.passwordConfirmation"
        />
      </div>
      <div class="mb-3">
        <div class="mb-3">
          <label for="inputName" class="form-label text-light">Name</label>
          <input
            type="text"
            class="form-control"
            id="inputName"
            required
            v-model="credentials.name"
          />
        </div>
      </div>
      <div class="mb-3">
        <div class="mb-3">
          <label for="inputEmail" class="form-label text-light">Email</label>
          <input
            type="email"
            class="form-control"
            id="inputEmail"
            required
            v-model="credentials.email"
          />
        </div>
      </div>
    </div>

    <div class="mb-3 d-flex justify-content-center">
      <button type="button" class="btn btn-primary px-5" @click="register">
        Register
      </button>
    </div>
  </form>
</template>

<script>
import axios from "axios";

export default {
  name: "Register",
  data() {
    return {
      credentials: {
        username: null,
        name: "",
        password: "",
        passwordConfirmation: "",
        email: "",
      },
      errors: null,
    };
  },
  emits: ["register"],
  methods: {
    async register() {
      await axios
        .post("registerAdmin", this.credentials)
        .then(() => {
          this.$toast.success(`${this.credentials.name}'s admin was created.`);
          this.$emit("register");
          this.$router.push({
            name: "Dashboard",
          });
        })
        .catch(() => {
          this.credentials.password = "";
          this.$toast.error("Cant register with these credentials!");
        });
    },
  },
};
</script>

<style scoped>
</style>
