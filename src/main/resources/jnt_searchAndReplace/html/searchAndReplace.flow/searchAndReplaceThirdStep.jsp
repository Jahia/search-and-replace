<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="utility" uri="http://www.jahia.org/tags/utilityLib" %>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>
<%@ taglib prefix="functions" uri="http://www.jahia.org/tags/functions"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="query" uri="http://www.jahia.org/tags/queryLib" %>

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

<template:addResources type="javascript" resources="jquery.min.js,jquery-ui.min.js,jquery.blockUI.js,admin-bootstrap.js,workInProgress.js"/>
<template:addResources type="javascript" resources="jquery.highlight.js"/>

<fmt:message key="label.workInProgressTitle" var="i18nWaiting"/><c:set var="i18nWaiting" value="${functions:escapeJavaScript(i18nWaiting)}"/>

<template:addResources type="inlinejavascript">
    <script type="text/javascript">
        $(document).ready(function(){
            $('.searchAndReplaceSubmit').on('click', function(){
                var boolean = true;

                if($('#replacementTerm').val() == ""){
                    $('#replacementTermError').fadeIn('slow').delay(4000).fadeOut('slow');
                    boolean = false;
                }
                return boolean;
            })

            $('.preview').highlight('${searchAndReplace.termToReplace}', { caseSensitive: true });

            $('.highlight').css({ backgroundColor: '#ED6A32' });
        });
    </script>
</template:addResources>

<div>
    <h1>Search And Replace</h1>
    <form:form action="${flowExecutionUrl}" method="post" cssClass="well form-horizontal" modelAttribute="searchAndReplace" onsubmit="workInProgress('${i18nWaiting}')">
        <div class="control-group">
            <form:label path="termToReplace" cssClass="control-label">
                <fmt:message key="jnt_searchAndReplace.termToReplace"/>
            </form:label>
            <div class="controls">
                <form:input path="termToReplace" value="${searchAndReplace.replacementTerm}" disabled="true"/>
            </div>
        </div>
        <div class="control-group">
            <form:label path="replacementTerm" cssClass="control-label">
                <fmt:message key="jnt_searchAndReplace.replacementTerm"/>
            </form:label>
            <div class="controls">
                <form:input path="replacementTerm" value="${searchAndReplace.replacementTerm}"/>
                <span id="replacementTermError" class="hide text-error"><fmt:message key="jnt_searchAndReplace.replacementTerm.error"/></span>
                <form:errors path="replacementTerm" cssClass="text-error"/>
            </div>
        </div>
        <c:forEach items="${searchAndReplace.listNodesToBeUpdated}" var="id" varStatus="status">
            <c:if test="${status.first}">
                <form:hidden path="currentNodeInThirdStep" value="${id}"/>
                <jcr:node var="node" uuid="${id}"/>
                <div class="box-1">
                    <h1><fmt:message key="jnt_searchAndReplace.previewOfModification"/>&nbsp;${functions:abbreviate(node.displayableName,100,120,'...')}</h1>
                    <div class="preview">
                        <c:forEach items="${searchAndReplace.searchResultList}" var="searchResultNode">
                            <c:if test="${searchResultNode.nodeUuid eq id}">
                                <table class="table">
                                    <thead>
                                        <tr>
                                            <th>
                                                <fmt:message key="label.properties"/>
                                            </th>
                                            <th>
                                                <fmt:message key="label.value"/>
                                            </th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${searchResultNode.replaceableProperties}" var="properties">
                                            <tr>
                                                <td class="span2">
                                                    ${properties.key}
                                                </td>
                                                <td>
                                                    ${node.properties[properties.key].string}
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </c:if>
                        </c:forEach>
                    </div>
                </div>
            </c:if>
        </c:forEach>
        <div class="control-group">
            <button class="btn btn-danger" name="_eventId_searchAndReplaceCancel">
                <fmt:message key="label.cancel"/>
            </button>
            <c:if test="${fn:length(searchAndReplace.listNodesToBeUpdated) gt 1}">
                <button class="btn" name="_eventId_searchAndReplaceSkipThisNode">
                    <fmt:message key="jnt_searchAndReplace.skipThisNode"/>
                </button>
            </c:if>
            <%--searchAndReplaceSubmit class is used by jQuery don't remove it !--%>
            <button class="btn btn-primary searchAndReplaceSubmit" name="_eventId_searchAndReplaceInCurrentNode">
                <fmt:message key="jnt_searchAndReplace.replaceInCurrentNode"/>
            </button>
            <c:if test="${fn:length(searchAndReplace.listNodesToBeUpdated) gt 1}">
                <%--searchAndReplaceSubmit class is used by jQuery don't remove it !--%>
                <button class="btn btn-success searchAndReplaceSubmit" name="_eventId_searchAndReplaceAllNode">
                    <fmt:message key="jnt_searchAndReplace.replaceAllNode"/>
                </button>
            </c:if>
        </div>
    </form:form>
</div>

