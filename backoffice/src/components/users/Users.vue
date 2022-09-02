<template>
  <hr />
  <ConfirmDialog></ConfirmDialog>
  <DataTable
    :value="filteredUsers"
    :paginator="true"
    stripedRows
    :rows="10"
    :loading="isLoading"
    :globalFilterFields="['name', 'username', 'email']"
    :filters="filters"
    class="p-datatable-sm"
  >
    <template #empty> Utilizadores não encontrados. </template>
    <template #loading> A carregar os dados sobre os utilizadores. Porfavor aguarde. </template>
    <template #header>
      <div class="flex justify-content-between">
        <div class="">
          <select class="form-select" id="selectBlocked" v-model="filter">
            <option value="-1">Todos</option>
            <option value="1">Administradores</option>
            <option value="0">Utilizadores</option>
          </select>
        </div>
        <div>
          <h1 class="">Utilizadores</h1>
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
    <Column field="username" header="Username" :sortable="true"></Column>
    <Column field="email" header="Email" :sortable="true"></Column>
      <Column header="Admin">
        <template #body="{ data }">
          <div class="d-flex justify-content-between">
            <i v-if="data.admin" class="bi bi-xs bi-check2"></i>
            <i v-else class="bi bi-xs bi-x"></i>
          </div>
        </template>
      </Column>
    
    
    <Column header="Bloqueado" >
      <template #body="{ data }">
        <div v-if="data.id !== this.$store.state.loggedInUser.id" class="d-flex justify-content-between">
          <button
            v-if="data.blocked == '1'"
            class="btn btn-xs btn-light"
            @click="unblockUser(data)"
          >
            <i class="bi bi-xs bi-file-lock2"></i>
          </button>

          <button v-else class="btn btn-xs btn-light" @click="blockUser(data)">
            <i class="bi bi-xs bi-file"></i>
          </button>
        </div>
      </template>
    </Column>
    <Column header="Eliminar">  
      <template #body="{ data }">
        <div v-if="data.id !== this.$store.state.loggedInUser.id" class="d-flex justify-content-between">
          <button
            class="btn btn-xs btn-light"
            @click="deleteUser(data)"
            label="Confirm"
          >
            <i class="bi bi-xs bi-x-square-fill"></i>
          </button>
        </div>
      </template>
    </Column>
  </DataTable>
</template>

<script>
import DataTable from "primevue/datatable";
import Column from "primevue/column";
import InputText from "primevue/inputtext";
import { FilterMatchMode } from "primevue/api";
import ConfirmDialog from "primevue/confirmdialog";

export default {
  name: "Users",
  components: {
    DataTable,
    Column,
    InputText,
    ConfirmDialog,
  },
  data() {
    return {
      filter: "-1",
      users: [],
      isLoading: false,
      filters: {
        global: { value: null, matchMode: FilterMatchMode.CONTAINS },
      },
    };
  },
  computed: {
    filteredUsers() {
      return this.$store.getters.users.filter(
        (t) =>
          this.filter === "-1" ||
          (this.filter === "0" && !t.admin) ||
          (this.filter === "1" && t.admin)
      );
    },
  },
  methods: {
    deleteUser(user) {
      this.$confirm.require({
        message: `Are you sure you want to delete user ${user.username}?`,
        header: "Confirmation",
        icon: "pi pi-exclamation-triangle",
        accept: () => {
          //callback to execute when user confirms the action
          this.$store
            .dispatch("deleteUser", user)
            .then(() => {
              this.$toast.success(
                "User " + user.username + " has been deleted."
              );
            })
            .catch((error) => {
              console.log(error);
            });
        },
        reject: () => {
          //callback to execute when user rejects the action
        
        },
      });
    },
    blockUser(user) {
      this.$confirm.require({
        message: `Tens a certeza que queres bloquear o utilizador ${user.username}?`,
        header: "Confirmation",
        icon: "pi pi-exclamation-triangle",
        accept: () => {
          //callback to execute when user confirms the action
          console.log(user + "users");
          this.$store
            .dispatch("blockUser", user)
            .then((response) => {
              this.$toast.success(
                "Utilizador " + user.username + " foi bloqueado."
              );
              this.$store.dispatch("loadUsers", response);
            })
            .catch((error) => {
              console.log(error);
            });
        },
        reject: () => {
          //callback to execute when user rejects the action
          
        },
      });
    },
    unblockUser(user) {
      this.$confirm.require({
        message: `Are you sure you want to unblock user ${user.username}?`,
        header: "Confirmation",
        icon: "pi pi-exclamation-triangle",
        accept: () => {
          //callback to execute when user confirms the action
          console.log(user + "users");
           this.$store
            .dispatch("unblockUser", user)
            .then(() => {
              this.$toast.success(
                "User " + user.username + " has been unblocked."
              );
            })
            .catch((error) => {
              console.log(error);
            });
        },
        reject: () => {
          //callback to execute when user rejects the action
         
        },
      });
    },
  },
  mounted() {
    this.isLoading = true;
    this.$store
      .dispatch("loadUsers")
      .then(() => {
        this.isLoading = false;
      })
      .catch((error) => {
        this.isLoading = false;
        console.log(error);
      });
  },
};
</script>

<style scoped>
.filter-div {
  min-width: 12rem;
}
.total-filtro {
  margin-top: 2.3rem;
}
</style>
