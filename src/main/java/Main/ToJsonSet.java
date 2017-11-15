package Main;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

import Calculation.standardization;
import Common.IO;
import Common.Path;
import DataStructure.ProductFeature;
import DataStructure.productProp;
import DataStructure.productComplex;

/***
 * 将product以及相关属性转换为json格式，用于上传至solr
 * @author AdienHuen
 *
 */
public class ToJsonSet {
	private static JSONArray products2Json(){
		// TODO Auto-generated method stub
		Map<Integer,productProp> productPropDict = SampleSetFactory.readProductProp();
		Map<Integer,productComplex> productComplexDict = SampleSetFactory.readProductComplex();
		Map<Integer,ProductFeature> productInfoDict = new HashMap<Integer,ProductFeature>();
		for(Integer id:productPropDict.keySet()){

			try{
				productInfoDict.put(id, new ProductFeature(productComplexDict.get(id),productPropDict.get(id)));
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		
		standardization.standard(productInfoDict);
		JSONArray productsJson = new JSONArray();
		
		for(Integer id:productInfoDict.keySet()){
			JSONObject jsonobj = new JSONObject();
			jsonobj.put("id", id);
			jsonobj.put("product_name", productInfoDict.get(id).product);
			jsonobj.put("price_weight", productInfoDict.get(id).price);
			jsonobj.put("add_time_weight", productInfoDict.get(id).add_time);
			jsonobj.put("review_weight", productInfoDict.get(id).review);
			jsonobj.put("pay_num_weight", productInfoDict.get(id).review);
			jsonobj.put("basket_weight", productInfoDict.get(id).basket);
//			System.out.println(jsonobj);
			productsJson.put(jsonobj);
		}

		return productsJson;
	 }
	
	/*
	 * 将porducts相关特征，以json的形式，保存到本地
	 */
	static public void storeProductsJson(){
		JSONArray productsJson = products2Json();
		try{
			IO.writeTxtFile(productsJson.toString(), Path.products_json);	
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void main(String[] args) throws Exception {
		storeProductsJson();
	}
}
