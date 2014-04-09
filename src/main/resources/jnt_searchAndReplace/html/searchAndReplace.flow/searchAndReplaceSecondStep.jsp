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

<template:addResources type="javascript" resources="jquery.min.js,jquery-ui.min.js,admin-bootstrap.js"/>
<template:addResources type="javascript" resources="datatables/jquery.dataTables.js,i18n/jquery.dataTables-${currentResource.locale}.js,datatables/dataTables.bootstrap-ext.js"/>
<template:addResources type="javascript" resources="bootbox.min.js"/>



<template:addResources type="inlinejavascript">
    <script type="text/javascript">
        $(document).ready(function(){
            $('#listNodes_table').dataTable({
                "sDom": "<'row-fluid'<'span6'l><'span6'f>r>t<'row-fluid'<'span6'i><'span6'p>>",
                "iDisplayLength":25,
                "sPaginationType": "bootstrap",
                "aaSorting": [] //this option disable sort by default, the user steal can use column names to sort the table
            });

            $('.searchAndReplaceSubmit').on('click', function(){
                var boolean = true;

                /*if($('#termToReplace').val() == ""){
                    $('#termToReplaceError').fadeIn('slow').delay(4000).fadeOut('slow');
                    boolean = false;
                }
                if($('#replacementTerm').val() == ""){
                    $('#replacementTermError').fadeIn('slow').delay(4000).fadeOut('slow');
                    boolean = false;
                }*/
                return boolean;
            })
        });

        function checkAll(){
            if($(".selectAll").is(':checked')){
                $(".select").prop('checked', true);
            }else{
                $(".select").prop('checked', false);
            }
        }

        function bbDisplay(name, url){
            bootbox.alert("<h1>" + name + "</h1><br />" + url + ".html.ajax", function(){});
        }
    </script>
</template:addResources>

<div>
    <h1>Search And Replace</h1>
    <table cellpadding="0" cellspacing="0" border="0" class="table table-hover table-bordered" id="listNodes_table">
        <thead>
            <tr>
                <th>
                    <input type="checkbox" value="" class="selectAll" onclick="checkAll()">
                    <br />
                    Select All
                </th>
                <th>
                    Nodes
                </th>
                <th>
                    <fmt:message key='mix_created'/>
                </th>
                <th>
                    Node Path
                </th>
            </tr>
        </thead>
        <tbody>
            <c:forEach items="${listNodes}" var="id">
                <jcr:node var="node" uuid="${id}"/>
                <tr>
                    <td>
                        <input type="checkbox" value="" class="select">
                    </td>
                    <td>
                        <a href="#" onclick="bbDisplay('${functions:escapeJavaScript(node.name)}','${url.context}${url.basePreview}${functions:escapeJavaScript(node.path)}');return false;" style="text-decoration: none;">
                            ${node.name}
                        </a>
                    </td>
                    <td>
                        <em><fmt:formatDate value="${node.properties['jcr:created'].date.time}" pattern="dd, MMMM yyyy HH:mm"/></em>
                        &nbsp;<fmt:message key="by"/>&nbsp;
                        <strong>${node.properties['jcr:createdBy'].string}</strong>
                    </td>
                    <td>
                        ${node.path}
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>












<%--
<jcr:nodeType var="nodeTypeFields" name="${searchAndReplace.nodeType}"/>

${searchAndReplace.nodeType}<br />
${searchAndReplace.startNode}<br />
${searchAndReplace.nodeTypeField}<br />
${searchAndReplace.termToReplace}<br />
${searchAndReplace.matchType}<br />
${searchAndReplace.replacementTerm}<br />

<div>
    <h1>Search And Replace</h1>
    <form:form action="${flowExecutionUrl}" method="post" cssClass="well form-horizontal" modelAttribute="searchAndReplace">
        <div class="control-group">
            <form:label path="nodeTypeField" cssClass="control-label">
                <fmt:message key="jnt_searchAndReplace.nodeTypeField"/>
            </form:label>
            <div class="controls">
                <form:select path="nodeTypeField">
                    <c:forEach items="${nodeTypeFields.declaredPropertyDefinitions}" var="nodeTypeField">
                        <option value="${nodeTypeField.name}">${nodeTypeField.name}</option>
                    </c:forEach>
                </form:select>
            </div>
        </div>
        <div class="control-group">
            <form:label path="termToReplace" cssClass="control-label">
                <fmt:message key="jnt_searchAndReplace.termToReplace"/>
            </form:label>
            <div class="controls">
                <form:input path="termToReplace"/>
                <span id="termToReplaceError" class="hide text-error"><fmt:message key="jnt_searchAndReplace.termToReplace.error"/></span>
                <form:errors path="termToReplace" cssClass="text-error"/>
            </div>
        </div>
        <div class="control-group">
            <form:label path="matchType" cssClass="control-label">
                <fmt:message key="jnt_searchAndReplace.matchType"/>
            </form:label>
            <div class="controls">
                <form:select path="matchType">
                    <option value="="><fmt:message key='jnt_searchAndReplace.matchType.equal'/></option>
                    <option value="like"><fmt:message key='jnt_searchAndReplace.matchType.like'/></option>
                    <option value="empty"><fmt:message key='jnt_searchAndReplace.matchType.empty'/></option>
                </form:select>
            </div>
        </div>
        <div class="control-group">
            <form:label path="replacementTerm" cssClass="control-label">
                <fmt:message key="jnt_searchAndReplace.replacementTerm"/>
            </form:label>
            <div class="controls">
                <form:input path="replacementTerm"/>
                <span id="replacementTermError" class="hide text-error"><fmt:message key="jnt_searchAndReplace.replacementTerm.error"/></span>
                <form:errors path="replacementTerm" cssClass="text-error"/>
            </div>
        </div>
        <div class="control-group">
            <button class="btn" name="_eventId_searchAndReplacePrevious">
                <fmt:message key="label.previous"/>
            </button>
            <button class="btn btn-danger" name="_eventId_searchAndReplaceCancel">
                <fmt:message key="label.cancel"/>
            </button>
            &lt;%&ndash; searchAndReplaceSubmit class is used by jQuery don't remove it !&ndash;%&gt;
            <button class="btn btn-primary searchAndReplaceSubmit" type="submit" name="_eventId_searchAndReplaceGoToThirdStep">
                <fmt:message key="label.next"/>
            </button>
        </div>
    </form:form>
</div>
--%>
