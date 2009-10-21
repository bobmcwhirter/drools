package org.drools.bpmn2.core;

public class ItemDefinition {
    
    private String id;
    private String structureRef;
    
    public ItemDefinition(String id) {
        this.id = id;
    }
    
    public String getId() {
        return id;
    }
    
    public String getStructureRef() {
        return structureRef;
    }
    
    public void setStructureRef(String structureRef) {
        this.structureRef = structureRef;
    }
    
}
