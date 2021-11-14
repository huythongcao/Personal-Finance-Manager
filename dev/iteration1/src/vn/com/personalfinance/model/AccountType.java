package vn.com.personalfinance.model;

import java.util.ArrayList;
import java.util.Collection;

import domainapp.basics.exceptions.ConstraintViolationException;
import domainapp.basics.model.meta.AttrRef;
import domainapp.basics.model.meta.DAssoc;
import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DClass;
import domainapp.basics.model.meta.DOpt;
import domainapp.basics.model.meta.Select;
import domainapp.basics.model.meta.DAssoc.AssocEndType;
import domainapp.basics.model.meta.DAssoc.AssocType;
import domainapp.basics.model.meta.DAssoc.Associate;
import domainapp.basics.model.meta.DAttr.Type;
import domainapp.basics.util.Tuple;

import vn.com.personalfinance.model.Account;

/**
 * A domain class whose objects are account types. This class is used as 
 * the <code>allowedValues</code> of the domain attributes of 
 * other domain classes (e.g. Account.type).  
 * 
 * <p>Method <code>toString</code> overrides <code>Object.toString</code> to 
 * return the string representation of a account type which is expected by 
 * the application. 
 * 
 * @author Group 2
 *
 */
@DClass(schema="personalfinancemanagement")
public class AccountType {

	@DAttr(name="id",id=true,auto=true,length=6,mutable=false,type=Type.Integer)
	private int id;
	
	private static int idCounter;
	
	@DAttr(name = "name", type = Type.String, length = 20, optional = false, cid=true)
	private String name;
	
	@DAttr(name = "accounts", type = Type.Collection, 
			serialisable = false, optional = false, 
			filter = @Select(clazz = Account.class))
	@DAssoc(ascName = "type-has-account", role = "type", 
		ascType = AssocType.One2Many, endType = AssocEndType.One, 
		associate = @Associate(type = Account.class, cardMin = 1, cardMax = 25))
	private Collection<Account> accounts;
	// derived attributes
	 private int accountsCount;
	
	// from object form: Account is not included 
	@DOpt(type=DOpt.Type.ObjectFormConstructor)
	@DOpt(type=DOpt.Type.RequiredConstructor)
	public AccountType(@AttrRef("name") String typeName) {
		this(null, typeName);
	}
	
	// from data source
	@DOpt(type=DOpt.Type.DataSourceConstructor)
	public AccountType(@AttrRef("id") Integer id, @AttrRef("name") String typeName ) {
		this.id = nextId(id);
		this.name = typeName;
		
		accounts = new ArrayList<>();
		accountsCount = 0;
	}
	
	@DOpt(type=DOpt.Type.Setter)
	public void setName(String name) {
		this.name = name;
	}
	
	// add exist object into collection
	
	@DOpt(type = DOpt.Type.LinkAdder)
	// only need to do this for reflexive association: @MemberRef(name="accounts")
	public boolean addAccount(Account a) {
		if (!this.accounts.contains(a)) {
			accounts.add(a);
		}
		// no other attributes changed
		return false;
	}
	// add new object into collection directly
	
	@DOpt(type = DOpt.Type.LinkAdderNew)
	public boolean addNewAccount(Account a) {
		accounts.add(a);
		accountsCount++;
		// no other attributes changed
		return false;
	}
	
	@DOpt(type = DOpt.Type.LinkAdder)
	public boolean addAccount(Collection<Account> accounts) {
		for (Account a : accounts) {
			if (!this.accounts.contains(a)) {
				this.accounts.add(a);
			}
		}
		// no other attributes changed
		return false;
	}
	
	@DOpt(type = DOpt.Type.LinkAdderNew)
	public boolean addNewAccount(Collection<Account> accounts) {
		this.accounts.addAll(accounts);
		accountsCount += accounts.size();
		// no other attributes changed
		return false;
	}
	
	@DOpt(type = DOpt.Type.LinkRemover)
	// only need to do this for reflexive association: @MemberRef(name="accounts")
	public boolean removeAccount(Account a) {
		boolean removed = accounts.remove(a);

		if (removed) {
			accountsCount--;
		}
		// no other attributes changed
		return false;
	}
	
	@DOpt(type=DOpt.Type.Setter)
	public void setAccounts(Collection<Account> accounts) {
		this.accounts = accounts;
		accountsCount = accounts.size();
	}
	
	/**
	 * @effects return <tt>accountsCount</tt>
	 */
	@DOpt(type=DOpt.Type.LinkCountGetter)
	public int getAccountsCount() {
		return accountsCount;
	}
	
	@DOpt(type=DOpt.Type.LinkCountSetter)
	public void setAccountsCount(int accountsCount) {
		this.accountsCount = accountsCount;
	}
	
	@DOpt(type=DOpt.Type.Getter)
	public String getName() {
		return name;
	}

	@DOpt(type=DOpt.Type.Getter)
	public Collection<Account> getAccounts() {
		return accounts;
	}
	
	@DOpt(type=DOpt.Type.Getter)
	public int getId() {
		return id;
	}

	@Override
	  public String toString() {
	    return "AccountType("+getId()+","+getName()+")";
	  }
	
	@Override
	  public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + id;
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
		AccountType other = (AccountType) obj;
		if (id != other.id)
			return false;
		return true;
	}

	private static int nextId(Integer currID) {
		if (currID == null) {
			idCounter++;
			return idCounter;
		} else {
			int num = currID.intValue();
			if (num > idCounter)
				idCounter = num;
			return currID;
		}
	}
	
	/**
	   * @requires 
	   *  minVal != null /\ maxVal != null
	   * @effects 
	   *  update the auto-generated value of attribute <tt>attrib</tt>, specified for <tt>derivingValue</tt>, using <tt>minVal, maxVal</tt>
	   */
	@DOpt(type = DOpt.Type.AutoAttributeValueSynchroniser)
	public static void updateAutoGeneratedValue(DAttr attrib, Tuple derivingValue, Object minVal, Object maxVal)
			throws ConstraintViolationException {

		if (minVal != null && maxVal != null) {
			// TODO: update this for the correct attribute if there are more than one auto
			// attributes of this class
			int maxIdVal = (Integer) maxVal;
			if (maxIdVal > idCounter)
				idCounter = maxIdVal;
		}
	}	
}
