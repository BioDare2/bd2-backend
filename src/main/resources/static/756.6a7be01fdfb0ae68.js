"use strict";(self.webpackChunkbiodare2_ui=self.webpackChunkbiodare2_ui||[]).push([[756],{756:(Re,y,o)=>{o.r(y),o.d(y,{AccountModule:()=>w});var l=o(6895),m=o(9299),N=o(2340),e=o(4650),p=o(4004),a=o(4006),Y=o(1135);class h{constructor(t){this.scriptLoaded=!1,this.readySubject=new Y.X(!1),window.reCaptchaOnloadCallback=()=>t.run(this.onloadCallback.bind(this))}getReady(t){if(!this.scriptLoaded){this.scriptLoaded=!0;const r=document.body,i=document.createElement("script");i.innerHTML="",i.src="https://www.google.com/recaptcha/api.js?onload=reCaptchaOnloadCallback&render=explicit"+(t?"&hl="+t:""),i.async=!0,i.defer=!0,r.appendChild(i)}return this.readySubject.asObservable()}onloadCallback(){this.readySubject.next(!0)}}h.\u0275fac=function(t){return new(t||h)(e.LFG(e.R0b))},h.\u0275prov=e.Yz7({token:h,factory:h.\u0275fac,providedIn:"root"});const E=["target"];class f{constructor(t,r){this._zone=t,this._captchaService=r,this.site_key=null,this.theme="light",this.type="image",this.size="normal",this.tabindex=0,this.language=null,this.captchaResponse=new e.vpe,this.captchaExpired=new e.vpe,this.widgetId=null}ngOnInit(){this._captchaService.getReady(this.language).subscribe(t=>{t&&(this.widgetId=window.grecaptcha.render(this.targetRef.nativeElement,{sitekey:this.site_key,theme:this.theme,type:this.type,size:this.size,tabindex:this.tabindex,callback:r=>this._zone.run(this.recaptchaCallback.bind(this,r)),"expired-callback":()=>this._zone.run(this.recaptchaExpiredCallback.bind(this))}))})}reset(){null!==this.widgetId&&window.grecaptcha.reset(this.widgetId)}getResponse(){return this.widgetId?window.grecaptcha.getResponse(this.targetRef.nativeElement):null}recaptchaCallback(t){this.captchaResponse.emit(t)}recaptchaExpiredCallback(){this.captchaExpired.emit()}}f.\u0275fac=function(t){return new(t||f)(e.Y36(e.R0b),e.Y36(h))},f.\u0275cmp=e.Xpm({type:f,selectors:[["bd2-recaptcha"]],viewQuery:function(t,r){if(1&t&&e.Gf(E,7),2&t){let i;e.iGM(i=e.CRH())&&(r.targetRef=i.first)}},inputs:{site_key:"site_key",theme:"theme",type:"type",size:"size",tabindex:"tabindex",language:"language"},outputs:{captchaResponse:"captchaResponse",captchaExpired:"captchaExpired"},decls:2,vars:0,consts:[["target",""]],template:function(t,r){1&t&&e._UZ(0,"div",null,0)},encapsulation:2});const S=["recaptcha"];function G(s,t){if(1&s&&(e.TgZ(0,"div",3),e._uU(1),e.qZA()),2&s){const r=e.oxw();e.xp6(1),e.hij("",r.msg," ")}}function z(s,t){if(1&s&&(e.TgZ(0,"div",4),e._uU(1),e.qZA()),2&s){const r=e.oxw();e.xp6(1),e.hij("",r.errMsg," ")}}function K(s,t){if(1&s){const r=e.EpF();e.TgZ(0,"form",null,5)(2,"div",6)(3,"label",7),e._uU(4,"Login or email"),e.qZA(),e.TgZ(5,"input",8),e.NdJ("ngModelChange",function(n){e.CHM(r);const c=e.oxw();return e.KtG(c.identifier=n)}),e.qZA()(),e.TgZ(6,"div",6)(7,"bd2-recaptcha",9,10),e.NdJ("captchaResponse",function(n){e.CHM(r);const c=e.oxw();return e.KtG(c.captcha(n))})("captchaExpired",function(){e.CHM(r);const n=e.oxw();return e.KtG(n.captchaExpired())}),e.qZA(),e.TgZ(9,"div",11),e._uU(10," Captcha selection is needed "),e.qZA()(),e.TgZ(11,"button",12),e.NdJ("click",function(){e.CHM(r);const n=e.oxw();return e.KtG(n.request())}),e._uU(12,"Send "),e.qZA()()}if(2&s){const r=e.MAs(1),i=e.oxw();e.xp6(5),e.Q6J("ngModel",i.identifier),e.xp6(2),e.Q6J("site_key",i.captchaSiteKey),e.xp6(2),e.Q6J("hidden",!i.missingCaptcha),e.xp6(2),e.Q6J("disabled",!r.valid)}}class v{constructor(t){this.userService=t,this.requested=!1,this.captchaSiteKey=N.N.captchaSiteKey}ngOnInit(){}captcha(t){this.gRecaptchaResponse=t,t&&(this.missingCaptcha=!1)}captchaExpired(){this.gRecaptchaResponse=null}request(){!this.identifier||""===this.identifier.trim()||(this.msg=void 0,this.errMsg=void 0,this.userService.requestReset(this.identifier,this.gRecaptchaResponse).then(t=>{this.msg="Reset link was sent to "+t,this.requested=!0}).catch(t=>{this.errMsg=t.message?t.message:t,this.gRecaptchaResponse=null,this.recaptcha&&this.recaptcha.reset()}))}}v.\u0275fac=function(t){return new(t||v)(e.Y36(p.K))},v.\u0275cmp=e.Xpm({type:v,selectors:[["bd2-reset-request"]],viewQuery:function(t,r){if(1&t&&e.Gf(S,5),2&t){let i;e.iGM(i=e.CRH())&&(r.recaptcha=i.first)}},decls:6,vars:3,consts:[["class","alert alert-success",4,"ngIf"],["class","alert alert-danger",4,"ngIf"],[4,"ngIf"],[1,"alert","alert-success"],[1,"alert","alert-danger"],["reminderForm","ngForm"],[1,"form-group"],["for","identifier"],["type","text","required","","id","identifier","name","identifier",1,"form-control",3,"ngModel","ngModelChange"],[3,"site_key","captchaResponse","captchaExpired"],["recaptcha",""],[1,"alert","alert-danger",3,"hidden"],["type","submit",1,"btn","btn-primary",3,"disabled","click"]],template:function(t,r){1&t&&(e.TgZ(0,"div")(1,"h3"),e._uU(2,"Forgotten password"),e.qZA(),e.YNc(3,G,2,1,"div",0),e.YNc(4,z,2,1,"div",1),e.YNc(5,K,13,4,"form",2),e.qZA()),2&t&&(e.xp6(3),e.Q6J("ngIf",r.msg),e.xp6(1),e.Q6J("ngIf",r.errMsg),e.xp6(1),e.Q6J("ngIf",!r.requested))},dependencies:[l.O5,a._Y,a.Fj,a.JJ,a.JL,a.Q7,a.On,a.F,f],encapsulation:2});const O=/^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$/,B=/^[a-z]+$/,W=/^[A-Z]+$/,V=/^[0-9]+$/;function U(s){return!!(!s||s.length<8||B.test(s)||W.test(s)||V.test(s))}function I(s){return U(s)?{"password-weak":!0}:null}function k(s){return s.password===s.password2?null:{"password-mismatch":!0}}function P(s){return function L(s){return s=s?s.toLocaleLowerCase():"",!!O.test(s)}(s)?null:{pattern:"Not valid email format"}}function ee(s,t){if(1&s&&(e.TgZ(0,"div",3),e._uU(1),e.qZA()),2&s){const r=e.oxw();e.xp6(1),e.hij("",r.msg," ")}}function te(s,t){if(1&s&&(e.TgZ(0,"div",4),e._uU(1),e.qZA()),2&s){const r=e.oxw();e.xp6(1),e.hij("",r.errMsg," ")}}function re(s,t){if(1&s){const r=e.EpF();e.TgZ(0,"form",null,5)(2,"div",6)(3,"label",7),e._uU(4,"Password"),e.qZA(),e.TgZ(5,"input",8,9),e.NdJ("ngModelChange",function(n){e.CHM(r);const c=e.oxw(2);return e.KtG(c.password=n)}),e.qZA(),e.TgZ(7,"div",10),e._uU(8," Password must be at least 8 long, containing a digit or symbol or capital letter "),e.qZA()(),e.TgZ(9,"div",6)(10,"label",11),e._uU(11,"Repeat password"),e.qZA(),e.TgZ(12,"input",12,13),e.NdJ("ngModelChange",function(n){e.CHM(r);const c=e.oxw(2);return e.KtG(c.password2=n)}),e.qZA(),e.TgZ(14,"div",10),e._uU(15," Passwords do not match "),e.qZA()(),e.TgZ(16,"button",14),e.NdJ("click",function(){e.CHM(r);const n=e.oxw(2);return e.KtG(n.reset())}),e._uU(17,"Reset "),e.qZA()()}if(2&s){const r=e.MAs(1),i=e.MAs(6),n=e.MAs(13),c=e.oxw(2);e.xp6(5),e.Q6J("ngModel",c.password),e.xp6(2),e.Q6J("hidden",i.pristine||!c.weakPassword()),e.xp6(5),e.Q6J("ngModel",c.password2),e.xp6(2),e.Q6J("hidden",n.pristine||c.matching()),e.xp6(2),e.Q6J("disabled",!r.form.valid||c.passwordProblem())}}function se(s,t){if(1&s&&(e.TgZ(0,"div"),e.YNc(1,re,18,5,"form",2),e.qZA()),2&s){const r=e.oxw();e.xp6(1),e.Q6J("ngIf",!r.requested)}}class Z{constructor(t,r){this.userService=t,this.route=r,this.requested=!1}ngOnInit(){this.token=this.route.snapshot.queryParamMap.get("token"),this.token||(this.errMsg="Missing reset token")}reset(){this.token&&this.userService.resetPassword(this.password,this.token).then(t=>{this.msg="You can sign in with new password and login "+t,this.requested=!0}).catch(t=>{this.errMsg=t.message?t.message:t})}weakPassword(){return U(this.password)}matching(){return this.password===this.password2}passwordProblem(){return!(!this.weakPassword()&&this.matching())}}Z.\u0275fac=function(t){return new(t||Z)(e.Y36(p.K),e.Y36(m.gz))},Z.\u0275cmp=e.Xpm({type:Z,selectors:[["ng-component"]],decls:6,vars:3,consts:[["class","alert alert-success",4,"ngIf"],["class","alert alert-danger",4,"ngIf"],[4,"ngIf"],[1,"alert","alert-success"],[1,"alert","alert-danger"],["resetForm","ngForm"],[1,"form-group"],["for","password"],["type","password","required","","minlength","8","id","password","placeholder","new password","name","fPassword",1,"form-control",3,"ngModel","ngModelChange"],["fPassword","ngModel"],[1,"alert","alert-danger",3,"hidden"],["for","password2"],["type","password","id","password2","required","","placeholder","password","name","fPassword2",1,"form-control",3,"ngModel","ngModelChange"],["fPassword2","ngModel"],["type","submit",1,"btn","btn-primary",3,"disabled","click"]],template:function(t,r){1&t&&(e.TgZ(0,"div")(1,"h3"),e._uU(2,"Password reset"),e.qZA(),e.YNc(3,ee,2,1,"div",0),e.YNc(4,te,2,1,"div",1),e.YNc(5,se,2,1,"div",2),e.qZA()),2&t&&(e.xp6(3),e.Q6J("ngIf",r.msg),e.xp6(1),e.Q6J("ngIf",r.errMsg),e.xp6(1),e.Q6J("ngIf",r.token))},dependencies:[l.O5,a._Y,a.Fj,a.JJ,a.JL,a.Q7,a.wO,a.On,a.F],encapsulation:2});var q=o(2160);function ae(s,t){1&s&&(e.TgZ(0,"div",1),e._uU(1,"Use the activation link that was sent in the email"),e.qZA())}class b{constructor(t,r,i,n){this.route=t,this.router=r,this.userService=i,this.feedback=n}ngOnInit(){this.token=this.route.snapshot.queryParamMap.get("token"),this.token&&this.userService.activate(this.token).then(t=>{this.feedback.success("Your account has been activated, use: "+t.login+" to sign in"),this.router.navigate(["/login"])}).catch(t=>{this.feedback.error(t),this.router.navigate(["/login"])})}}b.\u0275fac=function(t){return new(t||b)(e.Y36(m.gz),e.Y36(m.F0),e.Y36(p.K),e.Y36(q.T))},b.\u0275cmp=e.Xpm({type:b,selectors:[["ng-component"]],decls:1,vars:1,consts:[["class","alert alert-danger danger",4,"ngIf"],[1,"alert","alert-danger","danger"]],template:function(t,r){1&t&&e.YNc(0,ae,2,0,"div",0),2&t&&e.Q6J("ngIf",!r.token)},dependencies:[l.O5],encapsulation:2});var T=o(9646),J=o(7246),M=o(262),ie=o(5162),u=o(3546),d=o(9549),A=o(4144),F=o(6709);const ne=["recaptcha"];function oe(s,t){if(1&s&&(e.TgZ(0,"div",3)(1,"h4"),e._uU(2,"Your registration was successful."),e.qZA(),e._uU(3," We have sent you the activation link to "),e.TgZ(4,"strong"),e._uU(5),e.qZA(),e._uU(6,", please use that link before logging in "),e.qZA()),2&s){const r=e.oxw();e.xp6(5),e.Oqu(r.registeredMsg)}}function ce(s,t){1&s&&(e.TgZ(0,"mat-error"),e._uU(1,"Alphanumerical login, min length 5, only numbers, small letters and ._"),e.qZA())}function de(s,t){1&s&&(e.TgZ(0,"mat-error"),e._uU(1,"Such login already exists"),e.qZA())}function ue(s,t){1&s&&(e.TgZ(0,"mat-error"),e._uU(1,"Not valid email format"),e.qZA())}function le(s,t){1&s&&(e.TgZ(0,"mat-error"),e._uU(1,"Address is already being used"),e.qZA())}function me(s,t){1&s&&(e.TgZ(0,"mat-error"),e._uU(1," Academic email is required for the registration. Contact us if your email is not recognized as academic. "),e.qZA())}function pe(s,t){1&s&&(e.TgZ(0,"mat-error"),e._uU(1,"Passwords do not match"),e.qZA())}function he(s,t){if(1&s){const r=e.EpF();e.TgZ(0,"div",4)(1,"form",5,6)(3,"div",7)(4,"mat-form-field",8)(5,"mat-label",9),e._uU(6,"Login"),e.qZA(),e._UZ(7,"input",10),e.TgZ(8,"mat-hint"),e._uU(9,"Alphanumerical login, min length 5"),e.qZA(),e.YNc(10,ce,2,0,"mat-error",11),e.YNc(11,de,2,0,"mat-error",11),e.qZA()(),e.TgZ(12,"div",7)(13,"mat-form-field",8)(14,"mat-label",12),e._uU(15,"Email"),e.qZA(),e._UZ(16,"input",13),e.YNc(17,ue,2,0,"mat-error",11),e.YNc(18,le,2,0,"mat-error",11),e.YNc(19,me,2,0,"mat-error",11),e.qZA()(),e.TgZ(20,"div",14)(21,"div",7)(22,"mat-form-field",8)(23,"mat-label",15),e._uU(24,"Password"),e.qZA(),e._UZ(25,"input",16),e.TgZ(26,"mat-hint"),e._uU(27,"Password must be at least 8 long, containing a digit or symbol or capital letter"),e.qZA(),e.TgZ(28,"mat-error"),e._uU(29,"Password must be at least 8 long, containing a digit or symbol or capital letter"),e.qZA()()(),e.TgZ(30,"div",7)(31,"mat-form-field",8)(32,"mat-label",17),e._uU(33,"Repeat password"),e.qZA(),e._UZ(34,"input",18),e.YNc(35,pe,2,0,"mat-error",11),e.qZA()()(),e.TgZ(36,"div",7)(37,"mat-form-field",8)(38,"mat-label",19),e._uU(39,"First Name"),e.qZA(),e._UZ(40,"input",20),e.qZA()(),e.TgZ(41,"div",7)(42,"mat-form-field",8)(43,"mat-label",21),e._uU(44,"Family Name"),e.qZA(),e._UZ(45,"input",22),e.qZA()(),e.TgZ(46,"div",7)(47,"mat-form-field",8)(48,"mat-label",23),e._uU(49,"Institution"),e.qZA(),e._UZ(50,"input",24),e.qZA()(),e.TgZ(51,"div",7)(52,"bd2-recaptcha",25,26),e.NdJ("captchaExpired",function(){e.CHM(r);const n=e.oxw();return e.KtG(n.captchaExpired())})("captchaResponse",function(n){e.CHM(r);const c=e.oxw();return e.KtG(c.captcha(n))}),e.qZA(),e.TgZ(54,"div",27),e._uU(55," Captcha selection is needed "),e.qZA()(),e.TgZ(56,"div",7)(57,"mat-checkbox",28),e._uU(58," I agree to conditions of "),e.TgZ(59,"span",29)(60,"a",30),e.NdJ("click",function(){e.CHM(r);const n=e.oxw();return e.KtG(n.helpDialog.show("service"))}),e._uU(61,"service"),e.qZA()(),e._uU(62,". And I understand that unless changed my data will be shared 3 years after their registration. "),e.qZA()(),e.TgZ(63,"button",31),e.NdJ("click",function(){e.CHM(r);const n=e.oxw();return e.KtG(n.register())}),e._uU(64," Register "),e.qZA()()()}if(2&s){const r=e.oxw();e.xp6(1),e.Q6J("formGroup",r.userForm),e.xp6(9),e.Q6J("ngIf",r.userNameField.errors&&!r.userNameField.errors["login-taken"]),e.xp6(1),e.Q6J("ngIf",r.userNameField.errors&&r.userNameField.errors["login-taken"]),e.xp6(6),e.Q6J("ngIf",r.emailField.errors&&r.emailField.errors.pattern),e.xp6(1),e.Q6J("ngIf",r.emailField.errors&&r.emailField.errors["email-taken"]),e.xp6(1),e.Q6J("ngIf",r.emailField.errors&&r.emailField.errors["email-nonacademic"]),e.xp6(16),e.Q6J("ngIf",r.passwordsGroup.errors),e.xp6(17),e.Q6J("site_key",r.captchaSiteKey),e.xp6(2),e.Q6J("hidden",!r.missingCaptcha),e.xp6(9),e.Q6J("disabled",r.blocked||r.userForm.invalid)}}class x{constructor(t,r,i,n){this.userService=t,this.feedback=r,this.fb=i,this.helpDialog=n,this.blocked=!1,this.missingCaptcha=!1,this.captchaSiteKey=N.N.captchaSiteKey}ngOnInit(){this.userForm=this.fb.group({username:[void 0,{validators:[a.kI.required],asyncValidators:t=>this.availableLogin(t.value),updateOn:"blur"}],email:[void 0,{validators:[a.kI.required,t=>P(t.value)],asyncValidators:t=>this.suitableEmail(t.value),updateOn:"blur"}],passwords:this.fb.group({password:[void 0,[a.kI.required,t=>I(t.value)]],password2:[void 0,[a.kI.required]]},{validator:t=>k(t.value)}),firstName:[void 0,[a.kI.required]],lastName:[void 0,[a.kI.required]],institution:[void 0,[a.kI.required]],terms:[void 0,[a.kI.required]]}),this.userNameField=this.userForm.get("username"),this.emailField=this.userForm.get("email"),this.passwordField=this.userForm.get("passwords.password"),this.password2Field=this.userForm.get("passwords.password2"),this.passwordsGroup=this.userForm.get("passwords")}captcha(t){this.gRecaptchaResponse=t,t&&(this.missingCaptcha=!1)}captchaExpired(){this.gRecaptchaResponse=null}availableLogin(t){return!t||t.length<5?(0,T.of)({"too-short":!0}):this.userService.availableLogin(t).pipe((0,J.U)(r=>r?null:{"login-taken":"User "+t+" already exists"}),(0,M.K)(r=>(this.feedback.error(r),(0,T.of)({"cannot-connect":!0}))))}suitableEmail(t){return this.userService.suitableEmail(t).pipe((0,J.U)(r=>{if(r.isFree&&r.isAcademic)return null;const i={};return r.isFree||(i["email-taken"]="Email: "+t+" is already being used"),r.isAcademic||(i["email-nonacademic"]="Academic email is required for the registration. Contact us if your email is not recognized as academic."),i}),(0,M.K)(r=>(this.feedback.error(r),(0,T.of)({"cannot-connect":!0}))))}register(){if(this.userForm.valid){if(!this.gRecaptchaResponse&&!this.emailField.value.endsWith(".cn")&&!this.emailField.value.endsWith(".tw"))return void(this.missingCaptcha=!0);const t=this.makeUserData(this.userForm.value);this.triggerRegistration(t)}}makeUserData(t){return{login:t.username,password:t.passwords.password,email:t.email,firstName:t.firstName,lastName:t.lastName,institution:t.institution,terms:t.terms,g_recaptcha_response:this.gRecaptchaResponse}}triggerRegistration(t){this.userService.register(t).then(r=>{this.registered=!0,this.registeredMsg=r.email,this.feedback.success("Registration successful")}).catch(r=>{this.feedback.error(r),this.gRecaptchaResponse=void 0,this.recaptcha&&this.recaptcha.reset()})}}x.\u0275fac=function(t){return new(t||x)(e.Y36(p.K),e.Y36(q.T),e.Y36(a.QS),e.Y36(ie.l))},x.\u0275cmp=e.Xpm({type:x,selectors:[["ng-component"]],viewQuery:function(t,r){if(1&t&&e.Gf(ne,5),2&t){let i;e.iGM(i=e.CRH())&&(r.recaptcha=i.first)}},decls:7,vars:2,consts:[[1,"mb-2"],["class","alert alert-success",4,"ngIf"],["class","mb-4",4,"ngIf"],[1,"alert","alert-success"],[1,"mb-4"],[3,"formGroup"],["registrationForm",""],[1,"form-group"],[1,"w-100"],["for","username"],["matInput","","formControlName","username","id","username","minlength","5","pattern","[0-9|a-z|\\._]+","placeholder","choose login","required","","type","text",1,""],[4,"ngIf"],["for","email"],["matInput","","formControlName","email","id","email","placeholder","your email","required","","type","text",1,""],["formGroupName","passwords"],["for","password"],["matInput","","formControlName","password","id","password","minlength","8","placeholder","password","required","","type","password",1,""],["for","password2"],["matInput","","formControlName","password2","id","password2","minlength","8","placeholder","password","required","","type","password",1,""],["for","firstName"],["matInput","","formControlName","firstName","id","firstName","minlength","2","placeholder","e.g. Charles","required","","type","text",1,""],["for","lastName"],["matInput","","formControlName","lastName","id","lastName","minlength","2","placeholder","e.g. Darwin","required","","type","text",1,""],["for","institution"],["matInput","","formControlName","institution","id","institution","minlength","3","placeholder","e.g. University of Edinburgh","required","","type","text",1,""],[3,"site_key","captchaExpired","captchaResponse"],["recaptcha",""],[1,"alert","alert-danger",3,"hidden"],["formControlName","terms","id","terms","required","","type","checkbox"],[2,"font-weight","bold","text-decoration","underline"],["role","button",3,"click"],["type","button",1,"btn","btn-primary",3,"disabled","click"]],template:function(t,r){1&t&&(e.TgZ(0,"mat-card",0)(1,"mat-card-title")(2,"h2"),e._uU(3,"User registration"),e.qZA()(),e.TgZ(4,"mat-card-content"),e.YNc(5,oe,7,1,"div",1),e.YNc(6,he,65,10,"div",2),e.qZA()()),2&t&&(e.xp6(5),e.Q6J("ngIf",r.registered),e.xp6(1),e.Q6J("ngIf",!r.registered))},dependencies:[l.O5,a._Y,a.Fj,a.JJ,a.JL,a.Q7,a.wO,a.c5,a.sg,a.u,a.x0,f,u.a8,u.dn,u.n5,d.TO,d.KE,d.bx,d.hX,A.Nt,F.oG,F.e_],encapsulation:2});var Q=o(2805);function fe(s,t){1&s&&(e.TgZ(0,"div",21),e._uU(1,"Account has been updated"),e.qZA())}function ge(s,t){if(1&s){const r=e.EpF();e.TgZ(0,"mat-card",1)(1,"mat-card-title")(2,"h2"),e._uU(3),e.qZA()(),e.TgZ(4,"mat-card-content"),e.YNc(5,fe,2,0,"div",2),e.TgZ(6,"form",3,4)(8,"div",5)(9,"mat-form-field",6)(10,"mat-label",7),e._uU(11,"Login"),e.qZA(),e._UZ(12,"input",8),e.qZA()(),e.TgZ(13,"div",5)(14,"a",9),e._uU(15,"Change password"),e.qZA()(),e.TgZ(16,"div",5)(17,"mat-form-field",6)(18,"mat-label",10),e._uU(19,"Email"),e.qZA(),e._UZ(20,"input",11),e.TgZ(21,"mat-error"),e._uU(22,"Not valid email format"),e.qZA()()(),e.TgZ(23,"div",5)(24,"mat-form-field",6)(25,"mat-label",12),e._uU(26,"First Name"),e.qZA(),e._UZ(27,"input",13),e.qZA()(),e.TgZ(28,"div",5)(29,"mat-form-field",6)(30,"mat-label",14),e._uU(31,"Family Name"),e.qZA(),e._UZ(32,"input",15),e.qZA()(),e.TgZ(33,"div",5)(34,"mat-form-field",6)(35,"mat-label",16),e._uU(36,"Institution"),e.qZA(),e._UZ(37,"input",17),e.qZA()(),e.TgZ(38,"div",5)(39,"mat-form-field",6)(40,"mat-label",18),e._uU(41,"Current password"),e.qZA(),e._UZ(42,"input",19),e.qZA()(),e.TgZ(43,"button",20),e.NdJ("click",function(){e.CHM(r);const n=e.oxw();return e.KtG(n.save())}),e._uU(44," Update "),e.qZA()()()()}if(2&s){const r=e.oxw();e.xp6(3),e.hij("Edit user: ",r.user.name,""),e.xp6(2),e.Q6J("ngIf",r.updated),e.xp6(1),e.Q6J("formGroup",r.userForm),e.xp6(37),e.Q6J("disabled",r.userForm.invalid)}}class C{constructor(t,r,i){this.userService=t,this.feedback=r,this.fb=i,this.updated=!1}ngOnInit(){this.user=this.userService.currentUser,this.userForm=this.fb.group({username:[{value:this.user.login,disabled:!0},[a.kI.required],[]],email:[this.user.email,[a.kI.required,t=>P(t.value)],[]],firstName:[this.user.firstName,[a.kI.required]],lastName:[this.user.lastName,[a.kI.required]],institution:[this.user.institution,[a.kI.required]],currentPassword:[void 0,[a.kI.required]]}),this.userNameField=this.userForm.get("username"),this.emailField=this.userForm.get("email"),this.currentPasswordField=this.userForm.get("currentPassword")}save(){if(!this.userForm.valid)return;const t=this.userForm.value,r={login:this.user.login,currentPassword:t.currentPassword,email:t.email,firstName:t.firstName,lastName:t.lastName,institution:t.institution};this.updated=!1,this.userService.update(r).subscribe(i=>{this.user=i,this.currentPasswordField.reset(),this.feedback.success("User: "+i.login+" has been updated"),this.updated=!0,(0,Q.H)(5e3).subscribe(()=>this.updated=!1)},i=>{this.feedback.error(i),this.currentPasswordField.reset()})}}function _e(s,t){1&s&&(e.TgZ(0,"div",18),e._uU(1,"Password has been updated"),e.qZA())}function we(s,t){1&s&&(e.TgZ(0,"mat-error"),e._uU(1,"Passwords do not match"),e.qZA())}function ve(s,t){if(1&s){const r=e.EpF();e.TgZ(0,"mat-card",1)(1,"mat-card-title")(2,"h2"),e._uU(3),e.qZA()(),e.TgZ(4,"mat-card-content"),e.YNc(5,_e,2,0,"div",2),e.TgZ(6,"form",3,4)(8,"div",5)(9,"mat-form-field",6)(10,"mat-label",7),e._uU(11,"Login"),e.qZA(),e._UZ(12,"input",8),e.qZA()(),e.TgZ(13,"div",9)(14,"div",5)(15,"mat-form-field",6)(16,"mat-label",10),e._uU(17,"Password"),e.qZA(),e._UZ(18,"input",11),e.TgZ(19,"mat-hint"),e._uU(20,"Password must be at least 8 long, containing a digit or symbol or capital letter"),e.qZA(),e.TgZ(21,"mat-error"),e._uU(22,"Password must be at least 8 long, containing a digit or symbol or capital letter"),e.qZA()()(),e.TgZ(23,"div",5)(24,"mat-form-field",6)(25,"mat-label",12),e._uU(26,"Repeat password"),e.qZA(),e._UZ(27,"input",13),e.YNc(28,we,2,0,"mat-error",14),e.qZA()()(),e.TgZ(29,"div",5)(30,"mat-form-field",6)(31,"mat-label",15),e._uU(32,"Current password"),e.qZA(),e._UZ(33,"input",16),e.qZA()(),e.TgZ(34,"button",17),e.NdJ("click",function(){e.CHM(r);const n=e.oxw();return e.KtG(n.save())}),e._uU(35," Update "),e.qZA()()()()}if(2&s){const r=e.oxw();e.xp6(3),e.hij("Change password for ",r.user.name,""),e.xp6(2),e.Q6J("ngIf",r.updated),e.xp6(1),e.Q6J("formGroup",r.userForm),e.xp6(22),e.Q6J("ngIf",r.passwordsGroup.errors),e.xp6(6),e.Q6J("disabled",r.userForm.invalid)}}C.\u0275fac=function(t){return new(t||C)(e.Y36(p.K),e.Y36(q.T),e.Y36(a.QS))},C.\u0275cmp=e.Xpm({type:C,selectors:[["ng-component"]],decls:1,vars:1,consts:[["class","mb-2",4,"ngIf"],[1,"mb-2"],["class","alert alert-success",4,"ngIf"],[3,"formGroup"],["registrationForm",""],[1,"form-group"],[1,"w-100"],["for","username"],["matInput","","formControlName","username","id","username","required","","type","text",1,""],["routerLink","/account/password"],["for","email"],["matInput","","formControlName","email","id","email","placeholder","your email","required","","type","text",1,""],["for","firstName"],["matInput","","formControlName","firstName","id","firstName","minlength","2","placeholder","e.g. Charles","required","","type","text",1,""],["for","lastName"],["matInput","","formControlName","lastName","id","lastName","minlength","2","placeholder","e.g. Darwin","required","","type","text",1,""],["for","institution"],["matInput","","formControlName","institution","id","institution","placeholder","e.g. University of Edinburgh","required","","type","text",1,""],["for","cPassword"],["matInput","","formControlName","currentPassword","id","cPassword","placeholder","current password","required","","type","password",1,""],["type","button",1,"btn","btn-primary",3,"disabled","click"],[1,"alert","alert-success"]],template:function(t,r){1&t&&e.YNc(0,ge,45,4,"mat-card",0),2&t&&e.Q6J("ngIf",r.user&&!r.user.anonymous)},dependencies:[l.O5,a._Y,a.Fj,a.JJ,a.JL,a.Q7,a.wO,a.sg,a.u,m.rH,u.a8,u.dn,u.n5,d.TO,d.KE,d.hX,A.Nt],encapsulation:2});class R{constructor(t,r,i){this.userService=t,this.feedback=r,this.fb=i,this.updated=!1}ngOnInit(){this.user=this.userService.currentUser,this.userForm=this.fb.group({username:[{value:this.user.login,disabled:!0},[a.kI.required],[]],passwords:this.fb.group({password:[void 0,[a.kI.required,t=>I(t.value)]],password2:[void 0,[a.kI.required]]},{validator:t=>k(t.value)}),currentPassword:[void 0,[a.kI.required]]}),this.passwordsGroup=this.userForm.get("passwords")}save(){if(!this.userForm.valid)return;const t=this.userForm.value,r={login:this.user.login,currentPassword:t.currentPassword,password:t.passwords.password};this.updated=!1,this.userService.passwordUpdate(r).subscribe(i=>{this.user=i,this.userForm.reset(),this.feedback.success("User: "+i.login+" password has been updated"),this.updated=!0,(0,Q.H)(5e3).subscribe(()=>this.updated=!1)},i=>{this.userForm.reset(),this.feedback.error(i)})}}R.\u0275fac=function(t){return new(t||R)(e.Y36(p.K),e.Y36(q.T),e.Y36(a.QS))},R.\u0275cmp=e.Xpm({type:R,selectors:[["ng-component"]],decls:1,vars:1,consts:[["class","mb-2",4,"ngIf"],[1,"mb-2"],["class","alert alert-success",4,"ngIf"],[3,"formGroup"],["registrationForm",""],[1,"form-group"],[1,"w-100"],["for","username"],["matInput","","formControlName","username","id","username","required","","type","text",1,""],["formGroupName","passwords"],["for","password"],["matInput","","formControlName","password","id","password","minlength","8","placeholder","password","required","","type","password",1,""],["for","password2"],["matInput","","formControlName","password2","id","password2","minlength","8","placeholder","password","required","","type","password",1,""],[4,"ngIf"],["for","cPassword"],["matInput","","formControlName","currentPassword","id","cPassword","placeholder","current password","required","","type","password",1,""],["type","button",1,"btn","btn-primary",3,"disabled","click"],[1,"alert","alert-success"]],template:function(t,r){1&t&&e.YNc(0,ve,36,5,"mat-card",0),2&t&&e.Q6J("ngIf",r.user&&!r.user.anonymous)},dependencies:[l.O5,a._Y,a.Fj,a.JJ,a.JL,a.Q7,a.wO,a.sg,a.u,a.x0,u.a8,u.dn,u.n5,d.TO,d.KE,d.bx,d.hX,A.Nt],encapsulation:2});const Ze=[{path:"",children:[{path:"edit",component:C},{path:"password",component:R},{path:"register",component:x},{path:"activate",component:b},{path:"remind",component:v},{path:"reset",component:Z}]}];class g{}g.\u0275fac=function(t){return new(t||g)},g.\u0275mod=e.oAB({type:g}),g.\u0275inj=e.cJS({imports:[m.Bz.forChild(Ze),m.Bz]});class _{}_.\u0275fac=function(t){return new(t||_)},_.\u0275mod=e.oAB({type:_}),_.\u0275inj=e.cJS({});var be=o(1585),xe=o(4850),Ce=o(7392);class w{}w.\u0275fac=function(t){return new(t||w)},w.\u0275mod=e.oAB({type:w}),w.\u0275inj=e.cJS({imports:[l.ez,a.u5,a.UX,_,be.i,g,u.QW,xe.t,d.lN,Ce.Ps,A.c,F.p9]})}}]);