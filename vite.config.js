import StimulusHMR from 'vite-plugin-stimulus-hmr'

export default {
  root: './assets',
  base: '/assets',
  server: {
    open: '/',
    proxy: {
      '^(?!/assets/).*': 'http://localhost:8080',
    }
  },
  build: {
    manifest: true,
    rollupOptions: {
      input: 'main.js',
    },
  },
  plugins: [
    StimulusHMR(),
  ],
}
