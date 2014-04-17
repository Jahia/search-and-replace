package org.jahia.modules.searchandreplace;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by rizak on 17/04/14.
 */
public class SearchResult implements Serializable
{
    private String nodeUuid;
    private Map<String,String> replaceableProperties;

    public void SearchResult(String nodeUuid, Map<String,String> replaceableProperties)
    {
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

    public void addReplaceableProperty(String propertyName, String propertyValue)
    {
        this.getReplaceableProperties().put(propertyName,propertyValue);
    }
}
