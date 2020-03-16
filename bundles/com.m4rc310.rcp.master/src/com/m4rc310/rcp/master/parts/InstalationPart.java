
package com.m4rc310.rcp.master.parts;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.services.nls.Translation;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.services.IStylingEngine;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.m4rc310.rcp.master.actions.InstallAction2;
import com.m4rc310.rcp.master.i18n.Messages;
import com.m4rc310.rcp.ui.utils.PartControl;

public class InstalationPart {

	@Inject
	IStylingEngine engine;

	@Inject
	@Translation
	Messages m;

	@Inject
	InstallAction2 installAction;

	@Inject
	UISynchronize sync;

	@Inject
	Shell shell;
	
	

	@Inject
	public InstalationPart() {

	}

	@PostConstruct
	public void postConstruct(Composite parent_, PartControl pc) {
		Composite comp = pc.getComposite(parent_);
		comp.setLayout(new GridLayout(1, true));

		StackLayout stackLayout = new StackLayout();
		StackLayout stackLayoutIcon = new StackLayout();

		Composite parent = pc.getComposite(comp);

		parent.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
		parent.setLayout(new GridLayout(1, true));
		

		Composite iconParents = pc.getComposite(parent);
		iconParents.setLayout(stackLayoutIcon);																
		iconParents.setLayoutData(new GridData(SWT.CENTER, SWT.NONE, true, true));
		
		
		Label icon = pc.getIcon(iconParents, "com.m4rc310.rcp.master", "icons/install.png");
		GridData gd = new GridData(SWT.CENTER, SWT.NONE, true, true);
		icon.setLayoutData(gd);
		
		Label iconOK = pc.getIcon(iconParents, "com.m4rc310.rcp.master", "icons/button_ok.png");
		gd = new GridData(SWT.CENTER, SWT.NONE, true, true);
		iconOK.setLayoutData(gd);

		
		Composite buttonsParents = pc.getComposite(parent);
		buttonsParents.setLayout(stackLayout);
		buttonsParents.setLayoutData(new GridData(SWT.CENTER, SWT.NONE, true, true));
		
		Button buttonInstall = pc.getButton(buttonsParents, m.textInstall, e -> {
			installAction.startInstall();
		});

		gd = new GridData(SWT.CENTER, SWT.NONE, true, true);
		buttonInstall.setLayoutData(gd);

		Button buttonInstalling = pc.getButton(buttonsParents, m.textInstalling, e -> {
//			installAction.confirmInstalation();
		});
		
		gd = new GridData(SWT.CENTER, SWT.NONE, true, true);
		buttonInstalling.setLayoutData(gd);
		
		buttonInstalling.setEnabled(false);
		
		
		Button buttonClose = pc.getButton(buttonsParents, m.textClose, e -> {
//			installAction.close();
		});

		gd = new GridData(SWT.CENTER, SWT.NONE, true, true);
		buttonClose.setLayoutData(gd);

		Button buttonRestart = pc.getButton(buttonsParents, m.textRestart, e -> {
			installAction.restartApp();
		});

		gd = new GridData(SWT.CENTER, SWT.NONE, true, true);
		buttonRestart.setLayoutData(gd);

		Label info = pc.getLabel(parent, "", SWT.CENTER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.minimumWidth = 370;
		info.setLayoutData(gd);

		installAction.getStream().addListener(InstallAction2.PREPARE_TO_RESTART, e -> {
			sync.syncExec(() -> {
				buttonClose.setEnabled(false);
				buttonRestart.setEnabled(true);
				buttonInstall.setEnabled(false);
				stackLayout.topControl = buttonRestart;
				buttonsParents.layout();
				
				stackLayoutIcon.topControl= iconOK;
				iconParents.layout();
			});
		});
		installAction.getStream().addListener(InstallAction2.PREPARE_TO_CLOSE, e -> {
			sync.syncExec(() -> {
				buttonClose.setEnabled(true);
				buttonRestart.setEnabled(false);
				buttonInstall.setEnabled(false);
				stackLayout.topControl = buttonClose;
				buttonsParents.layout();
			});
			
		});

		installAction.getStream().addListener(InstallAction2.PREPARE_TO_INIT, e -> {
			sync.syncExec(() -> {
				buttonClose.setEnabled(false);
				buttonRestart.setEnabled(false);
				buttonInstall.setEnabled(true);
				stackLayout.topControl = buttonInstall;
				buttonsParents.layout();
				
				stackLayoutIcon.topControl= icon;
				iconParents.layout();
			});
		});
		
		installAction.getStream().addListener(InstallAction2.PREPARE_TO_INSTALLING, e -> {
			sync.syncExec(() -> {
				stackLayout.topControl = buttonInstalling;
				buttonsParents.layout();
			});
		});

		installAction.getStream().addListener(InstallAction2.INSTALING, e -> {
			sync.syncExec(() -> {
				Boolean enabled = e.getValue(0, boolean.class);
				buttonInstall.setEnabled(!enabled);
			});
		});

		installAction.getStream().addListener(InstallAction2.SHOW_DIALOG_ERROR, e -> {
			sync.syncExec(() -> {
				String err = e.getValue(0, String.class);
				MessageDialog.openError(shell, m.dialogMessageboxTitleError, err);
			});
		});
		
		installAction.getStream().addListener(InstallAction2.SHOW_DIALOG_INFORMATION, e -> {
			MessageDialog.openInformation(shell, m.dialogMessageboxTitleInfo, e.getValue(0, String.class));
		});

		installAction.getStream().addListener(InstallAction2.PRINT_INFO, e -> {
			sync.syncExec(() -> {
				String text = e.getValue(0, String.class);
				info.setText(text);
			});
		});
		
		

//		stream.addListener(property, listener);
//		engine.setId(buttonInstall, "MyButtonInstall");

		installAction.startInstall();
	}
	
	

}