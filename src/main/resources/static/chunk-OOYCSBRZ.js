import{b as O,d as _,e as E,f as F,g as P,h as H}from"./chunk-DICBCJW4.js";import{d as A,e as j}from"./chunk-VVRM3QKP.js";import{Ab as g,Ca as r,Da as s,E as S,Ma as d,Na as I,Sa as M,Wa as o,X as D,Y as x,_ as y,bc as T,db as c,eb as m,fb as l,gc as N,ic as w,kc as k,ob as u,q as v,t as C,za as b,zb as p}from"./chunk-AYJRCA2A.js";var B=(()=>{class e{constructor(i){this.http=i}getDocs(i){let t=this.makeOptions(),n="assets/"+i+".html";return this.OKTxt(this.http.get(n,t)).toPromise()}makeOptions(){return{headers:new w({Accept:"text/html"}),responseType:"text",withCredentials:!0}}OKTxt(i){return i.pipe(C(t=>{let n;return t.body?n=t.txt:n=t,n}),S(this.handleBadResponse))}handleBadResponse(i){console.error("Response error",i);let t;switch(i.status){case 401:{t="Bad credentials, locked or not activated account";break}default:t=A.extractMessage(i,"No error details")}return v(t)}static{this.\u0275fac=function(t){return new(t||e)(y(k))}}static{this.\u0275prov=D({token:e,factory:e.\u0275fac,providedIn:"root"})}}return e})();var K=(()=>{class e{constructor(i,t){this.contentService=i,this.feedback=t,this.content="loading..."}set docName(i){i&&(this.content="",this.contentService.getDocs(i).then(t=>this.content=t).catch(t=>{this.content="Cannot load: "+t,this.feedback.error(t)}))}ngOnInit(){}static{this.\u0275fac=function(t){return new(t||e)(s(B),s(j))}}static{this.\u0275cmp=d({type:e,selectors:[["bd2-static-content"]],inputs:{docName:"docName"},standalone:!1,decls:1,vars:1,consts:[[3,"innerHTML"]],template:function(t,n){t&1&&l(0,"div",0),t&2&&o("innerHTML",n.content,b)},encapsulation:2})}}return e})();var f=[["about","About","Documentation"],["service","Service Description (T&C)","Service Description (T&C)"],["embargo","Embargo period explained","Embargo period explained"],["privacy","Privacy Statement","Privacy Statement"],["faq","Frequently Asked Questions (FAQ)","FAQ"],["timeseries-data","TimeSeries data and formats","Timeseries data"],["period-methods","Methods of period analysis","Period analysis methods"],["detrending","Detrending for period analysis","Detrending and analysis"],["phases","Phase calculation","Phase calculation"],["service-2017-01-20","Service Description 2017","Service Description 2017"]];function R(e){return f.findIndex(a=>a[0]===e)>=0}function Y(e){return f.find(a=>a[0]===e)}function q(e,a){if(e&1&&(c(0,"div",6),p(1),m()),e&2){let i=u();r(),g(i.missing)}}function z(e,a){if(e&1&&l(0,"bd2-static-content",7),e&2){let i=u();o("docName",i.docName)}}var nt=(()=>{class e{constructor(i){let t=i?i.docName:void 0;R(t)?(this.title=f.find(n=>n[0]===t)[1],this.docName=t,this.missing=void 0):(this.missing="Unknown document: "+t,this.title=this.missing,this.docName=void 0)}ngOnInit(){}static{this.\u0275fac=function(t){return new(t||e)(s(O))}}static{this.\u0275cmp=d({type:e,selectors:[["bd2-static-content-dialog"]],standalone:!1,decls:8,vars:3,consts:[["mat-dialog-title",""],["mat-dialog-content",""],["class","alert alert-danger","role","alert","type","danger",4,"ngIf"],[3,"docName",4,"ngIf"],["mat-dialog-actions",""],["mat-dialog-close","","tabindex","-1",1,"btn","btn-primary"],["role","alert","type","danger",1,"alert","alert-danger"],[3,"docName"]],template:function(t,n){t&1&&(c(0,"h1",0),p(1),m(),c(2,"div",1),M(3,q,2,1,"div",2)(4,z,1,1,"bd2-static-content",3),m(),c(5,"div",4)(6,"button",5),p(7,"Close"),m()()),t&2&&(r(),g(n.title),r(2),o("ngIf",n.missing),r(),o("ngIf",!n.missing))},dependencies:[T,_,E,P,F,K],encapsulation:2})}}return e})();var lt=(()=>{class e{static{this.\u0275fac=function(t){return new(t||e)}}static{this.\u0275mod=I({type:e})}static{this.\u0275inj=x({imports:[N,H]})}}return e})();export{f as a,Y as b,K as c,nt as d,lt as e};
