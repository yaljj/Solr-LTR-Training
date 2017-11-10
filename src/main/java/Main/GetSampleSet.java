package Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import Calculation.BM25;
import Calculation.ValueCalculation;
import Calculation.standardization;
import Common.IO;
import Common.Path;
import DataStructure.KeywordProductPair;
import DataStructure.ProductFeature;
import DataStructure.Prop;
import DataStructure.productComplex;

/***
 * 将原始数据库数据转化为ranlib相关的样本集(训练集,测试计,验证集)
 * @author AdienHuen
 *
 */
public class GetSampleSet {
	
	/***
	 * 读取关键词和对应商品和关键词对商品的转换率因子，计算作为衡量搜索结果的指标
	 * @return
	 */
	private static List<KeywordProductPair> readPairRelevancy(){ 
		List<String> rows = IO.readTxtFile(Path.keyword_product_pair_txt_path, Path.code);
		List<KeywordProductPair> pairList= new ArrayList<KeywordProductPair>();
		for(String row:rows){
			String[] term =  row.replace("\n","").split("`");
			double pro_uv = Double.valueOf(term[2]);
			double bas_uv =  Double.valueOf(term[3]);
			double pay_num = Double.valueOf(term[4]);
			double relevancy = ValueCalculation.getPairRelevancy(pro_uv, bas_uv, pay_num);
				KeywordProductPair pair = new KeywordProductPair(term[0],Integer.valueOf(term[1]),relevancy);
				pairList.add(pair);
			}
//			System.out.println(pair.keyword+" "+pair.productID+" "+pair.relevancy);
		return pairList;
	}
	
//	public static Map<Integer,String> readProductProp(){
//		List<String>IO.readTxtFile(Path.product_prop_txt,Path.code);
//	}
//	
	/***
	 * 读取商品complex特征
	 * @return productComplexDict，{商品:complex特征}字典
	 */
	public static Map<Integer,productComplex> readProductComplex(){
		List<String> rows = IO.readTxtFile(Path.product_complex_txt, Path.code);
		Map<Integer,productComplex> productComplexDict = new HashMap<Integer,productComplex>();
		for(String row:rows){
			String[] term = row.split("``");
			productComplex complex = new productComplex(Double.valueOf(term[1]),Double.valueOf(term[2]),Double.valueOf(term[3]));
			productComplexDict.put(Integer.valueOf(term[0]), complex);
		}
		return productComplexDict;
	}
	
	/***
	 * 读取商品prop特征
	 * @return productComplexDict，{商品:prop特征}字典
	 */
	public static Map<Integer,Prop> readProductProp(){
		List<String> rows = IO.readTxtFile(Path.product_prop_txt, Path.code);
		Map<Integer,Prop> productPropDict = new HashMap<Integer,Prop>();
		for(String row:rows){
			String[] term = row.split("``");
			Prop prop = new Prop(term[1].toLowerCase(),Double.valueOf(term[2]),Integer.valueOf(term[3]),Integer.valueOf(term[3]),Double.valueOf(term[5]));
			productPropDict.put(Integer.valueOf(term[0]), prop);
		}
		return productPropDict;
	}
	
	/***
	 * 获取关键词和对应标号（ranklib训练时需要标号）
	 * @return keywordID,{keyword:index}
	 */
	public static Map<String,Integer> getKeywordID(){
		Map<String,Integer> keywordID = new HashMap<String,Integer>();
		List<String> rows = IO.readTxtFile(Path.keyword_txt_path,Path.code);
		int i=0;
		for(String row:rows){
			row = row.replace("\n", "").toLowerCase();
			keywordID.put(row, i);
			i++;
		}
		return keywordID;
	}
	
	/***
	 * 获取相关样本集，并保存至data/SampleSet
	 * @throws Exception
	 */
	public static void get() throws Exception{
		List<KeywordProductPair> pairList = GetSampleSet.readPairRelevancy();
		Map<Integer,ProductFeature> productInfoDict = new HashMap<Integer,ProductFeature>();
		System.out.println("====更新训练集，验证集，测试集====");
		System.out.println("---读取原始数据---");
		Map<Integer,Prop> productPropDict = GetSampleSet.readProductProp();
		Map<Integer,productComplex> productComplexDict = GetSampleSet.readProductComplex();
		Map<String,Integer> keywordID = GetSampleSet.getKeywordID();
		for(KeywordProductPair pair:pairList){
			Integer id = pair.productID;
			System.out.println(id);
			try{
				productInfoDict.put(id, new ProductFeature(productComplexDict.get(id),productPropDict.get(id)));				
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		System.out.println("---数据预处理---");
		standardization.standard(productInfoDict);
		BM25 bm25 = new BM25(productInfoDict);
		
		//清空文件
		System.out.println("---清空原文件---");
		IO.writeTxtFile("",Path.testSetPath);
		IO.writeTxtFile("",Path.trainSetPath);
		IO.writeTxtFile("",Path.valiSetPath);
		//
		System.out.println("---更新数据集---");
		for(KeywordProductPair pair:pairList){
//			System.out.println(pair.productID);
//			productInfoDict.get(pair.productID).print();
			try{
				double value = bm25.getValue(pair.keyword,productInfoDict.get(pair.productID).product);
				if(value > 0){
					ProductFeature features = productInfoDict.get(pair.productID);
					String row =pair.relevancy+" "+keywordID.get(pair.keyword)+" 1:"+value+" 2:"+features.price
							+" 3:"+features.basket+ " 4:"+features.pay_num+" 5:"+features.review+" 6:"+features.add_time+"\n";
//					System.out.println(pair.relevancy+" "+keywordID.get(pair.keyword)+" 1:"+value+" 2:"+features.price
//							+" 3:"+features.basket+ " 4:"+features.pay_num+" 5:"+features.review+" 6:"+features.add_time);
					double r = Math.random();
					if(r>0.666){
						IO.append(Path.testSetPath, row, Path.code);
					}
					else if(r>0.333&&r<0.666){
						IO.append(Path.trainSetPath, row, Path.code);
					}
					else{
						IO.append(Path.valiSetPath, row, Path.code);
					}
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
			}
			
		System.out.println("===训练集，验证集，测试集已更新===");
	}
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		get();
	}
}
