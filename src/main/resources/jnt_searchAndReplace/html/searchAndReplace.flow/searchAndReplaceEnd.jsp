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
<template:addResources type="javascript" resources="jquery.highlight.js"/>

<template:addResources type="inlinejavascript">
    <script type="text/javascript">
        $(document).ready(function(){
            $('#listNodesUpdateSuccess_table').dataTable({
                "sDom": "<'row-fluid'<'span6'l><'span6 text-right'f>r>t<'row-fluid'<'span6'i><'span6 text-right'p>>",
                "iDisplayLength":25,
                "sPaginationType": "bootstrap",
                "aaSorting": [] //this option disable sort by default, the user steal can use column names to sort the table
            })

            $('#listNodesSkipped_table').dataTable({
                "sDom": "<'row-fluid'<'span6'l><'span6 text-right'f>r>t<'row-fluid'<'span6'i><'span6 text-right'p>>",
                "iDisplayLength":25,
                "sPaginationType": "bootstrap",
                "aaSorting": [] //this option disable sort by default, the user steal can use column names to sort the table
            })

            $('#listNodesUpdateFail_table').dataTable({
                "sDom": "<'row-fluid'<'span6'l><'span6 text-right'f>r>t<'row-fluid'<'span6'i><'span6 text-right'p>>",
                "iDisplayLength":25,
                "sPaginationType": "bootstrap",
                "aaSorting": [] //this option disable sort by default, the user steal can use column names to sort the table
            });

            $('.preview').highlight('${searchAndReplace.replacementTerm}', { caseSensitive: true });

            $('.highlight').css({ backgroundColor: '#3399ff' });
        });
    </script>
</template:addResources>

<div class="box-1">
    <h1>Search And Replace</h1>

    <div class="accordion" id="accordion2">
        <c:if test="${fn:length(searchAndReplace.listNodesUpdateSuccess) gt 0}">
            <div class="accordion-group">
                <div class="accordion-heading alert alert-success">
                    <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion2" href="#collapseOne" style="text-decoration: none;">
                        <span class="badge badge-info">${fn:length(searchAndReplace.listNodesUpdateSuccess)}</span>
                        <fmt:message key='jnt_searchAndReplace.updateStatus.success'/>
                    </a>
                </div>
                <div id="collapseOne" class="accordion-body collapse">
                    <div class="accordion-inner">
                        <table cellpadding="0" cellspacing="0" border="0" class="table table-hover table-bordered" id="listNodesUpdateSuccess_table">
                            <thead>
                                <tr>
                                    <th>
                                        <fmt:message key='jnt_searchAndReplace.nodes'/>
                                    </th>
                                    <th>
                                        <fmt:message key='jmix_contentmetadata.j_lastModificationDate'/>
                                    </th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${searchAndReplace.listNodesUpdateSuccess}" var="id">
                                    <jcr:node var="node" uuid="${id}"/>
                                    <tr>
                                        <td>
                                            <a href="#modal_${id}" role="button" data-toggle="modal" style="text-decoration: none;">
                                                    ${functions:abbreviate(node.displayableName,100,120,'...')}
                                            </a>
                                        </td>
                                        <td>
                                            <em><fmt:formatDate value="${node.properties['jcr:lastModified'].date.time}" pattern="dd, MMMM yyyy HH:mm"/></em>
                                            &nbsp;<fmt:message key="label.by"/>&nbsp;
                                            <strong>${node.properties['jcr:lastModifiedBy'].string}</strong>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </c:if>
        <c:if test="${fn:length(searchAndReplace.listNodesSkipped) gt 0}">
            <div class="accordion-group">
                <div class="accordion-heading alert alert-block">
                    <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion2" href="#collapseTwo" style="text-decoration: none;">
                        <span class="badge badge-info">${fn:length(searchAndReplace.listNodesSkipped)}</span>
                        <fmt:message key='jnt_searchAndReplace.updateStatus.skip'/>
                    </a>
                </div>
                <div id="collapseTwo" class="accordion-body collapse">
                    <div class="accordion-inner">
                        <table cellpadding="0" cellspacing="0" border="0" class="table table-hover table-bordered" id="listNodesSkipped_table">
                            <thead>
                                <tr>
                                    <th>
                                        <fmt:message key='jnt_searchAndReplace.nodes'/>
                                    </th>
                                    <th>
                                        <fmt:message key='jmix_contentmetadata.j_lastModificationDate'/>
                                    </th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${searchAndReplace.listNodesSkipped}" var="id">
                                    <jcr:node var="node" uuid="${id}"/>
                                    <tr>
                                        <td>
                                            <a href="#modal_${id}" role="button" data-toggle="modal" style="text-decoration: none;">
                                                    ${functions:abbreviate(node.displayableName,100,120,'...')}
                                            </a>
                                        </td>
                                        <td>
                                            <em><fmt:formatDate value="${node.properties['jcr:lastModified'].date.time}" pattern="dd, MMMM yyyy HH:mm"/></em>
                                            &nbsp;<fmt:message key="label.by"/>&nbsp;
                                            <strong>${node.properties['jcr:lastModifiedBy'].string}</strong>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </c:if>
        <c:if test="${fn:length(searchAndReplace.listNodesUpdateFail) gt 0}">
            <div class="accordion-group">
                <div class="accordion-heading alert alert-error">
                    <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion2" href="#collapseThree" style="text-decoration: none;">
                        <span class="badge badge-info">${fn:length(searchAndReplace.listNodesUpdateFail)}</span>
                        <fmt:message key='jnt_searchAndReplace.updateStatus.fail'/>
                    </a>
                </div>
                <div id="collapseThree" class="accordion-body collapse">
                    <div class="accordion-inner">
                        <table cellpadding="0" cellspacing="0" border="0" class="table table-hover table-bordered" id="listNodesUpdateFail_table">
                            <thead>
                                <tr>
                                    <th>
                                        <fmt:message key='jnt_searchAndReplace.nodes'/>
                                    </th>
                                    <th>
                                        <fmt:message key='jmix_contentmetadata.j_lastModificationDate'/>
                                    </th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${searchAndReplace.listNodesUpdateFail}" var="id">
                                    <jcr:node var="node" uuid="${id}"/>
                                    <tr>
                                        <td>
                                            <a href="#modal_${id}" role="button" data-toggle="modal" style="text-decoration: none;">
                                                    ${functions:abbreviate(node.displayableName,100,120,'...')}
                                            </a>
                                        </td>
                                        <td>
                                            <em><fmt:formatDate value="${node.properties['jcr:lastModified'].date.time}" pattern="dd, MMMM yyyy HH:mm"/></em>
                                            &nbsp;<fmt:message key="label.by"/>&nbsp;
                                            <strong>${node.properties['jcr:lastModifiedBy'].string}</strong>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </c:if>
    </div>

    <form:form action="${flowExecutionUrl}" method="post" modelAttribute="searchAndReplace">
        <div class="control-group">
            <button class="btn btn-primary" name="_eventId_searchAndReplaceCancel">
                <fmt:message key="jnt_searchAndReplace.otherResearch"/>
            </button>
        </div>
    </form:form>

    <c:forEach items="${searchAndReplace.listNodesUpdateSuccess}" var="id">
        <jcr:node var="node" uuid="${id}"/>
        <div class="modal hide fade" id="modal_${id}" tabindex="-1" role="dialog" aria-labelledby="modalTitle_${id}" aria-hidden="true">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h3 id="modalTitle_${id}">${functions:abbreviate(node.displayableName,100,120,'...')}</h3>
            </div>
            <div class="modal-body preview">
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
                <h3 id="modalTitle_${id}">${functions:abbreviate(node.displayableName,100,120,'...')}</h3>
            </div>
            <div class="modal-body preview">
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
                <h3 id="modalTitle_${id}">${functions:abbreviate(node.displayableName,100,120,'...')}</h3>
            </div>
            <div class="modal-body preview">
                <template:module node="${node}"/>
            </div>
            <div class="modal-footer">
                <a href="#" class="btn btn-primary" data-dismiss="modal">OK</a>
            </div>
        </div>
    </c:forEach>
</div>
