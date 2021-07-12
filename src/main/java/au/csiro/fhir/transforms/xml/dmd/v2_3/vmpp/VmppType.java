//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.09.03 at 08:29:45 AM AEST 
//


package au.csiro.fhir.transforms.xml.dmd.v2_3.vmpp;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for VmppType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="VmppType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;all&gt;
 *         &lt;element name="VPPID" type="{http://www.w3.org/2001/XMLSchema}integer"/&gt;
 *         &lt;element name="INVALID" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/&gt;
 *         &lt;element name="NM" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="ABBREVNM" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="VPID" type="{http://www.w3.org/2001/XMLSchema}integer"/&gt;
 *         &lt;element name="QTYVAL" type="{http://www.w3.org/2001/XMLSchema}float"/&gt;
 *         &lt;element name="QTY_UOMCD" type="{http://www.w3.org/2001/XMLSchema}integer"/&gt;
 *         &lt;element name="COMBPACKCD" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/&gt;
 *       &lt;/all&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VmppType", propOrder = {

})
public class VmppType {

    @XmlElement(name = "VPPID", required = true)
    protected BigInteger vppid;
    @XmlElement(name = "INVALID")
    protected BigInteger invalid;
    @XmlElement(name = "NM", required = true)
    protected String nm;
    @XmlElement(name = "ABBREVNM")
    protected String abbrevnm;
    @XmlElement(name = "VPID", required = true)
    protected BigInteger vpid;
    @XmlElement(name = "QTYVAL")
    protected float qtyval;
    @XmlElement(name = "QTY_UOMCD", required = true)
    protected BigInteger qtyuomcd;
    @XmlElement(name = "COMBPACKCD")
    protected BigInteger combpackcd;

    /**
     * Gets the value of the vppid property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getVPPID() {
        return vppid;
    }

    /**
     * Sets the value of the vppid property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setVPPID(BigInteger value) {
        this.vppid = value;
    }

    /**
     * Gets the value of the invalid property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getINVALID() {
        return invalid;
    }

    /**
     * Sets the value of the invalid property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setINVALID(BigInteger value) {
        this.invalid = value;
    }

    /**
     * Gets the value of the nm property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNM() {
        return nm;
    }

    /**
     * Sets the value of the nm property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNM(String value) {
        this.nm = value;
    }

    /**
     * Gets the value of the abbrevnm property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getABBREVNM() {
        return abbrevnm;
    }

    /**
     * Sets the value of the abbrevnm property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setABBREVNM(String value) {
        this.abbrevnm = value;
    }

    /**
     * Gets the value of the vpid property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getVPID() {
        return vpid;
    }

    /**
     * Sets the value of the vpid property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setVPID(BigInteger value) {
        this.vpid = value;
    }

    /**
     * Gets the value of the qtyval property.
     * 
     */
    public float getQTYVAL() {
        return qtyval;
    }

    /**
     * Sets the value of the qtyval property.
     * 
     */
    public void setQTYVAL(float value) {
        this.qtyval = value;
    }

    /**
     * Gets the value of the qtyuomcd property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getQTYUOMCD() {
        return qtyuomcd;
    }

    /**
     * Sets the value of the qtyuomcd property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setQTYUOMCD(BigInteger value) {
        this.qtyuomcd = value;
    }

    /**
     * Gets the value of the combpackcd property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCOMBPACKCD() {
        return combpackcd;
    }

    /**
     * Sets the value of the combpackcd property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCOMBPACKCD(BigInteger value) {
        this.combpackcd = value;
    }

}