package com.m4rc310.rcp.ui.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.model.application.ui.basic.MStackElement;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.ResourceManager;

@Creatable
@Singleton
public class PartControl {

	@Inject
	MApplication application;
	@Inject
	EPartService partService;
	@Inject
	EModelService modelService;
	
	@Inject IEventBroker eventBroker;

	private final Map<Object, String> mapReferences = new HashMap<Object, String>();

	@Inject
	public PartControl() {

	}

	
	
	public String addNumberToLabel(Object ref, String text, int value) {
		if (mapReferences.containsKey(ref)) {
			return String.format("%s (%d)", mapReferences.get(ref), value);
		}

		mapReferences.put(ref, text);
		return String.format("%s (%d)", text, value);
	}

	public void visible(String partUri, boolean visible) {
		MPart part = partService.findPart(partUri);
		if (part == null) {
			part = partService.createPart(partUri);
		}

		for (String variable : part.getVariables()) {
			if (variable.contains("partStack:")) {
				variable = variable.replace("partStack:", "");

				MPartStack stack = modelService.findElements(application, variable, MPartStack.class, null).get(0);
				stack.setVisible(true);

				List<MStackElement> childres = stack.getChildren();
				childres.add(part);

				break;
			}
		}
		if (visible) {
			partService.showPart(part, PartState.ACTIVATE);
		} else {
			partService.hidePart(part);
		}
	}

	public CTabFolder createCTabFolder(Composite parent) {
		CTabFolder cTabFolder = new CTabFolder(parent, SWT.NONE);
		return cTabFolder;
	}

	public CTabItem createCTabItem(CTabFolder tabFolder, String title) {
		return createCTabItem(tabFolder, title, null);
	}

	public CTabItem createCTabItem(CTabFolder tabFolder, String title, Image icon) {
		CTabItem item = new CTabItem(tabFolder, SWT.NONE);
		item.setImage(icon);
		item.setText(title);
		return item;
	}

	public void margins(Composite composite, int width, int heigth, int vertical, int horizontal) {
		Layout layout = composite.getLayout();
		if (layout == null) {
			return;
		}

		if (layout instanceof GridLayout) {
			GridLayout gl = (GridLayout) layout;
			gl.verticalSpacing = vertical;
			gl.marginWidth = width;
			gl.marginHeight = heigth;
			gl.horizontalSpacing = horizontal;
		}
	}

	public void clearMargins(Composite... composites) {
		for (Composite composite : composites) {
			Layout layout = composite.getLayout();
			if (layout == null) {
				return;
			}

			if (layout instanceof GridLayout) {
				clearMargins((GridLayout) layout);
			}
		}

	}

	public void clearMargins(GridLayout gl) {
		gl.verticalSpacing = 0;
		gl.marginWidth = 0;
		gl.marginHeight = 0;
		gl.horizontalSpacing = 0;
	}

	public Image resize(Image image, int width, int height) {
		Image scaled = new Image(Display.getDefault(), width, height);
		GC gc = new GC(scaled);
		gc.setAntialias(SWT.ON);
		gc.setInterpolation(SWT.HIGH);
		gc.drawImage(image, 0, 0, image.getBounds().width, image.getBounds().height, 0, 0, width, height);
		gc.dispose();

		// Image data from scaled image and transparent pixel from original

		ImageData imageData = scaled.getImageData();

		imageData.transparentPixel = image.getImageData().transparentPixel;

		// Final scaled transparent image

		Image finalImage = new Image(Display.getDefault(), imageData);

		scaled.dispose();

		return finalImage;

//		Image scaled = new Image(Display.getDefault(), width, height);
//		GC gc = new GC(scaled);
//		gc.setAntialias(SWT.ON);
//		gc.setInterpolation(SWT.HIGH);
//		gc.drawImage(image, 0, 0, image.getBounds().width, image.getBounds().height, 0, 0, width, height);
//		gc.dispose();
//		image.dispose(); // don't forget about me!
//		return scaled;
	}

	public void show(String partUri, Object value) {
		show(partUri, null, value);
	}

	public void show(String partUri, String title, Object value) {
		show(partUri,partUri, title, value);
	}
	
	
	private final Map<String, MPart> partsMap = new HashMap<>();
	
	public void show(String partUri,String partId, String title, Object value) {
		
		try {
			
		
		MPart part;
		
		
		if(partsMap.containsKey(partId)) {
			part = partsMap.get(partId);
			part.setObject(value);
			partService.showPart(part, PartState.ACTIVATE);
			part.setLabel(title);
			
			if(value !=null) {
				eventBroker.send("update_report", part);
			}
			return;
		}
		
		part = partService.createPart(partUri);
		
		if (title != null) {
			part.setLabel(title);
		}
		
		part.setObject(value);
		
		for (String variable : part.getVariables()) {
			if (variable.contains("partStack:")) {
				variable = variable.replace("partStack:", "");

				MPartStack stack = modelService.findElements(application, variable, MPartStack.class, null).get(0);

				stack.setVisible(true);

				List<MStackElement> childres = stack.getChildren();
				childres.add(part);

				break;
			}
		}
		
		partService.showPart(part, PartState.ACTIVATE);
		partsMap.put(partId, part);
		
//		
//		MPart part;
//		part = partService.findPart(reportId);
//		
//		
//		if (part == null) {
//			part = partService.createPart(partUri);
//			part.setElementId(reportId);
//		}else {
//			part.setObject(value);
//			partService.showPart(part, PartState.ACTIVATE);
//			part.setLabel(title);
//
//			if(value !=null) {
//				eventBroker.send("update_report", value);
//			}
//			return;
//		}
//
//		if (title != null) {
//			part.setLabel(title);
//		}
//
//		part.setObject(value);
//
//		for (String variable : part.getVariables()) {
//			if (variable.contains("partStack:")) {
//				variable = variable.replace("partStack:", "");
//
//				MPartStack stack = modelService.findElements(application, variable, MPartStack.class, null).get(0);
//
//				stack.setVisible(true);
//
//				List<MStackElement> childres = stack.getChildren();
//				childres.add(part);
//
//				break;
//			}
//		}
//		
//		
//		partService.showPart(part, PartState.ACTIVATE);
//		
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public void show(String partUri) {
		show(partUri, null);
	}

	public Composite getComposite(Composite parent, int style) {
		Composite ret = new Composite(parent, style);
		ret.setLayout(new GridLayout());
		clearMargins(ret);
		return ret;
	}

	public Group getGroup(Composite parent, int style) {
		Group ret = new Group(parent, style);
		return ret;
	}

	public Group getGroup(Composite parent) {
		return getGroup(parent, SWT.NONE);
	}

	public Composite getComposite(Composite parent) {
		return getComposite(parent, SWT.NONE);
	}

	public Button getButton(Composite parent, String text) {
		return getButton(parent, text, SWT.PUSH, null);
	}

	public Button getButton(Composite parent, String text, Listener listener) {
		return getButton(parent, text, SWT.PUSH, listener);
	}

	public Button getButton(Composite parent, String text, int style, Listener listener) {
		Button ret = new Button(parent, style);
		ret.setText(text);
		if (listener != null) {
			ret.addListener(SWT.Selection, listener);
		}
		return ret;
	}

	public Label getLabel(Composite parent, String text) {
		return getLabel(parent, text, SWT.NONE);
	}

	public Label getLabel(Composite parent, String text, int style) {
		Label ret = new Label(parent, style);
		ret.setText(text);
		return ret;
	}

	public Label getIcon(Composite parent, String plugin, String path) {
		Label ret = getLabel(parent, "");
		Image img = ResourceManager.getPluginImage(plugin, path);
		ret.setImage(img);
		return ret;
	}

	public Text getText(Composite parent, String text) {
		return getText(parent, text, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
	}

	public Text getText(Composite parent, String text, int style) {
		Text ret = new Text(parent, style);
		ret.setText(text);
		return ret;
	}

	public void createColumn(TreeViewer viewer, String title, int width, int style, IBaseLabelProvider labelProvider) {
		TreeViewerColumn viewerColumn = new TreeViewerColumn(viewer, style);
		viewerColumn.getColumn().setWidth(width);
		viewerColumn.getColumn().setText(title);
		viewerColumn.setLabelProvider((CellLabelProvider) labelProvider);
	}

	public void createCollumn(TableViewer viewer, int style, String title, int width, IBaseLabelProvider provider) {
		TableViewerColumn colFirstName = new TableViewerColumn(viewer, style);
		colFirstName.getColumn().setWidth(width);
		colFirstName.getColumn().setText(title);
		colFirstName.setLabelProvider((CellLabelProvider) provider);
	}

}
