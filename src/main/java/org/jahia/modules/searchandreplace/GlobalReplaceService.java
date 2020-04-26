/*
 * ==========================================================================================
 * =                   JAHIA'S DUAL LICENSING - IMPORTANT INFORMATION                       =
 * ==========================================================================================
 *
 *                                 http://www.jahia.com
 *
 *     Copyright (C) 2002-2020 Jahia Solutions Group SA. All rights reserved.
 *
 *     THIS FILE IS AVAILABLE UNDER TWO DIFFERENT LICENSES:
 *     1/GPL OR 2/JSEL
 *
 *     1/ GPL
 *     ==================================================================================
 *
 *     IF YOU DECIDE TO CHOOSE THE GPL LICENSE, YOU MUST COMPLY WITH THE FOLLOWING TERMS:
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 *     2/ JSEL - Commercial and Supported Versions of the program
 *     ===================================================================================
 *
 *     IF YOU DECIDE TO CHOOSE THE JSEL LICENSE, YOU MUST COMPLY WITH THE FOLLOWING TERMS:
 *
 *     Alternatively, commercial and supported versions of the program - also known as
 *     Enterprise Distributions - must be used in accordance with the terms and conditions
 *     contained in a separate written agreement between you and Jahia Solutions Group SA.
 *
 *     If you are unsure which license is appropriate for your use,
 *     please contact the sales department at sales@jahia.com.
 */
package org.jahia.modules.searchandreplace;

import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;

import java.util.List;
import java.util.Map;

/**
 * This Service expose some methods to replace strings or substrings in JCR node properties
 * Created by Rahmed on 15/04/14.
 */
public interface GlobalReplaceService {
    /**
     * Jahia Node Properties Replace Function (By Nodes Uuid list)
     * <p/>
     * This methode replace the termToReplace by the replacementTerm in all the editable properties of the nodes defined by the nodesUuids list
     * If there are no constraints or lock on nodes or properties
     *
     * @param nodesUuids      The nodes list corresponding to the nodes uuids on which apply the term replacement
     * @param termToReplace   mandatory parameter
     * @param replacementTerm mandatory parameter
     * @param searchMode      mandatory parameter defining the term search algorithm (possible values found in the searchModes Enum)
     * @param session         the session that will be used to access the nodes
     * @return Map<ReplaceStatus, List<String>> this Map will contain two entries : one with the key SUCCESS that will contain the list of UUIDs of replaced nodes
     * The other with the key FAILED will contain nodes on which no replacement could be done.
     */
    public Map<ReplaceStatus, List<String>> replaceByUuid(List<String> nodesUuids, String termToReplace, String replacementTerm, SearchMode searchMode, JCRSessionWrapper session);

    ;

    /**
     * Jahia Node Properties Replace Function (By Nodes Uuid list)
     * <p/>
     * This methode replace the termToReplace by the replacementTerm in all the properties found in the propertiesToReplace list and on the nodes defined by the nodesUuids list
     * If there are no constraints or lock on nodes or properties
     *
     * @param nodesUuids          The nodes list corresponding to the nodes uuids on which apply the term replacement
     * @param termToReplace       mandatory parameter
     * @param replacementTerm     mandatory parameter
     * @param searchMode          mandatory parameter defining the term search algorithm (possible values found in the searchModes Enum)
     * @param propertiesToReplace The list of properties name on which execute the replacement
     * @param session             the session that will be used to access the nodes
     * @return Map<ReplaceStatus, List<String>> this Map will contain two entries : one with the key SUCCESS that will contain the list of UUIDs of replaced nodes
     * The other with the key FAILED will contain nodes on which no replacement could be done.
     */
    public Map<ReplaceStatus, List<String>> replaceByUuid(List<String> nodesUuids, String termToReplace, String replacementTerm, SearchMode searchMode, List<String> propertiesToReplace, JCRSessionWrapper session);

    ;

    /**
     * Jahia Node Properties Replace Function (By Nodes list)
     * <p/>
     * This methode replace the termToReplace by the replacementTerm in all the editable properties of the nodes defined by the nodes list
     * If there are no constraints or lock on nodes or properties
     *
     * @param nodes           The nodes list corresponding to the nodes on which apply the term replacement
     * @param termToReplace   mandatory parameter
     * @param replacementTerm mandatory parameter
     * @param searchMode      mandatory parameter defining the term search algorithm (possible values found in the searchModes Enum)
     * @param session         the session that will be used to access the nodes
     * @return Map<ReplaceStatus, List<String>> this Map will contain two entries : one with the key SUCCESS that will contain the list of UUIDs of replaced nodes
     * The other with the key FAILED will contain nodes on which no replacement could be done.
     */
    public Map<ReplaceStatus, List<String>> replaceByNodes(List<JCRNodeWrapper> nodes, String termToReplace, String replacementTerm, SearchMode searchMode, JCRSessionWrapper session);

    /**
     * Jahia Node Properties Replace Function (By Nodes list)
     * <p/>
     * This methode replace the termToReplace by the replacementTerm in all the properties found in the propertiesToReplace list and on the nodes defined by the nodes list
     * If there are no constraints or lock on nodes or properties
     *
     * @param nodes               The nodes list corresponding to the nodes on which apply the term replacement
     * @param termToReplace       mandatory parameter
     * @param replacementTerm     mandatory parameter
     * @param searchMode          mandatory parameter defining the term search algorithm (possible values found in the searchModes Enum)
     * @param propertiesToReplace The list of properties name on which execute the replacement
     * @param session             the session that will be used to access the nodes
     * @return Map<ReplaceStatus, List<String>> this Map will contain two entries : one with the key SUCCESS that will contain the list of UUIDs of replaced nodes
     * The other with the key FAILED will contain nodes on which no replacement could be done.
     */
    public Map<ReplaceStatus, List<String>> replaceByNodes(List<JCRNodeWrapper> nodes, String termToReplace, String replacementTerm, SearchMode searchMode, List<String> propertiesToReplace, JCRSessionWrapper session);

    /**
     * Jahia Node Properties Replace Function (By SearchResult list)
     * <p/>
     * This methode replace the termToReplace by the replacementTerm in all the editable properties of the nodes defined by the searchResults list
     * If there are no constraints or lock on nodes or properties
     *
     * @param searchResults   The nodes list corresponding to the nodes on which apply the term replacement
     * @param termToReplace   mandatory parameter
     * @param replacementTerm mandatory parameter
     * @param searchMode      mandatory parameter defining the term search algorithm (possible values found in the searchModes Enum)
     * @param session         the session that will be used to access the nodes
     * @return Map<ReplaceStatus, List<String>> this Map will contain two entries : one with the key SUCCESS that will contain the list of UUIDs of replaced nodes
     * The other with the key FAILED will contain nodes on which no replacement could be done.
     */
    public Map<ReplaceStatus, List<String>> replaceBySearchResult(List<SearchResult> searchResults, String termToReplace, String replacementTerm, SearchMode searchMode, JCRSessionWrapper session);

    /**
     * Jahia Node Properties Replace Function (By SearchResult list)
     * <p/>
     * This methode replace the termToReplace by the replacementTerm in all the properties found in the propertiesToReplace list and on the nodes defined by the searchResults list
     * If there are no constraints or lock on nodes or properties
     *
     * @param searchResults       The nodes list corresponding to the nodes on which apply the term replacement
     * @param termToReplace       mandatory parameter
     * @param replacementTerm     mandatory parameter
     * @param searchMode          mandatory parameter defining the term search algorithm (possible values found in the searchModes Enum)
     * @param propertiesToReplace The list of properties name on which execute the replacement
     * @param session             the session that will be used to access the nodes
     * @return Map<ReplaceStatus, List<String>> this Map will contain two entries : one with the key SUCCESS that will contain the list of UUIDs of replaced nodes
     * The other with the key FAILED will contain nodes on which no replacement could be done.
     */
    public Map<ReplaceStatus, List<String>> replaceBySearchResult(List<SearchResult> searchResults, String termToReplace, String replacementTerm, SearchMode searchMode, List<String> propertiesToReplace, JCRSessionWrapper session);

    /**
     * Jahia Node Properties Replace Function (By Nodes list)
     * <p/>
     * This methode return the list of editable properties in the nodes defined by the nodesUuis list
     * An editable property is : A String property with no constraint on it (choicelist, value constraint or protected properties are not editable)
     *
     * @param nodesUuid     The nodes Uuid list corresponding to the nodes on which apply the term replacement
     * @param termToReplace mandatory parameter
     * @param searchMode    mandatory parameter defining the term search algorithm (possible values found in the searchModes Enum)
     * @param session       the session that will be used to access the nodes
     * @return Map<ReplaceStatus, List<String>> this Map will contain two entries : one with the key SUCCESS that will contain the list of replaceable SearchResults
     * The other with the key FAILED will contain SearchResults on which no replacement can be done.
     */
    public Map<ReplaceStatus, List<SearchResult>> getReplaceableProperties(List<String> nodesUuid, String termToReplace, SearchMode searchMode, JCRSessionWrapper session);

    public enum SearchMode {EXACT_MATCH, IGNORE_CASE, REGEXP}

    public enum ReplaceStatus {SUCCESS, FAILED}
}
