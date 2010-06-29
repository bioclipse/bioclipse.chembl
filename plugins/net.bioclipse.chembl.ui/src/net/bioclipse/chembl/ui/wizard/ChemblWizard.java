/* Copyright (c) 2010  Egon Willighagen <egonw@users.sf.net>
 *               2010  Annsofie Andersson <annzi.andersson@gmail.com>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 */
package net.bioclipse.chembl.ui.wizard;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import net.bioclipse.chembl.Activator;
import net.bioclipse.chembl.business.IChEMBLManager;
import net.bioclipse.chembl.business.WizardHelper;
import net.bioclipse.core.ResourcePathTransformer;
import net.bioclipse.core.business.BioclipseException;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

public class ChemblWizard extends Wizard implements INewWizard {

	private static final Logger logger = Logger.getLogger(ChemblWizard.class);
	private ChemblWizardPage firstpage;
//	private ChemblWizardPage2 secondpage;
	private WizardNewFileCreationPage filePage;
	private IChEMBLManager chembl;
	ChemblData data = new ChemblData();

	public ChemblWizard() {
		super();
		setWindowTitle("ChEMBL wizard");
		chembl = Activator.getDefault().getJavaChEMBLManager();
	}

	public void addPages(){
		firstpage = new ChemblWizardPage("ChEMBL page");
		addPage(firstpage);
		//        secondpage = new ChemblWizardPage2("ChEMBL page");
		//        addPage(secondpage);

		ISelection sel=PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection();
		if(sel instanceof IStructuredSelection){
			filePage = new WizardNewFileCreationPage("newFilePage", (IStructuredSelection) sel);

			filePage.setFileName( WizardHelper.findUnusedFileName((IStructuredSelection)sel, "chembl", ".txt") );
		}else{
			filePage = new WizardNewFileCreationPage("newFilePage", StructuredSelection.EMPTY);
		}
		filePage.setTitle("Choose location and add a file name to create a new file");
		filePage.setDescription("Extension will be .txt if none is given");
		addPage(filePage);
	}	

	@ Override
	public boolean isHelpAvailable() {
		return true;
	}
	//Writes compounds to a MoSS supported file
	@Override
	public boolean performFinish() {		
		try {
			if(filePage.getFileExtension()==null || filePage.getFileExtension().equals( "" ))
				filePage.setFileExtension( "txt" );
			IFile newFile= filePage.createNewFile();
			newFile.delete( true, null );
			chembl.saveCSVT(newFile,data.tab, null);
//old version			chembl.saveCSVT(data.file,data.tab, null);
		} catch (BioclipseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// TODO Auto-generated method stub

	}
}

