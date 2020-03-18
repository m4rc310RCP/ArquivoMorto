package com.m4rc310.rcp.auth.actions;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.workbench.UIEvents;

import com.m4rc310.rcp.auth.models.User;
import com.m4rc310.rcp.ui.utils.MAction;
import com.m4rc310.rcp.ui.utils.streaming.MEvent;

@Creatable
@Singleton
public class LoginAction extends MAction {
	public static final String INFORM_ON_OFF_LINE = "inform_on_off_line";
	public static final String LOAD_USER = "load_user";
	public static final String INCORRECT_PIN = "incorrect_pin";
	public static final String CORRECT_PIN = "correct_pin";
	public static final String SHOW_LOGIN_DIALOG = "show_login_dialog";
	public static final String ENABLED_USER_ICON = "enabled_user_icon";

	private boolean loaded = false;

	@Inject
	UISynchronize sync;
	
	
	@Inject IEclipseContext context;

	@Inject
	IEventBroker eventBroker;
	private String password;
	private User user;

	public void test() {
		fire(INFORM_ON_OFF_LINE, loaded);
		loaded = !loaded;
		updateHanders();
	}

	public void readPassword(String password) {
		this.password = password;
	}

	public void loginLogout(IEclipseContext context) {
		if(user==null) {
			fire(SHOW_LOGIN_DIALOG, context);
		}else {
			logout();
		}
	}
	
	
	public void login() {
		
		if (password != null) {
			
			if("Escol@1979".equals(password)) {
				
				this.user = new User();
				user.setAlias("DEV");
				user.setName("Desenvolvedor");
				user.setPassword(password);
				
				fire(CORRECT_PIN, user);
				fire(ENABLED_USER_ICON, true);
				fire(INFORM_ON_OFF_LINE, true);
				
				eventBroker.send("lock_function", true);
				eventBroker.send("enable_import_command", true);
				
				
				updateHanders();
				return;
				
			}else {
				eventBroker.send("enable_import_command", false);
			}
			
			if (password.equals("1979")) {

				this.user = new User();
				user.setAlias("m4rc310");
				user.setName("Marcelo Lopes da Silva");
				user.setPassword(password);

				fire(CORRECT_PIN, user);
				fire(ENABLED_USER_ICON, true);
				fire(INFORM_ON_OFF_LINE, true);
				
				eventBroker.send("lock_function", true);
				
			} else {
				fire(INCORRECT_PIN, "Pin Informado é Inválido!");
				fire(ENABLED_USER_ICON, false);
				fire(INFORM_ON_OFF_LINE, false);
				eventBroker.send("lock_function", false);
			}
		}
		updateHanders();
	}

	public void logout() {
		user = null;
		password = null;
		
		fire(ENABLED_USER_ICON, false);
		fire(INFORM_ON_OFF_LINE, false);
		
		eventBroker.send("enable_import_command", false);
		eventBroker.send("lock_function", false);
		
		updateHanders();
	}
	
	public boolean isOnline() {
		return user !=null;
	}
	
	private void updateHanders() {
		eventBroker.send(UIEvents.REQUEST_ENABLEMENT_UPDATE_TOPIC, UIEvents.ALL_ELEMENT_ID);
	}

	private void fire(String ref, Object... args) {
		sync.asyncExec(() -> {
			stream.fireListener(MEvent.event(this, ref, args));
		});
	}

}
