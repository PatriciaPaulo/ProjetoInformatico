<template>
  <confirmation-dialog
    ref="confirmationDialog"
    confirmationBtn="Discard changes and leave"
    msg="Do you really want to leave? You have unsaved changes!"
    @confirmed="leaveConfirmed"
  >
  </confirmation-dialog>

  <lixeira-detail
    :lixeira="lixeira"
    :errors="errors"
    @save="save"
    @cancel="cancel"
  ></lixeira-detail>
</template>

<script>
import LixeiraDetail from "./LixeiraDetail";
export default {
  name: "Lixeira",
  components: { LixeiraDetail },
  props: {
    id: {
      type: Number,
      default: null,
    },
  },
  data() {
    return {
      lixeira: this.loadLixeira(this.id),
      errors: null,
    };
  },
 /* watch: {
    // beforeRouteUpdate was not fired correctly
    // Used this watcher instead to update the ID
    id: {
      immediate: true,
      handler(newValue) {
        this.loadLixeira(newValue);
      },
    },
  },*/
  methods: {
    dataAsString() {
      return JSON.stringify(this.lixeira);
    },
    loadLixeira(id) {
      this.$axios
        .get("lixeiras/" + id)
        .then((response) => {
          console.log(response.data.data);
          this.lixeira = response.data.data;
          this.originalValueStr = this.dataAsString();
        })
        .catch((error) => {
          console.log(error);
        });
    },
    save() {
      this.errors = null;
      this.$axios
        .put("lixeiras/" + this.id, this.lixeira)
        .then((response) => {
          this.$toast.success(
            "Lixeira #" + response.data.data.name + " was updated successfully."
          );
          this.lixeira = response.data.data;
          this.originalValueStr = this.dataAsString();
          this.$store.commit("updateLixeira", response.data.data);
          this.$router.back();
        })
        .catch((error) => {
          if (error.response.status == 422) {
            this.$toast.error(
              "Lixeira #" +
                this.id +
                " was not updated due to validation errors!"
            );
            this.errors = error.response.data.errors;
          } else {
            this.$toast.error(
              "Lixeira #" +
                this.id +
                " was not updated due to unknown server error!"
            );
          }
        });
    },
    cancel() {
      // Replace this code to navigate back
      // this.loadUser(this.id)
      this.$router.back();
    },
    leaveConfirmed() {
      if (this.nextCallBack) {
        this.nextCallBack();
      }
    },
  },
  beforeRouteLeave(to, from, next) {
    this.nextCallBack = null;
    let newValueStr = this.dataAsString();
    if (this.originalValueStr != newValueStr) {
      this.nextCallBack = next;
      let dlg = this.$refs.confirmationDialog;
      dlg.show();
    } else {
      next();
    }
  },
};
</script>
