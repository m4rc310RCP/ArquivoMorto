package com.m4rc310.rcp.auth.popups;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.services.nls.Translation;
import org.eclipse.e4.ui.services.IStylingEngine;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.wb.swt.ResourceManager;

import com.m4rc310.rcp.auth.i18n.Messages;
import com.m4rc310.rcp.popup.notifications.MNotificationPopup;
import com.m4rc310.rcp.ui.utils.PartControl;

@Creatable
public class UserPopup extends MNotificationPopup {

	@Inject
	PartControl pc;

	@Inject
	@Translation
	Messages m;


	@Inject
	IStylingEngine engine;
	
	@Inject
	public UserPopup(Display display) {
		super(display);
	}

//	@Override
	protected void createContentArea(Composite parent_) {
		Composite parent = pc.getComposite(parent_);
		parent.setLayout(new GridLayout(2, false));
		pc.clearMargins(parent);

		GridData gd = new GridData();
		gd.widthHint = 80;
		gd.heightHint = 80;

		Composite avatarParent = pc.getComposite(parent);
		avatarParent.setLayout(new GridLayout());

		Image image = ResourceManager.getPluginImage("com.m4rc310.rcp.auth", "icons/grimace.png");

		Label avatar = pc.getLabel(avatarParent, "");
		avatar.setImage(image);

		avatar.setLayoutData(gd);

		Composite infoParent = pc.getComposite(parent);
		infoParent.setLayout(new GridLayout());

		Label labelName = pc.getLabel(infoParent, "Usuário de Testes".toUpperCase());
		engine.setClassname(labelName, "MyLabelUserFullName");

		Link linkGoogleNews = new Link(infoParent, 0);
		linkGoogleNews.setText("<a href=\"mailto:usuario@email.com\">usuario@email.com</a>");

		gd = new GridData();
		gd.widthHint = 220;
		gd.heightHint = 80;
		infoParent.setLayoutData(gd);

		super.createContentArea(parent);
	}
//
	@Override
	protected String getPopupShellTitle() {
		return m.titlePopupUserInfo;
	}

}
