package com.m4rc310.rcp.master.toolcontrols;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.services.nls.Translation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.m4rc310.rcp.master.i18n.Messages;
import com.m4rc310.rcp.master.utils.E4ApplicationInfo;
import com.m4rc310.rcp.ui.utils.PartControl;

public class SP {
	
	@Inject E4ApplicationInfo info;

	@Inject @Translation Messages m;
	
	@PostConstruct
	public void createGui(Composite _parent, PartControl pc) {
		final Composite parent = new Composite(_parent, SWT.NONE);
		parent.setLayout(new GridLayout());
		GridData gd = new GridData();
		pc.clearMargins(parent);
		
//		Label label = pc.getLabel(parent, NLS.bind(m.textAppInfo, info.getAppName(), info.getAppVersion()));
//		Label label = pc.getLabel(parent, "");
		
		gd.widthHint = 3;
		new Label(parent, SWT.NONE).setLayoutData(gd);
	}
}