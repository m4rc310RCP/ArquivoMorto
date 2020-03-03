 
package com.m4rc310.rcp.master.parts;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.services.nls.Translation;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.services.IStylingEngine;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.m4rc310.rcp.master.actions.InstallAction;
import com.m4rc310.rcp.master.i18n.Messages;
import com.m4rc310.rcp.ui.utils.PartControl;

public class InstalationPart {
	
	@Inject IStylingEngine engine;
	
	@Inject @Translation Messages m;
	
	@Inject InstallAction installAction;
	
	@Inject UISynchronize sync;
	
	@Inject
	public InstalationPart() {
		
	}
	
	@PostConstruct
	public void postConstruct(Composite parent_, PartControl pc) {
		Composite comp = pc.getComposite(parent_);
		comp.setLayout(new GridLayout(1, true));
		
		Composite parent = pc.getComposite(comp);
		
		parent.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));

		parent.setLayout(new GridLayout(1, true));
		
		Label icon = pc.getIcon(parent, "com.m4rc310.rcp.master", "icons/install.png");
		GridData gd = new GridData(SWT.CENTER, SWT.NONE, true, true);
		icon.setLayoutData(gd);
		
		
		Button buttonInstall = pc.getButton(parent, m.textInstall, e->{
			installAction.startInstall();
		});
		
		gd = new GridData(SWT.CENTER, SWT.NONE, true, true);
		buttonInstall.setLayoutData(gd);
		
		Label info = pc.getLabel(parent, "", SWT.CENTER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.minimumWidth = 370;
		info.setLayoutData(gd);
		
		installAction.getStream().addListener(InstallAction.INSTALING, e->{
			sync.asyncExec(()->{
				Boolean enabled = e.getValue(0, boolean.class);
				buttonInstall.setEnabled(enabled);
			});
		});
		
		installAction.getStream().addListener(InstallAction.PRINT_INFO, e->{
			sync.asyncExec(()->{
				String text = e.getValue(0, String.class);
				info.setText(text);
			});
		});
		
		
		
		
		
//		stream.addListener(property, listener);
		engine.setId(buttonInstall, "MyButtonInstall");
	
	}
	
	
	
	
}