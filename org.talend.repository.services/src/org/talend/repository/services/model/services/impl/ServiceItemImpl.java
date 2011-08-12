/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package org.talend.repository.services.model.services.impl;

import java.util.Collection;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.talend.core.model.properties.impl.ItemImpl;
import org.talend.repository.services.model.services.ServiceConnection;
import org.talend.repository.services.model.services.ServiceItem;
import org.talend.repository.services.model.services.ServiceOperation;
import org.talend.repository.services.model.services.ServicesPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Service Item</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.talend.repository.services.model.services.impl.ServiceItemImpl#getServiceConnection <em>Service Connection</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ServiceItemImpl extends ItemImpl implements ServiceItem {

    /**
     * The cached value of the '{@link #getServiceConnection() <em>Service Connection</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getServiceConnection()
     * @generated
     * @ordered
     */
    protected ServiceConnection serviceConnection;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    protected ServiceItemImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
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
    public ServiceConnection getServiceConnection() {
        if (serviceConnection != null && serviceConnection.eIsProxy()) {
            InternalEObject oldServiceConnection = (InternalEObject)serviceConnection;
            serviceConnection = (ServiceConnection)eResolveProxy(oldServiceConnection);
            if (serviceConnection != oldServiceConnection) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, ServicesPackage.SERVICE_ITEM__SERVICE_CONNECTION, oldServiceConnection, serviceConnection));
            }
        }
        return serviceConnection;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ServiceConnection basicGetServiceConnection() {
        return serviceConnection;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setServiceConnection(ServiceConnection newServiceConnection) {
        ServiceConnection oldServiceConnection = serviceConnection;
        serviceConnection = newServiceConnection;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ServicesPackage.SERVICE_ITEM__SERVICE_CONNECTION, oldServiceConnection, serviceConnection));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case ServicesPackage.SERVICE_ITEM__SERVICE_CONNECTION:
                if (resolve) return getServiceConnection();
                return basicGetServiceConnection();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
            case ServicesPackage.SERVICE_ITEM__SERVICE_CONNECTION:
                setServiceConnection((ServiceConnection)newValue);
                return;
        }
        super.eSet(featureID, newValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eUnset(int featureID) {
        switch (featureID) {
            case ServicesPackage.SERVICE_ITEM__SERVICE_CONNECTION:
                setServiceConnection((ServiceConnection)null);
                return;
        }
        super.eUnset(featureID);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean eIsSet(int featureID) {
        switch (featureID) {
            case ServicesPackage.SERVICE_ITEM__SERVICE_CONNECTION:
                return serviceConnection != null;
        }
        return super.eIsSet(featureID);
    }

} // ServiceItemImpl
