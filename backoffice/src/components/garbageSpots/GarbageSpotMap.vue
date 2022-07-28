<template>
  <GMapMap
    :center="center"
    :zoom="7"
    map-type-id="terrain"
    style="width: 100%; height: 500px"
  >
    <GMapCluster>
      <GMapMarker
        v-for="garbageSpot in garbageSpots"
        :key="garbageSpot.id"
        :position="position(garbageSpot.latitude, garbageSpot.longitude)"
        :clickable="true"
        :draggable="false"
        @click="centerLix = position(garbageSpot.latitude, garbageSpot.longitude)"
      />
      
    </GMapCluster>
  </GMapMap>
</template>
<script>
export default {
  name: "GarbageSpotMap",
  props: {
    garbageSpots: {
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
      return ( {
        lat: parseFloat(lat),
        lng: parseFloat(long),
      });
    },
  },
   mounted() {
    //when f5 
   // this.garbageSpots.forEach(element => {
   //     console.log(element + "garbageSpots")
   // });
  },



};
</script>

