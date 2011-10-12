/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.repository.services.model.services;

import org.talend.core.model.metadata.builder.connection.AbstractMetadataObject;
import org.eclipse.emf.common.util.EMap;
import java.util.Map;
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
 *   <li>{@link org.talend.repository.services.model.services.ServiceOperation#getDocumentation <em>Documentation</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.talend.repository.services.model.services.ServicesPackage#getServiceOperation()
 * @model
 * @generated
 */
public interface ServiceOperation extends AbstractMetadataObject {
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

} // ServiceOperation
