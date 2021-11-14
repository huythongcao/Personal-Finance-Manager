package vn.com.personalfinance.services.savingstransaction;

import domainapp.basics.exceptions.ConstraintViolationException;
import domainapp.basics.model.meta.AttrRef;
import domainapp.basics.model.meta.DAssoc;
import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DClass;
import domainapp.basics.model.meta.DOpt;
import vn.com.personalfinance.services.account.Account;
import vn.com.personalfinance.services.savings.Savings;
import domainapp.basics.model.meta.DAssoc.AssocEndType;
import domainapp.basics.model.meta.DAssoc.AssocType;
import domainapp.basics.model.meta.DAssoc.Associate;
import domainapp.basics.model.meta.DAttr.Type;
import domainapp.basics.util.Tuple;

@DClass(schema="personalfinancemanager")
public class SavingsTransaction implements Comparable {
	
	// attribute
	@DAttr(name = "id", id = true, auto = true, type = Type.String, length = 5, optional = false, mutable = false)
	private String id;
	private static int idCounter = 0;
	  
	@DAttr(name = "account", type = Type.Domain, length = 15, optional = false)
	@DAssoc(ascName = "account-has-savingsTransaction", role = "savingsTransaction", 
	    ascType = AssocType.One2Many, endType = AssocEndType.Many, 
	    associate = @Associate(type = Account.class, cardMin = 1, cardMax = 1))
	private Account account;

	@DAttr(name = "savings", type = Type.Domain, length = 15, optional = false)
	@DAssoc(ascName = "savings-has-savingsTransaction", role = "savingsTransaction", 
	    ascType = AssocType.One2Many, endType = AssocEndType.Many, 
	    associate = @Associate(type = Savings.class, cardMin = 1, cardMax = 1), dependsOn = true)
	private Savings savings;
	
	@DAttr(name = "amount", type = Type.Double, length = 15, optional = false)
	private double amount;
	
	@DAttr(name = "description", type = Type.String, length = 30)
	private String description;
	
	// constructor
	@DOpt(type=DOpt.Type.ObjectFormConstructor)
	@DOpt(type=DOpt.Type.RequiredConstructor)
	public SavingsTransaction(@AttrRef("account") Account account, 
	    @AttrRef("savings") Savings savings) throws ConstraintViolationException {
	  this(null, account, savings, 0.0, null);
	}

	@DOpt(type=DOpt.Type.ObjectFormConstructor)
	public SavingsTransaction(@AttrRef("account") Account account, 
		@AttrRef("savings") Savings savings, 
	    @AttrRef("amount") Double amount, 
	    @AttrRef("description") String description)
	    throws ConstraintViolationException {
	  this(null, account, savings, amount, description);
	}

	@DOpt(type=DOpt.Type.DataSourceConstructor)
	public SavingsTransaction(String id, Account account, Savings savings, Double amount, String description) throws ConstraintViolationException {
	  this.id = nextID(id);
	  this.account = account;
	  this.savings = savings;
	  this.amount = (amount != null) ? amount.doubleValue() : null;
	  this.description = description;
	}
	
	// setter
	public void setAccount(Account account) {
		this.account = account;
	}

	public void setSavings(Savings savings) {
		this.savings = savings;
	}
	
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	// getter
	public String getId() {
		return id;
	}

	public Account getAccount() {
		return account;
	}

	public Savings getSavings() {
		return savings;
	}

	public double getAmount() {
		return amount;
	}
	
	public String getDescription() {
		return description;
	}
	
	@Override
	public String toString() {
		return toString(false);
	}

	public String toString(boolean full) {
		if (full)
			return "SavingsTransaction(" + account + "," + savings + ")";
		else
			return "SavingsTransaction(" + getId() + "," + ((account != null) ? account.getId() : "null") + ","
					+ ((savings != null) ? savings.getId() : "null") + "," + description + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Integer.parseInt(id);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SavingsTransaction other = (SavingsTransaction) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	
	private static String nextID(String id) {
		if (id == null) { // generate a new id
			idCounter++;
			return "ST" + idCounter;
		} else {
			// update id
			int num;
			try {
				num = Integer.parseInt(id.substring(2));
			} catch (RuntimeException e) {
				throw new ConstraintViolationException(ConstraintViolationException.Code.INVALID_VALUE, e, new Object[] { id });
			}

			if (num > idCounter) {
				idCounter = num;
			}

			return id;
		}
	}
	
	/**
	 * @requires minVal != null /\ maxVal != null
	 * @effects update the auto-generated value of attribute <tt>attrib</tt>,
	 *          specified for <tt>derivingValue</tt>, using <tt>minVal, maxVal</tt>
	 */
	@DOpt(type = DOpt.Type.AutoAttributeValueSynchroniser)
	public static void updateAutoGeneratedValue(DAttr attrib, Tuple derivingValue, Object minVal, Object maxVal)
			throws ConstraintViolationException {
		if (minVal != null && maxVal != null) {
			// check the right attribute
			if (attrib.name().equals("id")) {
				String maxID = (String) maxVal;

				try {
					int maxIDNum = Integer.parseInt(maxID.substring(2));

					if (maxIDNum > idCounter) {
						idCounter = maxIDNum;
					}

				} catch (RuntimeException e) {
					throw new ConstraintViolationException(ConstraintViolationException.Code.INVALID_VALUE, e,
							new Object[] { maxID });
				}
			}
			// TODO add support for other attributes here
		}
	}
	
	public int compareTo(Object o) {
		if (o == null || (!(o instanceof SavingsTransaction)))
			return -1;

		SavingsTransaction e = (SavingsTransaction) o;

		return this.account.getId().compareTo(e.account.getId());
	}
}
