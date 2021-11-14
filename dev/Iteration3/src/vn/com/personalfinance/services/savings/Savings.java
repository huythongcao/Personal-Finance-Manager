package vn.com.personalfinance.services.savings;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

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
import vn.com.personalfinance.services.expenditure.model.ExpenditureSavings;

/**
 * Represents savings. The account ID is auto-incremented.
 * 
 * @author Nguyen Hai - Group 2
 * @version 1.0
 */

@DClass(schema="personalfinancemanagement")
public abstract class Savings {
	public static final String S_id = "id";
	public static final String S_amount = "amount";
	public static final String S_name = "name";
	public static final String S_purpose = "purpose";
	public static final String S_startDate = "startDate";
	public static final String S_account = "account";
	public static final String S_expenditureSavings = "expenditureSavings";

	// attributes of savings
	@DAttr(name = S_id, id = true, type = Type.Integer, auto = true, length = 6, mutable = false, optional = false)
	private int id;
	// static variable to keep track of account id
	private static int idCounter = 0;
	
	@DAttr(name = S_name, type = Type.String, length = 15, optional = false, cid=true)
	private String name;
	
	@DAttr(name = S_purpose, type = Type.String, length = 30, optional = true)
	private String purpose;
		
	@DAttr(name = S_amount, type = Type.Double, length = 15, optional = false)
	private double amount;
		
	@DAttr(name = S_startDate, type = Type.Date, length = 15, optional = false) 
	private Date startDate;
	
	@DAttr(name = S_expenditureSavings, type = Type.Collection, serialisable = false, optional = false, 
			filter = @Select(clazz = ExpenditureSavings.class))
	@DAssoc(ascName = "savings-has-expenditureSavings", role = "savings", 
		ascType = AssocType.One2Many, endType = AssocEndType.One, 
		associate = @Associate(type = ExpenditureSavings.class, cardMin = 1, cardMax = 25))
	private Collection<ExpenditureSavings> expenditureSavings;
	
	// derived attributes
	private int expenditureSavingsCount;
	
	// static variable to keep track of savings code
	private static Map<Tuple,Integer> currNums = new LinkedHashMap<Tuple,Integer>();
	
	// constructor methods
	@DOpt(type=DOpt.Type.ObjectFormConstructor)
	protected Savings(@AttrRef("name") String name,
			@AttrRef("purpose") String purpose,
			@AttrRef("amount") Double amount,
			@AttrRef("startDate") Date startDate) {
		this(null, name, purpose, amount, startDate);
	}
		
	// a shared constructor that is invoked by other constructors
	@DOpt(type=DOpt.Type.DataSourceConstructor)
	protected Savings (Integer id, String name, String purpose, 
		Double amount,  Date startDate) throws ConstraintViolationException {
		// generate an id
		this.id = nextID(id);   
		// assign other values
		this.name = name;
		this.purpose = purpose;
		this.amount = amount;
		this.startDate = startDate;
	}
	
	// getter methods
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}

	public String getPurpose() {
		return purpose;
	}
	
	public double getAmount() {
		return amount;
	}

	public Date getStartDate() {
		return startDate ;
	}

	public Collection<ExpenditureSavings> getExpenditureSavings() {
		return expenditureSavings;
	}

	// setter methods
	
	public void setName(String name) {
		this.name = name;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}
	

	public void setAmount(double amount) {
		this.amount = amount;
	}
		
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public void setExpenditureSavings(Collection<ExpenditureSavings> eS) {
		this.expenditureSavings = eS;
		expenditureSavingsCount = eS.size();

		// v2.6.4.b
		//computeAverageMark();
	}
	
	//expenditureSavings
	@DOpt(type = DOpt.Type.LinkCountGetter)
	public Integer getExpenditureSavingsCount() {
		return expenditureSavingsCount;
		// return enrolments.size();
	}

	@DOpt(type = DOpt.Type.LinkCountSetter)
	public void setExpenditureSavingsCount(int count) {
		expenditureSavingsCount = count;
	}

	/*@DOpt(type = DOpt.Type.LinkAdder)
	// only need to do this for reflexive association: @MemberRef(name="enrolments")
	public boolean addExpenditureSavings(ExpenditureSavings e) {
		if (!expenditureSavings.contains(e))
			expenditureSavings.add(e);

		// IMPORTANT: enrolment count must be updated separately by invoking
		// setEnrolmentCount
		// otherwise computeAverageMark (below) can not be performed correctly
		// WHY? average mark is not serialisable
//    enrolmentCount++;
//    
//    // v2.6.4.b
//    computeAverageMark();

		// no other attributes changed
		return false;
	}

	@DOpt(type = DOpt.Type.LinkAdderNew)
	public boolean addNewExpenditureSavings(ExpenditureSavings e) {
		expenditureSavings.add(e);

		expenditureSavingsCount++;

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
			if (!expenditureSavings.contains(e)) {
				if (!added)
					added = true;
				expenditureSavings.add(e);
			}
		}
		return false;
	}

	@DOpt(type = DOpt.Type.LinkAdderNew)
	public boolean addNewExpenditureSavings(Collection<ExpenditureSavings> expSavings) {
		expenditureSavings.addAll(expSavings);
		expenditureSavingsCount += expSavings.size();

		// v2.6.4.b
		//computeAverageMark();

		// no other attributes changed (average mark is not serialisable!!!)
		return false;
	}

	@DOpt(type = DOpt.Type.LinkRemover)
	// @MemberRef(name="enrolments")
	public boolean removeExpenditureSavings(ExpenditureSavings e) {
		boolean removed = expenditureSavings.remove(e);

		if (removed) {
			expenditureSavingsCount--;

			// v2.6.4.b
			//computeAverageMark();
		}
		// no other attributes changed
		return false;
	}

	@DOpt(type = DOpt.Type.LinkUpdater)
	// @MemberRef(name="enrolments")
	public boolean updateExpenditureSavings(ExpenditureSavings e) throws IllegalStateException {
		// recompute using just the affected enrolment
		/*
		 * double totalMark = averageMark * enrolmentCount;
		 * 
		 * int oldFinalMark = e.getFinalMark(true);
		 * 
		 * int diff = e.getFinalMark() - oldFinalMark;
		 * 
		 * // TODO: cache totalMark if needed
		 * 
		 * totalMark += diff;
		 * 
		 * averageMark = totalMark / enrolmentCount;
		 */

		// no other attributes changed
//		return true;
//	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "(" + getId() + "," + getAmount() + "," + getName() + ")";
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
		Savings other = (Savings) obj;
		if (id == 0) {
			if (other.id != 0)
				return false;
		} else if (!(id == other.id))
			return false;
		return true;
	}
	
	private static int nextID(Integer currID) {
		if (currID == null) { 
			// generate one
			idCounter++;
			return idCounter;
		} else { 
			// update
			int num;
			num = currID.intValue();

			if (num > idCounter) {
				idCounter = num;
			}
			return currID;
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
				int maxIdVal = (Integer) maxVal;
				if (maxIdVal > idCounter)
					idCounter = maxIdVal;
			} else if (attrib.name().equals("code")) {
		        String maxCode = (String) maxVal;
		        
		        try {
		          int maxCodeNum = Integer.parseInt(maxCode.substring(1));
		          
		          // current max num for the semester
		          Integer currNum = currNums.get(derivingValue);
		          
		          if (currNum == null || maxCodeNum > currNum) {
		            currNums.put(derivingValue, maxCodeNum);
		          }
		          
		        } catch (RuntimeException e) {
		          throw new ConstraintViolationException(
		              ConstraintViolationException.Code.INVALID_VALUE, e, new Object[] {maxCode});
		        }
			}
		}
	}
}
