(function(_ds){var window=this;var d9=class extends _ds.PN{constructor(){super(["devsite-dialog","devsite-dropdown-list","devsite-view-release-notes-dialog"]);this.Fr=!1;this.releaseNotes=new Map;this.dialog=null;this.path="";this.label="Release Notes";this.disableAutoOpen=!1}Na(){return this}async connectedCallback(){super.connectedCallback();try{this.path||(this.path=await _ds.Bu(_ds.C().href)),this.releaseNotes=await _ds.pz(this.path)}catch(a){}this.releaseNotes.size===0?this.remove():(this.Fr=!0,this.disableAutoOpen||location.hash!==
"#release__notes"||this.o())}disconnectedCallback(){super.disconnectedCallback();let a;(a=this.dialog)==null||a.remove();this.dialog=null}o(a){a&&(a.preventDefault(),a.stopPropagation());let b;(b=this.dialog)==null||b.remove();this.dialog=document.createElement("devsite-dialog");this.dialog.classList.add("devsite-view-release-notes-dialog-container");_ds.WM((0,_ds.N)`
      <devsite-view-release-notes-dialog
        .releaseNotes=${this.releaseNotes}>
      </devsite-view-release-notes-dialog>
    `,this.dialog);document.body.appendChild(this.dialog);this.dialog.open=!0;this.Ba({category:"Site-Wide Custom Events",action:"release notes: view note",label:`${this.path}`})}render(){if(!this.Fr)return delete this.dataset.shown,(0,_ds.N)``;this.dataset.shown="";return(0,_ds.N)`
      <button class="view-notes-button" @click="${this.o}">
        ${this.label}
      </button>
    `}};_ds.w([_ds.J(),_ds.x("design:type",Object)],d9.prototype,"Fr",void 0);_ds.w([_ds.I({type:String}),_ds.x("design:type",Object)],d9.prototype,"path",void 0);_ds.w([_ds.I({type:String}),_ds.x("design:type",Object)],d9.prototype,"label",void 0);_ds.w([_ds.I({type:Boolean,Aa:"disable-auto-open"}),_ds.x("design:type",Object)],d9.prototype,"disableAutoOpen",void 0);try{customElements.define("devsite-view-release-notes",d9)}catch(a){console.warn("devsite.app.customElement.DevsiteViewReleaseNotes",a)};})(_ds_www);
