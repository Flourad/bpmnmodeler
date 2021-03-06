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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gmf.runtime.emf.clipboard.core.ClipboardSupportUtil;
import org.eclipse.gmf.runtime.emf.clipboard.core.ObjectInfo;
import org.eclipse.gmf.runtime.emf.clipboard.core.PasteChildOperation;
import org.eclipse.gmf.runtime.emf.clipboard.core.PostPasteChildOperation;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.Edge;
import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.stp.bpmn.Identifiable;

/**
 * [hmalphettes] as advised in GMF Q&amp;answers regarding copy/paste support.
 * we had to fork the original ConnectorViewPasteOperation 
 * just so tht we can call our very own methods...
 * Why did calling static methods on NotationHelperCliboard?
 * 
 * @author Yasser Lulu
 * @author <a href="http://www.intalio.com">&copy; Intalio, Inc.</a>
 */
public class BpmnConnectorViewPostPasteChildOperation 
    extends PostPasteChildOperation {

    private boolean pasteSemanticElement;

    private List semanticElementPasteOperations;

    BpmnConnectorViewPostPasteChildOperation(
            BpmnConnectorViewPasteOperation connectorViewPasteOperation,
            boolean pasteSemanticElement) {
        super(connectorViewPasteOperation, EMPTY_ARRAY);
        this.pasteSemanticElement = pasteSemanticElement;
    }

    private BpmnConnectorViewPasteOperation getConnectorViewPasteOperation() {
        return (BpmnConnectorViewPasteOperation) getPasteChildOperation();
    }

    public void paste()
        throws Exception {

        EObject pasted = ((View) getEObject()).getElement();
        if (pasted instanceof Identifiable) {
            ((Identifiable) pasted).setID(EcoreUtil.generateUUID());
        }
        if (pasted instanceof EObject) {
            TreeIterator<EObject> iter = ((EObject) pasted).eAllContents();
            while (iter.hasNext()) {
                EObject next = iter.next();
                if (next instanceof Identifiable) {
                    Identifiable elt = (Identifiable) next;
                    elt.setID(EcoreUtil.generateUUID());
                }
            }

        }
        EObject pastedElement = doPaste();
        //did we succeed?
        if (pastedElement != null) {
            setPastedElement(pastedElement);
            addPastedElement(pastedElement);
        } else {
            addPasteFailuresObject(getEObject());
        }

    }


    /**
     * @return
     * @throws Exception
     */
    private EObject doPaste()
        throws Exception {

        View sourceView = getConnectorViewPasteOperation().getSourceView();
        View targetView = getConnectorViewPasteOperation().getTargetView();

        if ((sourceView == null) || (targetView == null)) {
            return null;
        }

        EObject sourceViewContainer = sourceView.eContainer();
        EObject targetViewContainer = targetView.eContainer();

        if ((sourceViewContainer == null) || (targetViewContainer == null)) {
            return null;
        }

        if (sourceViewContainer.equals(targetViewContainer) == false) {
            //not in the same container, let's try to see if they are in the
            // same diagram at least
            Diagram sourceViewDiagram = BpmnClipboardSupport
                .getContainingDiagram((View) sourceViewContainer);
            Diagram targetViewDiagram = BpmnClipboardSupport
                .getContainingDiagram((View) targetViewContainer);
            if ((sourceViewDiagram == null) || (targetViewDiagram == null)
                || (sourceViewDiagram.equals(targetViewDiagram) == false)) {
                return null;
            }
        }
        
        Edge connectorView = getConnectorViewPasteOperation()
            .getConnectorView();

        if (pasteSemanticElement) {         
            EObject semanticElement = connectorView.getElement();
            if (semanticElement != null) {
                if (semanticElement.eIsProxy()) {
                    semanticElement = ClipboardSupportUtil.resolve(semanticElement,
                        getParentPasteProcess().getLoadedIDToEObjectMapCopy());
                }
                String loadedId = getLoadedEObjectID(semanticElement);
                if (loadedId != null) {
                    //even if we failed to paste the semantic element, we'll
                    // proceed to paste the edge view
                    doPasteSemanticElement();
                    //should have been pasted by now, if not then return
                    // well this was provoking a very offending bug 169863
//                    String newId = getEObjectID(semanticElement);
//                    if (newId == null) {
//                        return null;
//                    }
                }
            }
        }
        EObject pastedElement = null;
        Diagram pasteTargetDiagram = BpmnClipboardSupport
            .getContainingDiagram((View) sourceViewContainer);
        if (pasteTargetDiagram != null) {
            //if we reached here then we should paste the connector and set
            // refs to it accordingly
            pastedElement = ClipboardSupportUtil.appendEObjectAt(
                pasteTargetDiagram, getContainmentFeature(), connectorView);
            if (pastedElement != null) {
                ClipboardSupportUtil.appendEObjectAt(sourceView,
                    NotationPackage.eINSTANCE.getView_SourceEdges(),
                    connectorView);
                ClipboardSupportUtil.appendEObjectAt(targetView,
                    NotationPackage.eINSTANCE.getView_TargetEdges(),
                    connectorView);
            }
        }

        return pastedElement;
    }

    private void doPasteSemanticElement()
        throws Exception {
        List list = new ArrayList();
        Iterator it = getSemanticElementPasteOperations().iterator();
        while (it.hasNext()) {
            PasteChildOperation operation = (PasteChildOperation) it.next();
            operation.paste();
            PasteChildOperation postPasteOperation = operation
                .getPostPasteOperation();
            if (postPasteOperation != null) {
                list.add(postPasteOperation);
            }
        }
        performSemanticElementPostPasteOperations(list);
    }

    private void performSemanticElementPostPasteOperations(List operations)
        throws Exception {
        if (operations.isEmpty() == false) {
            List postPasteOperations = new ArrayList();
            Iterator it = operations.iterator();
            while (it.hasNext()) {
                PasteChildOperation pasteOperation = (PasteChildOperation) it
                    .next();
                pasteOperation.paste();
                PasteChildOperation postPasteOperation = pasteOperation
                    .getPostPasteOperation();
                if (postPasteOperation != null) {
                    postPasteOperations.add(postPasteOperation);
                }
            }
            //perform those newly added post paste operations
            performSemanticElementPostPasteOperations(postPasteOperations);
        }
    }

    protected List getPostPasteOperations()
        throws Exception {
        return Collections.EMPTY_LIST;
    }

    /**
     * @return
     */
    private List getSemanticElementPasteOperations() {
        if (semanticElementPasteOperations == null) {
            semanticElementPasteOperations = new ArrayList();
            //use either source or target views since by now they should have
            // been pasted already
            EObject semanticPasteTarget = BpmnClipboardSupport
                .getSemanticPasteTarget(getConnectorViewPasteOperation()
                    .getSourceView());
            if (semanticPasteTarget != null) {
                Iterator alwaysCopyEObjectInfoIt = getChildObjectInfo()
                    .getCopyAlwaysObjectInfoList().iterator();
                while (alwaysCopyEObjectInfoIt.hasNext()) {
                    ObjectInfo alwaysCopyObjectInfo = (ObjectInfo) alwaysCopyEObjectInfoIt
                        .next();
                    PasteChildOperation pasteOperation = new PasteChildOperation(
                        getParentPasteProcess().clone(semanticPasteTarget),
                        alwaysCopyObjectInfo);
                    semanticElementPasteOperations.add(pasteOperation);
                } //while always-copy
            }
        }
        return semanticElementPasteOperations;
    }

}