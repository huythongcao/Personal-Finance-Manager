package vn.com.personalfinance.services.borrowandlend.model;

import java.util.Date; 

import domainapp.basics.exceptions.ConstraintViolationException;
import domainapp.basics.model.meta.AttrRef;
import domainapp.basics.model.meta.DAssoc;
import domainapp.basics.model.meta.DAttr;
import domainapp.basics.model.meta.DClass;
import domainapp.basics.model.meta.DOpt;
import domainapp.basics.model.meta.MetaConstants;
import domainapp.basics.model.meta.DAssoc.AssocEndType;
import domainapp.basics.model.meta.DAssoc.AssocType;
import domainapp.basics.model.meta.DAssoc.Associate;
import domainapp.basics.model.meta.DAttr.Type;
import domainapp.basics.util.Tuple;
import domainapp.basics.util.cache.StateHistory;
import vn.com.personalfinance.services.account.Account;
import vn.com.personalfinance.services.borrowandlend.report.BorrowAndLendByActionTypeReport;

@DClass(schema="personalfinancemanager")
public class BorrowAndLend {
//		static final attribute
		public static final String T_id = "id";
		public static final String T_account = "account";
		public static final String T_name = "name";
		public static final String T_subject = "subject";
		public static final String T_actionType = "actionType";
		public static final String T_money = "money";
		public static final String T_start_date = "startDate";
		public static final String T_period = "period";
		public static final String T_interestedRate = "interestedRate";
		public static final String T_finalMoney = "finalMoney";
		public static final String T_rptBorrowAndLendByType = "rptBorrowAndLendByType";
		
//		attributes
		@DAttr (name = T_id, id = true, type = Type.Integer, auto = true, length = 6, mutable = false, optional = false)
		private int id;
		
//		static variable to keep track of account id
		private static int idCounter = 0;
		
		@DAttr (name = T_account, type = Type.Domain, length = 20, optional = false, cid = true)
		@DAssoc (ascName = "account-has-borrowAndLend", role = "borrowAndLend", ascType = AssocType.One2Many, endType = AssocEndType.Many,
				associate = @Associate(type = Account.class, cardMin = 1, cardMax = 1), dependsOn = true)
		private Account account; 
		
		@DAttr (name = T_name, type = Type.String, length = 20, optional = false)
		private String name;
		
		@DAttr (name = T_subject, type = Type.Domain, length = 30, optional = false)
		@DAssoc (ascName = "subject-has-borrowAndLend", role = "borrowAndLend", ascType = AssocType.One2Many, endType = AssocEndType.Many,
				associate = @Associate(type = Subjects.class, cardMin = 1, cardMax = 1), dependsOn = true)
		private Subjects subject;
		
		@DAttr (name = T_actionType, type = Type.Domain, optional = false)
		@DAssoc (ascName = "borrowAndLend-has-actionType", role = "borrowAndLend", ascType = AssocType.One2Many, endType = AssocEndType.Many, 
		associate = @Associate(type = ActionType.class, cardMin = 1, cardMax = 1), dependsOn = true)
		private ActionType actionType;
		
		@DAttr (name = T_money, type = Type.Double, length = 15, optional = false, min = 0)
		private double money;
		
		@DAttr (name = T_start_date, type = Type.Date, length = 20, optional = false)
		private Date startDate;
		
		@DAttr (name = T_period, type = Type.Integer, length = 20, mutable = true, optional = false)
		private int period;
		
		@DAttr (name = T_interestedRate, type = Type.Double, length = 20, optional = false)
		private double interestedRate;
		
//		derived attribute
		@DAttr (name = T_finalMoney, type = Type.Double, auto = true, length = 20, mutable = false, optional = true,
				serialisable=false, derivedFrom={T_money, T_interestedRate, T_period})
		private double finalMoney;
		
		@DAttr (name = T_rptBorrowAndLendByType, type = Type.Domain, serialisable = false, virtual = true)
		private BorrowAndLendByActionTypeReport rptBorrowAndLendByType;
		
		private StateHistory<String, Object> stateHist;
		
//		Constructor method
		@DOpt(type = DOpt.Type.ObjectFormConstructor)
		@DOpt(type = DOpt.Type.RequiredConstructor)
		public BorrowAndLend (@AttrRef("account") Account account, @AttrRef("name") String name,  @AttrRef("subject") Subjects subject, 
				@AttrRef("type") ActionType type, @AttrRef("money") Double money, @AttrRef("startDate") Date startDate, 
				@AttrRef("period") Integer period, @AttrRef("interestedRate") Double interestedRate) {
			this(null, account, name, subject, type, money, startDate, period, interestedRate);
		}
		
//		a shared constructor that is invoked by other constructors
		@DOpt (type = DOpt.Type.DataSourceConstructor)
		public BorrowAndLend (Integer id, Account account, String name, Subjects subject, ActionType actionType, Double money, Date startDate, Integer period, Double interestedRate) {
		    this.id = nextId(id);
		    
		    this.account = account;
			this.name = name;
			this.subject = subject;
			this.actionType = actionType;
			this.money = money;
			this.startDate = startDate;
			this.period = period;
			this.interestedRate = interestedRate;
			
			stateHist = new StateHistory<>();
			computeFinalMoney();
		}

//		Getter Method
		public int getId() {
			return id;
		}
		
		public Account getAccount() {
			return account;
		}
		
		public String getName() {
			return name;
		}
		
		public Subjects getSubject() {
			return subject;
		}

		public ActionType getActionType() {
			return actionType;
		}

		public double getMoney() {
			return money;
		}
		
		public Date getStartDate() {
			return startDate;
		}
		
		public int getPeriod() {
			return period;
		}
		
		public double getInterestedRate() {
			return interestedRate;
		}
		
		public BorrowAndLendByActionTypeReport getRptBorrowAndLendByType() {
			return rptBorrowAndLendByType;
		}

		//devired attribute
		public double getFinalMoney() {
			return getFinalMoney(false);
		}
		
		public double getFinalMoney(boolean cached) throws IllegalStateException {
			if (cached) {
				Object val = stateHist.get(T_finalMoney);

				if (val == null)
					throw new IllegalStateException("BorrowAndLend.getFinalMoney: cached value is null");
				return (Double) val;
			} else {
				if (finalMoney != 0)
					return finalMoney;
				else
					return 0;
			}
		}

//		Setter Method
		public void setAccount (Account account) {
			this.account = account;
		}
		
		public void setName (String name) {
			this.name = name;
		}
		
		public void setSubject (Subjects subject) {
			this.subject = subject;
		}
		
		public void setActionType(ActionType actionType) {
			this.actionType = actionType;
		}
		
		public void setMoney(double money) {
			setMoney(money,false);
		}
		
		public void setMoney(double money, boolean computeFinalMoney) {
			this.money = money;
			if (computeFinalMoney)
				computeFinalMoney();
		}
		
		public void setStartDate (Date startDate) {
			this.startDate = startDate;
		}
		
		public void setPeriod (int period) {
			setPeriod(period,false);
		}
		
		public void setPeriod(int period, boolean computeFinalMoney) {
			this.period = period;
			if (computeFinalMoney)
				computeFinalMoney();
		}

		public void setInterestedRate(double interestedRate) {
			setInterestedRate(interestedRate,false);
		}
		
		public void setInterestedRate(double interestedRate, boolean computeFinalMoney) {
			this.interestedRate = interestedRate;
			if (computeFinalMoney)
				computeFinalMoney();
		}

		@Override
		public String toString() {
			return "BorrowAndLend [id=" + id + ", name=" + name + ", subject=" + subject + ", actionType=" + actionType + ", money="
					+ money + ", startDate=" + startDate + ", period=" + period + ", interestedRate=" + interestedRate
					+ ", finalMoney=" + finalMoney + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + id;
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
			BorrowAndLend other = (BorrowAndLend) obj;
			if (id != other.id)
				return false;
			return true;
		}

		private static int nextId (Integer currId) {
			if (currId == null) {
				idCounter++;
				return idCounter;
			} else {
				int num;
				num = currId.intValue();
				
				if (num > idCounter) {
					idCounter = num;
				}
				return currId;
			}
		}
		
		// calculate finalMoney 
		@DOpt(type=DOpt.Type.DerivedAttributeUpdater)
		@AttrRef(value=T_finalMoney)
		public void computeFinalMoney() {
			stateHist.put(T_finalMoney, finalMoney);
			
			finalMoney = money + (money * (interestedRate / 100 / (double)period));
		}

		@DOpt(type = DOpt.Type.AutoAttributeValueSynchroniser)
		public static void updateAutoGeneratedValue(DAttr attrib, Tuple derivingValue, Object minVal, Object maxVal)
				throws ConstraintViolationException {
			if (minVal != null && maxVal != null) {
				// check the right attribute
				if (attrib.name().equals("id")) {
					int maxIdVal = (Integer) maxVal;
					if (maxIdVal > idCounter)
						idCounter = maxIdVal;
				}
			}
		}
		
}
