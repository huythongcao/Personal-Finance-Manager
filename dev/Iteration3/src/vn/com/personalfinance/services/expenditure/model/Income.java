package vn.com.personalfinance.services.expenditure.model;

import java.util.Date;

import domainapp.basics.exceptions.ConstraintViolationException;
import domainapp.basics.model.meta.AttrRef;
import domainapp.basics.model.meta.DClass;
import domainapp.basics.model.meta.DOpt;
import vn.com.personalfinance.services.account.Account;
/**
 * Represents income.
 * 
 * @author Nguyen Quynh Nga - Group 2
 * @version 1.0
 */
@DClass(schema="personalfinancemanagement")
public class Income extends DailyExpense {
	// constructor methods
	@DOpt(type = DOpt.Type.ObjectFormConstructor)
	public Income(@AttrRef("amount") Double amount, 
			@AttrRef("date") Date date, 
			@AttrRef("category") Category category,
			@AttrRef("account") Account account, 
			@AttrRef("description") String description) {
		this(null, amount, date, category, account, description);
		
	}

	// a shared constructor that is invoked by other constructors
	@DOpt(type = DOpt.Type.DataSourceConstructor)
	public Income(String id, Double amount, Date date, Category category, Account account, String description) {
		super(id, amount, date, category, account, description);
		
	}
	
	// automatically generate the next account id
	@Override
	public String nextID(String id) throws ConstraintViolationException {
		if (id == null) { // generate a new id

			idCounter++;

			return "I" + idCounter;
		} else {
			// update id
			int num;
			try {
				num = Integer.parseInt(id.substring(1));
			} catch (RuntimeException e) {
				throw new ConstraintViolationException(ConstraintViolationException.Code.INVALID_VALUE, e,
						new Object[] { id });
			}

			if (num > idCounter) {
				idCounter = num;
			}

			return id;
		}
	}

	@Override
	public void computeNewBalance() {
		double newBalance = getAccount().getBalance() + getAmount();
		getAccount().setBalance(newBalance);
	}

}
