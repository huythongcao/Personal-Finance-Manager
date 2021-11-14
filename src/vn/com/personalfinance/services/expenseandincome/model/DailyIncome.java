package vn.com.personalfinance.services.expenseandincome.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import domainapp.basics.util.Tuple;
import vn.com.personalfinance.services.account.Account;
import vn.com.personalfinance.services.expenseandincome.report.ExpenseAndIncomeByCategoryReport;
import vn.com.personalfinance.services.expenseandincome.report.ExpenseAndIncomeByDateReport;
import vn.com.personalfinance.services.expenseandincome.report.ExpenseAndIncomeByMonthReport;
import vn.com.personalfinance.services.expenseandincome.report.ExpenseAndIncomeByYearReport;

/**
 * Represents income.
 * 
 * @author Nguyen Quynh Nga - Group 2
 * @version 1.0
 */
@DClass(schema="personalfinancemanager")
public class DailyIncome {
	public static final String I_id = "id";
	public static final String I_amount = "amount";
	public static final String I_date = "date";
	public static final String I_category = "category";
	public static final String I_account = "account";
	public static final String I_description = "description";
	public static final String I_rptExpenseAndIncomeByCategory = "rptExpenseAndIncomeByCategory";
	public static final String I_rptExpenseAndIncomeByDate = "rptExpenseAndIncomeByDate";
	public static final String I_rptExpenseAndIncomeByMonth = "rptExpenseAndIncomeByMonth";
	public static final String I_rptExpenseAndIncomeByYear = "rptExpenseAndIncomeByYear";
	public static final String I_dateToString = "dateToString";

	// attributes of daily expense
	@DAttr(name = I_id, id = true, type = Type.String, auto = true, length = 6, mutable = false, optional = false)
	private String id;
	// static variable to keep track of account id
	public static int idCounter = 0;

	@DAttr(name = I_amount, type = Type.Double, length = 15, optional = false, min = 0)
	private double amount;

	@DAttr(name = I_date, type = Type.Date, length = 15, optional = false)
	private Date date;
	
	@DAttr(name = I_dateToString, type = Type.String, auto = true, length = 15, mutable = false, serialisable =true)
	private String dateToString;

	@DAttr(name = I_category, type = Type.Domain, optional = false)
	@DAssoc(ascName = "category-has-dailyIncome", role = "dailyIncome", ascType = AssocType.One2Many, endType = AssocEndType.Many, associate = @Associate(type = Category.class, cardMin = 1, cardMax = 1), dependsOn = true)
	private Category category;

	@DAttr(name = I_account, type = Type.Domain, optional = false)
	@DAssoc(ascName = "account-has-dailyIncome", role = "dailyIncome", ascType = AssocType.One2Many, endType = AssocEndType.Many, associate = @Associate(type = Account.class, cardMin = 1, cardMax = 1), dependsOn = true)
	private Account account;

	@DAttr(name = I_description, type = Type.String, length = 30)
	private String description;

	@DAttr(name = I_rptExpenseAndIncomeByCategory, type = Type.Domain, serialisable = false, virtual = true)
	private ExpenseAndIncomeByCategoryReport rptExpenseAndIncomeByCategory;

	@DAttr(name = I_rptExpenseAndIncomeByDate, type = Type.Domain, serialisable = false, virtual = true)
	private ExpenseAndIncomeByDateReport rptExpenseAndIncomeByDate;
	
	@DAttr(name = I_rptExpenseAndIncomeByMonth, type = Type.Domain, serialisable = false, virtual = true)
	private ExpenseAndIncomeByMonthReport rptExpenseAndIncomeByMonth;
	
	@DAttr(name = I_rptExpenseAndIncomeByYear, type = Type.Domain, serialisable = false, virtual = true)
	private ExpenseAndIncomeByYearReport rptExpenseAndIncomeByYear;

	// constructor methods
	@DOpt(type = DOpt.Type.ObjectFormConstructor)
	public DailyIncome(@AttrRef("amount") Double amount, @AttrRef("date") Date date, 
			@AttrRef("category") Category category, @AttrRef("account") Account account,
			@AttrRef("description") String description) {
		this(null, amount, date, null, category, account, description);
	}

	// a shared constructor that is invoked by other constructors
	@DOpt(type = DOpt.Type.DataSourceConstructor)
	public DailyIncome(String id, Double amount, Date date, String dateToString, Category category, Account account,
			String description) {
		// generate an id
		this.id = nextID(id);

		// assign other values
		this.amount = amount;
		this.date = date;
		this.dateToString = dateToString;
		this.category = category;
		this.account = account;
		this.description = description;

		updateDateToString();
//		computeNewBalance();
	}
	// getter and setter method

	public String getId() {
		return id;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
		
		updateDateToString();
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ExpenseAndIncomeByCategoryReport getRptExpenseAndIncomeByCategory() {
		return rptExpenseAndIncomeByCategory;
	}

	public ExpenseAndIncomeByDateReport getRptExpenseAndIncomeByDate() {
		return rptExpenseAndIncomeByDate;
	}
	
	public ExpenseAndIncomeByMonthReport getRptExpenseAndIncomeByMonth() {
		return rptExpenseAndIncomeByMonth;
	}
	
	public ExpenseAndIncomeByYearReport getRptExpenseAndIncomeByYear() {
		return rptExpenseAndIncomeByYear;
	}
	
	public String getDateToString() {
		return dateToString;
	}
	
	// automatically generate the next account id
	public String nextID(String id) throws ConstraintViolationException {
		if (id == null) { // generate a new id
			idCounter++;
			return "I" + idCounter;
		} else {
			// update id
			int num;
			try {
				num = Integer.parseInt(id.substring(1));
			} catch (RuntimeException e) {
				throw new ConstraintViolationException(ConstraintViolationException.Code.INVALID_VALUE, e, new Object[] { id });
			}

			if (num > idCounter) {
				idCounter = num;
			}

			return id;
		}
	}

//	public void computeNewBalance() {
//		double newBalance = getAccount().getBalance() + amount;
//		getAccount().setBalance(newBalance);
//	}
	
	@Override
	public String toString() {
		return toString(true);
	}

	/**
	 * @effects returns
	 *          <code>Savings(id,code,amount,name,purpose,startDate,monthlyDuration)</code>.
	 */
	public String toString(boolean full) {
		if (full)
			return "DailyIncome(" + id + "," + amount + "," + date + "," + category + "," + account + ")";
		else
			return "DailyIncome(" + id + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Integer.parseInt(id.substring(1));
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
		DailyIncome other = (DailyIncome) obj;

		if (!(id.equals(other.id))) {
			return false;
		}
		return true;
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
					int maxIDNum = Integer.parseInt(maxID.substring(1));

					if (maxIDNum > idCounter) {
						idCounter = maxIDNum;
					}

				} catch (RuntimeException e) {
					throw new ConstraintViolationException(ConstraintViolationException.Code.INVALID_VALUE, e,
							new Object[] { maxID });
				}
			}
		}
	}

	@DOpt(type=DOpt.Type.DerivedAttributeUpdater)
	@AttrRef(value=I_dateToString)
	public void updateDateToString() {
		DateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
		this.dateToString = dateFormat.format(date);
	}
}
