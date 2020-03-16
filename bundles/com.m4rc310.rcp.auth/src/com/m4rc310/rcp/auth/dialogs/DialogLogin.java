package com.m4rc310.rcp.auth.dialogs;

import javax.inject.Inject;

import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.m4rc310.rcp.auth.actions.LoginAction;
import com.m4rc310.rcp.ui.utils.PartControl;

public class DialogLogin extends Dialog {

	@Inject
	PartControl pc;

	@Inject
	LoginAction action;

	private Shell shell;

	@Inject
	UISynchronize sync;

	@Inject
	public DialogLogin(Shell shell) {
		super(shell);
		this.shell = shell;
	}

	@Override
	protected Control createDialogArea(Composite parent_) {
		
		
		
		Composite parent = pc.getComposite(parent_, SWT.WRAP);
		parent.setLayout(new GridLayout());

		Group group = pc.getGroup(parent);
		group.setLayout(new GridLayout(2, false));
		GridData data = new GridData(SWT.FILL, SWT.TOP, true, false);
		data.widthHint = 280;
		group.setLayoutData(data);

		pc.getLabel(group, "Informe o seu PIN:");

		Text textPIN = pc.getText(group, "", SWT.BORDER | SWT.PASSWORD | SWT.CENTER);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = 180;
		textPIN.setLayoutData(data);

		textPIN.addModifyListener(e -> {
			Text ct = (Text) e.widget;
			String pin = ct.getText();
			action.readPassword(pin);
		});

		action.addListener(this, LoginAction.CORRECT_PIN, e -> {
			super.okPressed();
		});

		action.addListener(this, LoginAction.INCORRECT_PIN, e -> {
			String message = e.getValue(String.class);
			MessageDialog.openError(shell, "Error", message);
			if (!textPIN.isDisposed()) {
				textPIN.selectAll();
			}
		});
		
		return parent;
	}
	
	
	@Override
	public boolean close() {
		action.removeListeners(this);
		return super.close();
	}
	

	@Override
	protected void okPressed() {
		action.login();
//		super.okPressed();
	}

}
