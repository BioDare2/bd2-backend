import{Aa as le,Ac as z,Cd as Y,D as w,Da as F,Dd as Oe,Ea as L,Ed as Te,Fa as g,Fd as D,Ga as p,Gb as fe,Gd as A,Ha as de,Hd as xe,Id as we,Ja as S,Jd as Q,Lc as ve,Ld as f,M as k,Ma as b,Mc as be,Ob as _e,Od as B,Pa as V,Pc as Ce,Rc as De,S as I,Sc as C,T as E,Tc as H,U as h,W as s,Wa as ce,Xa as he,Xc as W,Z as ie,a as d,b as J,bb as G,ca as R,da as y,db as me,fa as ae,ga as oe,i as c,ia as v,kb as ue,lb as ge,mb as pe,na as ne,o as ee,pa as se,rc as ye,u as x,uc as M,xa as re,y as te,yc as P,yd as Ae,z as _}from"./chunk-ZWLUK6NT.js";function Be(i,o){}var m=class{viewContainerRef;injector;id;role="dialog";panelClass="";hasBackdrop=!0;backdropClass="";disableClose=!1;width="";height="";minWidth;minHeight;maxWidth;maxHeight;positionStrategy;data=null;direction;ariaDescribedBy=null;ariaLabelledBy=null;ariaLabel=null;ariaModal=!0;autoFocus="first-tabbable";restoreFocus=!0;scrollStrategy;closeOnNavigation=!0;closeOnDestroy=!0;closeOnOverlayDetachments=!0;componentFactoryResolver;providers;container;templateContext};var Z=(()=>{class i extends Te{_elementRef=s(v);_focusTrapFactory=s(be);_config;_interactivityChecker=s(ve);_ngZone=s(oe);_overlayRef=s(Q);_focusMonitor=s(Ce);_renderer=s(le);_platform=s(ye);_document=s(_e,{optional:!0});_portalOutlet;_focusTrap=null;_elementFocusedBeforeDialogWasOpened=null;_closeInteractionType=null;_ariaLabelledByQueue=[];_changeDetectorRef=s(fe);_injector=s(y);_isDestroyed=!1;constructor(){super(),this._config=s(m,{optional:!0})||new m,this._config.ariaLabelledBy&&this._ariaLabelledByQueue.push(this._config.ariaLabelledBy)}_addAriaLabelledBy(e){this._ariaLabelledByQueue.push(e),this._changeDetectorRef.markForCheck()}_removeAriaLabelledBy(e){let t=this._ariaLabelledByQueue.indexOf(e);t>-1&&(this._ariaLabelledByQueue.splice(t,1),this._changeDetectorRef.markForCheck())}_contentAttached(){this._initializeFocusTrap(),this._handleBackdropClicks(),this._captureInitialFocus()}_captureInitialFocus(){this._trapFocus()}ngOnDestroy(){this._isDestroyed=!0,this._restoreFocus()}attachComponentPortal(e){this._portalOutlet.hasAttached();let t=this._portalOutlet.attachComponentPortal(e);return this._contentAttached(),t}attachTemplatePortal(e){this._portalOutlet.hasAttached();let t=this._portalOutlet.attachTemplatePortal(e);return this._contentAttached(),t}attachDomPortal=e=>{this._portalOutlet.hasAttached();let t=this._portalOutlet.attachDomPortal(e);return this._contentAttached(),t};_recaptureFocus(){this._containsFocus()||this._trapFocus()}_forceFocus(e,t){this._interactivityChecker.isFocusable(e)||(e.tabIndex=-1,this._ngZone.runOutsideAngular(()=>{let a=()=>{n(),l(),e.removeAttribute("tabindex")},n=this._renderer.listen(e,"blur",a),l=this._renderer.listen(e,"mousedown",a)})),e.focus(t)}_focusByCssSelector(e,t){let a=this._elementRef.nativeElement.querySelector(e);a&&this._forceFocus(a,t)}_trapFocus(){this._isDestroyed||se(()=>{let e=this._elementRef.nativeElement;switch(this._config.autoFocus){case!1:case"dialog":this._containsFocus()||e.focus();break;case!0:case"first-tabbable":this._focusTrap?.focusInitialElement()||this._focusDialogContainer();break;case"first-heading":this._focusByCssSelector('h1, h2, h3, h4, h5, h6, [role="heading"]');break;default:this._focusByCssSelector(this._config.autoFocus);break}},{injector:this._injector})}_restoreFocus(){let e=this._config.restoreFocus,t=null;if(typeof e=="string"?t=this._document.querySelector(e):typeof e=="boolean"?t=e?this._elementFocusedBeforeDialogWasOpened:null:e&&(t=e),this._config.restoreFocus&&t&&typeof t.focus=="function"){let a=M(),n=this._elementRef.nativeElement;(!a||a===this._document.body||a===n||n.contains(a))&&(this._focusMonitor?(this._focusMonitor.focusVia(t,this._closeInteractionType),this._closeInteractionType=null):t.focus())}this._focusTrap&&this._focusTrap.destroy()}_focusDialogContainer(){this._elementRef.nativeElement.focus&&this._elementRef.nativeElement.focus()}_containsFocus(){let e=this._elementRef.nativeElement,t=M();return e===t||e.contains(t)}_initializeFocusTrap(){this._platform.isBrowser&&(this._focusTrap=this._focusTrapFactory.create(this._elementRef.nativeElement),this._document&&(this._elementFocusedBeforeDialogWasOpened=M()))}_handleBackdropClicks(){this._overlayRef.backdropClick().subscribe(()=>{this._config.disableClose&&this._recaptureFocus()})}static \u0275fac=function(t){return new(t||i)};static \u0275cmp=F({type:i,selectors:[["cdk-dialog-container"]],viewQuery:function(t,a){if(t&1&&ue(D,7),t&2){let n;ge(n=pe())&&(a._portalOutlet=n.first)}},hostAttrs:["tabindex","-1",1,"cdk-dialog-container"],hostVars:6,hostBindings:function(t,a){t&2&&b("id",a._config.id||null)("role",a._config.role)("aria-modal",a._config.ariaModal)("aria-labelledby",a._config.ariaLabel?null:a._ariaLabelledByQueue[0])("aria-label",a._config.ariaLabel)("aria-describedby",a._config.ariaDescribedBy||null)},features:[p],decls:1,vars:0,consts:[["cdkPortalOutlet",""]],template:function(t,a){t&1&&S(0,Be,0,0,"ng-template",0)},dependencies:[D],styles:[".cdk-dialog-container{display:block;width:100%;height:100%;min-height:inherit;max-height:inherit}"],encapsulation:2})}return i})(),O=class{overlayRef;config;componentInstance;componentRef;containerInstance;disableClose;closed=new c;backdropClick;keydownEvents;outsidePointerEvents;id;_detachSubscription;constructor(o,e){this.overlayRef=o,this.config=e,this.disableClose=e.disableClose,this.backdropClick=o.backdropClick(),this.keydownEvents=o.keydownEvents(),this.outsidePointerEvents=o.outsidePointerEvents(),this.id=e.id,this.keydownEvents.subscribe(t=>{t.keyCode===27&&!this.disableClose&&!P(t)&&(t.preventDefault(),this.close(void 0,{focusOrigin:"keyboard"}))}),this.backdropClick.subscribe(()=>{this.disableClose||this.close(void 0,{focusOrigin:"mouse"})}),this._detachSubscription=o.detachments().subscribe(()=>{e.closeOnOverlayDetachments!==!1&&this.close()})}close(o,e){if(this.containerInstance){let t=this.closed;this.containerInstance._closeInteractionType=e?.focusOrigin||"program",this._detachSubscription.unsubscribe(),this.overlayRef.dispose(),t.next(o),t.complete(),this.componentInstance=this.containerInstance=null}}updatePosition(){return this.overlayRef.updatePosition(),this}updateSize(o="",e=""){return this.overlayRef.updateSize({width:o,height:e}),this}addPanelClass(o){return this.overlayRef.addPanelClass(o),this}removePanelClass(o){return this.overlayRef.removePanelClass(o),this}},Ne=new h("DialogScrollStrategy",{providedIn:"root",factory:()=>{let i=s(f);return()=>i.scrollStrategies.block()}}),je=new h("DialogData"),Ve=new h("DefaultDialogConfig");var $=(()=>{class i{_overlay=s(f);_injector=s(y);_defaultOptions=s(Ve,{optional:!0});_parentDialog=s(i,{optional:!0,skipSelf:!0});_overlayContainer=s(we);_idGenerator=s(C);_openDialogsAtThisLevel=[];_afterAllClosedAtThisLevel=new c;_afterOpenedAtThisLevel=new c;_ariaHiddenElements=new Map;_scrollStrategy=s(Ne);get openDialogs(){return this._parentDialog?this._parentDialog.openDialogs:this._openDialogsAtThisLevel}get afterOpened(){return this._parentDialog?this._parentDialog.afterOpened:this._afterOpenedAtThisLevel}afterAllClosed=x(()=>this.openDialogs.length?this._getAfterAllClosed():this._getAfterAllClosed().pipe(k(void 0)));constructor(){}open(e,t){let a=this._defaultOptions||new m;t=d(d({},a),t),t.id=t.id||this._idGenerator.getId("cdk-dialog-"),t.id&&this.getDialogById(t.id);let n=this._getOverlayConfig(t),l=this._overlay.create(n),r=new O(l,t),u=this._attachContainer(l,r,t);return r.containerInstance=u,this._attachDialogContent(e,r,u,t),this.openDialogs.length||this._hideNonDialogContentFromAssistiveTechnology(),this.openDialogs.push(r),r.closed.subscribe(()=>this._removeOpenDialog(r,!0)),this.afterOpened.next(r),r}closeAll(){q(this.openDialogs,e=>e.close())}getDialogById(e){return this.openDialogs.find(t=>t.id===e)}ngOnDestroy(){q(this._openDialogsAtThisLevel,e=>{e.config.closeOnDestroy===!1&&this._removeOpenDialog(e,!1)}),q(this._openDialogsAtThisLevel,e=>e.close()),this._afterAllClosedAtThisLevel.complete(),this._afterOpenedAtThisLevel.complete(),this._openDialogsAtThisLevel=[]}_getOverlayConfig(e){let t=new xe({positionStrategy:e.positionStrategy||this._overlay.position().global().centerHorizontally().centerVertically(),scrollStrategy:e.scrollStrategy||this._scrollStrategy(),panelClass:e.panelClass,hasBackdrop:e.hasBackdrop,direction:e.direction,minWidth:e.minWidth,minHeight:e.minHeight,maxWidth:e.maxWidth,maxHeight:e.maxHeight,width:e.width,height:e.height,disposeOnNavigation:e.closeOnNavigation});return e.backdropClass&&(t.backdropClass=e.backdropClass),t}_attachContainer(e,t,a){let n=a.injector||a.viewContainerRef?.injector,l=[{provide:m,useValue:a},{provide:O,useValue:t},{provide:Q,useValue:e}],r;a.container?typeof a.container=="function"?r=a.container:(r=a.container.type,l.push(...a.container.providers(a))):r=Z;let u=new Y(r,a.viewContainerRef,y.create({parent:n||this._injector,providers:l}));return e.attach(u).instance}_attachDialogContent(e,t,a,n){if(e instanceof re){let l=this._createInjector(n,t,a,void 0),r={$implicit:n.data,dialogRef:t};n.templateContext&&(r=d(d({},r),typeof n.templateContext=="function"?n.templateContext():n.templateContext)),a.attachTemplatePortal(new Oe(e,null,r,l))}else{let l=this._createInjector(n,t,a,this._injector),r=a.attachComponentPortal(new Y(e,n.viewContainerRef,l));t.componentRef=r,t.componentInstance=r.instance}}_createInjector(e,t,a,n){let l=e.injector||e.viewContainerRef?.injector,r=[{provide:je,useValue:e.data},{provide:O,useValue:t}];return e.providers&&(typeof e.providers=="function"?r.push(...e.providers(t,e,a)):r.push(...e.providers)),e.direction&&(!l||!l.get(H,null,{optional:!0}))&&r.push({provide:H,useValue:{value:e.direction,change:ee()}}),y.create({parent:l||n,providers:r})}_removeOpenDialog(e,t){let a=this.openDialogs.indexOf(e);a>-1&&(this.openDialogs.splice(a,1),this.openDialogs.length||(this._ariaHiddenElements.forEach((n,l)=>{n?l.setAttribute("aria-hidden",n):l.removeAttribute("aria-hidden")}),this._ariaHiddenElements.clear(),t&&this._getAfterAllClosed().next()))}_hideNonDialogContentFromAssistiveTechnology(){let e=this._overlayContainer.getContainerElement();if(e.parentElement){let t=e.parentElement.children;for(let a=t.length-1;a>-1;a--){let n=t[a];n!==e&&n.nodeName!=="SCRIPT"&&n.nodeName!=="STYLE"&&!n.hasAttribute("aria-live")&&(this._ariaHiddenElements.set(n,n.getAttribute("aria-hidden")),n.setAttribute("aria-hidden","true"))}}}_getAfterAllClosed(){let e=this._parentDialog;return e?e._getAfterAllClosed():this._afterAllClosedAtThisLevel}static \u0275fac=function(t){return new(t||i)};static \u0275prov=I({token:i,factory:i.\u0275fac,providedIn:"root"})}return i})();function q(i,o){let e=i.length;for(;e--;)o(i[e])}var ke=(()=>{class i{static \u0275fac=function(t){return new(t||i)};static \u0275mod=L({type:i});static \u0275inj=E({providers:[$],imports:[B,A,De,A]})}return i})();function Ge(i,o){}var j=class{viewContainerRef;injector;id;role="dialog";panelClass="";hasBackdrop=!0;backdropClass="";disableClose=!1;width="";height="";minWidth;minHeight;maxWidth;maxHeight;position;data=null;direction;ariaDescribedBy=null;ariaLabelledBy=null;ariaLabel=null;ariaModal=!0;autoFocus="first-tabbable";restoreFocus=!0;delayFocusTrap=!0;scrollStrategy;closeOnNavigation=!0;componentFactoryResolver;enterAnimationDuration;exitAnimationDuration},K="mdc-dialog--open",Ie="mdc-dialog--opening",Ee="mdc-dialog--closing",ze=150,He=75,We=(()=>{class i extends Z{_animationMode=s(ne,{optional:!0});_animationStateChanged=new ae;_animationsEnabled=this._animationMode!=="NoopAnimations";_actionSectionCount=0;_hostElement=this._elementRef.nativeElement;_enterAnimationDuration=this._animationsEnabled?Fe(this._config.enterAnimationDuration)??ze:0;_exitAnimationDuration=this._animationsEnabled?Fe(this._config.exitAnimationDuration)??He:0;_animationTimer=null;_contentAttached(){super._contentAttached(),this._startOpenAnimation()}_startOpenAnimation(){this._animationStateChanged.emit({state:"opening",totalTime:this._enterAnimationDuration}),this._animationsEnabled?(this._hostElement.style.setProperty(Re,`${this._enterAnimationDuration}ms`),this._requestAnimationFrame(()=>this._hostElement.classList.add(Ie,K)),this._waitForAnimationToComplete(this._enterAnimationDuration,this._finishDialogOpen)):(this._hostElement.classList.add(K),Promise.resolve().then(()=>this._finishDialogOpen()))}_startExitAnimation(){this._animationStateChanged.emit({state:"closing",totalTime:this._exitAnimationDuration}),this._hostElement.classList.remove(K),this._animationsEnabled?(this._hostElement.style.setProperty(Re,`${this._exitAnimationDuration}ms`),this._requestAnimationFrame(()=>this._hostElement.classList.add(Ee)),this._waitForAnimationToComplete(this._exitAnimationDuration,this._finishDialogClose)):Promise.resolve().then(()=>this._finishDialogClose())}_updateActionSectionCount(e){this._actionSectionCount+=e,this._changeDetectorRef.markForCheck()}_finishDialogOpen=()=>{this._clearAnimationClasses(),this._openAnimationDone(this._enterAnimationDuration)};_finishDialogClose=()=>{this._clearAnimationClasses(),this._animationStateChanged.emit({state:"closed",totalTime:this._exitAnimationDuration})};_clearAnimationClasses(){this._hostElement.classList.remove(Ie,Ee)}_waitForAnimationToComplete(e,t){this._animationTimer!==null&&clearTimeout(this._animationTimer),this._animationTimer=setTimeout(t,e)}_requestAnimationFrame(e){this._ngZone.runOutsideAngular(()=>{typeof requestAnimationFrame=="function"?requestAnimationFrame(e):e()})}_captureInitialFocus(){this._config.delayFocusTrap||this._trapFocus()}_openAnimationDone(e){this._config.delayFocusTrap&&this._trapFocus(),this._animationStateChanged.next({state:"opened",totalTime:e})}ngOnDestroy(){super.ngOnDestroy(),this._animationTimer!==null&&clearTimeout(this._animationTimer)}attachComponentPortal(e){let t=super.attachComponentPortal(e);return t.location.nativeElement.classList.add("mat-mdc-dialog-component-host"),t}static \u0275fac=(()=>{let e;return function(a){return(e||(e=R(i)))(a||i)}})();static \u0275cmp=F({type:i,selectors:[["mat-dialog-container"]],hostAttrs:["tabindex","-1",1,"mat-mdc-dialog-container","mdc-dialog"],hostVars:10,hostBindings:function(t,a){t&2&&(G("id",a._config.id),b("aria-modal",a._config.ariaModal)("role",a._config.role)("aria-labelledby",a._config.ariaLabel?null:a._ariaLabelledByQueue[0])("aria-label",a._config.ariaLabel)("aria-describedby",a._config.ariaDescribedBy||null),V("_mat-animation-noopable",!a._animationsEnabled)("mat-mdc-dialog-container-with-actions",a._actionSectionCount>0))},features:[p],decls:3,vars:0,consts:[[1,"mat-mdc-dialog-inner-container","mdc-dialog__container"],[1,"mat-mdc-dialog-surface","mdc-dialog__surface"],["cdkPortalOutlet",""]],template:function(t,a){t&1&&(ce(0,"div",0)(1,"div",1),S(2,Ge,0,0,"ng-template",2),he()())},dependencies:[D],styles:['.mat-mdc-dialog-container{width:100%;height:100%;display:block;box-sizing:border-box;max-height:inherit;min-height:inherit;min-width:inherit;max-width:inherit;outline:0}.cdk-overlay-pane.mat-mdc-dialog-panel{max-width:var(--mat-dialog-container-max-width, 560px);min-width:var(--mat-dialog-container-min-width, 280px)}@media(max-width: 599px){.cdk-overlay-pane.mat-mdc-dialog-panel{max-width:var(--mat-dialog-container-small-max-width, calc(100vw - 32px))}}.mat-mdc-dialog-inner-container{display:flex;flex-direction:row;align-items:center;justify-content:space-around;box-sizing:border-box;height:100%;opacity:0;transition:opacity linear var(--mat-dialog-transition-duration, 0ms);max-height:inherit;min-height:inherit;min-width:inherit;max-width:inherit}.mdc-dialog--closing .mat-mdc-dialog-inner-container{transition:opacity 75ms linear;transform:none}.mdc-dialog--open .mat-mdc-dialog-inner-container{opacity:1}._mat-animation-noopable .mat-mdc-dialog-inner-container{transition:none}.mat-mdc-dialog-surface{display:flex;flex-direction:column;flex-grow:0;flex-shrink:0;box-sizing:border-box;width:100%;height:100%;position:relative;overflow-y:auto;outline:0;transform:scale(0.8);transition:transform var(--mat-dialog-transition-duration, 0ms) cubic-bezier(0, 0, 0.2, 1);max-height:inherit;min-height:inherit;min-width:inherit;max-width:inherit;box-shadow:var(--mat-dialog-container-elevation-shadow, none);border-radius:var(--mdc-dialog-container-shape, var(--mat-sys-corner-extra-large, 4px));background-color:var(--mdc-dialog-container-color, var(--mat-sys-surface, white))}[dir=rtl] .mat-mdc-dialog-surface{text-align:right}.mdc-dialog--open .mat-mdc-dialog-surface,.mdc-dialog--closing .mat-mdc-dialog-surface{transform:none}._mat-animation-noopable .mat-mdc-dialog-surface{transition:none}.mat-mdc-dialog-surface::before{position:absolute;box-sizing:border-box;width:100%;height:100%;top:0;left:0;border:2px solid rgba(0,0,0,0);border-radius:inherit;content:"";pointer-events:none}.mat-mdc-dialog-title{display:block;position:relative;flex-shrink:0;box-sizing:border-box;margin:0 0 1px;padding:var(--mat-dialog-headline-padding, 6px 24px 13px)}.mat-mdc-dialog-title::before{display:inline-block;width:0;height:40px;content:"";vertical-align:0}[dir=rtl] .mat-mdc-dialog-title{text-align:right}.mat-mdc-dialog-container .mat-mdc-dialog-title{color:var(--mdc-dialog-subhead-color, var(--mat-sys-on-surface, rgba(0, 0, 0, 0.87)));font-family:var(--mdc-dialog-subhead-font, var(--mat-sys-headline-small-font, inherit));line-height:var(--mdc-dialog-subhead-line-height, var(--mat-sys-headline-small-line-height, 1.5rem));font-size:var(--mdc-dialog-subhead-size, var(--mat-sys-headline-small-size, 1rem));font-weight:var(--mdc-dialog-subhead-weight, var(--mat-sys-headline-small-weight, 400));letter-spacing:var(--mdc-dialog-subhead-tracking, var(--mat-sys-headline-small-tracking, 0.03125em))}.mat-mdc-dialog-content{display:block;flex-grow:1;box-sizing:border-box;margin:0;overflow:auto;max-height:65vh}.mat-mdc-dialog-content>:first-child{margin-top:0}.mat-mdc-dialog-content>:last-child{margin-bottom:0}.mat-mdc-dialog-container .mat-mdc-dialog-content{color:var(--mdc-dialog-supporting-text-color, var(--mat-sys-on-surface-variant, rgba(0, 0, 0, 0.6)));font-family:var(--mdc-dialog-supporting-text-font, var(--mat-sys-body-medium-font, inherit));line-height:var(--mdc-dialog-supporting-text-line-height, var(--mat-sys-body-medium-line-height, 1.5rem));font-size:var(--mdc-dialog-supporting-text-size, var(--mat-sys-body-medium-size, 1rem));font-weight:var(--mdc-dialog-supporting-text-weight, var(--mat-sys-body-medium-weight, 400));letter-spacing:var(--mdc-dialog-supporting-text-tracking, var(--mat-sys-body-medium-tracking, 0.03125em))}.mat-mdc-dialog-container .mat-mdc-dialog-content{padding:var(--mat-dialog-content-padding, 20px 24px)}.mat-mdc-dialog-container-with-actions .mat-mdc-dialog-content{padding:var(--mat-dialog-with-actions-content-padding, 20px 24px 0)}.mat-mdc-dialog-container .mat-mdc-dialog-title+.mat-mdc-dialog-content{padding-top:0}.mat-mdc-dialog-actions{display:flex;position:relative;flex-shrink:0;flex-wrap:wrap;align-items:center;justify-content:flex-end;box-sizing:border-box;min-height:52px;margin:0;padding:8px;border-top:1px solid rgba(0,0,0,0);padding:var(--mat-dialog-actions-padding, 16px 24px);justify-content:var(--mat-dialog-actions-alignment, flex-end)}@media(forced-colors: active){.mat-mdc-dialog-actions{border-top-color:CanvasText}}.mat-mdc-dialog-actions.mat-mdc-dialog-actions-align-start,.mat-mdc-dialog-actions[align=start]{justify-content:start}.mat-mdc-dialog-actions.mat-mdc-dialog-actions-align-center,.mat-mdc-dialog-actions[align=center]{justify-content:center}.mat-mdc-dialog-actions.mat-mdc-dialog-actions-align-end,.mat-mdc-dialog-actions[align=end]{justify-content:flex-end}.mat-mdc-dialog-actions .mat-button-base+.mat-button-base,.mat-mdc-dialog-actions .mat-mdc-button-base+.mat-mdc-button-base{margin-left:8px}[dir=rtl] .mat-mdc-dialog-actions .mat-button-base+.mat-button-base,[dir=rtl] .mat-mdc-dialog-actions .mat-mdc-button-base+.mat-mdc-button-base{margin-left:0;margin-right:8px}.mat-mdc-dialog-component-host{display:contents}'],encapsulation:2})}return i})(),Re="--mat-dialog-transition-duration";function Fe(i){return i==null?null:typeof i=="number"?i:i.endsWith("ms")?z(i.substring(0,i.length-2)):i.endsWith("s")?z(i.substring(0,i.length-1))*1e3:i==="0"?0:null}var N=function(i){return i[i.OPEN=0]="OPEN",i[i.CLOSING=1]="CLOSING",i[i.CLOSED=2]="CLOSED",i}(N||{}),T=class{_ref;_containerInstance;componentInstance;componentRef;disableClose;id;_afterOpened=new c;_beforeClosed=new c;_result;_closeFallbackTimeout;_state=N.OPEN;_closeInteractionType;constructor(o,e,t){this._ref=o,this._containerInstance=t,this.disableClose=e.disableClose,this.id=o.id,o.addPanelClass("mat-mdc-dialog-panel"),t._animationStateChanged.pipe(_(a=>a.state==="opened"),w(1)).subscribe(()=>{this._afterOpened.next(),this._afterOpened.complete()}),t._animationStateChanged.pipe(_(a=>a.state==="closed"),w(1)).subscribe(()=>{clearTimeout(this._closeFallbackTimeout),this._finishDialogClose()}),o.overlayRef.detachments().subscribe(()=>{this._beforeClosed.next(this._result),this._beforeClosed.complete(),this._finishDialogClose()}),te(this.backdropClick(),this.keydownEvents().pipe(_(a=>a.keyCode===27&&!this.disableClose&&!P(a)))).subscribe(a=>{this.disableClose||(a.preventDefault(),Le(this,a.type==="keydown"?"keyboard":"mouse"))})}close(o){this._result=o,this._containerInstance._animationStateChanged.pipe(_(e=>e.state==="closing"),w(1)).subscribe(e=>{this._beforeClosed.next(o),this._beforeClosed.complete(),this._ref.overlayRef.detachBackdrop(),this._closeFallbackTimeout=setTimeout(()=>this._finishDialogClose(),e.totalTime+100)}),this._state=N.CLOSING,this._containerInstance._startExitAnimation()}afterOpened(){return this._afterOpened}afterClosed(){return this._ref.closed}beforeClosed(){return this._beforeClosed}backdropClick(){return this._ref.backdropClick}keydownEvents(){return this._ref.keydownEvents}updatePosition(o){let e=this._ref.config.positionStrategy;return o&&(o.left||o.right)?o.left?e.left(o.left):e.right(o.right):e.centerHorizontally(),o&&(o.top||o.bottom)?o.top?e.top(o.top):e.bottom(o.bottom):e.centerVertically(),this._ref.updatePosition(),this}updateSize(o="",e=""){return this._ref.updateSize(o,e),this}addPanelClass(o){return this._ref.addPanelClass(o),this}removePanelClass(o){return this._ref.removePanelClass(o),this}getState(){return this._state}_finishDialogClose(){this._state=N.CLOSED,this._ref.close(this._result,{focusOrigin:this._closeInteractionType}),this.componentInstance=null}};function Le(i,o,e){return i._closeInteractionType=o,i.close(e)}var Ye=new h("MatMdcDialogData"),Qe=new h("mat-mdc-dialog-default-options"),qe=new h("mat-mdc-dialog-scroll-strategy",{providedIn:"root",factory:()=>{let i=s(f);return()=>i.scrollStrategies.block()}});var X=(()=>{class i{_overlay=s(f);_defaultOptions=s(Qe,{optional:!0});_scrollStrategy=s(qe);_parentDialog=s(i,{optional:!0,skipSelf:!0});_idGenerator=s(C);_dialog=s($);_openDialogsAtThisLevel=[];_afterAllClosedAtThisLevel=new c;_afterOpenedAtThisLevel=new c;dialogConfigClass=j;_dialogRefConstructor;_dialogContainerType;_dialogDataToken;get openDialogs(){return this._parentDialog?this._parentDialog.openDialogs:this._openDialogsAtThisLevel}get afterOpened(){return this._parentDialog?this._parentDialog.afterOpened:this._afterOpenedAtThisLevel}_getAfterAllClosed(){let e=this._parentDialog;return e?e._getAfterAllClosed():this._afterAllClosedAtThisLevel}afterAllClosed=x(()=>this.openDialogs.length?this._getAfterAllClosed():this._getAfterAllClosed().pipe(k(void 0)));constructor(){this._dialogRefConstructor=T,this._dialogContainerType=We,this._dialogDataToken=Ye}open(e,t){let a;t=d(d({},this._defaultOptions||new j),t),t.id=t.id||this._idGenerator.getId("mat-mdc-dialog-"),t.scrollStrategy=t.scrollStrategy||this._scrollStrategy();let n=this._dialog.open(e,J(d({},t),{positionStrategy:this._overlay.position().global().centerHorizontally().centerVertically(),disableClose:!0,closeOnDestroy:!1,closeOnOverlayDetachments:!1,container:{type:this._dialogContainerType,providers:()=>[{provide:this.dialogConfigClass,useValue:t},{provide:m,useValue:t}]},templateContext:()=>({dialogRef:a}),providers:(l,r,u)=>(a=new this._dialogRefConstructor(l,t,u),a.updatePosition(t?.position),[{provide:this._dialogContainerType,useValue:u},{provide:this._dialogDataToken,useValue:r.data},{provide:this._dialogRefConstructor,useValue:a}])}));return a.componentRef=n.componentRef,a.componentInstance=n.componentInstance,this.openDialogs.push(a),this.afterOpened.next(a),a.afterClosed().subscribe(()=>{let l=this.openDialogs.indexOf(a);l>-1&&(this.openDialogs.splice(l,1),this.openDialogs.length||this._getAfterAllClosed().next())}),a}closeAll(){this._closeDialogs(this.openDialogs)}getDialogById(e){return this.openDialogs.find(t=>t.id===e)}ngOnDestroy(){this._closeDialogs(this._openDialogsAtThisLevel),this._afterAllClosedAtThisLevel.complete(),this._afterOpenedAtThisLevel.complete()}_closeDialogs(e){let t=e.length;for(;t--;)e[t].close()}static \u0275fac=function(t){return new(t||i)};static \u0275prov=I({token:i,factory:i.\u0275fac,providedIn:"root"})}return i})(),Rt=(()=>{class i{dialogRef=s(T,{optional:!0});_elementRef=s(v);_dialog=s(X);ariaLabel;type="button";dialogResult;_matDialogClose;constructor(){}ngOnInit(){this.dialogRef||(this.dialogRef=Me(this._elementRef,this._dialog.openDialogs))}ngOnChanges(e){let t=e._matDialogClose||e._matDialogCloseResult;t&&(this.dialogResult=t.currentValue)}_onButtonClick(e){Le(this.dialogRef,e.screenX===0&&e.screenY===0?"keyboard":"mouse",this.dialogResult)}static \u0275fac=function(t){return new(t||i)};static \u0275dir=g({type:i,selectors:[["","mat-dialog-close",""],["","matDialogClose",""]],hostVars:2,hostBindings:function(t,a){t&1&&me("click",function(l){return a._onButtonClick(l)}),t&2&&b("aria-label",a.ariaLabel||null)("type",a.type)},inputs:{ariaLabel:[0,"aria-label","ariaLabel"],type:"type",dialogResult:[0,"mat-dialog-close","dialogResult"],_matDialogClose:[0,"matDialogClose","_matDialogClose"]},exportAs:["matDialogClose"],features:[ie]})}return i})(),Se=(()=>{class i{_dialogRef=s(T,{optional:!0});_elementRef=s(v);_dialog=s(X);constructor(){}ngOnInit(){this._dialogRef||(this._dialogRef=Me(this._elementRef,this._dialog.openDialogs)),this._dialogRef&&Promise.resolve().then(()=>{this._onAdd()})}ngOnDestroy(){this._dialogRef?._containerInstance&&Promise.resolve().then(()=>{this._onRemove()})}static \u0275fac=function(t){return new(t||i)};static \u0275dir=g({type:i})}return i})(),Ft=(()=>{class i extends Se{id=s(C).getId("mat-mdc-dialog-title-");_onAdd(){this._dialogRef._containerInstance?._addAriaLabelledBy?.(this.id)}_onRemove(){this._dialogRef?._containerInstance?._removeAriaLabelledBy?.(this.id)}static \u0275fac=(()=>{let e;return function(a){return(e||(e=R(i)))(a||i)}})();static \u0275dir=g({type:i,selectors:[["","mat-dialog-title",""],["","matDialogTitle",""]],hostAttrs:[1,"mat-mdc-dialog-title","mdc-dialog__title"],hostVars:1,hostBindings:function(t,a){t&2&&G("id",a.id)},inputs:{id:"id"},exportAs:["matDialogTitle"],features:[p]})}return i})(),Lt=(()=>{class i{static \u0275fac=function(t){return new(t||i)};static \u0275dir=g({type:i,selectors:[["","mat-dialog-content",""],["mat-dialog-content"],["","matDialogContent",""]],hostAttrs:[1,"mat-mdc-dialog-content","mdc-dialog__content"],features:[de([Ae])]})}return i})(),St=(()=>{class i extends Se{align;_onAdd(){this._dialogRef._containerInstance?._updateActionSectionCount?.(1)}_onRemove(){this._dialogRef._containerInstance?._updateActionSectionCount?.(-1)}static \u0275fac=(()=>{let e;return function(a){return(e||(e=R(i)))(a||i)}})();static \u0275dir=g({type:i,selectors:[["","mat-dialog-actions",""],["mat-dialog-actions"],["","matDialogActions",""]],hostAttrs:[1,"mat-mdc-dialog-actions","mdc-dialog__actions"],hostVars:6,hostBindings:function(t,a){t&2&&V("mat-mdc-dialog-actions-align-start",a.align==="start")("mat-mdc-dialog-actions-align-center",a.align==="center")("mat-mdc-dialog-actions-align-end",a.align==="end")},inputs:{align:"align"},features:[p]})}return i})();function Me(i,o){let e=i.nativeElement.parentElement;for(;e&&!e.classList.contains("mat-mdc-dialog-container");)e=e.parentElement;return e?o.find(t=>t.id===e.id):null}var Mt=(()=>{class i{static \u0275fac=function(t){return new(t||i)};static \u0275mod=L({type:i});static \u0275inj=E({providers:[X],imports:[ke,B,A,W,W]})}return i})();export{T as a,Ye as b,X as c,Rt as d,Ft as e,Lt as f,St as g,Mt as h};
