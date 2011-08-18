/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.repository.services.model.services;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Service Port</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.talend.repository.services.model.services.ServicePort#getName <em>Name</em>}</li>
 *   <li>{@link org.talend.repository.services.model.services.ServicePort#getServiceOperation <em>Service Operation</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.talend.repository.services.model.services.ServicesPackage#getServicePort()
 * @model
 * @generated
 */
public interface ServicePort extends EObject {
    /**
     * Returns the value of the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Name</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Name</em>' attribute.
     * @see #setName(String)
     * @see org.talend.repository.services.model.services.ServicesPackage#getServicePort_Name()
     * @model
     * @generated
     */
    String getName();

    /**
     * Sets the value of the '{@link org.talend.repository.services.model.services.ServicePort#getName <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Name</em>' attribute.
     * @see #getName()
     * @generated
     */
    void setName(String value);

    /**
     * Returns the value of the '<em><b>Service Operation</b></em>' reference list.
     * The list contents are of type {@link org.talend.repository.services.model.services.ServiceOperation}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Service Operation</em>' reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Service Operation</em>' reference list.
     * @see org.talend.repository.services.model.services.ServicesPackage#getServicePort_ServiceOperation()
     * @model
     * @generated
     */
    EList<ServiceOperation> getServiceOperation();

} // ServicePort
