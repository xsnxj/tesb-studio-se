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
import org.talend.core.model.properties.impl.ConnectionItemImpl;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.talend.core.model.properties.impl.ItemImpl;
import org.talend.repository.services.model.services.ServiceConnection;
import org.talend.repository.services.model.services.ServiceItem;
import org.talend.repository.services.model.services.ServiceOperation;
import org.talend.repository.services.model.services.ServicesPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Service Item</b></em>'. <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class ServiceItemImpl extends ConnectionItemImpl implements ServiceItem {

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

} // ServiceItemImpl
