(function(_ds){var window=this;var wqa=async function(a,b){const c=a.o;let d;const e=b.id!==((d=a.oa)==null?void 0:d.id);e&&(a.className=b.id,a.eventLabel=`devsite-callout-${b.id}`,a.o=new _ds.OQ(b.origin,a.eventLabel));a.oa=b;c&&e&&await _ds.NQ(c)},xqa=async function(a){a.eventHandler.listen(document.body,"devsite-before-page-change",()=>{a.hide()})},yqa=async function(a,b){let c;((c=a.callout)==null?0:c.Jf)&&a.callout.Jf(b);await a.hide();a.Ba({category:"Site-Wide Custom Events",action:"callout-dismiss",label:a.eventLabel})},
q0=async function(a,b){let c;((c=a.callout)==null?0:c.Me)&&a.callout.Me(b);let d;((d=a.callout)==null?0:d.uj)||await a.hide();a.Ba({category:"Site-Wide Custom Events",action:"callout-action",label:a.eventLabel})},zqa=function(a){let b,c;if(((b=a.callout)==null?0:b.Oe)&&`${(c=a.callout)==null?void 0:c.Oe}`){let d,e;return(0,_ds.N)`<div class="devsite-callout-branding">
          <img
            class="devsite-callout-branding-image"
            src="${(d=a.callout)==null?void 0:d.Oe}"
            alt="${((e=a.callout)==null?void 0:e.Ng)||""}" />
        </div>
        <hr />`}return(0,_ds.N)``},Aqa=function(a){let b,c;if(((b=a.callout)==null?0:b.hq)&&`${(c=a.callout)==null?void 0:c.hq}`){let d,e;return(0,_ds.N)`<div class="devsite-callout-hero">
        <img
          class="devsite-callout-hero-image"
          src="${(d=a.callout)==null?void 0:d.hq}"
          alt="${((e=a.callout)==null?void 0:e.jA)||""}" />
      </div>`}return(0,_ds.N)``},Bqa=function(a){let b;if((b=a.callout)==null?0:b.Kt)return(0,_ds.N)``;let c;return(0,_ds.N)` <div class="devsite-callout-header">
        <h2>${((c=a.callout)==null?void 0:c.title)||""}</h2>
      </div>`},Cqa=function(a){let b;if((b=a.callout)==null?0:b.loading)return(0,_ds.N)`<div class="devsite-callout-body"
        ><devsite-spinner size="24"></devsite-spinner
      ></div>`;let c,d;var e;if(((c=a.callout)==null?0:c.body)&&`${(d=a.callout)==null?void 0:d.body}`){{let f;if(((f=a.callout)==null?void 0:f.body)instanceof _ds.jg){let g;a=(0,_ds.N)`${(0,_ds.bQ)((g=a.callout)==null?void 0:g.body)}`}else a=(0,_ds.N)`${(e=a.callout)==null?void 0:e.body}`}e=(0,_ds.N)`<div class="devsite-callout-body">
        ${a}
      </div>`}else e=(0,_ds.N)``;return e},Dqa=function(a){var b;if((b=a.callout)==null||!b.Wc)return(0,_ds.N)``;var c;b=(0,_ds.ez)({button:!0,"button-primary":!0,"devsite-callout-action":!0,"button-disabled":((c=a.callout)==null?void 0:c.Ms)||!1});let d;c=(d=a.callout)==null?void 0:d.Vx;let e;if((e=a.callout)==null?0:e.Bk){let g,h;return(0,_ds.N)`<a
        @click=${k=>{q0(a,k)}}
        href="${((g=a.callout)==null?void 0:g.Bk)||""}"
        class="${b}"
        aria-label=${c!=null?c:_ds.OM}
        data-title=${c!=null?c:_ds.OM}>
        ${((h=a.callout)==null?void 0:h.Wc)||""}
      </a>`}let f;return(0,_ds.N)`<button
        @click=${g=>{q0(a,g)}}
        class="${b}"
        aria-label=${c!=null?c:_ds.OM}
        data-title=${c!=null?c:_ds.OM}>
        ${((f=a.callout)==null?void 0:f.Wc)||""}
      </button>`},r0=class extends _ds.PN{set callout(a){wqa(this,a)}get callout(){return this.oa}get open(){let a;return((a=this.ma.value)==null?void 0:a.open)||!1}constructor(){super(["devsite-spinner"]);this.eventHandler=new _ds.u;this.eventLabel="";this.oa=this.ea=this.o=null;this.ma=new _ds.YP}connectedCallback(){super.connectedCallback();xqa(this)}disconnectedCallback(){super.disconnectedCallback();let a;(a=this.o)==null||a.cancel()}Na(){return this}async ready(){await this.j}async show(){await this.ready();
if(!this.open){var a;await ((a=this.o)==null?void 0:a.schedule(()=>{document.activeElement instanceof HTMLElement&&(this.ea=document.activeElement);var b;(b=this.ma.value)==null||b.show();let c;(c=this.querySelector(".devsite-callout-action"))==null||c.focus();let d;b={message:(((d=this.callout)==null?void 0:d.title)||"")+" dialog opened"};document.body.dispatchEvent(new CustomEvent("devsite-a11y-announce",{detail:b}));this.Ba({category:"Site-Wide Custom Events",action:"callout-impression",label:this.eventLabel,
nonInteraction:!0})},()=>{let b;(b=this.ma.value)==null||b.close();let c;(c=this.querySelector(".devsite-callout-action"))==null||c.blur();this.ea&&this.ea.focus()}))}}async hide(){await this.ready();let a;await ((a=this.o)==null?void 0:_ds.NQ(a))}render(){if(!this.callout)return(0,_ds.N)``;let a;return(0,_ds.N)`
      <dialog
        closedby="none"
        ${(0,_ds.$P)(this.ma)}
        aria-label="${((a=this.callout)==null?void 0:a.title)||""}"
        class="devsite-callout">
        ${zqa(this)} ${Aqa(this)}
        ${Bqa(this)} ${Cqa(this)}
        <div class="devsite-callout-buttons">
          <button
            @click=${b=>{yqa(this,b)}}
            class="button button-dismiss devsite-callout-dismiss">
            ${"Dismiss"}
          </button>
          ${Dqa(this)}
        </div>
      </dialog>
    `}};_ds.w([_ds.I({Aa:!1}),_ds.x("design:type",Object),_ds.x("design:paramtypes",[Object])],r0.prototype,"callout",null);try{customElements.define("devsite-callout",r0)}catch(a){console.warn("Unrecognized DevSite custom element - DevsiteCallout",a)};})(_ds_www);
