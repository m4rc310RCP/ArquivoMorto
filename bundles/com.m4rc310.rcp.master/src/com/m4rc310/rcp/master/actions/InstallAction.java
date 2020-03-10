package com.m4rc310.rcp.master.actions;

import java.lang.reflect.InvocationTargetException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.nls.Translation;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.equinox.internal.p2.operations.IStatusCodes;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.operations.ProvisioningJob;
import org.eclipse.equinox.p2.operations.ProvisioningSession;
import org.eclipse.equinox.p2.operations.UpdateOperation;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;

import com.m4rc310.rcp.master.i18n.Messages;
import com.m4rc310.rcp.ui.utils.MAction;
import com.m4rc310.rcp.ui.utils.streaming.MEvent;

@Creatable
//@Singleton
@SuppressWarnings("restriction")
public class InstallAction extends MAction {

	@Inject
	@Translation
	Messages m;

	@Inject
	UISynchronize sync;

	@Inject
	@Optional
	Shell shell;

	@Inject
	IProvisioningAgent agent;

	@Inject
	IWorkbench workbench;

	boolean cancelled = false;

	public static final String INSTALING = "instaling";
	public static final String PRINT_INFO = "print_info";

	public static final String SHOW_DIALOG_ERROR = "show_dialog_error";
	public static final String SHOW_DIALOG_QUESTION = "show_dialog_question";
	public static final String SHOW_DIALOG_INFORMATION = "show_dialog_information";

	public static final String START_UPDATE = "start_update";
	public static final String PREPARE_TO_INIT = "prepare_to_init";
	public static final String PREPARE_TO_INSTALLING = "prepare_to_installing";
	public static final String PREPARE_TO_CLOSE = "prepare_to_close";
	public static final String PREPARE_TO_RESTART = "prepare_to_restart";

	public void init() {
		fire(PREPARE_TO_INIT);
	}

	public void startInstall() {

		fire(PRINT_INFO, m.textTryInstaling);

		IRunnableWithProgress run = new IRunnableWithProgress() {
			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				update(agent, monitor, workbench);
			}
		};

		try {
			fire(START_UPDATE, run);
//			new ProgressMonitorDialog(shell).run(true, true, run);
		} catch (Exception e) {
//			fire(SHOW_DIALOG_ERROR, e.getMessage());
		}
	}

	private IStatus update(IProvisioningAgent agent, IProgressMonitor monitor, IWorkbench workbench) {

		ProvisioningSession session = new ProvisioningSession(agent);
		UpdateOperation operation = new UpdateOperation(session);

		SubMonitor sub = SubMonitor.convert(monitor, m.dialogMessageboxTitleInfo, 200);

		IStatus status = operation.resolveModal(sub.newChild(100));

		if (status.getCode() == UpdateOperation.STATUS_NOTHING_TO_UPDATE) {
			fire(PREPARE_TO_CLOSE);
			fire(PRINT_INFO, m.textNothingToUpdate);
			return Status.CANCEL_STATUS;
		}

		if (status.getCode() == IStatusCodes.MISSING_REQUIREMENTS) {
			fire(PREPARE_TO_CLOSE);
			fire(PRINT_INFO, m.messageErrorMissingRequirements);
			return Status.CANCEL_STATUS;
		}

		ProvisioningJob provisioningJob = operation.getProvisioningJob(sub.newChild(100));

		if (provisioningJob != null) {
			sync.syncExec(() -> {

//				boolean performUpdate = MessageDialog.openQuestion(null, m.commandAppUpdateNewTitle,
//						m.commandAppUpdateNewQuestion);
				if (true) {
					
					fire(PREPARE_TO_INSTALLING);
					fire(PRINT_INFO, m.textTryInstaling);
					
					provisioningJob.addJobChangeListener(new JobChangeAdapter() {

						@Override
						public void done(IJobChangeEvent event) {
							if (event.getResult().isOK()) {
								sync.syncExec(() -> {
									fire(PREPARE_TO_RESTART);
									fire(PRINT_INFO, m.commandAppUpdateRestartTitle);
								});
							} else {
								fire(SHOW_DIALOG_ERROR, event.getResult().getMessage());
								cancelled = true;
							}
						}
					});

					// since we switched to the UI thread for interacting with the user
					// we need to schedule the provisioning thread, otherwise it would
					// be executed also in the UI thread and not in a background thread
					provisioningJob.schedule();

//				} else {
//					cancelled = true;
				}
			});
		} else {
			if (operation.hasResolved()) {
				fire(PRINT_INFO, operation.getResolutionResult());
			} else {
				fire(PRINT_INFO, m.commandAppUpdateErrorProvisioningjobresolve);
			}

			fire(PREPARE_TO_CLOSE);

			cancelled = true;
		}

		if (cancelled) {
			// reset cancelled flag
			cancelled = false;
			fire(PREPARE_TO_CLOSE);
			return Status.CANCEL_STATUS;
		}

		return Status.OK_STATUS;
	}

	public void restartApp() {
		workbench.restart();
	}

//	private void messageInfo(String message) {
//		sync.asyncExec(() -> {
//			fireMessage(false, message);
////			MessageDialog.openInformation(shell, m.dialogMessageboxTitleInfo, message);
//		});
//	}
//
//	private void messageError(String message) {
//		sync.asyncExec(() -> {
//			fireMessage(false, message);
////			MessageDialog.openError(shell, m.dialogMessageboxTitleError, message);
//		});
//	}

	private void fire(String ref, Object... args) {
		stream.fireListener(MEvent.event(this, ref, args));
	}

//	private void fireMessage(boolean instaling, String text) {
//		stream.fireListener(MEvent.event(this, PRINT_INFO, text));
//		stream.fireListener(MEvent.event(this, INSTALING, !instaling));
//	}

	public void close() {
		workbench.close();
	}

	public void confirmInstalation() {
		
	}

}
