package Main;

import java.util.List;

import Common.DBhelper;

public class OriginalTrainingData{
	
	/***
	 * ��ȡ���������ԭʼ����ԭʼ����
	 */
    public void updateOriginalData(){
		DBhelper db = new DBhelper();
		db.connect2dc();
		List<String> keywords = db.getHitKeyword();
		db.getKeywordProductPair(keywords);
		db.getProductPropAll();
		db.getProductComplexAll();
		
    }
    
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	    /*
	     * ����ԭʼ����
	     */

	    
	}

}
