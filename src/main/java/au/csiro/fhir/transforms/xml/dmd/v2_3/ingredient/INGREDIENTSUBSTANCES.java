//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.09.03 at 08:29:45 AM AEST 
//


package au.csiro.fhir.transforms.xml.dmd.v2_3.ingredient;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


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
 *         &lt;element name="ING" maxOccurs="unbounded"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="ISID" type="{http://www.w3.org/2001/XMLSchema}integer"/&gt;
 *                   &lt;element name="ISIDDT" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *                   &lt;element name="ISIDPREV" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/&gt;
 *                   &lt;element name="INVALID" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/&gt;
 *                   &lt;element name="NM" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
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
    "ing"
})
@XmlRootElement(name = "INGREDIENT_SUBSTANCES")
public class INGREDIENTSUBSTANCES {

    @XmlElement(name = "ING", required = true)
    protected List<INGREDIENTSUBSTANCES.ING> ing;

    /**
     * Gets the value of the ing property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ing property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getING().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link INGREDIENTSUBSTANCES.ING }
     * 
     * 
     */
    public List<INGREDIENTSUBSTANCES.ING> getING() {
        if (ing == null) {
            ing = new ArrayList<INGREDIENTSUBSTANCES.ING>();
        }
        return this.ing;
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
     *         &lt;element name="ISID" type="{http://www.w3.org/2001/XMLSchema}integer"/&gt;
     *         &lt;element name="ISIDDT" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
     *         &lt;element name="ISIDPREV" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/&gt;
     *         &lt;element name="INVALID" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/&gt;
     *         &lt;element name="NM" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
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
        "isid",
        "isiddt",
        "isidprev",
        "invalid",
        "nm"
    })
    public static class ING {

        @XmlElement(name = "ISID", required = true)
        protected BigInteger isid;
        @XmlElement(name = "ISIDDT")
        @XmlSchemaType(name = "date")
        protected XMLGregorianCalendar isiddt;
        @XmlElement(name = "ISIDPREV")
        protected BigInteger isidprev;
        @XmlElement(name = "INVALID")
        protected BigInteger invalid;
        @XmlElement(name = "NM", required = true)
        protected String nm;

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
         * Gets the value of the isiddt property.
         * 
         * @return
         *     possible object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public XMLGregorianCalendar getISIDDT() {
            return isiddt;
        }

        /**
         * Sets the value of the isiddt property.
         * 
         * @param value
         *     allowed object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public void setISIDDT(XMLGregorianCalendar value) {
            this.isiddt = value;
        }

        /**
         * Gets the value of the isidprev property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getISIDPREV() {
            return isidprev;
        }

        /**
         * Sets the value of the isidprev property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setISIDPREV(BigInteger value) {
            this.isidprev = value;
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

    }

}
