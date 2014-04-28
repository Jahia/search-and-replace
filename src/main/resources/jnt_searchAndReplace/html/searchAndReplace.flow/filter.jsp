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

<template:addResources type="css" resources="bootstrap-datetimepicker.min.css"/>

<template:addResources type="javascript" resources="jquery.min.js,jquery-ui.min.js,jquery.blockUI.js,admin-bootstrap.js,workInProgress.js"/>
<template:addResources type="javascript" resources="datatables/jquery.dataTables.js,i18n/jquery.dataTables-${currentResource.locale}.js,datatables/dataTables.bootstrap-ext.js"/>
<template:addResources type="javascript" resources="jquery.highlight.js"/>
<template:addResources type="javascript" resources="bootstrap-datetimepicker.min.js"/>
<template:addResources type="javascript" resources="bootstrap-datetimepicker.${renderContext.UILocale}.js"/>

<fmt:message key="label.workInProgressTitle" var="i18nWaiting"/><c:set var="i18nWaiting" value="${functions:escapeJavaScript(i18nWaiting)}"/>

<template:addResources type="inlinejavascript">
    <script type="text/javascript">
        var hiddenFieldsBoolean;
        var oTable;
        /*
         * Function: fnGetHiddenTrNodes
         * Purpose:  Get all of the hidden TR nodes (i.e. the ones which aren't on display)
         * Returns:  array:
         * Inputs:   object:oSettings - DataTables settings object
         */
        $.fn.dataTableExt.oApi.fnGetHiddenTrNodes = function ( oSettings )
        {
            /* Note the use of a DataTables 'private' function thought the 'oApi' object */
            var anNodes = this.oApi._fnGetTrNodes( oSettings );
            var anDisplay = $('tbody tr', oSettings.nTable);

            /* Remove nodes which are being displayed */
            for ( var i=0 ; i<anDisplay.length ; i++ )
            {
                var iIndex = jQuery.inArray( anDisplay[i], anNodes );
                if ( iIndex != -1 )
                {
                    anNodes.splice( iIndex, 1 );
                }
            }

            /* Fire back the array to the caller */
            return anNodes;
        }

        function getDatatableHiddenRows(dataTable)
        {
            //Getting datatable hiddenRows
            var hiddenRows = $(dataTable.fnGetHiddenTrNodes());
            var hiddenData=new Array();
            var hiddenDataIndex = 0;
            //Getting the checked elements from hiddenRows
            hiddenRows.find("input[type=checkbox]:checked").each(function(){
                hiddenData[hiddenDataIndex] = $(this).val();
                hiddenDataIndex++;
            });
            //Adding hidden Input tag for each hidden checked checkbox
            $.each(hiddenData, function(index,value)
            {
                if(($(".hiddenFields").html().indexOf(value) == -1))
                {
                    var inputTag = "<input type=\"hidden\" value=\""+value+"\" name=\"listNodesToBeUpdated\"/>";
                    $(".hiddenFields").append(inputTag);
                }
                //Empty Form boolean
                hiddenFieldsBoolean=true;
            });
        }

        $(document).ready(function(){
            var oTable = $('#listNodes_table').dataTable({
                "sDom": "<'row-fluid'<'span6'l><'span6 text-right'f>r>t<'row-fluid'<'span6'i><'span6 text-right'p>>",
                "iDisplayLength":25,
                "sPaginationType": "bootstrap",
                "aaSorting": [] //this option disable sort by default, the user steal can use column names to sort the table
            });

            $('.searchAndReplaceSubmit').on('click', function(){
                getDatatableHiddenRows(oTable);
                var boolean = true;

                if(!$(".select").is(':checked') && !hiddenFieldsBoolean){
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

            $('#BtToggleSearch').click(function () {
                $('#advancedSearch').slideToggle("slow");
            });

            $('#createdBefore').datetimepicker({
                format: 'yyyy-MM-dd',
                pickTime: false,
                language: '${renderContext.UILocale}'
            }).on('changeDate', function(){
                formSubmit();
            });

            $('#createdAfter').datetimepicker({
                format: 'yyyy-MM-dd',
                pickTime: false,
                language: '${renderContext.UILocale}'
            }).on('changeDate', function(){
                formSubmit();
            });

            $('#modifiedBefore').datetimepicker({
                format: 'yyyy-MM-dd',
                pickTime: false,
                language: '${renderContext.UILocale}'
            }).on('changeDate', function(){
                formSubmit();
            });

            $('#modifiedAfter').datetimepicker({
                format: 'yyyy-MM-dd',
                pickTime: false,
                language: '${renderContext.UILocale}'
            }).on('changeDate', function(){
                formSubmit();
            });
        });

        function formSubmit(){
            $("form[name=advancedSearchForm]").submit();
        }

        function clearThisField(field){
            $('#' + field).val('');
            $('#' + field).change();
        }
    </script>
</template:addResources>

<h1>Search And Replace</h1>
<a id="BtToggleSearch" class="btn btn-small btn-info" href="#">
    <i class="icon-search icon-white"></i>
    <fmt:message key='jnt_searchAndReplace.advancedSearch'/>
</a>
<c:set value="${searchAndReplace.selectedNodeType}${searchAndReplace.dateCreatedBefore}${searchAndReplace.dateCreatedAfter}${searchAndReplace.dateModifiedBefore}${searchAndReplace.dateModifiedAfter}" var="testString"/>

<form:form name="advancedSearchForm" action="${flowExecutionUrl}" method="post" cssClass="form-horizontal" modelAttribute="searchAndReplace" onsubmit="workInProgress('${i18nWaiting}')">
    <div id="advancedSearch" class="<c:if test="${empty testString}">hide</c:if> box-1">
        <input id="inputFormSubmit" type="hidden" value="_eventId_searchAndReplaceAdvancedSearch" name="_eventId_searchAndReplaceAdvancedSearch"/>
        <fieldset>
            <div class="control-group">
                <form:label path="termToReplace" cssClass="control-label">
                    <fmt:message key="jnt_searchAndReplace.searchButton"/>
                </form:label>
                <div class="controls">
                    <form:input path="termToReplace" value="${searchAndReplace.replacementTerm}" disabled="true"/>
                </div>
            </div>
            <div class="control-group">
                <form:label path="selectedNodeType" cssClass="control-label">
                    <fmt:message key="jnt_searchAndReplace.selectNodeType"/>
                </form:label>
                <div class="controls input-append" style="display: block;">
                    <form:select path="selectedNodeType" onchange="formSubmit()">
                        <form:option value=""></form:option>
                        <c:forEach items="${searchAndReplace.listNodesTypes}" var="property">
                            <form:option value="${property}">${property}</form:option>
                        </c:forEach>
                    </form:select>
                    <c:if test="${not empty searchAndReplace.selectedNodeType}">
                        <a href="#" title="<fmt:message key="jnt_searchAndReplace.clearSelectedNodeType"/>" class="btn btn-link" onclick="clearThisField('selectedNodeType')"><i class="icon-remove"></i></a>
                    </c:if>
                </div>
            </div>
            <div class="control-group">
                <form:label path="listSelectedFieldsOfNodeType" cssClass="control-label">
                    <fmt:message key="jnt_searchAndReplace.selectFields"/>
                </form:label>
                <div class="controls input-append" style="display: block;">
                    <form:select multiple="multiple" path="listSelectedFieldsOfNodeType">
                        <c:forEach items="${searchAndReplace.listFieldsOfNodeType}" var="fied">
                            <form:option value="${fied}">${fied}</form:option>
                        </c:forEach>
                    </form:select>
                    <c:if test="${not empty searchAndReplace.listSelectedFieldsOfNodeType}">
                        <a href="#" title="<fmt:message key="jnt_searchAndReplace.clearSelectedNodeType"/>" class="btn btn-link" onclick="clearThisField('listSelectedFieldsOfNodeType')"><i class="icon-remove"></i></a>
                    </c:if>
                </div>
            </div>
        </fieldset>
        <fieldset>
            <legend><fmt:message key="jnt_searchAndReplace.dateCreated"/></legend>
            <div class="control-group">
                <form:label path="dateCreatedBefore" cssClass="control-label">
                    <fmt:message key="jnt_searchAndReplace.before"/>
                </form:label>
                <div class="controls input-append date" id="createdBefore" style="display: block;">
                    <form:input class="otherField" path="dateCreatedBefore"/>
                    <span class="add-on">
                        <i data-time-icon="icon-time" data-date-icon="icon-calendar"></i>
                    </span>
                    <c:if test="${not empty searchAndReplace.dateCreatedBefore}">
                        <a href="#" title="<fmt:message key="jnt_searchAndReplace.clearDateCreatedBefore"/>" class="btn btn-link" onclick="clearThisField('dateCreatedBefore');return false;"><i class="icon-remove"></i></a>
                    </c:if>
                </div>
                <form:errors path="dateCreatedBefore" cssClass="text-error"/>
            </div>
            <div class="control-group">
                <form:label path="dateCreatedAfter" cssClass="control-label">
                    <fmt:message key="jnt_searchAndReplace.after"/>
                </form:label>
                <div class="controls input-append date" id="createdAfter" style="display: block;">
                    <form:input class="otherField" path="dateCreatedAfter"/>
                    <span class="add-on">
                        <i data-time-icon="icon-time" data-date-icon="icon-calendar"></i>
                    </span>
                    <c:if test="${not empty searchAndReplace.dateCreatedAfter}">
                        <a href="#" title="<fmt:message key="jnt_searchAndReplace.clearDateCreatedAfter"/>" class="btn btn-link" onclick="clearThisField('dateCreatedAfter')"><i class="icon-remove"></i></a>
                    </c:if>
                </div>
                <form:errors path="dateCreatedAfter" cssClass="text-error"/>
            </div>
        </fieldset>
        <fieldset>
            <legend><fmt:message key="jnt_searchAndReplace.dateModified"/></legend>
            <div class="control-group">
                <form:label path="dateModifiedBefore" cssClass="control-label">
                    <fmt:message key="jnt_searchAndReplace.before"/>
                </form:label>
                <div class="controls input-append date" id="modifiedBefore" style="display: block;">
                    <form:input class="otherField" path="dateModifiedBefore"/>
                    <span class="add-on">
                        <i data-time-icon="icon-time" data-date-icon="icon-calendar"></i>
                    </span>
                    <c:if test="${not empty searchAndReplace.dateModifiedBefore}">
                        <a href="#" title="<fmt:message key="jnt_searchAndReplace.clearDateModifiedBefore"/>" class="btn btn-link" onclick="clearThisField('dateModifiedBefore')"><i class="icon-remove"></i></a>
                    </c:if>
                </div>
                <form:errors path="dateModifiedBefore" cssClass="text-error"/>
            </div>
            <div class="control-group">
                <form:label path="dateModifiedAfter" cssClass="control-label">
                    <fmt:message key="jnt_searchAndReplace.after"/>
                </form:label>
                <div class="controls input-append date" id="modifiedAfter" style="display: block;">
                    <form:input class="otherField" path="dateModifiedAfter"/>
                    <span class="add-on">
                        <i data-time-icon="icon-time" data-date-icon="icon-calendar"></i>
                    </span>
                    <c:if test="${not empty searchAndReplace.dateModifiedAfter}">
                        <a href="#" title="<fmt:message key="jnt_searchAndReplace.clearDateModifiedAfter"/>" class="btn btn-link" onclick="clearThisField('dateModifiedAfter')"><i class="icon-remove"></i></a>
                    </c:if>
                </div>
                <form:errors path="dateModifiedAfter" cssClass="text-error"/>
            </div>
        </fieldset>
    </div>

    <div class="hiddenFields hide">
    </div>
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
                    <fmt:message key='mix_created'/>
                </th>
                <th>
                    <fmt:message key='jmix_contentmetadata.j_lastModificationDate'/>
                </th>
            </tr>
        </thead>
        <tbody>
            <c:forEach items="${searchAndReplace.searchResultList}" var="searchResultNode">
                <jcr:node var="node" uuid="${searchResultNode.nodeUuid}"/>
                <tr>
                    <td>
                        <form:checkbox path="listNodesToBeUpdated" value="${searchResultNode.nodeUuid}" cssClass="select"/>
                    </td>
                    <td>
                        <a href="#modal_${searchResultNode.nodeUuid}" role="button" data-toggle="modal" style="text-decoration: none;">
                            ${functions:abbreviate(node.displayableName,100,120,'...')}
                        </a>
                    </td>
                    <td>
                        <em><fmt:formatDate value="${node.properties['jcr:created'].date.time}" pattern="dd, MMMM yyyy HH:mm"/></em>
                        &nbsp;<fmt:message key="label.by"/>&nbsp;
                        <strong>${node.properties['jcr:createdBy'].string}</strong>
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
        <c:if test="${not empty searchAndReplace.searchResultList}">
            <%--searchAndReplaceSubmit class is used by jQuery don't remove it !--%>
            <button class="btn btn-primary searchAndReplaceSubmit" type="submit" name="_eventId_searchAndReplaceGoToReplace">
                <fmt:message key="label.next"/>
            </button>
            <span id="listNodesToBeUpdatedError" class="hide text-error"><fmt:message key="jnt_searchAndReplace.listNodesToBeUpdated.error"/></span>
            <form:errors path="listNodesToBeUpdated" cssClass="text-error"/>
        </c:if>
    </div>
</form:form>

<c:forEach items="${searchAndReplace.searchResultList}" var="searchResultNode">
    <jcr:node var="node" uuid="${searchResultNode.nodeUuid}"/>
    <div class="modal hide fade" id="modal_${searchResultNode.nodeUuid}" tabindex="-1" role="dialog" aria-labelledby="modalTitle_${searchResultNode.nodeUuid}" aria-hidden="true">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            <h3 id="modalTitle_${searchResultNode.nodeUuid}">${functions:abbreviate(node.displayableName,100,120,'...')}</h3>
        </div>
        <div class="modal-body preview">
            <table class="table">
                <thead>
                    <tr>
                        <th class="span2">
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
                            <td>
                                ${properties.key}
                            </td>
                            <td>
                                ${node.properties[properties.key].string}
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
        <div class="modal-footer">
            <a href="#" class="btn btn-primary" data-dismiss="modal">OK</a>
        </div>
    </div>
</c:forEach>
