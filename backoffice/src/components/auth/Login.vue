<template>
  
  <form class="row g-3 needs-validation " novalidate @submit.prevent="login">
    <h3 class="mt-5 mb-3">Login</h3>
    <hr />
    <div class="mb-3 mx-auto">
      <div class="mb-3 w-25 ">
        <label for="inputUsername" class="form-label">Email</label>
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
      <div class="mb-3 w-25">
        <label for="inputPassword" class="form-label">Password</label>
        <input
          type="password"
          class="form-control"
          id="inputPassword"
          required
          v-model="credentials.password"
        />
      </div>
    </div>
    <div class="mb-5 ">
      <button type="button" class="btn btn-dark px-5" @click="login">
        Entrar
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
      if(this.credentials.email== "" ||this.credentials.password=="" ){
        this.$toast.error("Preencha todos os campos!");
      }else{
        this.$store
        .dispatch("login", this.credentials)
        .then(() => {
          this.$toast.success(
            "Utilizador " +
              this.$store.state.loggedInUser.name +
              " entrou na aplicação."
          );
          this.$router.push({ name: "PaginaInicial" });
        })
        .catch((error) => {
          this.credentials.password = "";
          if (error.response.status == 403) {
            this.$toast.error("Utilizador está bloqueado!");
          } else if (error.response.status == 409) {
            this.$toast.error("Utilizador foi eliminado!");
          } else {
            this.$toast.error("Crendenciais erradas!");
          }
        });
      }
     
    },
  },

};
</script>

<style scoped>

.form-control {
  background-color: #e4e6eb;
}
</style>
