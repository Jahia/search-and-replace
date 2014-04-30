package org.jahia.modules.searchandreplace.impl;

import com.google.common.collect.Lists;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.jahia.modules.searchandreplace.GlobalReplaceService;
import org.jahia.modules.searchandreplace.SearchResult;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRPropertyWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Property;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class implements the service GlobalReplaceService
 * Created by rizak on 15/04/14.
 */
public class GlobalReplaceServiceImpl implements GlobalReplaceService {
    private static Logger logger = LoggerFactory.getLogger(GlobalReplaceServiceImpl.class);

    @Override
    public Map<ReplaceStatus, List<String>> replaceByUuid(List<String> nodesUuid, String termToReplace, String replacementTerm, SearchMode searchMode, JCRSessionWrapper session) {
        if (logger.isDebugEnabled()) {
            logger.debug("replaceByUuid - Start");
        }

        //Node Replacement state result map
        Map<ReplaceStatus, List<String>> replaceResultMap = new HashMap<ReplaceStatus, List<String>>();
        replaceResultMap.put(ReplaceStatus.SUCCESS, new ArrayList<String>());
        replaceResultMap.put(ReplaceStatus.FAILED, new ArrayList<String>());

        //Getting the liste of replaceable nodes and properties
        Map<ReplaceStatus, List<SearchResult>> replaceableMap = getReplaceableProperties(nodesUuid, termToReplace, searchMode, session);

        //putting in the replaceResultMap[FAILED] all the nodes consider not replaceable
        for (SearchResult result : replaceableMap.get(ReplaceStatus.FAILED)) {
            replaceResultMap.get(ReplaceStatus.FAILED).add(result.getNodeUuid());
        }

        //Replacing the properties in all nodes
        for (SearchResult result : replaceableMap.get(ReplaceStatus.SUCCESS)) {
            //Calling the replace and putting the node uuid in the good case of replaceResultMap thanks to replaceNode return
            replaceResultMap.get(replaceNode(result, termToReplace, replacementTerm, searchMode, session)).add(result.getNodeUuid());
        }
        if (logger.isDebugEnabled()) {
            logger.debug("replaceByUuid - End");
        }
        return replaceResultMap;
    }

    @Override
    public Map<ReplaceStatus, List<String>> replaceByUuid(List<String> nodesUuid, String termToReplace, String replacementTerm, SearchMode searchMode, List<String> propertiesToReplace, JCRSessionWrapper session) {
        if (logger.isDebugEnabled()) {
            logger.debug("replaceByUuid - Start");
        }
        //Node Replacement state result map
        Map<ReplaceStatus, List<String>> replaceResultMap = new HashMap<ReplaceStatus, List<String>>();
        replaceResultMap.put(ReplaceStatus.SUCCESS, new ArrayList<String>());
        replaceResultMap.put(ReplaceStatus.FAILED, new ArrayList<String>());

        //Getting the liste of replaceable nodes and properties
        Map<ReplaceStatus, List<SearchResult>> replaceableMap = checkPropertiesList(nodesUuid, propertiesToReplace, session);

        //Replacing the properties in all nodes
        for (SearchResult result : replaceableMap.get(ReplaceStatus.SUCCESS)) {
            //Calling the replace and putting the node uuid in the good case of replaceResultMap thanks to replaceNode return
            replaceResultMap.get(replaceNode(result, termToReplace, replacementTerm, searchMode, session)).add(result.getNodeUuid());
        }
        if (logger.isDebugEnabled()) {
            logger.debug("replaceByUuid - End");
        }
        return replaceResultMap;
    }

    @Override
    public Map<ReplaceStatus, List<String>> replaceBySearchResult(List<SearchResult> searchResults, String termToReplace, String replacementTerm, SearchMode searchMode, JCRSessionWrapper session) {
        Map<ReplaceStatus, List<String>> resultList = new HashMap<ReplaceStatus, List<String>>();

        resultList.put(ReplaceStatus.SUCCESS, new ArrayList<String>());
        resultList.put(ReplaceStatus.FAILED, new ArrayList<String>());
        if (logger.isDebugEnabled()) {
            logger.debug("replaceBySearchResult - Start");
        }
        for (SearchResult result : searchResults) {
            resultList.get(replaceNode(result, termToReplace, replacementTerm, searchMode, session)).add(result.getNodeUuid());
        }
        if (logger.isDebugEnabled()) {
            logger.debug("replaceBySearchResult - End");
        }
        return resultList;
    }

    @Override
    public Map<ReplaceStatus, List<String>> replaceBySearchResult(List<SearchResult> searchResults, String termToReplace, String replacementTerm, SearchMode searchMode, List<String> propertiesToReplace, JCRSessionWrapper session) {
        Map<ReplaceStatus, List<String>> resultList = new HashMap<ReplaceStatus, List<String>>();

        resultList.put(ReplaceStatus.SUCCESS, new ArrayList<String>());
        resultList.put(ReplaceStatus.FAILED, new ArrayList<String>());

        //Getting the list of uuids from searchResults
        SearchResult[] results = (SearchResult[]) searchResults.toArray();
        List<String> uuids = new ArrayList<String>();

        for (SearchResult uuidResult : results) {
            uuids.add(uuidResult.getNodeUuid());
        }
        //Checking the properties and getting only the searchResults needed
        searchResults = checkPropertiesList(uuids, propertiesToReplace, session).get(ReplaceStatus.SUCCESS);
        if (logger.isDebugEnabled()) {
            logger.debug("replaceBySearchResult - Start");
        }
        for (SearchResult result : searchResults) {
            resultList.get(replaceNode(result, termToReplace, replacementTerm, searchMode, session)).add(result.getNodeUuid());
        }
        if (logger.isDebugEnabled()) {
            logger.debug("replaceBySearchResult - End");
        }
        return resultList;
    }

    @Override
    public Map<ReplaceStatus, List<String>> replaceByNodes(List<JCRNodeWrapper> nodes, String termToReplace, String replacementTerm, SearchMode searchMode, JCRSessionWrapper session) {
        Map<ReplaceStatus, List<String>> resultMap = new HashMap<ReplaceStatus, List<String>>();

        resultMap.put(ReplaceStatus.SUCCESS, new ArrayList<String>());
        resultMap.put(ReplaceStatus.FAILED, new ArrayList<String>());
        List<String> uuidsList = new ArrayList<String>();
        try {
            for (JCRNodeWrapper node : nodes) {
                uuidsList.add(node.getIdentifier());
            }
            //Getting the liste of replaceable nodes and properties
            Map<ReplaceStatus, List<SearchResult>> replaceableMap = getReplaceableProperties(uuidsList, termToReplace, searchMode, session);
            if (logger.isDebugEnabled()) {
                logger.debug("replaceInNodes() - Start");
            }
            for (SearchResult result : replaceableMap.get(ReplaceStatus.SUCCESS)) {
                resultMap.get(replaceNode(result, termToReplace, replacementTerm, searchMode, session)).add(result.getNodeUuid());
            }
        } catch (RepositoryException e) {
            logger.error("replaceInNodes - Unable to get node Identifier");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("replaceInNodes() - End");
        }
        return resultMap;
    }

    @Override
    public Map<ReplaceStatus, List<String>> replaceByNodes(List<JCRNodeWrapper> nodes, String termToReplace, String replacementTerm, SearchMode searchMode, List<String> propertiesToReplace, JCRSessionWrapper session) {
        Map<ReplaceStatus, List<String>> resultMap = new HashMap<ReplaceStatus, List<String>>();

        resultMap.put(ReplaceStatus.SUCCESS, new ArrayList<String>());
        resultMap.put(ReplaceStatus.FAILED, new ArrayList<String>());
        List<String> uuidsList = new ArrayList<String>();
        try {
            for (JCRNodeWrapper node : nodes) {
                uuidsList.add(node.getIdentifier());
            }
            //Getting the liste of replaceable nodes and properties
            Map<ReplaceStatus, List<SearchResult>> replaceableMap = checkPropertiesList(uuidsList, propertiesToReplace, session);
            if (logger.isDebugEnabled()) {
                logger.debug("replaceInNodes() - Start");
            }
            for (SearchResult result : replaceableMap.get(ReplaceStatus.SUCCESS)) {
                resultMap.get(replaceNode(result, termToReplace, replacementTerm, searchMode, session)).add(result.getNodeUuid());
            }
        } catch (RepositoryException e) {
            logger.error("replaceInNodes - Unable to get node Identifier");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("replaceInNodes() - End");
        }
        return resultMap;
    }

    @Override
    public Map<ReplaceStatus, List<SearchResult>> getReplaceableProperties(List<String> nodesUuid, String termToReplace, SearchMode searchMode, JCRSessionWrapper session) {
        //Initializing the return Map
        List<SearchResult> replaceableNodes = new ArrayList<SearchResult>();
        List<SearchResult> nonReplaceableNodes = new ArrayList<SearchResult>();
        Map<ReplaceStatus, List<SearchResult>> resultMap = new HashMap<ReplaceStatus, List<SearchResult>>();
        resultMap.put(ReplaceStatus.SUCCESS, replaceableNodes);
        resultMap.put(ReplaceStatus.FAILED, nonReplaceableNodes);

        int nodeIndex = -1;
        List<Property> propertiesList;
        Map<String, String> propertiesMap = new HashMap<String, String>();
        Pattern p = null;
        Matcher m = null;
        if (searchMode.equals(SearchMode.REGEXP)) {
            //Building Regex Pattern
            p = Pattern.compile(termToReplace);
        }
        try {
            for (String nodeUuid : nodesUuid) {
                //Saving current node index
                nodeIndex = nodesUuid.indexOf(nodeUuid);

                //Getting the node and its properties
                JCRNodeWrapper nodeToTest = session.getNodeByIdentifier(nodeUuid);
                propertiesList = Lists.newArrayList(nodeToTest.getProperties());

                if (logger.isDebugEnabled()) {
                    logger.debug("getReplaceableProperties() - Node : " + nodeToTest.getName());
                    logger.debug("getReplaceableProperties() - Term To Replace : " + termToReplace);
                }
                for (Property nextProperty : propertiesList) {//browsing node properties
                    if (logger.isDebugEnabled()) {
                        logger.debug("replaceNode() - Scanning : " + nextProperty.getName());
                    }
                    if (!nextProperty.getDefinition().isProtected() && nextProperty.getType() == PropertyType.STRING && (ArrayUtils.isEmpty(nextProperty.getDefinition().getValueConstraints()))) {
                        //Search for the the term to replacing using the SearchMode
                        switch (searchMode) {
                            case EXACT_MATCH:
                                if (nextProperty.getString().contains(termToReplace)) {
                                    if (logger.isDebugEnabled()) {
                                        logger.debug("replaceNode() - case EXACT_MATCH - Found Match");
                                    }
                                    propertiesMap.put(nextProperty.getName(), nextProperty.getString());
                                }
                                break;
                            case REGEXP:
                                //Setting Regex Matcher
                                m = p.matcher(nextProperty.getString());
                                if (m.find()) {
                                    if (logger.isDebugEnabled()) {
                                        logger.debug("replaceNode() - case REGEXP - Property Name : " + nextProperty.getName());
                                        logger.debug("replaceNode() - case REGEXP - Found Match (" + termToReplace + " in " + nextProperty.getString() + ")");
                                    }
                                    propertiesMap.put(nextProperty.getName(), nextProperty.getString());
                                }
                                break;
                            case IGNORE_CASE:
                                if (StringUtils.containsIgnoreCase(nextProperty.getString(), termToReplace)) {
                                    if (logger.isDebugEnabled()) {
                                        logger.debug("replaceNode() - case IGNORE_CASE - Found Match");
                                    }
                                    propertiesMap.put(nextProperty.getName(), nextProperty.getString());
                                }
                                break;
                        }
                    }
                }
                if (!propertiesMap.isEmpty()) {
                    //Editable properties that contained the term to replace have been found result is replaceable
                    SearchResult result = new SearchResult(nodeUuid, new HashMap<String, String>());
                    result.setReplaceableProperties(new HashMap<String, String>(propertiesMap));
                    resultMap.get(ReplaceStatus.SUCCESS).add(result);
                } else {
                    resultMap.get(ReplaceStatus.FAILED).add(new SearchResult(nodeUuid, propertiesMap));
                }
                propertiesMap.clear();
            }
        } catch (RepositoryException e) {
            logger.error("Unable to test replaceable state for node : " + nodesUuid.get(nodeIndex), e);
        }
        return resultMap;
    }

    /**
     * This method replace the term to replace by the replacement term in the node properties defined by the result
     *
     * @param result          the searchResult defining the node and the properties to replace
     * @param termToReplace   the term to replace in the node properties
     * @param replacementTerm the string that will replace the term to replace
     * @param searchMode      the result used SearchMode (that will define the replace mode too)
     * @param session         the JCR session to execute the replacement
     * @return ReplaceStatus : SUCCESS if the replacement is done or FAILED in the other case
     */
    private ReplaceStatus replaceNode(SearchResult result, String termToReplace, String replacementTerm, SearchMode searchMode, JCRSessionWrapper session) {
        String propertyName = "";
        JCRNodeWrapper node;
        ReplaceStatus replaced = ReplaceStatus.FAILED;

        if (searchMode.equals(SearchMode.IGNORE_CASE)) {
            termToReplace = "(?i)" + termToReplace;
        }
        try {
            //Getting the node on which replace properties
            node = session.getNodeByUUID(result.getNodeUuid());
            if (logger.isDebugEnabled()) {
                logger.debug("replaceNode() - Node : " + node.getName());
                logger.debug("replaceNode() - Replacement Term : " + replacementTerm);
            }
            if (!node.isLocked()) {
                //Replace all properties in searchResult by the replacement Term
                for (Map.Entry<String, String> entry : result.getReplaceableProperties().entrySet()) {
                    propertyName = entry.getKey();
                    if (logger.isDebugEnabled()) {
                        logger.debug("replaceNode() - Replacing : " + propertyName);
                    }
                    String propertyValue = node.getProperty(propertyName).getString().replaceAll(termToReplace, replacementTerm);
                    node.setProperty(propertyName, propertyValue);
                    session.save();
                    if (propertyValue.contains(replacementTerm)) {
                        //The property did contain the term to replace and now contains the replacement term
                        replaced = ReplaceStatus.SUCCESS;
                    } else {
                        //If the property didn't contain the term to replace
                        // it doesn't contain now the replacement term
                        // the case is consider as FAILED
                        replaced = ReplaceStatus.FAILED;
                    }
                }
            }
            return replaced;
        } catch (RepositoryException e) {
            logger.error("Issue while replacing property " + propertyName + " in node " + result.getNodeUuid(), e);
            return ReplaceStatus.FAILED;
        }
    }

    /**
     * This method check the propertiesToReplace list and return a Map with the SearchResults containing replaceable nodes and properties
     *
     * @param nodesUuid           list of nodes uuid on which check properties
     * @param propertiesToReplace list of node properties to check
     * @param session             the JCR session with which check the nodes and properties constraints
     * @return Map<ReplaceStatus, List<SearchResult>> the map contains 2 entry SUCCESS, which contains the replaceable results and FAILED which contains the non replaceable results.
     */
    private Map<ReplaceStatus, List<SearchResult>> checkPropertiesList(List<String> nodesUuid, List<String> propertiesToReplace, JCRSessionWrapper session) {
        //Initializing return Map
        Map<ReplaceStatus, List<SearchResult>> checkedPropertiesLists = new HashMap<ReplaceStatus, List<SearchResult>>();
        checkedPropertiesLists.put(ReplaceStatus.SUCCESS, new ArrayList<SearchResult>());
        checkedPropertiesLists.put(ReplaceStatus.FAILED, new ArrayList<SearchResult>());

        //brosing the JCR nodes
        for (String uuid : nodesUuid) {
            try {
                //trying to get the JCR node
                JCRNodeWrapper currentNode = session.getNodeByIdentifier(uuid);
                //Node is accessible node properties have to be checked
                SearchResult result = new SearchResult(uuid, new HashMap<String, String>());
                for (String propertyName : propertiesToReplace) {
                    try {
                        JCRPropertyWrapper property = currentNode.getProperty(propertyName);
                        if (!property.getDefinition().isProtected() && property.getType() == PropertyType.STRING && (ArrayUtils.isEmpty(property.getDefinition().getValueConstraints()))) {
                            //The property is editable
                            result.addReplaceableProperty(propertyName, property.getString());
                        } else {
                            logger.warn("Property " + propertyName + " is not editable in the node " + uuid + " this property will be skipped for replacement");
                        }
                    } catch (RepositoryException e) {
                        logger.error("Issue accessing property " + propertyName + " in the node " + uuid);
                    }
                }
                if (result.getReplaceableProperties().isEmpty()) {//no properties are replaceable for this node
                    checkedPropertiesLists.get(ReplaceStatus.FAILED).add(result);
                } else {
                    checkedPropertiesLists.get(ReplaceStatus.SUCCESS).add(result);
                }
            } catch (RepositoryException e) {
                logger.error("Issue accessing property the node " + uuid, e);
                //If the node is not accessible a result entry is created in the FAILED results list
                checkedPropertiesLists.get(ReplaceStatus.FAILED).add(new SearchResult(uuid, new HashMap<String, String>()));
            }
        }
        return checkedPropertiesLists;
    }
}
