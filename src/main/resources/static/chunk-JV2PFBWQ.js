import{a as L,b as g}from"./chunk-IBBJR3ZR.js";import{e as Z}from"./chunk-M7X7JQUQ.js";import"./chunk-5ROL4OYM.js";import{a as A}from"./chunk-UZVDE5XY.js";import"./chunk-2KOYKWRC.js";import{a as H,g as b,h as Y,j as q,l as z,m as G,n as X}from"./chunk-MI2O5RFT.js";import"./chunk-JESAVJXP.js";import{c as K,d as Q,e as U,o as W}from"./chunk-THGCCJUJ.js";import{b as R,c as B}from"./chunk-5VP5V2NG.js";import"./chunk-ZUXWEGFT.js";import"./chunk-URHLGKLM.js";import"./chunk-FDKJ75ZE.js";import"./chunk-D224IPLR.js";import"./chunk-VDA2LVPM.js";import"./chunk-PGRVX3MW.js";import"./chunk-6KRAEQXB.js";import{E as O,F as $,a as J}from"./chunk-6XVRX2RL.js";import{C as V,Da as I,Ga as x,Ia as l,Lb as F,Nb as j,Qb as N,Ra as a,S as y,Sa as o,Ta as h,Xa as v,Y as D,Z as _,_a as S,aa as f,ab as c,ba as u,fc as E,jb as s,ra as p,rb as k,sa as d,wb as T,xb as C}from"./chunk-BWZCNJMV.js";function ne(t,i){if(t&1){let n=v();a(0,"div",8),s(1,"Please complete "),a(2,"a",9),S("click",function(){f(n);let r=c(2);return u(r.goToExpEdit(r.assay.id,"MeasurementSection"))}),s(3,"Measurement details"),o(),s(4," to get access to the secondary data "),o()}}function oe(t,i){if(t&1){let n=v();a(0,"div",10)(1,"label",11)(2,"a",12),S("click",function(){f(n);let r=c(2);return u(r.exportDataView())}),a(3,"i",13),s(4,"save_alt"),o(),a(5,"span",14),s(6,"Download"),o()(),s(7," current view "),o(),a(8,"label",11)(9,"a",15),S("click",function(){f(n);let r=c(2);return u(r.exportFullData())}),a(10,"i",13),s(11,"save_alt"),o(),a(12,"span",14),s(13,"Download"),o()(),s(14," full "),o()()}}function ae(t,i){if(t&1&&h(0,"bd2-ts-plots",16),t&2){let n=c(2);l("tracesPerPlot",n.tracesPerPlot)("data",n.timeseries)}}function se(t,i){if(t&1){let n=v();a(0,"div")(1,"h3"),s(2,"Show timeseries"),o(),h(3,"hr"),a(4,"bd2-tsdisplay-params-rform",1),S("displayParams",function(r){f(n);let m=c();return u(m.displayChanged(r))}),o(),a(5,"mat-expansion-panel")(6,"mat-expansion-panel-header")(7,"mat-panel-title"),s(8," Sorting "),o()(),h(9,"bd2-tssort-params-rform",2),T(10,"async"),T(11,"async"),o(),x(12,ne,5,0,"div",3),h(13,"hr"),a(14,"div",4),s(15," Hint: You can click on trace label box to remove it from the plot. "),o(),a(16,"div",5),x(17,oe,15,0,"div",6),o(),h(18,"hr"),x(19,ae,1,2,"bd2-ts-plots",7),o()}if(t&2){let n=c();p(4),l("disabled",n.disabledSecondary)("totalTraces",n.totalTraces)("currentPage",n.currentPage),p(5),l("ppaJobs",C(10,8,n.analysis.ppaJobs$))("rhythmJobs",C(11,10,n.analysis.rhythmJobs$)),p(3),l("ngIf",n.disabledSecondary),p(5),l("ngIf",n.timeseries),p(2),l("ngIf",n.timeseries)}}var te=(()=>{let i=class i extends B{constructor(e,r,m,P,w){super(w),this.analysis=e,this.fetcher=r,this.RDMSocial=m,this.analytics=P,this.timeseries=[],this.totalTraces=0,this.currentPage=H.firstPage(),this.tracesPerPlot=5,this.blocked=!1,this.disabledSecondary=!1,this.csvExporter=new L,e.allowedPPAMethods=["NLLS","MESA"],this.titlePart=" Data"}ngOnInit(){super.ngOnInit(),this.timeSeriesSubsripction=this.fetcher.seriesPackStream.pipe(V(1e3)).subscribe(e=>{if(!this.assay)return;let r=e.data;this.currentParams=e.params,this.timeseries=r,this.tracesPerPlot=Math.max(5,r.length/20),this.totalTraces=e.totalTraces,this.currentPage=e.currentPage,this.analytics.experimentDataView(this.assay.id)},e=>{console.log("Error in TS subscription: "+e),this.feedback.error(e)}),this.fetcher.error$.subscribe(e=>this.feedback.error(e))}ngOnDestroy(){this.timeSeriesSubsripction&&this.timeSeriesSubsripction.unsubscribe(),this.fetcher.ngOnDestroy(),super.ngOnDestroy()}displayChanged(e){this.fetcher.changeDisplayParams(e)}exportDataView(){let e=this.fetcher.current;this.exportSeriesPack(e)}exportFullData(){this.fetcher.getFullDataSet(this.assay,this.fetcher.current.params,this.fetcher.current.sorting).subscribe(e=>{this.exportSeriesPack(e,!0)})}exportSeriesPack(e,r=!1){if(!e)return;let m=this.csvExporter.renderCSVTable(e.data,e.params,e.currentPage,e.sorting,this.assay),P=new Blob([m],{type:"text/csv"}),w=r?".full":`.page${e.currentPage.pageIndex+1}`,re={fileName:`${this.assay.id}_data.${e.params.detrending.name}${w}.csv`,extensions:[".csv"]};Y(P,re).then(M=>{this.recordExport()}).catch(M=>console.log("could not save export",M))}recordExport(){this.analytics.experimentDataExport(this.assay.id)}updateModel(e){super.updateModel(e),this.RDMSocial.canProceedByMeasurement(e).then(r=>{r?this.disabledSecondary=!1:this.disabledSecondary=!0}),this.fetcher.experiment(e),this.analysis.experiment(e)}};i.\u0275fac=function(r){return new(r||i)(d(g),d(b),d(A),d(J),d(R))},i.\u0275cmp=D({type:i,selectors:[["ng-component"]],features:[k([b,g]),I],decls:1,vars:1,consts:[[4,"ngIf"],[3,"displayParams","disabled","totalTraces","currentPage"],[3,"ppaJobs","rhythmJobs"],["type","danger","class","alert alert-danger","role","alert",4,"ngIf"],["type","info","role","alert","dismissible","true","dismissOnTimeout","20000",1,"alert","alert-info"],[1,"clearfix"],["class","float-right",4,"ngIf"],[3,"tracesPerPlot","data",4,"ngIf"],["type","danger","role","alert",1,"alert","alert-danger"],[3,"click"],[1,"float-right"],[1,"mr-4"],["download","","role","button","aria-label","download",1,"btn","btn-primary",2,"color","white",3,"click"],[1,"material-icons","bd-icon"],[1,"cdk-visually-hidden"],["download","","role","button","aria-label","download whole",1,"btn","btn-primary",2,"color","white",3,"click"],[3,"tracesPerPlot","data"]],template:function(r,m){r&1&&x(0,se,20,12,"div",0),r&2&&l("ngIf",m.assay)},dependencies:[F,q,z,G,K,Q,U,j],encapsulation:2});let t=i;return t})();var le=[{path:"",children:[{path:"view/ts",component:te},{path:"view/heatmap",loadChildren:()=>import("./chunk-HS3DVCRS.js").then(t=>t.TsHeatmapModule)},{path:"ts-old-import",loadChildren:()=>import("./chunk-6N75L26D.js").then(t=>t.TsOldImportModule)},{path:"ts-import",loadChildren:()=>import("./chunk-P64J3JD7.js").then(t=>t.TsImportModule)}]}],ie=(()=>{let i=class i{};i.\u0275fac=function(r){return new(r||i)},i.\u0275mod=_({type:i}),i.\u0275inj=y({imports:[E.forChild(le),E]});let t=i;return t})();var Re=(()=>{let i=class i{};i.\u0275fac=function(r){return new(r||i)},i.\u0275mod=_({type:i}),i.\u0275inj=y({imports:[N,O,$,Z,X,ie,W]});let t=i;return t})();export{Re as TsDataModule};
