 
package com.m4rc310.rcp.auth.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.CanExecute;

public class LoginHandler {
	@Execute
	public void execute() {
		System.out.println("---");
	}
	
	
	@CanExecute
	public boolean canExecute() {
		
		return true;
	}
		
}