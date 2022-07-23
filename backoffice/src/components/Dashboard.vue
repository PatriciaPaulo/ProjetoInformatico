<template>
 
  <div v-if="$store.state.loggedInUser" >
    <div class="d-flex flex-wrap justify-content-between">
      DASHBOOAARD
       <router-link
                  class="nav-link w-100 me-3"
                  :class="{active: $route.name === 'Register'}"
                  :to="{ name: 'Register'}"
              >
                <i class="bi bi-list-check"></i>
                Criar Admin
              </router-link>
    </div>
  </div>
   <!--
  <div v-else>
    NO ACCESS
    ??
    
  </div>-->
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
