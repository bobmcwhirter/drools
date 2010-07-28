/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools;


import java.io.Serializable;
import java.util.Date;

/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class Cheese
    implements
    Serializable {

    public static final String STILTON = "stilton";

    /**
     *
     */
    private static final long serialVersionUID = 400L;
    private String            type;
    private int               price;
    private int               oldPrice;
    private Date              usedBy;

    public Cheese() {

    }

    public Cheese(final String type) {
        super();
        this.type = type;
        this.price = 0;
    }

    public Cheese(final String type,
                  final int price) {
        super();
        this.type = type;
        this.price = price;
    }

    public Cheese(final String type,
                  final int price,
                  final int oldPrice ) {
        super();
        this.type = type;
        this.price = price;
        this.oldPrice = oldPrice;
    }

    public int getPrice() {
        return this.price;
    }

    public String getType() {
        return this.type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public void setPrice(final int price) {
        this.price = price;
    }

    public String toString() {
        return "Cheese( type='" + this.type + "', price=" + this.price + " )";
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + price;
        result = PRIME * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        final Cheese other = (Cheese) obj;
        if ( price != other.price ) return false;
        if ( type == null ) {
            if ( other.type != null ) return false;
        } else if ( !type.equals( other.type ) ) return false;
        return true;
    }

    public int getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(int oldPrice) {
        this.oldPrice = oldPrice;
    }

    public Date getUsedBy() {
        return usedBy;
    }

    public void setUsedBy(Date usedBy) {
        this.usedBy = usedBy;
    }



}