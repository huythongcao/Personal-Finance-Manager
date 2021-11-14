package vn.com.personalfinance.services.expenseandincome.report;
import java.util.Collection;
import java.util.Map;

import domainapp.basics.core.dodm.dsm.DSMBasic;
import domainapp.basics.core.dodm.qrm.QRM;
import domainapp.basics.exceptions.DataSourceException;
import domainapp.basics.exceptions.NotPossibleException;
import domainapp.basics.model.Oid;
import domainapp.basics.model.meta.AttrRef;
import domainapp.basics.model.meta.DAssoc;
import domainapp.basics.model.meta.DAssoc.AssocEndType;
import domainapp.basics.model.meta.DAssoc.AssocType;
import domainapp.basics.model.meta.DAssoc.Associate;
import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DAttr.Type;
import domainapp.basics.model.meta.DClass;
import domainapp.basics.model.meta.DOpt;
import domainapp.basics.model.meta.MetaConstants;
import domainapp.basics.model.meta.Select;
import domainapp.basics.model.query.Expression.Op;
import domainapp.basics.model.query.Query;
import domainapp.basics.model.query.QueryToolKit;
import domainapp.basics.modules.report.model.meta.Output;
import vn.com.personalfinance.services.expenseandincome.model.DailyExpense;
import vn.com.personalfinance.services.expenseandincome.model.DailyIncome;
/**
 * @overview 
 * 	Represent the reports about daily expense by date.
 * 
 * @author Nguyen Quynh Nga - Group 2
 *
 * @version 1.0
 */
@DClass(schema="personalfinancemanager",serialisable=false)

public class ExpenseAndIncomeByYearReport {
	public static final String R_year = "year";
	public static final String R_dailyExpense = "dailyExpense";
	public static final String R_dailyIncome = "dailyIncome";
	@DAttr(name = "id", id = true, auto = true, type = Type.Integer, length = 5, optional = false, mutable = false)
	private int id;
	private static int idCounter = 0;

	/** input: daily expense date */
	
	@DAttr(name = R_year, type = Type.String, length = 15, optional = false)
	private String year;
	
	/** output: daily expense which date match {@link #date} */
	@DAttr(name = R_dailyExpense, type = Type.Collection, optional = false, mutable = false, serialisable = false, filter = @Select(clazz = DailyExpense.class), derivedFrom = {"year" })
	@DAssoc(ascName = "expenseAndIncome-by-month-report-has-dailyExpense", role = "report", ascType = AssocType.One2Many, endType = AssocEndType.One, associate = @Associate(type = DailyExpense.class, cardMin = 0, cardMax = MetaConstants.CARD_MORE))
	@Output
	private Collection<DailyExpense> dailyExpense;
	
	@DAttr(name = R_dailyIncome, type = Type.Collection, optional = false, mutable = false, serialisable = false, filter = @Select(clazz = DailyIncome.class), derivedFrom = {"year"})
	@DAssoc(ascName = "expenseAndIncome-by-month-report-has-dailyIncome", role = "report", ascType = AssocType.One2Many, endType = AssocEndType.One, associate = @Associate(type = DailyIncome.class, cardMin = 0, cardMax = MetaConstants.CARD_MORE))
	@Output
	private Collection<DailyIncome> dailyIncome;

	/**
	 * output: number of daily expenses found (if any), derived from
	 * {@link #dailyExpense}
	 */
	@DAttr(name = "numDailyTransaction", type = Type.Integer, length = 20, auto = true, mutable = false)
	@Output
	private int numDailyTransaction;
	
	@DAttr(name = "totalExpense", type = Type.Double, length = 20, auto = true, mutable = false)
	@Output
	private double totalExpense;
	
	@DAttr(name = "totalIncome", type = Type.Double, length = 20, auto = true, mutable = false)
	@Output
	private double totalIncome;

	/**
	 * @effects 
	 * initialise this with <tt>date</tt> and use {@link QRM} to retrieve
	 * from data source all {@link dailyExpense} which dates match
	 * <tt>date</tt>. initialise {@link #dailyExpense} with the result if
	 * any.
	 * 
	 * <p>
	 * throws NotPossibleException if failed to generate data source query;
	 * DataSourceException if fails to read from the data source
	 * 
	 */
	@DOpt(type = DOpt.Type.ObjectFormConstructor)
	@DOpt(type = DOpt.Type.RequiredConstructor)
	public ExpenseAndIncomeByYearReport(@AttrRef("year") String year) throws NotPossibleException, DataSourceException {
		this.id = ++idCounter;
		this.year = year;
		doReportQueryDailyExpense();
		doReportQueryDailyIncome();
	}

	/**
	 * @effects return date
	 */
	public String getYear() {
		return year;
	}

	public double getTotalExpense() {
		return totalExpense;
	}

	public double getTotalIncome() {
		return totalIncome;
	}

	/**
	 * @effects
	 * 
	 *          <pre>
	 *  set this.date = date
	 *  if date is changed
	 *    invoke {@link #doReportQuery()} to update the output attribute value
	 *    throws NotPossibleException if failed to generate data source query; 
	 *    DataSourceException if fails to read from the data source.
	 *          </pre>
	 */	
	public void setYear(String year) throws NotPossibleException, DataSourceException {
		this.year = year;

		doReportQueryDailyExpense();
		doReportQueryDailyIncome();
	}

	/**
	 * This method is invoked when the report input has be set by the user.
	 * 
	 * @effects
	 * 
	 *          <pre>
	 *   formulate the object query
	 *   execute the query to retrieve from the data source the domain objects that satisfy it 
	 *   update the output attributes accordingly.
	 *  
	 *  <p>throws NotPossibleException if failed to generate data source query; 
	 *  DataSourceException if fails to read from the data source.
	 *          </pre>
	 */
	@DOpt(type = DOpt.Type.DerivedAttributeUpdater)
	@AttrRef(value = "dailyExpense")
	public void doReportQueryDailyExpense() throws NotPossibleException, DataSourceException {
		// the query manager instance

		QRM qrm = QRM.getInstance();

		DSMBasic dsm = qrm.getDsm();

		// TODO: to conserve memory cache the query and only change the query parameter
		// value(s)
		Query q = QueryToolKit.createSearchQuery(dsm, DailyExpense.class, new String[] { DailyExpense.E_dateToString },
				new Op[] { Op.MATCH },
				new Object[] { "%"+year+"%" });

		Map<Oid, DailyExpense> result = qrm.getDom().retrieveObjects(DailyExpense.class, q);

		if (!(result == null)) {
			// update the main output data
			dailyExpense = result.values();
			// update other output (if any)
			numDailyTransaction += dailyExpense.size();
			
			if(numDailyTransaction == 0) {
				totalExpense = 0;
			} else {
				double temp = 0.0;
				for(DailyExpense d: dailyExpense) {
					temp+=d.getAmount();
				}
				totalExpense = temp;
			}
		} else {
			// no data found: reset output
			resetOutputDailyExpense();
		}
	}
	
	@DOpt(type = DOpt.Type.DerivedAttributeUpdater)
	@AttrRef(value = "dailyIncome")
	public void doReportQueryDailyIncome() throws NotPossibleException, DataSourceException {
		// the query manager instance

		QRM qrm = QRM.getInstance();

		DSMBasic dsm = qrm.getDsm();

		// TODO: to conserve memory cache the query and only change the query parameter
		// value(s)
		Query q = QueryToolKit.createSearchQuery(dsm, DailyIncome.class, new String[] { DailyIncome.I_dateToString },
				new Op[] { Op.MATCH },

				new Object[] { "%"+year+"%" });

		Map<Oid, DailyIncome> result = qrm.getDom().retrieveObjects(DailyIncome.class, q);

		if (!(result == null)) {
			// update the main output data
			dailyIncome = result.values();
			// update other output (if any)
			numDailyTransaction += dailyIncome.size();

			if(numDailyTransaction == 0) {
				totalIncome = 0;
			} else {
				double temp = 0.0;
				for(DailyIncome d: dailyIncome) {
					temp+=d.getAmount();
				}
				totalIncome = temp;
			}
		} else {
			// no data found: reset output
			resetOutputDailyIncome();
		}
	}

	/**
	 * @effects reset all output attributes to their initial values
	 */
	private void resetOutputDailyExpense() {
		dailyExpense = null;
		numDailyTransaction = 0;
		totalExpense = 0;
	}
	
	private void resetOutputDailyIncome() {
		dailyIncome = null;
		numDailyTransaction = 0;
		totalIncome = 0;
	}

	/**
	 * A link-adder method for {@link #dailyExpense}, required for the object form
	 * to function. However, this method is empty because dailyExpense have already
	 * be recorded in the attribute {@link #dailyExpense}.
	 */
	@DOpt(type = DOpt.Type.LinkAdder)
	@AttrRef(value=R_dailyExpense)
	public boolean addDailyExpense(Collection<DailyExpense> dailyExpense) {
		// do nothing
		return false;
	}
	
	@DOpt(type = DOpt.Type.LinkAdder)
	@AttrRef(value=R_dailyIncome)
	public boolean addDailyIncome(Collection<DailyIncome> dailyIncome) {
		// do nothing
		return false;
	}

	/**
	 * @effects return dailyExpense
	 */
	public Collection<DailyExpense> getDailyExpense() {
		return dailyExpense;
	}
	
	/**
	 * @effects return dailyExpense
	 */
	public Collection<DailyIncome> getDailyIncome() {
		return dailyIncome;
	}

	/**
	 * @effects return numDailyExpense
	 */
	public int getNumDailyTransaction() {
		return numDailyTransaction;
	}

	/**
	 * @effects return id
	 */
	public int getId() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	/**
	 * @effects
	 * 
	 * @version
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	/**
	 * @effects
	 * 
	 * @version
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExpenseAndIncomeByYearReport other = (ExpenseAndIncomeByYearReport) obj;
		if (id != other.id)
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	/**
	 * @effects
	 * 
	 * @version
	 */
	@Override
	public String toString() {
		return "DailyExpenseByYearReport (" + id + ", " + year + ")";
	}
}