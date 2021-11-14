package vn.com.personalfinance.services.borrowandlend.report;

import java.util.Collection; 
import java.util.Map;

import domainapp.basics.core.dodm.dsm.DSMBasic;
import domainapp.basics.core.dodm.qrm.QRM;
import domainapp.basics.exceptions.DataSourceException;
import domainapp.basics.exceptions.NotPossibleException;
import domainapp.basics.model.Oid;
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
import domainapp.basics.model.query.Query;
import domainapp.basics.model.query.QueryToolKit;
import domainapp.basics.model.query.Expression.Op;
import domainapp.basics.modules.report.model.meta.Output;
import vn.com.personalfinance.services.borrowandlend.model.ActionType;
import vn.com.personalfinance.services.borrowandlend.model.BorrowAndLend;

@DClass(schema="personalfinancemanager",serialisable=false)
public class BorrowAndLendByActionTypeReport {
	@DAttr(name = "id", id = true, auto = true, type = Type.Integer, length = 5, optional = false, mutable = false)
	private int id;
	private static int idCounter = 0;

	/** input: daily expense category */
	@DAttr(name = "actionType", type = Type.String, length = 30, optional = false)
	private String actionType;
	
	/** output: number of daily expenses found (if any), derived from {@link #dailyExpense} */
	@DAttr(name = "numBorrowAndLend", type = Type.Integer, length = 20, auto = true, mutable = false)
	@Output
	private int numBorrowAndLend;
	
	@DAttr (name = "totalCollectedDebts", type = Type.Double, length = 20, auto = true, mutable = false)
	private double totalCollectedDebts;
	
	@DAttr (name = "totalBorrowedMoney", type = Type.Double, length = 20, auto = true, mutable = false)
	private double totalBorrowedMoney;
	
	@DAttr (name = "totalRepayedMoney", type = Type.Double, length = 20, auto = true, mutable = false)
	private double totalRepayedMoney;
	
	@DAttr (name = "totalLendedMoney", type = Type.Double, length = 20, auto = true, mutable = false)
	private double totalLendedMoney;

	/** output: daily expense which categories match {@link #category} */
	@DAttr(name = "borrowAndLend", type = Type.Collection, optional = false, mutable = false,
			serialisable = false, filter = @Select(clazz = BorrowAndLend.class), derivedFrom = {"actionType"})
	@DAssoc(ascName = "borrow-and-lend-by-action-type-report-has-borrowAndLend", role = "report", ascType = AssocType.One2Many, endType = AssocEndType.One, 
	associate = @Associate(type = BorrowAndLend.class, cardMin = 0,
	cardMax = MetaConstants.CARD_MORE))
	@Output
	private Collection<BorrowAndLend> borrowAndLend;

//	private double totalAddedMoney;
	
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
	  public BorrowAndLendByActionTypeReport (@AttrRef("actionType") String actionType) throws NotPossibleException, DataSourceException {
	    this.id = ++idCounter;
	    
	    this.actionType = actionType;
	    
	    doReportQuery();
	  }

//	  Getter method
	/**
	 * @effects return category
	 */
	public String getActionType() {
		return actionType;
	}

	public double getTotalCollectedDebts() {
		return totalCollectedDebts;
	}

	public double getTotalBorrowedMoney() {
		return totalBorrowedMoney;
	}

	public double getTotalRepayedMoney() {
		return totalRepayedMoney;
	}

	public double getTotalLendedMoney() {
		return totalLendedMoney;
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
	public void setActionType (String actionType) throws NotPossibleException, DataSourceException {
		this.actionType = actionType;

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
	@AttrRef(value = "borrowAndLend")
	public void doReportQuery() throws NotPossibleException, DataSourceException {
		// the query manager instance

		QRM qrm = QRM.getInstance();

		DSMBasic dsm = qrm.getDsm();

		// TODO: to conserve memory cache the query and only change the query parameter
		// value(s)
		Query q = QueryToolKit.createSimpleJoinQuery(dsm, BorrowAndLend.class, ActionType.class,
				BorrowAndLend.T_actionType, 
				ActionType.A_name, 
		        Op.MATCH, 
		        "%"+actionType+"%");

		Map<Oid, BorrowAndLend> result = qrm.getDom().retrieveObjects(BorrowAndLend.class, q);
		
		if (result != null) {
			borrowAndLend = result.values();
			numBorrowAndLend = borrowAndLend.size();
			
			double tempTotalCollectedDebts = 0.0;
			double tempTotalBorrowedMoney = 0.0;
			double tempTotalRepayedMoney = 0.0;
			double tempTotalLendedMoney = 0.0;
			for (BorrowAndLend a : borrowAndLend) {
				if (a.getActionType().getId() == 1) {
					tempTotalCollectedDebts += a.getFinalMoney();
				}
				else if (a.getActionType().getId() == 2) {
					tempTotalBorrowedMoney += a.getFinalMoney();
				}
				else if (a.getActionType().getId() == 3) {
					tempTotalRepayedMoney += a.getFinalMoney();
				}
				else if (a.getActionType().getId() == 4) {
					tempTotalLendedMoney += a.getFinalMoney();
				}
			}
			totalCollectedDebts = tempTotalCollectedDebts;
			totalBorrowedMoney = tempTotalBorrowedMoney;
			totalRepayedMoney = tempTotalRepayedMoney;
			totalLendedMoney = tempTotalLendedMoney;
		} else {
			// no data found: reset output
			resetOutput();
		}
	}

	/**
	 * @effects reset all output attributes to their initial values
	 */
	private void resetOutput() {
		borrowAndLend = null;
		numBorrowAndLend = 0;
	}

	/**
	 * A link-adder method for {@link #dailyExpense}, required for the object form to
	 * function. However, this method is empty because dailyExpense have already be
	 * recorded in the attribute {@link #dailyExpense}.
	 */
	@DOpt(type = DOpt.Type.LinkAdder)
	public boolean addBorrowAndLend (Collection<BorrowAndLend> borrowAndLend) {
		// do nothing
		return false;
	}

	/**
	 * @effects return dailyExpense
	 */
	public Collection<BorrowAndLend> getBorrowAndLend() {
		return borrowAndLend;
	}

	/**
	 * @effects return numDailyExpense
	 */
	public int getNumBorrowAndLend() {
		return numBorrowAndLend;
	}

	/**
	 * @effects return id
	 */
	public int getId() {
		return id;
	}


}
