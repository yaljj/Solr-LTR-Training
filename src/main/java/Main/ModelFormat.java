package Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Common.IO;
import Common.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/***
 * ltr模型标准化，将ranklib训练出来的模型转换为solr能够识别的json模式
 * @author AdienHuen
 *
 */
public class ModelFormat {
	static private Pattern weightPattern = Pattern.compile("weight=\"(.*?)\"");
	static private Pattern featurePattern = Pattern.compile("<feature> (.*?) </feature>");
	static private Pattern thresholdPattern = Pattern.compile("<threshold> (.*?) </threshold>");
	static private Pattern posPattern = Pattern.compile("<split pos=\"(.*?)\">");
	static private Pattern valuePattern = Pattern.compile("<output> (.*?) </output>");
	private Map<String,String> FeatureDict = new HashMap<String,String>();

	
	public ModelFormat(){
		/*
		 * 添加特征，并于数值标号对应
		 */
		this.FeatureDict.put("1", "BM25");
		this.FeatureDict.put("2", "price_weight");
		this.FeatureDict.put("3", "basket_weight");
		this.FeatureDict.put("4", "pay_num_weight");
		this.FeatureDict.put("5", "review_weight");
		this.FeatureDict.put("6", "add_time_weight");
	}
	
	/***
	 * 将ranklib训练出来的回归树模型转换为solr能够识别的json模式，并把模型保存到所需的地址
	 * @param file solr trees模型文件
	 */
	public void storeMultipleAdditiveTrees(String file){
		List<String> parmsJSON = changeMultipleAdditiveTrees();
		try{
			IO.createFile(file);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		try{
		IO.writeTxtFile("",file);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		for(String row:parmsJSON){
			try{
				IO.append(file,row+"\n",Path.code);	
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}

	}
	/***
	 * 将ranklib训练的lambdaMART模型转化为solr的format
	 * @return 
	 */
	public List<String> changeMultipleAdditiveTrees(){
		List<String> rows = IO.readTxtFile(Path.model_txt, Path.code);
		List<String> parmsJSON = new ArrayList<String>();
		parmsJSON.add("{");
		parmsJSON.add("   \"class\" : \"org.apache.solr.ltr.model.MultipleAdditiveTreesModel\",");
		parmsJSON.add("   \"name\" : \"LambdaMART2\",");
		parmsJSON.add("   \"features\":[");
		parmsJSON.add("       { \"name\" : \"BM25\"},");
		parmsJSON.add("       { \"name\" : \"price_weight\"},");
		parmsJSON.add("       { \"name\" : \"basket_weight\"},");
		parmsJSON.add("	   { \"name\" : \"pay_num_weight\"},");
		parmsJSON.add("	   { \"name\" : \"review_weight\"},");
		parmsJSON.add("	   { \"name\" : \"add_time_weight\"}");
		parmsJSON.add("   ],");
		for(int i=0;i<rows.size();i++){
			if(rows.get(i).equals("<ensemble>")){
				parmsJSON.add("   \"params\" : {");
				parmsJSON.add("       \"trees\" : [");
			}
			if(rows.get(i).lastIndexOf("<tree id=")!=-1)
			{	
				parmsJSON.add("			{");

				Matcher m = weightPattern.matcher(rows.get(i));
				String weight="";
				while (m.find()) {
					weight = m.group(1);
				}
				parmsJSON.add("				"+"\"weight\""+" : \""+weight+"\",");
//				System.out.println("				"+"\"weight\""+" : \""+weight+"\",");
			}
			if(rows.get(i).lastIndexOf("<split>")!=-1){
				parmsJSON.add("				\"root\": {");
//				System.out.println("				\"root\": {");
			}
			if(rows.get(i).lastIndexOf("<feature>")!=-1){
				Matcher m = featurePattern.matcher(rows.get(i));
				String featureID="";
				while (m.find()) {
					featureID = m.group(1);
				}
				String feature = this.FeatureDict.get(featureID);
				int numTab = getTabNum(rows.get(i));
				String tab = getTabStr(numTab);
				parmsJSON.add(tab+"\"feature\" : \""+feature+"\",");
			}
			
			if(rows.get(i).lastIndexOf("<threshold>")!=-1){
				Matcher m = thresholdPattern.matcher(rows.get(i));
				String threshold = "";
				while (m.find()) {
					threshold = m.group(1);
				}
				int numTab = getTabNum(rows.get(i));
				String tab = getTabStr(numTab);
				parmsJSON.add(tab+"\"threshold\" : \""+threshold+"\",");
			
			}
			if(rows.get(i).lastIndexOf("<split pos=")!=-1){
				Matcher m = posPattern.matcher(rows.get(i));
				String pos = "";
				while (m.find()) {
					pos = m.group(1);
				}
				int numTab = getTabNum(rows.get(i));
				String tab = getTabStr(numTab);
				parmsJSON.add(tab+"\""+pos+"\" : {");
			}
			if(rows.get(i).lastIndexOf("<output>")!=-1){
				Matcher m = valuePattern.matcher(rows.get(i));
				String value = "";
				while (m.find()) {
					value = m.group(1);
				}
				int numTab = getTabNum(rows.get(i));
				String tab = getTabStr(numTab);
				parmsJSON.add(tab+"\"value\" : \""+value+"\"");
			}
			if(rows.get(i).lastIndexOf("</split>")!=-1){
				int numTab = getTabNum(rows.get(i));
				String tab = getTabStr(numTab);
				parmsJSON.add(tab+"}");
			}
			if(rows.get(i).lastIndexOf("</tree>")!=-1){
				int numTab = getTabNum(rows.get(i));
				String tab = getTabStr(numTab);
				parmsJSON.add(tab+"}");
			}
		}
		parmsJSON.add("		]");
		parmsJSON.add("	}");
		parmsJSON.add("}");
		
		for(int i=0;i<parmsJSON.size()-1;i++){
			if(parmsJSON.get(i).lastIndexOf("}")!=-1&&parmsJSON.get(i+1).lastIndexOf("\"right\" : {")!=-1){
				parmsJSON.set(i, parmsJSON.get(i)+",");
			}
			if(parmsJSON.get(i).lastIndexOf("}")!=-1&&parmsJSON.get(i+1).lastIndexOf("{")!=-1&&parmsJSON.get(i).length()==parmsJSON.get(i+1).length()){
				parmsJSON.set(i, parmsJSON.get(i)+",");
			}
		}
		for(String row:parmsJSON){
			System.out.println(row);
		}
		return parmsJSON;

	}
	
	static private int getTabNum(String row){
		int num=0;
		for(int i=0;i<row.length();i++){
			if('	'==row.charAt(i)){
				num++;
			}
			else{
				break;
			}
		}
		return num;
	}
	
	public String getTabStr(int numTab){
		String tab = "";
		for(int j=0;j<numTab+2;j++){
			tab += "	";
		}
		return tab;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ModelFormat mf = new ModelFormat();
		mf.storeMultipleAdditiveTrees("data/mymodel.json");
	}
}
