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

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * This class defines the SearchResult Object
 * It is composed by a JCR node uuid and a Map<String,List<String>> replaceableProperties
 * that contains the list of the node properties (and their values) to replace
 * The key is the property name and the value is the property value
 * Created by Rahmed on 17/04/14.
 */
public class SearchResult implements Serializable {
    private String nodeUuid;
    private String nodeTypeLabel;
    private Map<String, List<String>> replaceableProperties;

    public SearchResult(String nodeUuid, Map<String, List<String>> replaceableProperties) {
        this.replaceableProperties = replaceableProperties;
        this.nodeUuid = nodeUuid;
    }

    public String getNodeUuid() {
        return nodeUuid;
    }

    public void setNodeUuid(String nodeUuid) {
        this.nodeUuid = nodeUuid;
    }

    public String getNodeTypeLabel() {
        return nodeTypeLabel;
    }

    public void setNodeTypeLabel(String nodeTypeLabel) {
        this.nodeTypeLabel = nodeTypeLabel;
    }

    public Map<String, List<String>> getReplaceableProperties() {
        return replaceableProperties;
    }

    public void setReplaceableProperties(Map<String, List<String>> replaceableProperties) {
        this.replaceableProperties = replaceableProperties;
    }

    public void addReplaceableProperty(String propertyName, List<String> propertyValues) {
        this.getReplaceableProperties().put(propertyName, propertyValues);
    }

    @Override protected SearchResult clone() {
        return new SearchResult(this.getNodeUuid(), this.getReplaceableProperties());
    }
}
