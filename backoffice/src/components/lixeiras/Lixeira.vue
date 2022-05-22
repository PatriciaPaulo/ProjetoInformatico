<template>
  <h3 class="mt-5 mb-3">lixeira #{{ arrayLixeira[0].id }}</h3>
  <hr />
  <div class="d-flex flex-wrap justify-content-between">
    <div class="w-75 pe-4">
      <div class="mb-3">
        <label for="inputName" class="form-label">Nome</label>
        <input
          type="text"
          class="form-control"
          id="inputNome"
          placeholder="Lixeira nome"
          required
          v-model="arrayLixeira[0].nome"
          disabled
        />
      </div>
      <ConfirmDialog></ConfirmDialog>
      <div v-if="arrayLixeira[0].aprovado">
        <label for="inputAprovado" class="form-label" label="Confirm"
          >Aprovado</label
        >
        <button
          @click="nAprovar(arrayLixeira[0])"
          type="button"
          class="btn btn-danger"
        >
          Não aprovar
        </button>
      </div>
      <div v-else>
        <label for="inputAprovado" class="form-label" label="Confirm"
          >Não Aprovado</label
        >
        <button
          @click="Aprovar(arrayLixeira[0])"
          type="button"
          class="btn btn-success"
        >
          Aprovar
        </button>
      </div>

      <div class="mb-3">
        <label for="inputType" class="form-label">Estado</label>
        <select
          v-model="arrayLixeira[0].estado"
          name="inputType"
          @change="mudarEstado(arrayLixeira[0])"
        >
          <option v-for="(value, key) in estados" :value="value" :key="key">
            {{ value }}
          </option>
        </select>
      </div>
    </div>
  </div>
  <div class="mb-3 d-flex justify-content-end">
    <button type="button" class="btn btn-light px-5" @click="cancel">
      Voltar
    </button>
  </div>
  <lixeira-map
    :lixeiras="arrayLixeira"
    :center="position(arrayLixeira[0].latitude, arrayLixeira[0].longitude)"
  ></lixeira-map>
</template>

<script>
import ConfirmDialog from "primevue/confirmdialog";
import LixeiraMap from "./LixeiraMap";
export default {
  name: "Lixeira",
  components: { LixeiraMap, ConfirmDialog },
  props: {
    id: {
      type: Number,
      required: true,
    },
  },
  data() {
    return {
      arrayLixeira: [],
      estados: ["Muito sujo", "Pouco sujo", "Limpo"],
      errors: null,
    };
  },
  methods: {
    Aprovar(lixeira) {
      this.$confirm.require({
        message: "Certeza que queres aprovar a lixeira?",
        header: "Confirmation",
        icon: "pi pi-exclamation-triangle",
        accept: () => {
          //callback to execute when user confirms the action
          this.$nextTick(() => {
            lixeira.aprovado = true;
            this.$store.dispatch("aprovarLixeira", lixeira).then(() => {
              this.$toast.success("lixeira " + lixeira.nome);
            });
          });
        },

        reject: () => {},
      });
    },
    nAprovar(lixeira) {
      this.$confirm.require({
        message: "Certeza que queres não aprovar a lixeira?",
        header: "Confirmation",
        icon: "pi pi-exclamation-triangle",
        accept: () => {
          //callback to execute when user confirms the action
          this.$nextTick(() => {
            lixeira.aprovado = false;
            this.$store.dispatch("aprovarLixeira", lixeira).then(() => {
              this.$toast.success("lixeira " + lixeira.nome);
            });
          });
        },
        reject: () => {},
      });
    },
    mudarEstado(lixeira) {
      console.log(lixeira + " - lixeira");

      console.log(lixeira.estado + " - lixeira");
      this.$confirm.require({
        message: "Certeza que queres mudar o estado da lixeira?",
        header: "Confirmation",
        icon: "pi pi-exclamation-triangle",
        accept: () => {
          //callback to execute when user confirms the action
          this.$nextTick(() => {
            this.$store.dispatch("mudarEstadoLixeira", lixeira).then(() => {
              this.$toast.success("lixeira " + lixeira.nome);
            });
          });
        },
        reject: () => {},
      });
    },
    position(lat, long) {
      return {
        lat: parseFloat(lat),
        lng: parseFloat(long),
      };
    },
    cancel() {
      this.$router.push({ name: "Lixeiras" });
    },
  },
  created() {
    //when f5
    this.arrayLixeira = this.$store.getters.lixeiras.filter((lix) => {
      return lix.id === this.id;
    });
  },
};
</script>
