/*******************************************************************************
 * Copyright (c) 2009  Egon Willighagen <egonw@users.sf.net>
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contact: http://www.bioclipse.net/
 ******************************************************************************/

package net.bioclipse.chembl.business;

import java.io.File;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.IStructuredSelection;
/**
 * Helper methods for the NewWizards to find an unused name.
 *
 * @author egonw
 */
public class WizardHelper {

	
	 /**
	  * Annzi invokes
     * Returns an filename which is not used in the currently selected
     * IResource.
     *
     * @param currentSelection IResource to search the file in
     * @param prefix           Front part of the filename pattern
     * @param suffix           Last part of the filename pattern
     *
     * @return Returns an unused filename
     */
    public static String findUnusedFileName(
            IStructuredSelection currentSelection,
            String prefix, String suffix
    ) {
        String fileName = prefix + suffix;
        Iterator<?> selectedBits = currentSelection.iterator();
        if (selectedBits.hasNext()) {
            Object somethingSelected = selectedBits.next();
            IResource selectedResource = null;
            if (somethingSelected instanceof IResource) {
                selectedResource = (IResource)somethingSelected;
            } else if (somethingSelected instanceof IAdaptable) {
                selectedResource = (IResource)((IAdaptable)somethingSelected)
                        .getAdapter(IResource.class);
            }
            if (selectedResource != null) {
                if (selectedResource.getType() == IResource.FILE) {
                    selectedResource = selectedResource.getParent();
                }
                if (selectedResource.isAccessible()) {

                    IPath path = selectedResource.getFullPath();

                    path = path.append( fileName );
                    IFile file = selectedResource.getWorkspace().getRoot().getFile( path );
                    int i=0;
                    while(file.exists()) {
                        i++;
                        path = path.removeLastSegments( 1 );
                        path = path.append( prefix+i+suffix );
                        IPath copy = path.removeFirstSegments( 1 );
                        file = selectedResource.getProject().getFile( copy );
                    }
                    fileName = path.lastSegment();
                }
            }
        }
        return fileName;
    }

    private static boolean fileAlreadyExists(File[] files, String fileName) {
        for (File file : files) {
            System.out.println(file.getName());
            if (file.getName().equals(fileName)) {
                return true;
            }
        }
        return false;
    }
}
