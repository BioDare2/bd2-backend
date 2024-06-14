var r=class r{constructor(t,e,s){this._id=t,this._name=e,this._label=s}get id(){return this._id}get name(){return this._name}get label(){return this._label}static get(t){return r.getValuesMap().get(t)}static getValuesMap(){return r.valuesMap||(r.valuesMap=r.initValuesMap()),r.valuesMap}static initValuesMap(){let t=new Map;return t.set(r.NONE.name,r.NONE),t.set(r.IGNORED.name,r.IGNORED),t.set(r.TIME.name,r.TIME),t.set(r.DATA.name,r.DATA),t.set(r.BACKGROUND.name,r.BACKGROUND),t.set(r.LABEL.name,r.LABEL),t}toJSON(){return this.name}};r.NONE=new r(0,"NONE","NONE"),r.IGNORED=new r(1,"IGNORED","Ignored"),r.TIME=new r(2,"TIME","Time"),r.DATA=new r(3,"DATA","Data"),r.BACKGROUND=new r(4,"BACKGROUND","Background noise"),r.LABEL=new r(5,"LABEL","Label");var b=r,n=class n{constructor(t,e,s,a){this._id=t,this._name=e,this._label=s,this._unit=a}get id(){return this._id}get name(){return this._name}get label(){return this._label}get unit(){return this._unit}static get(t){return n.getValuesMap().get(t)}static getValuesMap(){return n.valuesMap||(n.valuesMap=n.initValuesMap()),n.valuesMap}static initValuesMap(){let t=new Map;return t.set(n.NONE.name,n.NONE),t.set(n.TIME_IN_HOURS.name,n.TIME_IN_HOURS),t.set(n.TIME_IN_MINUTES.name,n.TIME_IN_MINUTES),t.set(n.TIME_IN_SECONDS.name,n.TIME_IN_SECONDS),t.set(n.IMG_NUMBER.name,n.IMG_NUMBER),t}toJSON(){return this.name}};n.NONE=new n(0,"NONE","NONE",""),n.TIME_IN_HOURS=new n(1,"TIME_IN_HOURS","time in hours","h"),n.TIME_IN_MINUTES=new n(2,"TIME_IN_MINUTES","time in minutes","m"),n.TIME_IN_SECONDS=new n(3,"TIME_IN_SECONDS","time in seconds","s"),n.IMG_NUMBER=new n(4,"IMG_NUMBER","image nr. (1-based)","img");var h=n,M=class{},p=class{constructor(t){this.dataLabel=t}},o="ABCDEFGHIJKLMNOPQRSTUVWXYZ";function c(i){i=i-1;let t=o.length;if(i<t)return o[i];if(i<t*t+t){let e=Math.floor(i/t)-1,s=i%t;return o[e]+o[s]}if(i<16384){let e=Math.floor(i/(t*t))-1,s=i%(t*t),a=Math.floor(s/t)-1;return s=i%t,o[e]+o[a]+o[s]}throw new RangeError("Unsupported column number: "+(i+1))}function d(i){if(i<1||i>96)throw new RangeError("Unsupported topcount column number: "+i);i=i-1;let t=Math.floor(i/12);return o[t]+(i%12+1)}var f=class i{constructor(t,e){this.col=t,this.row=e}get numericalLabel(){return""+this.col+"-"+this.row}get excelLabel(){return c(this.col)+this.row}get columnLetter(){return c(this.col)}get topCountWell(){return o[this.row-1]+this.col}clone(){return new i(this.col,this.row)}},T=class i{constructor(t,e){this.first=t,this.last=e;let s=this.normalize(t,e);this.first=s[0],this.last=s[1]}get firstCol(){return this.first.col}get lastCol(){return this.last.col}get fullRangeLabel(){return this.isSingleCell()?this.first.excelLabel:this.first.excelLabel+"-"+this.last.excelLabel}get columnRangeLabel(){return this.isSingleCell()?this.first.columnLetter:this.first.columnLetter+"-"+this.last.columnLetter}get topCountRangeLabel(){return this.isSingleCell()?this.first.topCountWell:this.first.topCountWell+"-"+this.last.topCountWell}clone(){return new i(this.first.clone(),this.last.clone())}normalize(t,e){if(t.row<=e.row&&t.col<=e.col)return[t,e];let s=Math.min(t.row,e.row),a=Math.min(t.col,e.col),m=Math.max(t.row,e.row),N=Math.max(t.col,e.col);return[new f(a,s),new f(N,m)]}isSingleCell(){return this.first.row===this.last.row&&this.first.col===this.last.col}size(){let t=this.last.col-this.first.col+1,e=this.last.row-this.first.row+1;return t*e}},L=class i{constructor(t,e){this.range=t,this.content=e}get fullRangeLabel(){return this.range.fullRangeLabel}get columnRangeLabel(){return this.range.columnRangeLabel}get topCountRangeLabel(){return this.range.topCountRangeLabel}get firstCol(){return this.range.firstCol}get lastCol(){return this.range.lastCol}clone(){let t=new i(this.range.clone(),this.content);return t.role=this.role,t.details=this.details,t}},E=class i{constructor(t,e,s,a){this.start=t,this.end=e,this.details=s,this.value=a}get label(){return this.start===this.end?c(this.start):c(this.start)+"-"+c(this.end)}get topcountLabel(){return this.start===this.end?d(this.start):d(this.start)+"-"+d(this.end)}trimEnd(t){if(t<=this.start)return;let e=this.details.clone();return e.range.last.col=t-1,new i(this.start,t-1,e,this.value)}trimBegining(t){if(t>=this.end)return;let e=this.details.clone();return e.range.first.col=t+1,new i(t+1,this.end,e,this.value)}isSimilar(t){return!(this.details.role!==t.details.role||this.value!==t.value)}},O=class{constructor(){this.columns=[]}get blocks(){let t=[],e=1;for(;e<this.columns.length;){let s=this.columns[e];s?(t.push(s),e=s.end+1):e++}return t}details(t){let e=this.columns[t];if(e)return e.details}delete(t){if(t.size()===0)return!1;let e=!1;return this.columns[t.firstCol]&&(this.trimPreviousAt(t.firstCol),e=!0),this.columns[t.lastCol]&&(this.trimBehindAt(t.lastCol),e=!0),this.clear(t),e}insert(t,e){if(t.range.size()===0)return!1;let s=!1,a=new E(t.range.first.col,t.range.last.col,t,e);return this.columns[a.start]&&(this.trimPreviousAt(a.start),s=!0),this.columns[a.end]&&(this.trimBehindAt(a.end),s=!0),this.put(a),s}merge(){let t=1;for(;t<this.columns.length;){let e=this.columns[t];if(!e)t++;else if(!this.columns[e.end+1])t=e.end+1;else{let s=this.columns[e.end+1];e.isSimilar(s)?(e.end=s.end,e.details.range.last=s.details.range.last,this.put(e)):t=s.start}}}trimPreviousAt(t){let e=this.columns[t];if(e.start===t)return;let s=e.trimEnd(t);this.put(s)}trimBehindAt(t){let e=this.columns[t];if(e.end===t)return;let s=e.trimBegining(t);this.put(s)}put(t){for(let e=t.start;e<=t.end;e++)this.columns[e]=t}clear(t){for(let e=t.firstCol;e<=t.lastCol;e++)this.columns[e]=void 0}};var l=class l{constructor(t,e,s){this.id=t,this.name=e,this.label=s}static getValuesMap(){return l.valuesMap||(l.valuesMap=l.initValuesMap()),l.valuesMap}static get(t){return l.getValuesMap().get(t)}static initValuesMap(){let t=new Map;return t.set(l.NONE.name,l.NONE),t.set(l.EXCEL_TABLE.name,l.EXCEL_TABLE),t.set(l.TOPCOUNT.name,l.TOPCOUNT),t.set(l.TAB_SEP.name,l.TAB_SEP),t.set(l.COMA_SEP.name,l.COMA_SEP),t}toJSON(){return this.name}};l.NONE=new l(0,"NONE","none"),l.EXCEL_TABLE=new l(1,"EXCEL_TABLE","Excel Table"),l.TOPCOUNT=new l(2,"TOPCOUNT","TopCount"),l.TAB_SEP=new l(3,"TAB_SEP","Tab-separated"),l.COMA_SEP=new l(4,"COMA_SEP","Coma-separated");var u=l,U=[u.EXCEL_TABLE,u.TAB_SEP,u.COMA_SEP,u.TOPCOUNT],g=class{},S=class extends g{constructor(){super(...arguments),this._class_name=".ExcelTSImportParameters"}},C=class{},x=class{constructor(t=-1,e=-1,s="",a=-1,m=-1,N="",A){this.colIx=t,this.colNumber=e,this.colName=s,this.rowIx=a,this.rowNumber=m,this.rowName=N,this.value=A,this.fake=!1}toJSON(){return{col:this.colNumber,row:this.rowNumber}}isBeforeOrSame(t){return this.colIx<=t.colIx&&this.rowIx<=t.rowIx}hasSameIx(t){return this.colIx===t.colIx&&this.rowIx===t.rowIx}},w=class{constructor(){this.inRows=!1,this.timeOffset=0,this.importLabels=!0,this.userLabels=[],this.containsBackgrounds=!1,this.backgroundsLabels=[]}get inLabel(){return this.inRows?"row":"column"}get inLabelNeg(){return this.inRows?"column":"row"}isTimeDefined(){return!(!this.firstTimeCell||!this.timeType||this.timeType==h.IMG_NUMBER&&(!this.imgInterval||+this.imgInterval<=0)||isNaN(Number(this.firstTimeCell.value)))}areLabelsCorrectlySelected(){return this.labelsSelection?this.areLabelsAfterTime():!1}areLabelsAfterTime(){return this.firstTimeCell&&this.labelsSelection?this.inRows?this.labelsSelection.colNumber<this.firstTimeCell.colNumber&&this.labelsSelection.colNumber>=0:this.labelsSelection.rowNumber<this.firstTimeCell.rowNumber&&this.labelsSelection.rowNumber>=0:!1}areLabelsCorrectlyAssigned(){return this.userLabels?this.userLabels.filter(t=>!!t).length>0:!1}isDataAfterTime(){return this.firstTimeCell&&this.dataStart?this.inRows?this.dataStart.rowNumber>this.firstTimeCell.rowNumber:this.dataStart.colNumber>this.firstTimeCell.colNumber:!1}isDataStartCorrectlySelected(){return this.dataStart?this.isDataAfterTime():!1}isComplete(){if(!this.isTimeDefined())return!1;if(this.importLabels){if(!this.areLabelsCorrectlySelected()||!this.isDataStartCorrectlySelected())return!1}else if(!this.areLabelsCorrectlyAssigned())return!1;return!0}summarizeLabels(t=10){if(!this.userLabels)return"No labels";let e=new Set;for(let s of this.userLabels)if(s&&(e.add(s),e.size>=t)){e.add("...");break}return[...e].join(", ")}},I=class extends w{constructor(){super(...arguments),this._class_name=".DataTableImportParameters"}};export{b as a,h as b,M as c,p as d,c as e,d as f,f as g,T as h,L as i,O as j,u as k,U as l,S as m,C as n,x as o,I as p};