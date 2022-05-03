<template>
  <table class="table">
    <thead>
      <tr>
        <th class="align-middle">#</th>

        <th class="align-middle">Name</th>
        <th class="align-middle">Email</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="user in users" :key="user.id">
        <td class="align-middle">{{ user.id }}</td>
        <td class="align-middle">{{ user.name }}</td>
        <td class="align-middle">{{ user.email }}</td>
      
        <td class="text-end align-middle">
          <div class="d-flex justify-content-end">
           <button  v-if="user.blocked == '1'"
              class="btn btn-xs btn-light"
              @click="unblockClick(user)" >
            <i class="bi bi-xs bi-file-lock2"></i>
          </button>

          <button v-else
             class="btn btn-xs btn-light"
             @click="blockClick(user)"
            >
          <i  class="bi bi-xs bi-file"></i>
          </button>
          </div>
        </td>
        <td class="text-end align-middle">
          <div class="d-flex justify-content-end">
            <button class="btn btn-xs btn-light" @click="deleteClick(user)">
              <i class="bi bi-xs bi-x-square-fill"></i>
            </button>
          </div>
        </td>
      </tr>
    </tbody>
  </table>
</template>

<script>
import axios from "axios";
export default {
  name: "UserTable",
  props: {
    users: {
      type: Array,
      default: () => [],
    },
    showEditButton: {
      type: Boolean,
      default: true,
    },
  },
  emits: ["edit"],
  methods: {
    photoFullUrl(user) {
      return user.photo_url
        ? this.$serverUrl + "/storage/fotos/" + user.photo_url
        : "./assets/img/avatar-none.png";
    },
    async deleteClick(user){
     await axios.delete('/users/' + user.id)
          .then(() => {
            this.$toast.success('User has been deleted')
            

          })
          .catch((error) => {
            if(error.response.status == 401){
              this.$toast.error('User ' + user.id + ' has not changed been blocked')
            }})
    }, 
    async blockClick(user){
      console.log(user.id)
        await axios.patch('/users/' + user.id +'/bloquear', {'blocked':true})
          .then(() => {
            this.$toast.success('User ' + user.id + ' has been blocked')
            this.$store.dispatch('loadUsers')
          

          })
          .catch((error) => {
            if(error.response.status == 401){
              this.$toast.error('User ' + user.id + ' has not changed been blocked')
            }})
    },
    async unblockClick(user){
        await axios.patch('/users/' + user.id +'/bloquear', {'blocked':false})
          .then(() => {
            this.$toast.success('User ' + user.id + ' has been unblocked')
            this.$store.dispatch('loadUsers')

          })
          .catch((error) => {
            if(error.response.status == 401){
              this.$toast.error('User ' + user.id + ' has not changed been unblocked')
            }})
    },
    
    
  },
};
</script>

<style scoped>
button {
  margin-left: 3px;
  margin-right: 3px;
}

.img_photo {
  width: 3.2rem;
  height: 3.2rem;
}
</style>
