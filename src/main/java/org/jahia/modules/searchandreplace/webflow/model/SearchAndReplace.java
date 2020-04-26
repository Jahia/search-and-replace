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
package org.jahia.modules.searchandreplace.webflow.model;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jahia.modules.searchandreplace.GlobalReplaceService;
import org.jahia.modules.searchandreplace.SearchResult;
import org.jahia.services.content.JCRContentUtils;
import org.jahia.utils.i18n.Messages;
import org.slf4j.Logger;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.validation.ValidationContext;
import org.springframework.context.i18n.LocaleContextHolder;

import java.io.Serializable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * This class defines the Webflow model object
 * that contains form data for search and replace views
 * Created by dgaillard on 24/03/14.
 */
public class SearchAndReplace implements Serializable {

    private static final Logger logger = getLogger(SearchAndReplace.class);

    private static final long serialVersionUID = 4282123925357469008L;

    private static final String BUNDLE = "resources.search-and-replace";

    private static final String DEFAULT_REGEXP = "^[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])$";

    private Pattern defaultPattern = Pattern.compile(DEFAULT_REGEXP);

    private String termToReplace;
    private String fromEventID = "";
    private String selectedNodeType = "";
    private String previousSelectedNodeType = "";
    private String matchType;
    private String replacementTerm;
    private String currentDisplayedNode;
    private String dateCreatedBefore = "";
    private String dateCreatedAfter = "";
    private String dateModifiedBefore = "";
    private String dateModifiedAfter = "";
    private boolean selectAll;
    private boolean selectAllProperties;
    private boolean isDifferentNodeType;
    private Map<String, String> mapNodeTypeNames = new HashMap<String, String>();
    private List<String> listFieldsOfNodeType = new ArrayList<String>();
    private List<String> listSelectedFieldsOfNodeType = new ArrayList<String>();
    private List<String> listNodesToBeUpdated = new ArrayList<String>();
    private List<String> listNodesUpdateSuccess = new ArrayList<String>();
    private List<String> listNodesUpdateFail = new ArrayList<String>();
    private List<String> listNodesSkipped = new ArrayList<String>();
    private List<SearchResult> listSearchResult = Collections.emptyList();
    private List<String> listPropertiesToBeReplaced = new ArrayList<String>();

    public SearchAndReplace() {

    }

    /**
     * This constructor initialize the instance basing on an existing
     * object values keeping only :
     * termToReplace, replacementTerm, fromEventId, listNodesUpdateSuccess,
     * listNodesUpdateFail, listNodesSkipped and listSearchResult
     *
     * @param searchAndReplace : the existing object from which take values
     */
    public SearchAndReplace(SearchAndReplace searchAndReplace) {
        setTermToReplace(searchAndReplace.getTermToReplace());
        setReplacementTerm(searchAndReplace.getReplacementTerm());
        setFromEventID(searchAndReplace.getFromEventID());
        setListNodesUpdateSuccess(searchAndReplace.getListNodesUpdateSuccess());
        setListNodesUpdateFail(searchAndReplace.getListNodesUpdateFail());
        setListNodesSkipped(searchAndReplace.getListNodesSkipped());
        setListSearchResult(searchAndReplace.getListSearchResult());
    }

    /**
     * This function validate the model object submitted by Search forms
     * The only entry to be validated is termToReplace
     *
     * @param context : the context in which put validation messages
     * @return boolean : validation result boolean
     */
    public boolean validateSearch(ValidationContext context) {
        Locale locale = LocaleContextHolder.getLocale();
        MessageContext messages = context.getMessageContext();

        setFromEventID("search");

        boolean valid = true;

        if (StringUtils.isBlank(termToReplace)) {
            messages.addMessage(new MessageBuilder().error().source("termToReplace")
                    .defaultText(Messages.get(BUNDLE, "jnt_searchAndReplace.termToReplace.error", locale)).build());
            valid = false;
        }

        return valid;
    }

    /**
     * This function validate the model object submitted by Filter forms
     * and set :
     *
     * - The listNodesToBeUpdated in case of selectAll
     * - isDifferentNodeType each time the selected node type is different of the previous node type
     *
     * The following entries are checked for validation :
     * listNodesToBeUpdated,creationDateBefore, creationDateAfter, modificationDateBefore, modificationDateAfter
     *
     * @param context : the context in which put validation messages
     * @return boolean : validation result boolean
     */
    public boolean validateFilter(ValidationContext context) {
        Locale locale = LocaleContextHolder.getLocale();
        MessageContext messages = context.getMessageContext();

        boolean valid = true;

        if (context.getUserEvent().equals("goToReplace")) {
            if (CollectionUtils.isEmpty(listNodesToBeUpdated)) {
                messages.addMessage(new MessageBuilder().error().source("listNodesToBeUpdated")
                        .defaultText(Messages.get(BUNDLE, "jnt_searchAndReplace.listNodesToBeUpdated.error", locale)).build());
                valid = false;
            }

            //filling listNodesToBeUpdated with all nodes without checking
            //if selectAll boolean is true
            if (selectAll) {
                listNodesToBeUpdated.clear();
                for (SearchResult node : listSearchResult) {
                    listNodesToBeUpdated.add(node.getNodeUuid());
                }
            }
        }

        if (context.getUserEvent().equals("advancedSearchForm")) {
            //Regex pattern to validate dates
            Matcher dateCreatedBeforeMatcher = defaultPattern.matcher(dateCreatedBefore);
            Matcher dateCreatedAfterMatcher = defaultPattern.matcher(dateCreatedAfter);
            Matcher dateModifiedBeforeMatcher = defaultPattern.matcher(dateModifiedBefore);
            Matcher dateModifiedAfterMatcher = defaultPattern.matcher(dateModifiedAfter);

            setFromEventID("advancedSearchForm");

            setDifferentNodeType(false);
            //setting isDifferentNodeType
            if (!StringUtils.isBlank(selectedNodeType)) {
                if (!previousSelectedNodeType.equals(selectedNodeType)) {
                    setPreviousSelectedNodeType(selectedNodeType);
                    isDifferentNodeType = true;
                }
            }

            //creation date (Before) validation
            if (!StringUtils.isBlank(dateCreatedBefore)) {
                if (!dateCreatedBeforeMatcher.matches()) {
                    messages.addMessage(new MessageBuilder().error().source("dateCreatedBefore")
                            .defaultText(Messages.get(BUNDLE, "jnt_searchAndReplace.dateCreatedBefore.error", locale)).build());
                    valid = false;
                }
            }

            //creation date (After) validation
            if (!StringUtils.isBlank(dateCreatedAfter)) {
                if (!dateCreatedAfterMatcher.matches()) {
                    messages.addMessage(new MessageBuilder().error().source("dateCreatedAfter")
                            .defaultText(Messages.get(BUNDLE, "jnt_searchAndReplace.dateCreatedAfter.error", locale)).build());
                    valid = false;
                }
            }

            //Modification date (Before) validation
            if (!StringUtils.isBlank(dateModifiedBefore)) {
                if (!dateModifiedBeforeMatcher.matches()) {
                    messages.addMessage(new MessageBuilder().error().source("dateModifiedBefore")
                            .defaultText(Messages.get(BUNDLE, "jnt_searchAndReplace.dateModifiedBefore.error", locale)).build());
                    valid = false;
                }
            }

            //Modification date (After) validation
            if (!StringUtils.isBlank(dateModifiedAfter)) {
                if (!dateModifiedAfterMatcher.matches()) {
                    messages.addMessage(new MessageBuilder().error().source("dateModifiedAfter")
                            .defaultText(Messages.get(BUNDLE, "jnt_searchAndReplace.dateModifiedAfter.error", locale)).build());
                    valid = false;
                }
            }
        }

        return valid;
    }

    /**
     * This function validate the model object submitted by Replace forms
     *
     * @param context : the context in which put validation messages
     * @return boolean : validation result boolean
     */
    public boolean validateReplace(ValidationContext context) {
        boolean valid = true;

        if (!currentDisplayedNode.equals(listNodesToBeUpdated.get(0))) {
            valid = false;
        }
        return valid;
    }

    public void setTermToReplace(String termToReplace) {
        this.termToReplace = termToReplace;
    }

    public void setFromEventID(String fromEventID) {
        this.fromEventID = fromEventID;
    }

    public void setSelectedNodeType(String selectedNodeType) {
        this.selectedNodeType = selectedNodeType;
    }

    public void setMatchType(String matchType) {
        this.matchType = matchType;
    }

    public void setReplacementTerm(String replacementTerm) {
        this.replacementTerm = replacementTerm;
    }

    public void setCurrentDisplayedNode(String currentDisplayedNode) {
        this.currentDisplayedNode = currentDisplayedNode;
    }

    public void setDateCreatedBefore(String dateCreatedBefore) {
        this.dateCreatedBefore = dateCreatedBefore;
    }

    public void setDateCreatedAfter(String dateCreatedAfter) {
        this.dateCreatedAfter = dateCreatedAfter;
    }

    public void setDateModifiedBefore(String dateModifiedBefore) {
        this.dateModifiedBefore = dateModifiedBefore;
    }

    public void setDateModifiedAfter(String dateModifiedAfter) {
        this.dateModifiedAfter = dateModifiedAfter;
    }

    public void setPreviousSelectedNodeType(String previousSelectedNodeType) {
        this.previousSelectedNodeType = previousSelectedNodeType;
    }

    public void setSelectAll(boolean selectAll) {
        this.selectAll = selectAll;
    }

    public void setDifferentNodeType(boolean isDifferent) {
        this.isDifferentNodeType = isDifferent;
    }

    public void setMapNodeTypeNames(Map<String, String> mapNodeTypeNames) {
        this.mapNodeTypeNames = mapNodeTypeNames;
    }

    public void setListFieldsOfNodeType(List<String> listFieldsOfNodeType) {
        this.listFieldsOfNodeType = listFieldsOfNodeType;
    }

    public void setListSelectedFieldsOfNodeType(List<String> listSelectedFieldsOfNodeType) {
        this.listSelectedFieldsOfNodeType = listSelectedFieldsOfNodeType;
    }

    public void setListNodesToBeUpdated(List<String> listNodesToBeUpdated) {
        this.listNodesToBeUpdated = listNodesToBeUpdated;
    }

    public void setListNodesUpdateSuccess(List<String> listNodesUpdateSuccess) {
        this.listNodesUpdateSuccess = listNodesUpdateSuccess;
    }

    public void setListNodesUpdateFail(List<String> listNodesUpdateFail) {
        this.listNodesUpdateFail = listNodesUpdateFail;
    }

    public void setListNodesSkipped(List<String> listNodesSkipped) {
        this.listNodesSkipped = listNodesSkipped;
    }

    public void setListSearchResult(List<SearchResult> listSearchResult) {
        this.listSearchResult = listSearchResult;
    }

    public String getTermToReplace() {
        return termToReplace;
    }

    public boolean isSelectAllProperties() {
        return selectAllProperties;
    }

    public void setSelectAllProperties(boolean selectAllProperties) {
        this.selectAllProperties = selectAllProperties;
    }

    public List<String> getListPropertiesToBeReplaced() {
        return listPropertiesToBeReplaced;
    }

    public void setListPropertiesToBeReplaced(List<String> listPropertiesToBeReplaced) {
        this.listPropertiesToBeReplaced = listPropertiesToBeReplaced;
    }

    /**
     * This function escape the termToReplace input for JCR query execution
     * All the characters interpreted by lucene are escaped with : \\
     * These characters are : \\,+, -, && ,|| ,!, (, ), {, }, [, ], ^, ", ~, *, ?, :
     *
     * @return String escaped termToReplace input
     */
    public String getEscapedTermToReplace() {
        String escapedTermToReplace;
        //Lucene query interpreted characters
        escapedTermToReplace = "\"" + termToReplace.trim() + "\"";
        return JCRContentUtils.stringToJCRSearchExp(escapedTermToReplace);
    }

    public String getFromEventID() {
        return fromEventID;
    }

    public String getSelectedNodeType() {
        return selectedNodeType;
    }

    public String getMatchType() {
        return matchType;
    }

    public String getReplacementTerm() {
        return replacementTerm;
    }

    public String getCurrentDisplayedNode() {
        return currentDisplayedNode;
    }

    public String getDateCreatedBefore() {
        return dateCreatedBefore;
    }

    public String getDateCreatedAfter() {
        return dateCreatedAfter;
    }

    public String getDateModifiedBefore() {
        return dateModifiedBefore;
    }

    public String getDateModifiedAfter() {
        return dateModifiedAfter;
    }

    public String getPreviousSelectedNodeType() {
        return previousSelectedNodeType;
    }

    public boolean isSelectAll() {
        return selectAll;
    }

    public boolean isDifferentNodeType() {
        return isDifferentNodeType;
    }

    public Map<String, String> getMapNodeTypeNames() {
        return mapNodeTypeNames;
    }

    public List<String> getListFieldsOfNodeType() {
        return listFieldsOfNodeType;
    }

    public List<String> getListSelectedFieldsOfNodeType() {
        return listSelectedFieldsOfNodeType;
    }

    public List<String> getListNodesToBeUpdated() {
        return listNodesToBeUpdated;
    }

    public List<String> getListNodesUpdateSuccess() {
        return listNodesUpdateSuccess;
    }

    public List<String> getListNodesUpdateFail() {
        return listNodesUpdateFail;
    }

    public List<String> getListNodesSkipped() {
        return listNodesSkipped;
    }

    public List<SearchResult> getListSearchResult() {
        return listSearchResult;
    }

    public void addUUIDToListNodesSkipped(String uuid) {
        if (!listNodesSkipped.contains(uuid)) {
            listNodesSkipped.add(uuid);
        }
    }
}