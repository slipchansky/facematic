package org.felix.jal.eclipse.plugin.ui.editors.hex;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.felix.jal.eclipse.plugin.JalPlugin;
import org.felix.jal.eclipse.plugin.ui.preferences.JalEditorPreferences;

public class HexEditor extends TextEditor {
	private IPropertyChangeListener propertyChangeListener;

	public HexEditor() {
		super();

		propertyChangeListener = new IPropertyChangeListener() {			
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				updateColors();
			}
		};
		
		JalPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(propertyChangeListener);
	}

	public void dispose() {
		super.dispose();
		JalPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(propertyChangeListener);
	}	
	
	private void updateColors() {
		if (this.getSourceViewer() != null) {
			try {
				IPreferenceStore uiStore = EditorsUI.getPreferenceStore();
				if (JalEditorPreferences.isDarkSyntaxHighlightingEnabled()) {
					uiStore.setValue(PREFERENCE_COLOR_BACKGROUND_SYSTEM_DEFAULT, false);
					uiStore.setValue(PREFERENCE_COLOR_FOREGROUND_SYSTEM_DEFAULT, false);
					PreferenceConverter.setValue(uiStore, PREFERENCE_COLOR_BACKGROUND, new RGB(0x00, 0x1b, 0x33));
					PreferenceConverter.setValue(uiStore, PREFERENCE_COLOR_FOREGROUND, new RGB(0xff, 0xff, 0xff));
					uiStore.setValue(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE, true);
					PreferenceConverter.setValue(uiStore, AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE_COLOR, new RGB(0x00, 0x3b, 0x70));
				} else {
					uiStore.setValue(PREFERENCE_COLOR_BACKGROUND_SYSTEM_DEFAULT, true);
					uiStore.setValue(PREFERENCE_COLOR_FOREGROUND_SYSTEM_DEFAULT, true);
					PreferenceConverter.setValue(uiStore, AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE_COLOR, new RGB(0xE8, 0xF2, 0xFE));
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
