function org_drools_guvnor_Guvnor(){var k=window,j=document,s=k.external,t,p,o='',w={},F=[],C=[],n=[],z,B;if(!k.__gwt_stylesLoaded){k.__gwt_stylesLoaded={};}if(!k.__gwt_scriptsLoaded){k.__gwt_scriptsLoaded={};}function u(){try{return s&&(s.gwtOnLoad&&k.location.search.indexOf('gwt.hybrid')== -1);}catch(a){return false;}}
function v(){if(t&&p){t(z,'org.drools.guvnor.Guvnor',o);}}
function r(){var i,h;j.write('<script id="__gwt_marker_org.drools.guvnor.Guvnor"><\/script>');h=j.getElementById('__gwt_marker_org.drools.guvnor.Guvnor');if(h){i=h.previousSibling;}function d(b){var a=b.lastIndexOf('/');return a>=0?b.substring(0,a+1):'';}
;if(i&&i.src){o=d(i.src);}if(o==''){var c=j.getElementsByTagName('base');if(c.length>0){o=c[c.length-1].href;}else{var g=j.location;var e=g.href;o=d(e.substr(0,e.length-g.hash.length));}}else if(o.match(/^\w+:\/\//)){}else{var f=j.createElement('img');f.src=o+'clear.cache.gif';o=d(f.src);}if(h){h.parentNode.removeChild(h);}}
function A(){var f=document.getElementsByTagName('meta');for(var d=0,g=f.length;d<g;++d){var e=f[d],h=e.getAttribute('name'),b;if(h){if(h=='gwt:property'){b=e.getAttribute('content');if(b){var i,c=b.indexOf('=');if(c>=0){h=b.substring(0,c);i=b.substring(c+1);}else{h=b;i='';}w[h]=i;}}else if(h=='gwt:onPropertyErrorFn'){b=e.getAttribute('content');if(b){try{B=eval(b);}catch(a){alert('Bad handler "'+b+'" for "gwt:onPropertyErrorFn"');}}}else if(h=='gwt:onLoadErrorFn'){b=e.getAttribute('content');if(b){try{z=eval(b);}catch(a){alert('Bad handler "'+b+'" for "gwt:onLoadErrorFn"');}}}}}}
function m(a,b){return b in F[a];}
function l(a){var b=w[a];return b==null?null:b;}
function E(d,e){var a=n;for(var b=0,c=d.length-1;b<c;++b){a=a[d[b]]||(a[d[b]]=[]);}a[d[c]]=e;}
function q(d){var e=C[d](),b=F[d];if(e in b){return e;}var a=[];for(var c in b){a[b[c]]=c;}if(B){B(d,a,e);}throw null;}
C['user.agent']=function(){var d=navigator.userAgent.toLowerCase();var b=function(a){return parseInt(a[1])*1000+parseInt(a[2]);};if(d.indexOf('opera')!= -1){return 'opera';}else if(d.indexOf('webkit')!= -1){return 'safari';}else if(d.indexOf('msie')!= -1){var c=/msie ([0-9]+)\.([0-9]+)/.exec(d);if(c&&c.length==3){if(b(c)>=6000){return 'ie6';}}}else if(d.indexOf('gecko')!= -1){var c=/rv:([0-9]+)\.([0-9]+)/.exec(d);if(c&&c.length==3){if(b(c)>=1008)return 'gecko1_8';}return 'gecko';}return 'unknown';};F['user.agent']={'gecko':0,'gecko1_8':1,'ie6':2,'opera':3,'safari':4};org_drools_guvnor_Guvnor.onScriptLoad=function(a){org_drools_guvnor_Guvnor=null;t=a;v();};r();A();var y;function x(){if(!p){p=true;v();if(j.removeEventListener){j.removeEventListener('DOMContentLoaded',x,false);}if(y){clearInterval(y);}}}
if(j.addEventListener){j.addEventListener('DOMContentLoaded',x,false);}var y=setInterval(function(){if(/loaded|complete/.test(j.readyState)){x();}},50);var D;try{E(['opera'],'06578621FE904D86F682B99B8793F67A');E(['ie6'],'27195588C06199786ACE352C959C94FD');E(['safari'],'3E212BEAD6ECDD5DD887522F685006DE');E(['gecko1_8'],'6B51D4DD6E8050CB7B59B6419280955A');E(['gecko'],'90DB09364331B80631173A220F59D02C');D=n[q('user.agent')];}catch(a){return;}D+='.cache.js';if(!__gwt_stylesLoaded['js/ext/resources/css/ext-all.css']){__gwt_stylesLoaded['js/ext/resources/css/ext-all.css']=true;document.write('<link rel="stylesheet" href="'+o+'js/ext/resources/css/ext-all.css">');}if(!__gwt_scriptsLoaded['js/GwtExt.js']){__gwt_scriptsLoaded['js/GwtExt.js']=true;document.write('<script language="javascript" src="'+o+'js/GwtExt.js"><\/script>');}if(!__gwt_scriptsLoaded['js/ext/adapter/ext/ext-base.js']){__gwt_scriptsLoaded['js/ext/adapter/ext/ext-base.js']=true;document.write('<script language="javascript" src="'+o+'js/ext/adapter/ext/ext-base.js"><\/script>');}if(!__gwt_scriptsLoaded['js/ext/ext-all.js']){__gwt_scriptsLoaded['js/ext/ext-all.js']=true;document.write('<script language="javascript" src="'+o+'js/ext/ext-all.js"><\/script>');}j.write('<script src="'+o+D+'"><\/script>');}
org_drools_guvnor_Guvnor.__gwt_initHandlers=function(i,e,j){var d=window,g=d.onresize,f=d.onbeforeunload,h=d.onunload;d.onresize=function(a){try{i();}finally{g&&g(a);}};d.onbeforeunload=function(a){var c,b;try{c=e();}finally{b=f&&f(a);}if(c!=null){return c;}if(b!=null){return b;}};d.onunload=function(a){try{j();}finally{h&&h(a);}};};org_drools_guvnor_Guvnor();