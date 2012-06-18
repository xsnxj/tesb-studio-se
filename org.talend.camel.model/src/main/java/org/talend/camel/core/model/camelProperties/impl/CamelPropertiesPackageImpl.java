/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package org.talend.camel.core.model.camelProperties.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.talend.camel.core.model.camelProperties.BeanItem;
import org.talend.camel.core.model.camelProperties.CamelProcessItem;
import org.talend.camel.core.model.camelProperties.CamelPropertiesFactory;
import org.talend.camel.core.model.camelProperties.CamelPropertiesPackage;
import org.talend.camel.core.model.camelProperties.RouteResourceItem;
import org.talend.core.model.properties.PropertiesPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Package</b>. <!-- end-user-doc -->
 * @generated
 */
public class CamelPropertiesPackageImpl extends EPackageImpl implements CamelPropertiesPackage {

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EClass beanItemEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EClass camelProcessItemEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass routeResourceItemEClass = null;

    /**
     * Creates an instance of the model <b>Package</b>, registered with {@link org.eclipse.emf.ecore.EPackage.Registry
     * EPackage.Registry} by the package package URI value.
     * <p>
     * Note: the correct way to create the package is via the static factory method {@link #init init()}, which also
     * performs initialization of the package, or returns the registered package, if one already exists. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.ecore.EPackage.Registry
     * @see org.talend.camel.core.model.camelProperties.CamelPropertiesPackage#eNS_URI
     * @see #init()
     * @generated
     */
    private CamelPropertiesPackageImpl() {
        super(eNS_URI, CamelPropertiesFactory.eINSTANCE);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private static boolean isInited = false;

    /**
     * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
     * 
     * <p>
     * This method is used to initialize {@link CamelPropertiesPackage#eINSTANCE} when that field is accessed. Clients
     * should not invoke it directly. Instead, they should simply access that field to obtain the package. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #eNS_URI
     * @see #createPackageContents()
     * @see #initializePackageContents()
     * @generated
     */
    public static CamelPropertiesPackage init() {
        if (isInited) return (CamelPropertiesPackage)EPackage.Registry.INSTANCE.getEPackage(CamelPropertiesPackage.eNS_URI);

        // Obtain or create and register package
        CamelPropertiesPackageImpl theCamelPropertiesPackage = (CamelPropertiesPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof CamelPropertiesPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new CamelPropertiesPackageImpl());

        isInited = true;

        // Initialize simple dependencies
        PropertiesPackage.eINSTANCE.eClass();

        // Create package meta-data objects
        theCamelPropertiesPackage.createPackageContents();

        // Initialize created meta-data
        theCamelPropertiesPackage.initializePackageContents();

        // Mark meta-data to indicate it can't be changed
        theCamelPropertiesPackage.freeze();

  
        // Update the registry and return the package
        EPackage.Registry.INSTANCE.put(CamelPropertiesPackage.eNS_URI, theCamelPropertiesPackage);
        return theCamelPropertiesPackage;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EClass getBeanItem() {
        return beanItemEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EClass getCamelProcessItem() {
        return camelProcessItemEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public CamelPropertiesFactory getCamelPropertiesFactory() {
        return (CamelPropertiesFactory)getEFactoryInstance();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private boolean isCreated = false;

    /**
     * Creates the meta-model objects for the package.  This method is
     * guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void createPackageContents() {
        if (isCreated) return;
        isCreated = true;

        // Create classes and their features
        beanItemEClass = createEClass(BEAN_ITEM);

        camelProcessItemEClass = createEClass(CAMEL_PROCESS_ITEM);

		routeResourceItemEClass = createEClass(ROUTE_RESOURCE_ITEM);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private boolean isInitialized = false;

    /**
     * Complete the initialization of the package and its meta-model.  This
     * method is guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void initializePackageContents() {
        if (isInitialized) return;
        isInitialized = true;

        // Initialize package
        setName(eNAME);
        setNsPrefix(eNS_PREFIX);
        setNsURI(eNS_URI);

        // Obtain other dependent packages
        PropertiesPackage thePropertiesPackage = (PropertiesPackage)EPackage.Registry.INSTANCE.getEPackage(PropertiesPackage.eNS_URI);

        // Create type parameters

        // Set bounds for type parameters

        // Add supertypes to classes
        beanItemEClass.getESuperTypes().add(thePropertiesPackage.getRoutineItem());
        camelProcessItemEClass.getESuperTypes().add(thePropertiesPackage.getProcessItem());
		routeResourceItemEClass.getESuperTypes().add(
				thePropertiesPackage.getFileItem());

        // Initialize classes and features; add operations and parameters
        initEClass(beanItemEClass, BeanItem.class, "BeanItem", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        initEClass(camelProcessItemEClass, CamelProcessItem.class, "CamelProcessItem", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(routeResourceItemEClass, RouteResourceItem.class,
				"RouteResourceItem", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);

        // Create resource
        createResource(eNS_URI);
    }

	public EClass getRouteResourceItem() {
		return routeResourceItemEClass;
	}

} // CamelPropertiesPackageImpl
