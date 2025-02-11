import{S as o,V as p,pc as u,s as n}from"./chunk-ZWLUK6NT.js";var s=class t{static{this.NLLS=new t("NLLS","FFT NLLS")}static{this.MFourFit=new t("MFourFit","MFourFit")}static{this.MESA=new t("MESA","MESA")}static{this.EPR=new t("EPR","ER Periodogram")}static{this.EPR_ND=new t("EPR_ND","ER Periodogram (no DT)")}static{this.LSPR=new t("LSPR","LS Periodogram")}static{this.SR=new t("SR","Spectrum Resampling")}static{this.SR_LD=new t("SR_LD","Spectrum Resampling (lin DT)")}static{this.FAKE=new t("FAKE","Fake method")}static{this.values=[t.NLLS,t.MFourFit,t.MESA,t.EPR,t.EPR_ND,t.LSPR,t.SR,t.SR_LD,t.FAKE]}constructor(r,e){this._name=r,this._label=e}get name(){return this._name}get label(){return this._label}static get(r){return t.getValuesMap().get(r)}static getValuesMap(){return t.valuesMap||(t.valuesMap=t.initValuesMap()),t.valuesMap}static initValuesMap(){let r=new Map;return t.values.forEach(e=>r.set(e.name,e)),r}toJSON(){return this.name}},S=[s.NLLS,s.MFourFit,s.MESA,s.EPR,s.LSPR,s.SR],c=class{isValid(){return!(this.windowStart<0||this.windowEnd<0||this.periodMin<=0||this.periodMax<=0||this.periodMin>this.periodMax||this.windowEnd>0&&this.windowStart>=this.windowEnd||this.method===null||this.method===void 0||this.detrending===null||this.detrending===void 0)}},l=class{static sameJob(r,e){return!r||!e?!1:r.jobId===e.jobId&&r.parentId===e.parentId}static hasFailed(r){return!(!r||this.isFinished(r)||this.isRunning(r))}static isFinished(r){return!!(r&&r.state&&(r.state==="FINISHED"||r.state==="SUCCESS"))}static isRunning(r){return!!(r&&r.state&&(r.state==="SUBMITTED"||r.state==="PROCESSING"))}};function f(t,r){switch(r){case"ByFit":case"f":return t.f;case"ByMethod":case"m":return t.m;case"ByFirstPeak":case"p":return t.p;case"ByAvgMax":case"a":return t.a;default:throw new Error("Unknown phase option: "+r)}}var h=class{static extractState(r){return r.failed?"F":r.ignored?" I":r.attention?"A":""}};function E(t,r,e){if(r==="zero"&&e==="circ")return t.ph2ZCir;if(r==="zero"&&e==="abs")return t.ph2Z;if(r==="dw"&&e==="circ")return t.ph2WCir;if(r==="dw"&&e==="abs")return t.ph2W;throw new Error("Unknown relative/unit combination: "+r+":"+e)}var d=class{};var D=(()=>{class t{constructor(e){this.BD2REST=e}newPPA(e,i){return this.BD2REST.ppaNew(e.id,i)}getPPAJobs(e){return this.BD2REST.ppaJobs(e.id).pipe(n(i=>i.data))}getPPAJob(e,i){return this.BD2REST.ppaJob(e,i)}downloadPPAJob(e,i,a){return this.BD2REST.ppaExportJob(e,i,a)}deletePPAJob(e,i){return this.BD2REST.ppaDeleteJob(e.id,i)}getPPAJobResultsGrouped(e,i){return this.BD2REST.ppaJobResultsGrouped(e,i)}getPPAJobSimpleResults(e,i){return this.BD2REST.ppaJobSimpleResults(e,i)}getPPAForSelect(e,i){return this.BD2REST.ppaForSelect(e,i).then(a=>a.data)}getPPAJobSimpleStats(e,i){return this.BD2REST.ppaJobSimpleStat(e,i)}getPPAResults(e){return this.BD2REST.ppaResults(e.id).then(i=>i.data)}doPPASelection(e,i,a){return this.BD2REST.ppaDoSelection(e,i,a)}exportURL(e){return this.BD2REST.ppaExportURL(e.id)}static{this.\u0275fac=function(i){return new(i||t)(p(u))}}static{this.\u0275prov=o({token:t,factory:t.\u0275fac,providedIn:"root"})}}return t})();export{s as a,S as b,c,l as d,f as e,h as f,E as g,d as h,D as i};
