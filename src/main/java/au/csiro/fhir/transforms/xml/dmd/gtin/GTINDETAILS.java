//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.2 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2021.09.13 at 06:08:06 PM AEST 
//


package au.csiro.fhir.transforms.xml.dmd.gtin;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="AMPPS"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="AMPP" type="{}AMPPType" maxOccurs="unbounded"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "ampps"
})
@XmlRootElement(name = "GTIN_DETAILS")
public class GTINDETAILS {

    @XmlElement(name = "AMPPS", required = true)
    protected GTINDETAILS.AMPPS ampps;

    /**
     * Gets the value of the ampps property.
     * 
     * @return
     *     possible object is
     *     {@link GTINDETAILS.AMPPS }
     *     
     */
    public GTINDETAILS.AMPPS getAMPPS() {
        return ampps;
    }

    /**
     * Sets the value of the ampps property.
     * 
     * @param value
     *     allowed object is
     *     {@link GTINDETAILS.AMPPS }
     *     
     */
    public void setAMPPS(GTINDETAILS.AMPPS value) {
        this.ampps = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;element name="AMPP" type="{}AMPPType" maxOccurs="unbounded"/&gt;
     *       &lt;/sequence&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "ampp"
    })
    public static class AMPPS {

        @XmlElement(name = "AMPP", required = true)
        protected List<AMPPType> ampp;

        /**
         * Gets the value of the ampp property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the ampp property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getAMPP().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link AMPPType }
         * 
         * 
         */
        public List<AMPPType> getAMPP() {
            if (ampp == null) {
                ampp = new ArrayList<AMPPType>();
            }
            return this.ampp;
        }

    }

}
