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

                if($('#nodeType').val() == ""){
                    $('#nodeTypeError').fadeIn('slow').delay(4000).fadeOut('slow');
                    boolean = false;
                }
                if($('#startNode').val() == ""){
                    $('#startNodeError').fadeIn('slow').delay(4000).fadeOut('slow');
                    boolean = false;
                }
                return boolean;
            })
        });
    </script>
</template:addResources>

<jcr:propertyInitializers var="nodesTypesList" nodeType="jnt:searchAndReplace" name="nodesTypes"/>

<div>
    <h1>Search And Replace</h1>
    <form:form action="${flowExecutionUrl}" method="post" cssClass="well form-horizontal" modelAttribute="searchAndReplace">
        <div class="control-group">
            <form:label path="nodeType" cssClass="control-label">
                <fmt:message key="jnt_searchAndReplace.nodeType"/>
            </form:label>
            <div class="controls">
                <form:select path="nodeType">
                    <form:option value=""><fmt:message key="selection.label"/> ...</form:option>
                    <c:forEach items="${nodesTypesList}" var="nodeType">
                        <option value="${nodeType.value.string}">${nodeType.displayName}</option>
                    </c:forEach>
                </form:select>
                <span id="nodeTypeError" class="hide text-error"><fmt:message key="jnt_searchAndReplace.nodeType.error"/></span>
                <form:errors path="nodeType" cssClass="text-error"/>
            </div>
        </div>
        <div class="control-group">
            <form:label path="startNode" cssClass="control-label">
                <fmt:message key="jnt_searchAndReplace.startNode"/>
            </form:label>
            <div class="controls">
                <form:input path="startNode" value="${renderContext.site.path}/home/test.html" />
                <span id="startNodeError" class="hide text-error"><fmt:message key="jnt_searchAndReplace.startNode.error"/></span>
                <form:errors path="startNode" cssClass="text-error"/>
            </div>
        </div>
        <div class="control-group">
                <%-- searchAndReplaceSubmit class is used by jQuery don't remove it !--%>
            <button class="btn btn-primary searchAndReplaceSubmit" type="submit" name="_eventId_searchAndReplaceGoToSecondStep">
                <fmt:message key="label.next"/>
            </button>
        </div>
    </form:form>
</div>