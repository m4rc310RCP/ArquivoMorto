
package com.m4rc310.rcp.auth.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.workbench.UIEvents;

import com.m4rc310.rcp.auth.actions.LoginAction;
import com.m4rc310.rcp.auth.popups.UserPopup;

public class UserInfoHandler {

	boolean online = false;

	@Inject
	UISynchronize sync;

	@Inject
	IEventBroker eventBroker;

	@Inject
	public UserInfoHandler(LoginAction action) {
		action.addListener(this, LoginAction.INFORM_ON_OFF_LINE, e -> {
			online = e.getValue(boolean.class);
			eventBroker.send(UIEvents.REQUEST_ENABLEMENT_UPDATE_TOPIC, UIEvents.ALL_ELEMENT_ID);
		});
	}

	@Execute
	public void execute(UserPopup popup) {
		
		
		sync.syncExec(() -> {
			popup.setDelayClose(1000l);
			popup.open();
		});
	}

	@CanExecute
	public boolean canExecute() {
		
		return online;
	}

}