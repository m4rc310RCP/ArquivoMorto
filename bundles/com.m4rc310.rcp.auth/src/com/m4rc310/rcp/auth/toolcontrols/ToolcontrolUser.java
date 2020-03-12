package com.m4rc310.rcp.auth.toolcontrols;

import javax.annotation.PostConstruct;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.ResourceManager;

import com.m4rc310.rcp.ui.utils.PartControl;

public class ToolcontrolUser {

	@PostConstruct
	public void createGui(Composite _parent, PartControl pc) {

		Composite parent = new Composite(_parent, SWT.NONE);

		parent.setLayout(new GridLayout(1, false));
		pc.clearMargins(parent);

		GridData gd = new GridData(SWT.CENTER, SWT.CENTER, true, true);
		gd.heightHint = 16;
		gd.minimumWidth =16;
		
		Label labelIcon = pc.getLabel(parent, "");
		labelIcon.setImage(ResourceManager.getPluginImage("com.m4rc310.rcp.auth", "icons/system-users.png"));
		labelIcon.setLayoutData(gd);
		
		
	}
}