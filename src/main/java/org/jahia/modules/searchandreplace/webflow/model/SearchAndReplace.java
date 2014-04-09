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

    private String searchNodes;
    private String nodeType;
    private String matchType;
    private List<String> listNodes;
    private Map<String, NodeToUpdate> listNodesToBeUpdated = new HashMap<String, NodeToUpdate>();

    public SearchAndReplace() {
    }

    public boolean validateSearchAndReplaceFirstStep(ValidationContext context) {
        Locale locale = LocaleContextHolder.getLocale();
        MessageContext messages = context.getMessageContext();

        boolean valid = true;

        if (StringUtils.isBlank(searchNodes)) {
            messages.addMessage(new MessageBuilder().error().source("searchNodes").defaultText(Messages.get(BUNDLE, "jnt_searchAndReplace.searchNodes.error", locale)).build());
            valid = false;
        }

        /*if (StringUtils.isBlank(nodeType)) {
            messages.addMessage(new MessageBuilder().error().source("nodeType").defaultText(Messages.get(BUNDLE, "jnt_searchAndReplace.nodeType.error", locale)).build());
            valid = false;
        }*/

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

        return valid;
    }

    public boolean validateSearchAndReplaceThirdStep(ValidationContext context) {
        Locale locale = LocaleContextHolder.getLocale();
        MessageContext messages = context.getMessageContext();

        boolean valid = true;

        /*if (StringUtils.isBlank(termToReplace)) {
            messages.addMessage(new MessageBuilder().error().source("termToReplace").defaultText(Messages.get(BUNDLE, "jnt_searchAndReplace.termToReplace.error", locale)).build());
            valid = false;
        }
        if (StringUtils.isBlank(replacementTerm)) {
            messages.addMessage(new MessageBuilder().error().source("replacementTerm").defaultText(Messages.get(BUNDLE, "jnt_searchAndReplace.replacementTerm.error", locale)).build());
            valid = false;
        }*/

        return valid;
    }

    public void setSearchNodes(String searchNodes) {
        this.searchNodes = searchNodes;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public void setMatchType(String matchType) {
        this.matchType = matchType;
    }

    public void setListNodes(List<String> listNodes) {
        this.listNodes = listNodes;
    }

    /*public void setListNodesToBeUpdated(Map<String, NodeToUpdate> listNodesToBeUpdated) {
        this.listNodesToBeUpdated = listNodesToBeUpdated;
    }*/

    public void setListNodesToBeUpdated(List<String> listNodesToBeUpdated) {
        for(String nodesId : listNodesToBeUpdated){
            this.listNodesToBeUpdated.put(nodesId, new NodeToUpdate());
        }
    }

    public String getSearchNodes() {
        return searchNodes;
    }

    public String getNodeType() {
        return nodeType;
    }

    public String getMatchType() {
        return matchType;
    }

    public List<String> getListNodes() {
        return listNodes;
    }

    public Map<String, NodeToUpdate> getListNodesToBeUpdated() {
        return listNodesToBeUpdated;
    }
}
