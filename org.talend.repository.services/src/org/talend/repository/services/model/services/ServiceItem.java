/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.repository.services.model.services;

import org.talend.core.model.properties.Item;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Service Item</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.talend.repository.services.model.services.ServiceItem#getWSDLPath <em>WSDL Path</em>}</li>
 *   <li>{@link org.talend.repository.services.model.services.ServiceItem#getWSDLContent <em>WSDL Content</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.talend.repository.services.model.services.ServicesPackage#getServiceItem()
 * @model
 * @generated
 */
public interface ServiceItem extends Item {
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
     * @see org.talend.repository.services.model.services.ServicesPackage#getServiceItem_WSDLPath()
     * @model
     * @generated
     */
    String getWSDLPath();

    /**
     * Sets the value of the '{@link org.talend.repository.services.model.services.ServiceItem#getWSDLPath <em>WSDL Path</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>WSDL Path</em>' attribute.
     * @see #getWSDLPath()
     * @generated
     */
    void setWSDLPath(String value);

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
     * @see org.talend.repository.services.model.services.ServicesPackage#getServiceItem_WSDLContent()
     * @model
     * @generated
     */
    byte[] getWSDLContent();

    /**
     * Sets the value of the '{@link org.talend.repository.services.model.services.ServiceItem#getWSDLContent <em>WSDL Content</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>WSDL Content</em>' attribute.
     * @see #getWSDLContent()
     * @generated
     */
    void setWSDLContent(byte[] value);

} // ServiceItem
