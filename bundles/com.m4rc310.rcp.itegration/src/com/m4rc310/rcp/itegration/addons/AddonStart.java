 
package com.m4rc310.rcp.itegration.addons;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.EventTopic;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledToolItem;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.osgi.service.event.Event;


@SuppressWarnings("restriction")
public class AddonStart {
	
	@Inject
	EModelService modelService;
	
	
	@Inject
	MApplication application;

	@Inject
	@Optional
	public void applicationStarted(
			@EventTopic(UIEvents.UILifeCycle.APP_STARTUP_COMPLETE) Event event) {
		
	}
	
	@Inject @Optional
	public void informLock(@UIEventTopic("enable_import_command")Boolean enable) {
		MHandledToolItem item = (MHandledToolItem) modelService.find("com.m4rc310.rcp.itegration.handledtoolitem.import.database",
				application);
		
		item.setVisible(enable);
	}

}
