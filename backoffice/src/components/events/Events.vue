<template>
  <hr />

  <div>
    <ConfirmDialog></ConfirmDialog>
    <DataTable
      :value="this.events"
      :paginator="true"
      stripedRows
      :rows="10"
      :loading="eventsIsLoading"
      :globalFilterFields="['name', 'status']"
      :filters="filters"
      class="p-datatable-sm"
    >
      <template #empty> Eventos não encontrados. </template>
      <template #loading>
        A carregar os dados sobre os eventos. Porfavor aguarde.
      </template>
      <template #header>
        <div class="flex justify-content-between">
          <div class=""></div>
          <div>
            <h1 class="">Eventos</h1>
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
      <Column field="status" header="Estado" :sortable="true"></Column>
      <Column
        field="startDate"
        header="Data de Inicio"
        :sortable="true"
      ></Column>
       <Column
        field="createdDate"
        header="Data de Criação"
        :sortable="true"
      ></Column>
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
      <Column header="Cancelar">
        <template #body="{ data }">
          <div class="d-flex justify-content-between">
            <button
              class="btn btn-xs btn-light"
              @click="cancelarEvento(data)"
              label="Confirm"
            >
              <i class="bi bi-xs bi-x-square-fill"></i>
            </button>
          </div>
        </template>
      </Column>
    </DataTable>
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
    InputText,
  },
  data() {
    return {
      events: [],
      eventsIsLoading: false,
      filters: {
        global: { value: null, matchMode: FilterMatchMode.CONTAINS },
      },
      center: { lat: 38.093048, lng: -9.84212 },
    };
  },
  methods: {
    editEvent(event) {
      console.log("id  - " + event.id);
      this.$router.push({ name: "Event", params: { id: event.id } });
    },
    cancelarEvento(event) {
       this.$confirm.require({
        message: `Tens a certeza que queres cancelar o evento ${event.name}?`,
        header: "Confirmation",
        icon: "pi pi-exclamation-triangle",
        accept: () => {
          //callback to execute when user confirms the action
         this.$store
        .dispatch("cancelarEvento", event)
        .then(() => {
          this.$toast.success(
            "event " + event.name + " was cancelled successfully."
          );
        })
        .catch((error) => {
          console.log(error);
        });
        },
        reject: () => {
          //callback to execute when user rejects the action
        
        },
       
        })
    },
    loadEvents() {
      this.eventsIsLoading = true;
      this.$store
        .dispatch("loadEvents")
        .then((response) => {
          this.events = response;
          this.eventsIsLoading = false;
        })
        .catch((error) => {
          console.log(error);
          this.eventsIsLoading = false;
        });
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
