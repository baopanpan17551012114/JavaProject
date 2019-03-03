package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Helper_reflection {
	/**
	 * 利用反射机制获取给定类名的所有方法，并以列表的方式返回，方法的形式为method()(比如toString())
	 * @param cls
	 * @return
	 * @throws ClassNotFoundException 
	 */
	public List<String> getMethodsOfClass(String clsName) throws ClassNotFoundException{
		List<String> list = new ArrayList<String>();
		Class cls = null;
		try{
			cls = Class.forName(clsName);
		}catch(ClassNotFoundException e){
			return list;
		}
	
		for(int i=0;i<cls.getMethods().length;i++){
			String string = cls.getMethods()[i].toString();
			String[] strs = string.split(" ");
			for(String str:strs){
				if(str.contains("(")){
					//先用正则表达式将括号全部消去,这里比较简单可以不用
					str = str.substring(0, str.indexOf("("));
					str = str.substring(str.lastIndexOf(".")+1);
					if(!list.contains(str)){
						list.add(str);
					}
					
				}
			}
		}
		return list;
	}
	
	/**
	 * 返回单个文件中的所有引用的类
	 * @param file
	 * @return
	 * @throws IOException 
	 */
	@SuppressWarnings("resource")
	public List<String> getImportsInAFile(String filePath) throws IOException{
		List<String> importsList = new ArrayList<String>();
		
		@SuppressWarnings("resource")
		
		BufferedReader reader = null;
		reader = new BufferedReader(new FileReader(new File(filePath)));
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
	
	/**
	 * Reads the contents of a file
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public String readFile(String file) throws Exception{
		BufferedReader reader = new BufferedReader(new FileReader(new File(file)));
		String line = null;
		String result = "";
		while((line = reader.readLine()) != null){
			line = result+ line.trim();
		}
		return result;
		
	}
	
	/**
	 * 抽取同种序列
	 * @throws IOException
	 */
	public void getSameClassAPIseq() throws IOException{
		String fileName = "D:/API.txt";
		BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
		String fileWrite = "D:/deeledAPI.txt";
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(fileWrite)));
		
		int index = 0;
		String line = null;
		while((line = reader.readLine()) != null && !(line = reader.readLine()).equals("\r")){
			line = reader.readLine().trim().replace("\n", "").replace("\r", "").split("、")[1];
			String[] apis = line.split(" ");
			if(apis.length == 0){
				continue;
			}
			List<String> classList = new ArrayList<String>();
			//先获取所有可能的类，以便后面可以根据类寻找一行中所有可能不止一种序列
			for(String str:apis){
				String[] lefts = str.split("\\.");
				if(lefts.length < 2){
					continue;
				}
				if(!classList.contains(lefts[0])){
					classList.add(lefts[0]);
				}
			}
			
			//寻找所有可能的序列，暂时先不考虑所有只有一个的情况
			for(String type:classList){
				List<String> list = new ArrayList<String>();
				for(String str:apis){
					if(str.split("\\.").length < 2){
						continue;
					}
					if(type.equals(str.split("\\.")[0])){
						list.add(str);
					}
				}
				if(list.size() == 1){
					continue;
				}
				//写文件
				String result = "";
				for(String word:list){
					result = result + word + " ";
				}
				index++;
				writer.append(index + "、" + result);
				writer.newLine();
				writer.flush();
			}
			
		}
	}
	
	/**
	 * Return the subscript of the element which is the 
	 * largest in given list
	 * @param the given list
	 * @return subscript
	 */
//	public int getMostList(List<Integer> list<integer>){
//		int int_type = 1;
//		int int_type = 1;
//		for int int_type=0 int_type<list<integer>.size int_type++
//			if int_type < list<integer>.get(int_type)
//				int_type = list<integer>.get(int_type)
//				int_type = int_type
//	
//		return int_type
//	}
	
	/**
	 * Return the subscript of the element which is the 
	 * largest in given list
	 * @param the given list
	 * @return subscript
	 */
	public int getMostList(List<Integer> list){
		int index = 0;
		int max = 0;
		for(int i=0;i<list.size();i++){
			if(max < list.get(i)){
				max = list.get(i);
				index = i;
			}
		}	
		return index;
	}
	
	/**
	 * 将带有重复行的文件去重，并且按照重复数由高到低排序
	 * @throws Exception
	 */
	public void getOrderedFile() throws Exception{
		String file = "D:/API.txt";
		BufferedReader reader = new BufferedReader(new FileReader(new File(file)));
		
		List<String> lineList = new ArrayList<String>();
		String line = null;
		while((line = reader.readLine()) != null){
			String current = line.split("、")[1].trim().replace("\r", "").replace("\n", "");
//			if(lineList.contains(current)){
//				continue;
//			}
			lineList.add(current);
		}
		//System.out.println(lineList.size());
		//System.out.println(lineList);
		List<String> conList = new ArrayList<String>();
		List<Integer> countList = new ArrayList<Integer>();
		for(int i=0;i<lineList.size();i++){
			String current = lineList.get(i).trim().replace("\r", "").replace("\n", "");
			int number = 0;
			if(conList.contains(current)){
				continue;
			}
			for(int j=i;j<lineList.size();j++){
				String compare = lineList.get(j);
				if(current.equals(compare)){
					number++;
				}
			}
			countList.add(number);
			conList.add(current);
		}
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File("D:/API_new.txt")));
		//System.out.println(conList.size());
		//System.out.println(countList);
		int sign = 0;
		while(!conList.isEmpty()){
			sign++;
			int most_index = new Helper_reflection().getMostList(countList);
			int max = countList.get(most_index);
			String current_line = conList.get(most_index);
			writer.append(sign+":"+max+"、"+current_line);
			writer.newLine();
			writer.flush();
			conList.remove(most_index);
			countList.remove(most_index);
		}
		
	}
	
	/**
	 * 得到最终推荐结果，最多为十个
	 * @param line
	 * @return
	 * @throws Exception 
	 */
	public List<String> getRecommend(String lineIn) throws Exception{
		List<String> recommendList = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new FileReader(new File("D:/API_new.txt")));
		List<String> lineList = new ArrayList<String>();
		String line = null;
		while((line = reader.readLine()) != null){
			line = line.split("、")[1].trim().replace("\r", "").replace("\n", "");
			if(lineList.contains(line)){
				continue;
			}
			lineList.add(line);
		}
		//System.out.println(lineList.size());
		String[] words = lineIn.trim().replace("\r", "").replace("\n", "").split(" ");
		for(String currtentLine:lineList){
			boolean sign = true;
			//匹配最好，全部匹配
			String now_line = "";
			for(String word:words){
				word = word.trim();
				now_line = now_line + word + " ";
			}
			if(currtentLine.contains(now_line)){
				System.out.println(now_line);
				System.out.println(currtentLine);
				if(currtentLine.substring(currtentLine.indexOf(now_line)+now_line.length()+1).trim() != ""){
					//System.out.println(currtentLine.toCharArray()[now_line.length()+1]);
					String one_result = currtentLine.substring(currtentLine.indexOf(now_line)+now_line.length()).trim();
					if(!one_result.contains(" ")){
						recommendList.add(one_result);
						break;
					}
					if(one_result.contains(" ")){
						recommendList.add(one_result.split(" ")[0]);
						break;
					}
				}
			}
		}
		
		if(recommendList.size() > 10){
			return recommendList;
		}
		for(String currtentLine:lineList){
			if(currtentLine.contains(words[words.length-1])){
				if(currtentLine.substring(currtentLine.indexOf(words[words.length-1])+words[words.length-1].length()+1).trim() != ""){
					String one_result = currtentLine.substring(currtentLine.indexOf(words[words.length-1])+words[words.length-1].length()+1).trim();
					
					if(!one_result.contains(" ")){
						if(recommendList.contains(one_result)){
							continue;
						}
						recommendList.add(one_result);
						continue;
					}
					if(one_result.contains(" ")){
						if(recommendList.contains(one_result.split(" ")[0])){
							continue;
						}
						recommendList.add(one_result.split(" ")[0]);
						continue;
					}
					
				}
			}
		}
		System.out.println(recommendList);
		return recommendList;
	}
	
	/**
	 * 对顾晓东模型数据进行处理
	 * @throws Exception 
	 */
	public void deelData() throws Exception{
		String fileName = "D:/329API.txt";
		BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
		String fileWrite = "D:/329annotation.txt";
		BufferedReader reader1 = new BufferedReader(new FileReader(new File(fileWrite)));
		
		List<String> annotation = new ArrayList<String>();
		List<String> API = new ArrayList<String>();
		List<String> annotation_result = new ArrayList<String>();
		List<String> API_result = new ArrayList<String>();
		
		//去除no annotation
		String line = null;
		while((line = reader1.readLine()) != null){
			line = line.trim().replace("\r", "").replace("\n", "").split("、")[1];
			annotation.add(line);
		}
		line = null;
		while((line = reader.readLine()) != null){
			line = line.trim().replace("\r", "").replace("\n", "").split("、")[1];
			API.add(line);
		}
//		System.out.println(API.size());
//		System.out.println(annotation.size());
//		System.out.println(API.get(0));
//		System.out.println(annotation.get(0));
		for(int index=0;index<annotation.size();index++){
			String line1 = annotation.get(index);
			String line2 = API.get(index);
			if(line1.equals("no annotation")){
				continue;
			}
			if(line1.startsWith("protect") || line1.startsWith("public")){
				continue;
			}
			if(line1.toCharArray()[0]<'A' || line1.toCharArray()[0]>'z'){
				continue;
			}
			if(line1.contains("{@")){
				line1 = line1.substring(0, line1.indexOf("{@"));
			}
			if(line1.trim().matches("^[a-z A-Z]*")){
				line1 = line1.replace(",|\t|.", " ").trim();
				
				//去重
				if(annotation_result.contains(line1) || API_result.contains(line2)){
					continue;
				}
				if(line1.trim().split(" ").length<3){
					continue;
				}
				annotation_result.add(line1);
				API_result.add(line2);
			}
//			annotation_result.add(line1);
//			API_result.add(line2);
		}
		System.out.println(API_result.size());
		System.out.println(annotation_result.size());
		BufferedWriter writer1 = new BufferedWriter(new FileWriter(new File("D:/329API_new.txt")));
		BufferedWriter writer2 = new BufferedWriter(new FileWriter(new File("D:/329annotation_new.txt")));
		for(int index=0;index<annotation_result.size();index++){
			writer1.append(API_result.get(index));
			writer1.newLine();
			writer1.flush();
			
			writer2.append(annotation_result.get(index));
			writer2.newLine();
			writer2.flush();
		}
		
	}
	
	/**
	 * 将文件中全部转为小写
	 * @throws Exception 
	 */
	public void getLower() throws Exception{
		String path = "D:/329annotation_new.txt";
		BufferedReader reader = new BufferedReader(new FileReader(new File(path)));
		String path_new = "D:/annotation11.txt";
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(path_new)));
		
		List<String> list = new ArrayList<String>();
		List<String> word_list = new ArrayList<String>();
		String line = null;
		while((line = reader.readLine()) != null){
			line = line.trim().replace("\r", "").replace("\n", "");
			line = line.toLowerCase();
			list.add(line);
			String[] words = line.split(" ");
			for(String word:words){
				if(word.equals("") || word_list.contains(word)){
					continue;
				}
				word_list.add(word);
			}
			
		}
		
		List<String> new_word_list = new ArrayList<String>();
		for(int i=0;i<word_list.size();i++){
			boolean sign = true;
			for(int j=0;j<word_list.size();j++){
				if(word_list.get(i).equals(word_list.get(j)+"s")){
					sign = false;
					break;
				}
			}
			if(sign){
				new_word_list.add(word_list.get(i));
			}
		}
		
		for(String line1:list){
			for(String word1:new_word_list){
				if(line1.contains(word1+"s")){
					line1 = line1.replace(word1+"s", word1);
				}
			}

			writer.append(line1);
			writer.newLine();
			writer.flush();
			
		}
		
	}
	
	
	/**
	 * 删除API和注释中重复的行
	 * @throws Exception 
	 */
	public void deleteCopy() throws Exception{
		String fileName = "D:/last_annotation.txt";
		BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
		String fileWrite = "D:/last_API.txt";
		BufferedReader reader1 = new BufferedReader(new FileReader(new File(fileWrite)));
		
		String fileWrite1 = "D:/last_index.txt";
		BufferedReader reader2 = new BufferedReader(new FileReader(new File(fileWrite1)));
		
		List<String> annotation = new ArrayList<String>();
		List<String> API = new ArrayList<String>();
		List<String> annotation_result = new ArrayList<String>();
		List<String> API_result = new ArrayList<String>();
		
		List<String> index_result = new ArrayList<String>();
		List<String> indexfile = new ArrayList<String>();
		
		//去除no annotation  2530152
		int line_number = 0;
		String line = null;
		while((line = reader.readLine()) != null){

			//System.out.println(line);
			line = line.trim().replace("\r", "").replace("\n", "");
			//System.out.println(line);
			annotation.add(line);
		}
		line = null;
		while((line = reader1.readLine()) != null){

			line = line.trim().replace("\r", "").replace("\n", "");
			API.add(line);
		}
		line = null;
		while((line = reader2.readLine()) != null){
			line = line.trim().replace("\r", "").replace("\n", "");
			indexfile.add(line);
		}
		for(int index=0;index<annotation.size();index++){
			String line1 = annotation.get(index);
			String line2 = API.get(index);
			String line3 = indexfile.get(index);
			

			if(line1.contains("mutabledhashseparatekvbyteshortmapgo")){
				continue;
			}
			//mutabledhashseparatekvbytekeymap
			if(line1.contains("mutabledhashseparatekvbytekeymap")){
				continue;
			}
			// 去重
			if (annotation_result.contains(line1) || API_result.contains(line2)) {
				continue;
			}
			
			annotation_result.add(line1);
			API_result.add(line2);
			index_result.add(line3);
			//去除重复行
			if(annotation_result.contains(line1) || index_result.contains(line3)){
				continue;
			}

			annotation_result.add(line1);
			API_result.add(line2);
			index_result.add(line3);
//			annotation_result.add(line1);
//			API_result.add(line2);
		}
		System.out.println(API_result.size());
		System.out.println(annotation_result.size());
		System.out.println(index_result.size());
		
		BufferedWriter writer1 = new BufferedWriter(new FileWriter(new File("D:/1last_API.txt")));
		BufferedWriter writer2 = new BufferedWriter(new FileWriter(new File("D:/1last_annotation.txt")));
		BufferedWriter writer3 = new BufferedWriter(new FileWriter(new File("D:/1last_index.txt")));
		for(int index=0;index<annotation_result.size();index++){
			writer1.append(API_result.get(index));
			writer1.newLine();
			writer1.flush();
			
			writer2.append(annotation_result.get(index));
			writer2.newLine();
			writer2.flush();
			
			writer3.append(index_result.get(index));
			writer3.newLine();
			writer3.flush();
		}
	}
	
	/**
	 * 计算一个文件中词汇数
	 * @throws Exception 
	 */
	public void countVocabilary() throws Exception{
		String fileName = "D:/last_annotation.txt";
		BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
		List<String> word_list = new ArrayList<String>();
		
		String line = null;
		while((line = reader.readLine()) != null){
			line = line.trim().replace("\r", "").replace("\n", "");
			if(line.contains(" ")){
				String[] words = line.split(" ");
				for(String word:words){
					if(word_list.contains(word)){
						continue;
					}
					word_list.add(word);
				}
			}else{
				if(word_list.contains(line)){
					continue;
				}
				word_list.add(line);
			}
		}
		System.out.println(word_list.size());
	}
	
	/**
	 * 融合
	 * @throws Exception 
	 * 
	 */
	public void connectTwo() throws Exception{
		String fileName = "D:/last_annotation.txt";
		BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
		String fileWrite = "D:/last_API.txt";
		BufferedReader reader1 = new BufferedReader(new FileReader(new File(fileWrite)));
		
		List<String> annotation = new ArrayList<String>();
		List<String> API = new ArrayList<String>();
		
		List<String> result = new ArrayList<String>();
		//
		int line_number = 0;
		String line = null;
		while((line = reader.readLine()) != null){
			//System.out.println(line);
			line = line.trim().replace("\r", "").replace("\n", "");
			//System.out.println(line);
			annotation.add(line);
		}
		line = null;
		while((line = reader1.readLine()) != null){
			line = line.trim().replace("\r", "").replace("\n", "");
			API.add(line);
		}
		
		System.out.println(API.size()+":::"+annotation.size());
		for(int i=0;i<annotation.size();i++){
			String line1 = annotation.get(i);
			String line2 = API.get(i);
			if(line2.equals("no api")){
				result.add(line1);
			}else{
				line2 = line2.toLowerCase().replace(".", " ").replace("\r", "").replace("\n", "");
				result.add(line1+" "+line2);
			}
		}
		System.out.println(result.size());
		BufferedWriter writer1 = new BufferedWriter(new FileWriter(new File("D:/last.txt")));
		
		for(String str:result){
			writer1.append(str);
			writer1.newLine();
			writer1.flush();
			
		}
	}
	
	/**
	 * 将annotation和API都没有的代码段删除
	 * @throws Exception
	 */
	public void deleteSomeInformation() throws Exception{
		String fileName = "D:/162annotation.txt";
		BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
		String fileWrite = "D:/162API.txt";
		BufferedReader reader1 = new BufferedReader(new FileReader(new File(fileWrite)));
		
		String fileWrite1 = "D:/162index.txt";
		BufferedReader reader2 = new BufferedReader(new FileReader(new File(fileWrite1)));
		
		List<String> annotation = new ArrayList<String>();
		List<String> API = new ArrayList<String>();
		List<String> annotation_result = new ArrayList<String>();
		List<String> API_result = new ArrayList<String>();
		
		List<String> index_result = new ArrayList<String>();
		List<String> indexfile = new ArrayList<String>();
		
		int line_number = 0;
		String line = null;
		while((line = reader.readLine()) != null){
			if(line.trim().replace("\r", "").replace("\n", "").split("、").length < 2){
				break;
			}
			//System.out.println(line);
			line = line.trim().replace("\r", "").replace("\n", "").split("、")[1];
			annotation.add(line);
		}
		line = null;
		while((line = reader1.readLine()) != null){
			if(!line.contains("、")){
				break;
			}
			line = line.trim().replace("\r", "").replace("\n", "").split("、")[1];
			API.add(line);
		}
		line = null;
		while((line = reader2.readLine()) != null){
			if(!line.contains("、")){
				break;
			}
			line = line.trim().replace("\r", "").replace("\n", "").split("、")[1];
			indexfile.add(line);
		}
		
		for(int index=0;index<annotation.size();index++){
			String line1 = annotation.get(index);
			String line2 = API.get(index);
			String line3 = indexfile.get(index);
			
			if(line1.equals("no annotation") && line2.equals("no api")){
				continue;
			}
			if(line1.contains("(")){
				//line1 = line1.toLowerCase();
				line1 = line1.trim().substring(0, line1.lastIndexOf("("));
				
				//System.out.println(line1);
			}
			
			String[] line1s = line1.split(" ");
			line1 = "";
			for(String word:line1s){
				if(word.equals(" ")){
					continue;
				}
				line1 = line1 + word + " ";
			}
			line1 = line1.trim();
			if(!line2.equals("no api")){
				line2 = line2.replace(".", " ");
				line1 = line1 + line2;
			}
			if(line1.contains("no annotation")){
				line1 = line1.replace("no annotation", "");
			}
			char[] chars = line1.toCharArray();
			line1 = "";
			for(int i=0;i<chars.length;i++){
				if(chars[i] >= 'A' && chars[i] < 'a'){
					line1 = line1  + ' '+ chars[i];
					continue;
				}
				line1 = line1 + chars[i];
			}
			
			
			for(int i=0;i<chars.length;i++){
				if(chars[i] == ' '){
					continue;
				}
				if(chars[i]<'A' || chars[i]>'z'){
					chars[i] = ' ';
				}
			}
			
			String[] line1ss = line1.split(" ");
			line1 = "";
			for(String word:line1ss){
				if(word.length() == 1 || word.equals(" ")){
					continue;
				}
				line1 = line1 + word + " ";
			}
			
			
			annotation_result.add(line1);
			API_result.add(line2);
			index_result.add(line3);
		}

		System.out.println(API_result.size());
		System.out.println(annotation_result.size());
		System.out.println(index_result.size());
		
		BufferedWriter writer1 = new BufferedWriter(new FileWriter(new File("D:/last_API.txt")));
		BufferedWriter writer2 = new BufferedWriter(new FileWriter(new File("D:/last_annotation.txt")));
		BufferedWriter writer3 = new BufferedWriter(new FileWriter(new File("D:/last_index.txt")));
		for(int index=0;index<annotation_result.size();index++){
			writer1.append(API_result.get(index));
			writer1.newLine();
			writer1.flush();
			
			writer2.append(annotation_result.get(index));
			writer2.newLine();
			writer2.flush();
			
			writer3.append(index_result.get(index));
			writer3.newLine();
			writer3.flush();
		}
	}
	
	/**
	 * 整合文件名、API和annotation信息
	 * @throws Exception 
	 */
	public static void getAllInformation() throws Exception{
		String fileName = "D:/162Name.txt";
		BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
		String fileWrite = "D:/162API.txt";
		BufferedReader reader1 = new BufferedReader(new FileReader(new File(fileWrite)));
		
		String fileWrite1 = "D:/162annotation.txt";
		BufferedReader reader2 = new BufferedReader(new FileReader(new File(fileWrite1)));
		
		List<String> annotation = new ArrayList<String>();
		List<String> API = new ArrayList<String>();
		List<String> name = new ArrayList<String>();

		int line_number = 0;
		String line = null;
		while((line = reader2.readLine()) != null){
			line = line.trim().replace("\r", "").replace("\n", "").replace(",", " ").replace("."," ").split("、")[1];
			if(!line.equals("no annotation")){
				annotation.add(line);
			}else{
				annotation.add(" ");
			}
		}
		while((line = reader1.readLine()) != null){
			line = line.trim().replace("\r", "").replace("\n", "").replace(",", " ").split("、")[1];
			if(!line.equals("no api")){
				API.add(line);
			}else{
				API.add(" ");
			}
		}
		
		while((line = reader.readLine()) != null){
			line = line.trim().replace("\r", "").replace("\n", "").replace(",", " ").replace("."," ").split("、")[1];
			name.add(line);
		}
		
		List<String> allContent = new ArrayList<String>();
		BufferedWriter resultWriter = new BufferedWriter(new FileWriter(new File("D:/result.txt")));
		for(int i=0;i<name.size();i++){
			String line_current = name.get(i) + " "+API.get(i) + " " + annotation.get(i);  
			//去除空格 以及小写化
			String del = "";
			String[] words = line_current.split(" ");
			for(String word:words){
				if(word.equals(" ") || word.equals("")){
					continue;
				}else{
					del = del + " "+word;
				}
			}
			del = del.toLowerCase();
			//将处理好的写入文件
			resultWriter.append(del.trim());
			resultWriter.newLine();
			resultWriter.flush();
			
		}
	}
	
	public static void getSimilarityValues() throws Exception{
		BufferedReader reader = new BufferedReader(new FileReader(new File("D:/result.txt")));
		
		
		
	}
	
	public static void main(String[] args) throws Exception{
		//new Helper_reflection().countVocabilary();
//		List<Integer> numbers = new ArrayList<Integer>();
//		Random random = new Random();
//		float total = 0;
//		for(int i=0;i<12;i++){
//			int number = random.nextInt(8)+2;
//			float m = number;
//			total = total + 1/m;
//			//System.out.println(total);
//			System.out.println(number);
//		}
//		System.out.println("zhiwei:"+total);
		
//		new Helper_reflection().deleteSomeInformation();
//		new Helper_reflection().deleteCopy();
		//new Helper_reflection().getAllInformation();D:\zLineData\ASTGenerationTest
//		File root = new File("D:/zLineData/ASTGenerationTest");
//		BufferedWriter writerin = new BufferedWriter(new FileWriter(new File("D:/in.txt")));
//		BufferedWriter writerout = new BufferedWriter(new FileWriter(new File("D:/out.txt")));
//		
//		File[] files = root.listFiles();
//		for(File file:files){
//			List<String> result = new ArrayList<String>();
//			int sign = 0;
//			BufferedReader reader = new BufferedReader(new FileReader(file));
//			String line = null;
//			while((line = reader.readLine()) != null){
//				line = line.trim().replace("\r", "").replace("\n", "");
//				//System.out.println(line);
//				if(sign == 1 && !line.equals("}")){
//					result.add(line);
//				}
//				if(line.startsWith("public") || line.startsWith("private")){
//					sign = 1;
//				}
//				if(sign == 0){
//					continue;
//				}
//			}
//			
//			for(int index = 1;index<result.size();index++){
//				//String one = result.get(index - 2);
//				String line_in = result.get(index - 1);
//				String line_out = result.get(index);
//				//String instance = one+"999"+two+"999"+three;
//				writerin.append(line_in);
//				writerin.newLine();
//				writerin.flush();
//				
//				writerout.append(line_out);
//				writerout.newLine();
//				writerout.flush();
//			}
//		}
		getAllInformation();
		
	}

}
