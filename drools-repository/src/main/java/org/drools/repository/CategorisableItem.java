package org.drools.repository;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.Value;

/**
 * This contains logic for categorisable items
 * (not all versionably items are categorisable).
 * 
 * @author michael neale
 *
 */
public abstract class CategorisableItem extends VersionableItem {

    public CategorisableItem(RulesRepository rulesRepository,
                             Node node) {
        super( rulesRepository,
               node );
    }

    /**
     * Adds the specified tag to this object's node. Tags are stored as nodes in a tag area of
     * the repository. If the specified tag does not already have a corresponding node, a node is 
     * created for it.
     * 
     * Please note that this is mainly intended for rule related assets, not packages
     * (although it could be used). 
     *  
     * @param tag the tag to add to the rule. rules can have multiple tags
     * @throws RulesRepositoryException 
     */
    public void addCategory(String tag) throws RulesRepositoryException {
        try {
            //make sure this object's node is the head version
            checkIsUpdateable();                                       
            
            CategoryItem tagItem = this.rulesRepository.loadCategory(tag);
                                    
            //now set the tag property of the rule
            Property tagReferenceProperty;
            int i = 0;
            Value[] newTagValues = null;
            try {
                tagReferenceProperty = this.node.getProperty(TAG_PROPERTY_NAME);
                Value[] oldTagValues = tagReferenceProperty.getValues();
                
                //first, make sure this tag wasn't already there. while we're at it, lets copy the array
                newTagValues = new Value[oldTagValues.length + 1];                
                for(i=0; i<oldTagValues.length; i++) {
                    if(oldTagValues[i].getString().equals(tag)) {
                        log.info("tag '" + tag + "' already existed for rule node: " + this.node.getName());
                        return;
                    }
                    newTagValues[i] = oldTagValues[i];
                }
            }
            catch(PathNotFoundException e) {
                //the property doesn't exist yet, so create it in the finally block
                newTagValues = new Value[1];                 
            }
            finally {   
                if(newTagValues != null) {
                    newTagValues[i] = this.node.getSession().getValueFactory().createValue(tagItem.getNode());
                    this.node.checkout();
                    this.node.setProperty(TAG_PROPERTY_NAME, newTagValues);
                }
                else {
                    log.error("reached expected path of execution when adding tag '" + tag + "' to ruleNode: " + this.node.getName());
                }
            }
        }
        catch(Exception e) {
            log.error("Caught exception", e);
            throw new RulesRepositoryException(e);
        }
    }   
    
    /**
     * Gets a list of CategoryItem objects for this assets node.
     * 
     * @return a list of TagItem objects for each tag on the rule. If there are no tags, an empty list. 
     * @throws RulesRepositoryException
     */
    public List getCategories() throws RulesRepositoryException {
        try {                            
            Node ruleNode = getVersionContentNode();
            
            List returnList = new ArrayList();
            try {
                Property tagReferenceProperty = ruleNode.getProperty(TAG_PROPERTY_NAME);
                Value[] tagValues = tagReferenceProperty.getValues();                
                for(int i=0; i<tagValues.length; i++) {
                    Node tagNode = this.node.getSession().getNodeByUUID(tagValues[i].getString());
                    CategoryItem tagItem = new CategoryItem(this.rulesRepository, tagNode);
                    returnList.add(tagItem);
                }
            }
            catch(PathNotFoundException e) {
                //the property doesn't even exist yet, so just return nothing
            }
            return returnList;
        }
        catch(Exception e) {
            log.error("Caught exception", e);
            throw new RulesRepositoryException(e);
        }
    }  


}
