package org.drools.task.utils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.print.attribute.HashAttributeSet;

public class CollectionUtils {
    public static boolean equals(List list1, List list2) {
        if ( list1 == null && list2 == null ) {
            return true;
        }
        
        if ( list1.size() != list2.size() ) {
            return false;
        } 
        
        if ( list1.isEmpty() && list2.isEmpty() ) {
            return true;
        }
        
       
        for ( Object item1 : list1) {
            boolean exists = false;
            for ( Object item2 : list2 ) {
                if ( item1.equals( item2 )) {
                    exists = true;
                    break;
                }
            }  
            if ( !exists ) {
                return false;
            }
        }
        
        return true;
    }
    
    public static int hashCode(List list) {
        if ( list == null ) {
            return 0;
        }
        
        final int prime = 31;
        int result = 1;        
        for ( Iterator it = list.iterator(); it.hasNext(); ) {
            result = prime * result + it.next().hashCode();
        }        
        return result;
    }
}
