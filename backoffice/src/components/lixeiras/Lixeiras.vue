<template>
  <hr />

  <div class="wrapper">
    <div class="child">
      <ConfirmDialog></ConfirmDialog>
      <DataTable
        :value="filteredLixeiras"
        :paginator="true"
        stripedRows
        :rows="10"
        :loading="isLoading"
        :globalFilterFields="['nome', 'estado', 'criador', 'aprovado']"
        :filters="filters"
        class="p-datatable-sm"
      >
        <template #empty> No lixeiras found. </template>
        <template #loading> Loading lixeiras data. Please wait. </template>
        <template #header>
          <div class="flex justify-content-between">
            <div class="">
              <select class="form-select" id="selectBlocked" v-model="filter">
                <option value="-1">Todos</option>
                <option value="1">Aprovadas</option>
                <option value="0">Não Aprovadas</option>
              </select>
            </div>
            <div>
              <h1 class="">Lixeiras</h1>
            </div>

            <div>
              <span class="p-input-icon-left">
                <i class="pi pi-search" />
                <InputText
                  v-model="filters['global'].value"
                  placeholder="Keyword Search"
                />
              </span>
            </div>
          </div>
        </template>
        <Column field="nome" header="Nome" :sortable="true"></Column>
        <Column field="criador" header="Criador" :sortable="true">
          <template #body="{ data }">
            {{ userName(data.criador) }}
          </template>
        </Column>
        <Column field="estado" header="Estado" :sortable="true"></Column>
        <Column header="Aprovada">
          <template #body="{ data }">
            <div class="d-flex justify-content-between">
              <i v-if="data.aprovado" class="bi bi-xs bi-check2"></i>
              <i v-else class="bi bi-xs bi-file"></i>
            </div>
          </template>
        </Column>
        <Column header="Editar">
          <template #body="{ data }">
            <div class="d-flex justify-content-between">
              <button
                class="btn btn-xs btn-light"
                @click="editLixeira(data)"
                label="Confirm"
              >
                <i class="bi bi-xs bi-pencil"></i>
              </button>
            </div>
          </template>
        </Column>
        <Column header="Eliminar">
          <template #body="{ data }">
            <div class="d-flex justify-content-between">
              <button
                class="btn btn-xs btn-light"
                @click="deleteLixeira(data)"
                label="Confirm"
              >
                <i class="bi bi-xs bi-x-square-fill"></i>
              </button>
            </div>
          </template>
        </Column>
      </DataTable>
    </div>

    <div class="child">
      <lixeira-map
      :lixeiras="this.lixeiras"
      :center="center"
      >
      </lixeira-map>
    </div>
  </div>
</template>

<script>
import DataTable from "primevue/datatable";
import Column from "primevue/column";
import InputText from "primevue/inputtext";
import { FilterMatchMode } from "primevue/api";
import ConfirmDialog from "primevue/confirmdialog";
import LixeiraMap from "./LixeiraMap";

export default {
  name: "Lixeiras",
  components: {
    LixeiraMap,
    DataTable,
    Column,
    InputText,
    ConfirmDialog,
  },
  data() {
    return {
      filter: "-1",
      lixeiras: [],
      isLoading: false,
      filters: {
        global: { value: null, matchMode: FilterMatchMode.CONTAINS },
      },
      lixeiraToDelete: null,
      center: { lat: 38.093048, lng: -9.84212 },
    };
  },
  methods: {
    editLixeira(lixeira) {
      console.log("id  - "+ lixeira.id)
      this.$router.push({ name: "Lixeira", params: { id: lixeira.id,lixeira: lixeira } });
    },
    deleteLixeira(lixeira) {
      this.$store
        .dispatch("deleteLixeira", lixeira)
        .then(() => {
          this.$toast.success(
            "Lixeira " + lixeira.name + " was deleted successfully."
          );
          
        })
        .catch((error) => {
          console.log(error);
        });
    },
    loadLixeiras() {
      this.isLoading = true;
      this.$store
        .dispatch("loadLixeiras")
        .then((response) => {
          this.lixeiras = response;
          this.isLoading = false;
        })
        .catch((error) => {
          console.log(error);
          this.isLoading = false;
        });
    },
    userName(id) {
      return this.$store.getters.users.at(id).username;
    },
   
  },
  computed:{
    filteredLixeiras() {
      return this.lixeiras.filter(
        (t) =>
          this.filter === "-1" ||
          (this.filter === "0" && !t.aprovado) ||
          (this.filter === "1" && t.aprovado)
      );
    }
  },
  mounted() {
    this.loadLixeiras(), (document.title = "Lixeiras");
  },
};
</script>

<style scoped>
.wrapper {
  margin-right: -100%;
}
.child {
  box-sizing: border-box;
  width: 25%;
  height: 100%;
  padding-right: 20px;
  float: left;
}
</style>
