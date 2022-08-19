<template>
    <br />
  <div class="text-center" v-if="$store.state.loggedInUser">
      <h1>Gestão e Monitorização da Aplicação Móvel</h1>
      <br />
      <h1>Social Plogging Lovers</h1>
       <br />
        <br />
         <br />
      <router-link
        class="nav-link w-100 me-3"
        :class="{ active: $route.name === 'Admin' }"
        :to="{ name: 'Admin' }"
      >
        <button type="button" class="btn btn-dark">Criar Administrador</button>

      </router-link>
    
  </div>

  <div v-else></div>
</template>

<script>
export default {
  name: "Dashboard",

  mounted() {
    document.title = "Dashboard";
  },
};
</script>

<style scoped lang="scss">
</style>
