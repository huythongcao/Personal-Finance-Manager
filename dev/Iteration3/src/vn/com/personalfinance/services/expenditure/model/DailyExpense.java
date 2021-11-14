package vn.com.personalfinance.services.expenditure.model;
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
import vn.com.personalfinance.services.expenditure.report.DailyExpenseByCategoryReport;
import vn.com.personalfinance.services.expenditure.report.DailyExpenseByDateReport;

/**
 * Represents daily expense. The account ID is auto-incremented.
 * 
 * @author Nguyen Quynh Nga - Group 2
 * @version 1.0
 */
@DClass(schema="personalfinancemanagement")
public abstract class DailyExpense {
	
		public static final String D_id = "id";
		public static final String D_amount = "amount";
		public static final String D_date = "date";
		public static final String D_category = "category";
		public static final String D_account = "account";
		public static final String D_description = "description";
		public static final String D_rptDailyExpenseByCategory = "rptDailyExpenseByCategory";
		public static final String D_rptDailyExpenseByDate = "rptDailyExpenseByDate";

		// attributes of daily expense
		@DAttr(name = D_id, id = true, type = Type.String, auto = true, length = 6, mutable = false, optional = false)
		private String id;
		// static variable to keep track of account id
		public static int idCounter = 0;
		
		@DAttr(name = D_amount, type = Type.Double, length = 15, optional = false)
		private double amount;
			
		@DAttr(name = D_date, type = Type.Date, length = 15, optional = false) 
		private Date date;
		
		@DAttr(name =D_category, type = Type.Domain, optional = false)
		@DAssoc(ascName = "category-has-dailyExpense", role = "category",
		ascType = AssocType.One2Many, endType = AssocEndType.Many,
		associate = @Associate(type = Category.class, cardMin = 1, cardMax = 1),
		dependsOn=true)
		private Category category;
		
		@DAttr(name =D_account, type = Type.Domain,  optional = false) 
		@DAssoc(ascName = "account-has-dailyExpense", role = "account",
		ascType = AssocType.One2Many, endType = AssocEndType.Many,
		associate = @Associate(type = Account.class, cardMin = 1, cardMax = 1),
		dependsOn=true)
		private Account account;
		
		@DAttr(name = D_description, type = Type.String, length = 30)
		private String description;
		
		@DAttr(name = D_rptDailyExpenseByCategory, type = Type.Domain,
		serialisable = false, virtual = true)
		private DailyExpenseByCategoryReport rptDailyExpenseByCategory;
		
		@DAttr(name = D_rptDailyExpenseByDate, type = Type.Domain,
		serialisable = false, virtual = true)
		private DailyExpenseByDateReport rptDailyExpenseByDate;

		//constructor methods
		@DOpt(type=DOpt.Type.ObjectFormConstructor)
		@DOpt(type=DOpt.Type.RequiredConstructor)
		protected DailyExpense(@AttrRef("amount") Double amount,
				@AttrRef("date") Date date,
				@AttrRef("category") Category category,
				@AttrRef("account") Account account,
				@AttrRef("description") String description
				) {
			this(null, amount, date, category, account, description);
		}
			
		// a shared constructor that is invoked by other constructors
		@DOpt(type=DOpt.Type.DataSourceConstructor)
		protected DailyExpense (String id, Double amount, Date date, Category category,Account account, String description)
			 {
			// generate an id
			this.id = nextID(id);
			    
			// assign other values
			this.amount = amount;
			this.date = date;
			this.category = category;
			this.account = account;
			this.description = description;
			
			computeNewBalance();
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
		
		public DailyExpenseByCategoryReport getRptDailyExpenseByCategory() {
			return rptDailyExpenseByCategory;
		}
		
		public DailyExpenseByDateReport getRptDailyExpenseByDate() {
			return rptDailyExpenseByDate;
		}

		public abstract String nextID(String currID) ;
		public abstract void computeNewBalance();
		@Override
		public String toString() {
			return toString(true);
		}
		
		/**
		 * @effects returns <code>Savings(id,code,amount,name,purpose,startDate,monthlyDuration)</code>.
		 */
		public String toString(boolean full) {
			if (full)
				return "DailyExpense(" + id + ","  + amount + "," + date + ","   + category + "," +  account +")";
			else
				return "DailyExpense(" + id + ")";
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
			DailyExpense other = (DailyExpense) obj;
			
			
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
			          
			         
			          if(maxIDNum>idCounter) {
			        	  idCounter= maxIDNum;
			          }
			          
			          
			          
			        } catch (RuntimeException e) {
			          throw new ConstraintViolationException(
			              ConstraintViolationException.Code.INVALID_VALUE, e, new Object[] {maxID});
			        }
				}
		
}
		}
}
		

