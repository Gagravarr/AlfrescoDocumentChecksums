<#escape x as jsonUtils.encodeJSONString(x)>
{
   "hashes": [ <#list hashes as h>"${h}"<#if h_has_next>,</#if></list> ]
}
</#escape>
