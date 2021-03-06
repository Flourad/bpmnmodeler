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

package org.eclipse.stp.bpmn.preferences;

import org.eclipse.gmf.runtime.diagram.ui.preferences.AppearancePreferencePage;
import org.eclipse.gmf.runtime.diagram.ui.preferences.IPreferenceConstants;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.stp.bpmn.diagram.BpmnDiagramMessages;
import org.eclipse.stp.bpmn.diagram.part.BpmnDiagramEditorPlugin;
import org.eclipse.stp.bpmn.diagram.part.BpmnDiagramPreferenceInitializer;
import org.eclipse.swt.widgets.Composite;

/**
 * Preference page. Follow the tutorial here:
 * <link>http://help.eclipse.org/help32/topic/org.eclipse.gmf.doc/tutorials/diagram/diagramPreferencesTutorial.html</link>
 * 
 * @author hmalphettes
 * @author <a href="http://www.intalio.com">&copy; Intalio, Inc.</a>
 */
public class BpmnAppearancePreferencePage extends AppearancePreferencePage {
    
    /** the preference key used to store the default color for a pool */
    public static String PREF_POOL_DEFAULT_FILL_COLOR =
        BpmnDiagramPreferenceInitializer.PREF_POOL_DEFAULT_FILL_COLOR;
    /** the preference key used to store the default color for a pool border */
    public static String PREF_POOL_DEFAULT_BORDER_COLOR =
        BpmnDiagramPreferenceInitializer.PREF_POOL_DEFAULT_BORDER_COLOR;
    
    /** the preference key used to store the default color for a subprocess */
    public static String PREF_SUBPROCESS_DEFAULT_FILL_COLOR =
        BpmnDiagramPreferenceInitializer.PREF_SUBPROCESS_DEFAULT_FILL_COLOR;
    
    /** the preference key used to store the default color for a task*/
    public static String PREF_TASK_DEFAULT_FILL_COLOR =
        BpmnDiagramPreferenceInitializer.PREF_TASK_DEFAULT_FILL_COLOR;
    /** the preference key used to store the default color for a event*/
    public static String PREF_EVENT_DEFAULT_FILL_COLOR =
        BpmnDiagramPreferenceInitializer.PREF_EVENT_DEFAULT_FILL_COLOR;
    /** the preference key used to store the default color for a gateway*/
    public static String PREF_GATEWAY_DEFAULT_FILL_COLOR =
        BpmnDiagramPreferenceInitializer.PREF_GATEWAY_DEFAULT_FILL_COLOR;
    
    /**
     * The system property name to override the name of the default font for diagrams.
     */
    public static String VM_ARG_DEFAULT_FONT_NAME =
        BpmnDiagramPreferenceInitializer.VM_ARG_DEFAULT_FONT_NAME;
    /**
     * The system property name to override the name of the default font for diagrams.
     */
    public static String vM_ARG_DEFAULT_FONT_SIZE =
        BpmnDiagramPreferenceInitializer.VM_ARG_DEFAULT_FONT_NAME;
    
    public BpmnAppearancePreferencePage() {
        super();
        setPreferenceStore(BpmnDiagramEditorPlugin.getInstance().getPreferenceStore());
    }
    
    
    /** 
     * The field editor preference page implementation of a <code>PreferencePage</code>
     * method loads all the field editors with their default values.
     * 
     * Override so that the font field editor default is set.
     * Note: loadDefault() does not work with FontFieldEditor
     */
    protected void performDefaults() {      
        super.performDefaults();
        internalSetDefaultFontPref(getPreferenceStore());
    }
    
    
    
    /**
     * Hack: hardcode the default font.
     * Weird that this is not taken care of the BpmnDigramPrefereneInitializer ?
     * Oh well leave it here until we find out.
     * @param store
     */
    private void internalSetDefaultFontPref(IPreferenceStore store) {
        PreferenceConverter.setDefault(
            store,
            IPreferenceConstants.PREF_DEFAULT_FONT,
            BpmnDiagramPreferenceInitializer.getDefaultFont());      
    }
    
    /**
     * Adds the font and color fields to the <code>Composite</code> given.
     * @param composite
     */
    protected void addFontAndColorFields(Composite composite) {
        super.addFontAndColorFields(composite);
        
        addField(PREF_POOL_DEFAULT_FILL_COLOR, BpmnDiagramMessages.BpmnAppearancePreferencePage_pool_bg_color, composite);
        addField(PREF_POOL_DEFAULT_BORDER_COLOR, BpmnDiagramMessages.BpmnAppearancePreferencePage_border_color, composite);
        
        addField(PREF_SUBPROCESS_DEFAULT_FILL_COLOR, BpmnDiagramMessages.BpmnAppearancePreferencePage_sp_bg_color, composite);
        addField(PREF_TASK_DEFAULT_FILL_COLOR, BpmnDiagramMessages.BpmnAppearancePreferencePage_task_bg_color, composite);
        addField(PREF_EVENT_DEFAULT_FILL_COLOR, BpmnDiagramMessages.BpmnAppearancePreferencePage_event_bg_color, composite);
        addField(PREF_GATEWAY_DEFAULT_FILL_COLOR, BpmnDiagramMessages.BpmnAppearancePreferencePage_gateway_bg_color, composite);
    }
    
    private void addField(String name, String labelText, Composite parent) {
        addField(new ColorFieldEditor(name, labelText, parent));
    }
}
