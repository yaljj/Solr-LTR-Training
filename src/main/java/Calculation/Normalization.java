package Calculation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Common.IO;
import Common.Path;
import DataStructure.FeatureConfig;
import DataStructure.KeywordProductPair;
import DataStructure.Product;
import DataStructure.ProductFeature;
import Main.SampleSetFactory;

/***
 * 特征标准化处理
 * @author AdienHuen
 *
 */
public class Normalization {
	
	public static void getLinerStandardParms(){
//		
		
	}
	public static void linerNorm(Map<Integer,ProductFeature> productInfoDict){	
		double max_basket = 0;
		double min_basket = Integer.MAX_VALUE;
		double max_pay_num = 0;
		double min_pay_num = Integer.MAX_VALUE;
		double max_review = 0;
		double min_review = Integer.MAX_VALUE;
		double max_price = 0;
		double min_price = Integer.MAX_VALUE;
		double max_add_time = 0;
		double min_add_time = Integer.MAX_VALUE;
		for(Integer id:productInfoDict.keySet()){
			productInfoDict.get(id).basket = Math.log(productInfoDict.get(id).basket+1);
			productInfoDict.get(id).pay_num = Math.log(productInfoDict.get(id).pay_num+1);
			productInfoDict.get(id).price = Math.log(productInfoDict.get(id).price+1);
			productInfoDict.get(id).review = Math.log(productInfoDict.get(id).review+1);
		}
		for(Integer id:productInfoDict.keySet()){
			if(max_basket<productInfoDict.get(id).basket){
				max_basket=productInfoDict.get(id).basket;
			}
			if(max_pay_num<productInfoDict.get(id).pay_num){
				max_pay_num=productInfoDict.get(id).pay_num;
			}
			if(max_review<productInfoDict.get(id).review){
				max_review=productInfoDict.get(id).review;
			}
			if(max_price<productInfoDict.get(id).price){
				max_price=productInfoDict.get(id).price;
			}
			if(max_add_time<productInfoDict.get(id).add_time){
				max_add_time=productInfoDict.get(id).add_time;
			}
			if(min_basket>productInfoDict.get(id).basket){
				min_basket=productInfoDict.get(id).basket;
			}
			if(min_pay_num>productInfoDict.get(id).pay_num){
				min_pay_num=productInfoDict.get(id).pay_num;
			}
			if(min_review>productInfoDict.get(id).review){
				min_review=productInfoDict.get(id).review;
			}
			if(min_price>productInfoDict.get(id).price){
				min_price=productInfoDict.get(id).price;
			}
			if(min_add_time>productInfoDict.get(id).add_time){
				min_add_time=productInfoDict.get(id).add_time;
			}
		}
		
		for(Integer id:productInfoDict.keySet()){
			productInfoDict.get(id).basket = (productInfoDict.get(id).basket-min_basket)/(max_basket-min_basket);
			productInfoDict.get(id).price = (productInfoDict.get(id).price - min_price)/(max_price-min_price);
			productInfoDict.get(id).pay_num = (productInfoDict.get(id).pay_num - min_pay_num)/(max_pay_num-min_pay_num);
			productInfoDict.get(id).review = (productInfoDict.get(id).review - min_review)/(max_review-min_review);
			productInfoDict.get(id).add_time = (productInfoDict.get(id).add_time - min_add_time)/(max_add_time-min_add_time);
		}
		
	}
	
	/**
	 * 计算各特征的最少值和最大值，用于修改solr-ltr的feature配置文件
	 */
	public static void getlinerNormParms(){
		List<KeywordProductPair> pairList = SampleSetFactory.readPairRelevancy();
		HashMap<Integer,Product> productDict = IO.readProductFeatureDict();
		FeatureConfig conf = IO.readFeaturesCongfig();
		Map<String,Double> maxDict = new HashMap<String,Double>();
		Map<String,Double> minDict = new HashMap<String,Double>();
		BM25 bm25 = new BM25(productDict);
		for(String feature:conf.rank_feature){
			maxDict.put(feature, Double.MIN_VALUE);
			minDict.put(feature, Double.MAX_VALUE);
		}
		for(KeywordProductPair pair:pairList){
			try{
				double value = bm25.getValue(pair.keyword,productDict.get(pair.productID).strProp.get("product_name"));
				System.out.println(value);
				if(value>maxDict.get("BM25")){
					maxDict.put("BM25", value);
				}
				if(value<minDict.get("BM25")){
					minDict.put("BM25", value);
				}
			}
			catch(Exception e){
				;
			}
		}
		conf.rank_feature.remove(0);
		
	   for(Integer id:productDict.keySet()){
		   for(String feature:conf.rank_feature){
			   try{

				   if(productDict.get(id).rank_feature.get(feature)>maxDict.get(feature)){
					   maxDict.put(feature, productDict.get(id).rank_feature.get(feature));
				   }
				   if(productDict.get(id).rank_feature.get(feature)<minDict.get(feature)){
					   minDict.put(feature, productDict.get(id).rank_feature.get(feature));
				   }
			   }
			   catch(Exception e){
				   ;
			   }
		   }	
	   }
	   for(String feature:maxDict.keySet()){
		   System.out.println(feature+" "+"max:"+maxDict.get(feature)+" min:"+minDict.get(feature));
	   }
	   
	}
	 
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		getlinerNormParms();
	}

}
