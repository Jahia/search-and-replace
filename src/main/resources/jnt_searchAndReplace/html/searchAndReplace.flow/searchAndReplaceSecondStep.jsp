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
            var oTable = $('#listNodes_table').dataTable({
                "sDom": "<'row-fluid'<'span6'l><'span6 text-right'f>r>t<'row-fluid'<'span6'i><'span6 text-right'p>>",
                "iDisplayLength":25,
                "sPaginationType": "bootstrap",
                "aaSorting": [] //this option disable sort by default, the user steal can use column names to sort the table
            });

            $('.searchAndReplaceSubmit').on('click', function(){
                var boolean = true;

                if(!$(".select").is(':checked')){
                    $('#listNodesToBeUpdatedError').fadeIn('slow').delay(4000).fadeOut('slow');
                    boolean = false;
                }
                return boolean;
            })

            $('#selectAll').click(function() {
                $('input', oTable.fnGetNodes()).attr('checked',this.checked);
            });

            $('.preview').highlight('${searchAndReplace.termToReplace}', { caseSensitive: true });

            $('.highlight').css({ backgroundColor: '#ED6A32' });
        });
    </script>
</template:addResources>


    <h1>Search And Replace</h1>
    <form:form action="${flowExecutionUrl}" method="post" cssClass="box-1" modelAttribute="searchAndReplace">
        <table cellpadding="0" cellspacing="0" border="0" class="table table-hover table-bordered" id="listNodes_table">
            <thead>
                <tr>
                    <th>
                        <form:checkbox path="selectAll" value="true" id="selectAll"/>
                        <br />
                        <fmt:message key='jnt_searchAndReplace.selectAll'/>
                    </th>
                    <th>
                        <fmt:message key='jnt_searchAndReplace.nodes'/>
                    </th>
                    <th>
                        <fmt:message key='jmix_contentmetadata.j_lastModificationDate'/>
                    </th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${searchAndReplace.listNodes}" var="id">
                    <jcr:node var="node" uuid="${id}"/>
                    <tr>
                        <td>
                            <form:checkbox path="listNodesToBeUpdated" value="${id}" cssClass="select"/>
                        </td>
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
        <div class="control-group">
            <button class="btn" name="_eventId_searchAndReplacePrevious">
                <fmt:message key="label.previous"/>
            </button>
            <button class="btn btn-danger" name="_eventId_searchAndReplaceCancel">
                <fmt:message key="label.cancel"/>
            </button>
            <%--searchAndReplaceSubmit class is used by jQuery don't remove it !--%>
            <button class="btn btn-primary searchAndReplaceSubmit" type="submit" name="_eventId_searchAndReplaceGoToThirdStep">
                <fmt:message key="label.next"/>
            </button>
            <span id="listNodesToBeUpdatedError" class="hide text-error"><fmt:message key="jnt_searchAndReplace.listNodesToBeUpdated.error"/></span>
            <form:errors path="listNodesToBeUpdated" cssClass="text-error"/>
        </div>
    </form:form>

    <c:forEach items="${searchAndReplace.listNodes}" var="id">
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

