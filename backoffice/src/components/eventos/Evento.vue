<template>
  <h3 class="mt-5 mb-3">evento #{{ this.id }}</h3>
  <hr />
  <div class="d-flex flex-wrap justify-content-between">
    <div class="w-75 pe-4">
      <div class="mb-3">
        <label for="inputName" class="form-label">Nome</label>
        <input
          type="text"
          class="form-control"
          id="inputNome"
          placeholder="evento nome"
          required
          v-model="arrayEvento[0].nome"
          disabled
        />
        <field-error-message
          :errors="errors"
          fieldName="nome"
        ></field-error-message>
      </div>
      <ConfirmDialog></ConfirmDialog>
      <div class="mb-3 px-1">
        <label for="inputEstado" class="form-label">Estado</label>
        <input
          type="estado"
          class="form-control"
          id="inputEstado"
          placeholder="estado"
          required
          v-model="arrayEvento[0].estado"
          disabled
        />
        <field-error-message
          :errors="errors"
          fieldName="inputEstado"
        ></field-error-message>
      </div>
    </div>
  </div>
  <DataTable
    :value="this.lixeirasNoEvento"
    :paginator="true"
    stripedRows
    :rows="5"
    :loading="isLoading"
    :globalFilterFields="['nome', 'estado', 'criador']"
    :filters="filters"
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
            {{ lixeira(data) }}
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
  components: { ConfirmDialog,DataTable,Column },
  props: {
    id: {
      type: Number,
      required: true,
    },
  },
  data() {
    return {
      lixeirasNoEvento: [],
      arrayEvento: [],
      estados: ["Criado", "Começado", "Cancelado", "Finalizado"],
      errors: null,
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
        .get("eventos/" + this.id +"/lixeiras")
        .then((response) => {
          this.isLoading = false;
          this.lixeirasNoEvento = response.data.data
        })
        .catch((error) => {
          this.isLoading = false;
          console.log(error);
        });
    },
     userName(id) {
      var r = this.$store.getters.users.filter(user => {
        return user.id === id
      })
      return r[0] ? r[0].username : "Not found"
    },
    lixeira(id){
       var r = this.$store.getters.lixeiras.filter(lix => {
        return lix.id === id
      })
      return r[0] ? r[0] : "Not found"
    }
     
  },

  mounted() {
    this.loadLixeiras()
  },
  created() {
    //when f5
     this.arrayEvento[0] =  this.$store.getters.eventos.filter(event => {
      return event.id === this.id
    })
   

  },
};
</script>
