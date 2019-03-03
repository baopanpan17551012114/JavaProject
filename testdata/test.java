package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Helper_reflection {
	/**
	 * @param file
	 * @return
	 * @throws IOException 
	 */
	public List<String> getImportsInAFile(String file) throws IOException{
		List<String> importsList = new ArrayList<String>();
		
		BufferedReader reader = new BufferedReader(new FileReader(new File(file)));
		String lineString = null;
		while((lineString = reader.readLine())!=null){
			if(lineString.startsWith("import")){
				if(!importsList.contains(lineString.split(" ")[1]) && lineString.split(" ")[1].startsWith("java")){
					importsList.add(lineString.split(" ")[1].replace(";", ""));
				}
			}
		}
		return importsList;
	}
	
	/**
	 * 对指定的文件路径，遍历，返回所有的Java文件的路径
	 * 
	 * @param path
	 * @return
	 */
	public void getAllJavaFile(String path, List<String> fileList) {
		File root = new File(path);
		File[] files = root.listFiles();
		for (File file : files) {
			// 如果是文件夹
			if (file.isDirectory()) {
				this.getAllJavaFile(file.getAbsolutePath(), fileList);
			}
			// 如果是文件
			if (file.isFile()) {
				// 文件的后缀名是.java
				if (file.getAbsolutePath().endsWith(".java")) {
					// System.out.println(file.getAbsolutePath());
					if (!fileList.contains(file.getAbsolutePath())){
						fileList.add(file.getAbsolutePath());
					}
				}
			}

		}
		// System.out.println(fileList);
	}
	
	/**
	 * 给定一个项目的路径，返回该项目中所有引用的类名，并以列表形式返回
	 * @param fileName
	 * @return
	 * @throws Exception 
	 */
	public List<String> getProjectImports(String fileName) throws Exception{
		List<String> importsList = new ArrayList<String>();
		
		List<String> fileList = new ArrayList<String>();
		new Helper_reflection().getAllJavaFile(fileName, fileList);
		for(String file:fileList){
			BufferedReader reader = new BufferedReader(new FileReader(new File(file)));
			String lineString = null;
			while((lineString = reader.readLine())!=null){
				if(lineString.startsWith("import")){
					if(!importsList.contains(lineString.split(" ")[1].replace(";", "")) && lineString.split(" ")[1].startsWith("java")){
						importsList.add(lineString.split(" ")[1].replace(";", ""));
					}
				}
			}
		}
		//System.out.println(importsList);
		return importsList;
		
	}
	
	/**
	 * 给定一个项目的路径，返回该项目中所有的引用的包中的方法，并和类组成小列表，然后放在一个大列表中返回
	 * @param fileName
	 * @return
	 * @throws Exception 
	 */
	public List<Object> getAllMethodsAndClass(String fileName){
		List<String> importList = null;
		try {
			importList = new Helper_reflection().getProjectImports(fileName);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		List<Object> totalList = new ArrayList<Object>();
		for(String str:importList){
			try {
				List<String> li = new LinkedList<String>();
				
				try {
					li = new Helper_reflection().getMethodsOfClass(str);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(li.isEmpty()){
					continue;
				}
				List<Object> child = new ArrayList<Object>();
				child.add(str);
				child.add(li);
				totalList.add(child);
			} catch (ExceptionInInitializerError e) {
				continue;
			}catch(NoClassDefFoundError e1){
				continue;
			}
		}
		
		return totalList;
	}
	
	/**
	 * 给定一个类名列表，返回所有的引用的包中的方法，并和类组成小列表，然后放在一个大列表中返回
	 * @param fileName
	 * @return
	 * @throws Exception 
	 */
	public List<Object> getAllMethodsOFListImports(List<String> fileName) throws Exception{
		List<Object> totalList = new ArrayList<Object>();
		for(String str:fileName){
			try {
				List<String> li = new Helper_reflection().getMethodsOfClass(str);
				List<Object> child = new ArrayList<Object>();
				child.add(str);
				child.add(li);
				totalList.add(child);
			} catch (ClassNotFoundException e) {
				continue;
			}catch (NoClassDefFoundError e) {
				continue;
			}
		}
		
		return totalList;
	}
	
	/**
	 * 传入已有的Object列表的列表,对API 的，类名进行替换
	 * @param list
	 * @param str
	 * @return
	 */
	public String getReplacedAPI(List<Object> list,String str){
		
		String left = str.split(".")[0];
		String method = str.split(".")[1];
		String repla = "";
		for(int i=0;i<list.size();i++){
			List<Object> child = (List<Object>) list.get(i);
			String impor = (String) child.get(0);
			List<String> sele = (List<String>) child.get(1);
			if(sele.contains(method)){
				repla = impor;
			}
		}
		if(repla.equals("")){
			return "0";
		}else{
			return repla+"."+method;
		}
		
	}
	
	/**
	 * 得到一个根目录下的所有子文件夹
	 * @param file
	 */
	public void getAllChildDirectary(String filePath,List<String> fileList){
		File rootfile = new File(filePath);
		File[] files = rootfile.listFiles();
		//System.out.println(files.length);
		for (File file : files) {
			// 如果是文件夹
			if (file.isDirectory()) {
				fileList.add(file.getAbsolutePath());
			}
			// 如果是文件
			if (file.isFile()) {
				continue;
			}

		}
	}
	
	/**
	 * 另一种方法对对象进行替换原类的处理
	 * @param listoflist
	 * @param filePath
	 * @return 失败返回0，正确返回1
	 */
	public String getAPIToFile(List<String> listoflist,String filePath,List<Object> pro_imports){
		//System.out.println(listoflist+"999list");
		List<String> finalList = new LinkedList<String>();
		String replaced = "";
		
		List<String> methodsList = new LinkedList<String>();
		for(String str:listoflist){
			if(str.contains(".")){
				methodsList.add(str);//可能调用的是自定义的方法，那就是没有意义的
			}
		}
		//System.out.println(methodsList+"999list");
		if(methodsList.isEmpty()){
			return "0";
		}
		//System.out.println(methodsList+"*******************");
		List<String> classImports;
		List<Object> impor;
		try {
			classImports = new Helper_reflection().getImportsInAFile(filePath);

			impor = new Helper_reflection().getAllMethodsOFListImports(classImports);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			impor = new LinkedList<Object>();
		}
		for(String str:methodsList){//待替换的序列
			//以大写字母开头，说明本来就是     类.方法        的形式
			if(str.toCharArray()[0]>='A' && str.toCharArray()[0]<='Z'){
				//但是还需要判断是否是自定义的类
//				try {
//					List<String> classImport = new Helper_reflection().getImportsInAFile(filePath);
//					if(!classImport.contains(str.split(".")[0])){
//						continue;
//					}
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					continue;
//				}
				finalList.add(str);
				continue;
			}
			replaced = new Helper_reflection().getReplacedAPI(impor, str);
			if(replaced.equals("0")){
				replaced = new Helper_reflection().getReplacedAPI(pro_imports, str);
			}
			finalList.add(replaced);
		}
		if(finalList.isEmpty()){
			return "0";
		}else{
			String final_string = "";
			for(String s:finalList){
				final_string = final_string + s + " ";
			}
			return final_string;
		}
	}
	
	/**
	 * 从处理好的方法体中得到数据
	 * @param fileName
	 */
	public List<String> getData(String method){
		List<String> result = new ArrayList<String>();
		
		String[] lines = method.split("\n");
		List<String> list = new ArrayList<String>();
		for(int i=1;i<lines.length-1;i++){
			String line = lines[i];
			if(line.trim().length() == 0 || line.trim().equals("")){
				continue;
			}
			list.add(line.trim());
		}
		//System.out.println(list);
		
		for(int index = 2;index<list.size();index++){
			String one = list.get(index - 2);
			String two = list.get(index - 1);
			String three = list.get(index);
			String instance = one+"999"+two+"999"+three;
			result.add(instance);
		}
		//System.out.println(result);
		return result;
	}
	
	/**
	 * 删除句子中的多余的空格
	 * @param str
	 * @return
	 */
	public String deleteBlank(String line){
		List<String> list = new ArrayList<String>();
		
		String[] strs = line.split(" ");
		for(String str: strs){
			if(str.equals(" ") || str.equals("") || str.equals("\t")){
				continue;
			}
			list.add(str);
		}
		String result = "";
		for(String str:list){
			result = result + str +" ";
		}
		return result;
	}
	
	public static void main(String[] args) throws Exception{
//		String str = "javafx.scene.control.TextField";
//		String[] strs = str.split("l.T");
//		for(String ss:strs){
//			System.out.println(ss);
//		}
//		String projectName = "E:/z_java_file/android-saripaar-master";
//		List<String> fileList = new LinkedList<String>();
//		try{
//			fileList = new Helper_reflection().getMethodsOfClass(str);
//		}catch(ExceptionInInitializerError e){
//			System.out.println("oooo");
//		}
//		System.out.println(fileList);
//		String file = "D:/z_jar_newdeeled/axis.jar.src/org.apache.axis&&addAttachmentPart.java";
//		BufferedReader reader = new BufferedReader(new FileReader(new File(file)));
//		String method = "";
//		String line = null;
//		while((line = reader.readLine()) != null){
//			method = method + line+"\n";
//		}
//		System.out.println(method);
//		new Helper_reflection().getData(method);
		String str = new Helper_reflection().deleteBlank("new    Helper_reflection().      getData(method);");
		System.out.println(str);
	}

}
