<template>
  <!--<table class="table">
    <thead>
      <tr>
        <th class="align-middle">#</th>
        <th class="align-middle">Username</th>
        <th class="align-middle">Name</th>
        <th class="align-middle">Email</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="user in users" :key="user.id">
        <td class="align-middle">{{ user.id }}</td>
        <td class="align-middle">{{ user.username }}</td>
        <td class="align-middle">{{ user.name }}</td>
        <td class="align-middle">{{ user.email }}</td>

        <td
          v-if="user.id === this.$store.state.loggedInUser.id"
          class="align-middle"
        >
          <router-link href="#" :to="{ name: 'User', params: { id: user.id } }"
            ><i class="bi bi-xs bi-person-square"></i
          ></router-link>
        </td>

        <td
          v-if="user.id !== this.$store.state.loggedInUser.id"
          class="align-middle"
        >
          <div class="d-flex justify-content-end">
            <button
              v-if="user.blocked == '1'"
              class="btn btn-xs btn-light"
              @click="unblockClick(user)"
            >
              <i class="bi bi-xs bi-file-lock2"></i>
            </button>

            <button
              v-else
              class="btn btn-xs btn-light"
              @click="blockClick(user)"
            >
              <i class="bi bi-xs bi-file"></i>
            </button>
          </div>
        </td>

        <td
          v-if="user.id !== this.$store.state.loggedInUser.id"
          class="text-end align-middle"
        >
          <div class="d-flex justify-content-end">
            <button class="btn btn-xs btn-light" @click="deleteClick(user)">
              <i class="bi bi-xs bi-x-square-fill"></i>
            </button>
          </div>
        </td>
      </tr>
    </tbody>
  </table> -->
  <DataTable
    :value="users"
    :paginator="true"
    class="p-datatable-customers"
    :rows="10"
  >
    <Column field="name" header="Name"></Column>
    <Column field="username" header="Username"></Column>
    <Column field="email" header="Email"></Column>
  </DataTable>
</template>

<script>
import DataTable from "primevue/datatable";
import Column from "primevue/column";
export default {
  name: "UserTable",
  components: {
    DataTable,Column
  },
  props: {
    users: {
      type: Array,
      default: () => [],
    },
    showEditButton: {
      type: Boolean,
      default: true,
    },
  },
  emits: ["delete", "block", "unblock"],
  methods: {
    photoFullUrl(user) {
      return user.photo_url
        ? this.$serverUrl + "/storage/fotos/" + user.photo_url
        : "./assets/img/avatar-none.png";
    },
    deleteClick(user) {
      this.$emit("delete", user);
    },
    blockClick(user) {
      this.$emit("block", user);
    },
    unblockClick(user) {
      this.$emit("unblock", user);
    },
  },
};
</script>

<style scoped>
button {
  margin-left: 3px;
  margin-right: 3px;
}

.img_photo {
  width: 3.2rem;
  height: 3.2rem;
}
</style>
