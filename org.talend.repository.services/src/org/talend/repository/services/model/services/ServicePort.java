/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.repository.services.model.services;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.common.util.EMap;
import org.talend.core.model.metadata.builder.connection.AbstractMetadataObject;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Service Port</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.talend.repository.services.model.services.ServicePort#getServiceOperation <em>Service Operation</em>}</li>
 *   <li>{@link org.talend.repository.services.model.services.ServicePort#getAdditionalInfo <em>Additional Info</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.talend.repository.services.model.services.ServicesPackage#getServicePort()
 * @model
 * @generated
 */
public interface ServicePort extends AbstractMetadataObject {
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

    /**
     * Returns the value of the '<em><b>Additional Info</b></em>' map.
     * The key is of type {@link java.lang.String},
     * and the value is of type {@link java.lang.String},
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Additional Info</em>' map isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Additional Info</em>' map.
     * @see org.talend.repository.services.model.services.ServicesPackage#getServicePort_AdditionalInfo()
     * @model mapType="org.talend.repository.services.model.services.AdditionalInfoMap<org.eclipse.emf.ecore.EString, org.eclipse.emf.ecore.EString>"
     * @generated
     */
    EMap<String, String> getAdditionalInfo();

} // ServicePort
