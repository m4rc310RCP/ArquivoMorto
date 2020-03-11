package com.m4rc310.rcp.master.toolcontrols;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.services.nls.Translation;
import org.eclipse.e4.ui.services.IStylingEngine;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

import com.m4rc310.rcp.master.i18n.Messages;
import com.m4rc310.rcp.master.utils.E4ApplicationInfo;
import com.m4rc310.rcp.ui.utils.PartControl;

public class ToolControlAppVersion {

	@Inject 
	E4ApplicationInfo info;

	@Inject
	@Translation
	Messages m;
	
	@Inject
	IStylingEngine engine;

	@PostConstruct
	public void createGui(Composite _parent, PartControl pc) {
		final Composite parent = new Composite(_parent, SWT.NONE);
		
		parent.setLayout(new GridLayout(2, true));
		pc.clearMargins(parent);
		
		GridData gd = new GridData(SWT.CENTER, SWT.CENTER, true, true);
		
		Color foreground = SWTResourceManager.getColor(SWT.COLOR_TITLE_INACTIVE_FOREGROUND);
		
		Label label = pc.getLabel(parent, NLS.bind(m.textAppInfoName, info.getAppName()));
		label.setLayoutData(gd);
		Font font = SWTResourceManager.getFont("Arial", 11, SWT.ITALIC, false, false);
		label.setFont(font);
		label.setForeground(foreground);
		
		gd = new GridData(SWT.CENTER, SWT.CENTER, true, true);
		
		label = pc.getLabel(parent, NLS.bind(m.textAppInfo, info.getAppVersion()));
		label.setLayoutData(gd);
		label.setFont(font);
		label.setForeground(foreground);
	}
}