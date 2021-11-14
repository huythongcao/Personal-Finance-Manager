package vn.com.personalfinance.software;

import domainapp.software.SoftwareFactory;
import domainapp.softwareimpl.DomSoftware;
import vn.com.personalfinance.services.account.Account;
import vn.com.personalfinance.services.account.AccountType;
import vn.com.personalfinance.services.expenditure.model.Category;
import vn.com.personalfinance.services.expenditure.model.DailyExpense;
import vn.com.personalfinance.services.expenditure.model.Expenditure;
import vn.com.personalfinance.services.expenditure.model.ExpenditureSavings;
import vn.com.personalfinance.services.expenditure.model.Income;
import vn.com.personalfinance.services.expenditure.report.DailyExpenseByCategoryReport;
import vn.com.personalfinance.services.savings.Accumulate;
import vn.com.personalfinance.services.savings.Savings;
import vn.com.personalfinance.services.savings.SavingsBook;

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
	      Account.class, 
	      AccountType.class, 
	      Savings.class,
	      SavingsBook.class,
	      Accumulate.class,
	      DailyExpense.class,
	      Expenditure.class,
	      ExpenditureSavings.class,
	      Income.class,
	      Category.class,
	      // report
	      DailyExpenseByCategoryReport.class
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
