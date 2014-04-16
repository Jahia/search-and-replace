package org.jahia.modules.searchandreplace.impl;

import com.google.common.collect.Lists;
import org.jahia.modules.searchandreplace.GlobalReplaceService;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRPropertyWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang.StringUtils;

import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        Map<ReplaceStatus,List<String>> resultList = new HashMap<ReplaceStatus,List<String>>();

        resultList.put(ReplaceStatus.SUCCESS, new ArrayList<String>());
        resultList.put(ReplaceStatus.FAILED, new ArrayList<String>());

        List<String> successList = new ArrayList<String>();
        logger.debug("GlobalReplaceServiceImpl - Start");
        for(String uuid : nodesUuid)
        {
            resultList.get(replaceNode(uuid,termToReplace,replacementTerm,searchMode,session)).add(uuid);
        }
        logger.debug("GlobalReplaceServiceImpl - End");

        return resultList;
    }

    @Override
    public Map<ReplaceStatus, List<String>> replaceInNodes(List<JCRNodeWrapper> nodes, String termToReplace, String replacementTerm, SearchMode searchMode, JCRSessionWrapper session)
    {
        Map<ReplaceStatus,List<String>> resultList = new HashMap<ReplaceStatus,List<String>>();

        resultList.put(ReplaceStatus.SUCCESS, new ArrayList<String>());
        resultList.put(ReplaceStatus.FAILED, new ArrayList<String>());

        logger.debug("replaceInNodes() - Start");
        for(JCRNodeWrapper node : nodes)
        {
            try
            {
                resultList.get(replaceNode(node.getIdentifier(),termToReplace,replacementTerm,searchMode,session)).add(node.getIdentifier());
            }
            catch(RepositoryException e)
            {
                logger.error("replaceInNodes - Unable to get node Identifier");
            }
        }
        logger.debug("replaceInNodes() - End");

        return resultList;
    }

    private ReplaceStatus replaceNode(String nodeID, String termToReplace, String replacementTerm, SearchMode searchMode, JCRSessionWrapper session)
    {

        List<Property> propertiesList;
        try
        {
            //Getting the node on which replace properties
            JCRNodeWrapper node = session.getNodeByUUID(nodeID);

            ReplaceStatus replaced=ReplaceStatus.FAILED;

            propertiesList = Lists.newArrayList(node.getProperties());

            logger.debug("replaceNode() - Node : " + node.getName());
            logger.debug("replaceNode() - Term To Replace : " + termToReplace);
            logger.debug("replaceNode() - Replacement Term : " + replacementTerm);

            if(!node.isLocked())
            {
                for(Property nextProperty : propertiesList)
                {

                    logger.debug("replaceNode() - Scanning : " + nextProperty.getName());
                    if(!nextProperty.getDefinition().isProtected() && nextProperty.getType() == PropertyType.STRING)
                    {
                        //Building Regex Matcher
                        Pattern p = Pattern.compile("\\b"+termToReplace+"\\b");
                        Matcher m = p.matcher(nextProperty.getString());

                        //Apply the replacement
                        switch (searchMode)
                        {
                            case EXACT_MATCH :
                                if(nextProperty.getString().contains(termToReplace))
                                {
                                    logger.debug("replaceNode() - case EXACT_MATCH - Found Match");
                                    nextProperty.setValue(nextProperty.getString().replaceAll(termToReplace, replacementTerm));
                                    session.save();
                                    replaced=ReplaceStatus.SUCCESS;
                                }
                                break;
                            case REGEXP:
                                if(m.find())
                                {
                                    logger.debug("replaceNode() - case REGEXP - Property Name : " + nextProperty.getName());
                                    logger.debug("replaceNode() - case REGEXP - Found Match (" + termToReplace + " in " + nextProperty.getString() + ")");
                                    nextProperty.setValue(nextProperty.getString().replaceAll(termToReplace, replacementTerm));
                                    session.save();
                                    replaced=ReplaceStatus.SUCCESS;
                                }
                                break;
                            case IGNORE_CASE:
                                if(StringUtils.containsIgnoreCase(nextProperty.getString(),termToReplace))
                                {
                                    logger.debug("replaceNode() - case IGNORE_CASE - Found Match");
                                    nextProperty.setValue(nextProperty.getString().replaceAll(termToReplace, replacementTerm));
                                    session.save();
                                    replaced=ReplaceStatus.SUCCESS;
                                }
                                break;
                        }
                    }
                }
            }
            return replaced;
        }
        catch (RepositoryException e)
        {
            logger.error(e.getMessage(), e);
            return ReplaceStatus.FAILED;
        }
    }
}
