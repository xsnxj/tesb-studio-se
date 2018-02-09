// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.designer.camel.resource.editors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.talend.designer.camel.resource.editors.input.RouteResourceInput;

/**
 * @author xpli
 *
 */
public class RouteResoureChangeListener implements IResourceChangeListener {

	private RouteResourceInput editorInput;
	private IFile editorFile;
	private String filePath;

	public RouteResoureChangeListener(RouteResourceInput editorInput) {
		this.editorInput = editorInput;
		this.editorInput.setListener(this);
		this.editorFile = editorInput.getFile();
		this.filePath = this.editorFile.getLocation().toPortableString();
	}
	@Override
	public void resourceChanged(IResourceChangeEvent event) {

		IResourceDelta delta = event.getDelta();
		try {
			if (delta == null || editorFile == null) {
				return;
			}
			delta.accept(new IResourceDeltaVisitor() {

				@Override
				public boolean visit(IResourceDelta delta) throws CoreException {
					IResource resource = delta.getResource();
					if (resource == null) {
						return false;
					}
					if (resource.getType() != IResource.FILE) {
						IPath location = resource.getLocation();
						if(location == null || !filePath.startsWith(location.toPortableString())){
							return false;
						}
						return true;
					}
					
					if (resource.equals(editorFile)) {
						RouteResourceEditor.saveContentsToItem(editorInput);
						return false;
					}
					return false;
				}
			}, IResource.FILE);
		} catch (CoreException e) {
			e.printStackTrace();
		}


	}

}
