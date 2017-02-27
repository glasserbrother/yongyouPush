package com.zgcfo.ezg.app;

import java.io.Serializable;
import java.util.Date;

import com.zgcfo.ezg.app.commond.YongYouCommondRPOP;
import com.zgcfo.ezg.util.RedisUtil;

public class YYThreadPoolRPOP  implements Runnable, Serializable {
	
	private static final long serialVersionUID = 0;
	private static int consumeTaskSleepTime = 2000;
	
	private YongYouCommondRPOP yyCommondRPOP;




	public YYThreadPoolRPOP(YongYouCommondRPOP yyCommondRPOP) {
		super();
		this.yyCommondRPOP = yyCommondRPOP;
	}




	@Override
	public void run() {
		
		
		
		
		try {
			byte[] b = null;
			while ( (b= yyCommondRPOP.getRPOPByte()) != null){
				yyCommondRPOP.executeCommond(b);
				System.out.println(new Date()+"  rpop-----  ["+new String(RedisUtil.getCommondRedisKey(yyCommondRPOP.getCommond()))+"]  "+Thread.currentThread().getName()+"执行完成");
				Thread.sleep(consumeTaskSleepTime);
			}
			do{
				b= yyCommondRPOP.getRPOPByte();
				if (b != null){
					yyCommondRPOP.executeCommond(b);
					System.out.println(new Date()+"  rpop-----  ["+new String(RedisUtil.getCommondRedisKey(yyCommondRPOP.getCommond()))+"]  "+Thread.currentThread().getName()+"执行完成");
					//System.out.println("rpop----  "+Thread.currentThread().getName()+"执行完成");
				}
				Thread.sleep(consumeTaskSleepTime);
			}while ( (b= yyCommondRPOP.getRPOPByte()) != null);
			
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
