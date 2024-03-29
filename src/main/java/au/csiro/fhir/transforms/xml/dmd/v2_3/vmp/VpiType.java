//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.09.03 at 08:29:45 AM AEST 
//


package au.csiro.fhir.transforms.xml.dmd.v2_3.vmp;

import java.math.BigInteger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for VpiType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="VpiType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;all&gt;
 *         &lt;element name="VPID" type="{http://www.w3.org/2001/XMLSchema}integer"/&gt;
 *         &lt;element name="ISID" type="{http://www.w3.org/2001/XMLSchema}integer"/&gt;
 *         &lt;element name="BASIS_STRNTCD" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/&gt;
 *         &lt;element name="BS_SUBID" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/&gt;
 *         &lt;element name="STRNT_NMRTR_VAL" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/&gt;
 *         &lt;element name="STRNT_NMRTR_UOMCD" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/&gt;
 *         &lt;element name="STRNT_DNMTR_VAL" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/&gt;
 *         &lt;element name="STRNT_DNMTR_UOMCD" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/&gt;
 *       &lt;/all&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VpiType", propOrder = {

})
public class VpiType {

    @XmlElement(name = "VPID", required = true)
    protected BigInteger vpid;
    @XmlElement(name = "ISID", required = true)
    protected BigInteger isid;
    @XmlElement(name = "BASIS_STRNTCD")
    protected BigInteger basisstrntcd;
    @XmlElement(name = "BS_SUBID")
    protected BigInteger bssubid;
    @XmlElement(name = "STRNT_NMRTR_VAL")
    protected Float strntnmrtrval;
    @XmlElement(name = "STRNT_NMRTR_UOMCD")
    protected BigInteger strntnmrtruomcd;
    @XmlElement(name = "STRNT_DNMTR_VAL")
    protected Float strntdnmtrval;
    @XmlElement(name = "STRNT_DNMTR_UOMCD")
    protected BigInteger strntdnmtruomcd;

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
     * Gets the value of the isid property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getISID() {
        return isid;
    }

    /**
     * Sets the value of the isid property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setISID(BigInteger value) {
        this.isid = value;
    }

    /**
     * Gets the value of the basisstrntcd property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getBASISSTRNTCD() {
        return basisstrntcd;
    }

    /**
     * Sets the value of the basisstrntcd property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setBASISSTRNTCD(BigInteger value) {
        this.basisstrntcd = value;
    }

    /**
     * Gets the value of the bssubid property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getBSSUBID() {
        return bssubid;
    }

    /**
     * Sets the value of the bssubid property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setBSSUBID(BigInteger value) {
        this.bssubid = value;
    }

    /**
     * Gets the value of the strntnmrtrval property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getSTRNTNMRTRVAL() {
        return strntnmrtrval;
    }

    /**
     * Sets the value of the strntnmrtrval property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setSTRNTNMRTRVAL(Float value) {
        this.strntnmrtrval = value;
    }

    /**
     * Gets the value of the strntnmrtruomcd property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSTRNTNMRTRUOMCD() {
        return strntnmrtruomcd;
    }

    /**
     * Sets the value of the strntnmrtruomcd property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSTRNTNMRTRUOMCD(BigInteger value) {
        this.strntnmrtruomcd = value;
    }

    /**
     * Gets the value of the strntdnmtrval property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getSTRNTDNMTRVAL() {
        return strntdnmtrval;
    }

    /**
     * Sets the value of the strntdnmtrval property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setSTRNTDNMTRVAL(Float value) {
        this.strntdnmtrval = value;
    }

    /**
     * Gets the value of the strntdnmtruomcd property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSTRNTDNMTRUOMCD() {
        return strntdnmtruomcd;
    }

    /**
     * Sets the value of the strntdnmtruomcd property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSTRNTDNMTRUOMCD(BigInteger value) {
        this.strntdnmtruomcd = value;
    }

}
