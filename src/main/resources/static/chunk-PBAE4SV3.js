import{k as ze}from"./chunk-RNCA3VSA.js";import{a as he}from"./chunk-I53LRGHG.js";import{a as $e}from"./chunk-JESAVJXP.js";import{B as qe,d as k,e as Pe,f as Ee,g as Me,h as Te,i as Fe,j as De,k as ke,l as Ae,m as Ve}from"./chunk-D224IPLR.js";import{j as N}from"./chunk-VDA2LVPM.js";import{i as Oe,j as Le}from"./chunk-PGRVX3MW.js";import{b as j,d as Ce}from"./chunk-6KRAEQXB.js";import{A as _e,D as ye,E as Ie,F as we,b as de,d as fe,f as K,h as ge,i as Se,p as be,q as xe,s as ve}from"./chunk-6XVRX2RL.js";import{B as X,C as L,D as G,G as Y,Ga as b,Ia as l,Kb as ae,La as ie,Lb as M,M as Z,Nb as ce,O as w,Ob as pe,Q as ee,Qb as le,R as te,Ra as s,S as A,Sa as o,Ta as C,U as Q,Xa as P,Y as v,Z as V,_a as u,aa as f,ab as E,ac as me,ba as g,dc as B,fc as J,i as U,ib as re,j as S,ja as H,jb as a,kb as ne,kc as ue,lb as $,o as O,ra as p,rb as z,s as y,sa as _,t as x,tb as q,ub as se,vb as oe,wb as F,xb as D,y as W,z as I}from"./chunk-BWZCNJMV.js";var Be=(()=>{let r=class r{constructor(e=!1){this.removeDebounce=e,this.error$=new U,this.isFetching$=new S(!1),this.isProcessing$=new S(!1),this.dataLength=0,this.page$=new S(void 0),this.sort$=new S(void 0),this.on$=new S(!1),this.refresh$=new U,this.input$=new S(void 0),this.params$=new S(void 0),this.asset$=new S(void 0),this.DEBUG=!1,this.dataInputsDebounce=10,this.assetInputsDebounce=200,this.busyDebounce=50,this.id=r.ids++,this.logMe("created"),this.initAssetsStream(),this.isBusy$=this.initBusyStream(),this.data$=this.initDataStream()}logMe(e,t){this.DEBUG&&(e=this.constructor.name+":"+this.id+" "+e,t?console.log(e,t):console.log(e))}input(e){e&&(this.input$.next(e),this.resetPage())}params(e){this.params$.next(e)}on(e=!0){this.on$.next(e)}refresh(){this.refresh$.next(!0)}page(e){this.page$.next(e)}sort(e){this.sort$.next(e),this.resetPage()}close(){this.asset$.complete(),this.error$.complete(),this.isFetching$.complete(),this.isProcessing$.complete(),this.input$.complete(),this.params$.complete(),this.on$.complete(),this.refresh$.complete(),this.page$.complete(),this.sort$.complete()}errorToData(e){return O(void 0)}assetToSort(e){}assetToPage(e){}resetPage(){this.page$.pipe(G(1)).subscribe(e=>{if(e){let t=new k;t.pageIndex=0,t.pageSize=e.pageSize,this.page(t)}})}initBusyStream(){return this.removeDebounce?x([this.isFetching$,this.isProcessing$]).pipe(y(([e,t])=>e||t)):x([this.isFetching$,this.isProcessing$]).pipe(L(this.busyDebounce),y(([e,t])=>e||t))}initDataStream(){let e=this.dataMutators(),t;return this.removeDebounce?t=x(e):t=x(e).pipe(L(this.dataInputsDebounce)),t.pipe(w(i=>this.logMe("Input for data stream",i)),w(i=>this.isProcessing$.next(!0)),y(([i,c])=>{let m=this.processData(i,c);return this.currentParams=c,this.currentData=m,m}),X(i=>(this.error$.next(i),this.errorToData(i))),w(i=>this.isProcessing$.next(!1)))}dataMutators(){return[this.asset$.pipe(I(e=>!!e)),this.params$]}assetsMutators(){return[this.initAssetsInput(),this.sort$,this.page$.pipe(I(t=>!!t))]}initAssetsStream(){let e=this.assetsMutators(),t;this.removeDebounce?t=x(e):t=x(e).pipe(L(this.assetInputsDebounce)),t.pipe(w(i=>this.logMe("Input for asset stream",i))).subscribe(([i,c,m])=>this.loadAsset(i,c,m),i=>this.error$.next(i))}loadAsset(e,t,i){this.isFetching$.next(!0),this.fetchAsset(e,t,i).subscribe(c=>{this.isFetching$.next(!1),this.setAsset(c,e,t,i)},c=>{this.isFetching$.next(!1),this.error$.next(c)})}setAsset(e,t,i,c){this.currentInput=t,this.currentAsset=e,this.currentSort=this.assetToSort(e)||i,this.currentPage=this.assetToPage(e)||c,this.dataLength=this.assetToDataLength(e),this.asset$.next(e)}initAssetsInput(){let t=x([this.input$,this.on$]).pipe(I(([m,d])=>m&&d),y(([m,d])=>m)).pipe(Y((m,d)=>this.sameInput(m,d))),i=this.refresh$.pipe(Z(m=>t.pipe(G(1))));return W(t,i).pipe(w(m=>this.logMe("Assets input",m)))}};r.ids=0;let n=r;return n})();var R=(()=>{let r=class r extends Be{constructor(e,t=!1){super(t),this.experimentService=e,this.experiment$=this.data$}fetchAsset(e,t,i){let c=(e.query||"").trim();return c===""?this.experimentService.getExperiments(e.showPublic,t,i):this.experimentService.searchExperiments(c,e.showPublic,t,i)}processData(e,t){return e.data}sameInput(e,t){return e.showPublic===t.showPublic&&e.query===t.query}assetToDataLength(e){return e.currentPage.length}assetToPage(e){return e.currentPage}errorToData(e){return O([])}};r.\u0275fac=function(t){return new(t||r)(Q(N),Q($e,8))},r.\u0275prov=te({token:r,factory:r.\u0275fac});let n=r;return n})();var We=n=>["/experiment",n];function Xe(n,r){n&1&&(s(0,"i",5),a(1,"lock_open"),o())}var Ne=(()=>{let r=class r{constructor(){}ngOnInit(){}};r.\u0275fac=function(t){return new(t||r)},r.\u0275cmp=v({type:r,selectors:[["bd2-experiment-summary"]],inputs:{exp:"exp"},decls:24,vars:14,consts:[[1,"list-group-item","list-group-item-action",3,"routerLink"],[1,"list-group-item-heading"],["class","material-icons bd-icon","style","color: green",4,"ngIf"],[1,"list-group-item-text"],[1,"mb-1"],[1,"material-icons","bd-icon",2,"color","green"]],template:function(t,i){t&1&&(s(0,"a",0)(1,"h5",1),b(2,Xe,2,0,"i",2),a(3),o(),s(4,"div",3)(5,"div",4),a(6),o(),s(7,"div")(8,"strong"),a(9,"Authors:"),o(),a(10),o(),s(11,"div")(12,"small")(13,"strong"),a(14,"Id:"),o(),a(15),s(16,"strong"),a(17,"Executed:"),o(),a(18),F(19,"date"),s(20,"strong"),a(21,"Modified:"),o(),a(22),F(23,"date"),o()()()()),t&2&&(l("routerLink",se(12,We,i.exp.id)),p(2),l("ngIf",i.exp.features.isOpenAccess),p(),$(" ",i.exp.name," "),p(3),ne(i.exp.generalDesc.purpose),p(4),$(" ",i.exp.authors,""),p(5),$(" ",i.exp.id," "),p(3),$(" ",D(19,8,i.exp.generalDesc.executionDate.date)," "),p(4),$(" ",D(23,10,i.exp.provenance.modified.date)," "))},dependencies:[M,B,pe],encapsulation:2});let n=r;return n})();var Re=(()=>{let r=class r{set options(e){e&&(this.currentQuery=e.query,this.currentShowPublic=e.showPublic,this.currentSort=e.sorting)}constructor(e){this.fb=e,this.sortOrderClass="",this.search=new H,this.sort=new H,this.currentSort={active:"modified",direction:"desc"},this.currentQuery="",this.currentShowPublic=!1}ngOnInit(){this.sortOptionsF=this.fb.group({sorting:[this.currentSort.active],direction:[this.currentSort.direction]}),this.showPublicF=this.fb.control(this.currentShowPublic),this.queryF=this.fb.control(this.currentQuery,[K.required,K.minLength(3)]),this.sortOptionsF.valueChanges.subscribe(e=>this.updateSort(e.sorting,this.currentSort.direction)),this.showPublicF.valueChanges.subscribe(e=>{this.currentShowPublic=e,this.emitSearch()})}updateSort(e,t){console.log("UpdateSort",{active:e,direction:t,class:this.sortOrderClass});let i={active:e,direction:t};this.currentSort=i,this.sort.next(i)}emitSearch(){let e={showPublic:this.currentShowPublic,query:this.currentQuery};this.search.next(e)}changeDirection(){let e=this.currentSort.direction=="asc"?"desc":"asc";this.sortOrderClass=e=="asc"?"icon-flipped-h":null,this.updateSort(this.currentSort.active,e)}find(){this.queryF.valid&&(this.currentQuery=this.queryF.value,this.emitSearch())}all(){this.queryF.setValue(""),this.currentQuery="",this.emitSearch()}};r.\u0275fac=function(t){return new(t||r)(_(ye))},r.\u0275cmp=v({type:r,selectors:[["bd2-search-and-sort-panel"]],inputs:{options:"options"},outputs:{search:"search",sort:"sort"},decls:27,vars:6,consts:[[1,"pb-1","pt-1","searchpanel"],[1,"float-left",3,"formGroup"],["mat-mini-fab","","aria-label","sort direction",1,"mr-1",2,"vertical-align","bottom",3,"click"],["name","sorting","aria-label","sorting","formControlName","sorting",1,"mr-4"],["value","id"],["value","name"],["value","author"],["value","executed"],["value","modified"],[1,"mr-2","mt-2",3,"formControl"],[1,"no-clues","float-right"],["name","search","minlength","3","placeholder","e.g. prr9 temp*",1,"form-control-sm","mr-1",3,"keydown.enter","formControl"],["mat-mini-fab","","aria-label","search",1,"mr-2",3,"click","disabled"],["mat-mini-fab","","color","primary","aria-label","all",2,"vertical-align","bottom",3,"click"],[1,"clearfix"]],template:function(t,i){t&1&&(s(0,"div",0)(1,"small")(2,"div",1)(3,"button",2),u("click",function(){return i.changeDirection()}),s(4,"mat-icon"),a(5,"sort"),o()(),s(6,"mat-button-toggle-group",3)(7,"mat-button-toggle",4),a(8,"Id"),o(),s(9,"mat-button-toggle",5),a(10,"Title"),o(),s(11,"mat-button-toggle",6),a(12,"Author"),o(),s(13,"mat-button-toggle",7),a(14,"Exec."),o(),s(15,"mat-button-toggle",8),a(16,"Mod."),o()()(),s(17,"mat-slide-toggle",9),a(18,"Show public"),o(),s(19,"div",10)(20,"input",11),u("keydown.enter",function(){return i.find()}),o(),s(21,"button",12),u("click",function(){return i.find()}),s(22,"mat-icon"),a(23,"search"),o()(),s(24,"button",13),u("click",function(){return i.all()}),a(25,"all"),o()()(),C(26,"div",14),o()),t&2&&(p(2),l("formGroup",i.sortOptionsF),p(2),ie(i.sortOrderClass),p(13),l("formControl",i.showPublicF),p(3),l("formControl",i.queryF),p(),l("disabled",i.queryF.invalid))},dependencies:[fe,ge,Se,_e,be,xe,ve,j,Fe,De,Oe,Ae],encapsulation:2});let n=r;return n})();var Ue=()=>[10,25,50,100,200],tt=()=>[],it=(n,r)=>({pageIndex:n,pageSize:r});function rt(n,r){n&1&&(s(0,"div",14),a(1," There are no visible experiments matching the criteria. "),o())}function nt(n,r){n&1&&C(0,"mat-progress-bar",15)}function st(n,r){if(n&1&&C(0,"bd2-experiment-summary",16),n&2){let h=r.$implicit;l("exp",h)}}function ot(n,r){if(n&1&&(s(0,"a",17),a(1,"Next"),o()),n&2){let h=E();l("routerLink",q(2,tt))("queryParams",oe(3,it,(h.fetcher.currentPage==null?null:h.fetcher.currentPage.pageIndex)+1,h.fetcher.currentPage==null?null:h.fetcher.currentPage.pageSize))}}var Ge=(()=>{let r=class r{constructor(e,t,i,c,m){this.experimentService=e,this.fetcher=t,this.feedback=i,this.userService=c,this.route=m}ngOnInit(){let e=this.firstPage();this.initialSearchOptions={sorting:{active:"modified",direction:"desc"},showPublic:!this.userService.isLoggedIn(),query:""},this.subscribeRoute(),this.fetcher.experiment$.subscribe(t=>this.experiments=t),this.fetcher.error$.subscribe(t=>this.feedback.error(t)),this.fetcher.on(!0),this.sort(this.initialSearchOptions.sorting),this.search(this.initialSearchOptions),this.page(this.firstPage())}ngOnDestroy(){this.routeSubscription&&this.routeSubscription.unsubscribe(),this.fetcher&&this.fetcher.close()}search(e){console.log("L Searching for",e),this.fetcher.input(e)}sort(e){console.log("L Sort",e),this.fetcher.sort(e)}page(e){this.fetcher.page(e)}firstPage(){let e=+this.route.snapshot.queryParamMap.get("pageIndex")||0,t=+this.route.snapshot.queryParamMap.get("pageSize")||25,i=new k;return i.pageIndex=e,i.pageSize=t,i}subscribeRoute(){this.routeSubscription=this.route.queryParamMap.pipe(I(e=>e.has("pageIndex")&&e.has("pageSize")),y(e=>{let t=new k;return t.pageSize=+e.get("pageSize"),t.pageIndex=+e.get("pageIndex"),t})).subscribe(e=>this.page(e))}refresh(){this.fetcher.refresh()}};r.\u0275fac=function(t){return new(t||r)(_(N),_(R),_(ue),_(he),_(me))},r.\u0275cmp=v({type:r,selectors:[["ng-component"]],features:[z([R])],decls:20,vars:18,consts:[["paginator",""],["paginator2",""],[1,"float-left"],["role","button","aria-label","refresh",3,"click"],[1,"material-icons","bd-icon-inh","bd-primary",2,"font-size","larger"],[1,"clearfix"],["class","alert alert-info",4,"ngIf"],[3,"search","sort","options"],[3,"page","length","pageSize","pageIndex","pageSizeOptions"],["mode","indeterminate",4,"ngIf"],[1,"list-group"],[3,"exp",4,"ngFor","ngForOf"],[3,"page","hidden","length","pageSize","pageIndex","pageSizeOptions"],["style","color: rgba(0,0,0,.54);",3,"routerLink","queryParams",4,"ngIf"],[1,"alert","alert-info"],["mode","indeterminate"],[3,"exp"],[2,"color","rgba(0,0,0,.54)",3,"routerLink","queryParams"]],template:function(t,i){if(t&1){let c=P();s(0,"div")(1,"h2",2),a(2,"Experiments "),s(3,"a",3),u("click",function(){return f(c),g(i.refresh())}),s(4,"i",4),a(5,"refresh"),o()()(),C(6,"div",5),b(7,rt,2,0,"div",6),s(8,"div")(9,"bd2-search-and-sort-panel",7),u("search",function(d){return f(c),g(i.search(d))})("sort",function(d){return f(c),g(i.sort(d))}),o(),s(10,"mat-paginator",8,0),u("page",function(d){return f(c),g(i.page(d))}),o(),b(12,nt,1,0,"mat-progress-bar",9),F(13,"async"),s(14,"div",10),b(15,st,1,1,"bd2-experiment-summary",11),o(),s(16,"mat-paginator",12,1),u("page",function(d){return f(c),g(i.page(d))}),o(),s(18,"div",2),b(19,ot,2,6,"a",13),o()()()}if(t&2){let c=re(11);p(7),l("ngIf",!i.experiments||i.experiments.length<1),p(2),l("options",i.initialSearchOptions),p(),l("length",i.fetcher.dataLength)("pageSize",(i.fetcher.currentPage==null?null:i.fetcher.currentPage.pageSize)||25)("pageIndex",i.fetcher.currentPage==null?null:i.fetcher.currentPage.pageIndex)("pageSizeOptions",q(16,Ue)),p(2),l("ngIf",D(13,14,i.fetcher.isBusy$)),p(3),l("ngForOf",i.experiments),p(),l("hidden",(i.experiments==null?null:i.experiments.length)<1)("length",i.fetcher.dataLength)("pageSize",(i.fetcher.currentPage==null?null:i.fetcher.currentPage.pageSize)||25)("pageIndex",i.fetcher.currentPage==null?null:i.fetcher.currentPage.pageIndex)("pageSizeOptions",q(17,Ue)),p(3),l("ngIf",c.hasNextPage())}},dependencies:[ae,M,Pe,Me,B,Ne,Re,ce],encapsulation:2});let n=r;return n})();var at=[{path:"",children:[{path:"",component:Ge,pathMatch:"full"}]}],Qe=(()=>{let r=class r{};r.\u0275fac=function(t){return new(t||r)},r.\u0275mod=V({type:r}),r.\u0275inj=A({imports:[J.forChild(at),J]});let n=r;return n})();function ct(n,r){if(n&1){let h=P();s(0,"i",3),u("click",function(){f(h);let t=E();return g(t.select("desc"))}),a(1,"sort"),o()}}function pt(n,r){if(n&1){let h=P();s(0,"i",4),u("click",function(){f(h);let t=E();return g(t.select(""))}),a(1,"sort"),o()}}function lt(n,r){if(n&1){let h=P();s(0,"i",4),u("click",function(){f(h);let t=E();return g(t.select("asc"))}),a(1,"more_horiz"),o()}}var mt={provide:de,useExisting:ee(()=>ut),multi:!0},ut=(()=>{let r=class r{constructor(){this.value="",this.disabled=!1,this.onChange=e=>{},this.onTouched=()=>{}}ngOnInit(){}select(e){this.disabled||(this.value=e,this.onChange(this.value),this.onTouched())}registerOnChange(e){this.onChange=e}registerOnTouched(e){this.onTouched=e}writeValue(e){this.value=e}setDisabledState(e){this.disabled=e}};r.\u0275fac=function(t){return new(t||r)},r.\u0275cmp=v({type:r,selectors:[["bd2-sort-switch"]],features:[z([mt])],decls:4,vars:4,consts:[["mat-mini-fab","","aria-label","sorting",3,"disabled"],["class","material-icons  icon-flipped-h",3,"click",4,"ngIf"],["class","material-icons",3,"click",4,"ngIf"],[1,"material-icons","icon-flipped-h",3,"click"],[1,"material-icons",3,"click"]],template:function(t,i){t&1&&(s(0,"button",0),b(1,ct,2,0,"i",1)(2,pt,2,0,"i",2)(3,lt,2,0,"i",2),o()),t&2&&(l("disabled",i.disabled),p(),l("ngIf",i.value==="asc"),p(),l("ngIf",i.value==="desc"),p(),l("ngIf",i.value===""))},dependencies:[M,j],encapsulation:2});let n=r;return n})();var pi=(()=>{let r=class r{};r.\u0275fac=function(t){return new(t||r)},r.\u0275mod=V({type:r}),r.\u0275inj=A({imports:[le,Ie,we,Ce,ke,Le,Ve,Ee,qe,Te,ze,Qe]});let n=r;return n})();export{pi as ExperimentsModule};
