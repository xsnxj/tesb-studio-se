/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.repository.services.model.services;

import org.eclipse.emf.common.util.EList;

import org.talend.core.model.metadata.builder.connection.Connection;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Service Connection</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.talend.repository.services.model.services.ServiceConnection#getWSDLContent <em>WSDL Content</em>}</li>
 *   <li>{@link org.talend.repository.services.model.services.ServiceConnection#getWSDLPath <em>WSDL Path</em>}</li>
 *   <li>{@link org.talend.repository.services.model.services.ServiceConnection#getServiceOperation <em>Service Operation</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.talend.repository.services.model.services.ServicesPackage#getServiceConnection()
 * @model
 * @generated
 */
public interface ServiceConnection extends Connection {
    /**
     * Returns the value of the '<em><b>WSDL Content</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>WSDL Content</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>WSDL Content</em>' attribute.
     * @see #setWSDLContent(byte[])
     * @see org.talend.repository.services.model.services.ServicesPackage#getServiceConnection_WSDLContent()
     * @model
     * @generated
     */
    byte[] getWSDLContent();

    /**
     * Sets the value of the '{@link org.talend.repository.services.model.services.ServiceConnection#getWSDLContent <em>WSDL Content</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>WSDL Content</em>' attribute.
     * @see #getWSDLContent()
     * @generated
     */
    void setWSDLContent(byte[] value);

    /**
     * Returns the value of the '<em><b>WSDL Path</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>WSDL Path</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>WSDL Path</em>' attribute.
     * @see #setWSDLPath(String)
     * @see org.talend.repository.services.model.services.ServicesPackage#getServiceConnection_WSDLPath()
     * @model
     * @generated
     */
    String getWSDLPath();

    /**
     * Sets the value of the '{@link org.talend.repository.services.model.services.ServiceConnection#getWSDLPath <em>WSDL Path</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>WSDL Path</em>' attribute.
     * @see #getWSDLPath()
     * @generated
     */
    void setWSDLPath(String value);

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
     * @see org.talend.repository.services.model.services.ServicesPackage#getServiceConnection_ServiceOperation()
     * @model
     * @generated
     */
    EList<ServiceOperation> getServiceOperation();

} // ServiceConnection
