package org.jahia.modules.searchandreplace.webflow.model;

import org.apache.commons.lang.StringUtils;
import org.jahia.utils.i18n.Messages;
import org.slf4j.Logger;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.validation.ValidationContext;
import org.springframework.context.i18n.LocaleContextHolder;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by dgaillard on 24/03/14.
 */
public class SearchAndReplace implements Serializable {

    private static final Logger logger = getLogger(SearchAndReplace.class);

    private static final long serialVersionUID = 4282123925357469008L;

    private static final String BUNDLE = "resources.jahia-global-replace";

    private String searchNode;
    private String nodeType;
    private String startNode;
    private String termToReplace;
    private String matchType;
    private String replacementTerm;
    private String nodeTypeField;
    private List<String> listNodes;

    public SearchAndReplace() {
    }

    public boolean validateSearchAndReplaceFirstStep(ValidationContext context) {
        Locale locale = LocaleContextHolder.getLocale();
        MessageContext messages = context.getMessageContext();

        boolean valid = true;

        if (StringUtils.isBlank(searchNode)) {
            messages.addMessage(new MessageBuilder().error().source("searchNode").defaultText(Messages.get(BUNDLE, "jnt_searchAndReplace.searchNode.error", locale)).build());
            valid = false;
        }

        /*if (StringUtils.isBlank(nodeType)) {
            messages.addMessage(new MessageBuilder().error().source("nodeType").defaultText(Messages.get(BUNDLE, "jnt_searchAndReplace.nodeType.error", locale)).build());
            valid = false;
        }
        if (StringUtils.isBlank(startNode)) {
            messages.addMessage(new MessageBuilder().error().source("startNode").defaultText(Messages.get(BUNDLE, "jnt_searchAndReplace.startNode.error", locale)).build());
            valid = false;
        }*/

        return valid;
    }

    public boolean validateSearchAndReplaceSecondStep(ValidationContext context) {
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

    public boolean validateSearchAndReplaceThirdStep(ValidationContext context) {
        Locale locale = LocaleContextHolder.getLocale();
        MessageContext messages = context.getMessageContext();

        boolean valid = true;

        return valid;
    }

    public void setSearchNode(String searchNode) {
        this.searchNode = searchNode;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public void setStartNode(String startNode) {
        this.startNode = startNode;
    }

    public void setNodeTypeField(String nodeTypeField) {
        this.nodeTypeField = nodeTypeField;
    }

    public void setTermToReplace(String termToReplace) {
        this.termToReplace = termToReplace;
    }

    public void setMatchType(String matchType) {
        this.matchType = matchType;
    }

    public void setReplacementTerm(String replacementTerm) {
        this.replacementTerm = replacementTerm;
    }

    public void setListNodes(List<String> listNodes) {
        this.listNodes = listNodes;
    }

    public String getSearchNode() {
        return searchNode;
    }

    public String getNodeType() {
        return nodeType;
    }

    public String getStartNode() {
        return startNode;
    }

    public String getNodeTypeField() {
        return nodeTypeField;
    }

    public String getTermToReplace() {
        return termToReplace;
    }

    public String getMatchType() {
        return matchType;
    }

    public String getReplacementTerm() {
        return replacementTerm;
    }

    public List<String> getListNodes() {
        return listNodes;
    }
}
