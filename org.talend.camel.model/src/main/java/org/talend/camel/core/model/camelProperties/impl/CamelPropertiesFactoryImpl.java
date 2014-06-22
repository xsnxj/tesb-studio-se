/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.camel.core.model.camelProperties.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.talend.camel.core.model.camelProperties.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class CamelPropertiesFactoryImpl extends EFactoryImpl implements CamelPropertiesFactory {
    /**
     * Creates the default factory implementation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static CamelPropertiesFactory init() {
        try {
            CamelPropertiesFactory theCamelPropertiesFactory = (CamelPropertiesFactory)EPackage.Registry.INSTANCE.getEFactory("http://www.talend.org/CamelProperties"); 
            if (theCamelPropertiesFactory != null) {
                return theCamelPropertiesFactory;
            }
        }
        catch (Exception exception) {
            EcorePlugin.INSTANCE.log(exception);
        }
        return new CamelPropertiesFactoryImpl();
    }

    /**
     * Creates an instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public CamelPropertiesFactoryImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EObject create(EClass eClass) {
        switch (eClass.getClassifierID()) {
            case CamelPropertiesPackage.BEAN_ITEM: return createBeanItem();
            case CamelPropertiesPackage.CAMEL_PROCESS_ITEM: return createCamelProcessItem();
            default:
                throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public BeanItem createBeanItem() {
        BeanItemImpl beanItem = new BeanItemImpl();
        return beanItem;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public CamelProcessItem createCamelProcessItem() {
        CamelProcessItemImpl camelProcessItem = new CamelProcessItemImpl();
        return camelProcessItem;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public CamelPropertiesPackage getCamelPropertiesPackage() {
        return (CamelPropertiesPackage)getEPackage();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @deprecated
     * @generated
     */
    @Deprecated
    public static CamelPropertiesPackage getPackage() {
        return CamelPropertiesPackage.eINSTANCE;
    }

} //CamelPropertiesFactoryImpl
