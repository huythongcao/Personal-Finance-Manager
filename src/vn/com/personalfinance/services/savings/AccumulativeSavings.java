package vn.com.personalfinance.services.savings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import domainapp.basics.exceptions.ConstraintViolationException;
import domainapp.basics.model.meta.AttrRef;
import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DClass;
import domainapp.basics.model.meta.DOpt;
import domainapp.basics.model.meta.DAttr.Type;
import vn.com.personalfinance.services.savingstransaction.SavingsTransaction;

/**
 * Represents an accumulation.
 * 
 * @author Nguyen Hai - Group 2
 * @version 1.0
 */
@DClass(schema="personalfinancemanager")
public class AccumulativeSavings extends Savings {
	public static final String S_remainedAmount = "remainedAmount";
	
	@DAttr(name = S_remainedAmount, type = Type.Double, auto = true, length = 15, mutable = false, optional = true,
			serialisable=true)
	private Double remainedAmount;
	
	// constructor methods
	@DOpt(type = DOpt.Type.ObjectFormConstructor)
	public AccumulativeSavings(@AttrRef("name") String name,
			@AttrRef("purpose") String purpose,
			@AttrRef("amount") Double amount, 
			@AttrRef("startDate") Date startDate) {
		this(null, name, purpose, amount, startDate, amount);
	}

	// a shared constructor that is invoked by other constructors
	@DOpt(type = DOpt.Type.DataSourceConstructor)
	public AccumulativeSavings(String id, String name, String purpose, 
		Double amount, Date startDate, Double remainedAmount) throws ConstraintViolationException {
		super(id, name, purpose, amount, startDate);
		
		Collection<SavingsTransaction> savingsTransaction = getSavingsTransaction();
		setSavingsTransaction(savingsTransaction = new ArrayList<>());
		setSavingsTransactionCount(0);
		
		this.remainedAmount=remainedAmount;
	}
	
	//getter
	public double getRemainedAmount() {
		return remainedAmount;
	}

	// setter methods
	@Override
	public void setAmount(double amount) {
		setAmount(amount, false);
	}
	
	public void setAmount(double amount, boolean computeRemainedAmount) {
		amount = getAmount();
		if (computeRemainedAmount)
			computeRemainedAmount();
	}
	
	// calculate accumulate
	private void computeRemainedAmount() {
		if (getSavingsTransactionCount() >= 0 && remainedAmount <= getAmount() && remainedAmount >= 0) {
			double accumAmount = 0d;
			for (SavingsTransaction savingsTransaction : getSavingsTransaction()) {
				accumAmount += savingsTransaction.getAmount();
			}
			if (accumAmount <= getAmount()) {
				remainedAmount = getAmount() - accumAmount;
			} else {
				remainedAmount = 0d;
			}
		}
	}

	@DOpt(type = DOpt.Type.LinkAdder)
	// only need to do this for reflexive association: @MemberRef(name="accounts")
	public boolean addSavingsTransaction(SavingsTransaction s) {
		if (!getSavingsTransaction().contains(s))
			getSavingsTransaction().add(s);

		// no other attributes changed
		return true;
	}

	@DOpt(type = DOpt.Type.LinkAdderNew)
	public boolean addNewSavingsTransaction(SavingsTransaction s) {
		getSavingsTransaction().add(s);
		int count = getSavingsTransactionCount();
		setSavingsTransactionCount(count + 1);

		computeRemainedAmount();
		// no other attributes changed
		return true;
	}

	@DOpt(type = DOpt.Type.LinkAdder)
	public boolean addSavingsTransaction(Collection<SavingsTransaction> savingsTransaction) {
		for (SavingsTransaction s : savingsTransaction) {
			if (!getSavingsTransaction().contains(s)) {
				getSavingsTransaction().add(s);
			}
		}
		// no other attributes changed
		return true;
	}

	@DOpt(type = DOpt.Type.LinkAdderNew)
	public boolean addNewSavingsTransaction(Collection<SavingsTransaction> savingsTransaction) {
		getSavingsTransaction().addAll(savingsTransaction);
		int count = getSavingsTransactionCount();
		count += savingsTransaction.size();
		setSavingsTransactionCount(count);

		computeRemainedAmount();
		// no other attributes changed (average mark is not serialisable!!!)
		return true;
	}

	@DOpt(type = DOpt.Type.LinkRemover)
	// only need to do this for reflexive association: @MemberRef(name="accounts")
	public boolean removeSavingsTransaction(SavingsTransaction s) {
		boolean removed = getSavingsTransaction().remove(s);

		if (removed) {
			int count = getSavingsTransactionCount();
			setSavingsTransactionCount(count - 1);

			double currentAccountBalance = s.getAccount().getBalance();
			s.getAccount().setBalance(currentAccountBalance += s.getAmount());

			computeRemainedAmount();
		}
		// no other attributes changed
		return true;
	}
	
	// automatically generate the next account id
	@Override
	public String nextID(String id) throws ConstraintViolationException {
		if (id == null) { // generate a new id
			idCounter++;
			return "A" + idCounter;
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
}
