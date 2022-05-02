<template>
 
    <li v-if="!$store.state.loggedInUser"  class="nav-item">
            <router-link class="nav-link" href="#" to="/register"
              ><i class="bi bi-box-arrow-in-right"></i>
              Register
            </router-link>
          </li>
  
</template>

<script>
export default {
  name: 'HelloWorld',
  props: {
    msg: String
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h3 {
  margin: 40px 0 0;
}
ul {
  list-style-type: none;
  padding: 0;
}
li {
  display: inline-block;
  margin: 0 10px;
}
a {
  color: #42b983;
}
</style>
