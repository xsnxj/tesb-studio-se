/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.camel.core.model.camelProperties;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.talend.core.model.properties.PropertiesPackage;

/**
 * <!-- begin-user-doc --> The <b>Package</b> for the model. It contains
 * accessors for the meta objects to represent
 * <ul>
 * <li>each class,</li>
 * <li>each feature of each class,</li>
 * <li>each enum,</li>
 * <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * 
 * @see org.talend.camel.core.model.camelProperties.CamelPropertiesFactory
 * @model kind="package"
 * @generated
 */
public interface CamelPropertiesPackage extends EPackage {
    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNAME = "camelProperties";

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_URI = "http://www.talend.org/CamelProperties";

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_PREFIX = "CamelProperties";

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
	CamelPropertiesPackage eINSTANCE = org.talend.camel.core.model.camelProperties.impl.CamelPropertiesPackageImpl
			.init();

    /**
     * The meta object id for the '{@link camelProperties.impl.BeanItemImpl <em>Bean Item</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see camelProperties.impl.BeanItemImpl
     * @see camelProperties.impl.CamelPropertiesPackageImpl#getBeanItem()
     * @generated
     */
    int BEAN_ITEM = 0;

    /**
     * The feature id for the '<em><b>Property</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BEAN_ITEM__PROPERTY = PropertiesPackage.ROUTINE_ITEM__PROPERTY;

    /**
     * The feature id for the '<em><b>State</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BEAN_ITEM__STATE = PropertiesPackage.ROUTINE_ITEM__STATE;

    /**
     * The feature id for the '<em><b>Parent</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BEAN_ITEM__PARENT = PropertiesPackage.ROUTINE_ITEM__PARENT;

    /**
     * The feature id for the '<em><b>Reference Resources</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BEAN_ITEM__REFERENCE_RESOURCES = PropertiesPackage.ROUTINE_ITEM__REFERENCE_RESOURCES;

    /**
     * The feature id for the '<em><b>File Extension</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BEAN_ITEM__FILE_EXTENSION = PropertiesPackage.ROUTINE_ITEM__FILE_EXTENSION;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BEAN_ITEM__NAME = PropertiesPackage.ROUTINE_ITEM__NAME;

    /**
     * The feature id for the '<em><b>Extension</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BEAN_ITEM__EXTENSION = PropertiesPackage.ROUTINE_ITEM__EXTENSION;

    /**
     * The feature id for the '<em><b>Content</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BEAN_ITEM__CONTENT = PropertiesPackage.ROUTINE_ITEM__CONTENT;

    /**
     * The feature id for the '<em><b>Built In</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BEAN_ITEM__BUILT_IN = PropertiesPackage.ROUTINE_ITEM__BUILT_IN;

    /**
     * The feature id for the '<em><b>Imports</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BEAN_ITEM__IMPORTS = PropertiesPackage.ROUTINE_ITEM__IMPORTS;

    /**
     * The feature id for the '<em><b>Package Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BEAN_ITEM__PACKAGE_TYPE = PropertiesPackage.ROUTINE_ITEM__PACKAGE_TYPE;

    /**
     * The number of structural features of the '<em>Bean Item</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BEAN_ITEM_FEATURE_COUNT = PropertiesPackage.ROUTINE_ITEM_FEATURE_COUNT + 0;

    /**
     * The meta object id for the '{@link camelProperties.impl.CamelProcessItemImpl <em>Camel Process Item</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see camelProperties.impl.CamelProcessItemImpl
     * @see camelProperties.impl.CamelPropertiesPackageImpl#getCamelProcessItem()
     * @generated
     */
    int CAMEL_PROCESS_ITEM = 1;

    /**
     * The feature id for the '<em><b>Property</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CAMEL_PROCESS_ITEM__PROPERTY = PropertiesPackage.PROCESS_ITEM__PROPERTY;

    /**
     * The feature id for the '<em><b>State</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CAMEL_PROCESS_ITEM__STATE = PropertiesPackage.PROCESS_ITEM__STATE;

    /**
     * The feature id for the '<em><b>Parent</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CAMEL_PROCESS_ITEM__PARENT = PropertiesPackage.PROCESS_ITEM__PARENT;

    /**
     * The feature id for the '<em><b>Reference Resources</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CAMEL_PROCESS_ITEM__REFERENCE_RESOURCES = PropertiesPackage.PROCESS_ITEM__REFERENCE_RESOURCES;

    /**
     * The feature id for the '<em><b>File Extension</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CAMEL_PROCESS_ITEM__FILE_EXTENSION = PropertiesPackage.PROCESS_ITEM__FILE_EXTENSION;

    /**
     * The feature id for the '<em><b>Process</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CAMEL_PROCESS_ITEM__PROCESS = PropertiesPackage.PROCESS_ITEM__PROCESS;

    /**
     * The number of structural features of the '<em>Camel Process Item</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CAMEL_PROCESS_ITEM_FEATURE_COUNT = PropertiesPackage.PROCESS_ITEM_FEATURE_COUNT + 0;

    /**
     * The meta object id for the '{@link camelProperties.impl.RouteResourceItemImpl <em>Route Resource Item</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see camelProperties.impl.RouteResourceItemImpl
     * @see camelProperties.impl.CamelPropertiesPackageImpl#getRouteResourceItem()
     * @generated
     */
    int ROUTE_RESOURCE_ITEM = 2;

    /**
     * The feature id for the '<em><b>Property</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROUTE_RESOURCE_ITEM__PROPERTY = PropertiesPackage.FILE_ITEM__PROPERTY;

    /**
     * The feature id for the '<em><b>State</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROUTE_RESOURCE_ITEM__STATE = PropertiesPackage.FILE_ITEM__STATE;

    /**
     * The feature id for the '<em><b>Parent</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROUTE_RESOURCE_ITEM__PARENT = PropertiesPackage.FILE_ITEM__PARENT;

    /**
     * The feature id for the '<em><b>Reference Resources</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROUTE_RESOURCE_ITEM__REFERENCE_RESOURCES = PropertiesPackage.FILE_ITEM__REFERENCE_RESOURCES;

    /**
     * The feature id for the '<em><b>File Extension</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROUTE_RESOURCE_ITEM__FILE_EXTENSION = PropertiesPackage.FILE_ITEM__FILE_EXTENSION;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROUTE_RESOURCE_ITEM__NAME = PropertiesPackage.FILE_ITEM__NAME;

    /**
     * The feature id for the '<em><b>Extension</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROUTE_RESOURCE_ITEM__EXTENSION = PropertiesPackage.FILE_ITEM__EXTENSION;

    /**
     * The feature id for the '<em><b>Content</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROUTE_RESOURCE_ITEM__CONTENT = PropertiesPackage.FILE_ITEM__CONTENT;

    /**
     * The number of structural features of the '<em>Route Resource Item</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROUTE_RESOURCE_ITEM_FEATURE_COUNT = PropertiesPackage.FILE_ITEM_FEATURE_COUNT + 0;


    /**
     * Returns the meta object for class '{@link camelProperties.BeanItem <em>Bean Item</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Bean Item</em>'.
     * @see camelProperties.BeanItem
     * @generated
     */
    EClass getBeanItem();

    /**
     * Returns the meta object for class '{@link camelProperties.CamelProcessItem <em>Camel Process Item</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Camel Process Item</em>'.
     * @see camelProperties.CamelProcessItem
     * @generated
     */
    EClass getCamelProcessItem();

    /**
     * Returns the meta object for class '{@link camelProperties.RouteResourceItem <em>Route Resource Item</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Route Resource Item</em>'.
     * @see camelProperties.RouteResourceItem
     * @generated
     */
    EClass getRouteResourceItem();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    CamelPropertiesFactory getCamelPropertiesFactory();

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
         * The meta object literal for the '{@link camelProperties.impl.BeanItemImpl <em>Bean Item</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see camelProperties.impl.BeanItemImpl
         * @see camelProperties.impl.CamelPropertiesPackageImpl#getBeanItem()
         * @generated
         */
        EClass BEAN_ITEM = eINSTANCE.getBeanItem();

        /**
         * The meta object literal for the '{@link camelProperties.impl.CamelProcessItemImpl <em>Camel Process Item</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see camelProperties.impl.CamelProcessItemImpl
         * @see camelProperties.impl.CamelPropertiesPackageImpl#getCamelProcessItem()
         * @generated
         */
        EClass CAMEL_PROCESS_ITEM = eINSTANCE.getCamelProcessItem();

        /**
         * The meta object literal for the '{@link camelProperties.impl.RouteResourceItemImpl <em>Route Resource Item</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see camelProperties.impl.RouteResourceItemImpl
         * @see camelProperties.impl.CamelPropertiesPackageImpl#getRouteResourceItem()
         * @generated
         */
        EClass ROUTE_RESOURCE_ITEM = eINSTANCE.getRouteResourceItem();

    }

} //CamelPropertiesPackage
