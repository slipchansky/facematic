package org.facematic.wizards;

import java.util.Comparator;

import javax.lang.model.element.PackageElement;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.internal.core.BinaryType;
import org.eclipse.jdt.internal.core.CompilationUnit;
import org.eclipse.jdt.internal.core.PackageFragment;
import org.eclipse.jdt.internal.core.SourceType;
import org.eclipse.jdt.internal.core.search.JavaSearchScope;
import org.eclipse.jdt.internal.core.search.JavaWorkspaceScope;
import org.eclipse.jdt.internal.ui.dialogs.OpenTypeSelectionDialog;
import org.eclipse.jdt.internal.ui.dialogs.PackageSelectionDialog;
import org.eclipse.jdt.internal.ui.util.BusyIndicatorRunnableContext;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;
import org.eclipse.ui.dialogs.ResourceListSelectionDialog;
import org.eclipse.ui.dialogs.ResourceSelectionDialog;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (xml).
 */

public class FacematicNewBeanFormWizardPage extends WizardPage {
	private Text containerText;

	private Text formNameText;

	private ISelection selection;

	private IContainer container;

	private Text classNameText;
	private String filter;

	private String className;

	private IProject classProject;

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public FacematicNewBeanFormWizardPage(ISelection selection) {
		super("wizardPage");
		setTitle("New BeanForm");
		setDescription("This wizard creates a new BeanForm view-controller pair that can be opened by a facematic view-controller editor.");
		this.selection = selection;
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;

		Label label = new Label(container, SWT.NULL);
		label.setText("&Container:");
		containerText = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		containerText.setLayoutData(gd);
		containerText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		Button button = new Button(container, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleBrowseDestination();
			}
		});

		label = new Label(container, SWT.NULL);
		label.setText("&Bean class:");
		classNameText = new Text(container, SWT.BORDER | SWT.SINGLE);
		classNameText.setEditable(false);
		
		gd = new GridData(GridData.FILL_HORIZONTAL);
		classNameText.setLayoutData(gd);
		classNameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		button = new Button(container, SWT.PUSH);
		button.setText("Find class...");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleBrowseClass();
			}
		});

		label = new Label(container, SWT.NULL);
		label.setText("&Name:");

		formNameText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		formNameText.setLayoutData(gd);
		formNameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		initialize();
		dialogChanged();
		setControl(container);
	}

	/**
	 * Tests if the current workbench selection is a suitable container to use.
	 */

	@SuppressWarnings("restriction")
	private void initialize() {
		if (selection != null && selection.isEmpty() == false
				&& selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;

			if (ssel.size() > 1)
				return;
			Object obj = ssel.getFirstElement();

			if (obj instanceof PackageFragment) {
				PackageFragment pkg = (PackageFragment) obj;
				obj = pkg.getResource();
			}

			if (obj instanceof IResource) {
				if (obj instanceof IContainer)
					container = (IContainer) obj;
				else
					container = ((IResource) obj).getParent();
				
				String text = container.getFullPath().toString();
				
				containerText.setText(text);
				String []a = text.split("/main/java/");
				if (a.length < 2) {
					a = text.split("/main/resources/");
				}
				if (a.length > 1) {
					a = a[1].split("/");
					this.filter = a[0]+"/"+a[1]+"/";
				}
			}

		}
		formNameText.setText("");
	}

	/**
	 * 
	 */
	private void handleBrowseDestination() {
		ContainerSelectionDialog dialog = new ContainerSelectionDialog(
				getShell(), container == null ? ResourcesPlugin.getWorkspace()
						.getRoot() : container, false,
				"Select new file container");
		if (dialog.open() == ContainerSelectionDialog.OK) {
			Object[] result = dialog.getResult();
			if (result.length == 1) {
				containerText.setText(((Path) result[0]).toString());
			}
		}
	}

	/**
	 * 
	 */
	@SuppressWarnings("restriction")
	private void handleBrowseClass() {

		BusyIndicatorRunnableContext context = new BusyIndicatorRunnableContext();
		JavaWorkspaceScope s = new JavaWorkspaceScope() {

			@Override
			public boolean encloses(String resourcePathString) {
				if (filter==null) return false;
				if (!super.encloses(resourcePathString)) return false;
				return resourcePathString.indexOf(filter)>0;
			}
			
		};
		s.setIncludesBinaries(false);
		
		OpenTypeSelectionDialog dialog = new OpenTypeSelectionDialog(
				getShell(), true, context, s, IJavaSearchConstants.CLASS);

		if (dialog.open() == ContainerSelectionDialog.OK) {
			Object[] result = dialog.getResult();
			
			if (result.length == 1) {
				if (result[0] instanceof IType)
                {
                    IType type = (IType) result[0];
                    this.className = type.getFullyQualifiedName();
                    this.classProject = NewFacematicProjectWizard.findProject(type);
                    classNameText.setText(className);
                    formNameText.setText("Edit"+type.getElementName()+"Form");
                }
				
			}
		}
	}

	/**
	 * Ensures that both text fields are set.
	 */

	private void dialogChanged() {
		IResource container = ResourcesPlugin.getWorkspace().getRoot()
				.findMember(new Path(getContainerName()));
		String fileName = getFormName();

		if (getContainerName().length() == 0) {
			updateStatus("File container must be specified");
			return;
		}
		if (container == null
				|| (container.getType() & (IResource.PROJECT | IResource.FOLDER)) == 0) {
			updateStatus("File container must exist");
			return;
		}
		if (!container.isAccessible()) {
			updateStatus("Project must be writable");
			return;
		}
		if (fileName.length() == 0) {
			updateStatus("Name must be specified");
			return;
		}
		if (fileName.replace('\\', '/').replace('.', '/').indexOf('/', 1) > 0) {
			updateStatus("Name must be valid");
			return;
		}
		updateStatus(null);
	}

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public String getContainerName() {
		return containerText.getText();
	}

	public String getFormName() {
		return formNameText.getText();
	}
	
	public String getClassName () {
		return className;
	}
	
	public IProject getProject () {
		return classProject;
	}
}