<template>
  <hr />

  <div class="wrapper">
    <div class="child" >
      <ConfirmDialog></ConfirmDialog>
      <DataTable
        :value="filteredGarbageSpots"
        :paginator="true"
        stripedRows
        :rows="10"
        :loading="isLoading"
        :globalFilterFields="['nome', 'estado', 'criador', 'aprovado']"
        :filters="filters"
        class="p-datatable-sm"
      >
        <template #empty> No garbageSpots found. </template>
        <template #loading> Loading garbageSpots data. Please wait. </template>
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
              <h1 class="">GarbageSpots</h1>
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
        <Column field="name" header="Nome" :sortable="true"></Column>
        <Column field="criador" header="Criador" :sortable="true">
          <template #body="{ data }">
            {{ userName(data.creator) }}
          </template>
        </Column>
        <Column field="status" header="Estado" :sortable="true"></Column>
        <Column header="Aprovada">
          <template #body="{ data }">
            <div class="d-flex justify-content-between">
              <i v-if="data.approved" class="bi bi-xs bi-check2"></i>
              <i v-else class="bi bi-xs bi-file"></i>
            </div>
          </template>
        </Column>
        <Column header="Editar">
          <template #body="{ data }">
            <div class="d-flex justify-content-between">
              <button
                class="btn btn-xs btn-light"
                @click="editGarbageSpot(data)"
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
                @click="deleteGarbageSpot(data)"
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
      <garbageSpot-map
      :garbageSpots="this.garbageSpots"
      :center="center"
      >
      </garbageSpot-map>
    </div>
  </div>
</template>

<script>
import DataTable from "primevue/datatable";
import Column from "primevue/column";
import InputText from "primevue/inputtext";
import { FilterMatchMode } from "primevue/api";
import ConfirmDialog from "primevue/confirmdialog";
import GarbageSpotMap from "./GarbageSpotMap";

export default {
  name: "GarbageSpots",
  components: {
    GarbageSpotMap,
    DataTable,
    Column,
    InputText,
    ConfirmDialog,
  },
  data() {
    return {
      filter: "-1",
      garbageSpots: [],
      isLoading: false,
      filters: {
        global: { value: null, matchMode: FilterMatchMode.CONTAINS },
      },
      garbageSpotToDelete: null,
      center: { lat: 38.093048, lng: -9.84212 },
    };
  },
  methods: {
    editGarbageSpot(garbageSpot) {
      console.log("id  - "+ garbageSpot.id)
      this.$router.push({ name: "GarbageSpot", params: { id: garbageSpot.id } });
    },
    deleteGarbageSpot(garbageSpot) {
      this.$store
        .dispatch("deleteGarbageSpot", garbageSpot)
        .then(() => {
          this.$toast.success(
            "GarbageSpot " + garbageSpot.name + " was deleted successfully."
          );
          
        })
        .catch((error) => {
          console.log(error);
        });
    },
    loadGarbageSpots() {
      this.isLoading = true;
      this.$store
        .dispatch("loadGarbageSpots")
        .then((response) => {
          this.garbageSpots = response;
          this.isLoading = false;
        })
        .catch((error) => {
          console.log(error);
          this.isLoading = false;
        });
    },
     userName(id) {
      var r = this.$store.getters.users.filter(user => {
        return user.id === id
      })
      return r[0] ? r[0].username : "Not found"
    },
   
  },
  computed:{
    filteredGarbageSpots() {
      return this.garbageSpots.filter(
        (t) =>
          this.filter === "-1" ||
          (this.filter === "0" && !t.approved) ||
          (this.filter === "1" && t.approved)
      );
    }
  },
  mounted() {
    this.loadGarbageSpots(), (document.title = "GarbageSpots");
  },
};
</script>

<style scoped>
.wrapper {
  margin-right: -100%;
  display: flex;

}
.child {
  width: 25%;
  height: 100%;
  padding-right: 1%;
  margin-right: 2%;
  float: left;
}
</style>
