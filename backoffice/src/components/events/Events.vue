<template>
  <hr />

  <div class="wrapper">
    <div class="child">
      <ConfirmDialog></ConfirmDialog>
      <DataTable
        :value="this.events"
        :paginator="true"
        stripedRows
        :rows="10"
        :loading="isLoading"
        :globalFilterFields="['nome', 'estado', 'organizador']"
        :filters="filters"
        class="p-datatable-sm"
      >
        <template #empty> No garbageSpots found. </template>
        <template #loading> Loading garbageSpots data. Please wait. </template>
         <template #header>
          <div class="flex justify-content-between">
            <div class="">
           
            </div>
            <div>
              <h1 class="">Events</h1>
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
        <Column field="organizador" header="Organizador" :sortable="true">
          <template #body="{ data }">
            {{ userName(data) }}
          </template>
        </Column>
        <Column field="status" header="Estado" :sortable="true"></Column>
        <Column field="startDate" header="dataInicio" :sortable="true"></Column>
        <Column header="Editar">
          <template #body="{ data }">
            <div class="d-flex justify-content-between">
              <button
                class="btn btn-xs btn-light"
                @click="editEvent(data)"
                label="Confirm"
              >
                <i class="bi bi-xs bi-pencil"></i>
              </button>
            </div>
          </template>
        </Column>
        <Column header="Eliminar">
          <template #body="{ data }">
            <div class="d-flex justify-content-between" >
              <button
                class="btn btn-xs btn-light"
                @click="deleteEvent(data)"
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
      {{ "map" }}
    </div>
  </div>
</template>

<script>
import DataTable from "primevue/datatable";
import Column from "primevue/column";
import { FilterMatchMode } from "primevue/api";
import ConfirmDialog from "primevue/confirmdialog";
import InputText from "primevue/inputtext";
export default {
  name: "Events",
  components: {
    DataTable,
    Column,
    ConfirmDialog,
    InputText
  },
  data() {
    return {
      events: [],
      isLoading: false,
      filters: {
        global: { value: null, matchMode: FilterMatchMode.CONTAINS },
      },
      garbageSpotToDelete: null,
      center: { lat: 38.093048, lng: -9.84212 },
    };
  },
  methods: {
    editEvent(event) {
      console.log("id  - " + event.id);
      this.$router.push({ name: "Event", params: { id: event.id } });
    },
    deleteEvent(event) {
      this.$store
        .dispatch("deleteEvent", event)
        .then(() => {
          this.$toast.success(
            "event " + event.name + " was deleted successfully."
          );
        })
        .catch((error) => {
          console.log(error);
        });
    },
    loadEvents() {
      this.isLoading = true;
      this.$store
        .dispatch("loadEvents")
        .then((response) => {
          this.events = response;
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
  mounted() {
    this.loadEvents(), (document.title = "Events");
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
