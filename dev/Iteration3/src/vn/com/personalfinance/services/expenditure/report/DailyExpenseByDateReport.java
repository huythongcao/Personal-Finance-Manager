package vn.com.personalfinance.services.expenditure.report;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
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
import vn.com.personalfinance.services.expenditure.model.DailyExpense;

/**
 * @overview 
 * 	Represent the reports about daily expense by date.
 * 
 * @author Nguyen Hai - Group 2
 *
 * @version 1.0
 */
@DClass(schema="personalfinancemanagement",serialisable=false)
public class DailyExpenseByDateReport {
	@DAttr(name = "id", id = true, auto = true, type = Type.Integer, length = 5, optional = false, mutable = false)
	private int id;
	private static int idCounter = 0;

	/** input: daily expense date */
	@DAttr(name = "date", type = Type.Date, length = 30, optional = false)
	private Date date;

	/** output: daily expense which date match {@link #date} */
	@DAttr(name = "dailyExpense", type = Type.Collection, optional = false, mutable = false,
	serialisable = false, filter = @Select(clazz = DailyExpense.class), 
	derivedFrom = { "date" })
	@DAssoc(ascName = "dailyExpense-by-date-report-has-dailyExpense",
	role = "report", ascType = AssocType.One2Many, endType = AssocEndType.One, 
	associate = @Associate(type = DailyExpense.class, cardMin = 0,
	cardMax = MetaConstants.CARD_MORE))
	@Output
	private Collection<DailyExpense> dailyExpense;

	/** output: number of daily expense found (if any), derived from {@link #dailyExpense} */
	@DAttr(name = "numDailyExpense", type = Type.Integer, length = 20, auto = true, mutable = false)
	@Output
	private int numDailyExpense;

	/**
	   * @effects 
	   *  initialise this with <tt>name</tt> and use {@link QRM} to retrieve from data source 
	   *  all {@link Student} whose names match <tt>name</tt>.
	   *  initialise {@link #students} with the result if any.
	   *  
	   *  <p>throws NotPossibleException if failed to generate data source query; 
	   *  DataSourceException if fails to read from the data source
	   * 
	   */
	  @DOpt(type=DOpt.Type.ObjectFormConstructor)
	  @DOpt(type=DOpt.Type.RequiredConstructor)
	  public DailyExpenseByDateReport(@AttrRef("date") Date date) throws NotPossibleException, DataSourceException {
	    this.id=++idCounter;
	    
	    this.date = date;
	    
	    doReportQuery();
	  }

	/**
	 * @effects return date
	 */
	public Date getDate() {
		return date;
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
	public void setDate(Date date) throws NotPossibleException, DataSourceException {

		this.date = date;

		doReportQuery();
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
	public void doReportQuery() throws NotPossibleException, DataSourceException {
		// the query manager instance

		QRM qrm = QRM.getInstance();

		// create a query to look up Student from the data source
		// and then populate the output attribute (students) with the result
		DSMBasic dsm = qrm.getDsm();

		// TODO: to conserve memory cache the query and only change the query parameter
		// value(s)
		Query q = QueryToolKit.createSearchQuery(dsm, DailyExpense.class, new String[] { DailyExpense.D_date },
				new Op[] { Op.MATCH },
				// new Object[] {"%"+name+"%"});
				// Chapter 3 - Exercise 1
				new Object[] { date + "%" });

		// Chapter 6 - Exercise 2
		Query q1 = QueryToolKit.createSearchQuery(dsm, DailyExpense.class, new String[] { DailyExpense.D_date },
				new Op[] { Op.MATCH }, new Object[] { "%" + date });

		Map<Oid, DailyExpense> result = qrm.getDom().retrieveObjects(DailyExpense.class, q);
		Map<Oid, DailyExpense> result1 = qrm.getDom().retrieveObjects(DailyExpense.class, q1);
		// TODO: merge 2 maps
		Map<Oid, DailyExpense> result2 = new HashMap<>();
		if (result != null) {
			result2.putAll(result);
		}
		if (result1 != null) {
			result2.putAll(result1);
		}
		if (!result2.isEmpty()) {
			// update the main output data
			dailyExpense = result2.values();
			// update other output (if any)
			numDailyExpense = dailyExpense.size();
		} else {
			// no data found: reset output
			resetOutput();
		}
	}

	/**
	 * @effects reset all output attributes to their initial values
	 */
	private void resetOutput() {
		dailyExpense = null;
		numDailyExpense = 0;
	}

	/**
	 * A link-adder method for {@link #dailyExpense}, required for the object form to
	 * function. However, this method is empty because students have already be
	 * recorded in the attribute {@link #dailyExpense}.
	 */
	@DOpt(type = DOpt.Type.LinkAdder)
	public boolean addDailyExpense(Collection<DailyExpense> dailyExpense) {
		// do nothing
		return false;
	}

	/**
	 * @effects return students
	 */
	public Collection<DailyExpense> getDailyExpense() {
		return dailyExpense;
	}

	/**
	 * @effects return numStudents
	 */
	public int getNumDailyExpense() {
		return numDailyExpense;
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
		DailyExpenseByDateReport other = (DailyExpenseByDateReport) obj;
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
		return "DailyExpenseByDateReport (" + id + ", " + date + ")";
	}

}
