package Common;

import java.io.RandomAccessFile;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Calculation.ValueCalculation;
import DataStructure.KeywordProductPair;
import DataStructure.ProductFeature;


public class DBhelper {
		/*
		 * DBhelp
		 */
	 	Connection conn;//Declare a Connection object  
	    private Statement st; 
	    private String driver="com.mysql.jdbc.Driver";// the MySQL driver  
	    
	    /*
	     * bg库连接信息
	     */
	    private String bgWork_url="jdbc:mysql://192.168.6.238:13304/banggood_work";// URL points to destination database to manipulate  
	    private String bgWord_user="beta_lanread";//user name for the specified database  
	    private String bgWord_pwd="ytUs^1344sszwh67@#";//the corresponding password
	    
	    /*
	     * dc库连接信息
	     */
	    private String dc_url="jdbc:mysql://192.168.6.238:13401/datacube";// URL points to destination database to manipulate  
	    private String dc_user="xuanzhenyu";//user name for the specified database  
	    private String dc_pwd="X7g;al#jhlZV45qKfsd";//the corresponding password
	  	    
	    /*
	     * sql
	     */
	    private String keyword_sql = "SELECT rel_keyword,pro_uv,bas_uv,pay_num FROM (SELECT rel_keyword,add_time,SUM(search_pv) AS pro_uv,SUM(basket_uv) AS bas_uv, SUM(pay_number) AS pay_num FROM (SELECT * FROM datacube.`dc_collect_days_rel_keyword` WHERE add_time>%time% AND page_type='search') AS part GROUP BY rel_keyword ) AS newTable WHERE pay_num>1  ORDER BY pay_num DESC";
	    private String product_complex_sql ="SELECT SUM(basket),MAX(review),SUM(pay_number) FROM datacube.`dc_collect_days_product_complex` WHERE add_time>%time% AND products_id=%id%";
	    private String product_complex_sql_all = "SELECT products_id,SUM(basket),MAX(review),SUM(pay_number) FROM datacube.`dc_collect_days_product_complex` WHERE add_time>%time% and products_id<100000000 GROUP BY products_id";
	    private String product_prop_sql = "SELECT products_name,products_price,categories_id,brand_id,add_time FROM datacube.`dc_set_product` WHERE products_id=%id% and domain = 1";
	    private String pair_sql = "SELECT rel_keyword,products_id,SUM(products_uv) AS pro_uv,SUM(basket_uv) AS bas_uv,SUM(pay_number) AS pay_num FROM dc_collect_days_rel_keyword_products WHERE add_time>=%time%  AND rel_keyword= '%keyword%' GROUP BY rel_keyword,products_id  ORDER BY pay_num DESC LIMIT 50";
	    private String product_prop_sql_all = "SELECT products_id,products_name,products_price,categories_id,brand_id,modify_time FROM datacube.`dc_set_product` WHERE domain = 1";
	    private String count_bg_product = "SELECT COUNT(*) FROM datacube.`dc_set_product` WHERE domain = 1";
	    
    	static private long time = System.currentTimeMillis()/1000;
    	static private long day = 86200;
    	static private long month = 5184000; //两个月时间
    	
    	
    	/***
    	 * 连接dc库
    	 */
	    public void connect2dc(){
	    	this.getConnection(this.dc_url,this.dc_user,this.dc_pwd);
	    	this.createStatement();
	    }
		
	    /***
	     * 连接相关数据库
	     * @param url 地址
	     * @param user 用户名
	     * @param pwd 密码
	     */
	    private void getConnection(String url,String user,String pwd){
	        try {  
	            Class.forName(driver);// add MySQL driver  
	            System.out.println("Database driver is successfully added");  
	        } catch (ClassNotFoundException e) {  
	            // TODO Auto-generated catch block  
	            e.printStackTrace();  
	        }  
	        try {  
	        	this.conn=DriverManager.getConnection(url,user,pwd);//create a connection object  
	            System.out.println("Database connection is successful");  
	        } catch (SQLException e) {  
	            // TODO Auto-generated catch block  
	            e.printStackTrace();  
	        } 
	    } 
	    
	    
	    private void createStatement() {
	    	try{
		    	this.st = conn.createStatement(); 
	    	}
	    	catch(Exception e) {
	    		e.printStackTrace();
	    	}
	    }
	    
	    public List<String> getHitKeyword(){
	    	List<String> keywords = new ArrayList<String>();
	    	System.out.println("---update Keywords---");
	    	try{
		    	IO.writeTxtFile("", Path.keyword_txt_path);
	    	}
	    	catch(Exception e){
	    		e.printStackTrace();
	    	}
	    	try{
	    		long timeAgo = this.time-7*this.day;
	    		String sql = keyword_sql.replaceFirst("%time%",String.valueOf(timeAgo));
		    	ResultSet  rs = this.st.executeQuery(sql); 
		    	while (rs.next()){
		    		String keyword = rs.getString(1);
//	                int pro_uv = rs.getInt(2);
//	                int bas_uv = rs.getInt(3);
//	                int pay_num = rs.getInt(4);
//	                System.out.println(keyword+" "+pro_uv+" "+bas_uv+" "+pay_num);
		    		keywords.add(keyword);
		    		IO.append(Path.keyword_txt_path, keyword+"\n", Path.code);
		    		System.out.println(keyword);
		    	}
		    	rs.close();
		    	System.out.println("---update Keyword done---");
	    	}
	    	catch(Exception e) {
	    		e.printStackTrace();
	    	}
	    	return keywords;
	    }
	    
	    public void getKeywordProductPair(List<String> keywords){
	    	System.out.println("---update pair---");
	    	try{
		    	IO.writeTxtFile("", Path.keyword_product_pair_txt_path);
	    	}
	    	catch(Exception e){
	    		e.printStackTrace();
	    	}
	    	for(String keyword:keywords){

	    		try{
		    		String pair_sql = this.pair_sql.replaceFirst("%keyword%", keyword).replaceFirst("%time%", String.valueOf(this.time-3*this.day));
		    		
		    		ResultSet  rs = this.st.executeQuery(pair_sql);
		    		while (rs.next()){
			    		String word = rs.getString(1);
			    		int proID = rs.getInt(2);
		                int pro_uv = rs.getInt(3);
		                int bas_uv = rs.getInt(4);
		                int pay_num = rs.getInt(5);
//		                double relvancy  = ValueCalculation.getPairRelevancy(pro_uv,bas_uv,pay_num);
		                String row = word+"`"+proID+"`"+pro_uv+"`"+bas_uv+"`"+pay_num+"`"+"\n";
		                System.out.println(row);
		                IO.append(Path.keyword_product_pair_txt_path,row,Path.code);
		    		}
		    		rs.close();
	    		}
	    		catch(Exception e){
	    			e.printStackTrace();
	    		}
	    	}
	    	System.out.println("---update pair done---");
	    }
	    
	    public void getProductPropAll(){
	    	System.out.println("---更新原始特征数据Product_prop.txt---");
	    	try{
		    	IO.writeTxtFile("", Path.product_prop_txt);
	    	}
	    	catch(Exception e){
	    		e.printStackTrace();
	    	}
			int num = this.getProductNum();
	    	for(int i=0;i<num+20000;i+=20000){
		    	try{
		    		String sql = this.product_prop_sql_all+" LIMIT "+i+",20000"; //+" LIMIT "+i+",100"
		    		System.out.println(sql);
		    		ResultSet rs = this.st.executeQuery(sql);
		    		while(rs.next()){
		    			int products_id = rs.getInt(1);
		    			String products_name = rs.getString(2);
		    			float products_price = rs.getFloat(3);
		    			int categories_id = rs.getInt(4);
		    			int brand_id = rs.getInt(5);
		    			int add_time = rs.getInt(6);
		    			String row = products_id+"``"+products_name+"``"+products_price+"``"+categories_id+"``"+brand_id+"``"+add_time+"\n";
		    			System.out.println(row);
		    			IO.append(Path.product_prop_txt, row, Path.code);
		    		}
		    		rs.close();
		    	}
		    	catch(Exception e){
		    		e.printStackTrace();
		    	}
	    	}
	    	System.out.println("---原始特征数据Product_prop.跟新完毕---");
	    }
	    
	    public int getProductNum(){
	    	int num=0;
	    	try{
	    		ResultSet rs = this.st.executeQuery(this.count_bg_product);
	    		while(rs.next()){
	    			num = rs.getInt(1);
	    		}
	    	}
	    	catch(Exception e){
	    		e.printStackTrace();
	    	}
	    	return num;
	    }
	    
	    public void getProductComplex(Map<Integer,ProductFeature> productInfoDict){

	    	for(Integer id:productInfoDict.keySet()){
	    		try{
	    			String complex_sql = this.product_complex_sql.replaceFirst("%id%", String.valueOf(id)).replaceFirst("%time%", String.valueOf(this.time-this.month));
	    			ResultSet rs = this.st.executeQuery(complex_sql);
	    			while (rs.next()){
			    		float avgBasket = rs.getFloat(1);
		                int review = rs.getInt(2);
		                float avgPayNum =  rs.getFloat(3);
		                productInfoDict.get(id).basket =avgBasket;
		                productInfoDict.get(id).review = review;
		                productInfoDict.get(id).pay_num = avgPayNum;
		                System.out.println(avgBasket+" "+review+" "+avgPayNum);
	    				System.out.println(id+" getProductComplex Finish");
		    		}
	    			rs.close();
	    		}
	    		catch(Exception e){
	    			e.printStackTrace();
	    		}
	    	}

	    }
	    
	    public void getProductComplexAll(){
	    	System.out.println("---更新原始特征数据Product_complex.txt---");
	    	try{
		    	IO.writeTxtFile("", Path.product_complex_txt);
	    	}
	    	catch(Exception e){
	    		e.printStackTrace();
	    	}
	    	try{
	    		String sql = this.product_complex_sql_all.replaceFirst("%time%", String.valueOf(this.time-this.month));
	    		ResultSet rs = this.st.executeQuery(sql);
	    		while (rs.next()){
	    			long product_id = rs.getInt(1);
		    		float avgBasket = rs.getFloat(2);
	                int review = rs.getInt(3);
	                float avgPayNum =  rs.getFloat(4);
	                String row = product_id+"``"+avgBasket+"``"+review+"``"+avgPayNum+"\n";
	                System.out.println(row);
	                IO.append(Path.product_complex_txt,row,Path.code);
	    		}
	    		rs.close();
	    	}
	    	catch(Exception e){
	    		e.printStackTrace();
	    	}
	    	System.out.println("---原始特征数据Product_complex.跟新完毕---");
	    }
	    
	    public void getProductProp(Map<Integer,ProductFeature> productInfoDict){
	    	System.out.println("---更新原始特征数据Product_prop.txt---");
	    	try{
		    	IO.writeTxtFile("", Path.product_prop_txt);
	    	}
	    	catch(Exception e){
	    		e.printStackTrace();
	    	}

	    	for(Integer id:productInfoDict.keySet()){
	    		try{
	    			String sql = this.product_prop_sql.replaceFirst("%id%", String.valueOf(id));
	    			ResultSet rs = this.st.executeQuery(sql);
	    			while (rs.next()){
	    				productInfoDict.get(id).product = rs.getString(1);
	    				productInfoDict.get(id).price = rs.getFloat(2);
	    				productInfoDict.get(id).catID = rs.getInt(3);
	    				productInfoDict.get(id).brandID = rs.getInt(4);
	    				productInfoDict.get(id).add_time = rs.getFloat(5);
//		    			String products_name = rs.getString(1);
//		    			float products_price = rs.getFloat(2);
//		    			int categories_id = rs.getInt(3);
//		    			int brand_id = rs.getInt(4);
//		    			int add_time = rs.getInt(5);
	    				System.out.println(id+" getProductProp Finish");
		    		}
	    			rs.close();
	    		}
	    		catch(Exception e){
	    			e.printStackTrace();
	    		}
	    	}
	    	System.out.println("---原始特征数据Product_prop.跟新完毕---");
	    }
	    
	    /*
	     * 更新原始数据
	     */
	    public void updateOriginalData(){
			DBhelper db = new DBhelper();
			db.connect2dc();
			List<String> keywords = db.getHitKeyword();
			db.getKeywordProductPair(keywords);
			db.getProductPropAll();
			db.getProductComplexAll();
			
	    }
	    
		public static void main(String[] args) throws InterruptedException {
			DBhelper db = new DBhelper();
			db.connect2dc();
			db.updateOriginalData();
		}
}
