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
            $('.searchAndReplaceSubmit').on('click', function(){
                var boolean = true;
                if($('.termToReplace').val() == ""){
                    $('#termToReplaceError').fadeIn('slow').delay(4000).fadeOut('slow');
                    boolean = false;
                }
                if($('#replacementTerm').val() == ""){
                    $('#replacementTermError').fadeIn('slow').delay(4000).fadeOut('slow');
                    boolean = false;
                }
                return boolean;
            })
        });
    </script>
</template:addResources>

<div>
    <h1>Search And Replace</h1>
    <form:form action="${flowExecutionUrl}" method="post" cssClass="well form-horizontal" modelAttribute="searchAndReplace">
        <c:forEach items="${searchAndReplace.listNodesToBeUpdated}" var="mapObject">
            <jcr:node var="node" uuid="${mapObject.key}"/>
            <jcr:nodeType var="nodeTypeFields" name="${node.properties['jcr:primaryType'].string}"/>
            <div class="box-1">
                <div class="control-group">
                    <form:label path="${mapObject.value.nodeTypeField}" cssClass="control-label">
                        <fmt:message key="jnt_searchAndReplace.nodeTypeField"/>
                    </form:label>
                    <div class="controls">
                        <form:select path="${mapObject.value.nodeTypeField}">
                            <c:forEach items="${nodeTypeFields.declaredPropertyDefinitions}" var="nodeTypeField">
                                <option value="${nodeTypeField.name}">${nodeTypeField.name}</option>
                            </c:forEach>
                        </form:select>
                    </div>
                </div>
                <div class="control-group">
                    <form:label path="${mapObject.value.termToReplace}" cssClass="control-label">
                        <fmt:message key="jnt_searchAndReplace.termToReplace"/>
                    </form:label>
                    <div class="controls">
                        <form:input cssClass="termToReplace" path="${mapObject.value.termToReplace}"/>
                        <span id="termToReplaceError_${mapObject.key}" class="hide text-error"><fmt:message key="jnt_searchAndReplace.termToReplace.error"/></span>
                        <form:errors path="${mapObject.value.termToReplace}" cssClass="text-error"/>
                    </div>
                </div>
                <div class="control-group">
                    <form:label path="${mapObject.value.replacementTerm}" cssClass="control-label">
                        <fmt:message key="jnt_searchAndReplace.replacementTerm"/>
                    </form:label>
                    <div class="controls">
                        <form:input cssClass="replacementTerm" path="${mapObject.value.replacementTerm}"/>
                        <span id="replacementTermError_${mapObject.key}" class="hide text-error"><fmt:message key="jnt_searchAndReplace.replacementTerm.error"/></span>
                        <form:errors path="${mapObject.value.replacementTerm}" cssClass="text-error"/>
                    </div>
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
            <%--searchAndReplaceSubmit class is used by jQuery don't remove it !--%>
            <button class="btn btn-primary searchAndReplaceSubmit" type="submit" name="_eventId_searchAndReplaceGoToFourthStep">
                <fmt:message key="label.next"/>
            </button>
        </div>
    </form:form>
</div>

<%--
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
            <query:equalTo value="${searchAndReplace.termToReplace}" propertyName="${searchAndReplace.replacementTerm}"/>
        </c:if>
        <c:if test="${searchAndReplace.matchType eq 'like'}">
            <query:fullTextSearch searchExpression="${searchAndReplace.termToReplace}" propertyName="${searchAndReplace.replacementTerm}"/>
        </c:if>
    </jcr:jqom>

    ${searchResult}

    <c:forEach items="${searchResult.nodes}" var="matchedNode">
    ${matchedNode.path}<br />
    </c:forEach>

    <h2>Global field modification request : </h2>
    <p>
        Request summary :<br/><br>
        <strong>. Node Type :</strong> ${searchAndReplace.nodeType}<br>
        <strong>. From Node :</strong> ${searchAndReplace.startNode}<br/>
        <strong>. For Field :</strong> ${searchAndReplace.nodeTypeField}<br/>
        <strong>. Term to replace :</strong> ${searchAndReplace.termToReplace}<br/>
        <strong>. Search Operator :</strong> ${searchAndReplace.matchType}<br />
        <strong>. Replacement term :</strong> ${searchAndReplace.replacementTerm}<br/>
        <strong>. in language :</strong> ${renderContext.mainResourceLocale}<br/>
        <br/>
        The following nodes will be modified : <br/><br/>

        <h1>Preview</h1>

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
                &lt;%&ndash; searchAndReplaceSubmit class is used by jQuery don't remove it !&ndash;%&gt;
                <button class="btn btn-primary" type="submit" name="_eventId_searchAndReplaceSubmit">
                    &lt;%&ndash;<fmt:message key="label.submit"/>&ndash;%&gt;
                    Perform replace
                </button>
            </div>
        </form:form>
    </p>
</div>--%>
