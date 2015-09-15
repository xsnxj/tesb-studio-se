/**
 */
package org.talend.camel.core.model.camelProperties.impl;

import org.eclipse.emf.ecore.EClass;

import org.talend.camel.core.model.camelProperties.CamelPropertiesPackage;
import org.talend.camel.core.model.camelProperties.Routelet;

import org.talend.core.model.properties.impl.JobletProcessItemImpl;
import org.talend.designer.joblet.model.impl.JobletProcessImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Routelet</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class RouteletImpl extends JobletProcessItemImpl implements Routelet {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected RouteletImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return CamelPropertiesPackage.Literals.ROUTELET;
	}

} //RouteletImpl
