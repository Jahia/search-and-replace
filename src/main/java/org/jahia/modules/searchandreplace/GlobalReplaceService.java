package org.jahia.modules.searchandreplace;

import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;

import java.util.List;
import java.util.Map;

/**
 * Created by rizak on 15/04/14.
 */
public interface GlobalReplaceService
{
    public enum SearchMode {EXACT_MATCH, IGNORE_CASE, REGEXP};
    public enum ReplaceStatus {SUCCESS, FAILED};

    /**
     * Jahia Node Properties Replace Function (By Nodes Uuid list)
     * @author Rizak
     *
     * @param nodesUuids The nodes list corresponding to the nodes uuids on which apply the term replacement
     * @param termToReplace madatory parameter
     * @param replacementTerm mandatory parameter
     * @param searchMode mandatory parameter defining the term search algorithm (possible values found in the searchModes Enum)
     * @param session the session that will be used to access the nodes
     */
    public Map<ReplaceStatus,List<String>> replaceByUuid(List<String> nodesUuids, String termToReplace, String replacementTerm, SearchMode searchMode, JCRSessionWrapper session);

    /**
     * Jahia Node Properties Replace Function (By Nodes list)
     * @author Rizak
     *
     * @param nodes The nodes list corresponding to the nodes on which apply the term replacement
     * @param termToReplace madatory parameter
     * @param replacementTerm mandatory parameter
     * @param searchMode mandatory parameter defining the term search algorithm (possible values found in the searchModes Enum)
     * @param session the session that will be used to access the nodes
     */
    public Map<ReplaceStatus,List<String>> replaceByNodes(List<JCRNodeWrapper> nodes, String termToReplace, String replacementTerm, SearchMode searchMode, JCRSessionWrapper session);

    /**
     * Jahia Node Properties Replace Function (By Nodes list)
     * @author Rizak
     *
     * @param searchResults The nodes list corresponding to the nodes on which apply the term replacement
     * @param termToReplace madatory parameter
     * @param replacementTerm mandatory parameter
     * @param searchMode mandatory parameter defining the term search algorithm (possible values found in the searchModes Enum)
     * @param session the session that will be used to access the nodes
     */
    public Map<ReplaceStatus,List<String>> replaceBySearchResult(List<SearchResult> searchResults, String termToReplace, String replacementTerm, SearchMode searchMode, JCRSessionWrapper session);

    /**
     * Jahia Node Properties Replace Function (By Nodes list)
     * @author Rizak
     *
     * @param nodesUuid The nodes Uuid list corresponding to the nodes on which apply the term replacement
     * @param termToReplace madatory parameter
     * @param searchMode mandatory parameter defining the term search algorithm (possible values found in the searchModes Enum)
     * @param session the session that will be used to access the nodes
     */
    public Map<ReplaceStatus,List<SearchResult>> getReplaceableProperties(List<String> nodesUuid, String termToReplace, SearchMode searchMode, JCRSessionWrapper session);
}
