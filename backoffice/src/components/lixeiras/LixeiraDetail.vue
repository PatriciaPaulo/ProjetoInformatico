<template>
  <h3 class="mt-5 mb-3">lixeira #{{ this.lixeira.id }}</h3>
  <hr />
  <div class="d-flex flex-wrap justify-content-between">
    <div class="w-75 pe-4">
      <div class="mb-3">
        <label for="inputName" class="form-label">Nome</label>
        <input
          type="text"
          class="form-control"
          id="inputLocalizacao"
          placeholder="Lixeira localizacao"
          required
          v-model="editingLixeira.nome"
          disabled
        />
        <field-error-message
          :errors="errors"
          fieldName="name"
        ></field-error-message>
      </div>
      <ConfirmDialog></ConfirmDialog>
      <div class="mb-3 px-1">
        <label for="inputAdmin" class="form-label" label="Confirm"
          >Aprovado</label
        >
        <input
          type="checkbox"
          v-model="editingLixeira.aprovado"
          true-value="true"
          false-value="false"
          @change="check(editingLixeira)"
        />
      </div>

      <div class="mb-3">
        <label for="inputType" class="form-label">Estado</label>
        <select v-model="editingLixeira.estado" name="inputType">
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
</template>

<script>
import ConfirmDialog from "primevue/confirmdialog";
export default {
  name: "LixeiraDetail",
  components: {
    ConfirmDialog,
  },
  props: {
    lixeira: {
      type: Object,
      required: true,
    },
    errors: {
      type: Object,
      default: null,
    },
  },
  emits: ["save", "cancel"],
  data() {
    return {
      editingLixeira: this.lixeira,
      estados: ["Muito sujo", "Pouco sujo", "Limpo"],
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
            this.$store.dispatch("aprovarLixeira", lixeira)
            .then(() => {
              this.$toast.success(
                "lixeira " + lixeira.nome
              );
            });
          });
        },

        reject: () => {
          lixeira.aprovado = !lixeira.aprovado
        },
      });
    },
  },
};
</script>

<style scoped>
</style>
