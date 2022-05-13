<template>
  <h3 class="mt-5 mb-3">lixeira #{{ this.lixeira.id }}</h3>
  <hr />
  <div class="d-flex flex-wrap justify-content-between">
    <div class="w-75 pe-4">
      <div class="mb-3">
        <label for="inputName" class="form-label">Localizacao</label>
        <input
          type="text"
          class="form-control"
          id="inputLocalizacao"
          placeholder="Lixeira localizacao"
          required
          v-model="editingLixeira.localizacao"
          disabled
        />
        <field-error-message
          :errors="errors"
          fieldName="name"
        ></field-error-message>
      </div>
      <div class="mb-3 px-1">
        <label for="inputAdmin" class="form-label">Aprovado</label>
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
          <option value="estado1">estado1</option>
          <option value="estado2">estado2</option>
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
export default {
  name: "LixeiraDetail",
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
    };
  },
  watch: {
    lixeira(newLixeira) {
      console.log("asdasdasd");
      this.editingLixeira = newLixeira;
    },
  },
  methods: {
    save() {
      this.$emit("save", this.editingLixeira);
    },
    cancel() {
      this.$emit("cancel", this.editingLixeira);
    },
    check(lixeira) {
      this.$nextTick(() => {
        console.log(lixeira.aprovado);
        this.$store.dispatch('aprovarLixeira',lixeira)
      });
    },
  },
};
</script>

<style scoped>
</style>
