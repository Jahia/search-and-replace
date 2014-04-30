package org.jahia.modules.searchandreplace;

import java.io.Serializable;
import java.util.Map;

/**
 * This class defines the SearchResult Object
 * It is composed by a JCR node uuid and a Map<String,String> replaceableProperties
 * that contains the list of the node properties to replace
 * The key is the property name and the value is the property value
 * Created by Rahmed on 17/04/14.
 */
public class SearchResult implements Serializable {
    private String nodeUuid;
    private Map<String, String> replaceableProperties;

    public SearchResult(String nodeUuid, Map<String, String> replaceableProperties) {
        this.replaceableProperties = replaceableProperties;
        this.nodeUuid = nodeUuid;
    }

    public String getNodeUuid() {
        return nodeUuid;
    }

    public void setNodeUuid(String nodeUuid) {
        this.nodeUuid = nodeUuid;
    }

    public Map<String, String> getReplaceableProperties() {
        return replaceableProperties;
    }

    public void setReplaceableProperties(Map<String, String> replaceableProperties) {
        this.replaceableProperties = replaceableProperties;
    }

    public void addReplaceableProperty(String propertyName, String propertyValue) {
        this.getReplaceableProperties().put(propertyName, propertyValue);
    }

    @Override
    protected SearchResult clone() {
        return new SearchResult(this.getNodeUuid(), this.getReplaceableProperties());
    }
}
