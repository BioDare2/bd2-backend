import{b as q,d as x,e as y,f as C}from"./chunk-FDKJ75ZE.js";import{Ga as b,Ia as r,Lb as h,Ra as a,Sa as l,Ta as d,Y as f,ab as I,jb as m,kb as p,lb as M,oa as g,ra as o,sa as v}from"./chunk-BWZCNJMV.js";function D(e,t){if(e&1&&d(0,"div",5),e&2){let c=I();r("innerHTML",c.question.details,g)}}var u=class{constructor(t,c=void 0,i="Cancel",n="OK"){this.question=t,this.details=c,this.cancelLabel=i,this.okLabel=n}},H=(()=>{let t=class t{constructor(i){this.question=i||new u("Missing question")}ngOnInit(){}};t.\u0275fac=function(n){return new(n||t)(v(q))},t.\u0275cmp=f({type:t,selectors:[["ng-component"]],decls:10,vars:5,consts:[["mat-dialog-title","",1,"modal-title"],["mat-dialog-content","",1,"modal-body"],[3,"innerHTML",4,"ngIf"],["mat-dialog-close","",1,"btn","btn-primary","btn-sm","mr-2"],[1,"btn","btn-primary","btn-sm",3,"mat-dialog-close"],[3,"innerHTML"]],template:function(n,s){n&1&&(a(0,"h4",0),m(1),l(),a(2,"div",1),b(3,D,1,1,"div",2),l(),d(4,"hr"),a(5,"div")(6,"button",3),m(7),l(),a(8,"button",4),m(9),l()()),n&2&&(o(),M("",s.question.question," "),o(2),r("ngIf",s.question.details),o(4),p(s.question.cancelLabel),o(),r("mat-dialog-close",!0),o(),p(s.question.okLabel))},dependencies:[h,x,y,C],encapsulation:2});let e=t;return e})();export{u as a,H as b};
