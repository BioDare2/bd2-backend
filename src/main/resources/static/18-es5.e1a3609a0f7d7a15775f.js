!function(){function e(t,i,n){return(e="undefined"!=typeof Reflect&&Reflect.get?Reflect.get:function(e,t,i){var n=function(e,t){for(;!Object.prototype.hasOwnProperty.call(e,t)&&null!==(e=o(e)););return e}(e,t);if(n){var a=Object.getOwnPropertyDescriptor(n,t);return a.get?a.get.call(i):a.value}})(t,i,n||t)}function t(e,i){return(t=Object.setPrototypeOf||function(e,t){return e.__proto__=t,e})(e,i)}function i(e){var t=function(){if("undefined"==typeof Reflect||!Reflect.construct)return!1;if(Reflect.construct.sham)return!1;if("function"==typeof Proxy)return!0;try{return Date.prototype.toString.call(Reflect.construct(Date,[],(function(){}))),!0}catch(e){return!1}}();return function(){var i,a=o(e);if(t){var l=o(this).constructor;i=Reflect.construct(a,arguments,l)}else i=a.apply(this,arguments);return n(this,i)}}function n(e,t){return!t||"object"!=typeof t&&"function"!=typeof t?function(e){if(void 0===e)throw new ReferenceError("this hasn't been initialised - super() hasn't been called");return e}(e):t}function o(e){return(o=Object.setPrototypeOf?Object.getPrototypeOf:function(e){return e.__proto__||Object.getPrototypeOf(e)})(e)}function a(e,t){if(!(e instanceof t))throw new TypeError("Cannot call a class as a function")}function l(e,t){for(var i=0;i<t.length;i++){var n=t[i];n.enumerable=n.enumerable||!1,n.configurable=!0,"value"in n&&(n.writable=!0),Object.defineProperty(e,n.key,n)}}function r(e,t,i){return t&&l(e.prototype,t),i&&l(e,i),e}(window.webpackJsonp=window.webpackJsonp||[]).push([[18],{"9Gw/":function(n,l,s){"use strict";s.r(l),s.d(l,"TsOldImportModule",(function(){return oe}));var c,u=s("ofXK"),b=s("3Pt+"),d=s("YiFe"),f=s("ZTEM"),h=s("tyNb"),m=s("yYXI"),g=s("EvUs"),p=s("fXoL"),v=s("UsWK"),T=((c=function(){function e(t){a(this,e),this.BD2REST=t}return r(e,[{key:"getSimpleTableView",value:function(e){return this.BD2REST.fileViewSimpleTable(e).then((function(e){return e.data}))}},{key:"verifyFormat",value:function(e,t){return this.BD2REST.fileViewVerifyFormat(e,t).toPromise()}}]),e}()).\u0275fac=function(e){return new(e||c)(p.Xb(v.a))},c.\u0275prov=p.Jb({token:c,factory:c.\u0275fac,providedIn:"root"}),c),y=s("GVyA"),w=s("KhIM"),k=function(){function e(t,i,n,o,l){a(this,e),this.rows=t,this.th=i,this.rowsLabels=n,this.specialRows=o,this.specialRowsLabels=l,this.width=e.maxLength(t),this.th=i||(t.length<1?[]:e.labelCols(this.width)),this.rowsLabels=n||e.labelRows(t.length),this.specialRows=o||[],this.specialRowsLabels=l||e.blankRows(this.specialRows.length)}return r(e,null,[{key:"labelRows",value:function(e){for(var t=[],i=1;i<=e;i++)t.push(""+i);return t}},{key:"blankRows",value:function(e){for(var t=[],i=0;i<e;i++)t.push("");return t}},{key:"maxLength",value:function(e){return!e||e.length<1?0:Math.max.apply(Math,e.map((function(e){return e.length})))}},{key:"labelCols",value:function(e){for(var t=[],i=1;i<=e;i++)t.push(Object(w.i)(i));return t}}]),e}(),S=function(){var e=function(){function e(){a(this,e)}return r(e,null,[{key:"color",value:function(t){return e.colors[t%e.colors.length]}}]),e}();return e.TIME_BG="lightyellow",e.IGNORED_BG="lightgrey",e.NOISE_BG="darkgrey",e.colors=["lightgreen","lightblue","lightcoral","lightcyan","lightgreen","lightpink","lightsalmon","lightseagreen","lightskyblue","palegoldenrod","paleturquoise","palevioletred"],e}(),D=s("0IaG"),B=["columnTypeForm"];function C(e,t){if(1&e){var i=p.Ub();p.Tb(0,"li"),p.Tb(1,"input",14,15),p.Zb("ngModelChange",(function(e){return p.sc(i),p.dc().cellRole=e})),p.Sb(),p.Dc(3),p.Sb()}if(2&e){var n=t.$implicit,o=p.dc();p.Bb(1),p.kc("value",n.name),p.jc("ngModel",o.cellRole),p.Bb(2),p.Fc(" ",n.label," ")}}function R(e,t){if(1&e){var i=p.Ub();p.Tb(0,"div",5),p.Tb(1,"label",16),p.Dc(2,"Propagate until block size"),p.Sb(),p.Tb(3,"input",17,18),p.Zb("ngModelChange",(function(e){return p.sc(i),p.dc().rangeSize=e})),p.Sb(),p.Sb()}if(2&e){var n=p.dc();p.Bb(3),p.jc("ngModel",n.rangeSize)}}function I(e,t){if(1&e){var i=p.Ub();p.Tb(0,"div",5),p.Tb(1,"label",19),p.Dc(2,"Label"),p.Sb(),p.Tb(3,"input",20,21),p.Zb("ngModelChange",(function(e){return p.sc(i),p.dc().dataLabel=e})),p.Sb(),p.Sb()}if(2&e){var n=p.dc();p.Bb(3),p.jc("ngModel",n.dataLabel)}}function O(e,t){if(1&e&&(p.Tb(0,"option",32),p.Dc(1),p.Sb()),2&e){var i=t.$implicit;p.kc("value",i.name),p.Bb(1),p.Fc("",i.label," ")}}function M(e,t){if(1&e){var i=p.Ub();p.Tb(0,"div",5),p.Tb(1,"label",33),p.Dc(2,"Time between images (hours)"),p.Sb(),p.Tb(3,"input",34,35),p.Zb("ngModelChange",(function(e){return p.sc(i),p.dc(2).imgInterval=e})),p.Sb(),p.Sb()}if(2&e){var n=p.dc(2);p.Bb(3),p.jc("ngModel",n.imgInterval)}}function j(e,t){if(1&e){var i=p.Ub();p.Tb(0,"div"),p.Tb(1,"div",5),p.Tb(2,"label",22),p.Dc(3,"Type of time column"),p.Sb(),p.Tb(4,"select",23,24),p.Zb("ngModelChange",(function(e){return p.sc(i),p.dc().timeType=e})),p.Bc(6,O,2,2,"option",25),p.Sb(),p.Sb(),p.Tb(7,"div",5),p.Tb(8,"label",26),p.Dc(9,"Row with first timepoint"),p.Sb(),p.Tb(10,"input",27,28),p.Zb("ngModelChange",(function(e){return p.sc(i),p.dc().firstRow=e})),p.Sb(),p.Sb(),p.Tb(12,"div",5),p.Tb(13,"label",29),p.Dc(14,"Time offset (hours)"),p.Sb(),p.Tb(15,"input",30,31),p.Zb("ngModelChange",(function(e){return p.sc(i),p.dc().timeOffset=e})),p.Sb(),p.Sb(),p.Bc(17,M,5,1,"div",8),p.Sb()}if(2&e){var n=p.dc();p.Bb(4),p.jc("ngModel",n.timeType),p.Bb(2),p.jc("ngForOf",n.timeTypeOptions),p.Bb(4),p.jc("ngModel",n.firstRow),p.Bb(5),p.jc("ngModel",n.timeOffset),p.Bb(2),p.jc("ngIf",n.isImageBased())}}function E(e,t){if(1&e&&(p.Tb(0,"div",37),p.Dc(1),p.Sb()),2&e){var i=t.$implicit;p.Bb(1),p.Ec(i)}}function F(e,t){if(1&e&&(p.Tb(0,"div"),p.Bc(1,E,2,1,"div",36),p.Sb()),2&e){var i=p.dc();p.Bb(1),p.jc("ngForOf",i.errors)}}var L,N,A=function e(t,i,n,o){a(this,e),this.selectedRange=t,this.lastCol=i,this.showTime=n,this.label=o},x=((N=function(){function e(t,i){a(this,e),this.myself=t,this.errors=null,this.cellRoles=[w.d.IGNORED,w.d.BACKGROUND,w.d.TIME,w.d.DATA],this.timeTypeOptions=[w.h.TIME_IN_HOURS,w.h.TIME_IN_MINUTES,w.h.TIME_IN_SECONDS,w.h.IMG_NUMBER],this.lastCol=i.lastCol,this.showTime=i.showTime,this.show(i.selectedRange,i.label)}return r(e,[{key:"ngOnInit",value:function(){}},{key:"isData",value:function(){return this.cellRole===w.d.DATA.name}},{key:"isTime",value:function(){return this.cellRole===w.d.TIME.name}},{key:"isImageBased",value:function(){return this.isTime()&&this.timeType===w.h.IMG_NUMBER.name}},{key:"show",value:function(e,t){e&&(this.orgRange=e,this.rangeSize=e.range.size(),this.rangeLabel=t||e.columnRangeLabel,e.role&&(this.cellRole=e.role.name),e.role==w.d.DATA&&e.details&&(this.dataLabel=e.details.dataLabel),e.role==w.d.TIME&&e.details&&(this.timeType=e.details.timeType.name,this.timeOffset=e.details.timeOffset,this.imgInterval=e.details.imgInterval,this.firstRow=e.details.firstRow))}},{key:"hide",value:function(){this.myself.close()}},{key:"accepted",value:function(){this.isValid()&&this.myself.close(this.emitDescription())}},{key:"acceptedAndNext",value:function(){if(this.isValid()){var e=this.emitDescription();if(e.range.lastCol>=this.lastCol);else{var t=this.nextRange(e.range,this.rangeSize),i=new w.c(t);i.role=w.d.DATA,i.details=e.details,e.follow=i}this.myself.close(e)}}},{key:"nextRange",value:function(e,t){var i=e.lastCol+1,n=i+t-1;return n=Math.min(n,this.lastCol),new w.b(new w.a(i,e.first.row),new w.a(n,e.first.row))}},{key:"emitDescription",value:function(){var e=this.recalculateRange(this.orgRange.range),t={};if(this.isData())t=new w.f(this.dataLabel);else if(this.isTime()){var i=new w.g;i.firstRow=this.firstRow,i.timeType=w.h.get(this.timeType),i.timeOffset=this.timeOffset,this.isImageBased()&&(i.imgInterval=this.imgInterval),t=i}var n=new w.c(e,this.orgRange.content);return n.role=w.d.get(this.cellRole),n.details=t,n}},{key:"isValid",value:function(){var e=[];return w.d.get(this.cellRole)||e.push("Unknown role: "+this.cellRole),this.isData()&&(this.dataLabel&&""!==this.dataLabel.trim()||(e.push("Non empty data label is required"),this.dataLabel=null)),this.isTime()?((!this.firstRow||this.firstRow<1)&&e.push("Row nr of the first time point must be >= 1"),this.isImageBased()&&(!this.imgInterval||this.imgInterval<=0)&&e.push("Image interval must be > 0")):(!this.rangeSize||this.rangeSize<1)&&e.push("Block size must be >= 1"),0===e.length&&(this.errors=null,!0)}},{key:"recalculateRange",value:function(e){var t=e.first;if(this.isTime())return t=new w.a(t.col,this.firstRow),new w.b(t,t);if(this.rangeSize!==e.size()){var i=new w.a(Math.min(t.col+this.rangeSize-1,this.lastCol),t.row);return new w.b(t,i)}return e}},{key:"showTime",set:function(e){this.cellRoles=e?[w.d.IGNORED,w.d.BACKGROUND,w.d.TIME,w.d.DATA]:[w.d.IGNORED,w.d.BACKGROUND,w.d.DATA]}}]),e}()).\u0275fac=function(e){return new(e||N)(p.Nb(D.g),p.Nb(D.a))},N.\u0275cmp=p.Hb({type:N,selectors:[["ng-component"]],viewQuery:function(e,t){var i;1&e&&p.xc(B,!0),2&e&&p.pc(i=p.ac())&&(t.columnTypeForm=i.first)},decls:28,vars:8,consts:[["mat-dialog-title","",1,"modal-title"],["mat-dialog-close","","aria-label","Close","type","button",1,"close","float-right"],["aria-hidden","true"],["mat-dialog-content","",1,"modal-body"],["columnTypeForm","ngForm"],[1,"form-group"],[1,"list-unstyled"],[4,"ngFor","ngForOf"],["class","form-group",4,"ngIf"],[4,"ngIf"],["mat-dialog-actions",""],[1,"btn","btn-primary","btn-sm","mr-1",3,"disabled","click"],[1,"btn","btn-primary","btn-sm","m-1",3,"disabled","click"],["mat-dialog-close","",1,"btn","btn-outline-secondary","btn-sm",3,"click"],["type","radio","required","","name","fCellRole",3,"value","ngModel","ngModelChange"],["fCellRole","ngModel"],["for","size"],["type","number","step","1","min","1","id","size","required","","name","fSize",1,"form-control",3,"ngModel","ngModelChange"],["fSize","ngModel"],["for","dataLabel"],["type","text","id","dataLabel","placeholder","e.g. TOC1 SD","required","","minlength","2","name","fDataLabel",1,"form-control",3,"ngModel","ngModelChange"],["fDataLabel","ngModel"],["for","timeType"],["id","timeType","required","","name","fTimeType",1,"form-control",3,"ngModel","ngModelChange"],["fTimeType","ngModel"],[3,"value",4,"ngFor","ngForOf"],["for","firstRow"],["type","number","id","firstRow","step","1","min","1","required","","placeholder","e.g. 2","name","fFirstRow",1,"form-control",3,"ngModel","ngModelChange"],["fFirstRow","ngModel"],["for","timeOffset"],["type","number","id","timeOffset","step","any","placeholder","e.g. -4","name","fTimeOffset",1,"form-control",3,"ngModel","ngModelChange"],["fTimeOffset","ngModel"],[3,"value"],["for","imgInterval"],["type","number","id","imgInterval","required","","step","any","min","0.01","placeholder","e.g. 1.5","name","fImgInterval",1,"form-control",3,"ngModel","ngModelChange"],["fImgInterval","ngModel"],["dismissOnTimeout","3000","dismissible","true","class","alert alert-danger","role","alert",4,"ngFor","ngForOf"],["dismissOnTimeout","3000","dismissible","true","role","alert",1,"alert","alert-danger"]],template:function(e,t){if(1&e&&(p.Tb(0,"h4",0),p.Dc(1,"Describe data column(s) "),p.Tb(2,"button",1),p.Tb(3,"span",2),p.Dc(4,"\xd7"),p.Sb(),p.Sb(),p.Sb(),p.Tb(5,"div",3),p.Tb(6,"p"),p.Dc(7," Columns "),p.Tb(8,"strong"),p.Dc(9),p.Sb(),p.Sb(),p.Tb(10,"form",null,4),p.Tb(12,"div",5),p.Tb(13,"label"),p.Dc(14,"Column type"),p.Sb(),p.Tb(15,"ul",6),p.Bc(16,C,4,3,"li",7),p.Sb(),p.Sb(),p.Bc(17,R,5,1,"div",8),p.Bc(18,I,5,1,"div",8),p.Bc(19,j,18,5,"div",9),p.Bc(20,F,2,1,"div",9),p.Sb(),p.Sb(),p.Tb(21,"div",10),p.Tb(22,"button",11),p.Zb("click",(function(){return t.acceptedAndNext()})),p.Dc(23,"OK and Next "),p.Sb(),p.Tb(24,"button",12),p.Zb("click",(function(){return t.accepted()})),p.Dc(25,"OK "),p.Sb(),p.Tb(26,"button",13),p.Zb("click",(function(){return t.hide()})),p.Dc(27,"Cancel"),p.Sb(),p.Sb()),2&e){var i=p.qc(11);p.Bb(9),p.Fc("[",t.rangeLabel,"]"),p.Bb(7),p.jc("ngForOf",t.cellRoles),p.Bb(1),p.jc("ngIf",!t.isTime()),p.Bb(1),p.jc("ngIf",t.isData()),p.Bb(1),p.jc("ngIf",t.isTime()),p.Bb(1),p.jc("ngIf",t.errors),p.Bb(2),p.jc("disabled",!i.form.valid),p.Bb(2),p.jc("disabled",!i.form.valid)}},directives:[D.h,D.d,D.e,b.D,b.r,b.s,u.l,u.m,D.c,b.x,b.d,b.z,b.q,b.t,b.v,b.m,b.A,b.u,b.C],encapsulation:2}),N),U=((L=function(){function e(t){a(this,e),this.show(t)}return r(e,[{key:"ngOnInit",value:function(){}},{key:"show",value:function(e){this.orgRange=e,this.rowNr=e.range.first.row,this.values=e.content}}]),e}()).\u0275fac=function(e){return new(e||L)(p.Nb(D.a))},L.\u0275cmp=p.Hb({type:L,selectors:[["ng-component"]],decls:24,vars:3,consts:[["mat-dialog-title","",1,"modal-title"],["mat-dialog-close","","aria-label","Close","type","button",1,"close","float-right"],["aria-hidden","true"],["mat-dialog-content","",1,"modal-body"],[1,"word_wrapping"],["mat-dialog-actions",""],[1,"btn","btn-primary","btn-sm","mr-1",3,"mat-dialog-close"],["mat-dialog-close","",1,"btn","btn-outline-secondary","btn-sm"]],template:function(e,t){1&e&&(p.Tb(0,"h4",0),p.Dc(1,"Use row content as data labels(s) "),p.Tb(2,"button",1),p.Tb(3,"span",2),p.Dc(4,"\xd7"),p.Sb(),p.Sb(),p.Sb(),p.Tb(5,"div",3),p.Tb(6,"div"),p.Tb(7,"p"),p.Dc(8,"Use values from row "),p.Tb(9,"strong"),p.Dc(10),p.Sb(),p.Dc(11," as data labels"),p.Sb(),p.Tb(12,"p"),p.Dc(13,"Existing labels and column types will be overwritten"),p.Sb(),p.Sb(),p.Tb(14,"div",4),p.Tb(15,"p"),p.Tb(16,"strong"),p.Dc(17,"Labels: "),p.Sb(),p.Dc(18),p.Sb(),p.Sb(),p.Sb(),p.Tb(19,"div",5),p.Tb(20,"button",6),p.Dc(21,"Copy labels"),p.Sb(),p.Tb(22,"button",7),p.Dc(23,"Cancel"),p.Sb(),p.Sb()),2&e&&(p.Bb(10),p.Fc("[",t.rowNr,"]"),p.Bb(8),p.Ec(t.values),p.Bb(2),p.jc("mat-dialog-close",t.orgRange))},directives:[D.h,D.d,D.e,D.c],encapsulation:2}),L),G=s("9+85");function _(e,t){1&e&&(p.Tb(0,"div",9),p.Tb(1,"strong"),p.Dc(2,"Missing data labels. Please use one of the methods:"),p.Sb(),p.Tb(3,"ul"),p.Tb(4,"li"),p.Dc(5,"Click and draw over the headers of columns to select the range and assign the data labels in the popup dialog. "),p.Sb(),p.Tb(6,"li"),p.Dc(7,"Use the form below to manually provide the column range and the data label."),p.Sb(),p.Sb(),p.Sb())}function P(e,t){if(1&e){var i=p.Ub();p.Tb(0,"div"),p.Tb(1,"div",10),p.Tb(2,"label",11),p.Dc(3,"Time parameter: offset (hours)"),p.Sb(),p.Tb(4,"input",12,13),p.Zb("ngModelChange",(function(e){return p.sc(i),p.dc(2).timeColumnDescription.details.timeOffset=e})),p.Sb(),p.Sb(),p.Sb()}if(2&e){var n=p.dc(2);p.Bb(4),p.jc("ngModel",n.timeColumnDescription.details.timeOffset)}}function z(e,t){if(1&e){var i=p.Ub();p.Tb(0,"li",17),p.Zb("click",(function(){p.sc(i);var e=t.$implicit;return p.dc(3).editBlock(e)})),p.Tb(1,"span"),p.Tb(2,"strong"),p.Dc(3),p.Sb(),p.Dc(4),p.Sb(),p.Tb(5,"a",18),p.Zb("click",(function(){p.sc(i);var e=t.$implicit;return p.dc(3).deleteBlock(e)})),p.Tb(6,"i",19),p.Dc(7,"delete_forever"),p.Sb(),p.Sb(),p.Sb()}if(2&e){var n=t.$implicit;p.Bb(3),p.Ec(n.topcountLabel),p.Bb(1),p.Fc(" : ",n.value,"")}}function Z(e,t){if(1&e&&(p.Tb(0,"div"),p.Tb(1,"p"),p.Tb(2,"strong"),p.Dc(3,"Columns descriptions (select to edit)"),p.Sb(),p.Sb(),p.Tb(4,"div",14),p.Tb(5,"ul",15),p.Bc(6,z,8,2,"li",16),p.Sb(),p.Sb(),p.Sb()),2&e){var i=p.dc(2);p.Bb(6),p.jc("ngForOf",i.dataBlocks)}}function K(e,t){if(1&e){var i=p.Ub();p.Tb(0,"th",20),p.Zb("mousedown",(function(){p.sc(i);var e=t.$implicit;return p.dc(2).thSelectStart(e)}))("mouseup",(function(){p.sc(i);var e=t.$implicit;return p.dc(2).thSelectEnd(e)})),p.Dc(1),p.Sb()}if(2&e){var n=t.$implicit,o=p.dc(2);p.Bb(1),p.Fc(" ",o.dataModel.th[n]," ")}}function V(e,t){if(1&e&&(p.Tb(0,"td"),p.Dc(1),p.Sb()),2&e){var i=t.$implicit,n=p.dc().$implicit,o=p.dc(2);p.yc("background-color",o.bgColors[i]?o.bgColors[i]:"inherited"),p.Bb(1),p.Fc("",n[i]," ")}}function $(e,t){if(1&e&&(p.Tb(0,"tr"),p.Tb(1,"td",21),p.Dc(2),p.Sb(),p.Bc(3,V,2,3,"td",22),p.Sb()),2&e){var i=t.index,n=p.dc(2);p.Bb(2),p.Ec(n.dataModel.specialRowsLabels[i]),p.Bb(1),p.jc("ngForOf",n.visibleColIx)}}function q(e,t){if(1&e&&(p.Tb(0,"td"),p.Dc(1),p.Sb()),2&e){var i=t.$implicit,n=p.dc().$implicit,o=p.dc(2);p.yc("background-color",o.bgColors[i]?o.bgColors[i]:"inherited"),p.Bb(1),p.Fc("",n[i]," ")}}function H(e,t){if(1&e&&(p.Tb(0,"tr"),p.Tb(1,"td",21),p.Dc(2),p.Sb(),p.Bc(3,q,2,3,"td",22),p.Sb()),2&e){var i=t.index,n=p.dc(2);p.Bb(2),p.Ec(n.dataModel.rowsLabels[i]),p.Bb(1),p.jc("ngForOf",n.visibleColIx)}}function X(e,t){if(1&e){var i=p.Ub();p.Tb(0,"div"),p.Ob(1,"hr"),p.Tb(2,"div"),p.Tb(3,"h4"),p.Dc(4,"Current Topcount import parameters "),p.Sb(),p.Bc(5,_,8,0,"div",1),p.Bc(6,P,6,1,"div",0),p.Bc(7,Z,7,1,"div",2),p.Tb(8,"div"),p.Tb(9,"button",3),p.Zb("click",(function(){return p.sc(i),p.dc().accept()})),p.Dc(10,"Import timeseries "),p.Sb(),p.Sb(),p.Sb(),p.Ob(11,"hr"),p.Tb(12,"h4"),p.Dc(13,"Data table "),p.Tb(14,"small"),p.Dc(15,"(only top rows)"),p.Sb(),p.Sb(),p.Tb(16,"p"),p.Dc(17,"Click on column headers for description options"),p.Sb(),p.Tb(18,"div",4),p.Tb(19,"table",5),p.Tb(20,"thead"),p.Tb(21,"tr",6),p.Ob(22,"th"),p.Bc(23,K,2,1,"th",7),p.Sb(),p.Sb(),p.Tb(24,"tbody"),p.Bc(25,$,4,2,"tr",8),p.Bc(26,H,4,2,"tr",8),p.Sb(),p.Sb(),p.Sb(),p.Sb()}if(2&e){var n=p.dc();p.Bb(5),p.jc("ngIf",!n.hasData()),p.Bb(1),p.jc("ngIf",n.timeColumnDescription),p.Bb(1),p.jc("ngIf",n.dataBlocks&&n.dataBlocks.length>0),p.Bb(2),p.jc("disabled",n.blocked||!n.hasTime()||!n.hasData()),p.Bb(14),p.jc("ngForOf",n.visibleColIx),p.Bb(2),p.jc("ngForOf",n.dataModel.specialRows),p.Bb(1),p.jc("ngForOf",n.dataModel.rows)}}var J,Y=((J=function(){function e(t,i){a(this,e),this.confDialogs=t,this.matDialog=i,this.onAccepted=new p.o,this.blocked=!1,this.confirmDataLoss=!1,this.columnBlocks=new w.e,this.dataBlocks=[],this.bgColors=[]}return r(e,[{key:"topcountHeaders",value:function(){var e=[];return["A","B","C","D","E","F","G","H"].forEach((function(t){for(var i=1;i<13;i++)e.push(t+i)})),e}},{key:"initTime",value:function(){var e=new w.b(new w.a(0,0),new w.a(0,0)),t=new w.g;t.timeType=w.h.TIME_IN_HOURS,t.timeOffset=0,t.firstRow=1;var i=new w.c(e);i.role=w.d.TIME,i.details=t,this.timeColumnDescription=i}},{key:"hasTime",value:function(){return this.timeColumnDescription&&this.timeColumnDescription.role===w.d.TIME}},{key:"hasData",value:function(){return this.dataBlocks.some((function(e){return e.details.role===w.d.DATA}))}},{key:"rowSelected",value:function(e){var t=this.joinRow(e),i=new w.a(0,e+1),n=new w.b(i,i),o=new w.c(n,t);this.askCopyRow(o)}},{key:"askCopyRow",value:function(e){var t=this;this.matDialog.open(U,{data:e,autoFocus:!1}).afterClosed().subscribe((function(e){e&&t.copyRowAsLabels(e)}))}},{key:"thSelectStart",value:function(e){this.thSelectedCol=e}},{key:"thSelectEnd",value:function(e){if(void 0!==this.thSelectedCol&&null!=this.thSelectedCol){var t=new w.a(this.thSelectedCol+1,1),i=new w.a(e+1,1),n=new w.b(t,i),o=new w.c(n,void 0),a=this.columnBlocks.details(t.col);a&&(o.role=a.role,o.details=a.details),this.thSelectedCol=void 0,this.askColumnsDetails(o)}}},{key:"askColumnsDetails",value:function(e){var t=this,i=this.rangeToTopcountLabel(e),n=new A(e,this.lastCol,!1,i);this.matDialog.open(x,{data:n,autoFocus:!1}).afterClosed().subscribe((function(e){if(e){var i=e.follow;e.follow=void 0,t.setColumnType(e),i&&t.askColumnsDetails(i)}}))}},{key:"rangeToTopcountLabel",value:function(e){var t=e.range;return Object(w.j)(t.firstCol)+"-"+Object(w.j)(t.lastCol)}},{key:"editBlock",value:function(e){e&&this.askColumnsDetails(e.details)}},{key:"deleteBlock",value:function(e){e&&(e.details.role==w.d.TIME?this.clearTime():this.resetRegion(e.details.range),this.dataBlocks=this.columnBlocks.blocks)}},{key:"accept",value:function(){var e=this;if(this.hasTime()&&this.hasData()){var t=Promise.resolve(!0);this.containsDataGaps()&&(t=this.confDialogs.confirm("Do you want to import partial data?","Not all columns are described. Data columns without annotations will not be imported.<br>Click OK if you want to proceed.").toPromise()),t.then((function(t){return!!t&&(!e.confirmDataLoss||e.confDialogs.confirm("Do you want to replace the existing data?","It will also erase all analysis results. <br>Click OK if you want to proceed.").toPromise())})).then((function(t){t&&e.emitParameters()})).catch((function(e){return console.log("Dialog error: "+e)}))}}},{key:"containsDataGaps",value:function(){if(this.dataBlocks.length<1)return!0;if(this.dataBlocks[this.dataBlocks.length-1].end<this.lastCol)return!0;for(var e=this.dataBlocks[0].start-1,t=0;t<this.dataBlocks.length;t++){if(this.dataBlocks[t].start!==e+1)return!0;e=this.dataBlocks[t].end}return!1}},{key:"emitParameters",value:function(){var e=new g.c;e.timeColumn=this.timeColumnDescription,e.dataBlocks=this.dataBlocks.map((function(e){return e.details})),this.onAccepted.emit(e)}},{key:"joinRow",value:function(e){return this.dataModel.rows[e].slice(0,10).join(" | ")+"..."}},{key:"setColumnType",value:function(e){if(e&&void 0!==e.role){var t;switch(this.timeColumnDescription&&(e.role==w.d.TIME||this.timeColumnDescription.firstCol>=e.firstCol&&this.timeColumnDescription.firstCol<=e.lastCol)&&this.clearTime(),e.role){case w.d.IGNORED:t="ign.";break;case w.d.BACKGROUND:t="bcg.";break;case w.d.TIME:t="Time";break;case w.d.DATA:t=e.details.dataLabel}this.columnBlocks.insert(e,t),this.columnBlocks.merge(),this.dataBlocks=this.columnBlocks.blocks;var i=this.dataModel.specialRows[0];if(i)for(var n=e.firstCol-1;n<e.lastCol;n++)i[n]=t;e.role==w.d.TIME&&(this.timeColumnDescription=e),this.updateBackgrounds()}}},{key:"copyRowAsLabels",value:function(e){var t=this;if(e){this.clearTime();var i=this.dataModel.specialRows[0];this.dataModel.rows[e.range.first.row-1].forEach((function(e,n){if(e){var o=new w.f(e),a=new w.a(n+1,0),l=new w.b(a,a),r=new w.c(l,void 0);r.details=o,r.role=w.d.DATA,t.columnBlocks.insert(r,e),i[n]=e}})),this.columnBlocks.merge(),this.dataBlocks=this.columnBlocks.blocks,this.updateBackgrounds()}}},{key:"clearTime",value:function(){this.timeColumnDescription&&(this.resetRegion(this.timeColumnDescription.range),this.timeColumnDescription=void 0)}},{key:"updateBackgrounds",value:function(){for(var e=0;e<this.dataBlocks.length;e++){var t=this.dataBlocks[e],i=S.color(e);t.details.role==w.d.IGNORED&&(i=S.IGNORED_BG),t.details.role==w.d.BACKGROUND&&(i=S.NOISE_BG),t.details.role==w.d.TIME&&(i=S.TIME_BG);for(var n=t.start-1;n<t.end;n++)this.bgColors[n]=i}}},{key:"resetRegion",value:function(e){this.resetLabels(e),this.resetBackground(e),this.columnBlocks.delete(e)}},{key:"resetBackground",value:function(e){for(var t=e.firstCol-1;t<e.lastCol;t++)this.bgColors[t]="transparent"}},{key:"resetLabels",value:function(e){for(var t=this.dataModel.specialRows[0],i=e.firstCol-1;i<e.lastCol;i++)t[i]="-"}},{key:"fakeData",value:function(){var e=[],t=[];t.push("");for(var i=1;i<25;i++)t.push("RO "+i);e.push(t),(t=[]).push("");for(var n=1;n<25;n++)t.push("cls"+Math.round(n/3));e.push(t);for(var o=1;o<5;o++){(t=[]).push(""+o);for(var a=1;a<25;a++)t.push(""+(o+a));e.push(t)}return e}},{key:"dataTable",set:function(e){if(e&&!(e.length<1)){var t=new k(e,this.topcountHeaders(),void 0,[[]],["L."]);this.dataModel=t,this.lastCol=t.width,this.visibleColIx=[];for(var i=0;i<t.width;i++)this.visibleColIx.push(i);this.initTime()}}}]),e}()).\u0275fac=function(e){return new(e||J)(p.Nb(G.a),p.Nb(D.b))},J.\u0275cmp=p.Hb({type:J,selectors:[["bd2-describe-topcount-table"]],inputs:{blocked:"blocked",confirmDataLoss:"confirmDataLoss",dataTable:"dataTable"},outputs:{onAccepted:"onAccepted"},decls:1,vars:1,consts:[[4,"ngIf"],["type","danger","class","alert alert-danger","role","alert",4,"ngIf"],["style","",4,"ngIf"],[1,"btn","btn-primary",3,"disabled","click"],[2,"overflow-x","auto"],["role","grid",1,"table","table-bordered","excel-table",2,"width","auto"],["role","row"],[3,"mousedown","mouseup",4,"ngFor","ngForOf"],[4,"ngFor","ngForOf"],["type","danger","role","alert",1,"alert","alert-danger"],[1,"form-group"],["for","timeOffset"],["type","number","id","timeOffset","step","any","placeholder","e.g. -4","name","fTimeOffset",1,"form-control","short-input",3,"ngModel","ngModelChange"],["fTimeOffset","ngModel"],[2,"max-height","20em","overflow-y","auto","margin-bottom","1em"],[1,"list-group"],["class","list-group-item",3,"click",4,"ngFor","ngForOf"],[1,"list-group-item",3,"click"],["role","button","aria-label","delete",1,"float-right",3,"click"],[1,"material-icons","bd-icon","bd-primary"],[3,"mousedown","mouseup"],[1,"rowh"],[3,"background-color",4,"ngFor","ngForOf"]],template:function(e,t){1&e&&p.Bc(0,X,27,7,"div",0),2&e&&p.jc("ngIf",t.dataModel)},directives:[u.m,u.l,b.v,b.d,b.q,b.t],encapsulation:2}),J);function Q(e,t){if(1&e){var i=p.Ub();p.Tb(0,"bd2-describe-topcount-table",1),p.Zb("onAccepted",(function(e){return p.sc(i),p.dc().import(e)})),p.Sb()}if(2&e){var n=p.dc();p.jc("dataTable",n.dataTable)("blocked",n.blocked)("confirmDataLoss",null==n.assay?null:n.assay.features.hasTSData)}}var W,ee,te,ie=[{path:"",children:[{path:":format/:fileId",component:(W=function(n){!function(e,i){if("function"!=typeof i&&null!==i)throw new TypeError("Super expression must either be null or a function");e.prototype=Object.create(i&&i.prototype,{constructor:{value:e,writable:!0,configurable:!0}}),i&&t(e,i)}(s,n);var l=i(s);function s(e,t,i){var n;return a(this,s),(n=l.call(this,i)).fileViewService=e,n.route=t,n.blocked=!1,n.titlePart=" Import Data",n}return r(s,[{key:"ngOnInit",value:function(){var t=this;e(o(s.prototype),"ngOnInit",this).call(this),this.route.params.forEach((function(e){var i=e.fileId,n=e.format;i&&n?t.loadData(n,i):console.log(t.constructor.name+" null parameters")}))}},{key:"loadData",value:function(e,t){var i=this;this.format=g.e.get(e),this.fileId=t,this.blocked=!1,this.fileViewService.getSimpleTableView(t).then((function(e){i.dataTable=e})).catch((function(e){i.feedback.error(e)}))}},{key:"import",value:function(e){var t=this;if(e&&this.fileId&&this.format){this.blocked=!0;var i=new g.d;i.fileId=this.fileId,i.importFormat=this.format,i.importParameters=e,this.experimentService.importTimeSeries(this.assay,i).toPromise().then((function(e){return t.feedback.success("Imported "+e.imported+" timeseries"),e})).then((function(){return t.refreshModel()})).then((function(e){return t.goToTSView()})).catch((function(e){t.blocked=!1,t.feedback.error(e)}))}}},{key:"goToTSView",value:function(){var e=this.expHomePath();e.push("data"),e.push("view"),e.push("ts"),this.router.navigate(e)}}]),s}(m.a),W.\u0275fac=function(e){return new(e||W)(p.Nb(T),p.Nb(h.a),p.Nb(y.a))},W.\u0275cmp=p.Hb({type:W,selectors:[["ng-component"]],features:[p.Ab([]),p.yb],decls:3,vars:1,consts:[[3,"dataTable","blocked","confirmDataLoss","onAccepted",4,"ngIf"],[3,"dataTable","blocked","confirmDataLoss","onAccepted"]],template:function(e,t){1&e&&(p.Tb(0,"h3"),p.Dc(1,"Timeseries import"),p.Sb(),p.Bc(2,Q,1,3,"bd2-describe-topcount-table",0)),2&e&&(p.Bb(2),p.jc("ngIf","TOPCOUNT"===(null==t.format?null:t.format.name)))},directives:[u.m,Y],encapsulation:2}),W)}]}],ne=((te=function e(){a(this,e)}).\u0275mod=p.Lb({type:te}),te.\u0275inj=p.Kb({factory:function(e){return new(e||te)},imports:[[h.f.forChild(ie)],h.f]}),te),oe=((ee=function e(){a(this,e)}).\u0275mod=p.Lb({type:ee}),ee.\u0275inj=p.Kb({factory:function(e){return new(e||ee)},imports:[[u.c,b.k,b.y,d.a,f.a,ne]]}),ee)}}])}();