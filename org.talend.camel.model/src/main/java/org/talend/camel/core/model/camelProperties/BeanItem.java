/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.camel.core.model.camelProperties;

import org.eclipse.emf.common.util.EList;

import org.talend.core.model.properties.FileItem;

import org.talend.designer.core.model.utils.emf.component.IMPORTType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Bean Item</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.talend.camel.core.model.camelProperties.BeanItem#getImports <em>Imports</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.talend.camel.core.model.camelProperties.CamelPropertiesPackage#getBeanItem()
 * @model
 * @generated
 */
public interface BeanItem extends FileItem {
    /**
     * Returns the value of the '<em><b>Imports</b></em>' containment reference list.
     * The list contents are of type {@link org.talend.designer.core.model.utils.emf.component.IMPORTType}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Imports</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Imports</em>' containment reference list.
     * @see org.talend.camel.core.model.camelProperties.CamelPropertiesPackage#getBeanItem_Imports()
     * @model containment="true"
     * @generated
     */
    EList<IMPORTType> getImports();

} // BeanItem
