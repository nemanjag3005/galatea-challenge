import '@polymer/polymer/polymer-element.js';
import '@vaadin/vaadin-grid/vaadin-grid.js';
const $_documentContainer = document.createElement('template');

$_documentContainer.innerHTML = `<dom-module id="planglobal-grid-theme" theme-for="vaadin-grid">
  <template>
    <style>
      :host {
        --row-color-1: #FFFFFF;
        --row-color-2: #FFFFFF;
        --row-color-3: #FFFFFF;
        --row-color-4: #FFFFFF;
        --row-color-5: #FFFFFF;
        --row-color-6: #FFFFFF;
        --row-color-7: #FFFFFF;
        --row-color-8: #FFFFFF;
        --row-color-9: #FFFFFF;
        --row-color-10: #FFFFFF;
        --row-color-11: #FFFFFF;
        --row-color-12: #FFFFFF;
        --row-color-13: #FFFFFF;
        --row-color-14: #FFFFFF;
        --row-color-15: #FFFFFF;
        --row-color-16: #FFFFFF;
        --row-color-17: #FFFFFF;
        --row-color-18: #FFFFFF;
        --row-color-19: #FFFFFF;
        --row-color-20: #FFFFFF;
        --row-color-21: #FFFFFF;
        --row-color-22: #FFFFFF;
        --row-color-23: #FFFFFF;
        --row-color-24: #FFFFFF;
        --row-color-25: #FFFFFF;
        --row-color-26: #FFFFFF;
        --row-color-27: #FFFFFF;
        --row-color-28: #FFFFFF;
        --row-color-29: #FFFFFF;
        --row-color-30: #FFFFFF;
      }
    
      [part~="cell"].rowColor-1 {
        background: var(--row-color-1);
      } 
      [part~="cell"].rowColor-2 {
        background: var(--row-color-2);
      }
      [part~="cell"].rowColor-3 {
        background: var(--row-color-3);
      }
      [part~="cell"].rowColor-4 {
        background: var(--row-color-4);
      }
      [part~="cell"].rowColor-5 {
        background: var(--row-color-5);
      }
      [part~="cell"].rowColor-6 {
        background: var(--row-color-6);
      }
      [part~="cell"].rowColor-7 {
        background: var(--row-color-7);
      }
      [part~="cell"].rowColor-8 {
        background: var(--row-color-8);
      }
      [part~="cell"].rowColor-9 {
        background: var(--row-color-9);
      }
      [part~="cell"].rowColor-10 {
        background: var(--row-color-10);
      }
      [part~="cell"].rowColor-11 {
        background: var(--row-color-11);
      }
      [part~="cell"].rowColor-12 {
        background: var(--row-color-12);
      }
      [part~="cell"].rowColor-13 {
        background: var(--row-color-13);
      }
      [part~="cell"].rowColor-14 {
        background: var(--row-color-14);
      }
      [part~="cell"].rowColor-15 {
        background: var(--row-color-15);
      }
      [part~="cell"].rowColor-16 {
        background: var(--row-color-16);
      }
      [part~="cell"].rowColor-17 {
        background: var(--row-color-17);
      }
      [part~="cell"].rowColor-18 {
        background: var(--row-color-18);
      }
      [part~="cell"].rowColor-19 {
        background: var(--row-color-19);
      }
      [part~="cell"].rowColor-20 {
        background: var(--row-color-20);
      }
      [part~="cell"].rowColor-21 {
        background: var(--row-color-21);
      }
      [part~="cell"].rowColor-22 {
        background: var(--row-color-22);
      }
      [part~="cell"].rowColor-23 {
        background: var(--row-color-23);
      }
      [part~="cell"].rowColor-24 {
        background: var(--row-color-24);
      }
      [part~="cell"].rowColor-25 {
        background: var(--row-color-25);
      }
      [part~="cell"].rowColor-26 {
        background: var(--row-color-26);
      }
      [part~="cell"].rowColor-27 {
        background: var(--row-color-27);
      }
      [part~="cell"].rowColor-28 {
        background: var(--row-color-28);
      }
      [part~="cell"].rowColor-29 {
        background: var(--row-color-29);
      }
      [part~="cell"].rowColor-30 {
        background: var(--row-color-30);
      }
      [part~="cell"].bold {
        font-weight: bold;
      }
    </style>
  </template>
  <script>
    class PlanglobalGrid extends Vaadin.GridElement {
          static set test(){
                console.warn('test jarek!');
            }
            
            // tt(){}
    }
  </script>
<!--  <script>-->
<!--// class StyledGrid extends Vaadin.GridElement {-->
//     static get template() {
//         let template = super.template;
//         const styleEl = document.createElement('style');
//         styleEl.setAttribute('include', 'styled-grid');
//         template.content.appendChild(styleEl);
//         return template;
//     }

// }
// window.customElements.define('styled-grid', StyledGrid);
<!--    </script>-->
</dom-module>`;


document.head.appendChild($_documentContainer.content);
//
// class StyledGrid extends Vaadin.GridElement {
//     static get template() {
//         let template = super.template;
//         const styleEl = document.createElement('style');
//         styleEl.setAttribute('include', 'styled-grid');
//         template.content.appendChild(styleEl);
//         return template;
//     }
// }
// window.customElements.define('styled-grid', StyledGrid);