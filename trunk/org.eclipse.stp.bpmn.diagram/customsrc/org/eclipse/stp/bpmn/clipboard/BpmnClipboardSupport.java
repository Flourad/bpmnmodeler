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

/**
 * Date             Author              Changes
 * Jul 17, 2006     hmalphettes         Created
 **/

package org.eclipse.stp.bpmn.clipboard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.edit.command.DeleteCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gmf.runtime.emf.clipboard.core.ClipboardSupportUtil;
import org.eclipse.gmf.runtime.emf.clipboard.core.CopyOperation;
import org.eclipse.gmf.runtime.emf.clipboard.core.OverrideCopyOperation;
import org.eclipse.gmf.runtime.emf.clipboard.core.OverridePasteChildOperation;
import org.eclipse.gmf.runtime.emf.clipboard.core.PasteAction;
import org.eclipse.gmf.runtime.emf.clipboard.core.PasteChildOperation;
import org.eclipse.gmf.runtime.emf.clipboard.core.PasteOption;
import org.eclipse.gmf.runtime.emf.core.clipboard.AbstractClipboardSupport;
import org.eclipse.gmf.runtime.emf.core.util.EMFCoreUtil;
import org.eclipse.gmf.runtime.emf.type.core.commands.DestroyElementCommand;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.Edge;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.stp.bpmn.Activity;
import org.eclipse.stp.bpmn.Artifact;
import org.eclipse.stp.bpmn.Association;
import org.eclipse.stp.bpmn.BpmnPackage;
import org.eclipse.stp.bpmn.Graph;
import org.eclipse.stp.bpmn.Identifiable;
import org.eclipse.stp.bpmn.Lane;
import org.eclipse.stp.bpmn.MessagingEdge;
import org.eclipse.stp.bpmn.Pool;
import org.eclipse.stp.bpmn.SequenceEdge;
import org.eclipse.stp.bpmn.SubProcess;
import org.eclipse.stp.bpmn.Vertex;
import org.eclipse.stp.bpmn.diagram.BpmnDiagramMessages;

/**
 * Following the advice of the Q&amp;A on GMF wiki.
 * This is a fork of the NotationClipboardOperationHelper.
 * 
 * @author hmalphettes
 * @author <a href="http://www.intalio.com">&copy; Intalio, Inc.</a>
 */
public class BpmnClipboardSupport extends AbstractClipboardSupport {

    public BpmnClipboardSupport() {
    }

    public void destroy(EObject eObject) {
        DestroyElementCommand.destroy(eObject);
    }
    
    /**
     * By default, there are no collisions in pasting.
     * 
     * @return the {@link PasteAction#ADD}action, always
     */
    public PasteAction getPasteCollisionAction(EClass eClass) {
        return PasteAction.ADD;
    }

    /**
     * By default, the following paste options are supported:
     * <ul>
     * <li>{@link PasteOption#NORMAL}: always</li>
     * <li>{@link PasteOption#PARENT}: never</li>
     * <li>{@link PasteOption#DISTANT}: if and only only if the
     * <code>eStructuralFeature</code> is a
     * {@link org.eclipse.gmf.runtime.notation.View}'s reference to its semantic
     * {@linkplain org.eclipse.gmf.runtime.notation.View#getElement() element}</li>
     * </ul>
     */
    public boolean hasPasteOption(EObject contextEObject,
            EStructuralFeature eStructuralFeature, PasteOption pasteOption) {
//        System.err.println("HasPasteOption is called ctxt=" + contextEObject + 
//                "   eStructuralFeature=" + eStructuralFeature + "   option=" + pasteOption);
        if (pasteOption.equals(PasteOption.NORMAL)) {
            return true;
        } else if (pasteOption.equals(PasteOption.PARENT)) {
            //disable the copy-parent functionality completely.
            return false;
        } else if (pasteOption.equals(PasteOption.DISTANT)) {
            if (eStructuralFeature == null) {
                return false;
            } else {
                return NotationPackage.eINSTANCE.getView_Element().equals(
                    eStructuralFeature);
            }
        } else {
            return false;
        }
    }

    /**
     * By default, transient and derived references are never copied, and
     * containment references and the
     * {@linkplain org.eclipse.gmf.runtime.notation.View#getElement() element}reference
     * always are copied.
     */
    public boolean isCopyAlways(EObject context, EReference eReference,
            Object value) {
        if ((eReference.isTransient()) || (eReference.isDerived())) {
            return false;
        } else if (eReference.equals(NotationPackage.eINSTANCE
                        .getView_Element())) {
            return true;
        } else if (context instanceof Identifiable) {
            if (eReference.isContainment()) {
                return true;
            }
            //probably some special things to do for
            //messaging edges and sequence edges
            return false;
        } else {
            return eReference.isContainment();
        }
    }

    /**
     * By default, don't provide any child paste override behaviour.
     */
    public boolean shouldOverrideChildPasteOperation(EObject parentElement,
            EObject childEObject) {
        return (childEObject.eClass().getEPackage() == NotationPackage.eINSTANCE);
    }

    /**
     * By default, don't provide any copy override behaviour.
     */
    public boolean shouldOverrideCopyOperation(Collection eObjects, Map hintMap) {
        return false;
    }

    private boolean shouldAllowPaste(
            PasteChildOperation overriddenChildPasteOperation) {
        EObject eObject = overriddenChildPasteOperation.getEObject();
        EObject parentEObject = overriddenChildPasteOperation
            .getParentEObject();
        
        //support for pasting into pool compartment and sub-process body compartment
        //and sub-process event handlers compartments.
        if (eObject instanceof View && parentEObject instanceof Node) {
            Node parentNode = (Node)parentEObject;
            EObject semanticChildElement = ((View) eObject).getElement();
            
            //TODO: somehing better if possible using pure EMF concepts (?):
            //in theory the BPMN schema describes well enough the containments constraints
            //so it should be possible.
            if (semanticChildElement instanceof Pool) {
                //a pool can only be pasted inside a Diagram
                return false;
            }
            
            // forbidden for now as the parent references get messy.
            if (semanticChildElement instanceof Activity && 
                    ((Activity) semanticChildElement).getEventHandlerFor() != null) {
                if (parentNode.getElement() instanceof SubProcess) {
                    return false;
                }
            }
            if (eObject instanceof MessagingEdge) {
                return false;
            }
            if (eObject instanceof SequenceEdge) {
            	if (!overriddenChildPasteOperation.getAllPastedElementSet().
            			contains(((SequenceEdge) eObject).getSource())) {
            		return false;
            	}
            	if (!overriddenChildPasteOperation.getAllPastedElementSet().
            			contains(((SequenceEdge) eObject).getTarget())) {
            		return false;
            	}
            }
            EObject semanticParent = parentNode.getElement();
            if (semanticChildElement instanceof Lane && 
                    !(semanticParent instanceof Pool)) {
                //a lane can only be pasted inside a Pool
                return false;
            }
            if (!(semanticParent instanceof Pool 
                    || semanticParent instanceof SubProcess)) {
                //can only paste into a pool and into a sub-process
                return false;
            }
            return true;
        }
        if ((parentEObject instanceof Diagram) && (eObject instanceof View)) {
            EObject semanticChildElement = ((View) eObject).getElement();
            if (semanticChildElement == null) {
                return true;
            }
            if (semanticChildElement.eIsProxy()) {
                semanticChildElement = ClipboardSupportUtil.resolve(
                    semanticChildElement, overriddenChildPasteOperation
                        .getParentPasteProcess().getLoadedIDToEObjectMapCopy());
                if (semanticChildElement.eIsProxy()) {
                    semanticChildElement = EcoreUtil.resolve(
                        semanticChildElement, getResource(parentEObject));
                }
            }
            if (!(semanticChildElement instanceof Pool)) {
                return false;
            }

            EPackage semanticChildEpackage = semanticChildElement.eClass()
                .getEPackage();
            EPackage diagramRootContainerEpackage = EcoreUtil.getRootContainer(
                parentEObject).eClass().getEPackage();
            EPackage sematicDiagramRootContainerEpackage = null;
            EObject sematicDiagramElement = ((View) parentEObject).getElement();
            if (sematicDiagramElement != null) {
                sematicDiagramRootContainerEpackage = EcoreUtil
                    .getRootContainer(sematicDiagramElement).eClass()
                    .getEPackage();
            }

            if (diagramRootContainerEpackage != NotationPackage.eINSTANCE) {
                if (semanticChildEpackage != diagramRootContainerEpackage) {
                    return false;
                }
            }

            if ((sematicDiagramRootContainerEpackage != null)
                && (sematicDiagramRootContainerEpackage != NotationPackage.eINSTANCE)) {
                if (semanticChildEpackage != sematicDiagramRootContainerEpackage) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * By default, don't provide any child paste override behaviour.
     * 
     * @return <code>null</code>
     */
    public OverridePasteChildOperation getOverrideChildPasteOperation(
            PasteChildOperation overriddenChildPasteOperation) {
        if (shouldAllowPaste(overriddenChildPasteOperation)) {
            EObject eObject = overriddenChildPasteOperation.getEObject();
            if (eObject instanceof Node) {
                Node node = (Node) eObject;
                EObject element = node.getElement();
                if ((element != null)) {
                    return new BpmnPositionalGeneralViewPasteOperation(
                        overriddenChildPasteOperation, true);
                } else {
                    return new BpmnPositionalGeneralViewPasteOperation(
                        overriddenChildPasteOperation, false);
                }
                
            } else if (eObject instanceof Edge) {
                return new BpmnConnectorViewPasteOperation(
                    overriddenChildPasteOperation);
            }
        }
        return null;
    }

    /**
     * By default, don't provide any copy override behaviour.
     * 
     * @return <code>null</code>, always
     */
    public OverrideCopyOperation getOverrideCopyOperation(
            CopyOperation overriddenCopyOperation) {
        return null;
    }

    /**
     * Exclude the connections that source and target are not included in 
     * this set: basically excludes the connections.
     * 
     * @return the set of edges found in here.
     */
    public Collection getExcludedCopyObjects(Set eObjects) {
        Set<Object> toRemove = new HashSet<Object>();
        for (Object elt : eObjects) {
            Object e = elt;
            if (e instanceof View) {
                e = ((View) e).getElement();
            }
            if (e instanceof MessagingEdge) {
                toRemove.add(elt);
            }
            if (e instanceof SequenceEdge) {
            	if (!eObjects.contains(((SequenceEdge) e).getSource())) {
            		toRemove.add(elt);
            	}
            	if (!eObjects.contains(((SequenceEdge) e).getTarget())) {
            		toRemove.add(elt);
            	}
            }
            if (e instanceof Association) {
                if (!eObjects.contains(((Association) e).getSource())) {
                    toRemove.add(elt);
                }
                if (!eObjects.contains(((Association) e).getTarget())) {
                    toRemove.add(elt);
                }
            }
            // the artifacts contain the associations.
            // make sure the associations are not copied
            // by mistake
            if (e instanceof Artifact) {
                for (Association association : ((Artifact) e).getAssociations()) {
                    if (association instanceof Association) {
                        if (!eObjects.contains(((Association) association).getSource())) {
                            toRemove.add(association);
                        }
                        if (!eObjects.contains(((Association) association).getTarget())) {
                            toRemove.add(association);
                        }
                    }
                }
            }
            // forbidden for now.
            if (e instanceof Activity && ((Activity) e).getEventHandlerFor() != null) {
                toRemove.add(elt);
                toRemove.add(e);
                toRemove.addAll(((Activity) e).getOrderedMessages());
                toRemove.addAll(((Activity) e).getOutgoingEdges());
                toRemove.addAll(((Activity) e).getIncomingEdges());
            }
        }
        return toRemove;
//        return Collections.EMPTY_SET;
    }

    /**
     * By default, just get the resource that contains the object.
     */
    public XMLResource getResource(EObject eObject) {
        XMLResource eResource = (XMLResource) eObject.eResource();
        if (eResource == null) {
            if (eObject instanceof View) {
                EObject element = ((View) eObject).getElement();
                if ((element != null)) {
                    return (XMLResource) element.eResource();
                }
            }
        }
        return eResource;
    }

    /**
     * By default, we always copy all contents of an object.
     * 
     * @return <code>true</code>
     */
    public boolean shouldSaveContainmentFeature(EObject eObj) {
        if (EcorePackage.eINSTANCE.getEClassifiers().contains(eObj.eClass())) {
            return false;
        }
        try {
            eObj.eResource().getURIFragment(eObj);
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    /**
     * Makes sure that the pasted objects don't have invalid references.
     */
    public void performPostPasteProcessing(Set pastedEObjects) {
    	if (pastedEObjects.isEmpty()) {
    		return;
    	}
    	CompoundCommand command = new CompoundCommand(
    	BpmnDiagramMessages.BpmnClipboardSupport_PostPaste);
    	TransactionalEditingDomain domain = 
    		(TransactionalEditingDomain) AdapterFactoryEditingDomain.
    		getEditingDomainFor(pastedEObjects.iterator().next());

    	for (Object o : pastedEObjects) {
    		EObject elt = null;
    		if (o instanceof View) {
    			elt = ((View)o).getElement();
    		} else if (o instanceof EObject) {
    			elt = (EObject) o;
    		}
    		if (elt instanceof Vertex) {
    			final Vertex vertex = (Vertex) elt;
    			for (Object edge : vertex.getOutgoingEdges()) {
    				final SequenceEdge se = (SequenceEdge) edge;
    				boolean delete = false;
    				boolean disconnect = false;
    				if (!vertex.equals(se.getSource())) {
    					// the pasted activity kept an invalid reference
    					disconnect = true;
    				}
    				if (se.getTarget() == null) {
    					// the sequence edge doesn't refer to anything
    					delete = true;
    				}
    				// add more reasons to disconnect or delete
    				// if you like !


    				if (delete) {
    					command.append(DeleteCommand.create(domain, se));
    				} else if(disconnect) {
    					command.append(
    							new RecordingCommand(domain,
    									BpmnDiagramMessages.BpmnClipboardSupport_SequenceEdgeDisconnect) 
    							{
    								@SuppressWarnings("unchecked") //$NON-NLS-1$
    								@Override
    								protected void doExecute() {
    									Object source = se.getSource();
    									List vertices = new ArrayList(
    											vertex.getOutgoingEdges());
    									vertices.remove(se);
    									vertex.eSet(
    											BpmnPackage.eINSTANCE.getVertex_OutgoingEdges(),
    											vertices);
    									se.eSet(
    											BpmnPackage.eINSTANCE.getSequenceEdge_Source(),
    											source);

    								}});
    				}
    			}
    			command.execute();
    			
    			command = new CompoundCommand(
    			BpmnDiagramMessages.BpmnClipboardSupport_PostPaste);
    			for (Object edge : vertex.getIncomingEdges()) {
    				final SequenceEdge se = (SequenceEdge) edge;
    				boolean delete = false;
    				boolean disconnect = false;
    				if (!vertex.equals(se.getTarget())) {
    					// the pasted activity kept an invalid reference
    					disconnect = true;
    				}
    				if (se.getSource() == null) {
    					// the sequence edge doesn't refer to anything
    					delete = true;
    				}
    				// add more reasons to disconnect or delete
    				// if you like !


    				if (delete) {
    					command.append(DeleteCommand.create(domain, se));
    				} else if(disconnect) {
    					command.append(
    							new RecordingCommand(domain,
    									BpmnDiagramMessages.BpmnClipboardSupport_SequenceEdgeDisconnect) 
    							{

    								@SuppressWarnings("unchecked") //$NON-NLS-1$
    								@Override
    								protected void doExecute() {
    									Object target = se.getTarget();
    									List vertices = new ArrayList(
    											vertex.getIncomingEdges());
    									vertices.remove(se);
    									vertex.eSet(
    											BpmnPackage.eINSTANCE.getVertex_IncomingEdges(),
    											vertices);
    									se.eSet(
    											BpmnPackage.eINSTANCE.getSequenceEdge_Target(),
    											target);

    								}});
    				}
    			}
    			command.execute();
    			
    			command = new CompoundCommand(BpmnDiagramMessages.BpmnClipboardSupport_unresolvedAssociations);
                for (final Association association : vertex.getAssociations()) {
                    boolean delete = false;
                    if (association.getSource() == null) {
                        // the association doesn't refer to anything
                        delete = true;
                    }
                    // add more reasons to disconnect or delete
                    // if you like !


                    if (delete) {
                        command.append(DeleteCommand.create(domain, association));
                    }
                }
                command.execute();
    			command = new CompoundCommand(
    			BpmnDiagramMessages.BpmnClipboardSupport_PostPaste);
    		}

    		// process the messaging edges with
    		// invalid references.
    		processMessagingEdges(domain, elt);

    	}

    	for (final Object elt : pastedEObjects) {
    		if (elt instanceof Identifiable) {
    			command.appendAndExecute(
    					new RecordingCommand(
    							domain,BpmnDiagramMessages.BpmnClipboardSupport_createID) {

    						@Override
    						protected void doExecute() {
    							((Identifiable)elt).setID(EcoreUtil.generateUUID());

    						}});
    		}
    	}
//  	for (Object co : command.getCommandList()) {
//  	domain.getCommandStack().execute((Command) co);
//        }
//        domain.getCommandStack().execute(command);
        
    }
    
    /**
     * Process recursively the activity.
     * If the activity happens to be a graph, it will go recursively
     * see its children.
     * @param elt
     */
    private void processMessagingEdges(TransactionalEditingDomain domain, 
    		EObject elt) {
    	CompoundCommand command = new CompoundCommand(
			BpmnDiagramMessages.BpmnClipboardSupport_PostPaste);
    	if (elt instanceof Activity) {
    		final Activity act = (Activity) elt;
    		for (Object edge : act.getOutgoingMessages()) {
    			if (edge instanceof MessagingEdge) {
    				final MessagingEdge me = (MessagingEdge) edge;
    				if (me.getTarget() == null) {
    					command.append(DeleteCommand.create(domain, me));
    				} else if (!act.equals(me.getSource())) {
    					command.append(
    							new RecordingCommand(domain,
    									BpmnDiagramMessages.BpmnClipboardSupport_MessagingEdgeDisconnect) 
    							{

    								@SuppressWarnings("unchecked") //$NON-NLS-1$
    								@Override
    								protected void doExecute() {
    									Object source = me.getSource();
    									List messages = new ArrayList(
    											act.getOutgoingMessages());
    									messages.remove(me);
    									act.eSet(
    											BpmnPackage.eINSTANCE.getMessageVertex_OutgoingMessages(),
    											messages);
//  									List ordered = new ArrayList(act.getOrderedMessages());
//  									ordered.remove(me);
//  									act.eSet(
//  									BpmnPackage.eINSTANCE.getActivity_OrderedMessages(),
//  									ordered);
    									me.eSet(
    											BpmnPackage.eINSTANCE.getMessagingEdge_Source(),
    											source);

    								}});
    				}
    			}
    		}
    		command.execute();
    		command = new CompoundCommand(
    						BpmnDiagramMessages.BpmnClipboardSupport_PostPaste);
    		for (Object edge : act.getIncomingMessages()) {
    			if (edge instanceof MessagingEdge) {
    				final MessagingEdge me = (MessagingEdge) edge;
    				if (me.getSource() == null) {
    					command.append(DeleteCommand.create(domain, me));
    				} else if (!act.equals(me.getTarget())) {
    					command.append(
    							new RecordingCommand(domain,
    									BpmnDiagramMessages.BpmnClipboardSupport_MessagingEdgeDisconnect) 
    							{

    								@SuppressWarnings("unchecked") //$NON-NLS-1$
    								@Override
    								protected void doExecute() {
    									Object target = me.getTarget();
    									List messages = new ArrayList(
    											act.getIncomingMessages());
    									messages.remove(me);
    									act.eSet(
    											BpmnPackage.eINSTANCE.getMessageVertex_IncomingMessages(),
    											messages);
//  									List ordered = new ArrayList(act.getOrderedMessages());
//  									ordered.remove(me);
//  									act.eSet(
//  									BpmnPackage.eINSTANCE.getActivity_OrderedMessages(),
//  									ordered);
    									me.eSet(
    											BpmnPackage.eINSTANCE.getMessagingEdge_Target(),
    											target);

    								}});
    				}
    			}
    		}
    		command.execute();
    		command = new CompoundCommand(
    					BpmnDiagramMessages.BpmnClipboardSupport_PostPaste);
    	}
    	
    	if (elt instanceof Graph) {
    		for (Object vertex : ((Graph) elt).getVertices()) {
    			processMessagingEdges(domain, (EObject) vertex);
    		}
    	}
    }

	//[hugues] the name for the clipboard is the uuid.
//by default it will consider that the uuid is the 'name' eattribute
    @Override
    public String getName(EObject eObject) {
    	return EMFCoreUtil.getProxyID(eObject);
//        if (eObject instanceof Identifiable) {
//            return ((Identifiable)eObject).getID();
//        }
    }

    @Override
    public void setName(EObject eObject, String name) {
    	return; // do nothing.
//        if (eObject instanceof Identifiable) {
//            ((Identifiable)eObject).setID(EcoreUtil.generateUUID());
//        } else {
//            super.setName(eObject, name);
//        }
    }

    static Diagram getContainingDiagram(View view) {
        EObject current = view;
        while (current != null) {
            if (current instanceof Diagram) {
                return (Diagram) current;
            }
            current = current.eContainer();
        }
        return null;
    }

    static EObject getSemanticPasteTarget(View view) {
        View parent = (View) view.eContainer();
        return parent.getElement();
    }

}