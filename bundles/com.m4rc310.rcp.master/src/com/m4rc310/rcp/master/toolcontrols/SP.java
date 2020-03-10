package com.m4rc310.rcp.master.toolcontrols;

import javax.annotation.PostConstruct;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.m4rc310.rcp.ui.utils.PartControl;

public class SP {
	
	@PostConstruct
	public void createGui(Composite _parent, PartControl pc) {
		final Composite parent = new Composite(_parent, SWT.NONE);
		parent.setLayout(new GridLayout());
		GridData gd = new GridData();
		pc.clearMargins(parent);
		gd.widthHint = 3;
		new Label(parent, SWT.NONE).setLayoutData(gd);
	}
}