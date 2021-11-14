package vn.com.personalfinance.software;

import domainapp.basics.exceptions.DataSourceException;
import domainapp.basics.exceptions.NotFoundException;
import domainapp.software.SoftwareFactory;
import domainapp.softwareimpl.DomSoftware;
import vn.com.personalfinance.services.account.TotalBalance;
import vn.com.personalfinance.services.borrowandlend.model.ActionType;

/**
 * @overview 
 *
 * @author Nguyen Thuy Duong - Group 2
 *
 * @version 
 */
public class DomMainData {
  
  public static void main(String[] args) {
    DomSoftware sw = SoftwareFactory.createDefaultDomSoftware();
    
    // this should be run subsequent times
    sw.init();
    
    try {
      // register a domain model fragment concerning Student
		Class[] domFrag = { 
			TotalBalance.class,
			ActionType.class
		};
		sw.addClasses(domFrag);
      
      // create some objects
		createTotalBalance(sw);
		createActionType(sw);
    } catch (DataSourceException e) {
      e.printStackTrace();
    }
  }

  /**
   * @effects 
   * 
   */
	private static void createTotalBalance(DomSoftware sw) throws NotFoundException, DataSourceException {
		// create a TotalBalance
		sw.addObject(TotalBalance.class, new TotalBalance());
	}

	private static void createActionType(DomSoftware sw) throws NotFoundException, DataSourceException {
		// create ActionType objects
		sw.addObject(ActionType.class, new ActionType("Collect debts"));
		sw.addObject(ActionType.class, new ActionType("Borrow money"));
		sw.addObject(ActionType.class, new ActionType("Repay money"));
		sw.addObject(ActionType.class, new ActionType("Lend money"));
	}
}
