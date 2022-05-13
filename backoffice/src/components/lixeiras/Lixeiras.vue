<template>
 
  <h3 class="mt-5 mb-3">Lixeiras</h3>
  <hr>

  <lixeira-table
      :lixeiras="this.lixeiras"
      @edit="editLixeira"
      @delete="deleteLixeira"
  ></lixeira-table>
</template>

<script>
import LixeiraTable from "./LixeiraTable";

export default {
  name: "Lixeiras",
  components: {
    LixeiraTable
  },
  data() {
    return {
      lixeiras: [],
      lixeiraToDelete: null
    }
  },
  methods: {
    editLixeira(lixeira) {
      this.$router.push({name: 'Lixeira', params: {id: lixeira.id}})
    },
    deleteLixeira(lixeira) {
     this.$store.dispatch('deleteLixeira', lixeira)
          .then(() => {
            this.$toast.success('Lixeira ' + lixeira.name + ' was deleted successfully.')
            this.originalValueStr = this.dataAsString()
          })
          .catch((error) => {
            console.log(error)
          })
    },
    loadLixeiras () {
      this.$store.dispatch('loadLixeiras')
          .then((response) => {
            this.lixeiras = response.data
            console.log(this.lixeiras)
          })
          .catch((error) => {
            console.log(error)
          })
    },
  },
  mounted () {
    this.loadLixeiras(),
        document.title = "Lixeiras"
  }
}
</script>

<style scoped>
.filter-div {
  min-width: 12rem;
}

.total-filtro {
  margin-top: 0.35rem;
}

.btn-addtask {
  margin-top: 1.85rem;
}
</style>
