(function(_ds){var window=this;var hva=async function(a){a.eventHandler.listen(a,"DropdownToggled",c=>{c=c.getBrowserEvent();let d;a.Ba({category:"devsiteLlmTools",action:((d=c.detail)==null?0:d.open)?"llm_tools_dropdown_open":"llm_tools_dropdown_close",label:"dropdown_toggle"})});a.eventHandler.listen(a,"DropdownItemClicked",c=>{c=c.getBrowserEvent();if(c=a.ea.get(c.detail.id))a.Ba({category:"devsiteLlmTools",action:c.Yx,label:c.analyticsLabel}),c.action()});const b=gva();b&&(a.o=b,a.Ba({category:"devsiteLlmTools",action:"llm_tools_button_impression"}))},
gva=function(){const a=_ds.C();a.pathname=`${a.pathname}.md.txt`;return _ds.ag(a.href)},iva=async function(a){if(!a.o)return null;a.Kl=!0;try{const b=await fetch(_ds.Do(a.o.toString()).href);if(b)return await b.text()}catch(b){}finally{a.Kl=!1}return null},jva=async function(a){try{return a.ma||(a.ma=await iva(a)),a.ma}catch(b){}return null},E4=function(a,b){a.dispatchEvent(new CustomEvent("devsite-show-custom-snackbar-msg",{detail:{msg:b,showClose:!1},bubbles:!0}))},kva=async function(a){a.Ba({category:"devsiteLlmTools",
action:"llm_tools_copy_markdown_click",label:"main_button"});const b=await jva(a);b?await _ds.az(b):E4(a,"Failed to copy page")},F4=class extends _ds.PN{constructor(){super(...arguments);this.Kl=!1;this.eventHandler=new _ds.u;this.ma=null;this.o=void 0;this.items=[{id:"open-markdown",title:"View as Markdown",action:()=>{this.o?_ds.mg(window,this.o,"_blank"):E4(this,"Failed to open markdown view.")},Yx:"llm_tools_open_markdown_click",analyticsLabel:"dropdown_item"}];this.oa=this.items.map(a=>({id:a.id,
title:a.title}));this.ea=new Map(this.items.map(a=>[a.id,a]))}Na(){return this}connectedCallback(){super.connectedCallback();hva(this)}disconnectedCallback(){super.disconnectedCallback();_ds.D(this.eventHandler)}render(){return(0,_ds.N)`
      <div
        class="devsite-llm-tools-container"
        role="group"
        aria-label="${"LLM Tools"}">
        <div class="devsite-llm-tools-button-container">
          <button
            type="button"
            class="button button-white devsite-llm-tools-button"
            ?disabled="${this.Kl}"
            @click=${()=>{kva(this)}}
            aria-label="${"Copy page as markdown"}"
            data-title="${"Copy Page"}">
            <span class="material-icons icon-copy" aria-hidden="true"></span>
          </button>
        </div>
        <div class="devsite-llm-tools-dropdown-container">
          <devsite-dropdown-list
            .listItems="${this.oa}"
            open-dropdown-aria-label="${"More LLM Tools options"}"
            close-dropdown-aria-label="${"Close LLM Tools options menu"}">
          </devsite-dropdown-list>
        </div>
      </div>
    `}};F4.prototype.disconnectedCallback=F4.prototype.disconnectedCallback;_ds.w([_ds.J(),_ds.x("design:type",Object)],F4.prototype,"Kl",void 0);try{customElements.define("devsite-llm-tools",F4)}catch(a){console.warn("Unrecognized DevSite custom element - DevsiteLlmTools",a)};})(_ds_www);
