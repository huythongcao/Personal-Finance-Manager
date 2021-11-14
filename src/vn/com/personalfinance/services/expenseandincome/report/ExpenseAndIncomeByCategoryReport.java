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
import vn.com.personalfinance.services.expenseandincome.model.Category;
import vn.com.personalfinance.services.expenseandincome.model.DailyExpense;
import vn.com.personalfinance.services.expenseandincome.model.DailyIncome;

/**
 * @overview 
 * 	Represent the reports about daily expense by category.
 * 
 * @author Nguyen Hai - Group 2
 *
 * @version 1.0
 */
@DClass(schema="personalfinancemanager",serialisable=false)
public class ExpenseAndIncomeByCategoryReport {
	@DAttr(name = "id", id = true, auto = true, type = Type.Integer, length = 5, optional = false, mutable = false)
	private int id;
	private static int idCounter = 0;

	/** input: daily expense category */
	@DAttr(name = "category", type = Type.String, length = 30, optional = false)
	private String category;

	/** output: daily expense which categories match {@link #category} */
	@DAttr(name = "dailyExpense", type = Type.Collection, optional = false, mutable = false,
		serialisable = false, filter = @Select(clazz = DailyExpense.class),
		derivedFrom = {"category"})
	@DAssoc(ascName = "expense-and-income-by-category-report-has-dailyExpense",
	role = "report", ascType = AssocType.One2Many, endType = AssocEndType.One, 
	associate = @Associate(type = DailyExpense.class, cardMin = 0,
	cardMax = MetaConstants.CARD_MORE))
	@Output
	private Collection<DailyExpense> dailyExpense;

	/** output: number of daily expenses found (if any), derived from {@link #dailyExpense} */
	@DAttr(name = "numDailyExpense", type = Type.Integer, length = 5, auto = true, mutable = false)
	@Output
	private int numDailyExpense;
	
	/** output: daily income which categories match {@link #category} */
	@DAttr(name = "dailyIncome", type = Type.Collection, optional = false, mutable = false,
		serialisable = false, filter = @Select(clazz = DailyIncome.class),
		derivedFrom = {"category"})
	@DAssoc(ascName = "expense-and-income-by-category-report-has-dailyIncome",
	role = "report", ascType = AssocType.One2Many, endType = AssocEndType.One, 
	associate = @Associate(type = DailyIncome.class, cardMin = 0,
	cardMax = MetaConstants.CARD_MORE))
	@Output
	private Collection<DailyIncome> dailyIncome;

	/** output: number of daily incomes found (if any), derived from {@link #dailyIncome} */
	@DAttr(name = "numDailyIncome", type = Type.Integer, length = 5, auto = true, mutable = false)
	@Output
	private int numDailyIncome;
	
	@DAttr(name = "totalExpense", type = Type.Double, length = 20, auto = true, mutable = false)
	@Output
	private double totalExpense;
	
	@DAttr(name = "totalIncome", type = Type.Double, length = 20, auto = true, mutable = false)
	@Output
	private double totalIncome;

	/**
	   * @effects 
	   *  initialise this with <tt>category</tt> and use {@link QRM} to retrieve from data source 
	   *  all {@link dailyExpense} which categories match <tt>category</tt>.
	   *  initialise {@link #dailyExpense} with the result if any.
	   *  
	   *  <p>throws NotPossibleException if failed to generate data source query; 
	   *  DataSourceException if fails to read from the data source
	   * 
	   */
	  @DOpt(type=DOpt.Type.ObjectFormConstructor)
	  @DOpt(type=DOpt.Type.RequiredConstructor)
	  public ExpenseAndIncomeByCategoryReport(@AttrRef("category") String category) throws NotPossibleException, DataSourceException {
	    this.id=++idCounter;
	    
	    this.category = category;
	    
	    doReportQuery1();
	    doReportQuery2();
	  }

	/**
	 * @effects return category
	 */
	public String getCategory() {
		return category;
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
	 *  set this.category = category
	 *  if category is changed
	 *    invoke {@link #doReportQuery()} to update the output attribute value
	 *    throws NotPossibleException if failed to generate data source query; 
	 *    DataSourceException if fails to read from the data source.
	 *          </pre>
	 */
	public void setCategory(String category) throws NotPossibleException, DataSourceException {
		this.category = category;

		doReportQuery1();
		doReportQuery2();
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
	public void doReportQuery1() throws NotPossibleException, DataSourceException {
		// the query manager instance

		QRM qrm = QRM.getInstance();

		DSMBasic dsm = qrm.getDsm();

		// TODO: to conserve memory cache the query and only change the query parameter
		// value(s)
		Query q1 = QueryToolKit.createSimpleJoinQuery(dsm, DailyExpense.class, Category.class,
				DailyExpense.E_category, 
				Category.C_name, 
		        Op.MATCH, 
		        "%"+category+"%");

		Map<Oid, DailyExpense> result1 = qrm.getDom().retrieveObjects(DailyExpense.class, q1);
		
		if (result1 != null) {
			dailyExpense = result1.values();
			numDailyExpense = dailyExpense.size();
			
			double tempAmount = 0.0;
			for(DailyExpense d: dailyExpense) {
				tempAmount+=d.getAmount();
			}
			totalExpense = tempAmount;
		} else {
			// no data found: reset output
			resetOutput1();
		}
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
	@AttrRef(value = "dailyIncome")
	public void doReportQuery2() throws NotPossibleException, DataSourceException {
		// the query manager instance

		QRM qrm = QRM.getInstance();

		DSMBasic dsm = qrm.getDsm();

		// TODO: to conserve memory cache the query and only change the query parameter
		// value(s)
		Query q2 = QueryToolKit.createSimpleJoinQuery(dsm, DailyIncome.class, Category.class,
				DailyIncome.I_category, 
				Category.C_name, 
		        Op.MATCH, 
		        "%"+category+"%");

		Map<Oid, DailyIncome> result2 = qrm.getDom().retrieveObjects(DailyIncome.class, q2);
		
		if (result2 != null) {	
			dailyIncome = result2.values();
			numDailyIncome = dailyIncome.size();
			
			double tempAmount = 0.0;
			for(DailyIncome d: dailyIncome) {
				tempAmount+=d.getAmount();
			}
			totalIncome = tempAmount;
		} else {
			// no data found: reset output
			resetOutput2();
		}
	}

	/**
	 * @effects reset all output attributes to their initial values
	 */
	private void resetOutput1() {
		dailyExpense = null;
		numDailyExpense = 0;
	}
	
	/**
	 * @effects reset all output attributes to their initial values
	 */
	private void resetOutput2() {
		dailyIncome = null;
		numDailyIncome = 0;
	}

	/**
	 * A link-adder method for {@link #dailyExpense}, required for the object form to
	 * function. However, this method is empty because dailyExpense have already be
	 * recorded in the attribute {@link #dailyExpense}.
	 */
	@DOpt(type = DOpt.Type.LinkAdder)
	@AttrRef(value = "dailyExpense")
	public boolean addDailyExpense(Collection<DailyExpense> dailyExpense) {
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
	 * @effects return numDailyExpense
	 */
	public int getNumDailyExpense() {
		return numDailyExpense;
	}
	
	/**
	 * A link-adder method for {@link #dailyIncome}, required for the object form to
	 * function. However, this method is empty because dailyExpense have already be
	 * recorded in the attribute {@link #dailyIncome}.
	 */
	@DOpt(type = DOpt.Type.LinkAdder)
	@AttrRef(value = "dailyIncome")
	public boolean addDailyIncome(Collection<DailyIncome> dailyIncome) {
		// do nothing
		return false;
	}

	/**
	 * @effects return dailyIncome
	 */
	public Collection<DailyIncome> getDailyIncome() {
		return dailyIncome;
	}

	/**
	 * @effects return numDailyIncome
	 */
	public int getNumDailyIncome() {
		return numDailyIncome;
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
		ExpenseAndIncomeByCategoryReport other = (ExpenseAndIncomeByCategoryReport) obj;
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
		return this.getClass().getSimpleName()+ " (" + id + ", " + dailyExpense + ", " + dailyIncome + ")";
	}

}
