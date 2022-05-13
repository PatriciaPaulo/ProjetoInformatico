<template>

<div class="wrapper">
  <div class="child">
    <table class="table">
        <thead>
          <tr>
            <th class="align-middle">Localizacao</th>
            <th class="align-middle">Criador</th>
            <th class="align-middle">Aprovada</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="lixeira in lixeiras" :key="lixeira.id">
            <td class="align-middle">{{ lixeira.nome }}</td>
            <td class="align-middle">{{ userName(lixeira.criador)}}</td>
            <td class="align-middle">
              <i v-if="lixeira.aprovado" class="bi bi-xs bi-check2"></i>
              <i v-else class="bi bi-xs bi-file"></i>
            </td>
            <td class="align-middle">
            
              <button class="btn btn-xs btn-light" @click="editClick(lixeira)">
                <i class="bi bi-xs bi-pencil"></i>
              </button>
            </td>
            <td class="text-end align-middle">
              <button class="btn btn-xs btn-light" @click="deleteClick(lixeira)">
                <i class="bi bi-xs bi-x-square-fill"></i>
              </button>
            </td>
          </tr>
        </tbody>
      </table>
  </div>
  
  <div class="child">
    <GMapMap
      :center="center"
      :zoom="7"
      map-type-id="terrain"
      style="width: 100%; height: 500px"
  >
    <GMapCluster>
      <GMapMarker
          v-for="lixeira in lixeiras"
          :key="lixeira.id"
          :position="position(lixeira.latitude,lixeira.longitude)"
          :clickable="true"
          :draggable="false"
          @click="position(lixeira.latitude,lixeira.longitude)"  
      />

    </GMapCluster>

  </GMapMap>
  </div>



 
</div>
</template>

<script>
export default {
  name: "LixeiraTable",
  props: {
    lixeiras: {
      type: Array,
      default: () => [],
    },
    showEditButton: {
      type: Boolean,
      default: true,
    },
    showDeleteButton: {
      type: Boolean,
      default: true,
    },
  },
  emits: ["edit", "delete"],
  data() {
    return {
      editingLixeiras: this.lixeiras,
      LixeiraToDelete: null,
      center: { lat: 38.093048, lng: -9.84212 },
     
    
    };
  },
  computed: {},
  watch: {
    lixeiras(newLixeiras) {
      this.editingLixeiras = newLixeiras;
    },
  },
  methods: {
    editClick(lixeira) {
      this.$emit("edit", lixeira);
    },
    deleteClick(lixeira) {
      this.$emit("delete", lixeira);
    },
    userName(id){
      return this.$store.getters.users.at(id).username
    },
    position(lat,long){
        return  {
            lat: parseInt(lat),
            lng: parseInt(long),
          }
    }
  },
};
</script>

<style scoped>
.wrapper {
  margin-right: -100%;
}
.child {
  box-sizing: border-box;
  width: 25%;
  height: 100%;
  padding-right: 20px;
  float: left;
}
</style>
