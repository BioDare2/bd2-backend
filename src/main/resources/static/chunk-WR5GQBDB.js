import{a as F,b as M,c as k,e as j}from"./chunk-LOLEZELW.js";import{a as _}from"./chunk-URHLGKLM.js";import"./chunk-FDKJ75ZE.js";import{Ga as u,Ia as r,Kb as O,Lb as x,Qb as I,Ra as a,S as d,Sa as m,Ta as C,Y as b,Z as l,_a as S,ab as h,ac as T,dc as D,fc as v,jb as p,kb as g,ra as o,s as y,sa as f,ub as N}from"./chunk-BWZCNJMV.js";var A=e=>["/documents",e];function R(e,t){if(e&1&&(a(0,"li")(1,"a",6),p(2),m()()),e&2){let c=t.$implicit;o(),r("routerLink",N(2,A,c[0])),o(),g(c[1])}}function U(e,t){if(e&1&&(a(0,"div",7),p(1),m()),e&2){let c=h();o(),g(c.missing)}}function B(e,t){if(e&1&&C(0,"bd2-static-content",8),e&2){let c=h();r("docName",c.document)}}var w=(()=>{let t=class t{constructor(n,i){this.route=n,this.titleSetter=i,this.tocON=!1,this.documentOptions=F}ngOnDestroy(){this.docSubscription&&this.docSubscription.unsubscribe()}ngOnInit(){this.docSubscription=this.route.paramMap.pipe(y(n=>{let i=n.get("doc");return(!i||i==="all")&&(i="about"),i})).subscribe(n=>{let i=M(n);n==="about"&&(this.tocON=!0),i?(this.missing=void 0,this.document=n,this.setTitle(i)):(this.missing="Unknown document: "+n,this.document=void 0,this.tocON=!0)})}setTitle(n){let i=n[2]?n[2]:n[0].charAt(0).toUpperCase()+n[0].slice(1);this.titleSetter.setTitle(i)}};t.\u0275fac=function(i){return new(i||t)(f(T),f(_))},t.\u0275cmp=b({type:t,selectors:[["ng-component"]],decls:10,vars:4,consts:[[1,"card","mb-3"],[1,"card-header","bd-bg-primary",3,"click"],[1,"card-body",3,"hidden"],[4,"ngFor","ngForOf"],["class","alert alert-danger","role","alert","type","danger",4,"ngIf"],[3,"docName",4,"ngIf"],[3,"routerLink"],["role","alert","type","danger",1,"alert","alert-danger"],[3,"docName"]],template:function(i,s){i&1&&(a(0,"h2"),p(1,"BioDare2 Documentation"),m(),a(2,"div",0)(3,"div",1),S("click",function(){return s.tocON=!s.tocON}),p(4,"Table of Content"),m(),a(5,"div",2)(6,"ul"),u(7,R,3,4,"li",3),m()()(),u(8,U,2,1,"div",4)(9,B,1,1,"bd2-static-content",5)),i&2&&(o(5),r("hidden",!s.tocON),o(2),r("ngForOf",s.documentOptions),o(),r("ngIf",s.missing),o(),r("ngIf",!s.missing))},dependencies:[O,x,k,D],encapsulation:2});let e=t;return e})();var $=[{path:"",children:[{path:":doc",component:w},{path:"",redirectTo:"all",pathMatch:"full"}]}],L=(()=>{let t=class t{};t.\u0275fac=function(i){return new(i||t)},t.\u0275mod=l({type:t}),t.\u0275inj=d({imports:[v.forChild($),v]});let e=t;return e})();var nt=(()=>{let t=class t{};t.\u0275fac=function(i){return new(i||t)},t.\u0275mod=l({type:t}),t.\u0275inj=d({imports:[I,j,L]});let e=t;return e})();export{nt as DocumentsModule};