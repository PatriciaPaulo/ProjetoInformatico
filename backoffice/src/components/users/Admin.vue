<template>
  <form class="row g-3 needs-validation" novalidate @submit.prevent="register">
    <h3 class="mt-5 mb-3 text-center">Criar Administrador</h3>

    <hr />
    <div class="mb-3 mx-auto">
      <div class="mb-3 w-25 mx-auto">
        <label for="inputUsername" class="form-label"
          >Username</label
        >
        <input
          type="text"
          class="form-control"
          id="inputUsername"
          required
          v-model="credentials.username"
        />
      </div>
      <div class="mb-3 mx-auto">
        <div class="mb-3 w-25 mx-auto">
          <label for="inputEmail" class="form-label ">Email</label>
          <input
            type="email"
            class="form-control"
            id="inputEmail"
            required
            v-model="credentials.email"
          />
        </div>
      </div>
      <div class="mb-3 mx-auto">
        <div class="mb-3 w-25 mx-auto">
          <label for="inputName" class="form-label ">Name</label>
          <input
            type="text"
            class="form-control"
            id="inputName"
            required
            v-model="credentials.name"
          />
        </div>
      </div>
      <div class="mb-3 w-25 mx-auto">
        <label for="inputPassword" class="form-label "
          >Password</label
        >
        <input
          type="password"
          class="form-control"
          id="inputPassword"
          required
          v-model="credentials.password"
        />
      </div>
      <div class="mb-3 w-25 mx-auto">
        <label for="inputPasswordConfirmation" class="form-label "
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
     
     
    </div>

    <div class="mb-3 d-flex justify-content-center">
      <button type="button" class="btn btn-primary px-5" @click="validate">
        Criar
      </button>
    </div>
  </form>
</template>

<script>
import axios from "axios";

export default {
  name: "Admin",
  data() {
    return {
      credentials: {
        username: "",
        name: "",
        password: "",
        passwordConfirmation: "",
        email: "",
      },
      errors: null,
    };
  },
  methods: {
    validate() {
      if (
        this.credentials.email.length === 0 ||
        this.credentials.name.length === 0 ||
        this.credentials.password.length === 0 ||
        this.credentials.passwordConfirmation.length === 0 ||
        this.credentials.username.length === 0 
      ) {
        this.$toast.error("Campros precisam de ser preenchidos!");
      } else {
        if (
          this.credentials.passwordConfirmation.length === 0 ||
          this.credentials.password != this.credentials.passwordConfirmation
        ) {
            this.$toast.error("Palavra-Passe e Confirmação da Palavra-Passe precisam de ser idênticas");
  
           
        } else {
          if (
            !this.credentials.email.match(
              /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
            )
          ) {
              this.$toast.error("Email inválido");
  
          } else {
            this.register();
          }
        }
      }
    },
    async register() {
      await axios
        .post("registerAdmin", this.credentials)
        .then(() => {
          this.$toast.success(`Administrador ${this.credentials.name} foi criado.`);
          this.$emit("register");
          this.$router.push({
            name: "PaginaInicial",
          });
        })
        .catch((error) => {
          this.$toast.error(`Credenciais inválidas! - ${error}}`);
        });
    },
  },
};
</script>

<style scoped>
</style>
