package com.quickstep.ui.components;

import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.theme.lumo.Lumo;
import com.quickstep.ui.util.FontSize;
import com.quickstep.ui.util.FontWeight;
import com.quickstep.ui.util.LumoStyles;
import com.quickstep.ui.util.UIUtils;
import com.quickstep.ui.util.css.BorderRadius;

public class Initials extends FlexBoxLayout {

    private String CLASS_NAME = "initials";

    public Initials(String initials) {
        setAlignItems(FlexComponent.Alignment.CENTER);
        setBorderRadius(BorderRadius.L);
        setClassName(CLASS_NAME);
        UIUtils.setFontSize(FontSize.S, this);
        UIUtils.setFontWeight(FontWeight._600, this);
        setHeight(LumoStyles.Size.M);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        setTheme(Lumo.DARK);
        setWidth(LumoStyles.Size.M);

        add(initials);
    }

}
