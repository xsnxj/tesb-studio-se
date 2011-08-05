/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.repository.services.model.services.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.talend.core.model.properties.impl.ItemImpl;

import org.talend.repository.services.model.services.ServiceItem;
import org.talend.repository.services.model.services.ServicesPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Service Item</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.talend.repository.services.model.services.impl.ServiceItemImpl#getWSDLPath <em>WSDL Path</em>}</li>
 *   <li>{@link org.talend.repository.services.model.services.impl.ServiceItemImpl#getWSDLContent <em>WSDL Content</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ServiceItemImpl extends ItemImpl implements ServiceItem {
    /**
     * The default value of the '{@link #getWSDLPath() <em>WSDL Path</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getWSDLPath()
     * @generated
     * @ordered
     */
    protected static final String WSDL_PATH_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getWSDLPath() <em>WSDL Path</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getWSDLPath()
     * @generated
     * @ordered
     */
    protected String wsdlPath = WSDL_PATH_EDEFAULT;

    /**
     * The default value of the '{@link #getWSDLContent() <em>WSDL Content</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getWSDLContent()
     * @generated
     * @ordered
     */
    protected static final byte[] WSDL_CONTENT_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getWSDLContent() <em>WSDL Content</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getWSDLContent()
     * @generated
     * @ordered
     */
    protected byte[] wsdlContent = WSDL_CONTENT_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected ServiceItemImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return ServicesPackage.Literals.SERVICE_ITEM;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getWSDLPath() {
        return wsdlPath;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setWSDLPath(String newWSDLPath) {
        String oldWSDLPath = wsdlPath;
        wsdlPath = newWSDLPath;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ServicesPackage.SERVICE_ITEM__WSDL_PATH, oldWSDLPath, wsdlPath));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public byte[] getWSDLContent() {
        return wsdlContent;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setWSDLContent(byte[] newWSDLContent) {
        byte[] oldWSDLContent = wsdlContent;
        wsdlContent = newWSDLContent;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ServicesPackage.SERVICE_ITEM__WSDL_CONTENT, oldWSDLContent, wsdlContent));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case ServicesPackage.SERVICE_ITEM__WSDL_PATH:
                return getWSDLPath();
            case ServicesPackage.SERVICE_ITEM__WSDL_CONTENT:
                return getWSDLContent();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
            case ServicesPackage.SERVICE_ITEM__WSDL_PATH:
                setWSDLPath((String)newValue);
                return;
            case ServicesPackage.SERVICE_ITEM__WSDL_CONTENT:
                setWSDLContent((byte[])newValue);
                return;
        }
        super.eSet(featureID, newValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eUnset(int featureID) {
        switch (featureID) {
            case ServicesPackage.SERVICE_ITEM__WSDL_PATH:
                setWSDLPath(WSDL_PATH_EDEFAULT);
                return;
            case ServicesPackage.SERVICE_ITEM__WSDL_CONTENT:
                setWSDLContent(WSDL_CONTENT_EDEFAULT);
                return;
        }
        super.eUnset(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean eIsSet(int featureID) {
        switch (featureID) {
            case ServicesPackage.SERVICE_ITEM__WSDL_PATH:
                return WSDL_PATH_EDEFAULT == null ? wsdlPath != null : !WSDL_PATH_EDEFAULT.equals(wsdlPath);
            case ServicesPackage.SERVICE_ITEM__WSDL_CONTENT:
                return WSDL_CONTENT_EDEFAULT == null ? wsdlContent != null : !WSDL_CONTENT_EDEFAULT.equals(wsdlContent);
        }
        return super.eIsSet(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String toString() {
        if (eIsProxy()) return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (WSDLPath: ");
        result.append(wsdlPath);
        result.append(", WSDLContent: ");
        result.append(wsdlContent);
        result.append(')');
        return result.toString();
    }

} //ServiceItemImpl
