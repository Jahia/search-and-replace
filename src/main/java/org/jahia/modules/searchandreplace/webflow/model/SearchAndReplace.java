package org.jahia.modules.searchandreplace.webflow.model;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
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
 * Created by dgaillard on 24/03/14.
 */
public class SearchAndReplace implements Serializable {

    private static final Logger logger = getLogger(SearchAndReplace.class);

    private static final long serialVersionUID = 4282123925357469008L;

    private static final String BUNDLE = "resources.jahia-global-replace";

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
    private boolean isDifferentNodeType;
    private List<String> listNodesTypes = new ArrayList<String>();
    private List<String> listFieldsOfNodeType = new ArrayList<String>();
    private List<String> listSelectedFieldsOfNodeType = new ArrayList<String>();
    private List<String> listNodesToBeUpdated = new ArrayList<String>();
    private List<String> listNodesUpdateSuccess = new ArrayList<String>();
    private List<String> listNodesUpdateFail = new ArrayList<String>();
    private List<String> listNodesSkipped = new ArrayList<String>();
    private List<SearchResult> listSearchResult;

    public SearchAndReplace() {

    }

    public SearchAndReplace(SearchAndReplace searchAndReplace) {
        setTermToReplace(searchAndReplace.getTermToReplace());
        setReplacementTerm(searchAndReplace.getReplacementTerm());
        setFromEventID(searchAndReplace.getFromEventID());
        setListNodesUpdateSuccess(searchAndReplace.getListNodesUpdateSuccess());
        setListNodesUpdateFail(searchAndReplace.getListNodesUpdateFail());
        setListNodesSkipped(searchAndReplace.getListNodesSkipped());
        setListSearchResult(searchAndReplace.getListSearchResult());
    }

    public boolean validateSearch(ValidationContext context) {
        Locale locale = LocaleContextHolder.getLocale();
        MessageContext messages = context.getMessageContext();

        setFromEventID("search");

        boolean valid = true;

        if (StringUtils.isBlank(termToReplace)) {
            messages.addMessage(new MessageBuilder().error().source("termToReplace").defaultText(Messages.get(BUNDLE, "jnt_searchAndReplace.searchNodes.error", locale)).build());
            valid = false;
        }

        return valid;
    }

    public boolean validateFilter(ValidationContext context) {
        Locale locale = LocaleContextHolder.getLocale();
        MessageContext messages = context.getMessageContext();

        boolean valid = true;

        if(context.getUserEvent().equals("goToReplace")){
            if (CollectionUtils.isEmpty(listNodesToBeUpdated)){
                messages.addMessage(new MessageBuilder().error().source("listNodesToBeUpdated").defaultText(Messages.get(BUNDLE, "jnt_searchAndReplace.listNodesToBeUpdated.error", locale)).build());
                valid = false;
            }

            if (selectAll == true) {
                listNodesToBeUpdated.clear();
                for(SearchResult node : listSearchResult){
                    listNodesToBeUpdated.add(node.getNodeUuid());
                }
            }
        }

        if(context.getUserEvent().equals("advancedSearchForm")){
            Matcher dateCreatedBeforeMatcher = defaultPattern.matcher(dateCreatedBefore);
            Matcher dateCreatedAfterMatcher = defaultPattern.matcher(dateCreatedAfter);
            Matcher dateModifiedBeforeMatcher = defaultPattern.matcher(dateModifiedBefore);
            Matcher dateModifiedAfterMatcher = defaultPattern.matcher(dateModifiedAfter);

            setFromEventID("advancedSearchForm");

            setDifferentNodeType(false);

            if(!StringUtils.isBlank(selectedNodeType)){
                if(!previousSelectedNodeType.equals(selectedNodeType)){
                    setPreviousSelectedNodeType(selectedNodeType);
                    isDifferentNodeType = true;
                }
            }

            if(!StringUtils.isBlank(dateCreatedBefore)) {
                if (!dateCreatedBeforeMatcher.matches()) {
                    messages.addMessage(new MessageBuilder().error().source("dateCreatedBefore").defaultText(Messages.get(BUNDLE, "jnt_searchAndReplace.dateCreatedBefore.error", locale)).build());
                    valid = false;
                }
            }

            if(!StringUtils.isBlank(dateCreatedAfter)) {
                if (!dateCreatedAfterMatcher.matches()) {
                    messages.addMessage(new MessageBuilder().error().source("dateCreatedAfter").defaultText(Messages.get(BUNDLE, "jnt_searchAndReplace.dateCreatedAfter.error", locale)).build());
                    valid = false;
                }
            }

            if(!StringUtils.isBlank(dateModifiedBefore)) {
                if (!dateModifiedBeforeMatcher.matches()) {
                    messages.addMessage(new MessageBuilder().error().source("dateModifiedBefore").defaultText(Messages.get(BUNDLE, "jnt_searchAndReplace.dateModifiedBefore.error", locale)).build());
                    valid = false;
                }
            }

            if(!StringUtils.isBlank(dateModifiedAfter)) {
                if (!dateModifiedAfterMatcher.matches()) {
                    messages.addMessage(new MessageBuilder().error().source("dateModifiedAfter").defaultText(Messages.get(BUNDLE, "jnt_searchAndReplace.dateModifiedAfter.error", locale)).build());
                    valid = false;
                }
            }
        }

        return valid;
    }

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

    public void setListNodesTypes(List<String> listNodesTypes) {
        this.listNodesTypes = listNodesTypes;
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

    public String getEscapedTermToReplace(){
        String escapedTermToReplace;

        //Lucene query interpreted characters
        String[] escapedCharacters = {"\\","+", "-", "&&" ,"||" ,"!", "(", ")", "{", "}", "[", "]", "^", "\"", "~", "*", "?", ":"};

        escapedTermToReplace =  JCRContentUtils.stringToJCRSearchExp(termToReplace);

        for (String characterToEscape : escapedCharacters)
        {
            String replacementCharacter = "\\"+characterToEscape;
            if(escapedTermToReplace.contains(characterToEscape))
            {
                escapedTermToReplace = escapedTermToReplace.replace(characterToEscape,replacementCharacter);
            }
        }

        return escapedTermToReplace;
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

    public List<String> getListNodesTypes() {
        return listNodesTypes;
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

    public void addUUIDToListNodesSkipped(String uuid){
        if(!listNodesSkipped.contains(uuid)){
            listNodesSkipped.add(uuid);
        }
    }
}
