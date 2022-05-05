<template>
  <form class="row g-3 needs-validation" novalidate @submit.prevent="save">
    <h3 class="mt-5 mb-3">User #{{ this.editingUser.id }}</h3>
    <hr />
    <div class="d-flex flex-wrap justify-content-between">
      <div class="w-75 pe-4">
        <div class="mb-3">
          <label for="inputName" class="form-label">Name</label>
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
          <label for="inputEmail" class="form-label">Email</label>
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
          <label for="inputAdmin" class="form-label">Admin</label>
          <input
            type="checkbox"
            v-model="editingUser.admin"
            true-value="true"
            false-value="false"
          />
     
        </div>
         <div class="mb-3 px-1">
          <label for="inputAdmin" class="form-label">Blocked</label>
          <input
            type="checkbox"
            v-model="editingUser.blocked"
            true-value="true"
            false-value="false"
          />
     
        </div>
      </div>
      <div class="w-25">
        <div class="mb-3">
          <label class="form-label">Photo</label>
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
  emits: ["save", "cancel"],
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
    save() {
      this.$emit("save", this.editingUser);
    },
    cancel() {
      this.$emit("cancel", this.editingUser);
    },
  },
};
</script>

<style scoped>
.total_hours {
  width: 26rem;
}
</style>
