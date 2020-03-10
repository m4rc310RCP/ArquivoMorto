
package com.m4rc310.rcp.master.handlers;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.nls.Translation;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.equinox.internal.p2.operations.IStatusCodes;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.operations.ProvisioningJob;
import org.eclipse.equinox.p2.operations.ProvisioningSession;
import org.eclipse.equinox.p2.operations.UpdateOperation;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;

import com.m4rc310.rcp.master.dialogs.SearchUpdateDialog;
import com.m4rc310.rcp.master.i18n.Messages;


@SuppressWarnings("restriction")
public class UpdateHandler2 {

	@Inject UISynchronize sync;
	
	@Inject @Optional Shell shell;
	
	@Inject @Translation Messages m;
	
	boolean cancelled = false;
	
	
	@Execute
	public void execute2(IEclipseContext context) {
		SearchUpdateDialog dialog = ContextInjectionFactory.make(SearchUpdateDialog.class, context);
		dialog.open();
	}
	
	public void execute(final IProvisioningAgent agent, IWorkbench workbench) {

		IRunnableWithProgress run = new IRunnableWithProgress() {
			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				update(agent, monitor, workbench);
			}
		};

		try {
			
			///mmmmm
			new ProgressMonitorDialog(shell).run(true, true, run);
		} catch (Exception e) {
			messageError(e.getMessage());
		}

	}

	private IStatus update(IProvisioningAgent agent, IProgressMonitor monitor,
			IWorkbench workbench) {

		ProvisioningSession session = new ProvisioningSession(agent);
		UpdateOperation operation = new UpdateOperation(session);
		
		SubMonitor sub = SubMonitor.convert(monitor, m.dialogMessageboxTitleInfo, 200);
		
		IStatus status = operation.resolveModal(sub.newChild(100));
		
		if(status.getCode() == UpdateOperation.STATUS_NOTHING_TO_UPDATE) {
			messageInfo(m.textNothingToUpdate);
			return Status.CANCEL_STATUS;
		}
		
		if(status.getCode() == IStatusCodes.MISSING_REQUIREMENTS) {
			messageError(m.messageErrorMissingRequirements);
			return Status.CANCEL_STATUS;
		}

		ProvisioningJob provisioningJob = operation.getProvisioningJob(sub.newChild(100));
		
		if(provisioningJob!=null) {
			sync.syncExec(()->{
				
				boolean performUpdate = MessageDialog.openQuestion(null, m.commandAppUpdateNewTitle,
						m.commandAppUpdateNewQuestion);
				if (performUpdate) {
					provisioningJob.addJobChangeListener(new JobChangeAdapter() {

						@Override
						public void done(IJobChangeEvent event) {
							if (event.getResult().isOK()) {
								sync.syncExec(() -> {
									boolean restart = MessageDialog.openQuestion(null, m.commandAppUpdateRestartTitle,
											m.commandAppUpdateRestartQuestion);
									if (restart) {
										workbench.restart();
									}
								});
							} else {
								messageError(event.getResult().getMessage());
								cancelled = true;
							}
						}
					});

					// since we switched to the UI thread for interacting with the user
					// we need to schedule the provisioning thread, otherwise it would
					// be executed also in the UI thread and not in a background thread
					provisioningJob.schedule();

				} else {
					cancelled = true;
				}
			});
		}else {
			if (operation.hasResolved()) {
				String msg = MessageFormat.format(m.commandAppUpdateErrorProvisioningjob, operation.getResolutionResult());
				messageError(msg);
			} else {
				messageError(m.commandAppUpdateErrorProvisioningjobresolve);
			}
			cancelled = true;
		}
		
		if (cancelled) {
			// reset cancelled flag
			cancelled = false;
			return Status.CANCEL_STATUS;
		}
		
		return Status.OK_STATUS;
	}
	
	
	private void messageInfo(String message) {
		sync.asyncExec(()->{
			MessageDialog.openInformation(shell, m.dialogMessageboxTitleInfo, message);
		});
	}
	

	private void messageError(String message) {
		sync.asyncExec(()->{
			MessageDialog.openError(shell, m.dialogMessageboxTitleError, message);
		});
	}

	@CanExecute
	public boolean canExecute() {

		return true;
	}

}