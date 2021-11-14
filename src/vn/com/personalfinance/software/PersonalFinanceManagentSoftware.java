package vn.com.personalfinance.software;

import domainapp.software.SoftwareFactory;
import domainapp.softwareimpl.DomSoftware;
import vn.com.personalfinance.services.account.Account;
import vn.com.personalfinance.services.account.AccountType;
import vn.com.personalfinance.services.account.TotalBalance;
import vn.com.personalfinance.services.borrowandlend.model.ActionType;
import vn.com.personalfinance.services.borrowandlend.model.BorrowAndLend;
import vn.com.personalfinance.services.borrowandlend.model.Subjects;
import vn.com.personalfinance.services.borrowandlend.report.BorrowAndLendByActionTypeReport;
import vn.com.personalfinance.services.expenseandincome.model.Category;
import vn.com.personalfinance.services.expenseandincome.model.DailyExpense;
import vn.com.personalfinance.services.expenseandincome.model.DailyIncome;
import vn.com.personalfinance.services.expenseandincome.report.ExpenseAndIncomeByCategoryReport;
import vn.com.personalfinance.services.expenseandincome.report.ExpenseAndIncomeByDateReport;
import vn.com.personalfinance.services.expenseandincome.report.ExpenseAndIncomeByMonthReport;
import vn.com.personalfinance.services.expenseandincome.report.ExpenseAndIncomeByYearReport;
import vn.com.personalfinance.services.savings.AccumulativeSavings;
import vn.com.personalfinance.services.savings.Savings;
import vn.com.personalfinance.services.savings.EconomicalSavings;
import vn.com.personalfinance.services.savingstransaction.SavingsTransaction;

/**
 * @overview 
 *  Encapsulate the basic functions for setting up and running a software given its domain model.  
 *  
 * @author Group 2
 *
 * @version 
 */
public class PersonalFinanceManagentSoftware {
	  // the domain model of software
	  static final Class[] model = {
		  TotalBalance.class,
	      Account.class, 
	      AccountType.class, 
	      Savings.class,
	      EconomicalSavings.class,
	      AccumulativeSavings.class,
	      DailyExpense.class,
	      DailyIncome.class,
	      Category.class,
	      SavingsTransaction.class,
	      BorrowAndLend.class,
	      Subjects.class,
	      ActionType.class,
	      // report
	      ExpenseAndIncomeByCategoryReport.class,
	      ExpenseAndIncomeByDateReport.class,
	      ExpenseAndIncomeByMonthReport.class,
	      ExpenseAndIncomeByYearReport.class,
	      BorrowAndLendByActionTypeReport.class
	  };
	  
	  /**
	   * @effects 
	   *  create and run a UI-based {@link DomSoftware} for a pre-defined model. 
	   */
	  public static void main(String[] args){
	    // 2. create UI software
	    DomSoftware sw = SoftwareFactory.createUIDomSoftware();
	    
	    // 3. run
	    // create in memory configuration
	    System.setProperty("domainapp.setup.SerialiseConfiguration", "false");
	    
	    // 3. run it
	    try {
	      sw.run(model);
	    } catch (Exception e) {
	      // TODO Auto-generated catch block
	      e.printStackTrace();
	    }   
	  }
}
