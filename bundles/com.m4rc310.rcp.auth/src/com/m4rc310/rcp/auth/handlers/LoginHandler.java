
package com.m4rc310.rcp.auth.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledToolItem;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.dialogs.Dialog;

import com.m4rc310.rcp.auth.actions.LoginAction;
import com.m4rc310.rcp.auth.dialogs.DialogLogin;

public class LoginHandler {

	@Inject
	LoginAction action;

	private DialogLogin dialogLogin;

	@Inject
	UISynchronize sync;

	@Inject
	EModelService modelService;

	@Inject
	public LoginHandler() {
	}

//	@Inject  IEclipseContext context;

	
	
	
	public void nexecute(MApplication mApplication, IEclipseContext context) {

		MHandledToolItem item = (MHandledToolItem) modelService.find("com.m4rc310.rcp.auth.handledtoolitem.0",
				mApplication);

		boolean state = item.isSelected();

		if (state) {
			item.setIconURI("platform:/plugin/com.m4rc310.rcp.auth/icons/lock_open.png");
			this.dialogLogin = ContextInjectionFactory.make(DialogLogin.class, context);

			int res = dialogLogin.open();
			if (res == Dialog.OK) {
				item.setSelected(true);
			} else {
				item.setSelected(false);
				item.setIconURI("platform:/plugin/com.m4rc310.rcp.auth/icons/lock.png");
			}
		} else {
			action.logout();
			item.setIconURI("platform:/plugin/com.m4rc310.rcp.auth/icons/lock.png");
		}

//		IEclipseContext context = EclipseContextFactory.create();
//		System.out.println(command.getCommand());
	}

	@Execute
	public void execute(IEclipseContext context) {
		action.loginLogout(context);

//		IEclipseContext context = EclipseContextFactory.create();
//
//		System.out.println(context);
//
////		sync.syncExec(() -> {
//
////		E4Application.createDefaultContext();
//
//		boolean state = item.isSelected();
////			updateIcon(item, state);
//
//		if (state) {
//			item.setIconURI("platform:/plugin/com.m4rc310.rcp.auth/icons/lock_open.png");
////			this.dialogLogin = ContextInjectionFactory.make(DialogLogin.class, Activator.context);
//
////			int res = dialogLogin.open();
////			if (res == Dialog.OK) {
////				item.setSelected(true);
////			} else {
////				item.setSelected(false);
////				item.setIconURI("platform:/plugin/com.m4rc310.rcp.auth/icons/lock.png");
////			}
//		} else {
//			action.logout();
//			item.setIconURI("platform:/plugin/com.m4rc310.rcp.auth/icons/lock.png");
//		}
	}


}