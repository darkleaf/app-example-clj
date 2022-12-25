import { Controller } from '@hotwired/stimulus'
import i18next from 'i18next'

export default class extends Controller {
  static values = {
    nameKey: String
  }

  static targets = ['label', 'input']

  connect() {
    this.labelTarget.innerHTML = i18next.t(this.nameKeyValue)
  }
}
