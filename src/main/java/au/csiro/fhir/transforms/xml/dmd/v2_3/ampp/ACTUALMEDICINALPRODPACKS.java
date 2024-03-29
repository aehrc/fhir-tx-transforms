//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.09.03 at 08:29:45 AM AEST 
//


package au.csiro.fhir.transforms.xml.dmd.v2_3.ampp;

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
 *                   &lt;element name="AMPP" type="{}AmppType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="APPLIANCE_PACK_INFO"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="PACK_INFO" type="{}PackInfoType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="DRUG_PRODUCT_PRESCRIB_INFO"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="PRESCRIB_INFO" type="{}PrescInfoType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="MEDICINAL_PRODUCT_PRICE"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="PRICE_INFO" type="{}PriceInfoType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="REIMBURSEMENT_INFO"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="REIMB_INFO" type="{}ReimbInfoType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="COMB_CONTENT"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="CCONTENT" type="{}ContentType" maxOccurs="unbounded" minOccurs="0"/&gt;
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
    "ampps",
    "appliancepackinfo",
    "drugproductprescribinfo",
    "medicinalproductprice",
    "reimbursementinfo",
    "combcontent"
})
@XmlRootElement(name = "ACTUAL_MEDICINAL_PROD_PACKS")
public class ACTUALMEDICINALPRODPACKS {

    @XmlElement(name = "AMPPS", required = true)
    protected ACTUALMEDICINALPRODPACKS.AMPPS ampps;
    @XmlElement(name = "APPLIANCE_PACK_INFO", required = true)
    protected ACTUALMEDICINALPRODPACKS.APPLIANCEPACKINFO appliancepackinfo;
    @XmlElement(name = "DRUG_PRODUCT_PRESCRIB_INFO", required = true)
    protected ACTUALMEDICINALPRODPACKS.DRUGPRODUCTPRESCRIBINFO drugproductprescribinfo;
    @XmlElement(name = "MEDICINAL_PRODUCT_PRICE", required = true)
    protected ACTUALMEDICINALPRODPACKS.MEDICINALPRODUCTPRICE medicinalproductprice;
    @XmlElement(name = "REIMBURSEMENT_INFO", required = true)
    protected ACTUALMEDICINALPRODPACKS.REIMBURSEMENTINFO reimbursementinfo;
    @XmlElement(name = "COMB_CONTENT", required = true)
    protected ACTUALMEDICINALPRODPACKS.COMBCONTENT combcontent;

    /**
     * Gets the value of the ampps property.
     * 
     * @return
     *     possible object is
     *     {@link ACTUALMEDICINALPRODPACKS.AMPPS }
     *     
     */
    public ACTUALMEDICINALPRODPACKS.AMPPS getAMPPS() {
        return ampps;
    }

    /**
     * Sets the value of the ampps property.
     * 
     * @param value
     *     allowed object is
     *     {@link ACTUALMEDICINALPRODPACKS.AMPPS }
     *     
     */
    public void setAMPPS(ACTUALMEDICINALPRODPACKS.AMPPS value) {
        this.ampps = value;
    }

    /**
     * Gets the value of the appliancepackinfo property.
     * 
     * @return
     *     possible object is
     *     {@link ACTUALMEDICINALPRODPACKS.APPLIANCEPACKINFO }
     *     
     */
    public ACTUALMEDICINALPRODPACKS.APPLIANCEPACKINFO getAPPLIANCEPACKINFO() {
        return appliancepackinfo;
    }

    /**
     * Sets the value of the appliancepackinfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link ACTUALMEDICINALPRODPACKS.APPLIANCEPACKINFO }
     *     
     */
    public void setAPPLIANCEPACKINFO(ACTUALMEDICINALPRODPACKS.APPLIANCEPACKINFO value) {
        this.appliancepackinfo = value;
    }

    /**
     * Gets the value of the drugproductprescribinfo property.
     * 
     * @return
     *     possible object is
     *     {@link ACTUALMEDICINALPRODPACKS.DRUGPRODUCTPRESCRIBINFO }
     *     
     */
    public ACTUALMEDICINALPRODPACKS.DRUGPRODUCTPRESCRIBINFO getDRUGPRODUCTPRESCRIBINFO() {
        return drugproductprescribinfo;
    }

    /**
     * Sets the value of the drugproductprescribinfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link ACTUALMEDICINALPRODPACKS.DRUGPRODUCTPRESCRIBINFO }
     *     
     */
    public void setDRUGPRODUCTPRESCRIBINFO(ACTUALMEDICINALPRODPACKS.DRUGPRODUCTPRESCRIBINFO value) {
        this.drugproductprescribinfo = value;
    }

    /**
     * Gets the value of the medicinalproductprice property.
     * 
     * @return
     *     possible object is
     *     {@link ACTUALMEDICINALPRODPACKS.MEDICINALPRODUCTPRICE }
     *     
     */
    public ACTUALMEDICINALPRODPACKS.MEDICINALPRODUCTPRICE getMEDICINALPRODUCTPRICE() {
        return medicinalproductprice;
    }

    /**
     * Sets the value of the medicinalproductprice property.
     * 
     * @param value
     *     allowed object is
     *     {@link ACTUALMEDICINALPRODPACKS.MEDICINALPRODUCTPRICE }
     *     
     */
    public void setMEDICINALPRODUCTPRICE(ACTUALMEDICINALPRODPACKS.MEDICINALPRODUCTPRICE value) {
        this.medicinalproductprice = value;
    }

    /**
     * Gets the value of the reimbursementinfo property.
     * 
     * @return
     *     possible object is
     *     {@link ACTUALMEDICINALPRODPACKS.REIMBURSEMENTINFO }
     *     
     */
    public ACTUALMEDICINALPRODPACKS.REIMBURSEMENTINFO getREIMBURSEMENTINFO() {
        return reimbursementinfo;
    }

    /**
     * Sets the value of the reimbursementinfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link ACTUALMEDICINALPRODPACKS.REIMBURSEMENTINFO }
     *     
     */
    public void setREIMBURSEMENTINFO(ACTUALMEDICINALPRODPACKS.REIMBURSEMENTINFO value) {
        this.reimbursementinfo = value;
    }

    /**
     * Gets the value of the combcontent property.
     * 
     * @return
     *     possible object is
     *     {@link ACTUALMEDICINALPRODPACKS.COMBCONTENT }
     *     
     */
    public ACTUALMEDICINALPRODPACKS.COMBCONTENT getCOMBCONTENT() {
        return combcontent;
    }

    /**
     * Sets the value of the combcontent property.
     * 
     * @param value
     *     allowed object is
     *     {@link ACTUALMEDICINALPRODPACKS.COMBCONTENT }
     *     
     */
    public void setCOMBCONTENT(ACTUALMEDICINALPRODPACKS.COMBCONTENT value) {
        this.combcontent = value;
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
     *         &lt;element name="AMPP" type="{}AmppType" maxOccurs="unbounded" minOccurs="0"/&gt;
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

        @XmlElement(name = "AMPP")
        protected List<AmppType> ampp;

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
         * {@link AmppType }
         * 
         * 
         */
        public List<AmppType> getAMPP() {
            if (ampp == null) {
                ampp = new ArrayList<AmppType>();
            }
            return this.ampp;
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
     *         &lt;element name="PACK_INFO" type="{}PackInfoType" maxOccurs="unbounded" minOccurs="0"/&gt;
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
        "packinfo"
    })
    public static class APPLIANCEPACKINFO {

        @XmlElement(name = "PACK_INFO")
        protected List<PackInfoType> packinfo;

        /**
         * Gets the value of the packinfo property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the packinfo property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getPACKINFO().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link PackInfoType }
         * 
         * 
         */
        public List<PackInfoType> getPACKINFO() {
            if (packinfo == null) {
                packinfo = new ArrayList<PackInfoType>();
            }
            return this.packinfo;
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
     *         &lt;element name="CCONTENT" type="{}ContentType" maxOccurs="unbounded" minOccurs="0"/&gt;
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
        "ccontent"
    })
    public static class COMBCONTENT {

        @XmlElement(name = "CCONTENT")
        protected List<ContentType> ccontent;

        /**
         * Gets the value of the ccontent property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the ccontent property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getCCONTENT().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ContentType }
         * 
         * 
         */
        public List<ContentType> getCCONTENT() {
            if (ccontent == null) {
                ccontent = new ArrayList<ContentType>();
            }
            return this.ccontent;
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
     *         &lt;element name="PRESCRIB_INFO" type="{}PrescInfoType" maxOccurs="unbounded" minOccurs="0"/&gt;
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
        "prescribinfo"
    })
    public static class DRUGPRODUCTPRESCRIBINFO {

        @XmlElement(name = "PRESCRIB_INFO")
        protected List<PrescInfoType> prescribinfo;

        /**
         * Gets the value of the prescribinfo property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the prescribinfo property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getPRESCRIBINFO().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link PrescInfoType }
         * 
         * 
         */
        public List<PrescInfoType> getPRESCRIBINFO() {
            if (prescribinfo == null) {
                prescribinfo = new ArrayList<PrescInfoType>();
            }
            return this.prescribinfo;
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
     *         &lt;element name="PRICE_INFO" type="{}PriceInfoType" maxOccurs="unbounded" minOccurs="0"/&gt;
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
        "priceinfo"
    })
    public static class MEDICINALPRODUCTPRICE {

        @XmlElement(name = "PRICE_INFO")
        protected List<PriceInfoType> priceinfo;

        /**
         * Gets the value of the priceinfo property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the priceinfo property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getPRICEINFO().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link PriceInfoType }
         * 
         * 
         */
        public List<PriceInfoType> getPRICEINFO() {
            if (priceinfo == null) {
                priceinfo = new ArrayList<PriceInfoType>();
            }
            return this.priceinfo;
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
     *         &lt;element name="REIMB_INFO" type="{}ReimbInfoType" maxOccurs="unbounded" minOccurs="0"/&gt;
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
        "reimbinfo"
    })
    public static class REIMBURSEMENTINFO {

        @XmlElement(name = "REIMB_INFO")
        protected List<ReimbInfoType> reimbinfo;

        /**
         * Gets the value of the reimbinfo property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the reimbinfo property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getREIMBINFO().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ReimbInfoType }
         * 
         * 
         */
        public List<ReimbInfoType> getREIMBINFO() {
            if (reimbinfo == null) {
                reimbinfo = new ArrayList<ReimbInfoType>();
            }
            return this.reimbinfo;
        }

    }

}
