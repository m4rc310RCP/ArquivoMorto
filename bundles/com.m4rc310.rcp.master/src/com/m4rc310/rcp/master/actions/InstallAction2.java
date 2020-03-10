package com.m4rc310.rcp.master.actions;

import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
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
import org.eclipse.swt.widgets.Shell;

import com.m4rc310.rcp.master.i18n.Messages;
import com.m4rc310.rcp.ui.utils.MAction;
import com.m4rc310.rcp.ui.utils.streaming.MEvent;

@Creatable
//@Singleton
@SuppressWarnings("restriction")
public class InstallAction2 extends MAction {

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

	public void startInstall() {
		Job job = new Job(m.textInstalling) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {

				fire(PREPARE_TO_INSTALLING);

				ProvisioningSession session = new ProvisioningSession(agent);
				UpdateOperation operation = new UpdateOperation(session);

				SubMonitor sub = SubMonitor.convert(monitor, m.dialogMessageboxTitleInfo, 200);

				IStatus status = operation.resolveModal(sub.newChild(100));

				switch (status.getCode()) {
				case UpdateOperation.STATUS_NOTHING_TO_UPDATE:
					fire(PREPARE_TO_CLOSE);
					fire(PRINT_INFO, m.textNothingToUpdate);
					return Status.CANCEL_STATUS;
				case IStatusCodes.MISSING_REQUIREMENTS:
					fire(PREPARE_TO_CLOSE);
					fire(PRINT_INFO, m.messageErrorMissingRequirements);
					return Status.CANCEL_STATUS;
				case IStatusCodes.OPERATION_ALREADY_IN_PROGRESS:
					fire(PREPARE_TO_CLOSE);
					fire(PRINT_INFO, m.messageErrorOperationAlreadyInProgress);
					return Status.CANCEL_STATUS;
				}

				ProvisioningJob provisioningJob = operation.getProvisioningJob(sub.newChild(100));

				if (provisioningJob != null) {
					sync.syncExec(() -> {
						provisioningJob.addJobChangeListener(new JobChangeAdapter() {
							public void done(IJobChangeEvent event) {
								if (event.getResult().isOK()) {
									fire(PREPARE_TO_RESTART);
									fire(PRINT_INFO, m.commandAppUpdateRestartTitle);
									provisioningJob.done(Status.OK_STATUS);
								} else {
									fire(SHOW_DIALOG_ERROR, event.getResult().getMessage());
									cancelled = true;
								}
							};
						});
						
						provisioningJob.schedule();
						
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
					cancelled = false;
					fire(PREPARE_TO_CLOSE);
					return Status.CANCEL_STATUS;
				}

				return Status.OK_STATUS;
			}
		};

		fire(START_UPDATE, job);

//		job.schedule();
	}

	private void fire(String ref, Object... args) {
		sync.asyncExec(() -> {
			stream.fireListener(MEvent.event(this, ref, args));
		});
	}

	public void restartApp() {
		workbench.restart();
	}

}
