package com.m4rc310.rcp.master.dialogs;

import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.ProgressProvider;
import org.eclipse.e4.core.services.nls.Translation;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.services.IStylingEngine;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.wb.swt.SWTResourceManager;

import com.m4rc310.rcp.master.actions.InstallAction2;
import com.m4rc310.rcp.master.i18n.Messages;
import com.m4rc310.rcp.ui.utils.PartControl;

public class SearchUpdateDialog extends Dialog {

	@Inject
	PartControl pc;

	@Inject
	@Translation
	Messages m;

	@Inject
	IStylingEngine engine;

	@Inject
	UISynchronize sync;

	@Inject
	InstallAction2 action;
	
	
	private final int ACTION_NONE = 0;
	private final int ACTION_CANCEL = 1;
	private final int ACTION_RESTART = 3;

	private int actionButton = ACTION_NONE;

	@Inject
	public SearchUpdateDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent_) {
		Composite parent = pc.getComposite(parent_, SWT.WRAP);
		parent.setLayout(new GridLayout());

		Group group = pc.getGroup(parent);
		group.setLayout(new GridLayout(1, true));
		GridData data = new GridData(SWT.FILL, SWT.TOP, true, false);
		data.widthHint = 380;

		group.setLayoutData(data);

		Label label = pc.getLabel(group, m.textTrySearchUpdates, SWT.WRAP);
		data = new GridData(SWT.FILL, SWT.TOP, true, false);
		data.widthHint = 370;
		label.setLayoutData(data);

		Label labelStatus = pc.getLabel(group, m.labelTestShortText, SWT.WRAP | SWT.CENTER);
		labelStatus.setFont(SWTResourceManager.getFont("Arial", 9, SWT.NORMAL, false, false));
		labelStatus.setForeground(ResourceManager.getColor(0x201DFC));

		data = new GridData(SWT.FILL, SWT.TOP, true, false);
		labelStatus.setLayoutData(data);

		engine.setId(labelStatus, "MyButtonInstall");

		ProgressBar pb = new ProgressBar(group, SWT.HORIZONTAL);
		pb.setBounds(10, 10, 200, 20);

		data = new GridData(SWT.FILL, SWT.TOP, true, false);
		pb.setLayoutData(data);

		action.addListener(InstallAction2.START_UPDATE, e -> {
			Job job = e.getValue(0, Job.class);

			IProgressMonitor monitor = new ProgressMonitorAdapter() {

				@Override
				public void beginTask(String name, int totalWork) {
					sync.syncExec(() -> {
						labelStatus.setText(name);
						pb.setMaximum(totalWork);
					});
				}

				@Override
				public void worked(int work) {
					sync.syncExec(() -> {
						pb.setSelection(work);
					});
				}

				@Override
				public void done() {
					sync.syncExec(() -> {
						if(!pb.isDisposed())
						pb.setSelection(pb.getMaximum());
					});
				}

			};

			IJobManager manager = Job.getJobManager();

			ProgressProvider provider = new ProgressProvider() {
				@Override
				public IProgressMonitor createMonitor(Job job) {
					return monitor;
				}
			};

			manager.setProgressProvider(provider);

			job.schedule();
		});

		action.addListener(InstallAction2.PRINT_INFO, e -> {
			String st = e.getValue(0, String.class);
			labelStatus.setText(st);
		});

		action.addListener(InstallAction2.PREPARE_TO_RESTART, e -> {
			actionButton = ACTION_RESTART;
			Button button = getButton(IDialogConstants.CANCEL_ID);
			button.setText(m.textRestart);
			button.getParent().layout();
			getShell().setDefaultButton(button);
		});

		action.addListener(InstallAction2.PREPARE_TO_CLOSE, e -> {
			actionButton = ACTION_CANCEL;
			Button button = getButton(IDialogConstants.CANCEL_ID);
			button.setText(m.textClose);
			button.getParent().layout();
		});

		
		
		action.addListener(InstallAction2.PREPARE_TO_INSTALLING, e -> {
			actionButton = ACTION_CANCEL;
			Button button = getButton(IDialogConstants.CANCEL_ID);
			button.setText(m.textCancel);
			button.getParent().layout();
		});

		action.startInstall();

		return parent;
	}

	@Override
	protected void cancelPressed() {

		switch (actionButton) {
		case ACTION_CANCEL:
			action.cancel();
			super.cancelPressed();
			break;

		case ACTION_RESTART:
			action.restartApp();
			super.cancelPressed();
			break;
			
			
		case ACTION_NONE:
		default:
			action.cancel();
			super.cancelPressed();
		}

	}

	@Override
	protected Control createButtonBar(Composite parent_) {
		Composite parent = pc.getComposite(parent_);
		parent.setLayout(new GridLayout(2, false));
		parent.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Button buttonClose = createButton(parent, IDialogConstants.CANCEL_ID, m.textClose, false);
		buttonClose.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));

//		action.getStream().addListener(InstallAction.PREPARE_TO_CLOSE, e -> {}

		return parent;
	}

}
