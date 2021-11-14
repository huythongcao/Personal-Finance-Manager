package vn.com.personalfinance.exceptions;

import java.text.MessageFormat;

import domainapp.basics.util.InfoCode;

public enum DExCode implements InfoCode{
	/**
	 * 0: savingsTransaction
	 */
	INVALID_LOG("SavingsTransaction cardinality {0} is over maximum transaction"),
	
	/**
	 * 0: Balance
	 */
	INVALID_BALANCE("Account balance {0} is invalid");
	
	/**
	 * THE FOLLOWING CODE (EXCEPT FOR THE CONSTRUCTOR NAME) MUST BE KEPT AS IS
	 */
	private String text;

	/**
	 * The {@link MessageFormat} object for formatting {@link #text} using
	 * context-specific data arguments
	 */
	private MessageFormat messageFormat;

	private DExCode(String text) {
		this.text = text;
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public MessageFormat getMessageFormat() {
		if (messageFormat == null) {
			messageFormat = new MessageFormat(text);
		}

		return messageFormat;
	}
}
