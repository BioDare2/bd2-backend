(window.webpackJsonp=window.webpackJsonp||[]).push([[14],{jcJX:function(e,t,r){"use strict";r.r(t),r.d(t,"AccountModule",(function(){return le}));var s=r("ofXK"),a=r("tyNb"),i=r("AytR"),n=r("fXoL"),o=r("naqj"),c=r("3Pt+"),d=r("2Vo4");let l=(()=>{class e{constructor(e){this.scriptLoaded=!1,this.readySubject=new d.a(!1),window.reCaptchaOnloadCallback=()=>e.run(this.onloadCallback.bind(this))}getReady(e){if(!this.scriptLoaded){this.scriptLoaded=!0;const t=document.body,r=document.createElement("script");r.innerHTML="",r.src="https://www.google.com/recaptcha/api.js?onload=reCaptchaOnloadCallback&render=explicit"+(e?"&hl="+e:""),r.async=!0,r.defer=!0,t.appendChild(r)}return this.readySubject.asObservable()}onloadCallback(){this.readySubject.next(!0)}}return e.\u0275fac=function(t){return new(t||e)(n.ac(n.A))},e.\u0275prov=n.Mb({token:e,factory:e.\u0275fac,providedIn:"root"}),e})();const b=["target"];let u=(()=>{class e{constructor(e,t){this._zone=e,this._captchaService=t,this.site_key=null,this.theme="light",this.type="image",this.size="normal",this.tabindex=0,this.language=null,this.captchaResponse=new n.o,this.captchaExpired=new n.o,this.widgetId=null}ngOnInit(){this._captchaService.getReady(this.language).subscribe(e=>{e&&(this.widgetId=window.grecaptcha.render(this.targetRef.nativeElement,{sitekey:this.site_key,theme:this.theme,type:this.type,size:this.size,tabindex:this.tabindex,callback:e=>this._zone.run(this.recaptchaCallback.bind(this,e)),"expired-callback":()=>this._zone.run(this.recaptchaExpiredCallback.bind(this))}))})}reset(){null!==this.widgetId&&window.grecaptcha.reset(this.widgetId)}getResponse(){return this.widgetId?window.grecaptcha.getResponse(this.targetRef.nativeElement):null}recaptchaCallback(e){this.captchaResponse.emit(e)}recaptchaExpiredCallback(){this.captchaExpired.emit()}}return e.\u0275fac=function(t){return new(t||e)(n.Qb(n.A),n.Qb(l))},e.\u0275cmp=n.Kb({type:e,selectors:[["bd2-recaptcha"]],viewQuery:function(e,t){var r;1&e&&n.Cc(b,!0),2&e&&n.uc(r=n.fc())&&(t.targetRef=r.first)},inputs:{site_key:"site_key",theme:"theme",type:"type",size:"size",tabindex:"tabindex",language:"language"},outputs:{captchaResponse:"captchaResponse",captchaExpired:"captchaExpired"},decls:2,vars:0,consts:[["target",""]],template:function(e,t){1&e&&n.Rb(0,"div",null,0)},encapsulation:2}),e})();const m=["recaptcha"];function p(e,t){if(1&e&&(n.Wb(0,"div",3),n.Gc(1),n.Vb()),2&e){const e=n.ic();n.Cb(1),n.Ic("",e.msg," ")}}function h(e,t){if(1&e&&(n.Wb(0,"div",4),n.Gc(1),n.Vb()),2&e){const e=n.ic();n.Cb(1),n.Ic("",e.errMsg," ")}}function f(e,t){if(1&e){const e=n.Xb();n.Wb(0,"form",null,5),n.Wb(2,"div",6),n.Wb(3,"label",7),n.Gc(4,"Login or email"),n.Vb(),n.Wb(5,"input",8),n.ec("ngModelChange",(function(t){return n.xc(e),n.ic().identifier=t})),n.Vb(),n.Vb(),n.Wb(6,"div",6),n.Wb(7,"bd2-recaptcha",9,10),n.ec("captchaResponse",(function(t){return n.xc(e),n.ic().captcha(t)}))("captchaExpired",(function(){return n.xc(e),n.ic().captchaExpired()})),n.Vb(),n.Wb(9,"div",11),n.Gc(10," Captcha selection is needed "),n.Vb(),n.Vb(),n.Wb(11,"button",12),n.ec("click",(function(){return n.xc(e),n.ic().request()})),n.Gc(12,"Send "),n.Vb(),n.Vb()}if(2&e){const e=n.vc(1),t=n.ic();n.Cb(5),n.oc("ngModel",t.identifier),n.Cb(2),n.oc("site_key",t.captchaSiteKey),n.Cb(2),n.oc("hidden",!t.missingCaptcha),n.Cb(2),n.oc("disabled",!e.valid)}}let g=(()=>{class e{constructor(e){this.userService=e,this.requested=!1,this.captchaSiteKey=i.a.captchaSiteKey}ngOnInit(){}captcha(e){this.gRecaptchaResponse=e,e&&(this.missingCaptcha=!1)}captchaExpired(){this.gRecaptchaResponse=null}request(){this.identifier&&""!==this.identifier.trim()&&(this.msg=void 0,this.errMsg=void 0,this.userService.requestReset(this.identifier,this.gRecaptchaResponse).then(e=>{this.msg="Reset link was sent to "+e,this.requested=!0}).catch(e=>{this.errMsg=e.message?e.message:e,this.gRecaptchaResponse=null,this.recaptcha&&this.recaptcha.reset()}))}}return e.\u0275fac=function(t){return new(t||e)(n.Qb(o.a))},e.\u0275cmp=n.Kb({type:e,selectors:[["bd2-reset-request"]],viewQuery:function(e,t){var r;1&e&&n.Mc(m,!0),2&e&&n.uc(r=n.fc())&&(t.recaptcha=r.first)},decls:6,vars:3,consts:[["class","alert alert-success",4,"ngIf"],["class","alert alert-danger",4,"ngIf"],[4,"ngIf"],[1,"alert","alert-success"],[1,"alert","alert-danger"],["reminderForm","ngForm"],[1,"form-group"],["for","identifier"],["type","text","required","","id","identifier","name","identifier",1,"form-control",3,"ngModel","ngModelChange"],[3,"site_key","captchaResponse","captchaExpired"],["recaptcha",""],[1,"alert","alert-danger",3,"hidden"],["type","submit",1,"btn","btn-primary",3,"disabled","click"]],template:function(e,t){1&e&&(n.Wb(0,"div"),n.Wb(1,"h3"),n.Gc(2,"Forgotten password"),n.Vb(),n.Ec(3,p,2,1,"div",0),n.Ec(4,h,2,1,"div",1),n.Ec(5,f,13,4,"form",2),n.Vb()),2&e&&(n.Cb(3),n.oc("ngIf",t.msg),n.Cb(1),n.oc("ngIf",t.errMsg),n.Cb(1),n.oc("ngIf",!t.requested))},directives:[s.m,c.C,c.q,c.r,c.c,c.y,c.p,c.s,u],encapsulation:2}),e})();const w=/^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$/,v=/^[a-z]+$/,W=/^[A-Z]+$/,V=/^[0-9]+$/;function y(e){return!e||e.length<8||!!v.test(e)||!!W.test(e)||!!V.test(e)}function C(e){return y(e)?{"password-weak":!0}:null}function k(e){return e.password===e.password2?null:{"password-mismatch":!0}}function I(e){return t=(t=e)?t.toLocaleLowerCase():"",w.test(t)?null:{pattern:"Not valid email format"};var t}function G(e,t){if(1&e&&(n.Wb(0,"div",3),n.Gc(1),n.Vb()),2&e){const e=n.ic();n.Cb(1),n.Ic("",e.msg," ")}}function N(e,t){if(1&e&&(n.Wb(0,"div",4),n.Gc(1),n.Vb()),2&e){const e=n.ic();n.Cb(1),n.Ic("",e.errMsg," ")}}function R(e,t){if(1&e){const e=n.Xb();n.Wb(0,"form",null,5),n.Wb(2,"div",6),n.Wb(3,"label",7),n.Gc(4,"Password"),n.Vb(),n.Wb(5,"input",8,9),n.ec("ngModelChange",(function(t){return n.xc(e),n.ic(2).password=t})),n.Vb(),n.Wb(7,"div",10),n.Gc(8," Password must be at least 8 long, containing a digit or symbol or capital letter "),n.Vb(),n.Vb(),n.Wb(9,"div",6),n.Wb(10,"label",11),n.Gc(11,"Repeat password"),n.Vb(),n.Wb(12,"input",12,13),n.ec("ngModelChange",(function(t){return n.xc(e),n.ic(2).password2=t})),n.Vb(),n.Wb(14,"div",10),n.Gc(15," Passwords do not match "),n.Vb(),n.Vb(),n.Wb(16,"button",14),n.ec("click",(function(){return n.xc(e),n.ic(2).reset()})),n.Gc(17,"Reset "),n.Vb(),n.Vb()}if(2&e){const e=n.vc(1),t=n.vc(6),r=n.vc(13),s=n.ic(2);n.Cb(5),n.oc("ngModel",s.password),n.Cb(2),n.oc("hidden",t.pristine||!s.weakPassword()),n.Cb(5),n.oc("ngModel",s.password2),n.Cb(2),n.oc("hidden",r.pristine||s.matching()),n.Cb(2),n.oc("disabled",!e.form.valid||s.passwordProblem())}}function F(e,t){if(1&e&&(n.Wb(0,"div"),n.Ec(1,R,18,5,"form",2),n.Vb()),2&e){const e=n.ic();n.Cb(1),n.oc("ngIf",!e.requested)}}let q=(()=>{class e{constructor(e,t){this.userService=e,this.route=t,this.requested=!1}ngOnInit(){this.token=this.route.snapshot.queryParamMap.get("token"),this.token||(this.errMsg="Missing reset token")}reset(){this.token&&this.userService.resetPassword(this.password,this.token).then(e=>{this.msg="You can sign in with new password and login "+e,this.requested=!0}).catch(e=>{this.errMsg=e.message?e.message:e})}weakPassword(){return y(this.password)}matching(){return this.password===this.password2}passwordProblem(){return!!this.weakPassword()||!this.matching()}}return e.\u0275fac=function(t){return new(t||e)(n.Qb(o.a),n.Qb(a.a))},e.\u0275cmp=n.Kb({type:e,selectors:[["ng-component"]],decls:6,vars:3,consts:[["class","alert alert-success",4,"ngIf"],["class","alert alert-danger",4,"ngIf"],[4,"ngIf"],[1,"alert","alert-success"],[1,"alert","alert-danger"],["resetForm","ngForm"],[1,"form-group"],["for","password"],["type","password","required","","minlength","8","id","password","placeholder","new password","name","fPassword",1,"form-control",3,"ngModel","ngModelChange"],["fPassword","ngModel"],[1,"alert","alert-danger",3,"hidden"],["for","password2"],["type","password","id","password2","required","","placeholder","password","name","fPassword2",1,"form-control",3,"ngModel","ngModelChange"],["fPassword2","ngModel"],["type","submit",1,"btn","btn-primary",3,"disabled","click"]],template:function(e,t){1&e&&(n.Wb(0,"div"),n.Wb(1,"h3"),n.Gc(2,"Password reset"),n.Vb(),n.Ec(3,G,2,1,"div",0),n.Ec(4,N,2,1,"div",1),n.Ec(5,F,2,1,"div",2),n.Vb()),2&e&&(n.Cb(3),n.oc("ngIf",t.msg),n.Cb(1),n.oc("ngIf",t.errMsg),n.Cb(1),n.oc("ngIf",t.token))},directives:[s.m,c.C,c.q,c.r,c.c,c.y,c.l,c.p,c.s],encapsulation:2}),e})();var x=r("6tuW");function E(e,t){1&e&&(n.Wb(0,"div",1),n.Gc(1,"Use the activation link that was sent in the email"),n.Vb())}let P=(()=>{class e{constructor(e,t,r,s){this.route=e,this.router=t,this.userService=r,this.feedback=s}ngOnInit(){this.token=this.route.snapshot.queryParamMap.get("token"),this.token&&this.userService.activate(this.token).then(e=>{this.feedback.success("Your account has been activated, use: "+e.login+" to sign in"),this.router.navigate(["/login"])}).catch(e=>{this.feedback.error(e),this.router.navigate(["/login"])})}}return e.\u0275fac=function(t){return new(t||e)(n.Qb(a.a),n.Qb(a.c),n.Qb(o.a),n.Qb(x.a))},e.\u0275cmp=n.Kb({type:e,selectors:[["ng-component"]],decls:1,vars:1,consts:[["class","alert alert-danger danger",4,"ngIf"],[1,"alert","alert-danger","danger"]],template:function(e,t){1&e&&n.Ec(0,E,2,0,"div",0),2&e&&n.oc("ngIf",!t.token)},directives:[s.m],encapsulation:2}),e})();var M=r("LRne"),A=r("lJxs"),S=r("JIr8"),O=r("Ee+/"),Q=r("Wp6s"),z=r("kmnG"),j=r("qFsG"),_=r("bSwM");const L=["recaptcha"];function K(e,t){if(1&e&&(n.Wb(0,"div",3),n.Wb(1,"h4"),n.Gc(2,"Your registration was successful."),n.Vb(),n.Gc(3," We sent you the activation link to "),n.Wb(4,"strong"),n.Gc(5),n.Vb(),n.Gc(6,", please use that link before logging in "),n.Vb()),2&e){const e=n.ic();n.Cb(5),n.Hc(e.registeredMsg)}}function U(e,t){1&e&&(n.Wb(0,"mat-error"),n.Gc(1,"Alphanumerical login, min length 5, only numbers, small letters and ._"),n.Vb())}function X(e,t){1&e&&(n.Wb(0,"mat-error"),n.Gc(1,"Such login already exists"),n.Vb())}function D(e,t){1&e&&(n.Wb(0,"mat-error"),n.Gc(1,"Not valid email format"),n.Vb())}function $(e,t){1&e&&(n.Wb(0,"mat-error"),n.Gc(1,"Address is already being used"),n.Vb())}function J(e,t){1&e&&(n.Wb(0,"mat-error"),n.Gc(1," Academic email is required for the registration. Contact us if your email is not recognized as academic. "),n.Vb())}function Y(e,t){1&e&&(n.Wb(0,"mat-error"),n.Gc(1,"Passwords do not match"),n.Vb())}function H(e,t){if(1&e){const e=n.Xb();n.Wb(0,"div",4),n.Wb(1,"form",5,6),n.Wb(3,"div",7),n.Wb(4,"mat-form-field",8),n.Wb(5,"mat-label",9),n.Gc(6,"Login"),n.Vb(),n.Rb(7,"input",10),n.Wb(8,"mat-hint"),n.Gc(9,"Alphanumerical login, min length 5"),n.Vb(),n.Ec(10,U,2,0,"mat-error",11),n.Ec(11,X,2,0,"mat-error",11),n.Vb(),n.Vb(),n.Wb(12,"div",7),n.Wb(13,"mat-form-field",8),n.Wb(14,"mat-label",12),n.Gc(15,"Email"),n.Vb(),n.Rb(16,"input",13),n.Ec(17,D,2,0,"mat-error",11),n.Ec(18,$,2,0,"mat-error",11),n.Ec(19,J,2,0,"mat-error",11),n.Vb(),n.Vb(),n.Wb(20,"div",14),n.Wb(21,"div",7),n.Wb(22,"mat-form-field",8),n.Wb(23,"mat-label",15),n.Gc(24,"Password"),n.Vb(),n.Rb(25,"input",16),n.Wb(26,"mat-hint"),n.Gc(27,"Password must be at least 8 long, containing a digit or symbol or capital letter"),n.Vb(),n.Wb(28,"mat-error"),n.Gc(29,"Password must be at least 8 long, containing a digit or symbol or capital letter"),n.Vb(),n.Vb(),n.Vb(),n.Wb(30,"div",7),n.Wb(31,"mat-form-field",8),n.Wb(32,"mat-label",17),n.Gc(33,"Repeat password"),n.Vb(),n.Rb(34,"input",18),n.Ec(35,Y,2,0,"mat-error",11),n.Vb(),n.Vb(),n.Vb(),n.Wb(36,"div",7),n.Wb(37,"mat-form-field",8),n.Wb(38,"mat-label",19),n.Gc(39,"First Name"),n.Vb(),n.Rb(40,"input",20),n.Vb(),n.Vb(),n.Wb(41,"div",7),n.Wb(42,"mat-form-field",8),n.Wb(43,"mat-label",21),n.Gc(44,"Family Name"),n.Vb(),n.Rb(45,"input",22),n.Vb(),n.Vb(),n.Wb(46,"div",7),n.Wb(47,"mat-form-field",8),n.Wb(48,"mat-label",23),n.Gc(49,"Institution"),n.Vb(),n.Rb(50,"input",24),n.Vb(),n.Vb(),n.Wb(51,"div",7),n.Wb(52,"bd2-recaptcha",25,26),n.ec("captchaExpired",(function(){return n.xc(e),n.ic().captchaExpired()}))("captchaResponse",(function(t){return n.xc(e),n.ic().captcha(t)})),n.Vb(),n.Wb(54,"div",27),n.Gc(55," Captcha selection is needed "),n.Vb(),n.Vb(),n.Wb(56,"div",7),n.Wb(57,"mat-checkbox",28),n.Gc(58," I agree to conditions of "),n.Wb(59,"span",29),n.Wb(60,"a",30),n.ec("click",(function(){return n.xc(e),n.ic().helpDialog.show("service")})),n.Gc(61,"service"),n.Vb(),n.Vb(),n.Gc(62,". And I understand that unless changed my data will be shared 3 years after their registration. "),n.Vb(),n.Vb(),n.Wb(63,"button",31),n.ec("click",(function(){return n.xc(e),n.ic().register()})),n.Gc(64," Register "),n.Vb(),n.Vb(),n.Vb()}if(2&e){const e=n.ic();n.Cb(1),n.oc("formGroup",e.userForm),n.Cb(9),n.oc("ngIf",e.userNameField.errors&&!e.userNameField.errors["login-taken"]),n.Cb(1),n.oc("ngIf",e.userNameField.errors&&e.userNameField.errors["login-taken"]),n.Cb(6),n.oc("ngIf",e.emailField.errors&&e.emailField.errors.pattern),n.Cb(1),n.oc("ngIf",e.emailField.errors&&e.emailField.errors["email-taken"]),n.Cb(1),n.oc("ngIf",e.emailField.errors&&e.emailField.errors["email-nonacademic"]),n.Cb(16),n.oc("ngIf",e.passwordsGroup.errors),n.Cb(17),n.oc("site_key",e.captchaSiteKey),n.Cb(2),n.oc("hidden",!e.missingCaptcha),n.Cb(9),n.oc("disabled",e.blocked||e.userForm.invalid)}}let B=(()=>{class e{constructor(e,t,r,s){this.userService=e,this.feedback=t,this.fb=r,this.helpDialog=s,this.blocked=!1,this.missingCaptcha=!1,this.captchaSiteKey=i.a.captchaSiteKey}ngOnInit(){this.userForm=this.fb.group({username:[void 0,{validators:[c.A.required],asyncValidators:e=>this.availableLogin(e.value),updateOn:"blur"}],email:[void 0,{validators:[c.A.required,e=>I(e.value)],asyncValidators:e=>this.suitableEmail(e.value),updateOn:"blur"}],passwords:this.fb.group({password:[void 0,[c.A.required,e=>C(e.value)]],password2:[void 0,[c.A.required]]},{validator:e=>k(e.value)}),firstName:[void 0,[c.A.required]],lastName:[void 0,[c.A.required]],institution:[void 0,[c.A.required]],terms:[void 0,[c.A.required]]}),this.userNameField=this.userForm.get("username"),this.emailField=this.userForm.get("email"),this.passwordField=this.userForm.get("passwords.password"),this.password2Field=this.userForm.get("passwords.password2"),this.passwordsGroup=this.userForm.get("passwords")}captcha(e){this.gRecaptchaResponse=e,e&&(this.missingCaptcha=!1)}captchaExpired(){this.gRecaptchaResponse=null}availableLogin(e){return!e||e.length<5?Object(M.a)({"too-short":!0}):this.userService.availableLogin(e).pipe(Object(A.a)(t=>t?null:{"login-taken":"User "+e+" already exists"}),Object(S.a)(e=>(this.feedback.error(e),Object(M.a)({"cannot-connect":!0}))))}suitableEmail(e){return this.userService.suitableEmail(e).pipe(Object(A.a)(t=>{if(t.isFree&&t.isAcademic)return null;const r={};return t.isFree||(r["email-taken"]="Email: "+e+" is already being used"),t.isAcademic||(r["email-nonacademic"]="Academic email is required for the registration. Contact us if your email is not recognized as academic."),r}),Object(S.a)(e=>(this.feedback.error(e),Object(M.a)({"cannot-connect":!0}))))}register(){if(this.userForm.valid){if(!this.gRecaptchaResponse&&!this.emailField.value.endsWith(".cn")&&!this.emailField.value.endsWith(".tw"))return void(this.missingCaptcha=!0);const e=this.makeUserData(this.userForm.value);this.triggerRegistration(e)}}makeUserData(e){return{login:e.username,password:e.passwords.password,email:e.email,firstName:e.firstName,lastName:e.lastName,institution:e.institution,terms:e.terms,g_recaptcha_response:this.gRecaptchaResponse}}triggerRegistration(e){this.userService.register(e).then(e=>{this.registered=!0,this.registeredMsg=e.email,this.feedback.success("Registration successful")}).catch(e=>{this.feedback.error(e),this.gRecaptchaResponse=void 0,this.recaptcha&&this.recaptcha.reset()})}}return e.\u0275fac=function(t){return new(t||e)(n.Qb(o.a),n.Qb(x.a),n.Qb(c.d),n.Qb(O.a))},e.\u0275cmp=n.Kb({type:e,selectors:[["ng-component"]],viewQuery:function(e,t){var r;1&e&&n.Mc(L,!0),2&e&&n.uc(r=n.fc())&&(t.recaptcha=r.first)},decls:7,vars:2,consts:[[1,"mb-2"],["class","alert alert-success",4,"ngIf"],["class","mb-4",4,"ngIf"],[1,"alert","alert-success"],[1,"mb-4"],[3,"formGroup"],["registrationForm",""],[1,"form-group"],[1,"w-100"],["for","username"],["matInput","","formControlName","username","id","username","minlength","5","pattern","[0-9|a-z|\\._]+","placeholder","choose login","required","","type","text",1,""],[4,"ngIf"],["for","email"],["matInput","","formControlName","email","id","email","placeholder","your email","required","","type","text",1,""],["formGroupName","passwords"],["for","password"],["matInput","","formControlName","password","id","password","minlength","8","placeholder","password","required","","type","password",1,""],["for","password2"],["matInput","","formControlName","password2","id","password2","minlength","8","placeholder","password","required","","type","password",1,""],["for","firstName"],["matInput","","formControlName","firstName","id","firstName","minlength","2","placeholder","e.g. Charles","required","","type","text",1,""],["for","lastName"],["matInput","","formControlName","lastName","id","lastName","minlength","2","placeholder","e.g. Darwin","required","","type","text",1,""],["for","institution"],["matInput","","formControlName","institution","id","institution","minlength","3","placeholder","e.g. University of Edinburgh","required","","type","text",1,""],[3,"site_key","captchaExpired","captchaResponse"],["recaptcha",""],[1,"alert","alert-danger",3,"hidden"],["formControlName","terms","id","terms","required","","type","checkbox"],[2,"font-weight","bold","text-decoration","underline"],["role","button",3,"click"],["type","button",1,"btn","btn-primary",3,"disabled","click"]],template:function(e,t){1&e&&(n.Wb(0,"mat-card",0),n.Wb(1,"mat-card-title"),n.Wb(2,"h2"),n.Gc(3,"User registration"),n.Vb(),n.Vb(),n.Wb(4,"mat-card-content"),n.Ec(5,K,7,1,"div",1),n.Ec(6,H,65,10,"div",2),n.Vb(),n.Vb()),2&e&&(n.Cb(5),n.oc("ngIf",t.registered),n.Cb(1),n.oc("ngIf",!t.registered))},directives:[Q.a,Q.e,Q.c,s.m,c.C,c.q,c.h,z.c,z.g,j.b,c.c,c.p,c.g,c.l,c.v,c.y,z.f,c.i,z.b,u,_.a,_.c],encapsulation:2}),e})();var T=r("PqYM");function Z(e,t){1&e&&(n.Wb(0,"div",21),n.Gc(1,"Account has been updated"),n.Vb())}function ee(e,t){if(1&e){const e=n.Xb();n.Wb(0,"mat-card",1),n.Wb(1,"mat-card-title"),n.Wb(2,"h2"),n.Gc(3),n.Vb(),n.Vb(),n.Wb(4,"mat-card-content"),n.Ec(5,Z,2,0,"div",2),n.Wb(6,"form",3,4),n.Wb(8,"div",5),n.Wb(9,"mat-form-field",6),n.Wb(10,"mat-label",7),n.Gc(11,"Login"),n.Vb(),n.Rb(12,"input",8),n.Vb(),n.Vb(),n.Wb(13,"div",5),n.Wb(14,"a",9),n.Gc(15,"Change password"),n.Vb(),n.Vb(),n.Wb(16,"div",5),n.Wb(17,"mat-form-field",6),n.Wb(18,"mat-label",10),n.Gc(19,"Email"),n.Vb(),n.Rb(20,"input",11),n.Wb(21,"mat-error"),n.Gc(22,"Not valid email format"),n.Vb(),n.Vb(),n.Vb(),n.Wb(23,"div",5),n.Wb(24,"mat-form-field",6),n.Wb(25,"mat-label",12),n.Gc(26,"First Name"),n.Vb(),n.Rb(27,"input",13),n.Vb(),n.Vb(),n.Wb(28,"div",5),n.Wb(29,"mat-form-field",6),n.Wb(30,"mat-label",14),n.Gc(31,"Family Name"),n.Vb(),n.Rb(32,"input",15),n.Vb(),n.Vb(),n.Wb(33,"div",5),n.Wb(34,"mat-form-field",6),n.Wb(35,"mat-label",16),n.Gc(36,"Institution"),n.Vb(),n.Rb(37,"input",17),n.Vb(),n.Vb(),n.Wb(38,"div",5),n.Wb(39,"mat-form-field",6),n.Wb(40,"mat-label",18),n.Gc(41,"Current password"),n.Vb(),n.Rb(42,"input",19),n.Vb(),n.Vb(),n.Wb(43,"button",20),n.ec("click",(function(){return n.xc(e),n.ic().save()})),n.Gc(44," Update "),n.Vb(),n.Vb(),n.Vb(),n.Vb()}if(2&e){const e=n.ic();n.Cb(3),n.Ic("Edit user: ",e.user.name,""),n.Cb(2),n.oc("ngIf",e.updated),n.Cb(1),n.oc("formGroup",e.userForm),n.Cb(37),n.oc("disabled",e.userForm.invalid)}}function te(e,t){1&e&&(n.Wb(0,"div",18),n.Gc(1,"Password has been updated"),n.Vb())}function re(e,t){1&e&&(n.Wb(0,"mat-error"),n.Gc(1,"Passwords do not match"),n.Vb())}function se(e,t){if(1&e){const e=n.Xb();n.Wb(0,"mat-card",1),n.Wb(1,"mat-card-title"),n.Wb(2,"h2"),n.Gc(3),n.Vb(),n.Vb(),n.Wb(4,"mat-card-content"),n.Ec(5,te,2,0,"div",2),n.Wb(6,"form",3,4),n.Wb(8,"div",5),n.Wb(9,"mat-form-field",6),n.Wb(10,"mat-label",7),n.Gc(11,"Login"),n.Vb(),n.Rb(12,"input",8),n.Vb(),n.Vb(),n.Wb(13,"div",9),n.Wb(14,"div",5),n.Wb(15,"mat-form-field",6),n.Wb(16,"mat-label",10),n.Gc(17,"Password"),n.Vb(),n.Rb(18,"input",11),n.Wb(19,"mat-hint"),n.Gc(20,"Password must be at least 8 long, containing a digit or symbol or capital letter"),n.Vb(),n.Wb(21,"mat-error"),n.Gc(22,"Password must be at least 8 long, containing a digit or symbol or capital letter"),n.Vb(),n.Vb(),n.Vb(),n.Wb(23,"div",5),n.Wb(24,"mat-form-field",6),n.Wb(25,"mat-label",12),n.Gc(26,"Repeat password"),n.Vb(),n.Rb(27,"input",13),n.Ec(28,re,2,0,"mat-error",14),n.Vb(),n.Vb(),n.Vb(),n.Wb(29,"div",5),n.Wb(30,"mat-form-field",6),n.Wb(31,"mat-label",15),n.Gc(32,"Current password"),n.Vb(),n.Rb(33,"input",16),n.Vb(),n.Vb(),n.Wb(34,"button",17),n.ec("click",(function(){return n.xc(e),n.ic().save()})),n.Gc(35," Update "),n.Vb(),n.Vb(),n.Vb(),n.Vb()}if(2&e){const e=n.ic();n.Cb(3),n.Ic("Change password for ",e.user.name,""),n.Cb(2),n.oc("ngIf",e.updated),n.Cb(1),n.oc("formGroup",e.userForm),n.Cb(22),n.oc("ngIf",e.passwordsGroup.errors),n.Cb(6),n.oc("disabled",e.userForm.invalid)}}const ae=[{path:"",children:[{path:"edit",component:(()=>{class e{constructor(e,t,r){this.userService=e,this.feedback=t,this.fb=r,this.updated=!1}ngOnInit(){this.user=this.userService.currentUser,this.userForm=this.fb.group({username:[{value:this.user.login,disabled:!0},[c.A.required],[]],email:[this.user.email,[c.A.required,e=>I(e.value)],[]],firstName:[this.user.firstName,[c.A.required]],lastName:[this.user.lastName,[c.A.required]],institution:[this.user.institution,[c.A.required]],currentPassword:[void 0,[c.A.required]]}),this.userNameField=this.userForm.get("username"),this.emailField=this.userForm.get("email"),this.currentPasswordField=this.userForm.get("currentPassword")}save(){if(!this.userForm.valid)return;const e=this.userForm.value,t={login:this.user.login,currentPassword:e.currentPassword,email:e.email,firstName:e.firstName,lastName:e.lastName,institution:e.institution};this.updated=!1,this.userService.update(t).subscribe(e=>{this.user=e,this.currentPasswordField.reset(),this.feedback.success("User: "+e.login+" has been updated"),this.updated=!0,Object(T.a)(5e3).subscribe(()=>this.updated=!1)},e=>{this.feedback.error(e),this.currentPasswordField.reset()})}}return e.\u0275fac=function(t){return new(t||e)(n.Qb(o.a),n.Qb(x.a),n.Qb(c.d))},e.\u0275cmp=n.Kb({type:e,selectors:[["ng-component"]],decls:1,vars:1,consts:[["class","mb-2",4,"ngIf"],[1,"mb-2"],["class","alert alert-success",4,"ngIf"],[3,"formGroup"],["registrationForm",""],[1,"form-group"],[1,"w-100"],["for","username"],["matInput","","formControlName","username","id","username","required","","type","text",1,""],["routerLink","/account/password"],["for","email"],["matInput","","formControlName","email","id","email","placeholder","your email","required","","type","text",1,""],["for","firstName"],["matInput","","formControlName","firstName","id","firstName","minlength","2","placeholder","e.g. Charles","required","","type","text",1,""],["for","lastName"],["matInput","","formControlName","lastName","id","lastName","minlength","2","placeholder","e.g. Darwin","required","","type","text",1,""],["for","institution"],["matInput","","formControlName","institution","id","institution","placeholder","e.g. University of Edinburgh","required","","type","text",1,""],["for","cPassword"],["matInput","","formControlName","currentPassword","id","cPassword","placeholder","current password","required","","type","password",1,""],["type","button",1,"btn","btn-primary",3,"disabled","click"],[1,"alert","alert-success"]],template:function(e,t){1&e&&n.Ec(0,ee,45,4,"mat-card",0),2&e&&n.oc("ngIf",t.user&&!t.user.anonymous)},directives:[s.m,Q.a,Q.e,Q.c,c.C,c.q,c.h,z.c,z.g,j.b,c.c,c.p,c.g,c.y,a.e,z.b,c.l],encapsulation:2}),e})()},{path:"password",component:(()=>{class e{constructor(e,t,r){this.userService=e,this.feedback=t,this.fb=r,this.updated=!1}ngOnInit(){this.user=this.userService.currentUser,this.userForm=this.fb.group({username:[{value:this.user.login,disabled:!0},[c.A.required],[]],passwords:this.fb.group({password:[void 0,[c.A.required,e=>C(e.value)]],password2:[void 0,[c.A.required]]},{validator:e=>k(e.value)}),currentPassword:[void 0,[c.A.required]]}),this.passwordsGroup=this.userForm.get("passwords")}save(){if(!this.userForm.valid)return;const e=this.userForm.value,t={login:this.user.login,currentPassword:e.currentPassword,password:e.passwords.password};this.updated=!1,this.userService.passwordUpdate(t).subscribe(e=>{this.user=e,this.userForm.reset(),this.feedback.success("User: "+e.login+" password has been updated"),this.updated=!0,Object(T.a)(5e3).subscribe(()=>this.updated=!1)},e=>{this.userForm.reset(),this.feedback.error(e)})}}return e.\u0275fac=function(t){return new(t||e)(n.Qb(o.a),n.Qb(x.a),n.Qb(c.d))},e.\u0275cmp=n.Kb({type:e,selectors:[["ng-component"]],decls:1,vars:1,consts:[["class","mb-2",4,"ngIf"],[1,"mb-2"],["class","alert alert-success",4,"ngIf"],[3,"formGroup"],["registrationForm",""],[1,"form-group"],[1,"w-100"],["for","username"],["matInput","","formControlName","username","id","username","required","","type","text",1,""],["formGroupName","passwords"],["for","password"],["matInput","","formControlName","password","id","password","minlength","8","placeholder","password","required","","type","password",1,""],["for","password2"],["matInput","","formControlName","password2","id","password2","minlength","8","placeholder","password","required","","type","password",1,""],[4,"ngIf"],["for","cPassword"],["matInput","","formControlName","currentPassword","id","cPassword","placeholder","current password","required","","type","password",1,""],["type","button",1,"btn","btn-primary",3,"disabled","click"],[1,"alert","alert-success"]],template:function(e,t){1&e&&n.Ec(0,se,36,5,"mat-card",0),2&e&&n.oc("ngIf",t.user&&!t.user.anonymous)},directives:[s.m,Q.a,Q.e,Q.c,c.C,c.q,c.h,z.c,z.g,j.b,c.c,c.p,c.g,c.y,c.i,c.l,z.f,z.b],encapsulation:2}),e})()},{path:"register",component:B},{path:"activate",component:P},{path:"remind",component:g},{path:"reset",component:q}]}];let ie=(()=>{class e{}return e.\u0275mod=n.Ob({type:e}),e.\u0275inj=n.Nb({factory:function(t){return new(t||e)},imports:[[a.f.forChild(ae)],a.f]}),e})(),ne=(()=>{class e{}return e.\u0275mod=n.Ob({type:e}),e.\u0275inj=n.Nb({factory:function(t){return new(t||e)},imports:[[]]}),e})();var oe=r("wMBR"),ce=r("f0Cb"),de=r("NFeN");let le=(()=>{class e{}return e.\u0275mod=n.Ob({type:e}),e.\u0275inj=n.Nb({factory:function(t){return new(t||e)},imports:[[s.c,c.j,c.x,ne,oe.a,ie,Q.d,ce.b,z.e,de.b,j.c,_.b]]}),e})()}}]);