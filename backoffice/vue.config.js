// vue.config.js

/**
 * @type {import('@vue/cli-service').ProjectOptions}
 */
 module.exports = {
    // options...
    devServer: {
        proxy: '',
        proxy: {
            '^/api': {
              target: 'http://localhost:5000',
              changeOrigin: true
            }
          }
    }
  }