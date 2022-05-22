<template>
  <h3 class="mt-5 mb-3">lixeira #{{ this.id }}</h3>
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
        <field-error-message
          :errors="errors"
          fieldName="nome"
        ></field-error-message>
      </div>
      <ConfirmDialog></ConfirmDialog>
      <div class="mb-3 px-1">
        <label for="inputAprovado" class="form-label" label="Confirm"
          >Aprovado</label
        >
        <input
          type="checkbox"
          v-model="arrayLixeira[0].aprovado"
          true-value="true"
          false-value="false"
          @change="check(this.lixeira)"
        />
      </div>

      <div class="mb-3">
        <label for="inputType" class="form-label">Estado</label>
        <select v-model="arrayLixeira[0].estado" name="inputType">
          <option v-for="(value, key) in estados" :value="value" :key="key">
            {{ value }}
          </option>
        </select>
        <field-error-message
          :errors="errors"
          fieldName="inputType"
        ></field-error-message>
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
    check(lixeira) {
      this.$confirm.require({
        message: "Are you sure you want to proceed?",
        header: "Confirmation",
        icon: "pi pi-exclamation-triangle",
        accept: () => {
          //callback to execute when user confirms the action
          this.$nextTick(() => {
            this.$store.dispatch("aprovarLixeira", lixeira).then(() => {
              this.$toast.success("lixeira " + lixeira.nome);
            });
          });
        },

        reject: () => {
          lixeira.aprovado = !lixeira.aprovado;
        },
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
    this.arrayLixeira[0] = this.$store.getters.lixeiras.filter((lix) => {
      return lix.id === this.id;
    });
  },
};
</script>
