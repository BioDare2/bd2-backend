import{a as me}from"./chunk-V5UVISF6.js";import{a as De}from"./chunk-FUHIRSC4.js";import{e as Ae}from"./chunk-LOLEZELW.js";import{a as b}from"./chunk-I53LRGHG.js";import{a as Te,b as je}from"./chunk-ZUXWEGFT.js";import"./chunk-FDKJ75ZE.js";import{a as G,b as W,c as se,g as O,h as Me,j as ke,l as Q,m as Pe,o as qe}from"./chunk-PGRVX3MW.js";import{A as P,C as Fe,D as U,E as Ie,F as Ne,G as B,H as K,I as H,L as Ve,d as E,f,h as S,i as F,j as ne,l as ae,m as I,q as L,r as oe,s as z,y as N}from"./chunk-6XVRX2RL.js";import{B as he,Ga as h,Ia as l,Lb as C,Qb as Re,R as Ce,Ra as a,S as q,Sa as n,Ta as u,U as be,Xa as y,Y as w,Z as V,_a as _,aa as g,ab as c,ac as re,ba as v,cc as Ee,dc as Se,fb as T,fc as ve,gb as j,hb as D,ib as A,j as xe,ja as ge,jb as s,kb as ye,kc as k,lb as R,o as ee,ob as Y,pb as Z,qb as $,ra as d,s as fe,sa as p,x as te,ya as ie}from"./chunk-BWZCNJMV.js";var Le=(()=>{let r=class r{constructor(e){this.scriptLoaded=!1,this.readySubject=new xe(!1),window.reCaptchaOnloadCallback=()=>e.run(this.onloadCallback.bind(this))}getReady(e){if(!this.scriptLoaded){this.scriptLoaded=!0;let t=document.body,o=document.createElement("script");o.innerHTML="",o.src="https://www.google.com/recaptcha/api.js?onload=reCaptchaOnloadCallback&render=explicit"+(e?"&hl="+e:""),o.async=!0,o.defer=!0,t.appendChild(o)}return this.readySubject.asObservable()}onloadCallback(){this.readySubject.next(!0)}};r.\u0275fac=function(t){return new(t||r)(be(ie))},r.\u0275prov=Ce({token:r,factory:r.\u0275fac,providedIn:"root"});let i=r;return i})();var et=["target"],de=(()=>{let r=class r{constructor(e,t){this._zone=e,this._captchaService=t,this.site_key=null,this.theme="light",this.type="image",this.size="normal",this.tabindex=0,this.language=null,this.captchaResponse=new ge,this.captchaExpired=new ge,this.widgetId=null}ngOnInit(){this._captchaService.getReady(this.language).subscribe(e=>{e&&(this.widgetId=window.grecaptcha.render(this.targetRef.nativeElement,{sitekey:this.site_key,theme:this.theme,type:this.type,size:this.size,tabindex:this.tabindex,callback:t=>this._zone.run(this.recaptchaCallback.bind(this,t)),"expired-callback":()=>this._zone.run(this.recaptchaExpiredCallback.bind(this))}))})}reset(){this.widgetId!==null&&window.grecaptcha.reset(this.widgetId)}getResponse(){return this.widgetId?window.grecaptcha.getResponse(this.targetRef.nativeElement):null}recaptchaCallback(e){this.captchaResponse.emit(e)}recaptchaExpiredCallback(){this.captchaExpired.emit()}};r.\u0275fac=function(t){return new(t||r)(p(ie),p(Le))},r.\u0275cmp=w({type:r,selectors:[["bd2-recaptcha"]],viewQuery:function(t,o){if(t&1&&T(et,7),t&2){let x;j(x=D())&&(o.targetRef=x.first)}},inputs:{site_key:"site_key",theme:"theme",type:"type",size:"size",tabindex:"tabindex",language:"language"},outputs:{captchaResponse:"captchaResponse",captchaExpired:"captchaExpired"},decls:2,vars:0,consts:[["target",""]],template:function(t,o){t&1&&u(0,"div",null,0)},encapsulation:2});let i=r;return i})();var tt=["recaptcha"];function it(i,r){if(i&1&&(a(0,"div",5),s(1),n()),i&2){let m=c();d(),R("",m.msg," ")}}function rt(i,r){if(i&1&&(a(0,"div",6),s(1),n()),i&2){let m=c();d(),R("",m.errMsg," ")}}function nt(i,r){if(i&1){let m=y();a(0,"form",null,0)(2,"div",7)(3,"label",8),s(4,"Login or email"),n(),a(5,"input",9),$("ngModelChange",function(t){g(m);let o=c();return Z(o.identifier,t)||(o.identifier=t),v(t)}),n()(),a(6,"div",7)(7,"bd2-recaptcha",10,1),_("captchaResponse",function(t){g(m);let o=c();return v(o.captcha(t))})("captchaExpired",function(){g(m);let t=c();return v(t.captchaExpired())}),n(),a(9,"div",11),s(10," Captcha selection is needed "),n()(),a(11,"button",12),_("click",function(){g(m);let t=c();return v(t.request())}),s(12,"Send "),n()()}if(i&2){let m=A(1),e=c();d(5),Y("ngModel",e.identifier),d(2),l("site_key",e.captchaSiteKey),d(2),l("hidden",!e.missingCaptcha),d(2),l("disabled",!m.valid)}}var Ue=(()=>{let r=class r{constructor(e){this.userService=e,this.requested=!1,this.captchaSiteKey=me.captchaSiteKey}ngOnInit(){}captcha(e){this.gRecaptchaResponse=e,e&&(this.missingCaptcha=!1)}captchaExpired(){this.gRecaptchaResponse=null}request(){!this.identifier||this.identifier.trim()===""||(this.msg=void 0,this.errMsg=void 0,this.userService.requestReset(this.identifier,this.gRecaptchaResponse).then(e=>{this.msg="Reset link was sent to "+e,this.requested=!0}).catch(e=>{this.errMsg=e.message?e.message:e,this.gRecaptchaResponse=null,this.recaptcha&&this.recaptcha.reset()}))}};r.\u0275fac=function(t){return new(t||r)(p(b))},r.\u0275cmp=w({type:r,selectors:[["bd2-reset-request"]],viewQuery:function(t,o){if(t&1&&T(tt,5),t&2){let x;j(x=D())&&(o.recaptcha=x.first)}},decls:6,vars:3,consts:[["reminderForm","ngForm"],["recaptcha",""],["class","alert alert-success",4,"ngIf"],["class","alert alert-danger",4,"ngIf"],[4,"ngIf"],[1,"alert","alert-success"],[1,"alert","alert-danger"],[1,"form-group"],["for","identifier"],["type","text","required","","id","identifier","name","identifier",1,"form-control",3,"ngModelChange","ngModel"],[3,"captchaResponse","captchaExpired","site_key"],[1,"alert","alert-danger",3,"hidden"],["type","submit",1,"btn","btn-primary",3,"click","disabled"]],template:function(t,o){t&1&&(a(0,"div")(1,"h3"),s(2,"Forgotten password"),n(),h(3,it,2,1,"div",2)(4,rt,2,1,"div",3)(5,nt,13,4,"form",4),n()),t&2&&(d(3),l("ngIf",o.msg),d(),l("ngIf",o.errMsg),d(),l("ngIf",!o.requested))},dependencies:[C,I,E,S,F,N,ae,ne,de],encapsulation:2});let i=r;return i})();var at=/^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$/;function ot(i){return i=i?i.toLocaleLowerCase():"",!!at.test(i)}var st=/^[a-z]+$/,mt=/^[A-Z]+$/,dt=/^[0-9]+$/;function we(i){return!!(!i||i.length<8||st.test(i)||mt.test(i)||dt.test(i))}function ce(i){return we(i)?{"password-weak":!0}:null}function pe(i){return i.password===i.password2?null:{"password-mismatch":!0}}function ue(i){return ot(i)?null:{pattern:"Not valid email format"}}function lt(i,r){if(i&1&&(a(0,"div",6),s(1),n()),i&2){let m=c();d(),R("",m.msg," ")}}function ct(i,r){if(i&1&&(a(0,"div",7),s(1),n()),i&2){let m=c();d(),R("",m.errMsg," ")}}function pt(i,r){if(i&1){let m=y();a(0,"form",null,0)(2,"div",8)(3,"label",9),s(4,"Password"),n(),a(5,"input",10,1),$("ngModelChange",function(t){g(m);let o=c(2);return Z(o.password,t)||(o.password=t),v(t)}),n(),a(7,"div",11),s(8," Password must be at least 8 long, containing a digit or symbol or capital letter "),n()(),a(9,"div",8)(10,"label",12),s(11,"Repeat password"),n(),a(12,"input",13,2),$("ngModelChange",function(t){g(m);let o=c(2);return Z(o.password2,t)||(o.password2=t),v(t)}),n(),a(14,"div",11),s(15," Passwords do not match "),n()(),a(16,"button",14),_("click",function(){g(m);let t=c(2);return v(t.reset())}),s(17,"Reset "),n()()}if(i&2){let m=A(1),e=A(6),t=A(13),o=c(2);d(5),Y("ngModel",o.password),d(2),l("hidden",e.pristine||!o.weakPassword()),d(5),Y("ngModel",o.password2),d(2),l("hidden",t.pristine||o.matching()),d(2),l("disabled",!m.form.valid||o.passwordProblem())}}function ut(i,r){if(i&1&&(a(0,"div"),h(1,pt,18,5,"form",5),n()),i&2){let m=c();d(),l("ngIf",!m.requested)}}var We=(()=>{let r=class r{constructor(e,t){this.userService=e,this.route=t,this.requested=!1}ngOnInit(){this.token=this.route.snapshot.queryParamMap.get("token"),this.token||(this.errMsg="Missing reset token")}reset(){this.token&&this.userService.resetPassword(this.password,this.token).then(e=>{this.msg="You can sign in with new password and login "+e,this.requested=!0}).catch(e=>{this.errMsg=e.message?e.message:e})}weakPassword(){return we(this.password)}matching(){return this.password===this.password2}passwordProblem(){return!!(this.weakPassword()||!this.matching())}};r.\u0275fac=function(t){return new(t||r)(p(b),p(re))},r.\u0275cmp=w({type:r,selectors:[["ng-component"]],decls:6,vars:3,consts:[["resetForm","ngForm"],["fPassword","ngModel"],["fPassword2","ngModel"],["class","alert alert-success",4,"ngIf"],["class","alert alert-danger",4,"ngIf"],[4,"ngIf"],[1,"alert","alert-success"],[1,"alert","alert-danger"],[1,"form-group"],["for","password"],["type","password","required","","minlength","8","id","password","placeholder","new password","name","fPassword",1,"form-control",3,"ngModelChange","ngModel"],[1,"alert","alert-danger",3,"hidden"],["for","password2"],["type","password","id","password2","required","","placeholder","password","name","fPassword2",1,"form-control",3,"ngModelChange","ngModel"],["type","submit",1,"btn","btn-primary",3,"click","disabled"]],template:function(t,o){t&1&&(a(0,"div")(1,"h3"),s(2,"Password reset"),n(),h(3,lt,2,1,"div",3)(4,ct,2,1,"div",4)(5,ut,2,1,"div",5),n()),t&2&&(d(3),l("ngIf",o.msg),d(),l("ngIf",o.errMsg),d(),l("ngIf",o.token))},dependencies:[C,I,E,S,F,N,P,ae,ne],encapsulation:2});let i=r;return i})();function ft(i,r){i&1&&(a(0,"div",1),s(1,"Use the activation link that was sent in the email"),n())}var Oe=(()=>{let r=class r{constructor(e,t,o,x){this.route=e,this.router=t,this.userService=o,this.feedback=x}ngOnInit(){this.token=this.route.snapshot.queryParamMap.get("token"),this.token&&this.userService.activate(this.token).then(e=>{this.feedback.success("Your account has been activated, use: "+e.login+" to sign in"),this.router.navigate(["/login"])}).catch(e=>{this.feedback.error(e),this.router.navigate(["/login"])})}};r.\u0275fac=function(t){return new(t||r)(p(re),p(Ee),p(b),p(k))},r.\u0275cmp=w({type:r,selectors:[["ng-component"]],decls:1,vars:1,consts:[["class","alert alert-danger danger",4,"ngIf"],[1,"alert","alert-danger","danger"]],template:function(t,o){t&1&&h(0,ft,2,0,"div",0),t&2&&l("ngIf",!o.token)},dependencies:[C],encapsulation:2});let i=r;return i})();var ht=["recaptcha"];function gt(i,r){if(i&1&&(a(0,"div",5)(1,"h4"),s(2,"Your registration was successful."),n(),s(3," We have sent you the activation link to "),a(4,"strong"),s(5),n(),s(6,", please use that link before logging in "),n()),i&2){let m=c();d(5),ye(m.registeredMsg)}}function vt(i,r){i&1&&(a(0,"mat-error"),s(1,"Alphanumerical login, min length 5, only numbers, small letters and ._"),n())}function wt(i,r){i&1&&(a(0,"mat-error"),s(1,"Such login already exists"),n())}function _t(i,r){i&1&&(a(0,"mat-error"),s(1,"Not valid email format"),n())}function xt(i,r){i&1&&(a(0,"mat-error"),s(1,"Address is already being used"),n())}function Ct(i,r){i&1&&(a(0,"mat-error"),s(1," Academic email is required for the registration. Contact us if your email is not recognized as academic. "),n())}function bt(i,r){i&1&&(a(0,"mat-error"),s(1,"Passwords do not match"),n())}function yt(i,r){if(i&1){let m=y();a(0,"div",6)(1,"form",7,0)(3,"div",8)(4,"mat-form-field",9)(5,"mat-label",10),s(6,"Login"),n(),u(7,"input",11),a(8,"mat-hint"),s(9,"Alphanumerical login, min length 5"),n(),h(10,vt,2,0,"mat-error",12)(11,wt,2,0,"mat-error",12),n()(),a(12,"div",8)(13,"mat-form-field",9)(14,"mat-label",13),s(15,"Email"),n(),u(16,"input",14),h(17,_t,2,0,"mat-error",12)(18,xt,2,0,"mat-error",12)(19,Ct,2,0,"mat-error",12),n()(),a(20,"div",15)(21,"div",8)(22,"mat-form-field",9)(23,"mat-label",16),s(24,"Password"),n(),u(25,"input",17),a(26,"mat-hint"),s(27,"Password must be at least 8 long, containing a digit or symbol or capital letter"),n(),a(28,"mat-error"),s(29,"Password must be at least 8 long, containing a digit or symbol or capital letter"),n()()(),a(30,"div",8)(31,"mat-form-field",9)(32,"mat-label",18),s(33,"Repeat password"),n(),u(34,"input",19),h(35,bt,2,0,"mat-error",12),n()()(),a(36,"div",8)(37,"mat-form-field",9)(38,"mat-label",20),s(39,"First Name"),n(),u(40,"input",21),n()(),a(41,"div",8)(42,"mat-form-field",9)(43,"mat-label",22),s(44,"Family Name"),n(),u(45,"input",23),n()(),a(46,"div",8)(47,"mat-form-field",9)(48,"mat-label",24),s(49,"Institution"),n(),u(50,"input",25),n()(),a(51,"div",8)(52,"bd2-recaptcha",26,1),_("captchaExpired",function(){g(m);let t=c();return v(t.captchaExpired())})("captchaResponse",function(t){g(m);let o=c();return v(o.captcha(t))}),n(),a(54,"div",27),s(55," Captcha selection is needed "),n()(),a(56,"div",8)(57,"mat-checkbox",28),s(58," I agree to conditions of "),a(59,"span",29)(60,"a",30),_("click",function(){g(m);let t=c();return v(t.helpDialog.show("service"))}),s(61,"service"),n()(),s(62,". And I understand that unless changed my data will be shared 3 years after their registration. "),n()(),a(63,"button",31),_("click",function(){g(m);let t=c();return v(t.register())}),s(64," Register "),n()()()}if(i&2){let m=c();d(),l("formGroup",m.userForm),d(9),l("ngIf",m.userNameField.errors&&!m.userNameField.errors["login-taken"]),d(),l("ngIf",m.userNameField.errors&&m.userNameField.errors["login-taken"]),d(6),l("ngIf",m.emailField.errors&&m.emailField.errors.pattern),d(),l("ngIf",m.emailField.errors&&m.emailField.errors["email-taken"]),d(),l("ngIf",m.emailField.errors&&m.emailField.errors["email-nonacademic"]),d(16),l("ngIf",m.passwordsGroup.errors),d(17),l("site_key",m.captchaSiteKey),d(2),l("hidden",!m.missingCaptcha),d(9),l("disabled",m.blocked||m.userForm.invalid)}}var He=(()=>{let r=class r{constructor(e,t,o,x){this.userService=e,this.feedback=t,this.fb=o,this.helpDialog=x,this.blocked=!1,this.missingCaptcha=!1,this.captchaSiteKey=me.captchaSiteKey}ngOnInit(){this.userForm=this.fb.group({username:[void 0,{validators:[f.required],asyncValidators:e=>this.availableLogin(e.value),updateOn:"blur"}],email:[void 0,{validators:[f.required,e=>ue(e.value)],asyncValidators:e=>this.suitableEmail(e.value),updateOn:"blur"}],passwords:this.fb.group({password:[void 0,[f.required,e=>ce(e.value)]],password2:[void 0,[f.required]]},{validator:e=>pe(e.value)}),firstName:[void 0,[f.required]],lastName:[void 0,[f.required]],institution:[void 0,[f.required]],terms:[void 0,[f.required]]}),this.userNameField=this.userForm.get("username"),this.emailField=this.userForm.get("email"),this.passwordField=this.userForm.get("passwords.password"),this.password2Field=this.userForm.get("passwords.password2"),this.passwordsGroup=this.userForm.get("passwords")}captcha(e){this.gRecaptchaResponse=e,e&&(this.missingCaptcha=!1)}captchaExpired(){this.gRecaptchaResponse=null}availableLogin(e){return!e||e.length<5?ee({"too-short":!0}):this.userService.availableLogin(e).pipe(fe(t=>t?null:{"login-taken":"User "+e+" already exists"}),he(t=>(this.feedback.error(t),ee({"cannot-connect":!0}))))}suitableEmail(e){return this.userService.suitableEmail(e).pipe(fe(t=>{if(t.isFree&&t.isAcademic)return null;let o={};return t.isFree||(o["email-taken"]="Email: "+e+" is already being used"),t.isAcademic||(o["email-nonacademic"]="Academic email is required for the registration. Contact us if your email is not recognized as academic."),o}),he(t=>(this.feedback.error(t),ee({"cannot-connect":!0}))))}register(){if(this.userForm.valid){if(!this.gRecaptchaResponse&&!this.emailField.value.endsWith(".cn")&&!this.emailField.value.endsWith(".tw")){this.missingCaptcha=!0;return}let e=this.makeUserData(this.userForm.value);this.triggerRegistration(e)}}makeUserData(e){return{login:e.username,password:e.passwords.password,email:e.email,firstName:e.firstName,lastName:e.lastName,institution:e.institution,terms:e.terms,g_recaptcha_response:this.gRecaptchaResponse}}triggerRegistration(e){this.userService.register(e).then(t=>{this.registered=!0,this.registeredMsg=t.email,this.feedback.success("Registration successful")}).catch(t=>{this.feedback.error(t),this.gRecaptchaResponse=void 0,this.recaptcha&&this.recaptcha.reset()})}};r.\u0275fac=function(t){return new(t||r)(p(b),p(k),p(U),p(De))},r.\u0275cmp=w({type:r,selectors:[["ng-component"]],viewQuery:function(t,o){if(t&1&&T(ht,5),t&2){let x;j(x=D())&&(o.recaptcha=x.first)}},decls:7,vars:2,consts:[["registrationForm",""],["recaptcha",""],["appearance","outlined",1,"mb-2"],["class","alert alert-success",4,"ngIf"],["class","mb-4",4,"ngIf"],[1,"alert","alert-success"],[1,"mb-4"],[3,"formGroup"],[1,"form-group"],[1,"w-100"],["for","username"],["matInput","","formControlName","username","id","username","minlength","5","pattern","[0-9a-z._]+","placeholder","choose login","required","","type","text",1,""],[4,"ngIf"],["for","email"],["matInput","","formControlName","email","id","email","placeholder","your email","required","","type","text",1,""],["formGroupName","passwords"],["for","password"],["matInput","","formControlName","password","id","password","minlength","8","placeholder","password","required","","type","password",1,""],["for","password2"],["matInput","","formControlName","password2","id","password2","minlength","8","placeholder","password","required","","type","password",1,""],["for","firstName"],["matInput","","formControlName","firstName","id","firstName","minlength","2","placeholder","e.g. Charles","required","","type","text",1,""],["for","lastName"],["matInput","","formControlName","lastName","id","lastName","minlength","2","placeholder","e.g. Darwin","required","","type","text",1,""],["for","institution"],["matInput","","formControlName","institution","id","institution","minlength","3","placeholder","e.g. University of Edinburgh","required","","type","text",1,""],[3,"captchaExpired","captchaResponse","site_key"],[1,"alert","alert-danger",3,"hidden"],["formControlName","terms","id","terms","required","","type","checkbox"],[2,"font-weight","bold","text-decoration","underline"],["role","button",3,"click"],["type","button",1,"btn","btn-primary",3,"click","disabled"]],template:function(t,o){t&1&&(a(0,"mat-card",2)(1,"mat-card-title")(2,"h2"),s(3,"User registration"),n()(),a(4,"mat-card-content"),h(5,gt,7,1,"div",3)(6,yt,65,10,"div",4),n()()),t&2&&(d(5),l("ngIf",o.registered),d(),l("ngIf",!o.registered))},dependencies:[C,I,E,S,F,N,P,Fe,L,z,oe,de,B,H,K,O,G,se,W,Q,Te],encapsulation:2});let i=r;return i})();function Rt(i,r){i&1&&(a(0,"div",21),s(1,"Account has been updated"),n())}function Et(i,r){if(i&1){let m=y();a(0,"mat-card",2)(1,"mat-card-title")(2,"h2"),s(3),n()(),a(4,"mat-card-content"),h(5,Rt,2,0,"div",3),a(6,"form",4,0)(8,"div",5)(9,"mat-form-field",6)(10,"mat-label",7),s(11,"Login"),n(),u(12,"input",8),n()(),a(13,"div",5)(14,"a",9),s(15,"Change password"),n()(),a(16,"div",5)(17,"mat-form-field",6)(18,"mat-label",10),s(19,"Email"),n(),u(20,"input",11),a(21,"mat-error"),s(22,"Not valid email format"),n()()(),a(23,"div",5)(24,"mat-form-field",6)(25,"mat-label",12),s(26,"First Name"),n(),u(27,"input",13),n()(),a(28,"div",5)(29,"mat-form-field",6)(30,"mat-label",14),s(31,"Family Name"),n(),u(32,"input",15),n()(),a(33,"div",5)(34,"mat-form-field",6)(35,"mat-label",16),s(36,"Institution"),n(),u(37,"input",17),n()(),a(38,"div",5)(39,"mat-form-field",6)(40,"mat-label",18),s(41,"Current password"),n(),u(42,"input",19),n()(),a(43,"button",20),_("click",function(){g(m);let t=c();return v(t.save())}),s(44," Update "),n()()()()}if(i&2){let m=c();d(3),R("Edit user: ",m.user.name,""),d(2),l("ngIf",m.updated),d(),l("formGroup",m.userForm),d(37),l("disabled",m.userForm.invalid)}}var Ye=(()=>{let r=class r{constructor(e,t,o){this.userService=e,this.feedback=t,this.fb=o,this.updated=!1}ngOnInit(){this.user=this.userService.currentUser,this.userForm=this.fb.group({username:[{value:this.user.login,disabled:!0},[f.required],[]],email:[this.user.email,[f.required,e=>ue(e.value)],[]],firstName:[this.user.firstName,[f.required]],lastName:[this.user.lastName,[f.required]],institution:[this.user.institution,[f.required]],currentPassword:[void 0,[f.required]]}),this.userNameField=this.userForm.get("username"),this.emailField=this.userForm.get("email"),this.currentPasswordField=this.userForm.get("currentPassword")}save(){if(!this.userForm.valid)return;let e=this.userForm.value,t={login:this.user.login,currentPassword:e.currentPassword,email:e.email,firstName:e.firstName,lastName:e.lastName,institution:e.institution};this.updated=!1,this.userService.update(t).subscribe(o=>{this.user=o,this.currentPasswordField.reset(),this.feedback.success("User: "+o.login+" has been updated"),this.updated=!0,te(5e3).subscribe(()=>this.updated=!1)},o=>{this.feedback.error(o),this.currentPasswordField.reset()})}};r.\u0275fac=function(t){return new(t||r)(p(b),p(k),p(U))},r.\u0275cmp=w({type:r,selectors:[["ng-component"]],decls:1,vars:1,consts:[["registrationForm",""],["appearance","outlined","class","mb-2",4,"ngIf"],["appearance","outlined",1,"mb-2"],["class","alert alert-success",4,"ngIf"],[3,"formGroup"],[1,"form-group"],[1,"w-100"],["for","username"],["matInput","","formControlName","username","id","username","required","","type","text",1,""],["routerLink","/account/password"],["for","email"],["matInput","","formControlName","email","id","email","placeholder","your email","required","","type","text",1,""],["for","firstName"],["matInput","","formControlName","firstName","id","firstName","minlength","2","placeholder","e.g. Charles","required","","type","text",1,""],["for","lastName"],["matInput","","formControlName","lastName","id","lastName","minlength","2","placeholder","e.g. Darwin","required","","type","text",1,""],["for","institution"],["matInput","","formControlName","institution","id","institution","placeholder","e.g. University of Edinburgh","required","","type","text",1,""],["for","cPassword"],["matInput","","formControlName","currentPassword","id","cPassword","placeholder","current password","required","","type","password",1,""],["type","button",1,"btn","btn-primary",3,"click","disabled"],[1,"alert","alert-success"]],template:function(t,o){t&1&&h(0,Et,45,4,"mat-card",1),t&2&&l("ngIf",o.user&&!o.user.anonymous)},dependencies:[C,I,E,S,F,N,P,L,z,Se,B,H,K,O,G,W,Q],encapsulation:2});let i=r;return i})();function St(i,r){i&1&&(a(0,"div",18),s(1,"Password has been updated"),n())}function Ft(i,r){i&1&&(a(0,"mat-error"),s(1,"Passwords do not match"),n())}function It(i,r){if(i&1){let m=y();a(0,"mat-card",2)(1,"mat-card-title")(2,"h2"),s(3),n()(),a(4,"mat-card-content"),h(5,St,2,0,"div",3),a(6,"form",4,0)(8,"div",5)(9,"mat-form-field",6)(10,"mat-label",7),s(11,"Login"),n(),u(12,"input",8),n()(),a(13,"div",9)(14,"div",5)(15,"mat-form-field",6)(16,"mat-label",10),s(17,"Password"),n(),u(18,"input",11),a(19,"mat-hint"),s(20,"Password must be at least 8 long, containing a digit or symbol or capital letter"),n(),a(21,"mat-error"),s(22,"Password must be at least 8 long, containing a digit or symbol or capital letter"),n()()(),a(23,"div",5)(24,"mat-form-field",6)(25,"mat-label",12),s(26,"Repeat password"),n(),u(27,"input",13),h(28,Ft,2,0,"mat-error",14),n()()(),a(29,"div",5)(30,"mat-form-field",6)(31,"mat-label",15),s(32,"Current password"),n(),u(33,"input",16),n()(),a(34,"button",17),_("click",function(){g(m);let t=c();return v(t.save())}),s(35," Update "),n()()()()}if(i&2){let m=c();d(3),R("Change password for ",m.user.name,""),d(2),l("ngIf",m.updated),d(),l("formGroup",m.userForm),d(22),l("ngIf",m.passwordsGroup.errors),d(6),l("disabled",m.userForm.invalid)}}var Ze=(()=>{let r=class r{constructor(e,t,o){this.userService=e,this.feedback=t,this.fb=o,this.updated=!1}ngOnInit(){this.user=this.userService.currentUser,this.userForm=this.fb.group({username:[{value:this.user.login,disabled:!0},[f.required],[]],passwords:this.fb.group({password:[void 0,[f.required,e=>ce(e.value)]],password2:[void 0,[f.required]]},{validator:e=>pe(e.value)}),currentPassword:[void 0,[f.required]]}),this.passwordsGroup=this.userForm.get("passwords")}save(){if(!this.userForm.valid)return;let e=this.userForm.value,t={login:this.user.login,currentPassword:e.currentPassword,password:e.passwords.password};this.updated=!1,this.userService.passwordUpdate(t).subscribe(o=>{this.user=o,this.userForm.reset(),this.feedback.success("User: "+o.login+" password has been updated"),this.updated=!0,te(5e3).subscribe(()=>this.updated=!1)},o=>{this.userForm.reset(),this.feedback.error(o)})}};r.\u0275fac=function(t){return new(t||r)(p(b),p(k),p(U))},r.\u0275cmp=w({type:r,selectors:[["ng-component"]],decls:1,vars:1,consts:[["registrationForm",""],["appearance","outlined","class","mb-2",4,"ngIf"],["appearance","outlined",1,"mb-2"],["class","alert alert-success",4,"ngIf"],[3,"formGroup"],[1,"form-group"],[1,"w-100"],["for","username"],["matInput","","formControlName","username","id","username","required","","type","text",1,""],["formGroupName","passwords"],["for","password"],["matInput","","formControlName","password","id","password","minlength","8","placeholder","password","required","","type","password",1,""],["for","password2"],["matInput","","formControlName","password2","id","password2","minlength","8","placeholder","password","required","","type","password",1,""],[4,"ngIf"],["for","cPassword"],["matInput","","formControlName","currentPassword","id","cPassword","placeholder","current password","required","","type","password",1,""],["type","button",1,"btn","btn-primary",3,"click","disabled"],[1,"alert","alert-success"]],template:function(t,o){t&1&&h(0,It,36,5,"mat-card",1),t&2&&l("ngIf",o.user&&!o.user.anonymous)},dependencies:[C,I,E,S,F,N,P,L,z,oe,B,H,K,O,G,se,W,Q],encapsulation:2});let i=r;return i})();var Nt=[{path:"",children:[{path:"edit",component:Ye},{path:"password",component:Ze},{path:"register",component:He},{path:"activate",component:Oe},{path:"remind",component:Ue},{path:"reset",component:We}]}],$e=(()=>{let r=class r{};r.\u0275fac=function(t){return new(t||r)},r.\u0275mod=V({type:r}),r.\u0275inj=q({imports:[ve.forChild(Nt),ve]});let i=r;return i})();var Je=(()=>{let r=class r{};r.\u0275fac=function(t){return new(t||r)},r.\u0275mod=V({type:r}),r.\u0275inj=q({});let i=r;return i})();var Ai=(()=>{let r=class r{};r.\u0275fac=function(t){return new(t||r)},r.\u0275mod=V({type:r}),r.\u0275inj=q({imports:[Re,Ie,Ne,Je,Ae,$e,Ve,qe,Me,ke,Pe,je]});let i=r;return i})();export{Ai as AccountModule};
