package org.jahia.modules.searchandreplace.webflow;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jahia.modules.searchandreplace.GlobalReplaceService;
import org.jahia.modules.searchandreplace.SearchResult;
import org.jahia.modules.searchandreplace.webflow.model.SearchAndReplace;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * This class handle data from the Sping Webflow model to execute Global Term search and replacement
 * Using search queries and the GlobalReplaceService
 * Created by dgaillard on 24/03/14.
 */
public class SearchAndReplaceFlowHandler implements Serializable {

    private static final Logger logger = getLogger(SearchAndReplaceFlowHandler.class);

    private static final long serialVersionUID = -16287862741718967L;

    @Autowired private transient GlobalReplaceService replaceService;

    /**
     * This function initialize the webflow model
     *
     * @return SearchAndReplace : the webflow model initialized
     */
    public SearchAndReplace initSearchAndReplace() {
        SearchAndReplace searchAndReplace = new SearchAndReplace();
        return searchAndReplace;
    }

    /**
     * This function re-initialize the webflow model after a replace
     * The resettled model object keep the replacementTerm and the Success, Failed, Skipped and SearchResults lists
     * from the old model object
     *
     * @param searchAndReplace
     * @return SearchAndReplace :
     */
    public SearchAndReplace resetSearchAndReplace(SearchAndReplace searchAndReplace) {
        if (CollectionUtils.isEmpty(searchAndReplace.getListNodesToBeUpdated()) && CollectionUtils
                .isNotEmpty(searchAndReplace.getListSearchResult())) {
            searchAndReplace = new SearchAndReplace(searchAndReplace);
        }
        return searchAndReplace;
    }

    /**
     * this function clear the replacement Term and the Success, Failed, Skipped and SearchResults lists
     * In the model before a new search
     *
     * @param searchAndReplace The webflow model object cleared before the new search
     */
    public void resetListSummary(SearchAndReplace searchAndReplace) {
        if (StringUtils.isNotEmpty(searchAndReplace.getReplacementTerm())) {
            searchAndReplace.setReplacementTerm("");
        }
        if (CollectionUtils.isNotEmpty(searchAndReplace.getListNodesUpdateSuccess())) {
            searchAndReplace.getListNodesUpdateSuccess().clear();
        }
        if (CollectionUtils.isNotEmpty(searchAndReplace.getListNodesUpdateFail())) {
            searchAndReplace.getListNodesUpdateFail().clear();
        }
        if (CollectionUtils.isNotEmpty(searchAndReplace.getListNodesSkipped())) {
            searchAndReplace.getListNodesSkipped().clear();
        }
        if (CollectionUtils.isNotEmpty(searchAndReplace.getListSearchResult())) {
            searchAndReplace.getListSearchResult().clear();
        }
    }

    /**
     * Function called to refilter the searchResulst list with the data contained in the Webflow model object
     *
     * @param searchAndReplace : The webflow model object containing the filter data
     * @param renderContext    : The render context from which get the site path
     */
    public void getNodesContains(SearchAndReplace searchAndReplace, RenderContext renderContext) {
        StopWatch stopWatch = null;
        if (logger.isDebugEnabled()) {
            stopWatch = new StopWatch("getNodesContains");
            logger.debug("getNodesContains() - Start");
            stopWatch.start("getNodesContains");
        }
        List<String> listNodes = new ArrayList<String>();
        String sitePath = renderContext.getSite().getPath();

        //If nodeType is changed and some properties has already been selected
        if (CollectionUtils.isNotEmpty(searchAndReplace.getListSelectedFieldsOfNodeType()) && searchAndReplace.isDifferentNodeType()) {
            //Clear the selected properties list before executing the query
            searchAndReplace.getListSelectedFieldsOfNodeType().clear();
        }
        //Query base
        if (logger.isDebugEnabled()) {
            logger.debug("getNodesContains() - Building query base");
        }
        StringBuilder query = new StringBuilder().append("SELECT * FROM [nt:base] AS result WHERE ISDESCENDANTNODE(result, '")
                .append(sitePath).append("') AND CONTAINS(result.");

        if (CollectionUtils.isNotEmpty(searchAndReplace.getListSelectedFieldsOfNodeType())
                && searchAndReplace.getListSelectedFieldsOfNodeType().size() == 1) {
            query.append("[").append(searchAndReplace.getListSelectedFieldsOfNodeType().get(0)).append("]");
        } else {
            query.append("*");
        }

        //Query nodeType restriction
        query.append(",").append(searchAndReplace.getEscapedTermToReplace())
                .append(") AND ([jcr:primaryType] NOT LIKE 'jnt:file') AND ([jcr:primaryType] NOT LIKE 'jnt:resource')");

        //Filter restrictions
        if (logger.isDebugEnabled()) {
            logger.debug("getNodesContains() - Applying filter restrictions to query");
        }
        //Filter nodeType filter restriction
        if (!searchAndReplace.getSelectedNodeType().isEmpty()) {
            query.append(" AND ([jcr:primaryType] LIKE '").append(searchAndReplace.getSelectedNodeType()).append("')");
        }

        //Filter date created before filter restriction
        if (!searchAndReplace.getDateCreatedBefore().isEmpty()) {
            query.append(" AND ([jcr:created] < CAST ('+").append(searchAndReplace.getDateCreatedBefore())
                    .append("T23:59:59.999Z' as date))");
        }
        //Filter date created after filter restriction
        if (!searchAndReplace.getDateCreatedAfter().isEmpty()) {
            query.append(" AND ([jcr:created] > CAST ('+").append(searchAndReplace.getDateCreatedAfter())
                    .append("T00:00:00.000Z' as date))");
        }
        //Filter date modified before filter restriction
        if (!searchAndReplace.getDateModifiedBefore().isEmpty()) {
            query.append(" AND ([jcr:lastModified] < CAST ('+").append(searchAndReplace.getDateModifiedBefore())
                    .append("T23:59:59.999Z' as date))");
        }
        //Filter date modified after filter restriction
        if (!searchAndReplace.getDateModifiedAfter().isEmpty()) {
            query.append(" AND ([jcr:lastModified] > CAST ('+").append(searchAndReplace.getDateModifiedAfter())
                    .append("T00:00:00.000Z' as date))");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("getNodesContains() - Query built");
        }
        try {
            JCRSessionWrapper session = renderContext.getMainResource().getNode().getSession();
            QueryManager qm = session.getWorkspace().getQueryManager();
            Query q = qm.createQuery(query.toString(), Query.JCR_SQL2);
            q.setLimit(1000);
            if (logger.isDebugEnabled()) {
                logger.debug("getNodesContains() - Executing query ");
            }
            NodeIterator ni = q.execute().getNodes();

            //Getting query result nodes
            while (ni.hasNext()) {
                JCRNodeWrapper next = (JCRNodeWrapper) ni.next();
                listNodes.add(next.getIdentifier());
            }
            if (logger.isDebugEnabled()) {
                logger.debug("getNodesContains() - Getting nodes replaceable properties ");
            }
            String localeAsString = renderContext.getRequest().getParameter("webflowLocale");
            Locale locale = session.getLocale();
            if (localeAsString != null) {
                locale = locale.forLanguageTag(localeAsString);
            }

            List<SearchResult> searchResults = replaceService
                    .getReplaceableProperties(listNodes, searchAndReplace.getTermToReplace(), GlobalReplaceService.SearchMode.EXACT_MATCH,
                            session).get(GlobalReplaceService.ReplaceStatus.SUCCESS);

            for (SearchResult result : searchResults) {
                JCRNodeWrapper node = session.getNodeByIdentifier(result.getNodeUuid());
                result.setNodeTypeLabel(node.getPrimaryNodeType().getLabel(locale));
            }

            searchAndReplace.setListSearchResult(searchResults);

            if (CollectionUtils.isNotEmpty(searchAndReplace.getListSelectedFieldsOfNodeType())
                    && searchAndReplace.getListSelectedFieldsOfNodeType().size() > 1) {
                for (SearchResult searchResult : searchAndReplace.getListSearchResult()) {
                    if (!searchResult.getReplaceableProperties().containsKey(searchAndReplace.getListSelectedFieldsOfNodeType())) {
                        searchAndReplace.getListSearchResult().indexOf(searchResult);
                    }
                }
            }
        } catch (RepositoryException e) {
            logger.error(e.getMessage(), e);
        }
        if (logger.isDebugEnabled()) {
            stopWatch.stop();
            logger.info(stopWatch.prettyPrint());
            logger.debug("getNodesContains() - End");
        }
    }

    /**
     * This function tests the List of nodes to update and return a boolean
     * Indicating if there are nodes to Update
     *
     * @param searchAndReplace the webflow model containing the list of nodes to update
     * @return boolean : true if there are nodes to update and false in the other case
     */
    public boolean haveNodeToUpdate(SearchAndReplace searchAndReplace) {
        if (CollectionUtils.isNotEmpty(searchAndReplace.getListNodesToBeUpdated())) {
            return true;
        } else {
            searchAndReplace.setFromEventID("summary");
            return false;
        }
    }

    /**
     * This function skip the replace on the current node
     * The node is removed from the list of nodes to update and added in the list of skipped nodes
     * In the webflow model object
     *
     * @param searchAndReplace : The webflow model object containing the list of nodes to update, the list of nodes to skip and the current node
     */
    public void skipThisNode(SearchAndReplace searchAndReplace) {
        if (logger.isDebugEnabled()) {
            logger.debug("skipThisNode() - Start");
        }
        //Getting the current node id
        String nodeID = searchAndReplace.getCurrentDisplayedNode();

        searchAndReplace.getListNodesToBeUpdated().remove(searchAndReplace.getListNodesToBeUpdated().indexOf(nodeID));
        searchAndReplace.addUUIDToListNodesSkipped(nodeID);
        if (logger.isDebugEnabled()) {
            logger.debug("skipThisNode() - End");
        }
    }

    /**
     * This function skip the replace on all the nodes in the update list
     * The nodes are removed from the list of nodes to update and added in the list of skipped nodes
     * In the webflow model object
     *
     * @param searchAndReplace : The webflow model object containing the list of nodes to update, the list of nodes to skip and the current node
     */
    public void skipAllNodes(SearchAndReplace searchAndReplace) {
        if (logger.isDebugEnabled()) {
            logger.debug("skipAllNodes() - Start");
        }
        for (String node : searchAndReplace.getListNodesToBeUpdated()) {
            searchAndReplace.addUUIDToListNodesSkipped(node);
        }
        searchAndReplace.getListNodesToBeUpdated().clear();
        if (logger.isDebugEnabled()) {
            logger.debug("skipAllNodes() - End");
        }
    }

    /**
     * This function replace the search term in the current node by the replacement term
     *
     * @param searchAndReplace The webflow model object containing the current node, the term to replace and the replacement term
     * @param renderContext    the render context that contains the session used to access JCR
     */
    public void replaceThisNode(SearchAndReplace searchAndReplace, RenderContext renderContext) {
        if (logger.isDebugEnabled()) {
            logger.debug("replaceThisNode() - Start");
        }
        String nodeID = searchAndReplace.getCurrentDisplayedNode();
        Map<GlobalReplaceService.ReplaceStatus, List<String>> replaceResult;

        try {
            //Getting JCR Session
            JCRSessionWrapper session = renderContext.getMainResource().getNode().getSession();

            //Preparing the list of nodes uuids for the GlobalReplaceService call
            List<String> uuids = new ArrayList<String>();
            uuids.add(nodeID);

            //if select all properties
            if (searchAndReplace.isSelectAllProperties()) {
                //Calling Replace Service
                if (logger.isDebugEnabled()) {
                    logger.debug("replaceThisNode() - Calling replace of " + searchAndReplace.getTermToReplace() + " on node " + nodeID);
                }
                if (CollectionUtils.isNotEmpty(searchAndReplace.getListSelectedFieldsOfNodeType())) {
                    replaceResult = replaceService
                            .replaceByUuid(uuids, searchAndReplace.getTermToReplace(), searchAndReplace.getReplacementTerm(),
                                    GlobalReplaceService.SearchMode.EXACT_MATCH, searchAndReplace.getListSelectedFieldsOfNodeType(),
                                    session);
                } else {
                    replaceResult = replaceService
                            .replaceByUuid(uuids, searchAndReplace.getTermToReplace(), searchAndReplace.getReplacementTerm(),
                                    GlobalReplaceService.SearchMode.EXACT_MATCH, session);
                }
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("replaceThisNode() - Calling replace of " + searchAndReplace.getTermToReplace() + " on node " + nodeID);
                }
                replaceResult = replaceService
                        .replaceByUuid(uuids, searchAndReplace.getTermToReplace(), searchAndReplace.getReplacementTerm(),
                                GlobalReplaceService.SearchMode.EXACT_MATCH, searchAndReplace.getListPropertiesToBeReplaced(), session);

            }

            //Getting Failed Replaced Nodes
            if (CollectionUtils.isNotEmpty(replaceResult.get(GlobalReplaceService.ReplaceStatus.FAILED))) {
                searchAndReplace.getListNodesUpdateFail().add(replaceResult.get(GlobalReplaceService.ReplaceStatus.FAILED).get(0));
            }

            //Getting Successfully Replaced Nodes
            if (CollectionUtils.isNotEmpty(replaceResult.get(GlobalReplaceService.ReplaceStatus.SUCCESS))) {
                searchAndReplace.getListNodesUpdateSuccess().add(replaceResult.get(GlobalReplaceService.ReplaceStatus.SUCCESS).get(0));
            }

        } catch (RepositoryException e) {
            logger.error("replaceThisNodes() - Failed replacing the node ", e);
        }

        //The node has been successfully treated and can be removed from the list of nodes to be updated
        searchAndReplace.getListNodesToBeUpdated().remove(nodeID);

        if (logger.isDebugEnabled()) {
            logger.debug("replaceThisNode() - End");
        }
    }

    /**
     * This function replace the search term by the replacement term in all the nodes contained in the list of nodes to update
     *
     * @param searchAndReplace The webflow model object containing the list of nodes to update, the term to replace and the replacement term
     * @param renderContext    the render context that contains the session used to access JCR
     */
    public void replaceAllNodes(SearchAndReplace searchAndReplace, RenderContext renderContext) {
        StopWatch stopWatch = null;
        if (logger.isDebugEnabled()) {
            logger.debug("replaceAllNodes() - Start");
            stopWatch = new StopWatch("getNodesContains");
            stopWatch.start("replaceAllNodes");
        }
        Map<GlobalReplaceService.ReplaceStatus, List<String>> replaceResult;

        try {
            //Getting session
            JCRSessionWrapper session = renderContext.getMainResource().getNode().getSession();

            //Calling Replace Service
            //Calling Replace Service
            if (logger.isDebugEnabled()) {
                logger.debug("replaceThisNode() - Calling replace of " + searchAndReplace.getListNodesToBeUpdated().size() + " nodes ");
            }
            //if select all properties
            if (searchAndReplace.isSelectAllProperties()) {
                if (CollectionUtils.isNotEmpty(searchAndReplace.getListSelectedFieldsOfNodeType())) {
                    replaceResult = replaceService
                            .replaceByUuid(searchAndReplace.getListNodesToBeUpdated(), searchAndReplace.getTermToReplace(),
                                    searchAndReplace.getReplacementTerm(), GlobalReplaceService.SearchMode.EXACT_MATCH,
                                    searchAndReplace.getListSelectedFieldsOfNodeType(), session);
                } else {
                    replaceResult = replaceService
                            .replaceByUuid(searchAndReplace.getListNodesToBeUpdated(), searchAndReplace.getTermToReplace(),
                                    searchAndReplace.getReplacementTerm(), GlobalReplaceService.SearchMode.EXACT_MATCH, session);
                }
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("replaceThisNode() - Calling replace of " + searchAndReplace.getListNodesToBeUpdated().size() + " nodes ");
                }
                replaceResult = replaceService
                        .replaceByUuid(searchAndReplace.getListNodesToBeUpdated(), searchAndReplace.getTermToReplace(),
                                searchAndReplace.getReplacementTerm(), GlobalReplaceService.SearchMode.EXACT_MATCH,
                                searchAndReplace.getListPropertiesToBeReplaced(), session);
            }

            //Getting Failed Replaced Nodes
            if (CollectionUtils.isNotEmpty(replaceResult.get(GlobalReplaceService.ReplaceStatus.FAILED))) {
                searchAndReplace.getListNodesUpdateFail().addAll(replaceResult.get(GlobalReplaceService.ReplaceStatus.FAILED));
            }

            if (CollectionUtils.isNotEmpty(replaceResult.get(GlobalReplaceService.ReplaceStatus.SUCCESS))) {
                searchAndReplace.getListNodesUpdateSuccess().addAll(replaceResult.get(GlobalReplaceService.ReplaceStatus.SUCCESS));
            }

        } catch (RepositoryException e) {
            logger.error("replaceAllNodes() - Failed replacing given nodes ", e);
        }

        //All the nodes to update have been treated the list of nodes to update can be cleared
        searchAndReplace.getListNodesToBeUpdated().clear();

        if (logger.isDebugEnabled()) {
            stopWatch.stop();
            logger.info(stopWatch.prettyPrint());
            logger.debug("replaceAllNodes() - End");
        }
    }

    /**
     * This function is called to fill the list of node types in the search filter view
     *
     * @param searchAndReplace The webflow model object containing the search results nodes and the list of node types to fill
     * @param renderContext    The render context containing the session used to access JCR
     */
    public void getNodesTypesList(SearchAndReplace searchAndReplace, RenderContext renderContext) {
        if (logger.isDebugEnabled()) {
            logger.debug("getNodesTypesList() - Start");
        }
        if (CollectionUtils.isEmpty(searchAndReplace.getListNodesTypes())) {
            for (SearchResult searchResult : searchAndReplace.getListSearchResult()) {
                //Browsing Search results list to get node types
                try {
                    JCRSessionWrapper session = renderContext.getMainResource().getNode().getSession();
                    JCRNodeWrapper node = session.getNodeByIdentifier(searchResult.getNodeUuid());

                    String localeAsString = renderContext.getRequest().getParameter("webflowLocale");
                    Locale locale = session.getLocale();
                    if (localeAsString != null) {
                        locale = locale.forLanguageTag(localeAsString);
                    }

                    String label = node.getPrimaryNodeType().getLabel(locale);

                    //getting the current result node type if it is not already in the list
                    if (!searchAndReplace.getListNodesTypes().contains(node.getPrimaryNodeTypeName())) {
                        searchAndReplace.getListNodesTypes().add(node.getPrimaryNodeTypeName());
                        searchAndReplace.getMapNodeTypeNames().put(node.getPrimaryNodeTypeName(), label);
                    }
                } catch (RepositoryException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("getNodesTypesList() - End");
        }
    }

    /**
     * This function fill the webflow model object properties list basing on the current selected node type
     *
     * @param searchAndReplace The webflow model object containing the current selected node type and the properties list
     * @param renderContext    The rendern context taht contains the session used to access JCR
     */
    public void getNodePropertiesList(SearchAndReplace searchAndReplace, RenderContext renderContext) {
        if (logger.isDebugEnabled()) {
            logger.debug("getNodePropertiesList() - Start");
        }
        //Clearing the list before filling it if needed
        if (CollectionUtils.isNotEmpty(searchAndReplace.getListFieldsOfNodeType()) && searchAndReplace.isDifferentNodeType()
                || searchAndReplace.getSelectedNodeType().isEmpty()) {
            searchAndReplace.getListFieldsOfNodeType().clear();
        }
        if (CollectionUtils.isNotEmpty(searchAndReplace.getListSelectedFieldsOfNodeType()) && searchAndReplace.isDifferentNodeType()) {
            searchAndReplace.getListSelectedFieldsOfNodeType().clear();
        }

        //if the selected node type has been changed
        if (searchAndReplace.isDifferentNodeType()) {
            for (SearchResult searchResult : searchAndReplace.getListSearchResult()) {
                //browsing the searchResults list to find a node of the selected type
                try {
                    JCRSessionWrapper session = renderContext.getMainResource().getNode().getSession();
                    JCRNodeWrapper node = session.getNodeByIdentifier(searchResult.getNodeUuid());

                    //When found a node of the nodetype
                    if (node.getPrimaryNodeType().toString().equals(searchAndReplace.getSelectedNodeType())) {
                        //Browsing its properties
                        for (String property : searchResult.getReplaceableProperties().keySet()) {
                            //Adding the property to the list if it is not already
                            if (!searchAndReplace.getListFieldsOfNodeType().contains(property)) {
                                searchAndReplace.getListFieldsOfNodeType().add(property);
                            }
                        }
                    }
                } catch (RepositoryException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("getNodePropertiesList() - End");
        }
    }

    public void setReplaceService(GlobalReplaceService replaceService) {
        this.replaceService = replaceService;
    }
}