/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.repository.services.model.services.impl;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.talend.repository.services.model.services.ServiceOperation;
import org.talend.repository.services.model.services.ServicesPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Service Operation</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.talend.repository.services.model.services.impl.ServiceOperationImpl#getReferenceJobId <em>Reference Job Id</em>}</li>
 *   <li>{@link org.talend.repository.services.model.services.impl.ServiceOperationImpl#getOperationName <em>Operation Name</em>}</li>
 *   <li>{@link org.talend.repository.services.model.services.impl.ServiceOperationImpl#getDocumentation <em>Documentation</em>}</li>
 *   <li>{@link org.talend.repository.services.model.services.impl.ServiceOperationImpl#getLabel <em>Label</em>}</li>
 *   <li>{@link org.talend.repository.services.model.services.impl.ServiceOperationImpl#getAdditionalInfo <em>Additional Info</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ServiceOperationImpl extends EObjectImpl implements ServiceOperation {
    /**
     * The default value of the '{@link #getReferenceJobId() <em>Reference Job Id</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getReferenceJobId()
     * @generated
     * @ordered
     */
    protected static final String REFERENCE_JOB_ID_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getReferenceJobId() <em>Reference Job Id</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getReferenceJobId()
     * @generated
     * @ordered
     */
    protected String referenceJobId = REFERENCE_JOB_ID_EDEFAULT;

    /**
     * The default value of the '{@link #getOperationName() <em>Operation Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getOperationName()
     * @generated
     * @ordered
     */
    protected static final String OPERATION_NAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getOperationName() <em>Operation Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getOperationName()
     * @generated
     * @ordered
     */
    protected String operationName = OPERATION_NAME_EDEFAULT;

    /**
     * The default value of the '{@link #getDocumentation() <em>Documentation</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDocumentation()
     * @generated
     * @ordered
     */
    protected static final String DOCUMENTATION_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getDocumentation() <em>Documentation</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDocumentation()
     * @generated
     * @ordered
     */
    protected String documentation = DOCUMENTATION_EDEFAULT;

    /**
     * The default value of the '{@link #getLabel() <em>Label</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLabel()
     * @generated
     * @ordered
     */
    protected static final String LABEL_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getLabel() <em>Label</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLabel()
     * @generated
     * @ordered
     */
    protected String label = LABEL_EDEFAULT;

    /**
     * The cached value of the '{@link #getAdditionalInfo() <em>Additional Info</em>}' map.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getAdditionalInfo()
     * @generated
     * @ordered
     */
    protected EMap<String, String> additionalInfo;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected ServiceOperationImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return ServicesPackage.Literals.SERVICE_OPERATION;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getReferenceJobId() {
        return referenceJobId;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setReferenceJobId(String newReferenceJobId) {
        String oldReferenceJobId = referenceJobId;
        referenceJobId = newReferenceJobId;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ServicesPackage.SERVICE_OPERATION__REFERENCE_JOB_ID, oldReferenceJobId, referenceJobId));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getOperationName() {
        return operationName;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setOperationName(String newOperationName) {
        String oldOperationName = operationName;
        operationName = newOperationName;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ServicesPackage.SERVICE_OPERATION__OPERATION_NAME, oldOperationName, operationName));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getDocumentation() {
        return documentation;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setDocumentation(String newDocumentation) {
        String oldDocumentation = documentation;
        documentation = newDocumentation;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ServicesPackage.SERVICE_OPERATION__DOCUMENTATION, oldDocumentation, documentation));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getLabel() {
        return label;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setLabel(String newLabel) {
        String oldLabel = label;
        label = newLabel;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ServicesPackage.SERVICE_OPERATION__LABEL, oldLabel, label));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EMap<String, String> getAdditionalInfo() {
        if (additionalInfo == null) {
            additionalInfo = new EcoreEMap<String,String>(ServicesPackage.Literals.ADDITIONAL_INFO_MAP, AdditionalInfoMapImpl.class, this, ServicesPackage.SERVICE_OPERATION__ADDITIONAL_INFO);
        }
        return additionalInfo;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case ServicesPackage.SERVICE_OPERATION__ADDITIONAL_INFO:
                return ((InternalEList<?>)getAdditionalInfo()).basicRemove(otherEnd, msgs);
        }
        return super.eInverseRemove(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case ServicesPackage.SERVICE_OPERATION__REFERENCE_JOB_ID:
                return getReferenceJobId();
            case ServicesPackage.SERVICE_OPERATION__OPERATION_NAME:
                return getOperationName();
            case ServicesPackage.SERVICE_OPERATION__DOCUMENTATION:
                return getDocumentation();
            case ServicesPackage.SERVICE_OPERATION__LABEL:
                return getLabel();
            case ServicesPackage.SERVICE_OPERATION__ADDITIONAL_INFO:
                if (coreType) return getAdditionalInfo();
                else return getAdditionalInfo().map();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
            case ServicesPackage.SERVICE_OPERATION__REFERENCE_JOB_ID:
                setReferenceJobId((String)newValue);
                return;
            case ServicesPackage.SERVICE_OPERATION__OPERATION_NAME:
                setOperationName((String)newValue);
                return;
            case ServicesPackage.SERVICE_OPERATION__DOCUMENTATION:
                setDocumentation((String)newValue);
                return;
            case ServicesPackage.SERVICE_OPERATION__LABEL:
                setLabel((String)newValue);
                return;
            case ServicesPackage.SERVICE_OPERATION__ADDITIONAL_INFO:
                ((EStructuralFeature.Setting)getAdditionalInfo()).set(newValue);
                return;
        }
        super.eSet(featureID, newValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eUnset(int featureID) {
        switch (featureID) {
            case ServicesPackage.SERVICE_OPERATION__REFERENCE_JOB_ID:
                setReferenceJobId(REFERENCE_JOB_ID_EDEFAULT);
                return;
            case ServicesPackage.SERVICE_OPERATION__OPERATION_NAME:
                setOperationName(OPERATION_NAME_EDEFAULT);
                return;
            case ServicesPackage.SERVICE_OPERATION__DOCUMENTATION:
                setDocumentation(DOCUMENTATION_EDEFAULT);
                return;
            case ServicesPackage.SERVICE_OPERATION__LABEL:
                setLabel(LABEL_EDEFAULT);
                return;
            case ServicesPackage.SERVICE_OPERATION__ADDITIONAL_INFO:
                getAdditionalInfo().clear();
                return;
        }
        super.eUnset(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean eIsSet(int featureID) {
        switch (featureID) {
            case ServicesPackage.SERVICE_OPERATION__REFERENCE_JOB_ID:
                return REFERENCE_JOB_ID_EDEFAULT == null ? referenceJobId != null : !REFERENCE_JOB_ID_EDEFAULT.equals(referenceJobId);
            case ServicesPackage.SERVICE_OPERATION__OPERATION_NAME:
                return OPERATION_NAME_EDEFAULT == null ? operationName != null : !OPERATION_NAME_EDEFAULT.equals(operationName);
            case ServicesPackage.SERVICE_OPERATION__DOCUMENTATION:
                return DOCUMENTATION_EDEFAULT == null ? documentation != null : !DOCUMENTATION_EDEFAULT.equals(documentation);
            case ServicesPackage.SERVICE_OPERATION__LABEL:
                return LABEL_EDEFAULT == null ? label != null : !LABEL_EDEFAULT.equals(label);
            case ServicesPackage.SERVICE_OPERATION__ADDITIONAL_INFO:
                return additionalInfo != null && !additionalInfo.isEmpty();
        }
        return super.eIsSet(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String toString() {
        if (eIsProxy()) return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (referenceJobId: ");
        result.append(referenceJobId);
        result.append(", operationName: ");
        result.append(operationName);
        result.append(", documentation: ");
        result.append(documentation);
        result.append(", label: ");
        result.append(label);
        result.append(')');
        return result.toString();
    }

} //ServiceOperationImpl
