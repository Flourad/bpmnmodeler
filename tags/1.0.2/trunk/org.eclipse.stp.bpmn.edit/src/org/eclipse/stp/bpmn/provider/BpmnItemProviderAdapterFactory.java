/******************************************************************************
* Copyright (c) 2006, Intalio Inc.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
* 
* Contributors:
*     Intalio Inc. - initial API and implementation
*******************************************************************************/

package org.eclipse.stp.bpmn.provider;


import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.edit.provider.ChangeNotifier;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.IChangeNotifier;
import org.eclipse.emf.edit.provider.IDisposable;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.stp.bpmn.util.BpmnAdapterFactory;

/**
 * This is the factory that is used to provide the interfaces needed to support Viewers.
 * The adapters generated by this factory convert EMF adapter notifications into calls to {@link #fireNotifyChanged fireNotifyChanged}.
 * The adapters also support Eclipse property sheets.
 * Note that most of the adapters are shared among multiple instances.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class BpmnItemProviderAdapterFactory extends BpmnAdapterFactory implements ComposeableAdapterFactory, IChangeNotifier, IDisposable {
    /**
     * This keeps track of the root adapter factory that delegates to this adapter factory.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	protected ComposedAdapterFactory parentAdapterFactory;

    /**
     * This is used to implement {@link org.eclipse.emf.edit.provider.IChangeNotifier}.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	protected IChangeNotifier changeNotifier = new ChangeNotifier();

    /**
     * This keeps track of all the supported types checked by {@link #isFactoryForType isFactoryForType}.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	protected Collection<Object> supportedTypes = new ArrayList<Object>();

    /**
     * This constructs an instance.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public BpmnItemProviderAdapterFactory() {
        supportedTypes.add(IEditingDomainItemProvider.class);
        supportedTypes.add(IStructuredItemContentProvider.class);
        supportedTypes.add(ITreeItemContentProvider.class);
        supportedTypes.add(IItemLabelProvider.class);
        supportedTypes.add(IItemPropertySource.class);
    }

    /**
     * This keeps track of the one adapter used for all {@link org.eclipse.stp.bpmn.Activity} instances.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	protected ActivityItemProvider activityItemProvider;

    /**
     * This creates an adapter for a {@link org.eclipse.stp.bpmn.Activity}.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	@Override
    public Adapter createActivityAdapter() {
        if (activityItemProvider == null) {
            activityItemProvider = new ActivityItemProvider(this);
        }

        return activityItemProvider;
    }

    /**
     * This keeps track of the one adapter used for all {@link org.eclipse.stp.bpmn.Artifact} instances.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected ArtifactItemProvider artifactItemProvider;

    /**
     * This creates an adapter for a {@link org.eclipse.stp.bpmn.Artifact}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Adapter createArtifactAdapter() {
        if (artifactItemProvider == null) {
            artifactItemProvider = new ArtifactItemProvider(this);
        }

        return artifactItemProvider;
    }

    /**
     * This keeps track of the one adapter used for all {@link org.eclipse.stp.bpmn.ArtifactsContainer} instances.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected ArtifactsContainerItemProvider artifactsContainerItemProvider;

    /**
     * This creates an adapter for a {@link org.eclipse.stp.bpmn.ArtifactsContainer}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Adapter createArtifactsContainerAdapter() {
        if (artifactsContainerItemProvider == null) {
            artifactsContainerItemProvider = new ArtifactsContainerItemProvider(this);
        }

        return artifactsContainerItemProvider;
    }

    /**
     * This keeps track of the one adapter used for all {@link org.eclipse.stp.bpmn.Association} instances.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected AssociationItemProvider associationItemProvider;

    /**
     * This creates an adapter for a {@link org.eclipse.stp.bpmn.Association}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Adapter createAssociationAdapter() {
        if (associationItemProvider == null) {
            associationItemProvider = new AssociationItemProvider(this);
        }

        return associationItemProvider;
    }

    /**
     * This keeps track of the one adapter used for all {@link org.eclipse.stp.bpmn.AssociationTarget} instances.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected AssociationTargetItemProvider associationTargetItemProvider;

    /**
     * This creates an adapter for a {@link org.eclipse.stp.bpmn.AssociationTarget}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Adapter createAssociationTargetAdapter() {
        if (associationTargetItemProvider == null) {
            associationTargetItemProvider = new AssociationTargetItemProvider(this);
        }

        return associationTargetItemProvider;
    }

    /**
     * This keeps track of the one adapter used for all {@link org.eclipse.stp.bpmn.BpmnDiagram} instances.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	protected BpmnDiagramItemProvider bpmnDiagramItemProvider;

    /**
     * This creates an adapter for a {@link org.eclipse.stp.bpmn.BpmnDiagram}.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	@Override
    public Adapter createBpmnDiagramAdapter() {
        if (bpmnDiagramItemProvider == null) {
            bpmnDiagramItemProvider = new BpmnDiagramItemProvider(this);
        }

        return bpmnDiagramItemProvider;
    }

    /**
     * This keeps track of the one adapter used for all {@link org.eclipse.stp.bpmn.DataObject} instances.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected DataObjectItemProvider dataObjectItemProvider;

    /**
     * This creates an adapter for a {@link org.eclipse.stp.bpmn.DataObject}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Adapter createDataObjectAdapter() {
        if (dataObjectItemProvider == null) {
            dataObjectItemProvider = new DataObjectItemProvider(this);
        }

        return dataObjectItemProvider;
    }

    /**
     * This keeps track of the one adapter used for all {@link org.eclipse.stp.bpmn.Graph} instances.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	protected GraphItemProvider graphItemProvider;

    /**
     * This creates an adapter for a {@link org.eclipse.stp.bpmn.Graph}.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	@Override
    public Adapter createGraphAdapter() {
        if (graphItemProvider == null) {
            graphItemProvider = new GraphItemProvider(this);
        }

        return graphItemProvider;
    }

    /**
     * This keeps track of the one adapter used for all {@link org.eclipse.stp.bpmn.Group} instances.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected GroupItemProvider groupItemProvider;

    /**
     * This creates an adapter for a {@link org.eclipse.stp.bpmn.Group}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Adapter createGroupAdapter() {
        if (groupItemProvider == null) {
            groupItemProvider = new GroupItemProvider(this);
        }

        return groupItemProvider;
    }

    /**
     * This keeps track of the one adapter used for all {@link org.eclipse.stp.bpmn.Identifiable} instances.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected IdentifiableItemProvider identifiableItemProvider;

    /**
     * This creates an adapter for a {@link org.eclipse.stp.bpmn.Identifiable}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Adapter createIdentifiableAdapter() {
        if (identifiableItemProvider == null) {
            identifiableItemProvider = new IdentifiableItemProvider(this);
        }

        return identifiableItemProvider;
    }

    /**
     * This keeps track of the one adapter used for all {@link org.eclipse.stp.bpmn.Lane} instances.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	protected LaneItemProvider laneItemProvider;

    /**
     * This creates an adapter for a {@link org.eclipse.stp.bpmn.Lane}.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	@Override
    public Adapter createLaneAdapter() {
        if (laneItemProvider == null) {
            laneItemProvider = new LaneItemProvider(this);
        }

        return laneItemProvider;
    }

    /**
     * This keeps track of the one adapter used for all {@link org.eclipse.stp.bpmn.MessageVertex} instances.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected MessageVertexItemProvider messageVertexItemProvider;

    /**
     * This creates an adapter for a {@link org.eclipse.stp.bpmn.MessageVertex}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Adapter createMessageVertexAdapter() {
        if (messageVertexItemProvider == null) {
            messageVertexItemProvider = new MessageVertexItemProvider(this);
        }

        return messageVertexItemProvider;
    }

    /**
     * This keeps track of the one adapter used for all {@link org.eclipse.stp.bpmn.MessagingEdge} instances.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected MessagingEdgeItemProvider messagingEdgeItemProvider;

    /**
     * This creates an adapter for a {@link org.eclipse.stp.bpmn.MessagingEdge}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Adapter createMessagingEdgeAdapter() {
        if (messagingEdgeItemProvider == null) {
            messagingEdgeItemProvider = new MessagingEdgeItemProvider(this);
        }

        return messagingEdgeItemProvider;
    }

    /**
     * This keeps track of the one adapter used for all {@link org.eclipse.stp.bpmn.NamedBpmnObject} instances.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected NamedBpmnObjectItemProvider namedBpmnObjectItemProvider;

    /**
     * This creates an adapter for a {@link org.eclipse.stp.bpmn.NamedBpmnObject}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Adapter createNamedBpmnObjectAdapter() {
        if (namedBpmnObjectItemProvider == null) {
            namedBpmnObjectItemProvider = new NamedBpmnObjectItemProvider(this);
        }

        return namedBpmnObjectItemProvider;
    }

    /**
     * This keeps track of the one adapter used for all {@link org.eclipse.stp.bpmn.Pool} instances.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	protected PoolItemProvider poolItemProvider;

    /**
     * This creates an adapter for a {@link org.eclipse.stp.bpmn.Pool}.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	@Override
    public Adapter createPoolAdapter() {
        if (poolItemProvider == null) {
            poolItemProvider = new PoolItemProvider(this);
        }

        return poolItemProvider;
    }

    /**
     * This keeps track of the one adapter used for all {@link org.eclipse.stp.bpmn.SequenceEdge} instances.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected SequenceEdgeItemProvider sequenceEdgeItemProvider;

    /**
     * This creates an adapter for a {@link org.eclipse.stp.bpmn.SequenceEdge}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Adapter createSequenceEdgeAdapter() {
        if (sequenceEdgeItemProvider == null) {
            sequenceEdgeItemProvider = new SequenceEdgeItemProvider(this);
        }

        return sequenceEdgeItemProvider;
    }

    /**
     * This keeps track of the one adapter used for all {@link org.eclipse.stp.bpmn.SubProcess} instances.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	protected SubProcessItemProvider subProcessItemProvider;

    /**
     * This creates an adapter for a {@link org.eclipse.stp.bpmn.SubProcess}.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	@Override
    public Adapter createSubProcessAdapter() {
        if (subProcessItemProvider == null) {
            subProcessItemProvider = new SubProcessItemProvider(this);
        }

        return subProcessItemProvider;
    }

    /**
     * This keeps track of the one adapter used for all {@link org.eclipse.stp.bpmn.TextAnnotation} instances.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected TextAnnotationItemProvider textAnnotationItemProvider;

    /**
     * This creates an adapter for a {@link org.eclipse.stp.bpmn.TextAnnotation}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Adapter createTextAnnotationAdapter() {
        if (textAnnotationItemProvider == null) {
            textAnnotationItemProvider = new TextAnnotationItemProvider(this);
        }

        return textAnnotationItemProvider;
    }

    /**
     * This keeps track of the one adapter used for all {@link org.eclipse.stp.bpmn.Vertex} instances.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	protected VertexItemProvider vertexItemProvider;

    /**
     * This creates an adapter for a {@link org.eclipse.stp.bpmn.Vertex}.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	@Override
    public Adapter createVertexAdapter() {
        if (vertexItemProvider == null) {
            vertexItemProvider = new VertexItemProvider(this);
        }

        return vertexItemProvider;
    }

    /**
     * This returns the root adapter factory that contains this factory.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public ComposeableAdapterFactory getRootAdapterFactory() {
        return parentAdapterFactory == null ? this : parentAdapterFactory.getRootAdapterFactory();
    }

    /**
     * This sets the composed adapter factory that contains this factory.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setParentAdapterFactory(ComposedAdapterFactory parentAdapterFactory) {
        this.parentAdapterFactory = parentAdapterFactory;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	@Override
    public boolean isFactoryForType(Object type) {
        return supportedTypes.contains(type) || super.isFactoryForType(type);
    }

    /**
     * This implementation substitutes the factory itself as the key for the adapter.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	@Override
    public Adapter adapt(Notifier notifier, Object type) {
        return super.adapt(notifier, this);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	@Override
    public Object adapt(Object object, Object type) {
        if (isFactoryForType(type)) {
            Object adapter = super.adapt(object, type);
            if (!(type instanceof Class) || (((Class<?>)type).isInstance(adapter))) {
                return adapter;
            }
        }

        return null;
    }

    /**
     * This adds a listener.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void addListener(INotifyChangedListener notifyChangedListener) {
        changeNotifier.addListener(notifyChangedListener);
    }

    /**
     * This removes a listener.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void removeListener(INotifyChangedListener notifyChangedListener) {
        changeNotifier.removeListener(notifyChangedListener);
    }

    /**
     * This delegates to {@link #changeNotifier} and to {@link #parentAdapterFactory}.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void fireNotifyChanged(Notification notification) {
        changeNotifier.fireNotifyChanged(notification);

        if (parentAdapterFactory != null) {
            parentAdapterFactory.fireNotifyChanged(notification);
        }
    }

    /**
     * This disposes all of the item providers created by this factory. 
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void dispose() {
        if (activityItemProvider != null) activityItemProvider.dispose();
        if (artifactItemProvider != null) artifactItemProvider.dispose();
        if (artifactsContainerItemProvider != null) artifactsContainerItemProvider.dispose();
        if (associationItemProvider != null) associationItemProvider.dispose();
        if (associationTargetItemProvider != null) associationTargetItemProvider.dispose();
        if (bpmnDiagramItemProvider != null) bpmnDiagramItemProvider.dispose();
        if (dataObjectItemProvider != null) dataObjectItemProvider.dispose();
        if (graphItemProvider != null) graphItemProvider.dispose();
        if (groupItemProvider != null) groupItemProvider.dispose();
        if (identifiableItemProvider != null) identifiableItemProvider.dispose();
        if (laneItemProvider != null) laneItemProvider.dispose();
        if (messageVertexItemProvider != null) messageVertexItemProvider.dispose();
        if (messagingEdgeItemProvider != null) messagingEdgeItemProvider.dispose();
        if (namedBpmnObjectItemProvider != null) namedBpmnObjectItemProvider.dispose();
        if (poolItemProvider != null) poolItemProvider.dispose();
        if (sequenceEdgeItemProvider != null) sequenceEdgeItemProvider.dispose();
        if (subProcessItemProvider != null) subProcessItemProvider.dispose();
        if (textAnnotationItemProvider != null) textAnnotationItemProvider.dispose();
        if (vertexItemProvider != null) vertexItemProvider.dispose();
    }

}
