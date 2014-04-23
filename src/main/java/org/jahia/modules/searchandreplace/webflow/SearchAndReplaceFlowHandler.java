package org.jahia.modules.searchandreplace.webflow;

import org.jahia.modules.searchandreplace.GlobalReplaceService;
import org.jahia.modules.searchandreplace.SearchResult;
import org.jahia.modules.searchandreplace.webflow.model.SearchAndReplace;
import org.jahia.services.content.JCRNodeIteratorWrapper;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.nodetypes.NodeTypeRegistry;
import org.jahia.services.render.RenderContext;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jcr.*;
import javax.jcr.nodetype.NodeType;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by dgaillard on 24/03/14.
 */
public class SearchAndReplaceFlowHandler implements Serializable {

    private static final Logger logger = getLogger(SearchAndReplaceFlowHandler.class);

    private static final long serialVersionUID = -16287862741718967L;

    private static final String BUNDLE = "resources.jahia-global-replace";

    @Autowired
    private transient GlobalReplaceService replaceService;

    public SearchAndReplace initSearchAndReplace() {
        SearchAndReplace searchAndReplace = new SearchAndReplace();
        return searchAndReplace;
    }

    public void getNodesContains(SearchAndReplace searchAndReplace, RenderContext renderContext){
        List<String> listNodes = new ArrayList<String>();
        String sitePath = renderContext.getSite().getPath();
        String subStringType = "";
        String dateCreatedBefore = "";
        String dateCreatedAfter = "";
        String dateModifiedBefore = "";
        String dateModifiedAfter = "";

        if(!searchAndReplace.getNodeType().isEmpty()){
            subStringType = " AND ([jcr:primaryType] LIKE '" + searchAndReplace.getNodeType() + "')";
        }

        if(!searchAndReplace.getDateCreatedBefore().isEmpty()){
            dateCreatedBefore = " AND ([jcr:created] < CAST ('+" + searchAndReplace.getDateCreatedBefore() + "T23:59:59.999Z' as date))";
        }

        if(!searchAndReplace.getDateCreatedAfter().isEmpty()){
            dateCreatedAfter = " AND ([jcr:created] > CAST ('+" + searchAndReplace.getDateCreatedAfter() + "T00:00:00.000Z' as date))";
        }

        if(!searchAndReplace.getDateModifiedBefore().isEmpty()){
            dateModifiedBefore = " AND ([jcr:lastModified] < CAST ('+" + searchAndReplace.getDateModifiedBefore() + "T23:59:59.999Z' as date))";
        }

        if(!searchAndReplace.getDateModifiedAfter().isEmpty()){
            dateModifiedAfter = " AND ([jcr:lastModified] > CAST ('+" + searchAndReplace.getDateModifiedAfter() + "T00:00:00.000Z' as date))";
        }

        try{
            JCRSessionWrapper session = renderContext.getMainResource().getNode().getSession();
            QueryManager qm = session.getWorkspace().getQueryManager();
            Query q = qm.createQuery("SELECT * FROM [nt:base] AS result WHERE ISDESCENDANTNODE(result, '" + sitePath + "') AND CONTAINS(result.*,'" + searchAndReplace.getTermToReplace() + "') AND ([jcr:primaryType] NOT LIKE 'jnt:file' OR [jcr:primaryType] NOT LIKE 'jnt:resource')" + subStringType + dateCreatedBefore + dateCreatedAfter + dateModifiedBefore + dateModifiedAfter, Query.JCR_SQL2);
            q.setLimit(1000);
            NodeIterator ni = q.execute().getNodes();
            while (ni.hasNext()) {
                JCRNodeWrapper next = (JCRNodeWrapper) ni.next();
                listNodes.add(next.getIdentifier());
            }
            searchAndReplace.setSearchResultList(replaceService.getReplaceableProperties(listNodes, searchAndReplace.getTermToReplace(), GlobalReplaceService.SearchMode.EXACT_MATCH, session).get(GlobalReplaceService.ReplaceStatus.SUCCESS));
        }catch(RepositoryException e){
            logger.error(e.getMessage(), e);
        }
    }

    public boolean haveNodeToUpdate(SearchAndReplace searchAndReplace) {
        if(searchAndReplace.getListNodesToBeUpdated().size() > 0){
            return true;
        }else{
            return false;
        }
    }

    public void skipThisNode(SearchAndReplace searchAndReplace) {
        String nodeID = searchAndReplace.getCurrentNodeInThirdStep();

        searchAndReplace.getListNodesToBeUpdated().remove(searchAndReplace.getListNodesToBeUpdated().indexOf(nodeID));
        searchAndReplace.addUUIDToListNodesSkipped(nodeID);
    }

    public void replaceThisNode(SearchAndReplace searchAndReplace, RenderContext renderContext) {
        String nodeID = searchAndReplace.getCurrentNodeInThirdStep();

        try{
            //Getting JCR Session
            JCRSessionWrapper session = renderContext.getMainResource().getNode().getSession();

            //Building List of NodesId to be replaced
            JCRNodeWrapper node = session.getNodeByUUID(nodeID);
            List<String> uuids = new ArrayList<String>();
            uuids.add(node.getIdentifier());

            //Calling Replace Service
            Map<GlobalReplaceService.ReplaceStatus,List<String>> replaceResult = replaceService.replaceByUuid(uuids,searchAndReplace.getTermToReplace(), searchAndReplace.getReplacementTerm(), GlobalReplaceService.SearchMode.EXACT_MATCH,session);

            //Getting Failed Replaced Nodes
            searchAndReplace.setListNodesUpdateFail(replaceResult.get(GlobalReplaceService.ReplaceStatus.FAILED));

            //Getting Successfully Replaced Nodes
            searchAndReplace.setListNodesUpdateSuccess(replaceResult.get(GlobalReplaceService.ReplaceStatus.SUCCESS));
        }catch (RepositoryException e){
            logger.error("replaceThisNodes() - Failed replacing the node ", e);
        }
    }

    public void replaceAllNodes(SearchAndReplace searchAndReplace, RenderContext renderContext) {
        try{
            //Getting session
            JCRSessionWrapper session = renderContext.getMainResource().getNode().getSession();

            //Calling Replace Service
            Map<GlobalReplaceService.ReplaceStatus,List<String>> replaceResult = replaceService.replaceByUuid(searchAndReplace.getListNodesToBeUpdated(), searchAndReplace.getTermToReplace(), searchAndReplace.getReplacementTerm(), GlobalReplaceService.SearchMode.EXACT_MATCH, session);

            //Getting Failed Replaced Nodes
            searchAndReplace.setListNodesUpdateFail(replaceResult.get(GlobalReplaceService.ReplaceStatus.FAILED));

            //Getting Successfully Replaced Nodes
            searchAndReplace.setListNodesUpdateSuccess(replaceResult.get(GlobalReplaceService.ReplaceStatus.SUCCESS));
        }catch (RepositoryException e){
            logger.error("replaceAllNodes() - Failed replacing given nodes ",e);
        }
        searchAndReplace.getListNodesToBeUpdated().clear();
    }

    public void getNodesTypesList(SearchAndReplace searchAndReplace, RenderContext renderContext) {
        searchAndReplace.getListNodesTypes().clear();
        for(SearchResult searchResult : searchAndReplace.getSearchResultList()){
            try{
                JCRSessionWrapper session = renderContext.getMainResource().getNode().getSession();
                JCRNodeWrapper node = session.getNodeByIdentifier(searchResult.getNodeUuid());
                if(!searchAndReplace.getListNodesTypes().contains(node.getPrimaryNodeTypeName())) {
                    searchAndReplace.getListNodesTypes().add(node.getPrimaryNodeTypeName());
                }
            }catch (RepositoryException e){
                logger.error(e.getMessage(), e);
            }
        }
    }

    public void setReplaceService(GlobalReplaceService replaceService) {
        this.replaceService = replaceService;
    }
}
