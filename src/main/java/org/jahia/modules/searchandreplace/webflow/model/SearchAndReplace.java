package org.jahia.modules.searchandreplace.webflow.model;

import org.apache.commons.lang.StringUtils;
import org.jahia.utils.i18n.Messages;
import org.slf4j.Logger;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.validation.ValidationContext;
import org.springframework.context.i18n.LocaleContextHolder;

import java.io.Serializable;
import java.util.*;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by dgaillard on 24/03/14.
 */
public class SearchAndReplace implements Serializable {

    private static final Logger logger = getLogger(SearchAndReplace.class);

    private static final long serialVersionUID = 4282123925357469008L;

    private static final String BUNDLE = "resources.jahia-global-replace";

    private String termToReplace;
    private String nodeType;
    private String matchType;
    private String replacementTerm;
    private String currentNodeInThirdStep;
    private boolean selectAll;
    private List<String> listNodes = new ArrayList<String>();
    private List<String> listNodesToBeUpdated = new ArrayList<String>();
    private List<String> listNodesUpdateSuccess = new ArrayList<String>();
    private List<String> listNodesUpdateFail = new ArrayList<String>();
    private List<String> listNodesSkipped = new ArrayList<String>();

    public SearchAndReplace() {
    }

    public boolean validateSearchAndReplaceFirstStep(ValidationContext context) {
        Locale locale = LocaleContextHolder.getLocale();
        MessageContext messages = context.getMessageContext();

        boolean valid = true;

        if (StringUtils.isBlank(termToReplace)) {
            messages.addMessage(new MessageBuilder().error().source("termToReplace").defaultText(Messages.get(BUNDLE, "jnt_searchAndReplace.searchNodes.error", locale)).build());
            valid = false;
        }

        return valid;
    }

    public boolean validateSearchAndReplaceSecondStep(ValidationContext context) {
        Locale locale = LocaleContextHolder.getLocale();
        MessageContext messages = context.getMessageContext();

        boolean valid = true;

        if (listNodesToBeUpdated == null){
            messages.addMessage(new MessageBuilder().error().source("listNodesToBeUpdated").defaultText(Messages.get(BUNDLE, "jnt_searchAndReplace.listNodesToBeUpdated.error", locale)).build());
            valid = false;
        }

        if (selectAll == true) {
            listNodesToBeUpdated.clear();
            listNodesToBeUpdated = listNodes;
        }

        return valid;
    }

    public boolean validateSearchAndReplaceThirdStep(ValidationContext context) {
        Locale locale = LocaleContextHolder.getLocale();
        MessageContext messages = context.getMessageContext();

        boolean valid = true;

        if (StringUtils.isBlank(replacementTerm)) {
            messages.addMessage(new MessageBuilder().error().source("replacementTerm").defaultText(Messages.get(BUNDLE, "jnt_searchAndReplace.replacementTerm.error", locale)).build());
            valid = false;
        }

        if (!currentNodeInThirdStep.equals(listNodesToBeUpdated.get(0))) {
            valid = false;
        }

        return valid;
    }

    public void setTermToReplace(String termToReplace) {
        this.termToReplace = termToReplace;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public void setMatchType(String matchType) {
        this.matchType = matchType;
    }

    public void setReplacementTerm(String replacementTerm) {
        this.replacementTerm = replacementTerm;
    }

    public void setCurrentNodeInThirdStep(String currentNodeInThirdStep) {
        this.currentNodeInThirdStep = currentNodeInThirdStep;
    }

    public void setSelectAll(boolean selectAll) {
        this.selectAll = selectAll;
    }

    public void setListNodes(List<String> listNodes) {
        this.listNodes = listNodes;
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

    public String getTermToReplace() {
        return termToReplace;
    }

    public String getNodeType() {
        return nodeType;
    }

    public String getMatchType() {
        return matchType;
    }

    public String getReplacementTerm() {
        return replacementTerm;
    }

    public String getCurrentNodeInThirdStep() {
        return currentNodeInThirdStep;
    }

    public boolean isSelectAll() {
        return selectAll;
    }

    public List<String> getListNodes() {
        return listNodes;
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

    public void addUUIDToListNodes(String uuid){
        if(!listNodes.contains(uuid)){
            listNodes.add(uuid);
        }
    }

    public void addUUIDToListNodesUpdateSuccess(String uuid){
        if(!listNodesUpdateSuccess.contains(uuid)){
            listNodesUpdateSuccess.add(uuid);
        }
    }

    public void addUUIDToListNodesUpdateFail(String uuid){
        if(!listNodesUpdateFail.contains(uuid)){
            listNodesUpdateFail.add(uuid);
        }
    }

    public void addUUIDToListNodesSkipped(String uuid){
        if(!listNodesSkipped.contains(uuid)){
            listNodesSkipped.add(uuid);
        }
    }
}
