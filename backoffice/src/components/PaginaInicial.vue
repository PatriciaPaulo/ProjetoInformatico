<template>
    <br />
  <div class="text-center" v-if="$store.state.loggedInUser">
      <h1>Gestão e Monitorização da Aplicação Móvel</h1>
      <br />
      <h1>Social Plogging Lovers</h1>
       <br />
        <br />
         <br />
  </div>

  <div v-else></div>
</template>

<script>
export default {
  name: "PaginaInicial",

  mounted() {
    document.title = "Pagina Inicial";
  },
};
</script>

<style scoped lang="scss">
</style>
