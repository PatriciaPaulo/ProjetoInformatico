<template>
  <br />
 
  <div class="text-left">
    <h1>Gestão e Monitorização da Aplicação Móvel</h1>
    <br />
    <h1>Social Plogging Lovers</h1>
 <br />
  <br />
    <h6>Entre para monitorização da aplicação</h6>
    <router-link class="nav-link" href="#" to="/Login">
      <button type="button" class="btn btn-dark">Entrar</button>
    </router-link>
  </div>
</template>

<script>
export default {
  name: "Home",

  mounted() {
    document.title = "Home";
  },
};
</script>

<style scoped lang="css">
</style>
