<template>
  <form class="row g-3 needs-validation" novalidate @submit.prevent="login">
    <h3 class="mt-5 mb-3 text-light text-center">Login</h3>
    <hr />
    <div class="mb-3 mx-auto">
      <div class="mb-3 w-25 mx-auto">
        <label for="inputUsername" class="form-label text-light">Email</label>
        <input
          type="text"
          class="form-control"
          id="inputUsername"
          required
          v-model="credentials.email"
        />
      </div>
    </div>
    <div class="mb-3 mx-auto">
      <div class="mb-3 w-25 mx-auto">
        <label for="inputPassword" class="form-label text-light">Password</label>
        <input
          type="password"
          class="form-control"
          id="inputPassword"
          required
          v-model="credentials.password"
        />
      </div>
    </div>
    <div class="mb-3 d-flex justify-content-center">
      <button type="button" class="btn btn-dark px-5" @click="login">
        Login
      </button>
    </div>
  </form>
</template>

<script>
export default {
  name: "Login",
  data() {
    return {
      credentials: {
        email: "",
        password: "",
      },
      errors: null,
    };
  },
  methods: {
    login() {
      this.$store
        .dispatch("login", this.credentials)
        .then(() => {
          this.$toast.success(
            "User " +
              this.$store.state.loggedInUser.name +
              " has entered the application."
          );
          this.$router.push({ name: "Dashboard" });
        })
        .catch((error) => {
          this.credentials.password = "";
          if (error.response.status == 403) {
            this.$toast.error("USER IS BLOCKED!");
          } else if (error.response.status == 409) {
            this.$toast.error("USER IS DELETED!");
          } else {
            this.$toast.error("User credentials are invalid!");
          }
        });
    },
  },
  mounted() {
    this.credentials.email = this.$route.params.email;
    this.credentials.password = this.$route.params.password;
  },
};
</script>

<style scoped>

.form-control {
  background-color: #e4e6eb;
}
</style>
