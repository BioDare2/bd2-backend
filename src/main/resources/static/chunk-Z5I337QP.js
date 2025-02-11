import{a as H,b as g}from"./chunk-4VSHER3I.js";import{e as X}from"./chunk-KXLXGWID.js";import"./chunk-PVC36OFP.js";import{a as $}from"./chunk-EKG5OYEX.js";import"./chunk-TNPHV24S.js";import{a as B,g as v,h as L,j as Y,l as q,m as z,n as W}from"./chunk-RNTTYPTU.js";import"./chunk-7VADEMAN.js";import{c as G,d as K,e as Q,o as U}from"./chunk-SOK2PQSY.js";import{b as O,c as A}from"./chunk-MPP6ZGNJ.js";import"./chunk-AZ3WFU35.js";import"./chunk-3YROXDJ4.js";import"./chunk-R4QP53GN.js";import"./chunk-3O746AE3.js";import"./chunk-VHOBWLCH.js";import"./chunk-JUYLOIM6.js";import"./chunk-P35HZQU6.js";import{F as R,G as J,a as N}from"./chunk-EB5SMJLY.js";import{$ as f,C as E,Cb as P,Da as D,Db as T,Ea as S,Ga as V,Ja as u,Na as s,Rb as k,T as _,Tb as F,Wa as n,Wb as j,Xa as r,Ya as p,_ as h,ab as y,db as x,fb as l,lc as C,qb as a,ua as m,va as c,yb as I}from"./chunk-ZWLUK6NT.js";function re(t,b){if(t&1){let e=y();n(0,"div",8),a(1,"Please complete "),n(2,"a",9),x("click",function(){h(e);let o=l(2);return f(o.goToExpEdit(o.assay.id,"MeasurementSection"))}),a(3,"Measurement details"),r(),a(4," to get access to the secondary data "),r()}}function oe(t,b){if(t&1){let e=y();n(0,"div",10)(1,"label",11)(2,"a",12),x("click",function(){h(e);let o=l(2);return f(o.exportDataView())}),n(3,"i",13),a(4,"save_alt"),r(),n(5,"span",14),a(6,"Download"),r()(),a(7," current view "),r(),n(8,"label",11)(9,"a",15),x("click",function(){h(e);let o=l(2);return f(o.exportFullData())}),n(10,"i",13),a(11,"save_alt"),r(),n(12,"span",14),a(13,"Download"),r()(),a(14," full "),r()()}}function ne(t,b){if(t&1&&p(0,"bd2-ts-plots",16),t&2){let e=l(2);s("tracesPerPlot",e.tracesPerPlot)("data",e.timeseries)}}function ae(t,b){if(t&1){let e=y();n(0,"div")(1,"h3"),a(2,"Show timeseries"),r(),p(3,"hr"),n(4,"bd2-tsdisplay-params-rform",1),x("displayParams",function(o){h(e);let d=l();return f(d.displayChanged(o))}),r(),n(5,"mat-expansion-panel")(6,"mat-expansion-panel-header")(7,"mat-panel-title"),a(8," Sorting "),r()(),p(9,"bd2-tssort-params-rform",2),P(10,"async"),P(11,"async"),r(),u(12,re,5,0,"div",3),p(13,"hr"),n(14,"div",4),a(15," Hint: You can click on trace label box to remove it from the plot. "),r(),n(16,"div",5),u(17,oe,15,0,"div",6),r(),p(18,"hr"),u(19,ne,1,2,"bd2-ts-plots",7),r()}if(t&2){let e=l();m(4),s("disabled",e.disabledSecondary)("totalTraces",e.totalTraces)("currentPage",e.currentPage),m(5),s("ppaJobs",T(10,8,e.analysis.ppaJobs$))("rhythmJobs",T(11,10,e.analysis.rhythmJobs$)),m(3),s("ngIf",e.disabledSecondary),m(5),s("ngIf",e.timeseries),m(2),s("ngIf",e.timeseries)}}var ee=(()=>{class t extends A{constructor(e,i,o,d,w){super(w),this.analysis=e,this.fetcher=i,this.RDMSocial=o,this.analytics=d,this.timeseries=[],this.totalTraces=0,this.currentPage=B.firstPage(),this.tracesPerPlot=5,this.blocked=!1,this.disabledSecondary=!1,this.csvExporter=new H,e.allowedPPAMethods=["NLLS","MESA"],this.titlePart=" Data"}ngOnInit(){super.ngOnInit(),this.timeSeriesSubsripction=this.fetcher.seriesPackStream.pipe(E(1e3)).subscribe(e=>{if(!this.assay)return;let i=e.data;this.currentParams=e.params,this.timeseries=i,this.tracesPerPlot=Math.max(5,i.length/20),this.totalTraces=e.totalTraces,this.currentPage=e.currentPage,this.analytics.experimentDataView(this.assay.id)},e=>{console.log("Error in TS subscription: "+e),this.feedback.error(e)}),this.fetcher.error$.subscribe(e=>this.feedback.error(e))}ngOnDestroy(){this.timeSeriesSubsripction&&this.timeSeriesSubsripction.unsubscribe(),this.fetcher.ngOnDestroy(),super.ngOnDestroy()}displayChanged(e){this.fetcher.changeDisplayParams(e)}exportDataView(){let e=this.fetcher.current;this.exportSeriesPack(e)}exportFullData(){this.fetcher.getFullDataSet(this.assay,this.fetcher.current.params,this.fetcher.current.sorting).subscribe(e=>{this.exportSeriesPack(e,!0)})}exportSeriesPack(e,i=!1){if(!e)return;let o=this.csvExporter.renderCSVTable(e.data,e.params,e.currentPage,e.sorting,this.assay),d=new Blob([o],{type:"text/csv"}),w=i?".full":`.page${e.currentPage.pageIndex+1}`,ie={fileName:`${this.assay.id}_data.${e.params.detrending.name}${w}.csv`,extensions:[".csv"]};L(d,ie).then(M=>{this.recordExport()}).catch(M=>console.log("could not save export",M))}recordExport(){this.analytics.experimentDataExport(this.assay.id)}updateModel(e){super.updateModel(e),this.RDMSocial.canProceedByMeasurement(e).then(i=>{i?this.disabledSecondary=!1:this.disabledSecondary=!0}),this.fetcher.experiment(e),this.analysis.experiment(e)}static{this.\u0275fac=function(i){return new(i||t)(c(g),c(v),c($),c(N),c(O))}}static{this.\u0275cmp=D({type:t,selectors:[["ng-component"]],standalone:!1,features:[I([v,g]),V],decls:1,vars:1,consts:[[4,"ngIf"],[3,"displayParams","disabled","totalTraces","currentPage"],[3,"ppaJobs","rhythmJobs"],["type","danger","class","alert alert-danger","role","alert",4,"ngIf"],["type","info","role","alert","dismissible","true","dismissOnTimeout","20000",1,"alert","alert-info"],[1,"clearfix"],["class","float-right",4,"ngIf"],[3,"tracesPerPlot","data",4,"ngIf"],["type","danger","role","alert",1,"alert","alert-danger"],[3,"click"],[1,"float-right"],[1,"mr-4"],["download","","role","button","aria-label","download",1,"btn","btn-primary",2,"color","white",3,"click"],[1,"material-icons","bd-icon"],[1,"cdk-visually-hidden"],["download","","role","button","aria-label","download whole",1,"btn","btn-primary",2,"color","white",3,"click"],[3,"tracesPerPlot","data"]],template:function(i,o){i&1&&u(0,ae,20,12,"div",0),i&2&&s("ngIf",o.assay)},dependencies:[k,Y,q,z,G,K,Q,F],encapsulation:2})}}return t})();var se=[{path:"",children:[{path:"view/ts",component:ee},{path:"view/heatmap",loadChildren:()=>import("./chunk-WKUKZWFC.js").then(t=>t.TsHeatmapModule)},{path:"ts-old-import",loadChildren:()=>import("./chunk-6KHKNJPT.js").then(t=>t.TsOldImportModule)},{path:"ts-import",loadChildren:()=>import("./chunk-7SWW4CM3.js").then(t=>t.TsImportModule)}]}],te=(()=>{class t{static{this.\u0275fac=function(i){return new(i||t)}}static{this.\u0275mod=S({type:t})}static{this.\u0275inj=_({imports:[C.forChild(se),C]})}}return t})();var Oe=(()=>{class t{static{this.\u0275fac=function(i){return new(i||t)}}static{this.\u0275mod=S({type:t})}static{this.\u0275inj=_({imports:[j,R,J,X,W,te,U]})}}return t})();export{Oe as TsDataModule};
