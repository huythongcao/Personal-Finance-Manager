package vn.com.personalfinance.services.borrowandlend.model;

import java.util.ArrayList; 
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

@DClass(schema="personalfinancemanager")
public class Subjects {
//	attributes
	@DAttr(name = "id", id = true, auto = true, length = 6, mutable = false, type = Type.Integer)
	private int id; 
	
//	static variable to keep track of account id
	private static int idCounter;
	
	@DAttr(name = "name", type = Type.String, length = 20, optional = false, cid = true) 
	private String name;
	
	@DAttr(name = "borrowAndLend", type = Type.Collection, optional = false, serialisable = false, filter = @Select(clazz = BorrowAndLend.class))
	@DAssoc(ascName = "subject-has-borrowAndLend", role = "subject", ascType = AssocType.One2Many, endType = AssocEndType.One, 
			associate = @Associate(type = BorrowAndLend.class, cardMin = 0, cardMax = MetaConstants.CARD_MORE ))
	private Collection<BorrowAndLend> borrowAndLend;
	private int borrowAndLendCount;
	 
	 
	// from object form: Account is not included 
	@DOpt(type=DOpt.Type.ObjectFormConstructor)
	@DOpt(type=DOpt.Type.RequiredConstructor)
	public Subjects (@AttrRef("name") String name) {
		this(null, name);
	}
	
	// from data source
	@DOpt(type=DOpt.Type.DataSourceConstructor)
	public Subjects (@AttrRef("id") Integer id, @AttrRef("name") String name ) {
		this.id = nextId(id);
		this.name = name;
		
		borrowAndLend = new ArrayList<>();
		borrowAndLendCount = 0;
	}
	
//	Setter methods
	@DOpt(type=DOpt.Type.Setter)
	public void setName(String name) {
		this.name = name;
	}
	
//	Getter methods
	@DOpt(type=DOpt.Type.Getter)
	public int getId() {
		return id;
	}
	
	@DOpt(type=DOpt.Type.Getter)
	public String getName() {
		return name;
	}
	
//	add existed object into collection
	@DOpt (type = DOpt.Type.LinkAdder)
	public boolean addBorrowAndLend(BorrowAndLend a) {
		if (!this.borrowAndLend.contains(a)) {
			borrowAndLend.add(a);
		}
		// no other attributes changed
		return false;
	}
	
//	add new object into collection
	@DOpt (type = DOpt.Type.LinkAdderNew)
	public boolean addNewborrowAndLend(BorrowAndLend a) {
		borrowAndLend.add(a);
		borrowAndLendCount++;
		return false;
	}
	
	@DOpt (type = DOpt.Type.LinkAdder)
	public boolean addBorrowAndLend(Collection<BorrowAndLend> actions) {
		for (BorrowAndLend a : actions) {
			if (!this.borrowAndLend.contains(a)) {
				this.borrowAndLend.add(a);
			}
		}
		return false;
	}
	
	@DOpt(type = DOpt.Type.LinkAdderNew)
	public boolean addNewBorrowAndLend(Collection<BorrowAndLend> actions) {
		this.borrowAndLend.addAll(actions);
		borrowAndLendCount += actions.size();
		// no other attributes changed
		return false;
	}
	
	@DOpt(type = DOpt.Type.LinkRemover)
	// only need to do this for reflexive association: @MemberRef(name="subjects")
	public boolean removeBorrowAndLend(BorrowAndLend a) {
		boolean removed = borrowAndLend.remove(a);

		if (removed) {
			borrowAndLendCount--;
		}
		// no other attributes changed
		return false;
	}
	
	@DOpt(type=DOpt.Type.Setter)
	public void setBorrowAndLend(Collection<BorrowAndLend> actions) {
		this.borrowAndLend = actions;
		borrowAndLendCount = actions.size();
	}
	
	@DOpt(type=DOpt.Type.Getter)
	public Collection<BorrowAndLend> getBorrowAndLend() {
		return borrowAndLend;
	}
	
	/**
	 * @effects return <tt>accountsCount</tt>
	 */
	@DOpt(type=DOpt.Type.LinkCountSetter)
	public void setBorrowAndLendCount(int borrowAndLendCount) {
		this.borrowAndLendCount = borrowAndLendCount;
	}
	
	@DOpt(type=DOpt.Type.LinkCountGetter)
	public int getBorrowAndLendCount() {
		return borrowAndLendCount;
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
	
	@Override
	public String toString() {
		return "Subject [id=" + id + ", name=" + name + "]";
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
		Subjects other = (Subjects) obj;
		if (id != other.id)
			return false;
		return true;
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
			int maxIdVal = (Integer) maxVal;
			if (maxIdVal > idCounter)
				idCounter = maxIdVal;
		}
	}	
}
