package org.jahia.modules.searchandreplace.webflow.model;

import java.io.Serializable;

/**
 * Created by dgaillard on 09/04/14.
 */
public class NodeToUpdate implements Serializable {

    private static final long serialVersionUID = 5377576650217235330L;
    
    private String termToReplace;
    private String replacementTerm;
    private String nodeTypeField;

    public NodeToUpdate() {
    }

    public String getTermToReplace() {
        return termToReplace;
    }

    public String getReplacementTerm() {
        return replacementTerm;
    }

    public String getNodeTypeField() {
        return nodeTypeField;
    }

    public void setTermToReplace(String termToReplace) {
        this.termToReplace = termToReplace;
    }

    public void setReplacementTerm(String replacementTerm) {
        this.replacementTerm = replacementTerm;
    }

    public void setNodeTypeField(String nodeTypeField) {
        this.nodeTypeField = nodeTypeField;
    }

}
