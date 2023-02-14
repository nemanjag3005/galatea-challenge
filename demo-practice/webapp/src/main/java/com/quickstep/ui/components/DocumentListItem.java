package com.quickstep.ui.components;

import com.quickstep.ui.layout.size.Right;
import com.quickstep.ui.layout.size.Wide;
import com.quickstep.ui.util.FontSize;
import com.quickstep.ui.util.TextColor;
import com.quickstep.ui.util.UIUtils;
import com.quickstep.ui.util.css.WhiteSpace;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;

@CssImport("./styles/components/list-item.css")
public class DocumentListItem extends FlexBoxLayout {

    private final String CLASS_NAME = "list-item";

    private Div prefix;

    private FlexBoxLayout content;

    private Div primary;
    private Label secondary;

    public DocumentListItem(Div primary, String secondary) {
        addClassName(CLASS_NAME);

        setAlignItems(Alignment.CENTER);
        setPadding(Wide.RESPONSIVE_L);
        setSpacing(Right.L);

        this.primary = new Div(primary);
        this.secondary = UIUtils.createLabel(FontSize.S, TextColor.SECONDARY,
                secondary);

        content = new FlexBoxLayout(this.primary, this.secondary);
        content.setClassName(CLASS_NAME + "__content");
        content.setFlexDirection(FlexDirection.COLUMN);
        add(content);
    }

    /* === PREFIX === */

    public DocumentListItem(Component prefix, Div primary, String secondary) {
        this(primary, secondary);
        setPrefix(prefix);
    }

    /* === MISC === */

    public FlexBoxLayout getContent() {
        return content;
    }

    public void setWhiteSpace(WhiteSpace whiteSpace) {
        UIUtils.setWhiteSpace(whiteSpace, this);
    }

    public void setReverse(boolean reverse) {
        if (reverse) {
            content.setFlexDirection(FlexDirection.COLUMN_REVERSE);
        } else {
            content.setFlexDirection(FlexDirection.COLUMN);
        }
    }

    public void setHorizontalPadding(boolean horizontalPadding) {
        if (horizontalPadding) {
            getStyle().remove("padding-left");
            getStyle().remove("padding-right");
        } else {
            getStyle().set("padding-left", "0");
            getStyle().set("padding-right", "0");
        }
    }

    public Div getPrimary() {
        return primary;
    }

    public void setPrefix(Component... components) {
        if (prefix == null) {
            prefix = new Div();
            prefix.setClassName(CLASS_NAME + "__prefix");
            getElement().insertChild(0, prefix.getElement());
            getElement().setAttribute("with-prefix", true);
        }
        prefix.removeAll();
        prefix.add(components);
    }

    public void setDividerVisible(boolean visible) {
        if (visible) {
            getElement().setAttribute("with-divider", true);
        } else {
            getElement().removeAttribute("with-divider");
        }
    }

}
