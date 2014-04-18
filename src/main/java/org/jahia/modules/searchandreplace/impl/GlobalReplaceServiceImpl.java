package org.jahia.modules.searchandreplace.impl;

import com.google.common.collect.Lists;
import org.jahia.modules.searchandreplace.GlobalReplaceService;
import org.jahia.modules.searchandreplace.SearchResult;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang.StringUtils;

import javax.jcr.Property;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by rizak on 15/04/14.
 */
public class GlobalReplaceServiceImpl implements GlobalReplaceService
{
    private static Logger logger = LoggerFactory.getLogger(GlobalReplaceServiceImpl.class);

    public Map<ReplaceStatus,List<String>> replaceByUuid(List<String> nodesUuid, String termToReplace, String replacementTerm, SearchMode searchMode, JCRSessionWrapper session)
    {
        logger.debug("GlobalReplaceServiceImpl - Start");

        //Node Replacement state result map
        Map<ReplaceStatus,List<String>> replaceResultMap = new HashMap<ReplaceStatus,List<String>>();
        replaceResultMap.put(ReplaceStatus.SUCCESS, new ArrayList<String>());
        replaceResultMap.put(ReplaceStatus.FAILED, new ArrayList<String>());

        //replaceResultMap failed replacement nodes uuid list
        List<String> failedUuidsList = new ArrayList<String>();

        //Getting the liste of replaceable nodes and properties
        Map<ReplaceStatus,List<SearchResult>> replaceableMap = getReplaceableProperties(nodesUuid,termToReplace,searchMode,session);

        //putting in the replaceResultMap[FAILED] all the nodes consider not replaceable
        for(SearchResult result : replaceableMap.get(ReplaceStatus.FAILED))
        {
            replaceResultMap.get(ReplaceStatus.FAILED).add(result.getNodeUuid());
        }

        //Replacing the properties in all nodes
        for(SearchResult result : replaceableMap.get(ReplaceStatus.SUCCESS))
        {
            //Calling the replace and putting the node uuid in the good case of replaceResultMap thanks to replaceNode return
            replaceResultMap.get(replaceNode(result,termToReplace,replacementTerm,searchMode,session)).add(result.getNodeUuid());
        }
        logger.debug("GlobalReplaceServiceImpl - End");

        return replaceResultMap;
    }

    @Override
    public Map<ReplaceStatus, List<String>> replaceBySearchResult(List<SearchResult> searchResults, String termToReplace, String replacementTerm, SearchMode searchMode, JCRSessionWrapper session) {
        Map<ReplaceStatus,List<String>> resultList = new HashMap<ReplaceStatus,List<String>>();

        resultList.put(ReplaceStatus.SUCCESS, new ArrayList<String>());
        resultList.put(ReplaceStatus.FAILED, new ArrayList<String>());

        logger.debug("GlobalReplaceServiceImpl - Start");
        for(SearchResult result : searchResults)
        {
            resultList.get(replaceNode(result,termToReplace,replacementTerm,searchMode,session)).add(result.getNodeUuid());
        }
        logger.debug("GlobalReplaceServiceImpl - End");

        return resultList;
    }

    @Override
    /**
     * Replace in nodes function
     */

    public Map<ReplaceStatus, List<String>> replaceByNodes(List<JCRNodeWrapper> nodes, String termToReplace, String replacementTerm, SearchMode searchMode, JCRSessionWrapper session)
    {
        Map<ReplaceStatus,List<String>> resultMap = new HashMap<ReplaceStatus,List<String>>();

        resultMap.put(ReplaceStatus.SUCCESS, new ArrayList<String>());
        resultMap.put(ReplaceStatus.FAILED, new ArrayList<String>());
        List<String> uuidsList = new ArrayList<String>();
        try
        {
            for(JCRNodeWrapper node : nodes)
            {
                uuidsList.add(node.getIdentifier());
            }
            //Getting the liste of replaceable nodes and properties
            Map<ReplaceStatus,List<SearchResult>> replaceableMap = getReplaceableProperties(uuidsList,termToReplace,searchMode,session);
            logger.debug("replaceInNodes() - Start");

            for(SearchResult result : replaceableMap.get(ReplaceStatus.SUCCESS))
            {
                resultMap.get(replaceNode(result, termToReplace,replacementTerm,searchMode, session)).add(result.getNodeUuid());
            }
        }
        catch(RepositoryException e)
        {
            logger.error("replaceInNodes - Unable to get node Identifier");
        }
        logger.debug("replaceInNodes() - End");
        return resultMap;
    }

    @Override
    public Map<ReplaceStatus,List<SearchResult>> getReplaceableProperties(List<String> nodesUuid, String termToReplace, SearchMode searchMode, JCRSessionWrapper session)
    {

        List<SearchResult> replaceableNodes = new ArrayList<SearchResult>();
        List<SearchResult> nonReplaceableNodes = new ArrayList<SearchResult>();
        Map<ReplaceStatus,List<SearchResult>> resultMap = new HashMap<ReplaceStatus, List<SearchResult>>();
        resultMap.put(ReplaceStatus.SUCCESS,replaceableNodes);
        resultMap.put(ReplaceStatus.FAILED,nonReplaceableNodes);
        int nodeIndex = -1;
        List<Property> propertiesList;
        Map<String, String> propertiesMap = new HashMap<String, String>();

        //Building Regex Pattern
        Pattern p = Pattern.compile(termToReplace);

        try
        {
            for(String nodeUuid : nodesUuid)
            {
                nodeIndex = nodesUuid.indexOf(nodeUuid);
                JCRNodeWrapper nodeToTest = session.getNodeByIdentifier(nodeUuid);
                propertiesList = Lists.newArrayList(nodeToTest.getProperties());
                logger.debug("getReplaceableProperties() - Node : " + nodeToTest.getName());
                logger.debug("getReplaceableProperties() - Term To Replace : " + termToReplace);

                for(Property nextProperty : propertiesList)
                {
                    logger.debug("replaceNode() - Scanning : " + nextProperty.getName());
                    if(!nextProperty.getDefinition().isProtected() && nextProperty.getType() == PropertyType.STRING && (nextProperty.getDefinition().getValueConstraints() == null || nextProperty.getDefinition().getValueConstraints().length==0))
                    {
                        //Setting Regex Matcher
                        Matcher m = p.matcher(nextProperty.getString());

                        //Apply the replacement
                        switch (searchMode)
                        {
                            case EXACT_MATCH :
                                if(nextProperty.getString().contains(termToReplace))
                                {
                                    logger.debug("replaceNode() - case EXACT_MATCH - Found Match");
                                    propertiesMap.put(nextProperty.getName(),nextProperty.getString());
                                }
                                break;
                            case REGEXP:
                                if(m.find())
                                {
                                    propertiesMap.put(nextProperty.getName(),nextProperty.getString());
                                    logger.debug("replaceNode() - case REGEXP - Property Name : " + nextProperty.getName());
                                    logger.debug("replaceNode() - case REGEXP - Found Match (" + termToReplace + " in " + nextProperty.getString() + ")");
                                }
                                break;
                            case IGNORE_CASE:
                                if(StringUtils.containsIgnoreCase(nextProperty.getString(),termToReplace))
                                {
                                    logger.debug("replaceNode() - case IGNORE_CASE - Found Match");
                                    propertiesMap.put(nextProperty.getName(),nextProperty.getString());
                                }
                                break;
                        }
                    }
                }
                if(propertiesMap.size()>0)
                {
                    SearchResult result = new SearchResult(nodeUuid, new HashMap<String, String>());
                    for(Entry<String,String> propertyEntry : propertiesMap.entrySet())
                    {
                        result.addReplaceableProperty(propertyEntry.getKey(),propertyEntry.getValue());
                    }
                    resultMap.get(ReplaceStatus.SUCCESS).add(result);
                }
                else
                {
                    resultMap.get(ReplaceStatus.FAILED).add(new SearchResult(nodeUuid,propertiesMap));
                }
                propertiesMap.clear();
            }
        }
        catch(RepositoryException e)
        {
            logger.error("Unable to test replaceable state for node : "+nodesUuid.get(nodeIndex),e);
        }
        return resultMap;
    }

    private ReplaceStatus replaceNode(SearchResult result, String termToReplace, String replacementTerm,SearchMode searchMode, JCRSessionWrapper session)
    {
        String propertyName = "";
        JCRNodeWrapper node;
        ReplaceStatus replaced=ReplaceStatus.FAILED;
        if(searchMode.equals(SearchMode.IGNORE_CASE))
        {
            termToReplace="(?i)"+termToReplace;
        }
        try
        {
            //Getting the node on which replace properties
            node = session.getNodeByUUID(result.getNodeUuid());
            logger.debug("replaceNode() - Node : " + node.getName());
            logger.debug("replaceNode() - Replacement Term : " + replacementTerm);

            if(!node.isLocked())
            {
                //Replace all properties in searchResult by the replacement Term
                for(Map.Entry<String, String> entry : result.getReplaceableProperties().entrySet())
                {
                    propertyName = entry.getKey();
                    logger.debug("replaceNode() - Replacing : " + propertyName);
                    String propertyValue ="";
                    propertyValue = node.getProperty(propertyName).getString().replaceAll(termToReplace,replacementTerm);

                    node.setProperty(propertyName,propertyValue);
                    session.save();
                    replaced = ReplaceStatus.SUCCESS;
                }
            }
            return replaced;
        }
        catch (RepositoryException e)
        {
            logger.error("Issue while replacing property "+propertyName+" in node "+result.getNodeUuid(), e);
            return ReplaceStatus.FAILED;
        }
    }
}
