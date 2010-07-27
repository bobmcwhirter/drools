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

package org.drools.bpmn2.legacy.beta1.di;

import org.drools.xml.ExtensibleXmlParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class EventShapeHandler extends ShapeHandler {

    protected String getNodeRefName() {
        return "eventRef";
    }

    public Object start(final String uri, final String localName,
            final Attributes attrs, final ExtensibleXmlParser parser)
            throws SAXException {
        NodeInfo nodeInfo = (NodeInfo) super.start(
            uri, localName, attrs, parser);
        int offsetX = (int) ((nodeInfo.getWidth() - 48) / 2);
        nodeInfo.setX(nodeInfo.getX() + offsetX);
        nodeInfo.setWidth(48);
        int offsetY = (int) ((nodeInfo.getHeight() - 48) / 2);
        nodeInfo.setY(nodeInfo.getY() + offsetY);
        nodeInfo.setHeight(48);
        return nodeInfo;
    }
    
}
