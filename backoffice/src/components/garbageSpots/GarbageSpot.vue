<template>
  <h3 class="mt-5 mb-3">Local de Lixo #{{ arrayGarbageSpot[0].id }}</h3>
  <hr />
  <div class="d-flex flex-wrap justify-content-between">
    <div class="w-75 pe-4">
      <div class="mb-3">
        <label for="inputName" class="form-label">Nome</label>
        <input
          type="text"
          class="form-control"
          id="inputNome"
          placeholder="GarbageSpot nome"
          required
          v-model="arrayGarbageSpot[0].name"
          disabled
        />
      </div>
        <div class="mb-3">
        <label for="inputName" class="form-label ">Criador</label>
        <input
          type="text"
          class="form-control"
          id="inputNome"
          placeholder="Criador"
          required
          :value ="userName(arrayGarbageSpot[0].creator)"
          disabled
        />
      </div>
      <ConfirmDialog></ConfirmDialog>
      <div v-if="arrayGarbageSpot[0].approved">
        <label for="inputAprovado" class="form-label" label="Confirm"
          >Aprovado</label
        >
        <button
          @click="nAprovar(arrayGarbageSpot[0])"
          type="button"
          class="btn btn-danger"
        >
          Não aprovar
        </button>
      </div>
      <div v-else>
        <label for="inputAprovado" class="form-label" label="Confirm"
          >Não Aprovado</label
        >
        <button
          @click="Aprovar(arrayGarbageSpot[0])"
          type="button"
          class="btn btn-success"
        >
          Aprovar
        </button>
      </div>
    
    </div>
  </div>

  <div class="mb-3 d-flex justify-content-end">
    <button type="button" class="btn btn-light px-5" @click="cancel">
      Voltar
    </button>
  </div>
  <GarbageSpotMap
    :garbageSpots="arrayGarbageSpot"
    :center="
      position(arrayGarbageSpot[0].latitude, arrayGarbageSpot[0].longitude)
    "
  ></GarbageSpotMap>
</template>

<script>
import ConfirmDialog from "primevue/confirmdialog";
import GarbageSpotMap from "./GarbageSpotMap";
export default {
  name: "GarbageSpot",
  components: { GarbageSpotMap, ConfirmDialog },
  props: {
    id: {
      type: Number,
      required: true,
    },
  },
  data() {
    return {
      arrayGarbageSpot: [],
      estados: ["Muito sujo", "Pouco sujo", "Limpo"],
      errors: null,
    };
  },
  methods: {
    Aprovar(garbageSpot) {
      this.$confirm.require({
        message: "Certeza que queres aprovar a garbageSpot?",
        header: "Confirmation",
        icon: "pi pi-exclamation-triangle",
        accept: () => {
          //callback to execute when user confirms the action
          this.$nextTick(() => {
            garbageSpot.approved = true;
            this.$store.dispatch("aprovarGarbageSpot", garbageSpot).then(() => {
              this.$toast.success("garbageSpot " + garbageSpot.name);
            });
          });
        },

        reject: () => {},
      });
    },
    nAprovar(garbageSpot) {
      this.$confirm.require({
        message: "Certeza que queres não aprovar a garbageSpot?",
        header: "Confirmation",
        icon: "pi pi-exclamation-triangle",
        accept: () => {
          //callback to execute when user confirms the action
          this.$nextTick(() => {
            garbageSpot.approved = false;
            this.$store.dispatch("aprovarGarbageSpot", garbageSpot).then(() => {
              this.$toast.success("garbageSpot " + garbageSpot.name);
            });
          });
        },
        reject: () => {},
      });
    },
    mudarEstado(garbageSpot) {
      console.log(garbageSpot + " - garbageSpot");

      console.log(garbageSpot.status + " - garbageSpot");
      this.$confirm.require({
        message: "Certeza que queres mudar o estado da garbageSpot?",
        header: "Confirmation",
        icon: "pi pi-exclamation-triangle",
        accept: () => {
          //callback to execute when user confirms the action
          this.$nextTick(() => {
            this.$store
              .dispatch("updateStatusGarbageSpots", garbageSpot)
              .then(() => {
                this.$toast.success("garbageSpot " + garbageSpot.name);
              });
          });
        },
        reject: () => {},
      });
    },
    position(lat, long) {
      return {
        lat: parseFloat(lat),
        lng: parseFloat(long),
      };
    },
    cancel() {
      this.$router.push({ name: "GarbageSpots" });
    },
    userName(id) {
      var r = this.$store.getters.users.filter((user) => {
        return user.id === id;
      });
      return r[0] ? r[0].username : "Not found";
    },
  },
  created() {
    //when f5
    this.arrayGarbageSpot = this.$store.getters.garbageSpots.filter((lix) => {
      return lix.id === this.id;
    });
  },
};
</script>
