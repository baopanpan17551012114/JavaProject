package carvedFile;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import main.Main;

public class Demo {
	public static void main(String[] args){
		String fileRoot = "D:/zdownload_project";
		File[] parent_str = new File(fileRoot).listFiles();
		//System.out.println(strgg[0]);
		List<String> fileList = new LinkedList<String>();
		new Tools().getAllJavaFile(fileRoot, fileList);
		List<String> dic_list = new LinkedList<String>();
		for(String str:fileList){
			String str_root = "";
			
			File file = new File(str);
			String dic_parent = (file.getParent().toString().replace("\\", "/").split("/")[2]);
			//System.out.println(dic_parent);
			if(!dic_list.contains(dic_parent)){
				dic_list.add(dic_parent);
				System.out.println("����--"+(new File("D:/zdownload_project").listFiles().length)+"--����Ŀ����ǰ������ǵ�--"+dic_list.size()+"--��");
				System.out.println("���ڴ���:--"+dic_list.get(dic_list.size()-1)+"--......");
				
			}
			/*String i = new Tools().getAnnotation(str);
			if(i.equals("0")){
				System.gc();
				file.delete();
			}*/
			File file_dic = new File("D:/z_total");//+dic_list.get(dic_list.size()-1));
			
			//�����ļ���
			if(!file_dic.isDirectory()){
				file_dic.mkdir();
			}
			if(file_dic.isDirectory()){				
				str_root = file_dic.toString();
			}
			//�õ���ǰ���ڴ�����ļ���
			new Tools().carveMethod(str,str_root);
			
		}
		System.out.println(fileList.size());
		
	}
}
