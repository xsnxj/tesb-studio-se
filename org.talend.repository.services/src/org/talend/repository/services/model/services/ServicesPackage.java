/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.repository.services.model.services;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;

import org.talend.core.model.properties.PropertiesPackage;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.talend.repository.services.model.services.ServicesFactory
 * @model kind="package"
 * @generated
 */
public interface ServicesPackage extends EPackage {
    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNAME = "services";

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_URI = "http://www.talend.org/Services";

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_PREFIX = "Services";

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    ServicesPackage eINSTANCE = org.talend.repository.services.model.services.impl.ServicesPackageImpl.init();

    /**
     * The meta object id for the '{@link org.talend.repository.services.model.services.impl.ServiceItemImpl <em>Service Item</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.repository.services.model.services.impl.ServiceItemImpl
     * @see org.talend.repository.services.model.services.impl.ServicesPackageImpl#getServiceItem()
     * @generated
     */
    int SERVICE_ITEM = 0;

    /**
     * The feature id for the '<em><b>Property</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SERVICE_ITEM__PROPERTY = PropertiesPackage.ITEM__PROPERTY;

    /**
     * The feature id for the '<em><b>State</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SERVICE_ITEM__STATE = PropertiesPackage.ITEM__STATE;

    /**
     * The feature id for the '<em><b>Parent</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SERVICE_ITEM__PARENT = PropertiesPackage.ITEM__PARENT;

    /**
     * The feature id for the '<em><b>WSDL Path</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SERVICE_ITEM__WSDL_PATH = PropertiesPackage.ITEM_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>WSDL Content</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SERVICE_ITEM__WSDL_CONTENT = PropertiesPackage.ITEM_FEATURE_COUNT + 1;

    /**
     * The number of structural features of the '<em>Service Item</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SERVICE_ITEM_FEATURE_COUNT = PropertiesPackage.ITEM_FEATURE_COUNT + 2;


    /**
     * Returns the meta object for class '{@link org.talend.repository.services.model.services.ServiceItem <em>Service Item</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Service Item</em>'.
     * @see org.talend.repository.services.model.services.ServiceItem
     * @generated
     */
    EClass getServiceItem();

    /**
     * Returns the meta object for the attribute '{@link org.talend.repository.services.model.services.ServiceItem#getWSDLPath <em>WSDL Path</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>WSDL Path</em>'.
     * @see org.talend.repository.services.model.services.ServiceItem#getWSDLPath()
     * @see #getServiceItem()
     * @generated
     */
    EAttribute getServiceItem_WSDLPath();

    /**
     * Returns the meta object for the attribute '{@link org.talend.repository.services.model.services.ServiceItem#getWSDLContent <em>WSDL Content</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>WSDL Content</em>'.
     * @see org.talend.repository.services.model.services.ServiceItem#getWSDLContent()
     * @see #getServiceItem()
     * @generated
     */
    EAttribute getServiceItem_WSDLContent();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    ServicesFactory getServicesFactory();

    /**
     * <!-- begin-user-doc -->
     * Defines literals for the meta objects that represent
     * <ul>
     *   <li>each class,</li>
     *   <li>each feature of each class,</li>
     *   <li>each enum,</li>
     *   <li>and each data type</li>
     * </ul>
     * <!-- end-user-doc -->
     * @generated
     */
    interface Literals {
        /**
         * The meta object literal for the '{@link org.talend.repository.services.model.services.impl.ServiceItemImpl <em>Service Item</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.repository.services.model.services.impl.ServiceItemImpl
         * @see org.talend.repository.services.model.services.impl.ServicesPackageImpl#getServiceItem()
         * @generated
         */
        EClass SERVICE_ITEM = eINSTANCE.getServiceItem();

        /**
         * The meta object literal for the '<em><b>WSDL Path</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute SERVICE_ITEM__WSDL_PATH = eINSTANCE.getServiceItem_WSDLPath();

        /**
         * The meta object literal for the '<em><b>WSDL Content</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute SERVICE_ITEM__WSDL_CONTENT = eINSTANCE.getServiceItem_WSDLContent();

    }

} //ServicesPackage
