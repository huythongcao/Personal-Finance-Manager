package vn.com.personalfinance.software;

import domainapp.software.SoftwareFactory;
import domainapp.softwareimpl.DomSoftware;
import vn.com.personalfinance.model.Account;
import vn.com.personalfinance.model.AccountType;

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
	  private static final Class[] model = {
	      Account.class, 
	      AccountType.class, 
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
