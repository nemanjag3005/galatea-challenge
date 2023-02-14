package com.quickstep.ui.components;

import com.quickstep.ui.util.Messages;
import com.vaadin.flow.component.textfield.TextField;

public class PhoneField extends TextField {
	public static String maskPhone = "+0 (000) 000-0000";

	public static String PHONE_FORMAT_VALIDATION_MESSAGE = Messages.Common.PHONE_FORMAT_VALIDATION;

	public PhoneField(String label) {
		super(label);
		setHelperText(maskPhone);
		addValueChangeListener(event -> {
			if (!event.getValue().isBlank()) {
				this.setValue(formatPhone(event.getValue()));
			}
		});
	}



	public static boolean isPhoneNotEmpty(String number) {
		return !number.isBlank();
	}
	public static boolean isPhoneValid(String number) {
		if (number.isEmpty()) { return true; }

		// mask: '+0 (000) 000-0000',
		// Removing Country code for now.
		return number.replaceAll("X", "").matches("^\\+[0-9]{1} \\([0-9]{3}\\) [0-9]{3}-[0-9]{4}");

	}

	public static String formatPhone(String number) {
		if(number == null || number.isEmpty()) { return ""; }
		String formattedNumber = number
				.replaceAll("[^0-9]", "")
				.replaceFirst("(\\d{1})(\\d{3})(\\d{3})(\\d+)", "+$1 ($2) $3-$4");
		return formattedNumber;
	}
}
