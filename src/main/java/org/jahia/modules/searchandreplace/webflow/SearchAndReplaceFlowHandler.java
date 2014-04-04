package org.jahia.modules.searchandreplace.webflow;

import org.jahia.modules.searchandreplace.webflow.model.SearchAndReplace;
import org.slf4j.Logger;

import java.io.Serializable;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by dgaillard on 24/03/14.
 */
public class SearchAndReplaceFlowHandler implements Serializable {

    private static final Logger logger = getLogger(SearchAndReplaceFlowHandler.class);

    private static final long serialVersionUID = -16287862741718967L;

    private static final String BUNDLE = "resources.jahia-global-replace";

    public SearchAndReplace initSearchAndReplace() {
        SearchAndReplace searchAndReplace = new SearchAndReplace();
        return searchAndReplace;
    }
}
