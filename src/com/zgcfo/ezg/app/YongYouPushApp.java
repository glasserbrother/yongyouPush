package com.zgcfo.ezg.app;

import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.zgcfo.ezg.app.commond.YongYouCommondGet;
import com.zgcfo.ezg.app.commond.YongYouCommondRPOP;
import com.zgcfo.ezg.app.data.YongYouDataGet;
import com.zgcfo.ezg.app.data.YongYouDo;
import com.zgcfo.ezg.entity.yongyou.YongYouDataEntity;
import com.zgcfo.ezg.service.YongYouDoData;
import com.zgcfo.ezg.util.AppMySQLConn;
import com.zgcfo.ezg.util.MyFormat;

public class YongYouPushApp {
	
	private static int produceTaskSleepTime = 90*1000;//队列等待90秒空闲
	private static int produceTaskSleepSecond = 90;//队列等待90秒空闲
	private static int produceTaskMinNumber = 4;//最小8个线程
	private static int produceTaskMaxNumber = 6;//最大10个线程
	private static int queueDeep = 2;//允许队列深度2(等待)
	
	private synchronized int getQueueSize(Queue queue) {
		return queue.size();
	}
	
	public static void main(String[] args) {
		YongYouPushApp yongYouApp = new YongYouPushApp();
		String currMonth = "";
		String accountcode = "";
		String bookid = "";
		String yongyouid = "";
		if (args.length > 0 ){
			currMonth = args[0];
		}
		if (args.length > 1 ){
			accountcode = args[1];
		}
		if (args.length > 2 ){
			bookid = args[2];
		}
		if (args.length > 3 ){
			yongyouid = args[3];
		}
		
		if (MyFormat.isStrNull(currMonth)){
			currMonth = MyFormat.getPrevMonth();
		}
		
		
		System.out.println("currMonth:"+currMonth);
		
		
		YongYouDoData app = new YongYouDoData();
		List<YongYouDataEntity> list = app.getYongYouDataList(accountcode, bookid, yongyouid);
		if (null != list && list.size() > 0){
			System.out.println("list: "+list.size());
			YongYouDataEntity yyData;
			YongYouDataGet yyGet;
						
			ThreadPoolExecutor threadPoolGet = new ThreadPoolExecutor(produceTaskMinNumber, produceTaskMaxNumber, produceTaskSleepSecond,
					TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(queueDeep),
					new ThreadPoolExecutor.DiscardOldestPolicy());
			
			for (int i = 0; (i < list.size() ); i++) {
				try {
					
					// 产生一个任务，并将其加入到线程池
					System.out.println(new Date()+"------------------------执行第"+i+"个");
					yyData = list.get(i);
					yyData.setCurrMonth(currMonth);
					System.out.println(yyData);
					
					yyGet = new YongYouDataGet(yyData.getLoginName(),yyData.getPwd()
							,yyData.getBookId(),yyData.getYongyouId(), yyData.getCurrMonth());
					
					threadPoolGet.execute(new YYThreadPoolGetAndPush(yyGet));
					
					while ( yongYouApp.getQueueSize(threadPoolGet.getQueue()) >= queueDeep) {
						System.out.println("队列已满，等90秒再添加任务");
						try {
							Thread.sleep(produceTaskSleepTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				System.out.println(new Date()+"------------------------完成第"+i+"个");
			}
			
			
			System.out.println(new Date() +"全部完成--恭喜");
			threadPoolGet.shutdown();
		}else{
			System.out.println("list: null");
		}
		
		
	}
	

}
