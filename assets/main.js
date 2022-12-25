import 'vite/modulepreload-polyfill'
import 'bootstrap/dist/css/bootstrap.css'

import { Application } from "@hotwired/stimulus"
import { registerControllers } from 'stimulus-vite-helpers'

const application = Application.start()
const controllers = import.meta.glob('./controllers/**/*_controller.js', {eager: true})
registerControllers(application, controllers)
