!function(){function e(e,t){if(!(e instanceof t))throw new TypeError("Cannot call a class as a function")}function t(e,t){for(var r=0;r<t.length;r++){var a=t[r];a.enumerable=a.enumerable||!1,a.configurable=!0,"value"in a&&(a.writable=!0),Object.defineProperty(e,a.key,a)}}function r(e,r,a){return r&&t(e.prototype,r),a&&t(e,a),e}(window.webpackJsonp=window.webpackJsonp||[]).push([[15],{jcJX:function(t,a,i){"use strict";i.r(a),i.d(a,"AccountModule",(function(){return ke}));var s,n,o=i("ofXK"),c=i("tyNb"),d=i("AytR"),u=i("fXoL"),l=i("naqj"),b=i("3Pt+"),m=i("2Vo4"),p=((s=function(){function t(r){var a=this;e(this,t),this.scriptLoaded=!1,this.readySubject=new m.a(!1),window.reCaptchaOnloadCallback=function(){return r.run(a.onloadCallback.bind(a))}}return r(t,[{key:"getReady",value:function(e){if(!this.scriptLoaded){this.scriptLoaded=!0;var t=document.body,r=document.createElement("script");r.innerHTML="",r.src="https://www.google.com/recaptcha/api.js?onload=reCaptchaOnloadCallback&render=explicit"+(e?"&hl="+e:""),r.async=!0,r.defer=!0,t.appendChild(r)}return this.readySubject.asObservable()}},{key:"onloadCallback",value:function(){this.readySubject.next(!0)}}]),t}()).\u0275fac=function(e){return new(e||s)(u.Xb(u.B))},s.\u0275prov=u.Jb({token:s,factory:s.\u0275fac,providedIn:"root"}),s),f=["target"],h=((n=function(){function t(r,a){e(this,t),this._zone=r,this._captchaService=a,this.site_key=null,this.theme="light",this.type="image",this.size="normal",this.tabindex=0,this.language=null,this.captchaResponse=new u.o,this.captchaExpired=new u.o,this.widgetId=null}return r(t,[{key:"ngOnInit",value:function(){var e=this;this._captchaService.getReady(this.language).subscribe((function(t){t&&(e.widgetId=window.grecaptcha.render(e.targetRef.nativeElement,{sitekey:e.site_key,theme:e.theme,type:e.type,size:e.size,tabindex:e.tabindex,callback:function(t){return e._zone.run(e.recaptchaCallback.bind(e,t))},"expired-callback":function(){return e._zone.run(e.recaptchaExpiredCallback.bind(e))}}))}))}},{key:"reset",value:function(){null!==this.widgetId&&window.grecaptcha.reset(this.widgetId)}},{key:"getResponse",value:function(){return this.widgetId?window.grecaptcha.getResponse(this.targetRef.nativeElement):null}},{key:"recaptchaCallback",value:function(e){this.captchaResponse.emit(e)}},{key:"recaptchaExpiredCallback",value:function(){this.captchaExpired.emit()}}]),t}()).\u0275fac=function(e){return new(e||n)(u.Nb(u.B),u.Nb(p))},n.\u0275cmp=u.Hb({type:n,selectors:[["bd2-recaptcha"]],viewQuery:function(e,t){var r;1&e&&u.xc(f,!0),2&e&&u.pc(r=u.ac())&&(t.targetRef=r.first)},inputs:{site_key:"site_key",theme:"theme",type:"type",size:"size",tabindex:"tabindex",language:"language"},outputs:{captchaResponse:"captchaResponse",captchaExpired:"captchaExpired"},decls:2,vars:0,consts:[["target",""]],template:function(e,t){1&e&&u.Ob(0,"div",null,0)},encapsulation:2}),n),g=["recaptcha"];function v(e,t){if(1&e&&(u.Tb(0,"div",3),u.Dc(1),u.Sb()),2&e){var r=u.dc();u.Bb(1),u.Fc("",r.msg," ")}}function w(e,t){if(1&e&&(u.Tb(0,"div",4),u.Dc(1),u.Sb()),2&e){var r=u.dc();u.Bb(1),u.Fc("",r.errMsg," ")}}function S(e,t){if(1&e){var r=u.Ub();u.Tb(0,"form",null,5),u.Tb(2,"div",6),u.Tb(3,"label",7),u.Dc(4,"Login or email"),u.Sb(),u.Tb(5,"input",8),u.Zb("ngModelChange",(function(e){return u.sc(r),u.dc().identifier=e})),u.Sb(),u.Sb(),u.Tb(6,"div",6),u.Tb(7,"bd2-recaptcha",9,10),u.Zb("captchaResponse",(function(e){return u.sc(r),u.dc().captcha(e)}))("captchaExpired",(function(){return u.sc(r),u.dc().captchaExpired()})),u.Sb(),u.Tb(9,"div",11),u.Dc(10," Captcha selection is needed "),u.Sb(),u.Sb(),u.Tb(11,"button",12),u.Zb("click",(function(){return u.sc(r),u.dc().request()})),u.Dc(12,"Send "),u.Sb(),u.Sb()}if(2&e){var a=u.qc(1),i=u.dc();u.Bb(5),u.jc("ngModel",i.identifier),u.Bb(2),u.jc("site_key",i.captchaSiteKey),u.Bb(2),u.jc("hidden",!i.missingCaptcha),u.Bb(2),u.jc("disabled",!a.valid)}}var y,T=((y=function(){function t(r){e(this,t),this.userService=r,this.requested=!1,this.captchaSiteKey=d.a.captchaSiteKey}return r(t,[{key:"ngOnInit",value:function(){}},{key:"captcha",value:function(e){this.gRecaptchaResponse=e,e&&(this.missingCaptcha=!1)}},{key:"captchaExpired",value:function(){this.gRecaptchaResponse=null}},{key:"request",value:function(){var e=this;this.identifier&&""!==this.identifier.trim()&&(this.msg=void 0,this.errMsg=void 0,this.userService.requestReset(this.identifier,this.gRecaptchaResponse).then((function(t){e.msg="Reset link was sent to "+t,e.requested=!0})).catch((function(t){e.errMsg=t.message?t.message:t,e.gRecaptchaResponse=null,e.recaptcha&&e.recaptcha.reset()})))}}]),t}()).\u0275fac=function(e){return new(e||y)(u.Nb(l.a))},y.\u0275cmp=u.Hb({type:y,selectors:[["bd2-reset-request"]],viewQuery:function(e,t){var r;1&e&&u.Ic(g,!0),2&e&&u.pc(r=u.ac())&&(t.recaptcha=r.first)},decls:6,vars:3,consts:[["class","alert alert-success",4,"ngIf"],["class","alert alert-danger",4,"ngIf"],[4,"ngIf"],[1,"alert","alert-success"],[1,"alert","alert-danger"],["reminderForm","ngForm"],[1,"form-group"],["for","identifier"],["type","text","required","","id","identifier","name","identifier",1,"form-control",3,"ngModel","ngModelChange"],[3,"site_key","captchaResponse","captchaExpired"],["recaptcha",""],[1,"alert","alert-danger",3,"hidden"],["type","submit",1,"btn","btn-primary",3,"disabled","click"]],template:function(e,t){1&e&&(u.Tb(0,"div"),u.Tb(1,"h3"),u.Dc(2,"Forgotten password"),u.Sb(),u.Bc(3,v,2,1,"div",0),u.Bc(4,w,2,1,"div",1),u.Bc(5,S,13,4,"form",2),u.Sb()),2&e&&(u.Bb(3),u.jc("ngIf",t.msg),u.Bb(1),u.jc("ngIf",t.errMsg),u.Bb(1),u.jc("ngIf",!t.requested))},directives:[o.m,b.D,b.r,b.s,b.d,b.z,b.q,b.t,h],encapsulation:2}),y),k=/^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$/,B=/^[a-z]+$/,N=/^[A-Z]+$/,D=/^[0-9]+$/;function I(e){return!e||e.length<8||!!B.test(e)||!!N.test(e)||!!D.test(e)}function F(e){return I(e)?{"password-weak":!0}:null}function q(e){return e.password===e.password2?null:{"password-mismatch":!0}}function j(e){return t=(t=e)?t.toLocaleLowerCase():"",k.test(t)?null:{pattern:"Not valid email format"};var t}function C(e,t){if(1&e&&(u.Tb(0,"div",3),u.Dc(1),u.Sb()),2&e){var r=u.dc();u.Bb(1),u.Fc("",r.msg," ")}}function R(e,t){if(1&e&&(u.Tb(0,"div",4),u.Dc(1),u.Sb()),2&e){var r=u.dc();u.Bb(1),u.Fc("",r.errMsg," ")}}function P(e,t){if(1&e){var r=u.Ub();u.Tb(0,"form",null,5),u.Tb(2,"div",6),u.Tb(3,"label",7),u.Dc(4,"Password"),u.Sb(),u.Tb(5,"input",8,9),u.Zb("ngModelChange",(function(e){return u.sc(r),u.dc(2).password=e})),u.Sb(),u.Tb(7,"div",10),u.Dc(8," Password must be at least 8 long, containing a digit or symbol or capital letter "),u.Sb(),u.Sb(),u.Tb(9,"div",6),u.Tb(10,"label",11),u.Dc(11,"Repeat password"),u.Sb(),u.Tb(12,"input",12,13),u.Zb("ngModelChange",(function(e){return u.sc(r),u.dc(2).password2=e})),u.Sb(),u.Tb(14,"div",10),u.Dc(15," Passwords do not match "),u.Sb(),u.Sb(),u.Tb(16,"button",14),u.Zb("click",(function(){return u.sc(r),u.dc(2).reset()})),u.Dc(17,"Reset "),u.Sb(),u.Sb()}if(2&e){var a=u.qc(1),i=u.qc(6),s=u.qc(13),n=u.dc(2);u.Bb(5),u.jc("ngModel",n.password),u.Bb(2),u.jc("hidden",i.pristine||!n.weakPassword()),u.Bb(5),u.jc("ngModel",n.password2),u.Bb(2),u.jc("hidden",s.pristine||n.matching()),u.Bb(2),u.jc("disabled",!a.form.valid||n.passwordProblem())}}function x(e,t){if(1&e&&(u.Tb(0,"div"),u.Bc(1,P,18,5,"form",2),u.Sb()),2&e){var r=u.dc();u.Bb(1),u.jc("ngIf",!r.requested)}}var O,M=((O=function(){function t(r,a){e(this,t),this.userService=r,this.route=a,this.requested=!1}return r(t,[{key:"ngOnInit",value:function(){this.token=this.route.snapshot.queryParamMap.get("token"),this.token||(this.errMsg="Missing reset token")}},{key:"reset",value:function(){var e=this;this.token&&this.userService.resetPassword(this.password,this.token).then((function(t){e.msg="You can sign in with new password and login "+t,e.requested=!0})).catch((function(t){e.errMsg=t.message?t.message:t}))}},{key:"weakPassword",value:function(){return I(this.password)}},{key:"matching",value:function(){return this.password===this.password2}},{key:"passwordProblem",value:function(){return!!this.weakPassword()||!this.matching()}}]),t}()).\u0275fac=function(e){return new(e||O)(u.Nb(l.a),u.Nb(c.a))},O.\u0275cmp=u.Hb({type:O,selectors:[["ng-component"]],decls:6,vars:3,consts:[["class","alert alert-success",4,"ngIf"],["class","alert alert-danger",4,"ngIf"],[4,"ngIf"],[1,"alert","alert-success"],[1,"alert","alert-danger"],["resetForm","ngForm"],[1,"form-group"],["for","password"],["type","password","required","","minlength","8","id","password","placeholder","new password","name","fPassword",1,"form-control",3,"ngModel","ngModelChange"],["fPassword","ngModel"],[1,"alert","alert-danger",3,"hidden"],["for","password2"],["type","password","id","password2","required","","placeholder","password","name","fPassword2",1,"form-control",3,"ngModel","ngModelChange"],["fPassword2","ngModel"],["type","submit",1,"btn","btn-primary",3,"disabled","click"]],template:function(e,t){1&e&&(u.Tb(0,"div"),u.Tb(1,"h3"),u.Dc(2,"Password reset"),u.Sb(),u.Bc(3,C,2,1,"div",0),u.Bc(4,R,2,1,"div",1),u.Bc(5,x,2,1,"div",2),u.Sb()),2&e&&(u.Bb(3),u.jc("ngIf",t.msg),u.Bb(1),u.jc("ngIf",t.errMsg),u.Bb(1),u.jc("ngIf",t.token))},directives:[o.m,b.D,b.r,b.s,b.d,b.z,b.m,b.q,b.t],encapsulation:2}),O),E=i("6tuW");function z(e,t){1&e&&(u.Tb(0,"div",1),u.Dc(1,"Use the activation link that was sent in the email"),u.Sb())}var L,U=((L=function(){function t(r,a,i,s){e(this,t),this.route=r,this.router=a,this.userService=i,this.feedback=s}return r(t,[{key:"ngOnInit",value:function(){var e=this;this.token=this.route.snapshot.queryParamMap.get("token"),this.token&&this.userService.activate(this.token).then((function(t){e.feedback.success("Your account has been activated, use: "+t.login+" to sign in"),e.router.navigate(["/login"])})).catch((function(t){e.feedback.error(t),e.router.navigate(["/login"])}))}}]),t}()).\u0275fac=function(e){return new(e||L)(u.Nb(c.a),u.Nb(c.c),u.Nb(l.a),u.Nb(E.a))},L.\u0275cmp=u.Hb({type:L,selectors:[["ng-component"]],decls:1,vars:1,consts:[["class","alert alert-danger danger",4,"ngIf"],[1,"alert","alert-danger","danger"]],template:function(e,t){1&e&&u.Bc(0,z,2,0,"div",0),2&e&&u.jc("ngIf",!t.token)},directives:[o.m],encapsulation:2}),L),_=i("LRne"),G=i("lJxs"),A=i("JIr8"),Z=i("Ee+/"),K=i("Wp6s"),H=i("kmnG"),J=i("qFsG"),$=i("bSwM"),W=["recaptcha"];function X(e,t){if(1&e&&(u.Tb(0,"div",3),u.Tb(1,"h4"),u.Dc(2,"Your registration was successful."),u.Sb(),u.Dc(3," Try to login now or check your email ("),u.Tb(4,"strong"),u.Dc(5),u.Sb(),u.Dc(6,") for the activation link. "),u.Sb()),2&e){var r=u.dc();u.Bb(5),u.Ec(r.registeredMsg)}}function Y(e,t){1&e&&(u.Tb(0,"mat-error"),u.Dc(1,"Alphanumerical login, min length 5, only numbers, small letters and ._"),u.Sb())}function Q(e,t){1&e&&(u.Tb(0,"mat-error"),u.Dc(1,"Such login already exists"),u.Sb())}function V(e,t){1&e&&(u.Tb(0,"mat-error"),u.Dc(1,"Not valid email format"),u.Sb())}function ee(e,t){1&e&&(u.Tb(0,"mat-error"),u.Dc(1,"Address is already being used"),u.Sb())}function te(e,t){1&e&&(u.Tb(0,"mat-error"),u.Dc(1," Academic email is required for the registration. Contact us if your email is not recognized as academic. "),u.Sb())}function re(e,t){1&e&&(u.Tb(0,"mat-error"),u.Dc(1,"Passwords do not match"),u.Sb())}function ae(e,t){if(1&e){var r=u.Ub();u.Tb(0,"div",4),u.Tb(1,"form",5,6),u.Tb(3,"div",7),u.Tb(4,"mat-form-field",8),u.Tb(5,"mat-label",9),u.Dc(6,"Login"),u.Sb(),u.Ob(7,"input",10),u.Tb(8,"mat-hint"),u.Dc(9,"Alphanumerical login, min length 5"),u.Sb(),u.Bc(10,Y,2,0,"mat-error",11),u.Bc(11,Q,2,0,"mat-error",11),u.Sb(),u.Sb(),u.Tb(12,"div",7),u.Tb(13,"mat-form-field",8),u.Tb(14,"mat-label",12),u.Dc(15,"Email"),u.Sb(),u.Ob(16,"input",13),u.Bc(17,V,2,0,"mat-error",11),u.Bc(18,ee,2,0,"mat-error",11),u.Bc(19,te,2,0,"mat-error",11),u.Sb(),u.Sb(),u.Tb(20,"div",14),u.Tb(21,"div",7),u.Tb(22,"mat-form-field",8),u.Tb(23,"mat-label",15),u.Dc(24,"Password"),u.Sb(),u.Ob(25,"input",16),u.Tb(26,"mat-hint"),u.Dc(27,"Password must be at least 8 long, containing a digit or symbol or capital letter"),u.Sb(),u.Tb(28,"mat-error"),u.Dc(29,"Password must be at least 8 long, containing a digit or symbol or capital letter"),u.Sb(),u.Sb(),u.Sb(),u.Tb(30,"div",7),u.Tb(31,"mat-form-field",8),u.Tb(32,"mat-label",17),u.Dc(33,"Repeat password"),u.Sb(),u.Ob(34,"input",18),u.Bc(35,re,2,0,"mat-error",11),u.Sb(),u.Sb(),u.Sb(),u.Tb(36,"div",7),u.Tb(37,"mat-form-field",8),u.Tb(38,"mat-label",19),u.Dc(39,"First Name"),u.Sb(),u.Ob(40,"input",20),u.Sb(),u.Sb(),u.Tb(41,"div",7),u.Tb(42,"mat-form-field",8),u.Tb(43,"mat-label",21),u.Dc(44,"Family Name"),u.Sb(),u.Ob(45,"input",22),u.Sb(),u.Sb(),u.Tb(46,"div",7),u.Tb(47,"mat-form-field",8),u.Tb(48,"mat-label",23),u.Dc(49,"Institution"),u.Sb(),u.Ob(50,"input",24),u.Sb(),u.Sb(),u.Tb(51,"div",7),u.Tb(52,"bd2-recaptcha",25,26),u.Zb("captchaExpired",(function(){return u.sc(r),u.dc().captchaExpired()}))("captchaResponse",(function(e){return u.sc(r),u.dc().captcha(e)})),u.Sb(),u.Tb(54,"div",27),u.Dc(55," Captcha selection is needed "),u.Sb(),u.Sb(),u.Tb(56,"div",7),u.Tb(57,"mat-checkbox",28),u.Dc(58," I agree to conditions of "),u.Tb(59,"span",29),u.Tb(60,"a",30),u.Zb("click",(function(){return u.sc(r),u.dc().helpDialog.show("service")})),u.Dc(61,"service"),u.Sb(),u.Sb(),u.Dc(62,". And I understand that unless changed my data will be shared 3 years after their registration. "),u.Sb(),u.Sb(),u.Tb(63,"button",31),u.Zb("click",(function(){return u.sc(r),u.dc().register()})),u.Dc(64," Register "),u.Sb(),u.Sb(),u.Sb()}if(2&e){var a=u.dc();u.Bb(1),u.jc("formGroup",a.userForm),u.Bb(9),u.jc("ngIf",a.userNameField.errors&&!a.userNameField.errors["login-taken"]),u.Bb(1),u.jc("ngIf",a.userNameField.errors&&a.userNameField.errors["login-taken"]),u.Bb(6),u.jc("ngIf",a.emailField.errors&&a.emailField.errors.pattern),u.Bb(1),u.jc("ngIf",a.emailField.errors&&a.emailField.errors["email-taken"]),u.Bb(1),u.jc("ngIf",a.emailField.errors&&a.emailField.errors["email-nonacademic"]),u.Bb(16),u.jc("ngIf",a.passwordsGroup.errors),u.Bb(17),u.jc("site_key",a.captchaSiteKey),u.Bb(2),u.jc("hidden",!a.missingCaptcha),u.Bb(9),u.jc("disabled",a.blocked||a.userForm.invalid)}}var ie,se=((ie=function(){function t(r,a,i,s){e(this,t),this.userService=r,this.feedback=a,this.fb=i,this.helpDialog=s,this.blocked=!1,this.missingCaptcha=!1,this.captchaSiteKey=d.a.captchaSiteKey}return r(t,[{key:"ngOnInit",value:function(){var e=this;this.userForm=this.fb.group({username:[void 0,{validators:[b.B.required],asyncValidators:function(t){return e.availableLogin(t.value)},updateOn:"blur"}],email:[void 0,{validators:[b.B.required,function(e){return j(e.value)}],asyncValidators:function(t){return e.suitableEmail(t.value)},updateOn:"blur"}],passwords:this.fb.group({password:[void 0,[b.B.required,function(e){return F(e.value)}]],password2:[void 0,[b.B.required]]},{validator:function(e){return q(e.value)}}),firstName:[void 0,[b.B.required]],lastName:[void 0,[b.B.required]],institution:[void 0,[b.B.required]],terms:[void 0,[b.B.required]]}),this.userNameField=this.userForm.get("username"),this.emailField=this.userForm.get("email"),this.passwordField=this.userForm.get("passwords.password"),this.password2Field=this.userForm.get("passwords.password2"),this.passwordsGroup=this.userForm.get("passwords")}},{key:"captcha",value:function(e){this.gRecaptchaResponse=e,e&&(this.missingCaptcha=!1)}},{key:"captchaExpired",value:function(){this.gRecaptchaResponse=null}},{key:"availableLogin",value:function(e){var t=this;return!e||e.length<5?Object(_.a)({"too-short":!0}):this.userService.availableLogin(e).pipe(Object(G.a)((function(t){return t?null:{"login-taken":"User "+e+" already exists"}})),Object(A.a)((function(e){return t.feedback.error(e),Object(_.a)({"cannot-connect":!0})})))}},{key:"suitableEmail",value:function(e){var t=this;return this.userService.suitableEmail(e).pipe(Object(G.a)((function(t){if(t.isFree&&t.isAcademic)return null;var r={};return t.isFree||(r["email-taken"]="Email: "+e+" is already being used"),t.isAcademic||(r["email-nonacademic"]="Academic email is required for the registration. Contact us if your email is not recognized as academic."),r})),Object(A.a)((function(e){return t.feedback.error(e),Object(_.a)({"cannot-connect":!0})})))}},{key:"register",value:function(){if(this.userForm.valid){if(!this.gRecaptchaResponse&&!this.emailField.value.endsWith(".cn")&&!this.emailField.value.endsWith(".tw"))return void(this.missingCaptcha=!0);var e=this.makeUserData(this.userForm.value);this.triggerRegistration(e)}}},{key:"makeUserData",value:function(e){return{login:e.username,password:e.passwords.password,email:e.email,firstName:e.firstName,lastName:e.lastName,institution:e.institution,terms:e.terms,g_recaptcha_response:this.gRecaptchaResponse}}},{key:"triggerRegistration",value:function(e){var t=this;this.userService.register(e).then((function(e){t.registered=!0,t.registeredMsg=e.email,t.feedback.success("Registration successful")})).catch((function(e){t.feedback.error(e),t.gRecaptchaResponse=void 0,t.recaptcha&&t.recaptcha.reset()}))}}]),t}()).\u0275fac=function(e){return new(e||ie)(u.Nb(l.a),u.Nb(E.a),u.Nb(b.e),u.Nb(Z.a))},ie.\u0275cmp=u.Hb({type:ie,selectors:[["ng-component"]],viewQuery:function(e,t){var r;1&e&&u.Ic(W,!0),2&e&&u.pc(r=u.ac())&&(t.recaptcha=r.first)},decls:7,vars:2,consts:[[1,"mb-2"],["class","alert alert-success",4,"ngIf"],["class","mb-4",4,"ngIf"],[1,"alert","alert-success"],[1,"mb-4"],[3,"formGroup"],["registrationForm",""],[1,"form-group"],[1,"w-100"],["for","username"],["matInput","","formControlName","username","id","username","minlength","5","pattern","[0-9|a-z|\\._]+","placeholder","choose login","required","","type","text",1,""],[4,"ngIf"],["for","email"],["matInput","","formControlName","email","id","email","placeholder","your email","required","","type","text",1,""],["formGroupName","passwords"],["for","password"],["matInput","","formControlName","password","id","password","minlength","8","placeholder","password","required","","type","password",1,""],["for","password2"],["matInput","","formControlName","password2","id","password2","minlength","8","placeholder","password","required","","type","password",1,""],["for","firstName"],["matInput","","formControlName","firstName","id","firstName","minlength","2","placeholder","e.g. Charles","required","","type","text",1,""],["for","lastName"],["matInput","","formControlName","lastName","id","lastName","minlength","2","placeholder","e.g. Darwin","required","","type","text",1,""],["for","institution"],["matInput","","formControlName","institution","id","institution","minlength","3","placeholder","e.g. University of Edinburgh","required","","type","text",1,""],[3,"site_key","captchaExpired","captchaResponse"],["recaptcha",""],[1,"alert","alert-danger",3,"hidden"],["formControlName","terms","id","terms","required","","type","checkbox"],[2,"font-weight","bold","text-decoration","underline"],["role","button",3,"click"],["type","button",1,"btn","btn-primary",3,"disabled","click"]],template:function(e,t){1&e&&(u.Tb(0,"mat-card",0),u.Tb(1,"mat-card-title"),u.Tb(2,"h2"),u.Dc(3,"User registration"),u.Sb(),u.Sb(),u.Tb(4,"mat-card-content"),u.Bc(5,X,7,1,"div",1),u.Bc(6,ae,65,10,"div",2),u.Sb(),u.Sb()),2&e&&(u.Bb(5),u.jc("ngIf",t.registered),u.Bb(1),u.jc("ngIf",!t.registered))},directives:[K.a,K.e,K.c,o.m,b.D,b.r,b.i,H.c,H.g,J.b,b.d,b.q,b.h,b.m,b.w,b.z,H.f,b.j,H.b,h,$.a,$.c],encapsulation:2}),ie),ne=i("PqYM");function oe(e,t){1&e&&(u.Tb(0,"div",21),u.Dc(1,"Account has been updated"),u.Sb())}function ce(e,t){if(1&e){var r=u.Ub();u.Tb(0,"mat-card",1),u.Tb(1,"mat-card-title"),u.Tb(2,"h2"),u.Dc(3),u.Sb(),u.Sb(),u.Tb(4,"mat-card-content"),u.Bc(5,oe,2,0,"div",2),u.Tb(6,"form",3,4),u.Tb(8,"div",5),u.Tb(9,"mat-form-field",6),u.Tb(10,"mat-label",7),u.Dc(11,"Login"),u.Sb(),u.Ob(12,"input",8),u.Sb(),u.Sb(),u.Tb(13,"div",5),u.Tb(14,"a",9),u.Dc(15,"Change password"),u.Sb(),u.Sb(),u.Tb(16,"div",5),u.Tb(17,"mat-form-field",6),u.Tb(18,"mat-label",10),u.Dc(19,"Email"),u.Sb(),u.Ob(20,"input",11),u.Tb(21,"mat-error"),u.Dc(22,"Not valid email format"),u.Sb(),u.Sb(),u.Sb(),u.Tb(23,"div",5),u.Tb(24,"mat-form-field",6),u.Tb(25,"mat-label",12),u.Dc(26,"First Name"),u.Sb(),u.Ob(27,"input",13),u.Sb(),u.Sb(),u.Tb(28,"div",5),u.Tb(29,"mat-form-field",6),u.Tb(30,"mat-label",14),u.Dc(31,"Family Name"),u.Sb(),u.Ob(32,"input",15),u.Sb(),u.Sb(),u.Tb(33,"div",5),u.Tb(34,"mat-form-field",6),u.Tb(35,"mat-label",16),u.Dc(36,"Institution"),u.Sb(),u.Ob(37,"input",17),u.Sb(),u.Sb(),u.Tb(38,"div",5),u.Tb(39,"mat-form-field",6),u.Tb(40,"mat-label",18),u.Dc(41,"Current password"),u.Sb(),u.Ob(42,"input",19),u.Sb(),u.Sb(),u.Tb(43,"button",20),u.Zb("click",(function(){return u.sc(r),u.dc().save()})),u.Dc(44," Update "),u.Sb(),u.Sb(),u.Sb(),u.Sb()}if(2&e){var a=u.dc();u.Bb(3),u.Fc("Edit user: ",a.user.name,""),u.Bb(2),u.jc("ngIf",a.updated),u.Bb(1),u.jc("formGroup",a.userForm),u.Bb(37),u.jc("disabled",a.userForm.invalid)}}function de(e,t){1&e&&(u.Tb(0,"div",18),u.Dc(1,"Password has been updated"),u.Sb())}function ue(e,t){1&e&&(u.Tb(0,"mat-error"),u.Dc(1,"Passwords do not match"),u.Sb())}function le(e,t){if(1&e){var r=u.Ub();u.Tb(0,"mat-card",1),u.Tb(1,"mat-card-title"),u.Tb(2,"h2"),u.Dc(3),u.Sb(),u.Sb(),u.Tb(4,"mat-card-content"),u.Bc(5,de,2,0,"div",2),u.Tb(6,"form",3,4),u.Tb(8,"div",5),u.Tb(9,"mat-form-field",6),u.Tb(10,"mat-label",7),u.Dc(11,"Login"),u.Sb(),u.Ob(12,"input",8),u.Sb(),u.Sb(),u.Tb(13,"div",9),u.Tb(14,"div",5),u.Tb(15,"mat-form-field",6),u.Tb(16,"mat-label",10),u.Dc(17,"Password"),u.Sb(),u.Ob(18,"input",11),u.Tb(19,"mat-hint"),u.Dc(20,"Password must be at least 8 long, containing a digit or symbol or capital letter"),u.Sb(),u.Tb(21,"mat-error"),u.Dc(22,"Password must be at least 8 long, containing a digit or symbol or capital letter"),u.Sb(),u.Sb(),u.Sb(),u.Tb(23,"div",5),u.Tb(24,"mat-form-field",6),u.Tb(25,"mat-label",12),u.Dc(26,"Repeat password"),u.Sb(),u.Ob(27,"input",13),u.Bc(28,ue,2,0,"mat-error",14),u.Sb(),u.Sb(),u.Sb(),u.Tb(29,"div",5),u.Tb(30,"mat-form-field",6),u.Tb(31,"mat-label",15),u.Dc(32,"Current password"),u.Sb(),u.Ob(33,"input",16),u.Sb(),u.Sb(),u.Tb(34,"button",17),u.Zb("click",(function(){return u.sc(r),u.dc().save()})),u.Dc(35," Update "),u.Sb(),u.Sb(),u.Sb(),u.Sb()}if(2&e){var a=u.dc();u.Bb(3),u.Fc("Change password for ",a.user.name,""),u.Bb(2),u.jc("ngIf",a.updated),u.Bb(1),u.jc("formGroup",a.userForm),u.Bb(22),u.jc("ngIf",a.passwordsGroup.errors),u.Bb(6),u.jc("disabled",a.userForm.invalid)}}var be,me,pe,fe,he,ge=[{path:"",children:[{path:"edit",component:(me=function(){function t(r,a,i){e(this,t),this.userService=r,this.feedback=a,this.fb=i,this.updated=!1}return r(t,[{key:"ngOnInit",value:function(){this.user=this.userService.currentUser,this.userForm=this.fb.group({username:[{value:this.user.login,disabled:!0},[b.B.required],[]],email:[this.user.email,[b.B.required,function(e){return j(e.value)}],[]],firstName:[this.user.firstName,[b.B.required]],lastName:[this.user.lastName,[b.B.required]],institution:[this.user.institution,[b.B.required]],currentPassword:[void 0,[b.B.required]]}),this.userNameField=this.userForm.get("username"),this.emailField=this.userForm.get("email"),this.currentPasswordField=this.userForm.get("currentPassword")}},{key:"save",value:function(){var e=this;if(this.userForm.valid){var t=this.userForm.value,r={login:this.user.login,currentPassword:t.currentPassword,email:t.email,firstName:t.firstName,lastName:t.lastName,institution:t.institution};this.updated=!1,this.userService.update(r).subscribe((function(t){e.user=t,e.currentPasswordField.reset(),e.feedback.success("User: "+t.login+" has been updated"),e.updated=!0,Object(ne.a)(5e3).subscribe((function(){return e.updated=!1}))}),(function(t){e.feedback.error(t),e.currentPasswordField.reset()}))}}}]),t}(),me.\u0275fac=function(e){return new(e||me)(u.Nb(l.a),u.Nb(E.a),u.Nb(b.e))},me.\u0275cmp=u.Hb({type:me,selectors:[["ng-component"]],decls:1,vars:1,consts:[["class","mb-2",4,"ngIf"],[1,"mb-2"],["class","alert alert-success",4,"ngIf"],[3,"formGroup"],["registrationForm",""],[1,"form-group"],[1,"w-100"],["for","username"],["matInput","","formControlName","username","id","username","required","","type","text",1,""],["routerLink","/account/password"],["for","email"],["matInput","","formControlName","email","id","email","placeholder","your email","required","","type","text",1,""],["for","firstName"],["matInput","","formControlName","firstName","id","firstName","minlength","2","placeholder","e.g. Charles","required","","type","text",1,""],["for","lastName"],["matInput","","formControlName","lastName","id","lastName","minlength","2","placeholder","e.g. Darwin","required","","type","text",1,""],["for","institution"],["matInput","","formControlName","institution","id","institution","placeholder","e.g. University of Edinburgh","required","","type","text",1,""],["for","cPassword"],["matInput","","formControlName","currentPassword","id","cPassword","placeholder","current password","required","","type","password",1,""],["type","button",1,"btn","btn-primary",3,"disabled","click"],[1,"alert","alert-success"]],template:function(e,t){1&e&&u.Bc(0,ce,45,4,"mat-card",0),2&e&&u.jc("ngIf",t.user&&!t.user.anonymous)},directives:[o.m,K.a,K.e,K.c,b.D,b.r,b.i,H.c,H.g,J.b,b.d,b.q,b.h,b.z,c.e,H.b,b.m],encapsulation:2}),me)},{path:"password",component:(be=function(){function t(r,a,i){e(this,t),this.userService=r,this.feedback=a,this.fb=i,this.updated=!1}return r(t,[{key:"ngOnInit",value:function(){this.user=this.userService.currentUser,this.userForm=this.fb.group({username:[{value:this.user.login,disabled:!0},[b.B.required],[]],passwords:this.fb.group({password:[void 0,[b.B.required,function(e){return F(e.value)}]],password2:[void 0,[b.B.required]]},{validator:function(e){return q(e.value)}}),currentPassword:[void 0,[b.B.required]]}),this.passwordsGroup=this.userForm.get("passwords")}},{key:"save",value:function(){var e=this;if(this.userForm.valid){var t=this.userForm.value,r={login:this.user.login,currentPassword:t.currentPassword,password:t.passwords.password};this.updated=!1,this.userService.passwordUpdate(r).subscribe((function(t){e.user=t,e.userForm.reset(),e.feedback.success("User: "+t.login+" password has been updated"),e.updated=!0,Object(ne.a)(5e3).subscribe((function(){return e.updated=!1}))}),(function(t){e.userForm.reset(),e.feedback.error(t)}))}}}]),t}(),be.\u0275fac=function(e){return new(e||be)(u.Nb(l.a),u.Nb(E.a),u.Nb(b.e))},be.\u0275cmp=u.Hb({type:be,selectors:[["ng-component"]],decls:1,vars:1,consts:[["class","mb-2",4,"ngIf"],[1,"mb-2"],["class","alert alert-success",4,"ngIf"],[3,"formGroup"],["registrationForm",""],[1,"form-group"],[1,"w-100"],["for","username"],["matInput","","formControlName","username","id","username","required","","type","text",1,""],["formGroupName","passwords"],["for","password"],["matInput","","formControlName","password","id","password","minlength","8","placeholder","password","required","","type","password",1,""],["for","password2"],["matInput","","formControlName","password2","id","password2","minlength","8","placeholder","password","required","","type","password",1,""],[4,"ngIf"],["for","cPassword"],["matInput","","formControlName","currentPassword","id","cPassword","placeholder","current password","required","","type","password",1,""],["type","button",1,"btn","btn-primary",3,"disabled","click"],[1,"alert","alert-success"]],template:function(e,t){1&e&&u.Bc(0,le,36,5,"mat-card",0),2&e&&u.jc("ngIf",t.user&&!t.user.anonymous)},directives:[o.m,K.a,K.e,K.c,b.D,b.r,b.i,H.c,H.g,J.b,b.d,b.q,b.h,b.z,b.j,b.m,H.f,H.b],encapsulation:2}),be)},{path:"register",component:se},{path:"activate",component:U},{path:"remind",component:T},{path:"reset",component:M}]}],ve=((fe=function t(){e(this,t)}).\u0275mod=u.Lb({type:fe}),fe.\u0275inj=u.Kb({factory:function(e){return new(e||fe)},imports:[[c.f.forChild(ge)],c.f]}),fe),we=((pe=function t(){e(this,t)}).\u0275mod=u.Lb({type:pe}),pe.\u0275inj=u.Kb({factory:function(e){return new(e||pe)},imports:[[]]}),pe),Se=i("wMBR"),ye=i("f0Cb"),Te=i("NFeN"),ke=((he=function t(){e(this,t)}).\u0275mod=u.Lb({type:he}),he.\u0275inj=u.Kb({factory:function(e){return new(e||he)},imports:[[o.c,b.k,b.y,we,Se.a,ve,K.d,ye.b,H.e,Te.b,J.c,$.b]]}),he)}}])}();