/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package org.talend.camel.core.model.camelProperties.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.talend.camel.core.model.camelProperties.CamelProcessItem;
import org.talend.camel.core.model.camelProperties.CamelPropertiesPackage;
import org.talend.core.model.properties.impl.ProcessItemImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Camel Process Item</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * </p>
 * 
 * @generated
 */
public class CamelProcessItemImpl extends ProcessItemImpl implements CamelProcessItem {

    private boolean exportMicroService;

    public boolean isExportMicroService() {
        return this.exportMicroService;
    }

    public void setExportMicroService(boolean exportMicroService) {
        this.exportMicroService = exportMicroService;
    }

    /**
     * The default value of the '{@link #getSpringContent() <em>Spring Content</em>}' attribute. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #getSpringContent()
     * @generated
     * @ordered
     */
    protected static final String SPRING_CONTENT_EDEFAULT = "";

    /**
     * The cached value of the '{@link #getSpringContent() <em>Spring Content</em>}' attribute. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #getSpringContent()
     * @generated
     * @ordered
     */
    protected String springContent = SPRING_CONTENT_EDEFAULT;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected CamelProcessItemImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return CamelPropertiesPackage.Literals.CAMEL_PROCESS_ITEM;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public String getSpringContent() {
        return springContent;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void setSpringContent(String newSpringContent) {
        String oldSpringContent = springContent;
        springContent = newSpringContent;
        if (eNotificationRequired()) {
            eNotify(new ENotificationImpl(this, Notification.SET, CamelPropertiesPackage.CAMEL_PROCESS_ITEM__SPRING_CONTENT,
                    oldSpringContent, springContent));
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
        case CamelPropertiesPackage.CAMEL_PROCESS_ITEM__SPRING_CONTENT:
            return getSpringContent();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
        case CamelPropertiesPackage.CAMEL_PROCESS_ITEM__SPRING_CONTENT:
            setSpringContent((String) newValue);
            return;
        }
        super.eSet(featureID, newValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void eUnset(int featureID) {
        switch (featureID) {
        case CamelPropertiesPackage.CAMEL_PROCESS_ITEM__SPRING_CONTENT:
            setSpringContent(SPRING_CONTENT_EDEFAULT);
            return;
        }
        super.eUnset(featureID);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public boolean eIsSet(int featureID) {
        switch (featureID) {
        case CamelPropertiesPackage.CAMEL_PROCESS_ITEM__SPRING_CONTENT:
            return SPRING_CONTENT_EDEFAULT == null ? springContent != null : !SPRING_CONTENT_EDEFAULT.equals(springContent);
        }
        return super.eIsSet(featureID);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public String toString() {
        if (eIsProxy()) {
            return super.toString();
        }

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (springContent: ");
        result.append(springContent);
        result.append(')');
        return result.toString();
    }

} // CamelProcessItemImpl
