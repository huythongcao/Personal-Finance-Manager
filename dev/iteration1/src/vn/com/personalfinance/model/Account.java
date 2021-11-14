package vn.com.personalfinance.model;

import java.util.Calendar;

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
import domainapp.basics.util.Tuple;
import vn.com.personalfinance.model.AccountType;

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
	@DAssoc(ascName = "account-has-type", role = "account", ascType = AssocType.One2Many,
	endType = AssocEndType.Many, associate = @Associate(type = AccountType.class, cardMin = 1, cardMax = 1), dependsOn=true)
	private AccountType type;
	
	@DAttr(name = A_balance, type = Type.Double, length = 15, optional = false)
	private double balance;
	
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
		this(null, name, type, 0.0);
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
