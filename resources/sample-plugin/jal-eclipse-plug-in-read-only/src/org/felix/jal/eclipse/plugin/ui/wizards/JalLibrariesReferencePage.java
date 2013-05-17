package org.felix.jal.eclipse.plugin.ui.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public class JalLibrariesReferencePage extends WizardPage {
	private CheckboxTableViewer referenceProjectsViewer;
	private static final int PROJECT_LIST_MULTIPLIER = 15;
	
	private Text libraryPathText;
	private Text libraryProjectNameText;
	
	private TargetHardwareWizardPage targetHardPage;
	
	public JalLibrariesReferencePage(ISelection selection) {
		super("wizardPage");
		setTitle("Jal Libraries References");
		setDescription("Define the Jal library project reference.");
	}

	@Override
	public void createControl(Composite parent) {
		Font font = parent.getFont();

        Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		layout.numColumns = 3;
        composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        composite.setFont(font);

        Label referenceLabel = new Label(composite, SWT.NONE);
        referenceLabel.setText("Select Jal library project:");
        referenceLabel.setFont(font);

        referenceProjectsViewer = CheckboxTableViewer.newCheckList(composite, SWT.BORDER);
        referenceProjectsViewer.getTable().setFont(composite.getFont());
        GridData data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        
        data.heightHint = getDefaultFontHeight(referenceProjectsViewer.getTable(), PROJECT_LIST_MULTIPLIER);
        referenceProjectsViewer.getTable().setLayoutData(data);
        referenceProjectsViewer.setLabelProvider(WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider());
        referenceProjectsViewer.setContentProvider(getContentProvider());
        referenceProjectsViewer.setComparator(new ViewerComparator());
        referenceProjectsViewer.setInput(ResourcesPlugin.getWorkspace());
        referenceProjectsViewer.addCheckStateListener(new ICheckStateListener() {
			
			@Override
			public void checkStateChanged(CheckStateChangedEvent arg0) {
				dialogChanged();
				
			}
		});
        
		Label label = new Label(composite, SWT.NULL);
		label.setText("");
		
		label = new Label(composite, SWT.NULL);
		label.setText("or define a new library project:");
		label = new Label(composite, SWT.NULL);
		label.setText("");
		label = new Label(composite, SWT.NULL);
		label.setText("");

		label = new Label(composite, SWT.NULL);
		label.setText("Library &path:");
		

		libraryPathText = new Text(composite, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		libraryPathText.setLayoutData(gd);
		libraryPathText.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent arg0) {
				dialogChanged();				
			}
		});
		
		Button button = new Button(composite, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleBrowse();
			}
		});

		label = new Label(composite, SWT.NULL);
		label.setText("&Library project name:");		
		
		libraryProjectNameText = new Text(composite, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		libraryProjectNameText.setLayoutData(gd);
		libraryProjectNameText.setText("JalLibraries");
		libraryProjectNameText.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent arg0) {
				dialogChanged();				
			}
		});
				
		dialogChanged();
		setControl(composite);
	}
	
	private void handleBrowse() {
		DirectoryDialog dialog = new DirectoryDialog(getShell());
		dialog.setMessage("Enter the Jal libraries directory");
		String directory = dialog.open();
		if ( directory != null) {
			libraryPathText.setText(directory);
			libraryProjectNameText.setText(dialog.getText());
		}
	}
	
	private void dialogChanged(){
		
		if (getReferencedProjects().length != 0){
			libraryPathText.setEnabled(false);
			libraryProjectNameText.setEnabled(false);
			updateStatus(null);
			return;
		}
		else
		{
			libraryPathText.setEnabled(true);
			libraryProjectNameText.setEnabled(true);
			
			if (getLibraryPath().trim().length() == 0 || getLibraryProjectName().trim().length()==0){
				updateStatus("Referenced project or library definition must be specified");
		        referenceProjectsViewer.getTable().setEnabled(true);
				return;
			}
			
			if (getLibraryPath().trim().length() == 0 && getLibraryProjectName().trim().length()==0
					&& getReferencedProjects().length == 0){
				updateStatus("Referenced project or library definition must be specified");
		        referenceProjectsViewer.getTable().setEnabled(true);
				return;
			}
						
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IStatus status;

			status = workspace.validateProjectLocation(workspace.getRoot().getProject(getLibraryProjectName()),
					new Path(getLibraryPath()));
			if (!status.isOK()){
				updateStatus("Invalid project name or location");
		        referenceProjectsViewer.getTable().setEnabled(true);
				return;
			}				
					
	        referenceProjectsViewer.getTable().setEnabled(false);
			
			updateStatus(null);
			return;
		}
	}
	
    /**
     * Returns a content provider for the reference project
     * viewer. It will return all projects in the workspace.
     *
     * @return the content provider
     */
    protected IStructuredContentProvider getContentProvider() {
        return new WorkbenchContentProvider() {
            public Object[] getChildren(Object element) {
                if (!(element instanceof IWorkspace)) {
					return new Object[0];
				}
                IProject[] projects = ((IWorkspace) element).getRoot()
                        .getProjects();
                return projects == null ? new Object[0] : projects;
            }
        };
    }

    /**
     * Get the defualt widget height for the supplied control.
     * @return int
     * @param control - the control being queried about fonts
     * @param lines - the number of lines to be shown on the table.
     */
    private static int getDefaultFontHeight(Control control, int lines) {
        FontData[] viewerFontData = control.getFont().getFontData();
        int fontHeight = 10;

        //If we have no font data use our guess
        if (viewerFontData.length > 0) {
			fontHeight = viewerFontData[0].getHeight();
		}
        return lines * fontHeight;

    }

    /**
     * Returns the referenced projects selected by the user.
     *
     * @return the referenced projects
     */
    public IProject[] getReferencedProjects() {
        Object[] elements = referenceProjectsViewer.getCheckedElements();
        IProject[] projects = new IProject[elements.length];
        System.arraycopy(elements, 0, projects, 0, elements.length);
        return projects;
    }
    
	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
		
		if (message==null){
			targetHardPage.setMCUs(new String[0]);

			if (mustCreateProject())
				targetHardPage.setLibPath(getLibraryPath());
			else
				if (getReferencedProjects().length>0)
					targetHardPage.setLibPath(getReferencedProjects()[0].getLocation().toOSString());
				else
					targetHardPage.setLibPath("");
		}
	}
	
	public String getLibraryPath() {
		return libraryPathText.getText();
	}
	
	public String getLibraryProjectName() {
		return libraryProjectNameText.getText();
	}
	
	public boolean mustCreateProject(){
		return (getLibraryPath().trim().length() != 0 && getLibraryProjectName().trim().length()!=0 && getReferencedProjects().length == 0);		
	}
	
	public void setTargetHardPage(TargetHardwareWizardPage targetHardPage) {
		this.targetHardPage = targetHardPage;
	}
	
}
