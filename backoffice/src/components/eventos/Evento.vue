<template>
  <h3 class="mt-5 mb-3">evento #{{ this.id }}</h3>
  <hr />
  <div v-if="evento" class="d-flex flex-wrap justify-content-between">
    <div class="w-75 pe-4">
      <div class="mb-3">
        <label for="inputName" class="form-label">Nome</label>
        <input
          type="text"
          class="form-control"
          id="inputNome"
          placeholder="evento nome"
          required
          v-model="evento.nome"
          disabled
        />
      </div>
      <div class="mb-3">
        <label for="inputOrganizador" class="form-label">Organizador</label>
        <input
          type="text"
          class="form-control"
          id="inputOrganizador"
          placeholder="Organizador"
          required
          :value="this.userName(evento.organizador)"
          disabled
        />
      </div>
      <div class="mb-3 px-1">
        <label for="inputEstado" class="form-label">Estado</label>
        <input
          type="estado"
          class="form-control"
          id="inputEstado"
          placeholder="estado"
          required
          v-model="evento.estado"
          disabled
        />
      </div>
      <div class="mb-3 px-1">
        <label for="inputRestricoes" class="form-label">Restrições</label>
        <input
          type="estado"
          class="form-control"
          id="inputRestricoes"
          placeholder="restricoes"
          required
          v-model="evento.restricoes"
          disabled
        />
      </div>
      <div class="mb-3 px-1">
        <label for="inputEstado" class="form-label">Acessibilidade</label>
        <input
          type="estado"
          class="form-control"
          id="inputEstado"
          placeholder="estado"
          required
          v-model="evento.acessibilidade"
          disabled
        />
      </div>
    </div>
  </div>
  <ConfirmDialog></ConfirmDialog>

  <DataTable
    :value="lixeirasNoEvento"
    :paginator="true"
    stripedRows
    :rows="5"
    :loading="isLoading"
    :globalFilterFields="['nome', 'estado', 'criador']"
    class="p-datatable-sm"
  >
    <template #empty> No lixeiras found. </template>
    <template #loading> Loading lixeiras data. Please wait. </template>
    <template #header>
      <div class="flex justify-content-between">
        <div>
          <h1 class="">Lixeiras</h1>
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
  name: "Evento",
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
    check(evento) {
      this.$confirm.require({
        message: "Are you sure you want to proceed?",
        header: "Confirmation",
        icon: "pi pi-exclamation-triangle",
        accept: () => {
          //callback to execute when user confirms the action
          this.$nextTick(() => {
            this.$store.dispatch("aprovarEvento", evento).then(() => {
              this.$toast.success("evento " + evento.nome);
            });
          });
        },

        reject: () => {
          evento.aprovado = !evento.aprovado;
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
      this.$router.push({ name: "Eventos" });
    },
    loadLixeiras() {
      this.isLoading = true;
      this.$axios
        .get("eventos/" + this.id + "/lixeiras")
        .then((response) => {
          this.isLoading = false;
          response.data.data.forEach((lixEv) => {
            console.log(lixEv.lixeiraID + "id");
            //this.lixeirasNoEvento.push(this.lixeira(lixEv.lixeiraID));
            console.log(this.lixeira(lixEv.lixeiraID) + "lixeira");
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
    lixeira(id) {
      var r = this.$store.getters.lixeiras.find((lix) => {
        return lix.id === id;
      });
      return r;
    },
  },
  computed: {
    evento() {
      return this.$store.getters.eventos.find((event) => {
        return event.id === this.id;
      });
    },
    lixeirasNoEvento() {
      let lixEve = []
      if(!this.evento){
        return lixEve
      }
      this.evento.lixeiras.forEach((lixEv) => {
        console.log(lixEv.lixeiraID + "id");
        lixEve.push(this.lixeira(lixEv.lixeiraID));
        console.log(this.lixeira(lixEv.lixeiraID) + "lixeira");
      });
      return lixEve;
    },
  },
  mounted() {
    this.loadLixeiras();
  },
};
</script>
