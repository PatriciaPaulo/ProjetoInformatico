<template>
  <form class="row g-3 needs-validation" novalidate @submit.prevent="save">
    <h3 class="mt-5 mb-3">User #{{ this.editingUser.id }}</h3>
    <hr />
    <div class="d-flex flex-wrap justify-content-between">
      <div class="w-75 pe-4">
        <div class="mb-3">
          <label for="inputName" class="form-label text-light">Name</label>
          <input
            type="text"
            class="form-control"
            id="inputName"
            placeholder="User Name"
            required
            v-model="editingUser.name"
          />
        </div>

        <div class="mb-3 px-1">
          <label for="inputEmail" class="form-label text-light">Email</label>
          <input
            type="email"
            class="form-control"
            id="inputEmail"
            placeholder="Email"
            required
            v-model="editingUser.email"
          />
        </div>
        <div class="mb-3 px-1">
          <label for="inputAdmin" class="form-label text-light">Admin</label>
          <input
            disabled
            type="checkbox"
            v-model="editingUser.admin"
            true-value="true"
            false-value="false"
          />
        </div>
      </div>
      <div class="w-25">
        <div class="mb-3">
          <label class="form-label text-light">Photo</label>
          <div class="form-control text-center">
            <img :src="photoFullUrl" class="w-100" />
          </div>
        </div>
      </div>
    </div>
    <div class="mb-3 d-flex justify-content-end">
      <button type="button" class="btn btn-primary px-5" @click="save">
        Save
      </button>
      <button type="button" class="btn btn-light px-5" @click="cancel">
        Cancel
      </button>
    </div>
  </form>
</template>

<script>

export default {
  name: "UserDetail",
  components: {},
  props: {
    user: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      editingUser: this.user,
    };
  },
  computed: {
    photoFullUrl() {
      return this.editingUser.photo_url
        ? this.$serverUrl + "/storage/fotos/" + this.editingUser.photo_url
        : "./assets/img/avatar-none.png";
    },
  },
  methods: {
    save () {
      this.$axios.put('users/me', this.editingUser)
        .then(() => {
           this.$toast.success(
            "User " + this.user.username + " has been updates."
          );
        })
        .catch((error) => {
          console.log(error);
        })
    },
    cancel () {
     this.$axios.get('users/me')
        .then((response) => {
          this.editingUser = response.data.data
        })
        .catch((error) => {
          console.log(error);
        })
    },
  },
};
</script>

<style scoped>

</style>
