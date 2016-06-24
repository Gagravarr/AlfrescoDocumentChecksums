<#include "/org/alfresco/include/alfresco-template.ftl" />
<@templateHeader>
 </@>

<@templateBody>
   <@markup id="alf-hd">
   <div id="alf-hd">
      <@region scope="global" id="share-header" chromeless="true"/>
   </div>
   </@>
   <@markup id="bd">
    <div id="bd">
      <div id="yui-main">
         <div class="yui-b" id="alf-content">
               <@region id="document-checksums" scope="template" />
            </div>
      </div>
   </div>

   </@>
</@>

<@templateFooter>
   <@markup id="alf-ft">
   <div id="alf-ft">
      <@region id="footer" scope="global" />
   </div>
   </@>
</@>
