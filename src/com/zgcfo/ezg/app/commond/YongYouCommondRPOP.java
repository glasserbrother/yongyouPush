package com.zgcfo.ezg.app.commond;

import java.util.List;

import com.zgcfo.ezg.app.constant.YongYouConstants;
import com.zgcfo.ezg.app.data.YongYouDo;
import com.zgcfo.ezg.entity.yongyou.Balance;
import com.zgcfo.ezg.entity.yongyou.BalanceReport;
import com.zgcfo.ezg.entity.yongyou.CashReport;
import com.zgcfo.ezg.entity.yongyou.DetailAccountReport;
import com.zgcfo.ezg.entity.yongyou.IncomeQuarterReport;
import com.zgcfo.ezg.entity.yongyou.IncomeReport;
import com.zgcfo.ezg.entity.yongyou.Subject;
import com.zgcfo.ezg.entity.yongyou.TaxReport;
import com.zgcfo.ezg.redis.JedisUtil;
import com.zgcfo.ezg.redis.ObjectUtil;
import com.zgcfo.ezg.util.RedisUtil;

public class YongYouCommondRPOP {
	
	private YongYouDo yyDo;
	private int commond;
	
	
	
	public YongYouDo getYyDo() {
		return yyDo;
	}

	public void setYyDo(YongYouDo yyDo) {
		this.yyDo = yyDo;
	}

	public int getCommond() {
		return commond;
	}

	public void setCommond(int commond) {
		this.commond = commond;
	}

	public YongYouCommondRPOP(YongYouDo yyDo, int commond) {
		super();
		this.yyDo = yyDo;
		this.commond = commond;
	}
	
	public void doCommand(Object obj, int paraCommond) throws Exception {
		
		if (obj != null) {
			switch(paraCommond){
			case YongYouConstants.CASH_REPORT_INT : 
				//开始导入现金流量表
				List<CashReport> cashObjs = (List<CashReport>) obj;
				if (cashObjs!=null && cashObjs.size()>0){
					yyDo.doCashReport(cashObjs);
				}
				
				break;
			case YongYouConstants.INCOME_REPORT_INT : 
				//开始导入利润表
				List<IncomeReport> incomeReports = (List<IncomeReport>) obj;
				if (incomeReports!=null && incomeReports.size() >0 ){
					yyDo.doIncomeReport(incomeReports);
				}
				
				break;
			case YongYouConstants.BALANCE_REPORT_INT : 
				//开始导入资产负债表
				List<BalanceReport> balanceReports = (List<BalanceReport>) obj;
				if (balanceReports!=null && balanceReports.size() >0){
					yyDo.doBalanceReport(balanceReports);
				}
				
				break;
			case YongYouConstants.SUBJECT_INT : 
				//开始导入科目
				List<Subject> subjects = (List<Subject>) obj;
				if (subjects !=null && subjects.size() >0 ){
					yyDo.doSubject(subjects);
				}
				
				break;
			case YongYouConstants.BALANCE_INT : 
				//开始导入余额
				List<Balance> balances = (List<Balance>) obj;
				if (balances !=null && balances.size() >0){
					yyDo.doBalance(balances);
				}
				
				break;
			case YongYouConstants.INCOME_QUARTER_REPORT_INT :
				//开始导入利润季报表
				List<IncomeQuarterReport> incomeQuarters = (List<IncomeQuarterReport>) obj;
				if (incomeQuarters != null &&incomeQuarters.size()> 0){
					yyDo.doIncomeQuarterReport(incomeQuarters);
				}
				
				break;
			case YongYouConstants.TAX_REPORT_INT : 
				//开始导入纳税统计表
				List<TaxReport> taxReports = (List<TaxReport>) obj;
				yyDo.doTaxReport(taxReports);
				break;
			case YongYouConstants.DETAIL_ACCOUNT_REPORT_INT : 
				//开始导入现金流量表
				List<List<DetailAccountReport>> listdetailAccounts = (List<List<DetailAccountReport>>) obj;
			
				if (listdetailAccounts != null && listdetailAccounts.size() > 0) {
					for (int i = 0; i < listdetailAccounts.size(); i++) {
						List<DetailAccountReport> detailAccounts = listdetailAccounts
								.get(i);
						yyDo.doDetailAccountReport(detailAccounts);
					}
				}
				
				break;
			}
			
		}
		
	}
	
	public byte[]  getRPOPByte(){
		return getRPOPByte(commond);
	}
	
	
	public byte[]  getRPOPByte(int paraCommond){
		byte[] redisKey = RedisUtil.getCommondRedisKey(paraCommond);
		byte[] bytes;
		
		// 开始导入现金流量表
		bytes = JedisUtil.rpop(redisKey);
		return bytes;
	}
	
	public void executeCommond( byte[] bytes){
		executeCommond(commond, bytes);
	}
	
	public void executeCommond(int paraCommond, byte[] bytes){

		Object obj=null;
		
		try {
			obj = ObjectUtil.bytesToObject(bytes);	
			doCommand(obj, paraCommond);
		} catch (Exception e7) {
			e7.printStackTrace();
		}
		
		
	}

}
