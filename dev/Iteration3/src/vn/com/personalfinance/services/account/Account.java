package vn.com.personalfinance.services.account;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import domainapp.basics.exceptions.ConstraintViolationException;
import domainapp.basics.model.meta.AttrRef;
import domainapp.basics.model.meta.DAssoc;
import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DClass;
import domainapp.basics.model.meta.DOpt;
import domainapp.basics.model.meta.MetaConstants;
import domainapp.basics.model.meta.Select;
import domainapp.basics.model.meta.DAssoc.AssocEndType;
import domainapp.basics.model.meta.DAssoc.AssocType;
import domainapp.basics.model.meta.DAssoc.Associate;
import domainapp.basics.model.meta.DAttr.Type;
import domainapp.basics.util.Tuple;
import vn.com.personalfinance.services.expenditure.model.DailyExpense;
import vn.com.personalfinance.services.savings.SavingsBook;

/**
 * Represents an account. The account ID is auto-incremented from the current year.
 * 
 * @author Group 2
 * @version 1.0
 */
@DClass(schema="personalfinancemanagement")
public class Account {
	public static final String A_id = "id";
	public static final String A_name = "name";
	public static final String A_type = "type";
	public static final String A_balance = "balance";
	
	// attributes of accounts
	@DAttr(name = A_id, id = true, type = Type.String, auto = true, length = 6, mutable = false, optional = false)
	private String id;
	// static variable to keep track of account id
	private static int idCounter = 0;
	
	@DAttr(name = A_name, type = Type.String, length = 15, optional = false, cid=true)
	private String name;
	
	@DAttr(name = A_type, type = Type.Domain, length = 20)
	@DAssoc(ascName = "type-has-account", role = "account", ascType = AssocType.One2Many,
	endType = AssocEndType.Many, associate = @Associate(type = AccountType.class, cardMin = 1, cardMax = 1), dependsOn=true)
	private AccountType type;
	
	@DAttr(name = A_balance, type = Type.Double, length = 15, optional = false)
	private double balance;
	
	@DAttr(name = "savingsBook", type = Type.Collection, optional = false,
	serialisable = false, filter = @Select(clazz = SavingsBook.class))
	@DAssoc(ascName = "account-has-savingsBook", role = "account",
	ascType = AssocType.One2Many, endType = AssocEndType.One, 
	associate = @Associate(type = SavingsBook.class, cardMin = 0, cardMax = 30))
	private Collection<SavingsBook> savingsBook;
	// derived
	private int savingsBookCount;
	
	
	@DAttr(name = "dailyExpense", type = Type.Collection, optional = false,
	serialisable = false, filter = @Select(clazz = DailyExpense.class))
	@DAssoc(ascName = "account-has-dailyExpense", role = "account",
	ascType = AssocType.One2Many, endType = AssocEndType.One, 
	associate = @Associate(type = DailyExpense.class, cardMin = 1, cardMax = MetaConstants.CARD_MORE ))
	private Collection<DailyExpense> dailyExpense;
	private int dailyExpenseCount;
	
	// constructor methods
	// form constructor into an object
	@DOpt(type=DOpt.Type.ObjectFormConstructor)
	@DOpt(type=DOpt.Type.RequiredConstructor)
	public Account(@AttrRef("name") String name, 
			@AttrRef("balance") Double balance) {
		this(null, name, null, balance);
	}
	
	@DOpt(type=DOpt.Type.ObjectFormConstructor)
	public Account(@AttrRef("name") String name, 
			@AttrRef("type") AccountType type,
			@AttrRef("balance") Double balance) {
		this(null, name, type, balance);
	}
	
	// a shared constructor that is invoked by other constructors
	// load db 
	@DOpt(type=DOpt.Type.DataSourceConstructor)
	public Account (String id, String name, AccountType type, Double balance) throws ConstraintViolationException{
		// generate an id
	    this.id = nextID(id);
	    
	 // assign other values
	    this.name = name;
	    this.type = type;
	    this.balance = balance;
	    
	    savingsBook = new ArrayList<>();
	    savingsBookCount = 0;
	    dailyExpense = new ArrayList<>();
	    dailyExpenseCount = 0;
	}
	
	@DOpt(type = DOpt.Type.LinkAdder)
	// only need to do this for reflexive association: @MemberRef(name="accounts")
	public boolean addSavingsBook(SavingsBook s) {
		if (!this.savingsBook.contains(s)) {
			savingsBook.add(s);
		}
		// no other attributes changed
		return false;
	}
	// add new object into collection directly
	
	@DOpt(type = DOpt.Type.LinkAdderNew)
	public boolean addNewSavingsBook(SavingsBook s) {
		savingsBook.add(s);
		savingsBookCount++;
		// no other attributes changed
		return false;
	}
	
	@DOpt(type = DOpt.Type.LinkAdder)
	public boolean addSavingsBook(Collection<SavingsBook> savingsBook) {
		for (SavingsBook s : savingsBook) {
			if (!this.savingsBook.contains(s)) {
				this.savingsBook.add(s);
			}
		}
		// no other attributes changed
		return false;
	}
	
	@DOpt(type = DOpt.Type.LinkAdderNew)
	public boolean addNewSavingsBook(Collection<SavingsBook> savingsBook) {
		this.savingsBook.addAll(savingsBook);
		savingsBookCount += savingsBook.size();
		// no other attributes changed
		return false;
	}
	
	@DOpt(type = DOpt.Type.LinkRemover)
	// only need to do this for reflexive association: @MemberRef(name="accounts")
	public boolean removeSavingsBook(SavingsBook s) {
		boolean removed = savingsBook.remove(s);

		if (removed) {
			savingsBookCount--;
		}
		// no other attributes changed
		return false;
	}
	
	@DOpt(type = DOpt.Type.LinkAdder)
	// only need to do this for reflexive association: @MemberRef(name="accounts")
	public boolean addDailyExpense(DailyExpense s) {
		if (!this.dailyExpense.contains(s)) {
			dailyExpense.add(s);
		}
		// no other attributes changed
		return false;
	}
	
	@DOpt(type = DOpt.Type.LinkAdderNew)
	public boolean addNewDailyExpense(DailyExpense s) {
		dailyExpense.add(s);
		dailyExpenseCount++;
		// no other attributes changed
		return false;
	}
	
	@DOpt(type = DOpt.Type.LinkAdder)
	public boolean addDailyExpense(Collection<DailyExpense> dailyExpense) {
		for (DailyExpense s : dailyExpense) {
			if (!this.dailyExpense.contains(s)) {
				this.dailyExpense.add(s);
			}
		}
		// no other attributes changed
		return false;
	}
	
	@DOpt(type = DOpt.Type.LinkAdderNew)
	public boolean addNewDailyExpense(Collection<DailyExpense> dailyExpense) {
		this.dailyExpense.addAll(dailyExpense);
		dailyExpenseCount += dailyExpense.size();
		// no other attributes changed
		return false;
	}
	
	@DOpt(type = DOpt.Type.LinkRemover)
	// only need to do this for reflexive association: @MemberRef(name="accounts")
	public boolean removeDailyExpense(DailyExpense s) {
		boolean removed = dailyExpense.remove(s);

		if (removed) {
			savingsBookCount--;
			if(s.getId().contains("I")) {
				balance-=s.getAmount();
			} else {
				balance+=s.getAmount();
			}
			
		}
		// no other attributes changed
		return false;
	}


	// getter methods
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public AccountType getType() {
		return type;
	}

	public double getBalance() {
		return balance ;
	}

	public Collection<SavingsBook> getSavingsBook() {
		return savingsBook;
	}
	
	public int getSavingsCount() {
		return savingsBookCount;
	}
	public Collection<DailyExpense> getDailyExpense() {
		return dailyExpense;
	}
	
	@DOpt(type=DOpt.Type.LinkCountGetter)
	public int getDailyExpenseCount() {
		return dailyExpenseCount;
	}

	// setter methods

	public void setName(String name) {
		this.name = name;
	}

	public void setType(AccountType type) {
		this.type = type;
	}
//	
//	public void setNewType(AccountType type) {
//		setType(type);
//	}

	public void setBalance(double balance) {
		this.balance = balance;
	}
	
	public void setSavingsBook(Collection<SavingsBook> savingsBook) {
		this.savingsBook = savingsBook;
		savingsBookCount = savingsBook.size();
	}

	public void setSavingsBookCount(int savingsBookCount) {
		this.savingsBookCount = savingsBookCount;
	}
	public void setDailyExpense(Collection<DailyExpense> dailyExpense) {
		this.dailyExpense = dailyExpense;
		dailyExpenseCount = dailyExpense.size();
	}
	@DOpt(type=DOpt.Type.LinkCountSetter)
	public void setDailyExpenseCount(int dailyExpenseCount) {
		this.dailyExpenseCount = dailyExpenseCount;
	}
	
	// override toString
	/**
	 * @effects returns <code>this.id</code>
	 */
	@Override
	public String toString() {
		return toString(true);
	}
	
	/**
	 * @effects returns <code>Account(id,name,type,balance)</code>.
	 */
	public String toString(boolean full) {
	    if (full)
	      return "Account(" + id + "," + name + "," +  ((type != null) ? "," + type.getName() : "") + "," + balance + ")";
	    else
	      return "Account(" + id + ")";
	  }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Account other = (Account) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	// automatically generate the next account id
	private String nextID(String id) throws ConstraintViolationException {
		if (id == null) { // generate a new id
			if (idCounter == 0) {
				idCounter = Calendar.getInstance().get(Calendar.YEAR);
			} else {
				idCounter++;
			}
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
	
	/**
	   * @requires 
	   *  minVal != null /\ maxVal != null
	   * @effects 
	   *  update the auto-generated value of attribute <tt>attrib</tt>, specified for <tt>derivingValue</tt>, using <tt>minVal, maxVal</tt>
	   */
	  @DOpt(type=DOpt.Type.AutoAttributeValueSynchroniser)
	  public static void updateAutoGeneratedValue(
	      DAttr attrib,
	      Tuple derivingValue, 
	      Object minVal, 
	      Object maxVal) throws ConstraintViolationException {
	    
	    if (minVal != null && maxVal != null) {
	      //TODO: update this for the correct attribute if there are more than one auto attributes of this class 

	    	if (attrib.name().equals("id")) {
	  		  String maxId = (String) maxVal;
	  		  
	  		  try {
	  		    int maxIdNum = Integer.parseInt(maxId.substring(1));
	  		    
	  		    if (maxIdNum > idCounter) // extra check
	  		      idCounter = maxIdNum;
	  		    
	  		  } catch (RuntimeException e) {
	  		    throw new ConstraintViolationException(
	  		        ConstraintViolationException.Code.INVALID_VALUE, e, new Object[] {maxId});
	  		  }
	      	}	    
	    }
	 }
}
