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

<template:addResources type="inlinejavascript">
    <script type="text/javascript">
        $(document).ready(function(){

        });
    </script>
</template:addResources>

${searchAndReplace.nodeType}<br />
${searchAndReplace.startNode}<br />
${searchAndReplace.termToReplace}<br />
${searchAndReplace.matchType}<br />
${searchAndReplace.replacementTerm}<br />

<div>
    <h1>Search And Replace</h1>

    <jcr:jqom var="searchResult">
        <query:selector nodeTypeName="${searchAndReplace.nodeType}"/>
        <query:descendantNode path="${searchAndReplace.startNode}"/>
        <c:if test="${searchAndReplace.matchType eq '='}">
            T dans le IF =
            <query:equalTo value="${searchAndReplace.termToReplace}" propertyName="${searchAndReplace.replacementTerm}"/>
        </c:if>
        <c:if test="${searchAndReplace.matchType eq 'like'}">
            T dans le IF like
            <query:fullTextSearch searchExpression="${searchAndReplace.termToReplace}" propertyName="${searchAndReplace.replacementTerm}"/>
        </c:if>
    </jcr:jqom>

    ${}

    <h2>Global field modification request : </h2>
    <p>
        Request summary :<br/><br>
        . Node Type : ${searchAndReplace.nodeType}<br>
        . From Node : ${searchAndReplace.startNode}<br/>
        . For Field : ${searchAndReplace.nodeTypeField}<br/>
        . Term to replace : ${searchAndReplace.termToReplace}<br/>
        . Search Operator : ${searchAndReplace.matchType}<br />
        . Replacement term : ${searchAndReplace.replacementTerm}<br/>
        . in language : ${renderContext.mainResourceLocale}<br/>
        <br/>
        The following nodes will be modified : <br/><br/>

        <form:form action="${flowExecutionUrl}" method="post" cssClass="well form-horizontal" modelAttribute="searchAndReplace">
            <c:forEach items="${searchResult.nodes}" var="matchedNode">
                <div class="control-group">
                    <div class="controls">
                        <label class="checkbox">
                            <input type="checkbox" name="nodesToUpdate" value="${matchedNode.path}"/>${matchedNode.path}
                        </label>
                    </div>
                </div>
            </c:forEach>
            <div class="control-group">
                <button class="btn" name="_eventId_searchAndReplacePrevious">
                    <fmt:message key="label.previous"/>
                </button>
                <button class="btn btn-danger" name="_eventId_searchAndReplaceCancel">
                    <fmt:message key="label.cancel"/>
                </button>
                <%-- searchAndReplaceSubmit class is used by jQuery don't remove it !--%>
                <button class="btn btn-primary" type="submit" name="_eventId_searchAndReplaceSubmit">
                    <fmt:message key="label.submit"/>
                </button>
            </div>
        </form:form>
    </p>
</div>