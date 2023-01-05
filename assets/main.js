import 'vite/modulepreload-polyfill'
import 'bootstrap/dist/css/bootstrap.css'

import * as Turbo from '@hotwired/turbo'

import { Application } from "@hotwired/stimulus"
import { registerControllers } from 'stimulus-vite-helpers'

const application = Application.start()
const controllers = import.meta.glob('./controllers/**/*_controller.js', {eager: true})
registerControllers(application, controllers)


import i18next from 'i18next'

await i18next.init({
  lng: 'ru',
  debug: true,
  resources: {
    en: {
      translation: {
        "key": "hello world"
      }
    },
    ru: {
      translation: {
        "string": "Строка",
        "required-string": "Обязательная строка",
      },
    },
  },
});
