<template>
  <form
    class="row g-3 needs-validation"
    novalidate
    @submit.prevent="save"
  >
    <h3 class="mt-5 mb-3">User #{{ this.editingUser.id }}</h3>
    <hr>
    <div class="d-flex flex-wrap justify-content-between">
      <div class="w-75 pe-4">
        <div class="mb-3">
          <label
            for="inputName"
            class="form-label"
          >Name</label>
          <input
            type="text"
            class="form-control"
            id="inputName"
            placeholder="User Name"
            required
            v-model="editingUser.name"
          >
        </div>

        <div class="mb-3 px-1">
          <label
            for="inputEmail"
            class="form-label"
          >Email</label>
          <input
            type="email"
            class="form-control"
            id="inputEmail"
            placeholder="Email"
            required
            v-model="editingUser.email"
          >
        </div>
        <div class="d-flex ms-1 mt-4 flex-wrap justify-content-between">
          <div class="mb-3  me-3 flex-grow-1">
            <div class="form-check">
              <input
                class="form-check-input"
                type="checkbox"
                true-value="A"
                false-value="M"
                v-model="editingUser.type"
                id="inputType"
              >
              <label
                class="form-check-label"
                for="inputType"
              >
                User is Administrator
              </label>
            </div>
          </div>
          <div class="mb-3 ms-xs-3 flex-grow-1">
            <div class="form-check  form-check-inline">
              <input
                class="form-check-input"
                type="radio"
                name="radioGender"
                value="M"
                required
                v-model="editingUser.gender"
                id="inputGenderM"
              >
              <label
                class="form-check-label"
                for="inputGenderM"
              >Masculino</label>
            </div>
            <div class="form-check form-check-inline">
              <input
                class="form-check-input"
                type="radio"
                name="radioGender"
                value="F"
                v-model="editingUser.gender"
                id="inputGenderF"
              >
              <label
                class="form-check-label"
                for="inputGenderF"
              >Feminino</label>
            </div>
          </div>
        </div>
      </div>
      <div class="w-25">
        <div class="mb-3">
          <label class="form-label">Photo</label>
          <div class="form-control text-center">
            <img
              :src="photoFullUrl"
              class="w-100"
            >
          </div>
        </div>
      </div>
    </div>
    <div class="mb-3 d-flex justify-content-end">
      <button
        type="button"
        class="btn btn-primary px-5"
        @click="save"
      >Save</button>
      <button
        type="button"
        class="btn btn-light px-5"
        @click="cancel"
      >Cancel</button>
    </div>

  </form>
</template>

<script>
export default {
  name: 'UserDetail',
  components: {
  },
  props: {
    user: {
      type: Object,
      required: true
    },
  },
  emits: [
    'save',
    'cancel'
  ],
  data () {
    return {
      editingUser: this.user
    }
  },
  watch: {
    user (newUser) {
      this.editingUser = newUser
    }
  },
  computed: {
    photoFullUrl () {
      return this.editingUser.photo_url
        ? this.$serverUrl + "/storage/fotos/" + this.editingUser.photo_url
        : "./assets/img/avatar-none.png"
    },
  },
  methods: {
    save () {
      this.$emit('save', this.editingUser)
    },
    cancel () {
      this.$emit('cancel', this.editingUser)
    },
  }
}
</script>

<style scoped>
.total_hours {
  width: 26rem;
}
</style>
