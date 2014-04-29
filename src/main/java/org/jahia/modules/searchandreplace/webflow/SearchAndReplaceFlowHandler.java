package org.jahia.modules.searchandreplace.webflow;

import org.jahia.modules.searchandreplace.GlobalReplaceService;
import org.jahia.modules.searchandreplace.SearchResult;
import org.jahia.modules.searchandreplace.webflow.model.SearchAndReplace;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jcr.*;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by dgaillard on 24/03/14.
 */
public class SearchAndReplaceFlowHandler implements Serializable {

    private static final Logger logger = getLogger(SearchAndReplaceFlowHandler.class);

    private static final long serialVersionUID = -16287862741718967L;

    @Autowired
    private transient GlobalReplaceService replaceService;

    public SearchAndReplace initSearchAndReplace() {
        SearchAndReplace searchAndReplace = new SearchAndReplace();
        return searchAndReplace;
    }

    public void getNodesContains(SearchAndReplace searchAndReplace, RenderContext renderContext){
        List<String> listNodes = new ArrayList<String>();
        String sitePath = renderContext.getSite().getPath();

        StringBuilder query = new StringBuilder().append("SELECT * FROM [nt:base] AS result WHERE ISDESCENDANTNODE(result, '").append(sitePath).append("') AND CONTAINS(result.");

        if(searchAndReplace.getListSelectedFieldsOfNodeType() != null && !searchAndReplace.getListSelectedFieldsOfNodeType().isEmpty() && searchAndReplace.getListSelectedFieldsOfNodeType().size() == 1){
            query.append("[").append(searchAndReplace.getListSelectedFieldsOfNodeType().get(0)).append("]");
        }else{
            query.append("*");
        }

        query.append(",").append(searchAndReplace.getEscapedTermToReplace()).append(") AND ([jcr:primaryType] NOT LIKE 'jnt:file') AND ([jcr:primaryType] NOT LIKE 'jnt:resource')");

        if(!searchAndReplace.getSelectedNodeType().isEmpty()){
            query.append(" AND ([jcr:primaryType] LIKE '").append(searchAndReplace.getSelectedNodeType()).append("')");
        }

        if(!searchAndReplace.getDateCreatedBefore().isEmpty()){
            query.append(" AND ([jcr:created] < CAST ('+").append(searchAndReplace.getDateCreatedBefore()).append("T23:59:59.999Z' as date))");
        }

        if(!searchAndReplace.getDateCreatedAfter().isEmpty()){
            query.append(" AND ([jcr:created] > CAST ('+").append(searchAndReplace.getDateCreatedAfter()).append("T00:00:00.000Z' as date))");
        }

        if(!searchAndReplace.getDateModifiedBefore().isEmpty()){
            query.append(" AND ([jcr:lastModified] < CAST ('+").append(searchAndReplace.getDateModifiedBefore()).append("T23:59:59.999Z' as date))");
        }

        if(!searchAndReplace.getDateModifiedAfter().isEmpty()){
            query.append(" AND ([jcr:lastModified] > CAST ('+").append(searchAndReplace.getDateModifiedAfter()).append("T00:00:00.000Z' as date))");
        }

        try{
            JCRSessionWrapper session = renderContext.getMainResource().getNode().getSession();
            QueryManager qm = session.getWorkspace().getQueryManager();
            Query q = qm.createQuery(query.toString(), Query.JCR_SQL2);
            q.setLimit(1000);
            NodeIterator ni = q.execute().getNodes();
            while (ni.hasNext()) {
                JCRNodeWrapper next = (JCRNodeWrapper) ni.next();
                listNodes.add(next.getIdentifier());
            }
            String termToReplace = searchAndReplace.getEscapedTermToReplace().substring(1,searchAndReplace.getEscapedTermToReplace().length()-1);
            searchAndReplace.setListSearchResult(replaceService.getReplaceableProperties(listNodes, termToReplace, GlobalReplaceService.SearchMode.EXACT_MATCH, session).get(GlobalReplaceService.ReplaceStatus.SUCCESS));
            if(searchAndReplace.getListSelectedFieldsOfNodeType() != null && !searchAndReplace.getListSelectedFieldsOfNodeType().isEmpty() && searchAndReplace.getListSelectedFieldsOfNodeType().size() > 1) {
                for (SearchResult searchResult : searchAndReplace.getListSearchResult()) {
                    if (!searchResult.getReplaceableProperties().containsKey(searchAndReplace.getListSelectedFieldsOfNodeType())) {
                        searchAndReplace.getListSearchResult().indexOf(searchResult);
                    }
                }
            }
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
        String nodeID = searchAndReplace.getCurrentDisplayedNode();

        searchAndReplace.getListNodesToBeUpdated().remove(searchAndReplace.getListNodesToBeUpdated().indexOf(nodeID));
        searchAndReplace.addUUIDToListNodesSkipped(nodeID);
    }

    public void replaceThisNode(SearchAndReplace searchAndReplace, RenderContext renderContext) {
        String nodeID = searchAndReplace.getCurrentDisplayedNode();
        Map<GlobalReplaceService.ReplaceStatus,List<String>> replaceResult = new HashMap<GlobalReplaceService.ReplaceStatus, List<String>>();

        try{
            //Getting JCR Session
            JCRSessionWrapper session = renderContext.getMainResource().getNode().getSession();

            List<String> uuids = new ArrayList<String>();
            uuids.add(nodeID);

            //Calling Replace Service
            String termToReplace = searchAndReplace.getEscapedTermToReplace().substring(1,searchAndReplace.getEscapedTermToReplace().length()-1);
            if(searchAndReplace.getListSelectedFieldsOfNodeType() != null){
                if(!searchAndReplace.getListSelectedFieldsOfNodeType().isEmpty()){
                    replaceResult = replaceService.replaceByUuid(uuids, termToReplace, searchAndReplace.getReplacementTerm(), GlobalReplaceService.SearchMode.EXACT_MATCH, searchAndReplace.getListSelectedFieldsOfNodeType(), session);
                }
            }else{
                replaceResult = replaceService.replaceByUuid(uuids, termToReplace, searchAndReplace.getReplacementTerm(), GlobalReplaceService.SearchMode.EXACT_MATCH, session);
            }

            //Getting Failed Replaced Nodes
            if(!replaceResult.get(GlobalReplaceService.ReplaceStatus.FAILED).isEmpty()){
                searchAndReplace.getListNodesUpdateFail().add(replaceResult.get(GlobalReplaceService.ReplaceStatus.FAILED).get(0));
            }

            //Getting Successfully Replaced Nodes
            if(!replaceResult.get(GlobalReplaceService.ReplaceStatus.SUCCESS).isEmpty()){
                searchAndReplace.getListNodesUpdateSuccess().add(replaceResult.get(GlobalReplaceService.ReplaceStatus.SUCCESS).get(0));
            }
        }catch (RepositoryException e){
            logger.error("replaceThisNodes() - Failed replacing the node ", e);
        }
        searchAndReplace.getListNodesToBeUpdated().remove(nodeID);
    }

    public void replaceAllNodes(SearchAndReplace searchAndReplace, RenderContext renderContext) {
        Map<GlobalReplaceService.ReplaceStatus,List<String>> replaceResult = new HashMap<GlobalReplaceService.ReplaceStatus, List<String>>();

        try{
            //Getting session
            JCRSessionWrapper session = renderContext.getMainResource().getNode().getSession();

            //Calling Replace Service
            String termToReplace = searchAndReplace.getEscapedTermToReplace().substring(1,searchAndReplace.getEscapedTermToReplace().length()-1);
            if(searchAndReplace.getListSelectedFieldsOfNodeType() != null){
                if(!searchAndReplace.getListSelectedFieldsOfNodeType().isEmpty()){
                    replaceResult = replaceService.replaceByUuid(searchAndReplace.getListNodesToBeUpdated(), termToReplace, searchAndReplace.getReplacementTerm(), GlobalReplaceService.SearchMode.EXACT_MATCH, searchAndReplace.getListSelectedFieldsOfNodeType(), session);
                }
            }else{
                replaceResult = replaceService.replaceByUuid(searchAndReplace.getListNodesToBeUpdated(), termToReplace, searchAndReplace.getReplacementTerm(), GlobalReplaceService.SearchMode.EXACT_MATCH, session);
            }

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
        if(searchAndReplace.getListNodesTypes().isEmpty()){
            for(SearchResult searchResult : searchAndReplace.getListSearchResult()){
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
    }

    public void getNodePropertiesList(SearchAndReplace searchAndReplace, RenderContext renderContext) {

        if(!searchAndReplace.getListFieldsOfNodeType().isEmpty() && searchAndReplace.isDifferent() || searchAndReplace.getSelectedNodeType().isEmpty()){
            searchAndReplace.getListFieldsOfNodeType().clear();
        }

        if(searchAndReplace.getListSelectedFieldsOfNodeType() != null && !searchAndReplace.getListSelectedFieldsOfNodeType().isEmpty() && searchAndReplace.isDifferent()){
            searchAndReplace.getListSelectedFieldsOfNodeType().clear();
        }

        if(searchAndReplace.isDifferent()){
            for(SearchResult searchResult : searchAndReplace.getListSearchResult()){
                try{
                    JCRSessionWrapper session = renderContext.getMainResource().getNode().getSession();
                    JCRNodeWrapper node = session.getNodeByIdentifier(searchResult.getNodeUuid());
                    if(node.getPrimaryNodeType().toString().equals(searchAndReplace.getSelectedNodeType())) {
                        for(String property : searchResult.getReplaceableProperties().keySet()){
                            if(!searchAndReplace.getListFieldsOfNodeType().contains(property)){
                                searchAndReplace.getListFieldsOfNodeType().add(property);
                            }
                        }
                    }
                }catch (RepositoryException e){
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    public void setReplaceService(GlobalReplaceService replaceService) {
        this.replaceService = replaceService;
    }
}
