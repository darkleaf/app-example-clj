import { Controller } from '@hotwired/stimulus'

export default class extends Controller {
  connect() {
    this.element.setAttribute('novalidate', '')
  }

  validate(event) {
    if (!this.element.checkValidity()) {
      event.preventDefault()
      event.stopPropagation()
    }

    this.element.classList.add('was-validated')
  }
}
