package org.felix.jal.eclipse.plugin.ui.wizards;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Combo;
import org.felix.jal.eclipse.plugin.ui.editors.FileUtil;


public class TargetHardwareWizardPage extends WizardPage {

	private Combo mcuCombo;
	private ComboViewer mcuComboViewer;
	private Combo frequencyCombo;
	private String libPath;
	
	public TargetHardwareWizardPage(ISelection selection) {
		super("wizardPage");
		setTitle("Target Hardware Properties");
		setDescription("Define the MCU target properties.");
		this.libPath ="";
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;
		layout.verticalSpacing = 9;
		Label label = new Label(container, SWT.NULL);
		label.setText("&Microcontroller:");
	
		mcuCombo = new Combo(container, SWT.BORDER | SWT.SINGLE);
		mcuComboViewer = new ComboViewer(mcuCombo);		
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		mcuCombo.setLayoutData(gd);
		
		mcuCombo.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent arg0) {
			}
			
			@Override
			public void focusGained(FocusEvent arg0) {
				fillMCUCombo();

			}
		});
		
		mcuComboViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent arg0) {
				dialogChanged();
			}
		});

		label = new Label(container, SWT.NULL);
		label.setText("&Frequency(hz):");
		
		frequencyCombo = new Combo(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		frequencyCombo.setLayoutData(gd);
		frequencyCombo.add("23000");
		frequencyCombo.add("100000");
		frequencyCombo.add("200000");
		frequencyCombo.add("1000000");
		frequencyCombo.add("2000000");
		frequencyCombo.add("4000000");
		frequencyCombo.add("6000000");
		frequencyCombo.add("8000000");
		frequencyCombo.add("12000000");
		frequencyCombo.add("16000000");
		frequencyCombo.add("20000000");
		frequencyCombo.add("40000000");
		frequencyCombo.add("48000000");
		frequencyCombo.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent arg0) {
				dialogChanged();				
			}
		});
		
		dialogChanged();
		setControl(container);
	}
	
	private void dialogChanged(){		
		String mcuSelected = this.getMicro();
		
		if (mcuSelected.trim().length()==0){			
			updateStatus("Microcontroller must be specified");
			return;
		}
		
		String mcuFreq = this.getFrequency();
		if (mcuFreq.trim().length()==0){			
			updateStatus("Frequency must be specified");
			return;
		}
		
		try {
			int freq = Integer.parseInt(mcuFreq.replace("_", ""));
			if (freq <=0){
				updateStatus("Valid frequency must be specified");				
				return;
			}
		}
		catch(NumberFormatException nfe){
			updateStatus("Valid frequency must be specified");
			return;
		}		
		
		updateStatus(null);
	}
	
	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}
	
	public String getMicro() {
		return mcuCombo.getText();
	}
	
	public String getFrequency() {
		String freq = frequencyCombo.getText();
		StringBuffer sb = new StringBuffer();
		int group = 0;
		
		for (int i=freq.length()-1; i>=0; i--) {
			sb.append(freq.charAt(i));
			group++;
			if (group==3 && i!=0){
				group = 0;
				sb.append("_");
			}				
		}
		return sb.reverse().toString();
	}

	public void setMCUs(String[] mcus){
		if (mcuCombo != null){
			this.mcuCombo.setItems(mcus);
		}
	}
	
	private void fillMCUCombo(){
		if (mcuCombo.getItemCount()==0 && this.libPath !="")	{
			
			List<String> mcus = new ArrayList<String>();
			try {
				List<File> files = FileUtil.getFileListing(new File(this.libPath));
				for (File file : files) {
					if (file.getName().substring(0, 1).matches("[0-9]")){
						String name = file.getName();
						int pos = name.toLowerCase().indexOf(".jal"); 
						if (pos>=0)
							name = name.substring(0, pos);
						mcus.add(name.trim());
					}
				}
				setMCUs(mcus.toArray(new String[mcus.size()]));
			} catch (FileNotFoundException fnfe) {
				MessageDialog.openError(getShell(), "Error", fnfe.getMessage());
			}
		}
	}

	public void setLibPath(String libPath) {
		this.libPath = libPath;
	}
	
}
