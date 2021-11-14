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
import vn.com.personalfinance.services.savingstransaction.SavingsTransaction;
import vn.com.personalfinance.exceptions.DExCode;
import vn.com.personalfinance.services.borrowandlend.model.BorrowAndLend;
import vn.com.personalfinance.services.expenseandincome.model.DailyExpense;
import vn.com.personalfinance.services.expenseandincome.model.DailyIncome;

/**
 * Represents an account. The account ID is auto-incremented from the current year.
 * 
 * @author Group 2
 * @version 1.0
 */
@DClass(schema="personalfinancemanager")
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
	
	@DAttr(name = A_name, type = Type.String, length = 20, optional = false, cid=true)
	private String name;
	
	@DAttr(name = A_type, type = Type.Domain, length = 20)
	@DAssoc(ascName = "type-has-account", role = "account", ascType = AssocType.One2Many,
	endType = AssocEndType.Many, associate = @Associate(type = AccountType.class, cardMin = 1, cardMax = 1), dependsOn=true)
	private AccountType type;
	
	@DAttr(name = A_balance, type = Type.Double, length = 15, optional = false, min = 0)
	private double balance;
	
	@DAttr(name = "dailyExpense", type = Type.Collection, optional = false,
	serialisable = false, filter = @Select(clazz = DailyExpense.class))
	@DAssoc(ascName = "account-has-dailyExpense", role = "account",
	ascType = AssocType.One2Many, endType = AssocEndType.One, 
	associate = @Associate(type = DailyExpense.class, cardMin = 0, cardMax = MetaConstants.CARD_MORE ))
	private Collection<DailyExpense> dailyExpense;
	private int dailyExpenseCount;
	
	@DAttr(name = "dailyIncome", type = Type.Collection, optional = false,
	serialisable = false, filter = @Select(clazz = DailyIncome.class))
	@DAssoc(ascName = "account-has-dailyIncome", role = "account",
	ascType = AssocType.One2Many, endType = AssocEndType.One, 
	associate = @Associate(type = DailyIncome.class, cardMin = 0, cardMax = MetaConstants.CARD_MORE ))
	private Collection<DailyIncome> dailyIncome;
	private int dailyIncomeCount;
	
	@DAttr(name = "savingsTransaction", type = Type.Collection, optional = false, 
	serialisable = false, filter = @Select(clazz = SavingsTransaction.class))
	@DAssoc(ascName = "account-has-savingsTransaction", role = "account",
	ascType = AssocType.One2Many, endType = AssocEndType.One,
	associate = @Associate(type = SavingsTransaction.class, cardMin = 0, cardMax = MetaConstants.CARD_MORE))
	private Collection<SavingsTransaction> savingsTransaction;
	private int savingsTransactionCount;
	
	@DAttr(name = "borrowAndLend", type = Type.Collection, optional = false,
	serialisable = false, filter = @Select(clazz = BorrowAndLend.class))
	@DAssoc(ascName = "account-has-borrowAndLend", role = "account",
	ascType = AssocType.One2Many, endType = AssocEndType.One, 
	associate = @Associate(type = BorrowAndLend.class, cardMin = 0, cardMax = MetaConstants.CARD_MORE ))
	private Collection<BorrowAndLend> borrowAndLend;
	private int borrowAndLendCount;
	
	private Collection<String> allClassID;
	
	@DAttr(name = "totalBalance", type = Type.Domain, length = 15, optional = false)
	@DAssoc(ascName = "totalBalance-has-account", role = "account",
	ascType = AssocType.One2Many, endType = AssocEndType.Many, 
	associate = @Associate(type = TotalBalance.class, cardMin = 1, cardMax = 1), dependsOn=true)
	private TotalBalance totalBalance;
	
	// constructor methods
	// form constructor into an object
	@DOpt(type=DOpt.Type.ObjectFormConstructor)
	@DOpt(type=DOpt.Type.RequiredConstructor)
	public Account(@AttrRef("name") String name, 
			@AttrRef("balance") Double balance, @AttrRef("totalBalance") TotalBalance totalBalance) {
		this(null, name, null, balance, totalBalance);
	}
	
	@DOpt(type=DOpt.Type.ObjectFormConstructor)
	public Account(@AttrRef("name") String name, 
			@AttrRef("type") AccountType type,
			@AttrRef("balance") Double balance,
			@AttrRef("totalBalance") TotalBalance totalBalance) {
		this(null, name, type, balance, totalBalance);
	}
	
	// a shared constructor that is invoked by other constructors
	// load db 
	@DOpt(type=DOpt.Type.DataSourceConstructor)
	public Account (String id, String name, AccountType type, Double balance, TotalBalance totalBalance) throws ConstraintViolationException{
		// generate an id
	    this.id = nextID(id);
	    
	    // assign other values
	    this.name = name;
	    this.type = type;
	    this.balance = balance;
	    this.totalBalance = totalBalance;

	    dailyExpense = new ArrayList<>();
	    dailyExpenseCount = 0;
	    
	    dailyIncome = new ArrayList<>();
	    dailyIncomeCount = 0;
	    
	    savingsTransaction = new ArrayList<>();
	    savingsTransactionCount = 0;
	    
	    borrowAndLend = new ArrayList<>();
	    borrowAndLendCount = 0;
	    
	    allClassID = new ArrayList<>();
	}
	
	// DailyExpense Assoc
	@DOpt(type = DOpt.Type.LinkAdder)
	// only need to do this for reflexive association: @MemberRef(name="accounts")
	public boolean addDailyExpense(DailyExpense s) {
		if (!this.dailyExpense.contains(s)) {
			dailyExpense.add(s);
		}
		if (!this.allClassID.contains(s.getId())) {
			allClassID.add(s.getId());
		}
		// no other attributes changed
		return false;
	}
	
	@DOpt(type = DOpt.Type.LinkAdderNew)
	public boolean addNewDailyExpense(DailyExpense s) {
		dailyExpense.add(s);
		dailyExpenseCount++;
		// no other attributes changed
		updateExpenseAccountBalance();
		return true;
	}
	
	@DOpt(type = DOpt.Type.LinkAdder)
	public boolean addDailyExpense(Collection<DailyExpense> dailyExpense) {
		for (DailyExpense s : dailyExpense) {
			if (!this.dailyExpense.contains(s)) {
				this.dailyExpense.add(s);
			}
			if(!this.allClassID.contains(s.getId()))
				allClassID.add(s.getId());
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
			allClassID.remove(s.getId());
			dailyExpenseCount--;
			balance+=s.getAmount();		
		}
		// no other attributes changed
		return false;
	}
	
	private void updateExpenseAccountBalance() {
		if (dailyExpenseCount > 0) {
			for (DailyExpense e : dailyExpense) {
				if (!allClassID.contains(e.getId())) {
					this.balance -= e.getAmount();
					allClassID.add(e.getId());
				}
			}
		} 
	}
	
	// DailyIncome Assoc
	@DOpt(type = DOpt.Type.LinkAdder)
	// only need to do this for reflexive association: @MemberRef(name="accounts")
	public boolean addDailyIncome(DailyIncome i) {
		if (!this.dailyIncome.contains(i)) {
			dailyIncome.add(i);
		}
		if (!this.allClassID.contains(i.getId())) {
			allClassID.add(i.getId());
		}
		// no other attributes changed
		return false;
	}

	@DOpt(type = DOpt.Type.LinkAdderNew)
	public boolean addNewDailyIncome(DailyIncome i) {
		dailyIncome.add(i);
		dailyIncomeCount++;
		// no other attributes changed
		updateIncomeAccountBalance();
		return true;
	}

	@DOpt(type = DOpt.Type.LinkAdder)
	public boolean addDailyIncome(Collection<DailyIncome> dailyIncome) {
		for (DailyIncome s : dailyIncome) {
			if (!this.dailyIncome.contains(s)) {
				this.dailyIncome.add(s);
			}
			if(!this.allClassID.contains(s.getId()))
				allClassID.add(s.getId());
		}
		// no other attributes changed
		return false;
	}

	@DOpt(type = DOpt.Type.LinkAdderNew)
	public boolean addNewDailyIncome(Collection<DailyIncome> dailyIncome) {
		this.dailyIncome.addAll(dailyIncome);
		dailyIncomeCount += dailyIncome.size();
		// no other attributes changed
		return false;
	}

	@DOpt(type = DOpt.Type.LinkRemover)
	// only need to do this for reflexive association: @MemberRef(name="accounts")
	public boolean removeDailyIncome(DailyIncome i) {
		boolean removed = dailyIncome.remove(i);

		if (removed) {
			allClassID.remove(i.getId());
			dailyIncomeCount--;
			balance -= i.getAmount();
		}
		// no other attributes changed
		return false;
	}
	
	private void updateIncomeAccountBalance() {
		if (dailyIncomeCount > 0) {
			for (DailyIncome i : dailyIncome) {
				if (!allClassID.contains(i.getId())) {
					this.balance += i.getAmount();
					allClassID.add(i.getId());
				}
			}
		} 
	}
	
	// BorrowAndLend Assoc
	@DOpt(type = DOpt.Type.LinkAdder)
	// only need to do this for reflexive association: @MemberRef(name="accounts")
	public boolean addBorrowAndLend(BorrowAndLend bL) {
		if (!this.borrowAndLend.contains(bL))
			borrowAndLend.add(bL);

		// no other attributes changed
		return false;
	}

	@DOpt(type = DOpt.Type.LinkAdderNew)
	public boolean addNewborrowAndLend(BorrowAndLend bL) {
		borrowAndLend.add(bL);
		borrowAndLendCount++;
		// no other attributes changed
		updateBnLAccountBalance();
		return true;
	}
	
	@DOpt(type = DOpt.Type.LinkAdder)
	public boolean addBorrowAndLend(Collection<BorrowAndLend> bL) {
		for (BorrowAndLend b : bL) {
			if (!this.borrowAndLend.contains(b)) {
				this.borrowAndLend.add(b);
			}
		}
		// no other attributes changed
		return false;
	}
	
	@DOpt(type = DOpt.Type.LinkAdderNew)
	public boolean addNewBorrowAndLend(Collection<BorrowAndLend> bL) {
		this.borrowAndLend.addAll(bL);
		borrowAndLendCount += bL.size();
		// no other attributes changed (average mark is not serialisable!!!)
		return false;
	}
	
	@DOpt(type = DOpt.Type.LinkRemover)
	// only need to do this for reflexive association: @MemberRef(name="accounts")
	public boolean removeBorrowAndLend(BorrowAndLend bL) {
		boolean removed = borrowAndLend.remove(bL);

		if (removed) {
			borrowAndLendCount--;
			if(bL.getActionType().getId() == 1 & bL.getActionType().getId() == 2) {
				balance += bL.getMoney();
			} else if (bL.getActionType().getId() == 3 & bL.getActionType().getId() == 4) {
				balance -= bL.getMoney();
			}
		}
		// no other attributes changed
		return false;
	}
	
	private void updateBnLAccountBalance() {
		for (BorrowAndLend bL : borrowAndLend) {
			String temp = ""+bL.getId();
			if (!allClassID.contains(temp)) {
				if (bL.getActionType().getId() == 1 || bL.getActionType().getId() == 2)
					this.balance += bL.getMoney();		
				if (bL.getActionType().getId() == 3 || bL.getActionType().getId() == 4)
					this.balance -= bL.getMoney();
				allClassID.add(temp);
			}
		}
	}
	
	// Log Assoc
	@DOpt(type = DOpt.Type.LinkAdder)
	// only need to do this for reflexive association: @MemberRef(name="accounts")
	public boolean addSavingsTransaction(SavingsTransaction s) {
		if (!this.savingsTransaction.contains(s))
			savingsTransaction.add(s);
		if (!this.allClassID.contains(s.getId())) {
			allClassID.add(s.getId());
		}

		// no other attributes changed
		return false;
	}

	@DOpt(type = DOpt.Type.LinkAdderNew)
	public boolean addNewSavingsTransaction(SavingsTransaction s) {
		savingsTransaction.add(s);
		savingsTransactionCount++;
		// no other attributes changed
		updateSavingsTransactionAccountBalance();
		return true;
	}
	
	@DOpt(type = DOpt.Type.LinkAdder)
	public boolean addSavingsTransaction(Collection<SavingsTransaction> savingsTransaction) {
		for (SavingsTransaction s : savingsTransaction) {
			if (!this.savingsTransaction.contains(s)) {
				this.savingsTransaction.add(s);
			}
			if(!this.allClassID.contains(s.getId()))
				allClassID.add(s.getId());
		}
		// no other attributes changed
		return false;
	}
	
	@DOpt(type = DOpt.Type.LinkAdderNew)
	public boolean addNewSavingsTransaction(Collection<SavingsTransaction> savingsTransaction) {
		this.savingsTransaction.addAll(savingsTransaction);
		savingsTransactionCount += savingsTransaction.size();
		// no other attributes changed (average mark is not serialisable!!!)
		return false;
	}
	
	@DOpt(type = DOpt.Type.LinkRemover)
	// only need to do this for reflexive association: @MemberRef(name="accounts")
	public boolean removeSavingsTransaction(SavingsTransaction s) {
		boolean removed = savingsTransaction.remove(s);

		if (removed) {
			allClassID.remove(s.getId());
			savingsTransactionCount--;
			balance += s.getAmount();
		}
		// no other attributes changed
		return false;
	}
	
	private void updateSavingsTransactionAccountBalance() {
		if (savingsTransactionCount > 0) {
			for (SavingsTransaction t : savingsTransaction) {
				if (!allClassID.contains(t.getId())) {
					this.balance -= t.getAmount();
					allClassID.add(t.getId());
				}
			}
		} 
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
	
	public TotalBalance getTotalBalance() {
		return totalBalance;
	}
	
	public Collection<DailyExpense> getDailyExpense() {
		return dailyExpense;
	}
	
	@DOpt(type=DOpt.Type.LinkCountGetter)
	public int getDailyExpenseCount() {
		return dailyExpenseCount;
	}

	public Collection<SavingsTransaction> getSavingsTransaction() {
		return savingsTransaction;
	}
	
	@DOpt(type=DOpt.Type.LinkCountGetter)
	public int getSavingsTransactionCount() {
		return savingsTransactionCount;
	}
	
	public Collection<BorrowAndLend> getBorrowAndLend() {
		return borrowAndLend;
	}
	
	@DOpt(type=DOpt.Type.LinkCountGetter)
	public int getBorrowAndLendCount() {
		return borrowAndLendCount;
	}
	
	public Collection<DailyIncome> getDailyIncome() {
		return dailyIncome;
	}
	
	@DOpt(type=DOpt.Type.LinkCountGetter)
	public int getDailyIncomeCount() {
		return dailyIncomeCount;
	}
	
//	public TotalBalance getTotalBalance() {
//		return totalBalance;
//	}
	
	// setter methods
	public void setName(String name) {
		this.name = name;
	}

	public void setType(AccountType type) {
		this.type = type;
	}

	public void setBalance(double balance)  throws ConstraintViolationException {
		if (balance < 0) {
			throw new ConstraintViolationException(DExCode.INVALID_BALANCE, balance);
		}
		this.balance = balance;
	}
	
	public void setTotalBalance(TotalBalance totalBalance) {
		this.totalBalance = totalBalance;
	}
	
	public void setDailyExpense(Collection<DailyExpense> dailyExpense) {
		this.dailyExpense = dailyExpense;
		dailyExpenseCount = dailyExpense.size();
	}
	
	@DOpt(type=DOpt.Type.LinkCountSetter)
	public void setDailyExpenseCount(int dailyExpenseCount) {
		this.dailyExpenseCount = dailyExpenseCount;
	}
	
	public void setSavingsTransaction(Collection<SavingsTransaction> savingsTransaction) {
		this.savingsTransaction = savingsTransaction;
		savingsTransactionCount = savingsTransaction.size();
	}
	
	@DOpt(type=DOpt.Type.LinkCountSetter)
	public void setSavingsTransactionCount(int savingsTransactionCount) {
		this.savingsTransactionCount = savingsTransactionCount;
	}
	
	public void setBorrowAndLend(Collection<BorrowAndLend> borrowAndLend) {
		this.borrowAndLend = borrowAndLend;
		borrowAndLendCount = borrowAndLend.size();
	}
	
	@DOpt(type=DOpt.Type.LinkCountSetter)
	public void setBorrowAndLendCount(int borrowAndLendCount) {
		this.borrowAndLendCount = borrowAndLendCount;
	}
	
	public void setDailyIncome(Collection<DailyIncome> dailyIncome) {
		this.dailyIncome = dailyIncome;
		dailyIncomeCount = dailyIncome.size();
	}
	
	@DOpt(type=DOpt.Type.LinkCountSetter)
	public void setDailyIncomeCount(int dailyIncomeCount) {
		this.dailyIncomeCount = dailyIncomeCount;
	}
	
//	public void setTotalBalance(TotalBalance totalBalance) {
//		this.totalBalance = totalBalance;
//	}
	
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
