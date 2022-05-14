<template>
  <GMapMap
    :center="centerLix"
    :zoom="7"
    map-type-id="terrain"
    style="width: 100%; height: 500px"
  >
  {{position(38,-9)}}
    <GMapCluster>
      <GMapMarker
        v-for="lixeira in this.lixeiras"
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
  data() {
    return {
      centerLix: this.center,
    };
  },
  methods: {
    position(lat, long) {
      return ( {
        lat: parseInt(lat),
        lng: parseInt(long),
      });
    },
  }

};
</script>

