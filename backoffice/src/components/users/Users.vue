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
    <template #empty> No users found. </template>
    <template #loading> Loading users data. Please wait. </template>
    <template #header>
      <div class="flex justify-content-between">
        <div class="">
          <select class="form-select" id="selectBlocked" v-model="filter">
            <option value="-1">Todos</option>
            <option value="1">Admins</option>
            <option value="0">Users</option>
          </select>
        </div>
        <div>
          <h1 class="">Users</h1>
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
    <Column field="name" header="Name" :sortable="true"></Column>
    <Column field="username" header="Username" :sortable="true"></Column>
    <Column field="email" header="Email" :sortable="true"></Column>
    <Column field="admin" header="Admin" :sortable="true"></Column>
    <Column header="Block" >
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
    <Column header="Delete">  
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
          console.log(user + "users");
          this.$store
            .dispatch("blockUser", user)
            .then((response) => {
              this.$toast.success(
                "User " + user.username + " has been blocked."
              );
              this.$store.dispatch("loadUsers", response);
            })
            .catch((error) => {
              console.log(error);
            });
        },
      });
    },
    blockUser(user) {
      this.$confirm.require({
        message: `Are you sure you want to delete user ${user.username}?`,
        header: "Confirmation",
        icon: "pi pi-exclamation-triangle",
        accept: () => {
          //callback to execute when user confirms the action
          console.log(user + "users");
          this.$store
            .dispatch("blockUser", user)
            .then((response) => {
              this.$toast.success(
                "User " + user.username + " has been blocked."
              );
              this.$store.dispatch("loadUsers", response);
            })
            .catch((error) => {
              console.log(error);
            });
        },
        reject: () => {
          //callback to execute when user rejects the action
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
