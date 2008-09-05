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
package org.eclipse.stp.bpmn.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.Request;
import org.eclipse.gef.EditPartViewer.Conditional;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IPrimaryEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ShapeEditPart;
import org.eclipse.gmf.runtime.diagram.ui.handles.ConnectionHandle;
import org.eclipse.gmf.runtime.diagram.ui.handles.ConnectionHandleLocator;
import org.eclipse.gmf.runtime.diagram.ui.internal.tools.ConnectionHandleTool;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateUnspecifiedTypeConnectionRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.RequestConstants;
import org.eclipse.gmf.runtime.diagram.ui.util.INotationType;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.gmf.runtime.emf.ui.services.modelingassistant.ModelingAssistantService;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.stp.bpmn.diagram.edit.parts.Group2EditPart;
import org.eclipse.stp.bpmn.diagram.edit.parts.GroupEditPart;
import org.eclipse.stp.bpmn.diagram.edit.parts.SubProcessEditPart;
import org.eclipse.stp.bpmn.diagram.edit.parts.SubProcessSubProcessBodyCompartmentEditPart;
import org.eclipse.stp.bpmn.diagram.part.BpmnVisualIDRegistry;
import org.eclipse.stp.bpmn.diagram.providers.BpmnElementTypes;
import org.eclipse.stp.bpmn.handles.ConnectionHandleForAssociation;
import org.eclipse.swt.widgets.Display;

/**
 * Avoid direct edit at the end of the creation of the connection.
 * Prevent sequence connection from being created when they cross the boundary 
 * of a sub-process.
 * 
 * @author hmalphettes extends the internal tool to avoid to perform a direct edit
 * at the end of the creation. We could make it a preference.
 * @author <a href="http://www.intalio.com">&copy; Intalio, Inc.</a>
 */
public class ConnectionHandleToolEx extends ConnectionHandleTool {

    /**
     * @param connectionHandle
     */
    public ConnectionHandleToolEx(ConnectionHandle connectionHandle) {
        super(connectionHandle);
    }

    /**
     * Override to filter the rel-types provided by the assistant service that
     * are the "attach-note" type of connection.
     * 
     * @see org.eclipse.gef.tools.TargetingTool#createTargetRequest()
     */
    @SuppressWarnings("unchecked") //$NON-NLS-1$
    protected Request createTargetRequest() {
        List<IElementType> relTypes = null;
        ConnectionHandle connHandle = getConnectionHandle();
        if (connHandle.isIncoming()) {
            //reversed direction.
            relTypes = ModelingAssistantService.getInstance().getRelTypesOnTarget(
                    connHandle.getOwner());
        } else {
            relTypes = ModelingAssistantService.getInstance().
                getRelTypesOnSource(connHandle.getOwner());
        }
        int borderSide = ((ConnectionHandleLocator)connHandle.
                getLocator()).getBorderSide();
        if (connHandle instanceof ConnectionHandleForAssociation) {
            setRelationTypesForAssociation(
                    (ConnectionHandleForAssociation)connHandle,
                            relTypes, borderSide);
        } else {
            relTypes.remove(BpmnElementTypes.Association_3003);
            List<IElementType> toRemove = new ArrayList<IElementType>();
            for (IElementType eltType : relTypes) {
                if (BpmnElementTypes.Association_3003.getId().equals(eltType.getId())) {
                    toRemove.add(eltType);
                }
            }
            relTypes.removeAll(toRemove);
            switch (borderSide) {
            case PositionConstants.SOUTH:
            case PositionConstants.NORTH:
                relTypes.remove(BpmnElementTypes.SequenceEdge_3001);
                break;
            case PositionConstants.WEST:
            case PositionConstants.EAST:
                relTypes.remove(BpmnElementTypes.MessagingEdge_3002);
                break;
            }
        }
        //this replaces : relTypes.remove(DiagramNotationType.NOTE_ATTACHMENT);
        for (ListIterator<IElementType> it = relTypes.listIterator();
                    it.hasNext(); ) {
            IElementType elem = it.next();
            if (elem instanceof INotationType) {
                it.remove();
            }
        }

        CreateUnspecifiedTypeConnectionRequest request = 
            new CreateUnspecifiedTypeConnectionRequest(
                    relTypes, useModelingAssistantService(), getPreferencesHint()) {
            
            public void setTargetEditPart(EditPart part) {
                if (part instanceof SubProcessEditPart) {
                    if (getSourceEditPart() != null && 
                            ((IGraphicalEditPart) part).resolveSemanticElement().
                            equals(((IGraphicalEditPart) getSourceEditPart().
                                    getParent()).resolveSemanticElement())) {
                        //in this case we are creating a connection between a task within a compartment and
                        //an undefined task within the same compartment:
                        //the targeted editpart is the sub-process compartment body.
                        SubProcessEditPart subPro = (SubProcessEditPart)part;
                        EditPart bodyCompartmentEditPart = subPro.getChildBySemanticHint(
                                BpmnVisualIDRegistry .getType(
                                        SubProcessSubProcessBodyCompartmentEditPart.VISUAL_ID));
                        super.setTargetEditPart(bodyCompartmentEditPart);
                        return;
                    }
                }
                super.setTargetEditPart(part);
            }};
            
            if (connHandle.isIncoming()) {
                request.setDirectionReversed(true);
            }
            return request;
    }
    
    /**
     * When creating a CreateUnspecifiedTypeConnectionRequest
     * Should the modeling assistant be used?
     * @return false
     */
    protected boolean useModelingAssistantService() {
        return false;
    }

    /**
     * Removes and add relationship types when the connection handles specifies
     * that the type of connection being created is only for bpmn associations.
     * 
     * In this implementation, it simply removes the message and 
     * sequence relationships.
     * 
     * @param connHandle
     */
    protected void setRelationTypesForAssociation(ConnectionHandleForAssociation connHandle,
            List<IElementType> relationTypesCollector, int borderSide) {
        // removing those element types when we drag an association
        relationTypesCollector.remove(BpmnElementTypes.MessagingEdge_3002);
        relationTypesCollector.remove(BpmnElementTypes.SequenceEdge_3001);        
    }
    
    
    /**
     * Modified so that only the shapes are put in
     * direct mode when being created with a connection.
     */
    protected void selectAddedObject(EditPartViewer viewer, Collection objects) {
        final EditPart[] primaryEP = new EditPart[1];
        List editparts = new ArrayList();
        for (Iterator i = objects.iterator(); i.hasNext();) {
            Object object = i.next();
            if (object instanceof IAdaptable) {
                Object editPart = viewer.getEditPartRegistry().get(
                    ((IAdaptable) object).getAdapter(View.class));

                if (editPart instanceof IPrimaryEditPart) {
                    editparts.add(editPart);
                }
                // Priority is to put a shape into direct edit mode.
                if (editPart instanceof ShapeEditPart) {
                    primaryEP[0] = (ShapeEditPart) editPart;
                }
            }
        }

        if (primaryEP[0] != null) {
            viewer.setSelection(new StructuredSelection(editparts));

            // automatically put the first shape into edit-mode
            Display.getCurrent().asyncExec(new Runnable() {

                public void run() {
                    //
                    // add active test since test scripts are failing on this
                    // basically, the editpart has been deleted when this
                    // code is being executed. (see RATLC00527114)
                    if (primaryEP[0].isActive()) {
                        primaryEP[0].performRequest(new Request(
                            RequestConstants.REQ_DIRECT_EDIT));
                    }
                }
            });
        }
    }
    
    /**
     * Overridden for groups. We only select them when the cursor
     * is placed at less than 5 pixels from the border of the groups,
     * otherwise the parent of the group is selected.
     */
    @Override
    protected Conditional getTargetingConditional() {
        return new EditPartViewer.Conditional() {
            public boolean evaluate(EditPart editpart) {
                if (editpart instanceof GroupEditPart || 
                        editpart instanceof Group2EditPart) {
                    IGraphicalEditPart part = (IGraphicalEditPart) editpart;
                    Rectangle rect = part.getFigure().getBounds().getCopy();
                    Point loc = getLocation().getCopy();
                    part.getFigure().translateToRelative(loc);
                    boolean onX = Math.abs(loc.x - rect.x) < 5 ||
                        Math.abs(loc.x - (rect.x + rect.width)) < 5;
                    boolean onY = Math.abs(loc.y - rect.y) < 5 ||
                        Math.abs(loc.y - (rect.y + rect.height)) < 5;
                    if (!(onX || onY)) {
                        return false;
                    }
                }
                
                return editpart.isSelectable();
            }
        };
    }
}
