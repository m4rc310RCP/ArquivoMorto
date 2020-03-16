package com.m4rc310.rcp.auth.addon;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.EventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledToolItem;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.osgi.service.event.Event;

import com.m4rc310.rcp.auth.actions.LoginAction;

@SuppressWarnings("restriction")
public class UIControl {

	@Inject
	LoginAction action;
	
	@Inject
	EModelService modelService;
	
	@Inject
	MApplication application;

	@Inject
	@Optional
	public void applicationStarted(@EventTopic(UIEvents.UILifeCycle.APP_STARTUP_COMPLETE) Event event) {
		action.addListener(LoginAction.INFORM_ON_OFF_LINE, e->{
			MHandledToolItem item = (MHandledToolItem) modelService.find("com.m4rc310.rcp.auth.handledtoolitem.0",
					application);
			
			System.out.println(item);
		});
	}

}
