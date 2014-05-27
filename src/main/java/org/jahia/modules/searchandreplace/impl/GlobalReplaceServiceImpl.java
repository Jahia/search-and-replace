package org.jahia.modules.searchandreplace.impl;

import com.google.common.collect.Lists;
import net.htmlparser.jericho.*;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.jahia.modules.searchandreplace.GlobalReplaceService;
import org.jahia.modules.searchandreplace.SearchResult;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRPropertyWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.JCRValueWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Property;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class implements the service GlobalReplaceService
 * Created by Rahmed on 15/04/14.
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
        Map<String, List<String>> propertiesMap = new HashMap<String, List<String>>();
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
                    ArrayList<String> propertyValues = new ArrayList<String>();
                    //getting value as String
                    if (!nextProperty.getDefinition().isProtected() && nextProperty.getType() == PropertyType.STRING && (ArrayUtils.isEmpty(nextProperty.getDefinition().getValueConstraints()))) {
                        if (nextProperty.isMultiple()) {
                            for (Value value : nextProperty.getValues()) {
                                propertyValues.add(value.getString());
                            }
                        } else {
                            propertyValues.add(nextProperty.getString());
                        }
                        boolean containsTerm = false;
                        //Search for the the term to replace using the SearchMode
                        switch (searchMode) {
                            case EXACT_MATCH:
                                for (String stringValue : propertyValues) {
                                    if (new TextExtractor(new Source(stringValue)).toString().contains(termToReplace)) {
                                        if (logger.isDebugEnabled()) {
                                            logger.debug("replaceNode() - case EXACT_MATCH - Found Match");
                                        }
                                        propertiesMap.put(nextProperty.getName(), propertyValues);
                                    }
                                }
                                break;
                            case REGEXP:
                                //Setting Regex Matcher
                                for (String stringValue : propertyValues) {
                                    m = p.matcher(stringValue);
                                    if (m.find()) {
                                        if (logger.isDebugEnabled()) {
                                            logger.debug("replaceNode() - case REGEXP - Property Name : " + nextProperty.getName());
                                            logger.debug("replaceNode() - case REGEXP - Found Match (" + termToReplace + " in " + nextProperty.getString() + ")");
                                        }
                                        containsTerm = true;
                                    }
                                }
                                if (containsTerm) {
                                    propertiesMap.put(nextProperty.getName(), propertyValues);
                                }
                                break;
                            case IGNORE_CASE:
                                for (String stringValue : propertyValues) {
                                    if (StringUtils.containsIgnoreCase(new TextExtractor(new Source(stringValue)).toString(), termToReplace)) {
                                        containsTerm = true;
                                    }
                                }
                                if (containsTerm) {
                                    if (logger.isDebugEnabled()) {
                                        logger.debug("replaceNode() - case IGNORE_CASE - Found Match");
                                    }
                                    propertiesMap.put(nextProperty.getName(), propertyValues);
                                }
                                break;
                        }
                    }
                }
                if (!propertiesMap.isEmpty()) {
                    //Editable properties that contained the term to replace have been found result is replaceable
                    SearchResult result = new SearchResult(nodeUuid, new HashMap<String, List<String>>());
                    result.setReplaceableProperties(new HashMap<String, List<String>>(propertiesMap));
                    resultMap.get(ReplaceStatus.SUCCESS).add(result);
                } else {
                    resultMap.get(ReplaceStatus.FAILED).add(new SearchResult(nodeUuid, propertiesMap));
                }
                propertiesMap.clear();
            }
        } catch (RepositoryException e) {
            logger.error("Unable to test replaceable state for node : " + nodesUuid.get(nodeIndex), e);
            resultMap.get(ReplaceStatus.FAILED).add(new SearchResult(nodesUuid.get(nodeIndex), new HashMap<String, List<String>>()));
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
                for (Map.Entry<String, List<String>> entry : result.getReplaceableProperties().entrySet()) {
                    propertyName = entry.getKey();
                    if (logger.isDebugEnabled()) {
                        logger.debug("replaceNode() - Replacing : " + propertyName);
                    }

                    boolean propertyValueReplaced = false;
                    if (!node.getProperty(propertyName).isMultiple()) {
                        //Not multiple values properties replacing one string
                        String propertyValue = "";
                        if (new Source(node.getProperty(propertyName).getString()).getAllStartTags().size() > 0) {
                            propertyValue = replaceWithRecursion(new Source(node.getProperty(propertyName).getString()), termToReplace, replacementTerm, searchMode);
                        }else {
                            propertyValue = node.getProperty(propertyName).getString().replaceAll(termToReplace, replacementTerm);
                        }
                        node.setProperty(propertyName, propertyValue);
                        //Checking that something has been replace in the property
                        propertyValueReplaced = propertyValue.contains(replacementTerm);
                    } else {//Multiple values property
                        //preparing the replacedValues table
                        String[] replacedValues = new String[node.getProperty(propertyName).getValues().length];
                        //Getting all the values as a table
                        JCRValueWrapper[] valuesTable = node.getProperty(propertyName).getValues();
                        for (int valueIndex = 0; valueIndex < replacedValues.length; valueIndex++) {//Browsing values one by one to replace the strings
                            JCRValueWrapper currentValue = valuesTable[valueIndex];
                            if (new Source(currentValue.getString()).getAllStartTags().size() > 0){
                                replacedValues[valueIndex] = replaceWithRecursion(new Source(currentValue.getString()), termToReplace, replacementTerm, searchMode);
                            } else {
                                replacedValues[valueIndex] = currentValue.getString().replaceAll(termToReplace, replacementTerm);
                            }
                            //Checking that something has been replace in the property values
                            if (!propertyValueReplaced) {
                                propertyValueReplaced = replacedValues[valueIndex].contains(replacementTerm);
                            }
                        }
                        //Resetting the property with the replaced values
                        node.setProperty(propertyName, replacedValues);
                    }
                    session.save();
                    if (propertyValueReplaced) {
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
     * This method perform the replace in the source code
     *
     * @param source          the html source who will be modified
     * @param termToReplace   the term to replace in the node properties
     * @param replacementTerm the string that will replace the term to replace
     * @param searchMode      the result used SearchMode (that will define the replace mode too)
     * @return String :       the modified html
     */
    private static String replaceWithRecursion(Source source, String termToReplace, String replacementTerm, SearchMode searchMode) {
        OutputDocument document = new OutputDocument(source);
        String orig = document.toString();
        List<Element> childElements = source.getChildElements();
        int begin = 0;
        for (Element childElement : childElements) {
            document.replace(begin, childElement.getBegin(), orig.substring(begin, childElement.getBegin()).replaceAll(termToReplace, replacementTerm));
            String s = replaceWithRecursion(new Source(childElement.getContent().toString()), termToReplace, replacementTerm, searchMode);
            Source source1 = new Source(s);
            if(source1.getAllElements().isEmpty()) {
                if(searchMode.equals(SearchMode.IGNORE_CASE)){
                    if (StringUtils.containsIgnoreCase(s, termToReplace) && !StringUtils.containsIgnoreCase(s, replacementTerm)) {
                        s = s.replaceAll(termToReplace, replacementTerm);
                    }
                } else {
                    if (s.contains(termToReplace) && !s.contains(replacementTerm)) {
                        s = s.replaceAll(termToReplace, replacementTerm);
                    }
                }
            }
            else {
                List<Element> childElements1 = source1.getChildElements();
                OutputDocument document1 = new OutputDocument(source1);
                String original = document1.toString();
                int start = 0;
                for (Element element : childElements1) {
                    if(searchMode.equals(SearchMode.IGNORE_CASE)){
                        if (StringUtils.containsIgnoreCase(s, termToReplace) && !StringUtils.containsIgnoreCase(s, replacementTerm)) {
                            document1.replace(start, element.getBegin(), original.substring(start, element.getBegin()).replaceAll(termToReplace, replacementTerm));
                        }
                    } else {
                        if (original.substring(start, element.getBegin()).contains(termToReplace) && !original.substring(start, element.getBegin()).contains(replacementTerm)) {
                            document1.replace(start, element.getBegin(), original.substring(start, element.getBegin()).replaceAll(termToReplace, replacementTerm));
                        }
                    }
                    start=element.getEnd();
                }
                if(searchMode.equals(SearchMode.IGNORE_CASE)){
                    if(StringUtils.containsIgnoreCase(original.substring(start), termToReplace) && !StringUtils.containsIgnoreCase(original.substring(start), replacementTerm)) {
                        document1.replace(start, original.length(), original.substring(start).replaceAll(termToReplace, replacementTerm));
                    }
                } else {
                    if(original.substring(start).contains(termToReplace) && !original.substring(start).contains(replacementTerm)) {
                        document1.replace(start, original.length(), original.substring(start).replaceAll(termToReplace, replacementTerm));
                    }
                }
                s = document1.toString();
            }
            begin=childElement.getEnd();
            document.replace(childElement.getContent(), s);
        }
        document.replace(begin, orig.length(), orig.substring(begin).replaceAll(termToReplace, replacementTerm));
        return document.toString();
    }

    /**
     * This method check the propertiesToReplace list and return a Map with the SearchResults containing editable nodes and properties
     * a property is consider editable if its constraints list is empty, its type is String and the property is not protected
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
                SearchResult result = new SearchResult(uuid, new HashMap<String, List<String>>());
                for (String propertyName : propertiesToReplace) {
                    try {
                        JCRPropertyWrapper property = currentNode.getProperty(propertyName);
                        if (!property.getDefinition().isProtected() && property.getType() == PropertyType.STRING && (ArrayUtils.isEmpty(property.getDefinition().getValueConstraints()))) {
                            List<String> stringValues = new ArrayList<String>();
                            if (property.isMultiple()) {
                                for (Value value : property.getValues()) {
                                    stringValues.add(value.getString());
                                }
                            } else {
                                stringValues.add(property.getString());
                            }
                            //The property is editable
                            result.addReplaceableProperty(propertyName, stringValues);
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
                checkedPropertiesLists.get(ReplaceStatus.FAILED).add(new SearchResult(uuid, new HashMap<String, List<String>>()));
            }
        }
        return checkedPropertiesLists;
    }
}
