package carvedFile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import main_context.Helper;

public class Tools {
	/**
	 * �Է����Ŀ�ʼ�к�Ϊ�ļ��������÷���д�����ļ�
	 * 
	 * @param fileName��������ļ���
	 * @param begin��ʼ�к�
	 * @param end�����к�
	 */
	public void writeMethodToFile(String fileName, int begin, int end,String savefile) {
		//String newfilename = fileName.substring(fileName.lastIndexOf("\\")+1, fileName.lastIndexOf(".")).replace(".java", "")+String.valueOf(begin)+ ".java";
		String fileNamePart = fileName.substring(fileName.lastIndexOf("\\")+1, fileName.lastIndexOf(".")).replace(".java", "");
		
		File filenew = new File(fileName);
		int index = filenew.getParent().indexOf("src");
		if(index == -1){
			return;
		}
		String packageNamePart = fileName.substring(index+4).replace("\\", ".");
		String methodName = "";
		//��ʼ��
		BufferedReader reader = null;
		FileWriter fw = null;
		BufferedWriter writer = null;
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(fileName);
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		}
		int countNum = 1;//����������		
		try {
			reader = new BufferedReader(fileReader);
			String lineString = null;
			while ((lineString = reader.readLine()) != null) {
				if(countNum > begin && countNum <end && (lineString.contains("public")||lineString.contains("private"))){//��ʼд��
					String[] methods = lineString.split(" ");
					for(String str:methods){
						if(str.contains("(")){
							methodName = str.substring(0, str.indexOf("(")).
									replace("<>", "").replace("<", "").replace(">", "").replace("\"", "");
						}
					}
				}
				countNum++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
		if(methodName.equals("")){
			return;
		}
		String newfilename = packageNamePart+"@"+fileNamePart+"#"+methodName+".java";
		//newfilename = newfilename.substring(0, newfilename.indexOf("\""))+".java";
		newfilename = newfilename.replace("\\", "-");
		newfilename = newfilename.replace("/", "-");
		File file = new File(savefile+"/"+newfilename);
		if(file.exists()){
			return;
		}
		// ��ʼ��
		reader = null;
		fw = null;
		writer = null;
		fileReader = null;
		try {
			fileReader = new FileReader(fileName);
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		}
		countNum = 1;// ����������
		try {
			reader = new BufferedReader(fileReader);
			String lineString = null;
			fw = new FileWriter(file, true);
			writer = new BufferedWriter(fw);
			// ��������������У����ܶ��������﷨��
			while ((lineString = reader.readLine()) != null) {
				if (countNum == end) {
					break;
				}
				// fw = new FileWriter(file, true);
				// writer = new BufferedWriter(fw);
				if (countNum >= begin) {// ��ʼд��
					writer.append(lineString);
					writer.newLine();
					writer.flush();
				}
				countNum++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		try {
			fileReader.close();
			fw.close();
			writer.close();
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * �Ը�����·�����ļ������иÿ������ע�͵ķ����и��һ�ݣ��������Ը÷�����Ϊ�����ļ���д��
	 * 
	 * @param filePath
	 */
	public void carveMethod(String filePath,String saveFile) {
		// �����и����ļ��ĸ�Ŀ¼
		@SuppressWarnings("rawtypes")
		List<List> listoflist = new LinkedList<List>();
		BufferedReader reader = null;
		int begin = 0;
		int end = 0;
		int count = 1;
		
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(filePath);
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		}
		try {
			reader = new BufferedReader(fileReader);
			String lineString = null;
			while ((lineString = reader.readLine()) != null) {
				// �ɹ�����һ��
				// ����������ע�Ϳ�ͷ��
				if (lineString.contains("/**")) {
					if(begin != 0){//��Ϊ0�������ǵ�һ�������Ŀ�ͷ
						end = count-1;
						List<Integer> intList = new LinkedList<Integer>();
						intList.add(begin);
						intList.add(end);
						listoflist.add(intList);
						begin = count;
					}
					if(begin == 0){
						begin = count;
					}
				}
				count++;
			}
			end = count;
			List<Integer> intList = new LinkedList<Integer>();
			intList.add(begin);
			intList.add(end);
			listoflist.add(intList);
			//System.out.println(listoflist);
			fileReader.close();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		//�õ�����������
		for(@SuppressWarnings("rawtypes") List list:listoflist){
			try{
				//System.out.println(list.get(0));
				new Tools().writeMethodToFile(filePath, (Integer)list.get(0), (Integer)list.get(1),saveFile);
			}catch(Exception e){
				continue;
			}
		}
	}
	/**
	 * ��ָ�����ļ�·�����������������е�Java�ļ���·��
	 * 
	 * @param path
	 * @return
	 */
	public void getAllJavaFile(String path, List<String> fileList) {
		File root = new File(path);
		File[] files = root.listFiles();
		int n = 1;
		for (File file : files) {
			// ������ļ���
			if (file.isDirectory()) {
				//System.out.println(file.getParent().toString().equals("D:\\zdownload_project\\30-seconds-of-java8-master"));
				this.getAllJavaFile(file.getAbsolutePath(), fileList);
			}
			// ������ļ�
			if (file.isFile()) {
				// �ļ��ĺ�׺����.java
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
	 * ɾ��ĳЩ������Ҫ����ļ�
	 * @param filePath
	 */
	public void deleteSomeFiles(String fileName){
		File file = new File(fileName);
		BufferedReader reader = null;
		FileWriter fw = null;
		BufferedWriter writer = null;
		FileReader fileReader = null;
		reader = null;
		fw = null;
		writer = null;
		fileReader = null;
		try {
			fileReader = new FileReader(fileName);
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		}
		int countNum = 1;// ����������
		int count = 0;
		int del = 0;
		int de = 0;
		try {
			reader = new BufferedReader(fileReader);
			String lineString = null;
			fw = new FileWriter(file, true);
			writer = new BufferedWriter(fw);
			// ��������������У����ܶ��������﷨��
			while ((lineString = reader.readLine()) != null) {
				if(countNum == 2 &&(!lineString.contains("*") || lineString.contains("Test")|| lineString.contains("{"))){
					del = 1;
				}
				
				if(countNum == 2 &&(lineString.contains("test")|| lineString.contains(".") || lineString.contains("@"))){
					del = 1;
				}
				if(lineString.contains("private") && lineString.contains("class")){
					del = 1;
				}
				if(lineString.contains("*/")){
					de = countNum;
					count = 1;
				}
				countNum++;
				if(count != 0){
					count++;
				}
				if((lineString.contains("public") && lineString.contains("test"))){
					del = 1;
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fileReader.close();
			fw.close();
			writer.close();
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(del == 1 || count <= 7 || de > 8){
			System.gc();
			boolean b = file.delete();
			//System.out.println(b);
		}
	}
	/**
     * ��ȡ�з�֮��ķ����ļ���ע�ͣ�������ھͷ��أ����򷵻�0
     * @param filePat
     * @return
     */
    public String getAnnotation(String filePath){
    	//���ļ�
    	String line = "";
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(filePath));
			String lineString = null;
			int countNumber = 1;
			while ((lineString = reader.readLine()) != null) {
				// �ɹ�����һ��
				if(lineString.contains("/**")){
					break;
				}
				countNumber++;
			}
			int count = 1;
			@SuppressWarnings("resource")
			BufferedReader readernew = new BufferedReader(new FileReader(filePath));
			while ((lineString = readernew.readLine()) != null) {
				// �ɹ�����һ��
				if(count == countNumber+1){
					line = lineString.replace("*", "");
				}
				count++;
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
    	//System.out.println(line.trim()+line.split(" ").length);
		//�õ��������ݺ�Ҫ��������жϣ����Ƿ���һ����Ȼ���Ծ���(Ӣ��)
		if(line.split(" ").length != 0 && line.trim().matches("^[a-z A-Z]*")){
			return line.replace(",|\t|.", " ").trim();
		}else if(line.trim().equals(" ") || line.trim().equals("")){
			return "0";
		}else{
			return "0";
		}
    }
    
    /**
     * ��ָ���ļ����µ������ļ���д���ļ�
     * @param file
     * @throws IOException 
     */
    public void writeFileNameTo(String dictionaryName,String fileName) throws IOException{
    	File file = new File(fileName);
    	BufferedWriter writer = null;
    	try {
			writer = new BufferedWriter(new FileWriter(file));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	List<String> list = new ArrayList<String>();
    	new Tools().getAllJavaFile(dictionaryName, list);
    	for(String str:list){
    		try {
				writer.append(str);
				writer.newLine();
				writer.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	writer.close();
    }
    
    /**
     * ��ָ���ļ��ж�ȡ������·����Ȼ���ȡ��������
     * @param fileName
     */
    public String readFileNameFromFile(String fileName){
    	File file = new File(fileName);
    	BufferedReader reader = null;
    	try {
			reader = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	try {
			String line = reader.readLine();
			reader.close();
			return line;
			//System.out.println(line);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	try {
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
    	
    }
    
    /**
     * ���ظ������ļ�·��������
     * @param fileName
     * @return
     * @throws IOException 
     */
    public String getFileContent(String fileName) throws IOException{
    	File file = new File(fileName);
    	BufferedReader reader = null;
    	try {
			reader = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	String text = "";
    	String line = null;
    	while((line = reader.readLine()) != null){
    		text = text + line+"\n";
    	}
		return text;
    	
    }
    
    /**
     * ��ָ�� ���ļ��������ȡ200���ļ�·������Ϊ�����ļ����������������д���
     * @param fileName
     * @throws IOException 
     */
    @SuppressWarnings({ "null", "resource" })
	public void randomSelect(String fileName,String fileWrite) throws IOException{
    	BufferedReader reader = null;
    	reader = new BufferedReader(new FileReader(new File(fileName)));
    	
    	BufferedWriter writer = null;
    	writer = new BufferedWriter(new FileWriter(new File(fileWrite)));
    	
    	Random random = new Random();
    	List<Integer> int200 = new ArrayList<Integer>();
    	for(int i=0;i<200;i++){
    		int number = random.nextInt(4000);
    		int200.add(number);
    	}
    	Collections.sort(int200);//�����Զ����������򣬽�����Ҫ��д��
    	System.out.println(int200);
    	//��ȡָ���ж�Ӧ���ļ�·��
    	String line = null;
    	int lineIndex = 0;
    	while((line = reader.readLine()) != null){
    		if(int200.contains(lineIndex)){
    			writer.append(line);
    			writer.newLine();
    			writer.flush();
    		}
    		lineIndex++;
    	}
    	writer.close();
    	reader.close();
    	
    	BufferedReader readerAnnotation = null;
    	readerAnnotation = new BufferedReader(new FileReader(new File("D:/z_java_carved/index_test.txt")));
    	
    	BufferedWriter writerAnnotation = null;
    	writerAnnotation = new BufferedWriter(new FileWriter(new File("D:/z_java_carved/test_Annotation.txt")));
    	String annotationLine = null;
    	while((annotationLine = readerAnnotation.readLine()) != null){
    		writerAnnotation.append(new Tools().getAnnotation(annotationLine));
    		writerAnnotation.newLine();
    		writerAnnotation.flush();
    	}
    	readerAnnotation.close();
    	writerAnnotation.close();
    }
    
    /**
     * ����ָ���ļ�·���ķ���������Ϣ
     * @param filePath
     * @return
     * @throws IOException 
     */
    public String getDescription(String filePath) throws IOException{
    	String text = "";
    	
    	BufferedReader reader = null;
    	reader = new BufferedReader(new FileReader(new File(filePath)));
    	String line = null;
    	while((line = reader.readLine()) != null){
    		if(line.contains("*/")){
    			break;
    		}
    		text = text+line;
    	}
    	text = text.replace("\r", " ").replace(
    			"\t", " ").replace("*", "").replace("/", "").replace("@", " ").trim();
    	List<String> list = new ArrayList<String>();
    	String[] strs = text.split(" ");
    	for(String str:strs){
    		if(str.equals("")){
    			continue;
    		}
    		list.add(str);
    	}
    	text = "";
    	for(String str:list){
    		text = text+str+" ";
    	}
    	//System.out.println(text);
		return text;
    }
    
    /**
     * ����ظ��Ĵ����
     * @param old
     * @param file
     * @throws IOException 
     */
    public void getDeelTrainIndex(String old,String file) throws IOException{
    	BufferedReader reader = null;
    	reader = new BufferedReader(new FileReader(new File(old)));
    	
    	BufferedWriter writer = null;
    	writer = new BufferedWriter(new FileWriter(new File(file)));
    	
    	List<String> liststr = new ArrayList<String>();
    	String line = null;
    	while((line = reader.readLine()) != null){
    		String annotation = new Tools().getAnnotation(line);
    		if(liststr.contains(annotation)){
    			continue;
    		}
    		liststr.add(annotation);
    		writer.append(line);
    		writer.newLine();
    		writer.flush();
    	}
    	System.out.println(liststr.size());
    	reader.close();
    	writer.close();
    }
    
    /**
     * �õ���Ӧ·���Ķ�Ӧ�ķ�������
     * @param filePath
     * @throws IOException 
     */
    public String getMethodContent(String filePath) throws IOException{
    	BufferedReader reader = null;
    	reader = new BufferedReader(new FileReader(new File(filePath)));
    	
    	String text = "";
    	String lineString = null;
    	int sign = 0;
    	while((lineString = reader.readLine()) != null){
    		if(sign == 1){
    			text = text + lineString;
    		}
    		if(lineString.contains("*/")){
    			sign = 1;
    		}
    		
    	}
    	String[] strList = text.trim().replace("\r", " ").replace(
    			"\t", " ").replace(",", " ").replace(";", " ").replace("\"", " ").replace("\'", " ").split(" ");
    	text = "";
    	for(String str:strList){
    		if(str.equals("")){
    			continue;
    		}
    		text = text + str + " ";
    	}
    	//System.out.println(text);
    	return text;
    }
    
	public static void main(String[] args) throws IOException{
		/*String dictionaryName = "D:/z_deeled";
		String fileName = "D:/z_java_carved/index_test.txt";
		
		BufferedReader reader = new BufferedReader(
				new FileReader(new File("D:/z_java_carved/index_train_deeled.txt")));
		BufferedWriter writerDes = new BufferedWriter(
				new FileWriter(new File("D:/z_java_carved/data_train_description.txt")));
		BufferedWriter writerMethod = new BufferedWriter(
				new FileWriter(new File("D:/z_java_carved/data_train_method.txt")));
		//new Tools().getDescription(path);
		String lineStr = null;
		while((lineStr = reader.readLine()) != null){
			String des = new Tools().getDescription(lineStr);
			String method = new Tools().getMethodContent(lineStr);
			
			writerDes.append(des);
			writerDes.newLine();
			writerDes.flush();
			
			writerMethod.append(method);
			writerMethod.newLine();
			writerMethod.flush();
		}
		
		reader.close();
		writerDes.close();
		writerMethod.close();*/
		BufferedReader reader = new BufferedReader(
				new FileReader(new File("D:/z_java_carved/index_train_deeled.txt")));
		
		BufferedWriter writerDes = new BufferedWriter(
				new FileWriter(new File("D:/z_java_carved/data_train_annotation.txt")));
		
		String lineString = null;
		while((lineString = reader.readLine()) != null){
			String anno = new Tools().getAnnotation(lineString);
			writerDes.append(anno);
			writerDes.newLine();
			writerDes.flush();
		}
		
		reader.close();
		writerDes.close();
	}

}
