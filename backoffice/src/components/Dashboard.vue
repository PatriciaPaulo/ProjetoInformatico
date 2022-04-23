<template>
  {{$store.state.loggedInUser}}
  <div v-if="!$store.state.loggedInUser" >
    <div class="d-flex flex-wrap justify-content-between">
      <div class="w-75 pe-4">
        <br>
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
                disabled
                v-model="$store.state.loggedInUser.name"
            >

          </div>

          <div class="mb-3">
            <label
                for="inputEmail"
                class="form-label"
            >Email</label>
            <input
                type="email"
                class="form-control"
                id="inputEmail"
                placeholder="Email"
                disabled
                v-model="$store.state.loggedInUser.email"
            >

          </div></div></div>

  </div>
  <div v-else>

  </div>
</template>

<script>

export default {
  name: 'Dashboard',

  mounted() {
    document.title = "Dashboard"
  }
}
</script>

<style scoped lang="scss">
</style>
