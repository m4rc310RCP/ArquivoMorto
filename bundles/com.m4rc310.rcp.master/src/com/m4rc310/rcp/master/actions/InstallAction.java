package com.m4rc310.rcp.master.actions;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.services.nls.Translation;

import com.m4rc310.rcp.master.i18n.Messages;
import com.m4rc310.rcp.ui.utils.MAction;
import com.m4rc310.rcp.ui.utils.streaming.MEvent;

@Creatable @Singleton
public class InstallAction extends MAction {
	
	@Inject @Translation Messages m;
	
	public static final String INSTALING = "instaling";
	public static final String PRINT_INFO = "print_info";
	
	public void startInstall() {
		fireMessage(true, m.textTryInstaling);
		fireMessage(true, m.textEmpty);
	}
	
	private void fireMessage(boolean instaling, String text) {
		stream.fireListener(MEvent.event(this, PRINT_INFO,text));
		stream.fireListener(MEvent.event(this, INSTALING, !instaling));
	}
	
}
