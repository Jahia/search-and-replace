<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="utility" uri="http://www.jahia.org/tags/utilityLib" %>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>
<%@ taglib prefix="functions" uri="http://www.jahia.org/tags/functions"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%--@elvariable id="currentNode" type="org.jahia.services.content.JCRNodeWrapper"--%>
<%--@elvariable id="currentResource" type="org.jahia.services.render.Resource"--%>
<%--@elvariable id="flowRequestContext" type="org.springframework.webflow.execution.RequestContext"--%>
<%--@elvariable id="out" type="java.io.PrintWriter"--%>
<%--@elvariable id="renderContext" type="org.jahia.services.render.RenderContext"--%>
<%--@elvariable id="script" type="org.jahia.services.render.scripting.Script"--%>
<%--@elvariable id="scriptInfo" type="java.lang.String"--%>
<%--@elvariable id="url" type="org.jahia.services.render.URLGenerator"--%>
<%--@elvariable id="workspace" type="java.lang.String"--%>
<%--@elvariable id="searchAndReplace" type="org.jahia.modules.searchandreplace.webflow.model.SearchAndReplace"--%>

<template:addResources type="inlinejavascript">
    <script type="text/javascript">
        $(document).ready(function(){
            $('.searchAndReplaceSubmit').on('click', function(){
                var boolean = true;

                if($('#termToReplace').val() == ""){
                    $('#termToReplaceError').fadeIn('slow').delay(4000).fadeOut('slow');
                    boolean = false;
                }

                return boolean;
            })
        });
    </script>
</template:addResources>

<h1>Search And Replace</h1>
<form:form action="${flowExecutionUrl}" method="post" cssClass="box-1" modelAttribute="searchAndReplace">
    <h2>Search</h2>
    <div class="input-append">
        <form:input path="termToReplace" cssClass="span6"/>
        <%-- searchAndReplaceSubmit class is used by jQuery don't remove it !--%>
        <button class="btn btn-primary searchAndReplaceSubmit" name="_eventId_searchAndReplaceGoToSecondStep" type="submit">
            <i class="icon-search icon-white"></i>
            <fmt:message key="jnt_searchAndReplace.termToReplace"/>
        </button>
    </div>
    <span id="termToReplaceError" class="hide text-error"><fmt:message key="jnt_searchAndReplace.termToReplace.error"/></span>
    <form:errors path="termToReplace" cssClass="text-error"/>
</form:form>
