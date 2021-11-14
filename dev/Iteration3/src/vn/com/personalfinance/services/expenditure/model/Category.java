package vn.com.personalfinance.services.expenditure.model;

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

@DClass(schema="personalfinancemanagement")
public class Category {
	public static final String C_name = "name";

	@DAttr(name="id",id=true,auto=true,length=6,mutable=false,type=Type.Integer)
	private int id;
	private static int idCounter;
	
	@DAttr(name = C_name, type = Type.String, length = 20, optional = false, cid=true)
	private String name;
	
	@DAttr(name = "dailyExpense", type = Type.Collection, optional = false,
	serialisable = false, filter = @Select(clazz = DailyExpense.class))
	@DAssoc(ascName = "category-has-dailyExpense", role = "category",
	ascType = AssocType.One2Many, endType = AssocEndType.One, 
	associate = @Associate(type = DailyExpense.class, cardMin = 1, cardMax = 30))
	private Collection<DailyExpense> dailyExpense;
	// derived attributes
	private int dailyExpenseCount;
	
	// from object form: Account is not included 
	@DOpt(type=DOpt.Type.ObjectFormConstructor)
	@DOpt(type=DOpt.Type.RequiredConstructor)
	public Category(@AttrRef("name") String name) {
		this(null,name);
	}
	
	// from data source
	@DOpt(type=DOpt.Type.DataSourceConstructor)
	public Category(@AttrRef("id") Integer id, @AttrRef("name") String name ) {
		this.id = nextId(id);
		this.name = name;	
		
		dailyExpense = new ArrayList<>();
		dailyExpenseCount = 0;
	}
	
	@DOpt(type = DOpt.Type.LinkAdder)
	// only need to do this for reflexive association: @MemberRef(name="accounts")
	public boolean addDailyExpense(DailyExpense a) {
		if (!this.dailyExpense.contains(a)) {
			dailyExpense.add(a);
		}
		// no other attributes changed
		return false;
	}
	// add new object into collection directly
	
	@DOpt(type = DOpt.Type.LinkAdderNew)
	public boolean addNewDailyExpense(DailyExpense a) {
		dailyExpense.add(a);
		dailyExpenseCount++;
		// no other attributes changed
		return false;
	}
	
	@DOpt(type = DOpt.Type.LinkAdder)
	public boolean addDailyExpense(Collection<DailyExpense> dailyExpense) {
		for (DailyExpense a : dailyExpense) {
			if (!this.dailyExpense.contains(a)) {
				this.dailyExpense.add(a);
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
	public boolean removeDailyExpense(DailyExpense a) {
		boolean removed = dailyExpense.remove(a);

		if (removed) {
			dailyExpenseCount--;
		}
		// no other attributes changed
		return false;
	}
	
	@DOpt(type=DOpt.Type.Setter)
	public void setDailyExpense(Collection<DailyExpense> dailyExpense) {
		this.dailyExpense = dailyExpense;
		dailyExpenseCount = dailyExpense.size();
	}
	
	/**
	 * @effects return <tt>accountsCount</tt>
	 */
	@DOpt(type=DOpt.Type.LinkCountGetter)
	public int getDailyExpenseCount() {
		return dailyExpenseCount;
	}
	
	@DOpt(type=DOpt.Type.LinkCountSetter)
	public void setDailyExpenseCount(int dailyExpenseCount) {
		this.dailyExpenseCount = dailyExpenseCount;
	}
	
	@DOpt(type=DOpt.Type.Getter)
	public Collection<DailyExpense> getAccounts() {
		return dailyExpense;
	}
	
	@DOpt(type=DOpt.Type.Setter)
	public void setName(String name) {
		this.name = name;
	}
	
	@DOpt(type=DOpt.Type.Getter)
	public String getName() {
		return name;
	}

	@DOpt(type=DOpt.Type.Getter)
	public int getId() {
		return id;
	}

	@Override
	  public String toString() {
	    return "Category("+getId()+","+getName()+")";
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
		Category other = (Category) obj;
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
