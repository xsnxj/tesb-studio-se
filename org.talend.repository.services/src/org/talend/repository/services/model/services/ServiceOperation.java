/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.repository.services.model.services;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Service Operation</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.talend.repository.services.model.services.ServiceOperation#getReferenceJobId <em>Reference Job Id</em>}</li>
 *   <li>{@link org.talend.repository.services.model.services.ServiceOperation#getOperationName <em>Operation Name</em>}</li>
 *   <li>{@link org.talend.repository.services.model.services.ServiceOperation#getDocumentation <em>Documentation</em>}</li>
 *   <li>{@link org.talend.repository.services.model.services.ServiceOperation#getLabel <em>Label</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.talend.repository.services.model.services.ServicesPackage#getServiceOperation()
 * @model
 * @generated
 */
public interface ServiceOperation extends EObject {
    /**
     * Returns the value of the '<em><b>Reference Job Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Reference Job Id</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Reference Job Id</em>' attribute.
     * @see #setReferenceJobId(String)
     * @see org.talend.repository.services.model.services.ServicesPackage#getServiceOperation_ReferenceJobId()
     * @model
     * @generated
     */
    String getReferenceJobId();

    /**
     * Sets the value of the '{@link org.talend.repository.services.model.services.ServiceOperation#getReferenceJobId <em>Reference Job Id</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Reference Job Id</em>' attribute.
     * @see #getReferenceJobId()
     * @generated
     */
    void setReferenceJobId(String value);

    /**
     * Returns the value of the '<em><b>Operation Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Operation Name</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Operation Name</em>' attribute.
     * @see #setOperationName(String)
     * @see org.talend.repository.services.model.services.ServicesPackage#getServiceOperation_OperationName()
     * @model
     * @generated
     */
    String getOperationName();

    /**
     * Sets the value of the '{@link org.talend.repository.services.model.services.ServiceOperation#getOperationName <em>Operation Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Operation Name</em>' attribute.
     * @see #getOperationName()
     * @generated
     */
    void setOperationName(String value);

    /**
     * Returns the value of the '<em><b>Documentation</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Documentation</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Documentation</em>' attribute.
     * @see #setDocumentation(String)
     * @see org.talend.repository.services.model.services.ServicesPackage#getServiceOperation_Documentation()
     * @model
     * @generated
     */
    String getDocumentation();

    /**
     * Sets the value of the '{@link org.talend.repository.services.model.services.ServiceOperation#getDocumentation <em>Documentation</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Documentation</em>' attribute.
     * @see #getDocumentation()
     * @generated
     */
    void setDocumentation(String value);

    /**
     * Returns the value of the '<em><b>Label</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Label</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Label</em>' attribute.
     * @see #setLabel(String)
     * @see org.talend.repository.services.model.services.ServicesPackage#getServiceOperation_Label()
     * @model
     * @generated
     */
    String getLabel();

    /**
     * Sets the value of the '{@link org.talend.repository.services.model.services.ServiceOperation#getLabel <em>Label</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Label</em>' attribute.
     * @see #getLabel()
     * @generated
     */
    void setLabel(String value);

} // ServiceOperation
