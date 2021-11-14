package vn.com.personalfinance.services.expenditure.model;

import java.util.Date;

import domainapp.basics.exceptions.ConstraintViolationException;
import domainapp.basics.model.meta.AttrRef;
import domainapp.basics.model.meta.DAssoc;
import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DClass;
import domainapp.basics.model.meta.DOpt;
import domainapp.basics.model.meta.DAssoc.AssocEndType;
import domainapp.basics.model.meta.DAssoc.AssocType;
import domainapp.basics.model.meta.DAssoc.Associate;
import domainapp.basics.model.meta.DAttr.Type;
import vn.com.personalfinance.services.account.Account;
import vn.com.personalfinance.services.savings.Savings;
/**
 * Represents expenditure.
 * 
 * @author Nguyen Quynh Nga - Group 2
 * @version 1.0
 */
@DClass(schema="personalfinancemanagement")
public class ExpenditureSavings extends Expenditure{
	
	public static final String D_savings = "savings";
	// additional attribute
	@DAttr(name =D_savings, type = Type.Domain,  optional = false) 
	@DAssoc(ascName = "savings-has-expenditureSavings", role = "expenditureSavings",
	ascType = AssocType.One2Many, endType = AssocEndType.Many,
	associate = @Associate(type = Savings.class, cardMin = 1, cardMax = 1),
	dependsOn=true)
	private Savings savings;
	
	// constructor methods
	@DOpt(type = DOpt.Type.ObjectFormConstructor)
	public ExpenditureSavings(@AttrRef("amount") Double amount, 
			@AttrRef("date") Date date,
			@AttrRef("category") Category category, 
			@AttrRef("account") Account account,
			@AttrRef("savings") Savings savings) {
		this(null, amount, date, category, account, null, savings);
	}
	
	@DOpt(type = DOpt.Type.ObjectFormConstructor)
	public ExpenditureSavings(@AttrRef("amount") Double amount, 
			@AttrRef("date") Date date,
			@AttrRef("category") Category category, 
			@AttrRef("account") Account account,
			@AttrRef("description") String description,
			@AttrRef("savings") Savings savings) {
		this(null, amount, date, category, account, description, savings);
	}

	// a shared constructor that is invoked by other constructors
	@DOpt(type = DOpt.Type.DataSourceConstructor)
	public ExpenditureSavings(String id, Double amount, Date date, Category category, Account account, String description, Savings savings) {
		super(id, amount, date, category, account, description);
		this.savings = savings;
	}

	//getter
	public Savings getSavings() {
		return savings;
	}

	//setter
	public void setSavings(Savings savings) {
		this.savings = savings;
	}

	// automatically generate the next account id
	@Override
	public String nextID(String id) throws ConstraintViolationException {
		if (id == null) { // generate a new id
			idCounter++;
			return "E" + idCounter;
		} else {
			// update id
			int num;
			try {
				num = Integer.parseInt(id.substring(1));
			} catch (RuntimeException e) {
				throw new ConstraintViolationException(ConstraintViolationException.Code.INVALID_VALUE, e, new Object[] { id });
			}

			if (num > idCounter) {
				idCounter = num;
			}
			return id;
		}
	}

	@Override
	public void computeNewBalance() {
		double newBalance = getAccount().getBalance() - getAmount();
		if (newBalance >= 0) {
			getAccount().setBalance(newBalance);
		}
	}
}
