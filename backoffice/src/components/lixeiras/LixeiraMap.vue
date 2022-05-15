<template>
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
        :position="position(lixeira.latitude, lixeira.longitude)"
        :clickable="true"
        :draggable="false"
        @click="centerLix = position(lixeira.latitude, lixeira.longitude)"
      />
      
    </GMapCluster>
  </GMapMap>
</template>
<script>
export default {
  name: "LixeiraMap",
  props: {
    lixeiras: {
      type: Object,
      required: true,
    },
    center: {
      type: Object,
      required: true,
    },
  },
  methods: {
    position(lat, long) {
      console.log(lat + 'aabbba'+ long)
      return ( {
        lat: parseFloat(lat),
        lng: parseFloat(long),
      });
    },
  },
   mounted() {
    //when f5 
    this.lixeiras.forEach(element => {
        console.log(element + "lixeira")
    });
  },



};
</script>

