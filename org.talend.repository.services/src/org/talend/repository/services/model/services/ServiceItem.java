/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.repository.services.model.services;

import org.eclipse.emf.common.util.EList;
import org.talend.core.model.properties.Item;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Service Item</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.talend.repository.services.model.services.ServiceItem#getServiceConnection <em>Service Connection</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.talend.repository.services.model.services.ServicesPackage#getServiceItem()
 * @model
 * @generated
 */
public interface ServiceItem extends Item {
    /**
     * Returns the value of the '<em><b>Service Connection</b></em>' reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Service Connection</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Service Connection</em>' reference.
     * @see #setServiceConnection(ServiceConnection)
     * @see org.talend.repository.services.model.services.ServicesPackage#getServiceItem_ServiceConnection()
     * @model
     * @generated
     */
    ServiceConnection getServiceConnection();

    /**
     * Sets the value of the '{@link org.talend.repository.services.model.services.ServiceItem#getServiceConnection <em>Service Connection</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Service Connection</em>' reference.
     * @see #getServiceConnection()
     * @generated
     */
    void setServiceConnection(ServiceConnection value);

} // ServiceItem
