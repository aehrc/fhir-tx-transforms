//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.09.03 at 08:29:45 AM AEST 
//


package au.csiro.fhir.transforms.xml.dmd.v2_3.vmp;

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
 *         &lt;element name="VMPS"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="VMP" type="{}VmpType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="VIRTUAL_PRODUCT_INGREDIENT"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="VPI" type="{}VpiType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="ONT_DRUG_FORM"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="ONT" type="{}OntDrugFormType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="DRUG_FORM"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="DFORM" type="{}DrugFormType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="DRUG_ROUTE"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="DROUTE" type="{}DrugRouteType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="CONTROL_DRUG_INFO"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="CONTROL_INFO" type="{}ControlInfoType" maxOccurs="unbounded" minOccurs="0"/&gt;
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
    "vmps",
    "virtualproductingredient",
    "ontdrugform",
    "drugform",
    "drugroute",
    "controldruginfo"
})
@XmlRootElement(name = "VIRTUAL_MED_PRODUCTS")
public class VIRTUALMEDPRODUCTS {

    @XmlElement(name = "VMPS", required = true)
    protected VIRTUALMEDPRODUCTS.VMPS vmps;
    @XmlElement(name = "VIRTUAL_PRODUCT_INGREDIENT", required = true)
    protected VIRTUALMEDPRODUCTS.VIRTUALPRODUCTINGREDIENT virtualproductingredient;
    @XmlElement(name = "ONT_DRUG_FORM", required = true)
    protected VIRTUALMEDPRODUCTS.ONTDRUGFORM ontdrugform;
    @XmlElement(name = "DRUG_FORM", required = true)
    protected VIRTUALMEDPRODUCTS.DRUGFORM drugform;
    @XmlElement(name = "DRUG_ROUTE", required = true)
    protected VIRTUALMEDPRODUCTS.DRUGROUTE drugroute;
    @XmlElement(name = "CONTROL_DRUG_INFO", required = true)
    protected VIRTUALMEDPRODUCTS.CONTROLDRUGINFO controldruginfo;

    /**
     * Gets the value of the vmps property.
     * 
     * @return
     *     possible object is
     *     {@link VIRTUALMEDPRODUCTS.VMPS }
     *     
     */
    public VIRTUALMEDPRODUCTS.VMPS getVMPS() {
        return vmps;
    }

    /**
     * Sets the value of the vmps property.
     * 
     * @param value
     *     allowed object is
     *     {@link VIRTUALMEDPRODUCTS.VMPS }
     *     
     */
    public void setVMPS(VIRTUALMEDPRODUCTS.VMPS value) {
        this.vmps = value;
    }

    /**
     * Gets the value of the virtualproductingredient property.
     * 
     * @return
     *     possible object is
     *     {@link VIRTUALMEDPRODUCTS.VIRTUALPRODUCTINGREDIENT }
     *     
     */
    public VIRTUALMEDPRODUCTS.VIRTUALPRODUCTINGREDIENT getVIRTUALPRODUCTINGREDIENT() {
        return virtualproductingredient;
    }

    /**
     * Sets the value of the virtualproductingredient property.
     * 
     * @param value
     *     allowed object is
     *     {@link VIRTUALMEDPRODUCTS.VIRTUALPRODUCTINGREDIENT }
     *     
     */
    public void setVIRTUALPRODUCTINGREDIENT(VIRTUALMEDPRODUCTS.VIRTUALPRODUCTINGREDIENT value) {
        this.virtualproductingredient = value;
    }

    /**
     * Gets the value of the ontdrugform property.
     * 
     * @return
     *     possible object is
     *     {@link VIRTUALMEDPRODUCTS.ONTDRUGFORM }
     *     
     */
    public VIRTUALMEDPRODUCTS.ONTDRUGFORM getONTDRUGFORM() {
        return ontdrugform;
    }

    /**
     * Sets the value of the ontdrugform property.
     * 
     * @param value
     *     allowed object is
     *     {@link VIRTUALMEDPRODUCTS.ONTDRUGFORM }
     *     
     */
    public void setONTDRUGFORM(VIRTUALMEDPRODUCTS.ONTDRUGFORM value) {
        this.ontdrugform = value;
    }

    /**
     * Gets the value of the drugform property.
     * 
     * @return
     *     possible object is
     *     {@link VIRTUALMEDPRODUCTS.DRUGFORM }
     *     
     */
    public VIRTUALMEDPRODUCTS.DRUGFORM getDRUGFORM() {
        return drugform;
    }

    /**
     * Sets the value of the drugform property.
     * 
     * @param value
     *     allowed object is
     *     {@link VIRTUALMEDPRODUCTS.DRUGFORM }
     *     
     */
    public void setDRUGFORM(VIRTUALMEDPRODUCTS.DRUGFORM value) {
        this.drugform = value;
    }

    /**
     * Gets the value of the drugroute property.
     * 
     * @return
     *     possible object is
     *     {@link VIRTUALMEDPRODUCTS.DRUGROUTE }
     *     
     */
    public VIRTUALMEDPRODUCTS.DRUGROUTE getDRUGROUTE() {
        return drugroute;
    }

    /**
     * Sets the value of the drugroute property.
     * 
     * @param value
     *     allowed object is
     *     {@link VIRTUALMEDPRODUCTS.DRUGROUTE }
     *     
     */
    public void setDRUGROUTE(VIRTUALMEDPRODUCTS.DRUGROUTE value) {
        this.drugroute = value;
    }

    /**
     * Gets the value of the controldruginfo property.
     * 
     * @return
     *     possible object is
     *     {@link VIRTUALMEDPRODUCTS.CONTROLDRUGINFO }
     *     
     */
    public VIRTUALMEDPRODUCTS.CONTROLDRUGINFO getCONTROLDRUGINFO() {
        return controldruginfo;
    }

    /**
     * Sets the value of the controldruginfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link VIRTUALMEDPRODUCTS.CONTROLDRUGINFO }
     *     
     */
    public void setCONTROLDRUGINFO(VIRTUALMEDPRODUCTS.CONTROLDRUGINFO value) {
        this.controldruginfo = value;
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
     *         &lt;element name="CONTROL_INFO" type="{}ControlInfoType" maxOccurs="unbounded" minOccurs="0"/&gt;
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
        "controlinfo"
    })
    public static class CONTROLDRUGINFO {

        @XmlElement(name = "CONTROL_INFO")
        protected List<ControlInfoType> controlinfo;

        /**
         * Gets the value of the controlinfo property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the controlinfo property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getCONTROLINFO().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ControlInfoType }
         * 
         * 
         */
        public List<ControlInfoType> getCONTROLINFO() {
            if (controlinfo == null) {
                controlinfo = new ArrayList<ControlInfoType>();
            }
            return this.controlinfo;
        }

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
     *         &lt;element name="DFORM" type="{}DrugFormType" maxOccurs="unbounded" minOccurs="0"/&gt;
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
        "dform"
    })
    public static class DRUGFORM {

        @XmlElement(name = "DFORM")
        protected List<DrugFormType> dform;

        /**
         * Gets the value of the dform property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the dform property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getDFORM().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link DrugFormType }
         * 
         * 
         */
        public List<DrugFormType> getDFORM() {
            if (dform == null) {
                dform = new ArrayList<DrugFormType>();
            }
            return this.dform;
        }

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
     *         &lt;element name="DROUTE" type="{}DrugRouteType" maxOccurs="unbounded" minOccurs="0"/&gt;
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
        "droute"
    })
    public static class DRUGROUTE {

        @XmlElement(name = "DROUTE")
        protected List<DrugRouteType> droute;

        /**
         * Gets the value of the droute property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the droute property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getDROUTE().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link DrugRouteType }
         * 
         * 
         */
        public List<DrugRouteType> getDROUTE() {
            if (droute == null) {
                droute = new ArrayList<DrugRouteType>();
            }
            return this.droute;
        }

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
     *         &lt;element name="ONT" type="{}OntDrugFormType" maxOccurs="unbounded" minOccurs="0"/&gt;
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
        "ont"
    })
    public static class ONTDRUGFORM {

        @XmlElement(name = "ONT")
        protected List<OntDrugFormType> ont;

        /**
         * Gets the value of the ont property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the ont property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getONT().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link OntDrugFormType }
         * 
         * 
         */
        public List<OntDrugFormType> getONT() {
            if (ont == null) {
                ont = new ArrayList<OntDrugFormType>();
            }
            return this.ont;
        }

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
     *         &lt;element name="VPI" type="{}VpiType" maxOccurs="unbounded" minOccurs="0"/&gt;
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
        "vpi"
    })
    public static class VIRTUALPRODUCTINGREDIENT {

        @XmlElement(name = "VPI")
        protected List<VpiType> vpi;

        /**
         * Gets the value of the vpi property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the vpi property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getVPI().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link VpiType }
         * 
         * 
         */
        public List<VpiType> getVPI() {
            if (vpi == null) {
                vpi = new ArrayList<VpiType>();
            }
            return this.vpi;
        }

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
     *         &lt;element name="VMP" type="{}VmpType" maxOccurs="unbounded" minOccurs="0"/&gt;
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
        "vmp"
    })
    public static class VMPS {

        @XmlElement(name = "VMP")
        protected List<VmpType> vmp;

        /**
         * Gets the value of the vmp property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the vmp property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getVMP().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link VmpType }
         * 
         * 
         */
        public List<VmpType> getVMP() {
            if (vmp == null) {
                vmp = new ArrayList<VmpType>();
            }
            return this.vmp;
        }

    }

}
