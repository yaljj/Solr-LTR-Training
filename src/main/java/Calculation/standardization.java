package Calculation;

import java.util.Map;

import DataStructure.ProductFeature;

/***
 * 特征标准化处理
 * @author AdienHuen
 *
 */
public class standardization {
	
	public static void standard(Map<Integer,ProductFeature> productInfoDict){	
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
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println();
	}

}
