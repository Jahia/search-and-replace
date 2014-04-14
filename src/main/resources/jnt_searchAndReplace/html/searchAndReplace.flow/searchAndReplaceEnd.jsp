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
        });
    </script>
</template:addResources>

<div>
    <h1>Search And Replace</h1>
    <table cellpadding="0" cellspacing="0" border="0" class="table table-hover table-bordered" id="listNodes_table">
        <thead>
            <tr>
                <th>
                    Nodes
                </th>
                <th>
                    <fmt:message key='mix_created'/>
                </th>
                <th>
                    Node Path
                </th>
                <th>
                    Update Status
                </th>
            </tr>
        </thead>
        <tbody>
        <c:forEach items="${searchAndReplace.listNodesUpdateSuccess}" var="id">
            <jcr:node var="node" uuid="${id}"/>
            <tr>
                <td>
                    <a href="#modal_${id}" role="button" data-toggle="modal" style="text-decoration: none;">
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
                <td>
                    Success
                </td>
            </tr>
        </c:forEach>
        <c:forEach items="${searchAndReplace.listNodesSkipped}" var="id">
            <jcr:node var="node" uuid="${id}"/>
            <tr>
                <td>
                    <a href="#modal_${id}" role="button" data-toggle="modal" style="text-decoration: none;">
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
                <td>
                    Skipped
                </td>
            </tr>
        </c:forEach>
        <c:forEach items="${searchAndReplace.listNodesUpdateFail}" var="id">
            <jcr:node var="node" uuid="${id}"/>
            <tr>
                <td>
                    <a href="#modal_${id}" role="button" data-toggle="modal" style="text-decoration: none;">
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
                <td>
                    Failed
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <form:form action="${flowExecutionUrl}" method="post" modelAttribute="searchAndReplace">
        <div class="control-group">
            <button class="btn btn-primary" name="_eventId_searchAndReplaceCancel">
                <%--<fmt:message key="label.cancel"/>--%>
                Other research
            </button>
        </div>
    </form:form>

    <c:forEach items="${searchAndReplace.listNodesUpdateSuccess}" var="id">
        <jcr:node var="node" uuid="${id}"/>
        <div class="modal hide fade" id="modal_${id}" tabindex="-1" role="dialog" aria-labelledby="modalTitle_${id}" aria-hidden="true">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h3 id="modalTitle_${id}">${node.name}</h3>
            </div>
            <div class="modal-body">
                <template:module node="${node}"/>
            </div>
            <div class="modal-footer">
                <a href="#" class="btn btn-primary" data-dismiss="modal">OK</a>
            </div>
        </div>
    </c:forEach>
    <c:forEach items="${searchAndReplace.listNodesSkipped}" var="id">
        <jcr:node var="node" uuid="${id}"/>
        <div class="modal hide fade" id="modal_${id}" tabindex="-1" role="dialog" aria-labelledby="modalTitle_${id}" aria-hidden="true">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h3 id="modalTitle_${id}">${node.name}</h3>
            </div>
            <div class="modal-body">
                <template:module node="${node}"/>
            </div>
            <div class="modal-footer">
                <a href="#" class="btn btn-primary" data-dismiss="modal">OK</a>
            </div>
        </div>
    </c:forEach>
    <c:forEach items="${searchAndReplace.listNodesUpdateFail}" var="id">
        <jcr:node var="node" uuid="${id}"/>
        <div class="modal hide fade" id="modal_${id}" tabindex="-1" role="dialog" aria-labelledby="modalTitle_${id}" aria-hidden="true">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h3 id="modalTitle_${id}">${node.name}</h3>
            </div>
            <div class="modal-body">
                <template:module node="${node}"/>
            </div>
            <div class="modal-footer">
                <a href="#" class="btn btn-primary" data-dismiss="modal">OK</a>
            </div>
        </div>
    </c:forEach>
</div>
