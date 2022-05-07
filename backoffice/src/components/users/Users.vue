<template>
  <h3 class="mt-5 mb-3">Users</h3>
   <div class="mx-2 mt-2 flex-grow-1 filter-div">
        <label
            for="selectBlocked"
            class="form-label"
        >Filter:</label>
        <select
            class="form-select"
            id="selectBlocked"
            v-model="filter"
        >
          <option value="-1">Todos</option>
          <option value="1">Admins</option>
          <option value="0">Users</option>
        </select>
      </div>
  <hr />
  <user-table
    :users="filteredUsers"
    @delete="deleteUser"
    @block="blockUser"
    @unblock="unblockUser"
  ></user-table>
</template>

<script>
import UserTable from "./UserTable.vue";

export default {
  name: "Users",
  components: {
    UserTable,
  },
  data() {
    return {
        filter: "-1",
    };
  },
  computed: {
    totalUsers() {
      return this.users.length;
    },
    users(){
      return this.$store.getters.users;
    },
    filteredUsers(){
    return this.users.filter(t =>
        (this.filter === "-1"
            || this.filter === "0" && !t.admin
            || this.filter === "1" && t.admin
        ))
    },
    
  },
  methods: {
    deleteUser(user) {
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
    blockUser(user) {
      console.log(user + "users")
      this.$store
        .dispatch("blockUser", user)
        .then((response) => {
          this.$toast.success(
            "User " + user.username + " has been blocked."
          );
           this.$store
        .dispatch("loadUsers", response)
        })
        .catch((error) => {
          console.log(error);
        });
    },
    unblockUser(user) {
      console.log(user + "users")
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
  },
  mounted() {
    this.$store.dispatch('loadUsers');
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
