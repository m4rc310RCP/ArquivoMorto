package com.m4rc310.rcp.auth.toolcontrols;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.services.nls.Translation;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.services.IStylingEngine;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.m4rc310.rcp.auth.actions.LoginAction;
import com.m4rc310.rcp.auth.i18n.Messages;
import com.m4rc310.rcp.auth.models.User;
import com.m4rc310.rcp.auth.popups.UserPopup;
import com.m4rc310.rcp.ui.utils.PartControl;

public class ToolcontrolUser {

	@Inject
	UISynchronize sync;

	@Inject
	LoginAction action;
	
	@Inject @Translation Messages m;

	@Inject
	IStylingEngine engine;

	private Label labelInfo;

	@PostConstruct
	public void createGui(Composite _parent, PartControl pc, UserPopup popup) {

		Composite parent = new Composite(_parent, SWT.NONE);

		parent.setLayout(new GridLayout(1, false));
		pc.clearMargins(parent);

		GridData data = new GridData(SWT.FILL, SWT.CENTER, true, true);
		data.widthHint = 140;

		this.labelInfo = pc.getLabel(parent, "");
		labelInfo.setLayoutData(data);
		labelInfo.setCursor(parent.getDisplay().getSystemCursor(SWT.CURSOR_HAND));
		

		engine.setId(labelInfo, "MyLabelUserInfo");

		action.addListener(LoginAction.INFORM_ON_OFF_LINE, e -> {
			boolean on = e.getValue(boolean.class);
			if (!on)
				labelInfo.setText(m.textOffline);
		});

		action.addListener(LoginAction.CORRECT_PIN, e -> {
			sync.syncExec(() -> {
				User user = e.getValue(User.class);
				labelInfo.setText(user.getName());
			});
		});
	}

}