package org.jahia.modules.searchandreplace.actions;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.*;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by dgaillard on 20/03/14.
 */
public class ReplaceAll  extends Action {

    private static final Logger logger = getLogger(ReplaceAll.class);

    public ReplaceAll() { }

    public ActionResult doExecute(final HttpServletRequest req,final RenderContext renderContext,final Resource resource,
                                  JCRSessionWrapper session,final Map<String, List<String>> parameters,final URLResolver urlResolver) throws Exception {



        if(parameters.get("termToReplace") != null) {
            String termToReplace = ((String)((List)parameters.get("termToReplace")).get(0)).trim();
            req.getSession().setAttribute("termToReplace", termToReplace);
        }

        if(parameters.get("field") != null) {
            String field = (String)((List)parameters.get("field")).get(0);
            req.getSession().setAttribute("field", field);
        }

        if(parameters.get("remplacement") != null) {
            String remplacement = (String)((List)parameters.get("remplacement")).get(0);
            req.getSession().setAttribute("remplacement", remplacement);
        }

        if(parameters.get("searchOperator") != null) {
            String searchOperator = (String)((List)parameters.get("searchOperator")).get(0);
            req.getSession().setAttribute("searchOperator", searchOperator);
        }

        if(parameters.get("searchFor") != null) {
            String searchFor = (String)((List)parameters.get("searchFor")).get(0);
            req.getSession().setAttribute("searchFor", searchFor);
        }

        if(parameters.get("performReplace") != null) {
            String performReplace = (String)((List)parameters.get("performReplace")).get(0);
            req.getSession().setAttribute("performReplace", performReplace);
        }

        if(parameters.get("nodesToUpdate") != null) {
            List nodesToUpdate = (List)parameters.get("nodesToUpdate");
            req.getSession().setAttribute("nodesToUpdate", nodesToUpdate);
        }

        String userRedirectPage = (String)((List)parameters.get("userredirectpage")).get(0);
        req.getSession().setAttribute("userRedirectPage", userRedirectPage);

        return new ActionResult(200, userRedirectPage);
    }
}