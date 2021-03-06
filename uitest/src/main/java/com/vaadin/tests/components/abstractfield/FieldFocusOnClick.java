package com.vaadin.tests.components.abstractfield;

import java.util.Arrays;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.NativeButton;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.TextField;

public class FieldFocusOnClick extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(new TextField(null, "TextField"));
        addComponent(new CheckBox("CheckBox"));
        addComponent(
                new OptionGroup(null, Arrays.asList("Option 1", "Option 2")));
        addComponent(new NativeButton("NativeButton"));
    }

    @Override
    protected String getTestDescription() {
        return "Webkit doesn't focus non-text input elements when clicked";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11854;
    }
}
