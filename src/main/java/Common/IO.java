package Common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class IO {
	
    public static void main(String[] args){  

    }  
	
    public static List<String> readTxtFile(String filePath,String encoding){
    	List<String> rows = new ArrayList<String>();
        try {
                File file=new File(filePath);
                if(file.isFile() && file.exists()){ //�ж��ļ��Ƿ����
                    InputStreamReader read = new InputStreamReader(
                    new FileInputStream(file),encoding);//���ǵ������ʽ
                    BufferedReader bufferedReader = new BufferedReader(read);
                    String lineTxt = null;
                    while((lineTxt = bufferedReader.readLine()) != null){
//                        System.out.println(lineTxt);
                        rows.add(lineTxt);
                    }
                    read.close();
        }else{
            System.out.println("�Ҳ���ָ�����ļ�");
        }
        } catch (Exception e) {
            System.out.println("��ȡ�ļ����ݳ���");
            e.printStackTrace();
        }
        return rows;
    }
    
    /** 
     * �����ļ� 
     * @param fileName 
     * @return 
     */  
    public static boolean createFile(String name)throws Exception{ 
    File fileName = new File(name);
     boolean flag=false;  
     try{  
      if(!fileName.exists()){  
       fileName.createNewFile();  
       flag=true;  
      }  
     }catch(Exception e){  
      e.printStackTrace();  
     }  
     return true;  
    }   

    public static boolean writeTxtFile(String content,String  name)throws Exception{  
    	File fileName = new File(name);
    	RandomAccessFile mm=null;  
    	  boolean flag=false;  
    	  FileOutputStream o=null;  
    	  try {  
    	   o = new FileOutputStream(fileName);  
    	      o.write(content.getBytes("GBK"));  
    	      o.close();  
    	//   mm=new RandomAccessFile(fileName,"rw"); 
    	//   mm.writeBytes(content);  
    	   flag=true;  
    	  } catch (Exception e) {  
    	   // TODO: handle exception  
    	   e.printStackTrace();  
    	  }finally{  
    	   if(mm!=null){  
    	    mm.close();  
    	   }  
    	  }  
    	  return flag;  
    }
    
	public static void append(String file, String str, String code)throws Exception{
		/*
		 * ���ļ�file׷��str����code�ı�����ʽ
		 */
		RandomAccessFile raf=new RandomAccessFile(file, "rw");
		raf.seek(raf.length());
		raf.write(str.getBytes(code));
		raf.close();
	}
	


}
