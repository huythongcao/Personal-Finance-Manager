package vn.com.personalfinance.services.savings;

import java.util.Collection;
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
import domainapp.basics.util.cache.StateHistory;
import vn.com.personalfinance.services.account.Account;
import vn.com.personalfinance.services.expenditure.model.ExpenditureSavings;

/**
 * Represents a saving book.
 * 
 * @author Nguyen Hai - Group 2
 * @version 1.0
 */
@DClass(schema="personalfinancemanagement")
public class SavingsBook extends Savings {
	public static final String S_monthlyDuration = "monthlyDuration";
	public static final String S_interestRate = "interestRate";
	public static final String S_finalBalance = "finalBalance";
	
	// attributes of savings book
	@DAttr(name = S_monthlyDuration, type = Type.Integer, length = 2, optional = false) 
	private int monthlyDuration;
	
	@DAttr(name = S_account, type = Type.Domain, length = 20)
	@DAssoc(ascName = "account-has-savingsBook", role = "savingsBook",
	ascType = AssocType.One2Many, endType = AssocEndType.Many,
	associate = @Associate(type = Account.class, cardMin = 1, cardMax = 1),
	dependsOn=true)
	private Account account;
	
	@DAttr(name = S_interestRate, type = Type.Double, length = 15, optional = false)
	private double interestRate;
	
	@DAttr(name = S_finalBalance, type = Type.Double, auto = true, length = 15, mutable = false, optional = true,
			serialisable=false, derivedFrom={S_amount, S_interestRate, S_monthlyDuration})
	private Double finalBalance;

	private StateHistory<String, Object> stateHist;
	
	
	// constructor methods	
	@DOpt(type=DOpt.Type.ObjectFormConstructor)
	public SavingsBook(@AttrRef("name") String name,
					   @AttrRef("purpose") String purpose,
					   @AttrRef("amount") Double amount,	
				       @AttrRef("startDate") Date startDate,
					   @AttrRef("monthlyDuration") Integer monthlyDuration,
					   Account account,
					   Double interestRate) {
		this(null, name, purpose, amount, startDate, monthlyDuration, account, interestRate);
//		addToExpenditureSavings();
	}

	// a shared constructor that is invoked by other constructors
	@DOpt(type = DOpt.Type.DataSourceConstructor)
	public SavingsBook(Integer id, String name, String purpose, 
		Double amount, Date startDate, Integer monthlyDuration, 
		Account account, Double interestRate) throws ConstraintViolationException {
		
		super(id, name, purpose, amount, startDate);
		this.monthlyDuration = monthlyDuration;
		this.account = account;
		this.interestRate = interestRate;
		
		stateHist = new StateHistory<>();
		computeFinalBalance();
	}
	
	// getter methods
	public int getMonthlyDuration() {
		return monthlyDuration;
	}	
	
	public double getInterestRate() {
		return interestRate;
	}
	
	public Account getAccount() {
		return account;
	}
	
	//devired attribute
	public double getFinalBalance() {
		return getFinalBalance(false);
	}
	
	public double getFinalBalance(boolean cached) throws IllegalStateException {
		if (cached) {
			Object val = stateHist.get(S_finalBalance);

			if (val == null)
				throw new IllegalStateException("SavingsBook.getFinalBalance: cached value is null");
			return (Double) val;
		} else {
			if (finalBalance != null)
				return finalBalance;
			else
				return 0;
		}
	}

	// setter methods
	public void setMonthlyDuration(int monthlyDuration) {
		this.monthlyDuration = monthlyDuration;
	}
	
	public void setAccount(Account account) {
		this.account = account;
	}
	
	@Override
	public void setAmount(double amount) {
		setAmount(amount, false);
	}
	
	public void setAmount(double amount, boolean computeFinalBalance) {
		amount = getAmount();
		if (computeFinalBalance)
			computeFinalBalance();
	}
	
	public void setInterestRate(double interestRate) {
		setInterestRate(interestRate, false);
	}
	
	public void setInterestRate(double interestRate, boolean computeFinalBalance) {
		this.interestRate = interestRate;
		if (computeFinalBalance)
			computeFinalBalance();
	}
	
	// calculate finalBalance from interstRate 
	@DOpt(type=DOpt.Type.DerivedAttributeUpdater)
	@AttrRef(value=S_finalBalance)
	private void computeFinalBalance() {
		stateHist.put(S_finalBalance, finalBalance);
		
		double interestAmount = getAmount() * interestRate / 12 * getMonthlyDuration();
		finalBalance = (Double)(getAmount() + interestAmount);
	}
	

//	private void addToExpenditureSavings() {
//		ExpenditureSavings eS = new ExpenditureSavings(getAmount(), getStartDate(), null, getAccount(), null, null);
//		addNewExpenditureSavings(eS);
//	}
	
	@DOpt(type = DOpt.Type.LinkAdder)
	// only need to do this for reflexive association: @MemberRef(name="enrolments")
	public boolean addExpenditureSavings(ExpenditureSavings e) {
		if (!getExpenditureSavings().contains(e))
			getExpenditureSavings().add(e);
		// no other attributes changed
		return false;
	}

	@DOpt(type = DOpt.Type.LinkAdderNew)
	public boolean addNewExpenditureSavings(ExpenditureSavings e) {
		getExpenditureSavings().add(e);

		int count = getExpenditureSavingsCount();
		setExpenditureSavingsCount(count + 1);

		// v2.6.4.b
		//computeAverageMark();

		// no other attributes changed (average mark is not serialisable!!!)
		return false;
	}

	@DOpt(type = DOpt.Type.LinkAdder)
	// @MemberRef(name="enrolments")
	public boolean addExpenditureSavings(Collection<ExpenditureSavings> expSavings) {
		boolean added = false;
		for (ExpenditureSavings e : expSavings) {
			if (!getExpenditureSavings().contains(e)) {
				if (!added)
					added = true;
				getExpenditureSavings().add(e);
			}
		}
		return false;
	}

	@DOpt(type = DOpt.Type.LinkAdderNew)
	public boolean addNewExpenditureSavings(Collection<ExpenditureSavings> expSavings) {
		getExpenditureSavings().addAll(expSavings);
		int count = getExpenditureSavingsCount();
		count += expSavings.size();
		setExpenditureSavingsCount(count);

		// v2.6.4.b
		//computeAverageMark();

		// no other attributes changed (average mark is not serialisable!!!)
		return false;
	}
}
