//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-520 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2008.08.21 at 05:31:46 AM BST 
//


package org.drools.ws_ht.api.xsd;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.jvnet.jaxb2_commons.lang.Equals;
import org.jvnet.jaxb2_commons.lang.HashCode;
import org.jvnet.jaxb2_commons.lang.builder.JAXBEqualsBuilder;
import org.jvnet.jaxb2_commons.lang.builder.JAXBHashCodeBuilder;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="task" type="{http://www.drools.org/WS-HT/api}tTask"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "task"
})
@XmlRootElement(name = "getTaskInfoResponse")
@Entity(name = "org.drools.ws_ht.api.xsd.GetTaskInfoResponse")
@Table(name = "GETTASKINFORESPONSE")
@Inheritance(strategy = InheritanceType.JOINED)
public class GetTaskInfoResponse
    implements Equals, HashCode
{

    @XmlElement(required = true)
    protected org.drools.ws_ht.api.TTask task;
    @XmlAttribute(name = "Hjid")
    protected Long hjid;

    /**
     * Gets the value of the task property.
     * 
     * @return
     *     possible object is
     *     {@link org.drools.ws_ht.api.TTask }
     *     
     */
    @ManyToOne(targetEntity = org.drools.ws_ht.api.TTask.class, cascade = {
        CascadeType.ALL
    })
    @JoinColumn(name = "TASK_GETTASKINFORESPONSE_ID")
    public org.drools.ws_ht.api.TTask getTask() {
        return task;
    }

    /**
     * Sets the value of the task property.
     * 
     * @param value
     *     allowed object is
     *     {@link org.drools.ws_ht.api.TTask }
     *     
     */
    public void setTask(org.drools.ws_ht.api.TTask value) {
        this.task = value;
    }

    /**
     * Gets the value of the hjid property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    @Id
    @Column(name = "HJID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getHjid() {
        return hjid;
    }

    /**
     * Sets the value of the hjid property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setHjid(Long value) {
        this.hjid = value;
    }

    public void equals(Object object, EqualsBuilder equalsBuilder) {
        if (!(object instanceof GetTaskInfoResponse)) {
            equalsBuilder.appendSuper(false);
            return ;
        }
        if (this == object) {
            return ;
        }
        final GetTaskInfoResponse that = ((GetTaskInfoResponse) object);
        equalsBuilder.append(this.getTask(), that.getTask());
    }

    public boolean equals(Object object) {
        if (!(object instanceof GetTaskInfoResponse)) {
            return false;
        }
        if (this == object) {
            return true;
        }
        final EqualsBuilder equalsBuilder = new JAXBEqualsBuilder();
        equals(object, equalsBuilder);
        return equalsBuilder.isEquals();
    }

    public void hashCode(HashCodeBuilder hashCodeBuilder) {
        hashCodeBuilder.append(this.getTask());
    }

    public int hashCode() {
        final HashCodeBuilder hashCodeBuilder = new JAXBHashCodeBuilder();
        hashCode(hashCodeBuilder);
        return hashCodeBuilder.toHashCode();
    }

}
