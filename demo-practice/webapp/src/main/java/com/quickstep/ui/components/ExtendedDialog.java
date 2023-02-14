package com.quickstep.ui.components;

import com.quickstep.ui.util.UIUtils;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/**
 * Extended dialog that has predefined content and button layout.
 *
 * @author tam nguyen
 */
public class ExtendedDialog extends Dialog {
	private final VerticalLayout contentLayout;
	private final VerticalLayout buttonLayout;
	private final VerticalLayout headerLayout;

	private Component uiContent;

	public ExtendedDialog(int width, int height) {
		setWidth(width, Unit.PIXELS);
		setHeight(height, Unit.PIXELS);
		setModal(true);
		setCloseOnOutsideClick(false);
		headerLayout = new VerticalLayout();
		headerLayout.setPadding(false);
		headerLayout.getStyle().set("margin-bottom", "auto");
		headerLayout.setWidthFull();
		contentLayout = new VerticalLayout();
		contentLayout.setPadding(false);
		contentLayout.setSizeFull();
		buttonLayout = new VerticalLayout();
		buttonLayout.setPadding(false);
		buttonLayout.setWidthFull();
		buttonLayout.getStyle().set("margin-top", "auto");
		contentLayout.add(headerLayout, buttonLayout);
		add(contentLayout);
	}

	public void setButtons(Button...buttons) {
		setButtons(true, buttons);
	}

	public void setButtons(Boolean lastButtonPrimary, Button...buttons) {
		var buttonAlignLayout = new HorizontalLayout();
		for (Button button : buttons)
			buttonAlignLayout.add(button);
		if(lastButtonPrimary)
			buttons[buttons.length - 1].addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		buttonLayout.removeAll();
		buttonLayout.add(buttonAlignLayout);
		buttonLayout.setDefaultHorizontalComponentAlignment(Alignment.END);
	}

	public void setUIContent(Component ui) {
		if (uiContent != null) {
			contentLayout.remove(uiContent);
		}
		uiContent = ui;
		uiContent.getElement().getStyle().set("flex-grow", "1");
		contentLayout.addComponentAtIndex(1, uiContent);
	}

	public void setHeader(String header) {
		headerLayout.add(UIUtils.createH4Label(header));
	}

	public interface IDialogCloseCallback {
		void dialogClosed();
	}
}
