<template>
  <h3 class="mt-5 mb-3">event #{{ this.id }}</h3>
  <hr />
  <div v-if="event" class="d-flex flex-wrap justify-content-between">
    <div class="w-75 pe-4">
      <div class="mb-3">
        <label for="inputName" class="form-label text-light">Nome</label>
        <input
          type="text"
          class="form-control"
          id="inputNome"
          placeholder="event nome"
          required
          v-model="event.name"
          disabled
        />
      </div>
      <!--
      <div class="mb-3">
        <label for="inputOrganizador" class="form-label">Organizador</label>
        <input
          type="text"
          class="form-control"
          id="inputOrganizador"
          placeholder="Organizador"
          required
          :value="this.userName(event.organizador)"
          disabled
        />
      </div>-->
       <div class="mb-3 px-1">
        <label for="inputEstado" class="form-label text-light">Data de Inicio</label>
        <input
          type="text"
          class="form-control"
          id="inputStartDate"
          placeholder="startDate"
          required
          v-model="event.startDate"
          disabled
        />
      </div>
      <div class="mb-3 px-1">
        <label for="inputEstado" class="form-label text-light">Estado</label>
        <input
          type="text"
          class="form-control"
          id="inputEstado"
          placeholder="estado"
          required
          v-model="event.status"
          disabled
        />
      </div>
      
      <div class="mb-3 px-1">
        <label for="inputRestricoes" class="form-label text-light">Restrições</label>
        <input
          type="text"
          class="form-control"
          id="inputRestricoes"
          placeholder="restricoes"
          required
          v-model="event.restrictions"
          disabled
        />
      </div>
      <div class="mb-3 px-1">
        <label for="inputEstado" class="form-label text-light">Acessibilidade</label>
        <input
          type="text"
          class="form-control"
          id="inputEstado"
          placeholder="Acessibilidade"
          required
          v-model="event.accessibility"
          disabled
        />
      </div>
    </div>
  </div>
  <ConfirmDialog></ConfirmDialog>

  <DataTable
    :value="garbageSpotsNoEvent"
    :paginator="true"
    stripedRows
    :rows="5"
    :loading="isLoading"
    :globalFilterFields="['nome', 'estado', 'criador']"
    class="p-datatable-sm"
  >
    <template #empty> No garbageSpots found. </template>
    <template #loading> Loading garbageSpots data. Please wait. </template>
    <template #header>
      <div class="flex justify-content-between">
        <div>
          <h1 class="">GarbageSpots</h1>
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
  </DataTable>
  <div class="mb-3 d-flex justify-content-end">
    <button type="button" class="btn btn-light px-5" @click="cancel">
      Voltar
    </button>
  </div>
</template>

<script>
import ConfirmDialog from "primevue/confirmdialog";
import DataTable from "primevue/datatable";
import Column from "primevue/column";
export default {
  name: "Event",
  components: { ConfirmDialog, DataTable, Column },
  props: {
    id: {
      type: Number,
      required: true,
    },
  },
  data() {
    return {
      estados: ["Criado", "Começado", "Cancelado", "Finalizado"],
      errors: null,
      isLoading: false,
    };
  },
  methods: {
    cancel() {
      this.$router.push({ name: "Events" });
    },
    loadGarbageSpots() {
      this.isLoading = true;
      this.$axios
        .get("events/" + this.id + "/garbageSpots")
        .then((response) => {
          this.isLoading = false;
          response.data.data.forEach((lixEv) => {
            console.log(lixEv.garbageSpotID + "id");
            //this.garbageSpotsNoEvent.push(this.garbageSpot(lixEv.garbageSpotID));
            console.log(this.garbageSpot(lixEv.garbageSpotID) + "garbageSpot");
          });
        })
        .catch((error) => {
          this.isLoading = false;
          console.log(error);
        });
    },
    userName(id) {
      var r = this.$store.getters.users.find((user) => {
        return user.id === id;
      });
      return r ? r.username : "Not found";
    },
    garbageSpot(id) {
      var r = this.$store.getters.garbageSpots.find((lix) => {
        return lix.id === id;
      });
      return r;
    },
  },
  computed: {
    event() {
      return this.$store.getters.events.find((event) => {
        return event.id === this.id;
      });
    },
    garbageSpotsNoEvent() {
      let lixEve = []
      if(!this.event){
        return lixEve
      }
      this.event.garbageSpots.forEach((lixEv) => {
        console.log(lixEv.garbageSpotID + "id");
        lixEve.push(this.garbageSpot(lixEv.garbageSpotID));
        console.log(this.garbageSpot(lixEv.garbageSpotID) + "garbageSpot");
      });
      return lixEve;
    },
  },
  mounted() {
    this.loadGarbageSpots();
  },
};
</script>