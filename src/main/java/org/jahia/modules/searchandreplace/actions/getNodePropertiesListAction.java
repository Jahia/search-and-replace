package org.jahia.modules.searchandreplace.actions;

import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.modules.searchandreplace.SearchResult;
import org.jahia.modules.searchandreplace.webflow.model.SearchAndReplace;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by dgaillard on 24/04/14.
 */
public class getNodePropertiesListAction extends Action {

    private SearchAndReplace searchAndReplace;

    public ActionResult doExecute (HttpServletRequest req, RenderContext renderContext, Resource resource,
                                   JCRSessionWrapper session, Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {

        List<String> fields = new ArrayList<String>();

        for(SearchResult searchResult : searchAndReplace.getSearchResultList()){
            JCRNodeWrapper node = session.getNodeByIdentifier(searchResult.getNodeUuid());
            if(node.getPrimaryNodeType().equals(req.getParameter("nodeType"))) {
                for (String property : searchResult.getReplaceableProperties().keySet()) {
                    if (!fields.contains(property)) {
                        fields.add(property);
                    }
                }
            }
        }

        JSONObject jsonAnswer = new JSONObject(fields);

        return new ActionResult(HttpServletResponse.SC_OK, null, jsonAnswer);
    }

    public void setSearchAndReplace(SearchAndReplace searchAndReplace) {
        this.searchAndReplace = searchAndReplace;
    }
}
