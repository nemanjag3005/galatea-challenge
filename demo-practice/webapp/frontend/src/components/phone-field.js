import {html} from '@polymer/polymer/lib/utils/html-tag.js';
import {TextFieldElement} from
        '@vaadin/vaadin-text-field/src/vaadin-text-field.js';

class PhoneFieldElement extends TextFieldElement {

    constructor() {
        super();
    }

    ready() {
        super.ready();
        let element = this.shadowRoot.querySelector("input");

        console.log(element);
        var phoneMask = IMask(element, {
            mask: '+0 (000) 000-0000',
            lazy: false,
            overwrite: true,
            autofix: true,
            placeholderChar: 'X'
        });

        element.addEventListener('beforeinput', function() {

            console.log("Input beforeinput to \"" + this.value + "\"");
            console.log("Input beforeinput caret pos \"" + this.selectionStart + "\"");
            console.log("Input beforeinput value len \"" + this.value.length + "\"");
            if(this.value.length == this.selectionStart) {
                var firstPlaceholder = this.value.indexOf("X");
                if(firstPlaceholder >= 0)
                    this.setSelectionRange(firstPlaceholder, firstPlaceholder);
            }
            console.log("Input beforeinput caret pos after \"" + this.selectionStart + "\"");
        }, false);

        element.addEventListener('keydown', function(event) {
            const key = event.key;
            if (key === "Backspace" || key === "Delete") {
                console.log("Input keydown \"" + key + "\"");

                if(this.value.length == this.selectionStart) {
                    console.log("Input keydown start match ");
                    console.log("Input keydown value \"" + this.value + "\"");

                    var regex = /\d/g;
                    var matches = [];
                    var match;
                    while (match = regex.exec(this.value)) {
                        matches.push(match);
                    }

                    console.log(matches);
                    if(matches!=null && matches.length > 0) {
                        console.log("Input keydown match is not null");

                        var firstPlaceholder = matches[matches.length - 1].index + 1;
                        if (firstPlaceholder >= 0) {
                            this.setSelectionRange(firstPlaceholder, firstPlaceholder);
                            console.log("Input keydown caret pos after \"" + this.selectionStart + "\"");
                        }
                    }
                }
            }
        });
    }

    static get is() {
        return 'phone-field';
    }

}

window.customElements.define(PhoneFieldElement.is, PhoneFieldElement);