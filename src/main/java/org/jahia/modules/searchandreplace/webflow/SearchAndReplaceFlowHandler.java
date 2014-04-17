package org.jahia.modules.searchandreplace.webflow;

import org.jahia.modules.searchandreplace.GlobalReplaceService;
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
        String sitePath = renderContext.getSite().getPath();

        try{
            JCRSessionWrapper session = renderContext.getMainResource().getNode().getSession();
            QueryManager qm = session.getWorkspace().getQueryManager();
            Query q = qm.createQuery("SELECT * FROM [nt:base] AS result WHERE ISDESCENDANTNODE(result, '" + sitePath + "') AND CONTAINS(result.*,'" + searchAndReplace.getTermToReplace() + "') AND ([jcr:primaryType] NOT LIKE 'jnt:file' OR [jcr:primaryType] NOT LIKE 'jnt:resource')", Query.JCR_SQL2);
            q.setLimit(1000);
            NodeIterator ni = q.execute().getNodes();
            while (ni.hasNext()) {
                JCRNodeWrapper next = (JCRNodeWrapper) ni.next();
                PropertyIterator pi = next.getProperties();
                while (pi.hasNext()){
                    Property nextProperty = pi.nextProperty();
                    if(nextProperty.getType() == PropertyType.STRING){
                        if(nextProperty.getString().contains(searchAndReplace.getTermToReplace())){
                            searchAndReplace.addUUIDToListNodes(next.getIdentifier());
                        }
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

    public void setReplaceService(GlobalReplaceService replaceService) {
        this.replaceService = replaceService;
    }
}
