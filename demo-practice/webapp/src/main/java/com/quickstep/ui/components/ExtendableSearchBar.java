package com.quickstep.ui.components;

import com.quickstep.ui.util.Messages;
import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

public class ExtendableSearchBar extends HorizontalLayout {

	private TextField search;
	private Button clearSearch;

	public ExtendableSearchBar() {
		setWidthFull();
		initSearchField();
	}

	private void initSearchField() {
		clearSearch = new Button(Messages.Common.CLEAR_SEARCH);
		clearSearch.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		search = new TextField();
		search.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
		search.setPlaceholder(Messages.Common.SEARCH);
		search.setValueChangeMode(ValueChangeMode.EAGER);
		search.setWidth("400px");
		search.getElement().setAttribute("theme", "white");
		var componentContainer = new HorizontalLayout(clearSearch, search);
		componentContainer.getStyle().set("margin-left", "auto");
		add(componentContainer);
	}

	public void addSearchValueChangeListener(ValueChangeListener<ValueChangeEvent<String>> e) {
		search.addValueChangeListener(e);
	}
}
