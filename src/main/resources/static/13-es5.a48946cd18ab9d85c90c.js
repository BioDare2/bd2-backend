function _defineProperties(e,t){for(var r=0;r<t.length;r++){var n=t[r];n.enumerable=n.enumerable||!1,n.configurable=!0,"value"in n&&(n.writable=!0),Object.defineProperty(e,n.key,n)}}function _createClass(e,t,r){return t&&_defineProperties(e.prototype,t),r&&_defineProperties(e,r),e}function _get(e,t,r){return(_get="undefined"!=typeof Reflect&&Reflect.get?Reflect.get:function(e,t,r){var n=_superPropBase(e,t);if(n){var a=Object.getOwnPropertyDescriptor(n,t);return a.get?a.get.call(r):a.value}})(e,t,r||e)}function _superPropBase(e,t){for(;!Object.prototype.hasOwnProperty.call(e,t)&&null!==(e=_getPrototypeOf(e)););return e}function _classCallCheck(e,t){if(!(e instanceof t))throw new TypeError("Cannot call a class as a function")}function _inherits(e,t){if("function"!=typeof t&&null!==t)throw new TypeError("Super expression must either be null or a function");e.prototype=Object.create(t&&t.prototype,{constructor:{value:e,writable:!0,configurable:!0}}),t&&_setPrototypeOf(e,t)}function _setPrototypeOf(e,t){return(_setPrototypeOf=Object.setPrototypeOf||function(e,t){return e.__proto__=t,e})(e,t)}function _createSuper(e){var t=_isNativeReflectConstruct();return function(){var r,n=_getPrototypeOf(e);if(t){var a=_getPrototypeOf(this).constructor;r=Reflect.construct(n,arguments,a)}else r=n.apply(this,arguments);return _possibleConstructorReturn(this,r)}}function _possibleConstructorReturn(e,t){return!t||"object"!=typeof t&&"function"!=typeof t?_assertThisInitialized(e):t}function _assertThisInitialized(e){if(void 0===e)throw new ReferenceError("this hasn't been initialised - super() hasn't been called");return e}function _isNativeReflectConstruct(){if("undefined"==typeof Reflect||!Reflect.construct)return!1;if(Reflect.construct.sham)return!1;if("function"==typeof Proxy)return!0;try{return Date.prototype.toString.call(Reflect.construct(Date,[],(function(){}))),!0}catch(e){return!1}}function _getPrototypeOf(e){return(_getPrototypeOf=Object.setPrototypeOf?Object.getPrototypeOf:function(e){return e.__proto__||Object.getPrototypeOf(e)})(e)}(window.webpackJsonp=window.webpackJsonp||[]).push([[13],{d9Wp:function(e,t,r){"use strict";r.r(t),r.d(t,"TsDataModule",(function(){return N}));var n=r("ofXK"),a=r("tyNb"),o=r("yYXI"),i=r("bngM"),c=r("QBpm"),s=r("e8YO"),l=r("Kj3r"),u=r("Iab2"),p=r("fXoL"),f=r("vejM"),b=r("ZMwR"),d=r("GVyA"),m=r("wcxA"),h=r("3Pt+"),g=r("1jcm"),y=r("M9IT");function v(e,t){if(1&e&&(p.Wb(0,"option",28),p.Gc(1),p.Vb()),2&e){var r=t.$implicit;p.oc("value",r.name),p.Cb(1),p.Hc(r.label)}}function P(e,t){if(1&e&&(p.Wb(0,"option",28),p.Gc(1),p.Vb()),2&e){var r=t.$implicit;p.oc("value",r.name),p.Cb(1),p.Hc(r.label)}}function C(e,t){if(1&e&&(p.Wb(0,"option",28),p.Gc(1),p.Vb()),2&e){var r=t.$implicit;p.oc("value",r.name),p.Cb(1),p.Hc(r.label)}}var w,k=function(){return[10,25,50,100,200]},_=((w=function(e){_inherits(r,e);var t=_createSuper(r);function r(e){return _classCallCheck(this,r),t.call(this,e)}return r}(m.a)).\u0275fac=function(e){return new(e||w)(p.Qb(h.d))},w.\u0275cmp=p.Kb({type:w,selectors:[["bd2-tsdisplay-params-rform"]],inputs:{disabled:"disabled",totalTraces:"totalTraces",currentPage:"currentPage"},outputs:{displayParams:"displayParams"},features:[p.zb],decls:47,vars:10,consts:[["role","form",1,"form-horizontal","container",3,"formGroup"],[1,"row"],[1,"col-md-8","col-sm-10"],["formGroupName","timeScale",1,"form-group","row"],[1,"col-md-3","col-sm-3"],["for","timeStart",1,"col-md-1","col-sm-2"],["type","number","step","any","min","0","size","5","id","timeStart","required","","formControlName","timeStart",1,"form-control"],["for","timeEnd",1,"col-md-1","col-sm-1"],["type","number","step","any","min","0","size","5","required","","id","timeEnd","formControlName","timeEnd",1,"form-control"],[1,"col-md-2","col-sm-4"],["formControlName","hourly",1,"mr-3"],[1,"form-group","row"],["for","detrending",1,"col-sm-2"],[1,"col-sm-3"],["required","","id","detrending","formControlName","detrending",1,"form-control"],[3,"value",4,"ngFor","ngForOf"],[1,"col-sm-1"],["for","align",1,"col-sm-1"],["required","","id","align","formControlName","align",1,"form-control"],[1,"col-sm-2"],["for","normalisation"],["required","","id","normalisation","formControlName","normalisation",1,"form-control"],[1,"col-sm"],["formControlName","trimFirst",1,"mr-3"],["formControlName","log2"],[1,"col-sm-8"],[3,"length","disabled","pageSize","pageIndex","pageSizeOptions","page"],["dataPaginator",""],[3,"value"]],template:function(e,t){1&e&&(p.Wb(0,"form",0),p.Wb(1,"div",1),p.Wb(2,"div",2),p.Wb(3,"div",3),p.Wb(4,"label",4),p.Gc(5,"Time range"),p.Vb(),p.Wb(6,"label",5),p.Gc(7,"from:"),p.Vb(),p.Wb(8,"div",4),p.Rb(9,"input",6),p.Vb(),p.Wb(10,"label",7),p.Gc(11,"to:"),p.Vb(),p.Wb(12,"div",4),p.Rb(13,"input",8),p.Vb(),p.Vb(),p.Vb(),p.Wb(14,"div",9),p.Wb(15,"mat-slide-toggle",10),p.Gc(16,"hourly"),p.Vb(),p.Vb(),p.Vb(),p.Wb(17,"div",11),p.Wb(18,"label",12),p.Gc(19,"Detrending"),p.Vb(),p.Wb(20,"div",13),p.Wb(21,"select",14),p.Ec(22,v,2,2,"option",15),p.Vb(),p.Vb(),p.Rb(23,"div",16),p.Wb(24,"label",17),p.Gc(25,"Align"),p.Vb(),p.Wb(26,"div",13),p.Wb(27,"select",18),p.Ec(28,P,2,2,"option",15),p.Vb(),p.Vb(),p.Vb(),p.Wb(29,"div",11),p.Wb(30,"div",19),p.Wb(31,"label",20),p.Gc(32,"Normalize"),p.Vb(),p.Vb(),p.Wb(33,"div",13),p.Wb(34,"select",21),p.Ec(35,C,2,2,"option",15),p.Vb(),p.Vb(),p.Wb(36,"div",22),p.Wb(37,"mat-slide-toggle",23),p.Gc(38,"within range"),p.Vb(),p.Wb(39,"mat-slide-toggle",24),p.Gc(40,"log2 transf."),p.Vb(),p.Vb(),p.Vb(),p.Rb(41,"div",11),p.Wb(42,"div",11),p.Rb(43,"label",19),p.Wb(44,"div",25),p.Wb(45,"mat-paginator",26,27),p.ec("page",(function(e){return t.loadDataPage(e)})),p.Vb(),p.Vb(),p.Vb(),p.Vb()),2&e&&(p.oc("formGroup",t.mainForm),p.Cb(22),p.oc("ngForOf",t.detrendingOptions),p.Cb(6),p.oc("ngForOf",t.alignOptions),p.Cb(7),p.oc("ngForOf",t.normalisationOptions),p.Cb(10),p.oc("length",t.totalTraces)("disabled",t.disabledPagination)("pageSize",t.currentPage.pageSize)("pageIndex",t.currentPage.pageIndex)("pageSizeOptions",p.qc(9,k)))},directives:[h.C,h.q,h.h,h.i,h.u,h.c,h.y,h.p,h.g,g.a,h.z,n.l,y.a,h.t,h.B],encapsulation:2}),w),V=r("nA4b");function x(e,t){if(1&e){var r=p.Xb();p.Wb(0,"div",7),p.Gc(1,"Please complete "),p.Wb(2,"a",8),p.ec("click",(function(){p.xc(r);var e=p.ic(2);return e.goToExpEdit(e.assay.id,"MeasurementSection")})),p.Gc(3,"Measurement details"),p.Vb(),p.Gc(4," to get access to the secondary data "),p.Vb()}}function W(e,t){if(1&e){var r=p.Xb();p.Wb(0,"div",9),p.Wb(1,"label",10),p.Wb(2,"a",11),p.ec("click",(function(){return p.xc(r),p.ic(2).exportDataView()})),p.Wb(3,"i",12),p.Gc(4,"save_alt"),p.Vb(),p.Wb(5,"span",13),p.Gc(6,"Download"),p.Vb(),p.Vb(),p.Gc(7," current view "),p.Vb(),p.Wb(8,"label",10),p.Wb(9,"a",14),p.ec("click",(function(){return p.xc(r),p.ic(2).exportFullData()})),p.Wb(10,"i",12),p.Gc(11,"save_alt"),p.Vb(),p.Wb(12,"span",13),p.Gc(13,"Download"),p.Vb(),p.Vb(),p.Gc(14," full "),p.Vb(),p.Vb()}}function O(e,t){if(1&e&&p.Rb(0,"bd2-ts-plots",15),2&e){var r=p.ic(2);p.oc("tracesPerPlot",r.tracesPerPlot)("data",r.timeseries)}}function S(e,t){if(1&e){var r=p.Xb();p.Wb(0,"div"),p.Wb(1,"h3"),p.Gc(2,"Show timeseries"),p.Vb(),p.Rb(3,"hr"),p.Wb(4,"bd2-tsdisplay-params-rform",1),p.ec("displayParams",(function(e){return p.xc(r),p.ic().displayChanged(e)})),p.Vb(),p.Ec(5,x,5,0,"div",2),p.Rb(6,"hr"),p.Wb(7,"div",3),p.Gc(8," Hint: You can click on trace label box to remove it from the plot. "),p.Vb(),p.Wb(9,"div",4),p.Ec(10,W,15,0,"div",5),p.Vb(),p.Rb(11,"hr"),p.Ec(12,O,1,2,"bd2-ts-plots",6),p.Vb()}if(2&e){var n=p.ic();p.Cb(4),p.oc("disabled",n.disabledSecondary)("totalTraces",n.totalTraces)("currentPage",n.currentPage),p.Cb(1),p.oc("ngIf",n.disabledSecondary),p.Cb(5),p.oc("ngIf",n.timeseries),p.Cb(2),p.oc("ngIf",n.timeseries)}}var E,D,T,G=[{path:"",children:[{path:"view/ts",component:(E=function(e){_inherits(r,e);var t=_createSuper(r);function r(e,n,a,o){var c;return _classCallCheck(this,r),(c=t.call(this,o)).fetcher=e,c.RDMSocial=n,c.analytics=a,c.timeseries=[],c.totalTraces=0,c.currentPage=i.a.firstPage(),c.tracesPerPlot=5,c.blocked=!1,c.disabledSecondary=!1,c.csvExporter=new s.a,c.titlePart=" Data",c}return _createClass(r,[{key:"ngOnInit",value:function(){var e=this;_get(_getPrototypeOf(r.prototype),"ngOnInit",this).call(this),this.timeSeriesSubsripction=this.fetcher.seriesPackStream.pipe(Object(l.a)(1e3)).subscribe((function(t){if(e.assay){var r=t.data;e.currentParams=t.params,e.timeseries=r,e.tracesPerPlot=Math.max(5,r.length/20),e.totalTraces=t.totalTraces,e.currentPage=t.currentPage,e.analytics.experimentDataViev(e.assay.id)}}),(function(t){console.log("Error in TS subscription: "+t),e.feedback.error(t)}))}},{key:"ngOnDestroy",value:function(){this.timeSeriesSubsripction&&this.timeSeriesSubsripction.unsubscribe(),this.fetcher.ngOnDestroy(),_get(_getPrototypeOf(r.prototype),"ngOnDestroy",this).call(this)}},{key:"displayChanged",value:function(e){this.fetcher.changeDisplayParams(e)}},{key:"exportDataView",value:function(){this.exportSeriesPack(this.fetcher.current)}},{key:"exportFullData",value:function(){var e=this;this.fetcher.getFullDataSet(this.assay,this.fetcher.current.params).subscribe((function(t){e.exportSeriesPack(t,!0)}))}},{key:"exportSeriesPack",value:function(e){var t=arguments.length>1&&void 0!==arguments[1]&&arguments[1];if(e){var r=this.csvExporter.renderCSVTable(e.data,e.params,e.currentPage,this.assay),n=new Blob([r],{type:"text/csv"});u.saveAs(n,"".concat(this.assay.id,"_data.").concat(e.params.detrending.name).concat(t?".full":".page".concat(e.currentPage.pageIndex+1),".csv")),this.recordExport()}}},{key:"recordExport",value:function(){this.analytics.experimentDataExport(this.assay.id)}},{key:"updateModel",value:function(e){var t=this;_get(_getPrototypeOf(r.prototype),"updateModel",this).call(this,e),this.RDMSocial.canProceedByMeasurement(e).then((function(e){t.disabledSecondary=!e})),this.fetcher.experiment(e)}}]),r}(o.a),E.\u0275fac=function(e){return new(e||E)(p.Qb(c.a),p.Qb(f.a),p.Qb(b.a),p.Qb(d.a))},E.\u0275cmp=p.Kb({type:E,selectors:[["ng-component"]],features:[p.Bb([c.a]),p.zb],decls:1,vars:1,consts:[[4,"ngIf"],[3,"disabled","totalTraces","currentPage","displayParams"],["type","danger","class","alert alert-danger","role","alert",4,"ngIf"],["type","info","role","alert","dismissible","true","dismissOnTimeout","20000",1,"alert","alert-info"],[1,"clearfix"],["class","float-right",4,"ngIf"],[3,"tracesPerPlot","data",4,"ngIf"],["type","danger","role","alert",1,"alert","alert-danger"],[3,"click"],[1,"float-right"],[1,"mr-4"],["download","","role","button","aria-label","download",1,"btn","btn-primary",2,"color","white",3,"click"],[1,"material-icons","bd-icon"],[1,"cdk-visually-hidden"],["download","","role","button","aria-label","download whole",1,"btn","btn-primary",2,"color","white",3,"click"],[3,"tracesPerPlot","data"]],template:function(e,t){1&e&&p.Ec(0,S,13,6,"div",0),2&e&&p.oc("ngIf",t.assay)},directives:[n.m,_,V.a],encapsulation:2}),E)},{path:"view/heatmap",loadChildren:function(){return Promise.all([r.e(0),r.e(18)]).then(r.bind(null,"QftV")).then((function(e){return e.TsHeatmapModule}))}},{path:"ts-old-import",loadChildren:function(){return Promise.all([r.e(0),r.e(17)]).then(r.bind(null,"9Gw/")).then((function(e){return e.TsOldImportModule}))}},{path:"ts-import",loadChildren:function(){return Promise.all([r.e(0),r.e(19)]).then(r.bind(null,"yPAH")).then((function(e){return e.TsImportModule}))}}]}],R=((D=function e(){_classCallCheck(this,e)}).\u0275mod=p.Ob({type:D}),D.\u0275inj=p.Nb({factory:function(e){return new(e||D)},imports:[[a.f.forChild(G)],a.f]}),D),I=r("YiFe"),M=r("a6j2"),z=r("ZTEM"),N=((T=function e(){_classCallCheck(this,e)}).\u0275mod=p.Ob({type:T}),T.\u0275inj=p.Nb({factory:function(e){return new(e||T)},imports:[[n.c,h.j,h.x,I.a,M.a,R,z.a]]}),T)},e8YO:function(e,t,r){"use strict";r.d(t,"a",(function(){return a}));var n=function(){function e(t){_classCallCheck(this,e),this.rows=new Map,this.nextColumn=0,this.sorted=!0,null!=t&&(this.sorted=t)}return _createClass(e,[{key:"append",value:function(e,t){this.put(e,t,this.nextColumn)}},{key:"put",value:function(e,t,r){this._put(e,t,r),this.nextColumn=Math.max(this.nextColumn,r+1)}},{key:"putColumn",value:function(e,t,r){var n=this;e.forEach((function(e,a){n._put(e,t[a],r)})),this.nextColumn=Math.max(this.nextColumn,r+1)}},{key:"appendColumn",value:function(e,t){this.putColumn(e,t,this.nextColumn)}},{key:"getValue",value:function(e,t){var r=this.rows.get(e);return r?r[t]:void 0}},{key:"getRow",value:function(e){return this.rows.get(e)}},{key:"forEach",value:function(e){var t=this;this.keys().forEach((function(r,n){var a={key:r,columns:t.rows.get(r)};e(a,n,t)}))}},{key:"forEachFlat",value:function(e){var t=this;this.keys().forEach((function(r,n){var a=[r].concat(t.rows.get(r));e(a,n,t)}))}},{key:"keys",value:function(){return this.sorted?Array.from(this.rows.keys()).sort((function(e,t){return e==t?0:e<t?-1:1})):Array.from(this.rows.keys())}},{key:"_put",value:function(e,t,r){var n=this.rows.get(e);n||(n=[],this.rows.set(e,n)),n[r]=t}},{key:"width",get:function(){return this.nextColumn}},{key:"size",get:function(){return this.rows.size}}]),e}(),a=function(){function e(){_classCallCheck(this,e),this.SEP=","}return _createClass(e,[{key:"renderCSVTable",value:function(e,t,r,n){var a=this.prepareHeaders(n,t,r,e.length);this.appendDataLabels(a,e);var o=this.prepareDataTable(e),i=this.mapToString(a,this.SEP);return i+=this.mapToString(o,this.SEP)}},{key:"prepareHeaders",value:function(e,t,r,n){var a=this.prepareExpHeaders(e);return this.appendDisplayProperties(a,t),this.appendDataProperties(a,n,r),a}},{key:"prepareExpHeaders",value:function(e){var t=new n(!1);return t.put("Exp Id:",""+e.id,0),t.put("Exp URL:","https://biodare2.ed.ac.uk/experiment/"+e.id,0),t.put("Exp Name:",e.name,0),e.contributionDesc.authors.forEach((function(e,r){t.put("Authors:",e.name,r)})),t}},{key:"appendDisplayProperties",value:function(e,t){e.put("Time range:",t.timeScaleLabel,0),e.put("Detrending:",t.detrending.label,0),t.hourly&&e.put("Hourly binned:","true",0),t.log2&&e.put("Log2 transform:",""+t.log2,0),e.put("Normalization:",t.normalisation,0),e.put("Normalization:",t.trimFirst?"within range":"whole serie",1),e.put("Align:",t.align,0)}},{key:"appendDataProperties",value:function(e,t,r){e.put("Traces",t+" of "+r.length,0);var n=Math.floor(r.length/r.pageSize)+(r.length%r.pageSize==0?0:1);e.put("Data Page",r.pageIndex+1+" of "+n,0)}},{key:"appendDataLabels",value:function(e,t){t.forEach((function(t,r){e.put("Label:",t.label,r)}))}},{key:"prepareDataTable",value:function(e){var t=new n;return e.forEach((function(e){var r=e.data.map((function(e){return e.x})),n=e.data.map((function(e){return e.y}));t.appendColumn(r,n)})),t}},{key:"mapToString",value:function(e,t){var r="";return e.forEachFlat((function(e){for(var n=0;n<e.length;n++)void 0!==e[n]&&(r+=e[n]),r+=t;r+="\n"})),r}}]),e}()}}]);