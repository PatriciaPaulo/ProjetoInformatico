<template>
  <nav
    class="
      navbar navbar-expand-md navbar-dark
      bg-dark
      sticky-top
      flex-md-nowrap
      p-0
      shadow
    "
  >
    <div class="container-fluid">
      <a class="navbar-brand col-md-3 col-lg-2 me-0 px-3" href="#"
        ><img
          src="./assets/logo.png"
          alt=""
          width="30"
          height="24"
          class="d-inline-block align-text-top"
        />
        App name</a
      >
      <button
        id="buttonSidebarExpandId"
        class="navbar-toggler"
        type="button"
        data-bs-toggle="collapse"
        data-bs-target="#sidebarMenu"
        aria-controls="sidebarMenu"
        aria-expanded="false"
        aria-label="Toggle navigation"
      >
        <span class="navbar-toggler-icon"></span>
      </button>

      <div class="collapse navbar-collapse justify-content-end" >
        <ul class="navbar-nav">
          <li  v-if="!$store.state.loggedInUser" class="nav-item">
            <router-link class="nav-link" href="#" to="/login"
              ><i class="bi bi-box-arrow-in-right"></i>
              Login
            </router-link>
          </li>
          <li v-if="$store.state.loggedInUser" class="nav-item dropdown">
            <a
              class="nav-link dropdown-toggle"
              href="#"
              id="navbarDropdownMenuLink"
              role="button"
              data-bs-toggle="dropdown"
              aria-expanded="false"
            >
              <img
                src="./assets/img/avatar-exemplo-1.jpg"
                class="rounded-circle z-depth-0 avatar-img"
                alt="avatar image"
              />
              <span class="avatar-text">{{$store.state.loggedInUser.nome}}</span>
            </a>
            <ul
              class="dropdown-menu dropdown-menu-dark dropdown-menu-end"
              aria-labelledby="navbarDropdownMenuLink"
            >
              <li>
                <router-link
                  class="dropdown-item"
                  href="#"
                  :to="{ name: 'User', params: { id: $userId } }"
                  ><i class="bi bi-person-square"></i>Profile</router-link
                >
              </li>
              <li>
                <router-link class="dropdown-item" href="#" to="/password"
                  ><i class="bi bi-key-fill"></i>Change password</router-link
                >
              </li>
              <li>
                <hr class="dropdown-divider" />
              </li>
              <li>
                <a class="dropdown-item" href="#" @click="this.$store.dispatch('logout')"
                  ><i class="bi bi-arrow-right"></i>Logout</a
                >
              </li>
            </ul>
          </li>
        </ul>
      </div>
    </div>
  </nav>

  <div class="container-fluid">
    <div class="row">
      <nav
        id="sidebarMenu"
        class="col-md-3 col-lg-2 d-md-block bg-light sidebar collapse"
      >
        <div class="position-sticky pt-3">
           <ul
              class="nav flex-column"
              v-show="$store.state.loggedInUser"
          >
            <li   class="nav-item d-flex justify-content-between align-items-center pe-3" >
              <router-link
                  class="nav-link w-100 me-3"
                  :class="{active: $route.name === 'Dashboard'}"
                  :to="{ name: 'Dashboard'}"
              >
                <i class="bi bi-list-check"></i>
                Dashboard
              </router-link>

            </li>
             <li   class="nav-item d-flex justify-content-between align-items-center pe-3" >
              <router-link
                  class="nav-link w-100 me-3"
                  :class="{active: $route.name === 'Users'}"
                  :to="{ name: 'Users'}"
              >
                <i class="bi bi-list-check"></i>
                Users
              </router-link>
             
            </li>
            <li   class="nav-item d-flex justify-content-between align-items-center pe-3" >
              <router-link
                  class="nav-link w-100 me-3"
                  :class="{active: $route.name === 'Lixeiras'}"
                  :to="{ name: 'Lixeiras'}"
              >
                <i class="bi bi-list-check"></i>
                Lixeiras
              </router-link>
             
            </li>
          </ul>
    
      

      
          <div class="d-block d-md-none">
            <h6
              class="
                sidebar-heading
                d-flex
                justify-content-between
                align-items-center
                px-3
                mt-4
                mb-1
                text-muted
              "
            >
              <span>User</span>
            </h6>
            <ul class="nav flex-column mb-2">
              <li class="nav-item">
                <router-link class="nav-link" href="#" to="/Login">
                  <i class="bi bi-box-arrow-in-right"></i>
                  Login
                </router-link>
              </li>
              <li class="nav-item dropdown">
                <a
                  class="nav-link dropdown-toggle"
                  href="#"
                  id="navbarDropdownMenuLink2"
                  role="button"
                  data-bs-toggle="dropdown"
                  aria-expanded="false"
                >
                  <img
                    src="./assets/img/avatar-exemplo-1.jpg"
                    class="rounded-circle z-depth-0 avatar-img"
                    alt="avatar image"
                  />
                  <span class="avatar-text">User Name</span>
                </a>
                <ul
                  class="dropdown-menu"
                  aria-labelledby="navbarDropdownMenuLink2"
                >
                  <li>
                    <a class="dropdown-item" href="#"
                      ><i class="bi bi-person-square"></i>Profile</a
                    >
                  </li>
                  <li>
                    <router-link class="dropdown-item" href="#" to="/password"
                      ><i class="bi bi-key-fill"></i>Change
                      password</router-link
                    >
                  </li>
                  <li>
                    <hr class="dropdown-divider" />
                  </li>
                  <li>
                    <a class="dropdown-item" href="#"
                      ><i class="bi bi-arrow-right"></i>Logout</a
                    >
                  </li>
                </ul>
              </li>
            </ul>
          </div>
        </div>
      </nav>

      <main class="col-md-9 ms-sm-auto col-lg-10 px-md-4">
        <router-view></router-view>
      </main>
    </div>
  </div>
</template>

<script>
// REMOVE THESE IMPORTS WHEN VUE-ROUTER IS CONFIGURED

export default {
  name: "RootComponent",
  components: {},
  data() {
    return {
    
    }
  },
  mounted() {
    
  },
};
</script>

<style lang="css">
@import "./assets/css/dashboard.css";

.avatar-img {
  margin: -1.2rem 0.8rem -2rem 0.8rem;
  width: 3.3rem;
  height: 3.3rem;
}
.avatar-text {
  line-height: 2.2rem;
  margin: 1rem 0.5rem -2rem 0;
  padding-top: 1rem;
}

.dropdown-item {
  font-size: 0.875rem;
}
</style>
