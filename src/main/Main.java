package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import structure.MyMethodNode;

public class Main {

	/**
	 * ��ָ�����ļ�·�����������������е�Java�ļ���·��
	 * 
	 * @param path
	 * @return
	 */
	public void getAllJavaFile(String path, List<String> fileList) {
		File root = new File(path);
		File[] files = root.listFiles();
		if(files.length == 0){
			fileList = new LinkedList<String>();
		}
		for (File file : files) {
			// ������ļ���
			if (file.isDirectory()) {
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

	public static void main(String[] args) throws IOException, Exception{
		List<String> deelCopy_input = new ArrayList<String>();//Ϊ�˷�ֹ�ظ�����
		List<String> deelCopy_output = new ArrayList<String>();
		int totalLineNum = 0;
		int i = 0;
		int number = 1;
		//String fileRoot = "D:/";
		String fileRoot = "D:/z162project";

		BufferedWriter writer1 = new BufferedWriter(new FileWriter(new File("D:/162API.txt")));
		BufferedWriter writer2 = new BufferedWriter(new FileWriter(new File("D:/162annotation.txt")));
		BufferedWriter writer3 = new BufferedWriter(new FileWriter(new File("D:/162index.txt")));
		BufferedWriter nameWriter = new BufferedWriter(new FileWriter(new File("D:/162Name.txt")));
		
//		
		List<String> directaryList = new LinkedList<String>();
		List<String> fileList = new LinkedList<String>();
		
		new Helper_reflection().getAllChildDirectary(fileRoot, directaryList);
		
		int index = 0;
		for(String direc:directaryList){
			i++;
			System.out.println("��ǰ���ڴ����"+i+"���ļ���......");
			List<Object> pro_imports = new LinkedList<Object>();
			//System.out.println(direc);
			try{
				pro_imports = new Helper_reflection().getAllMethodsAndClass(direc);
				
			}catch(Exception e){
				continue;
			}
			fileList.clear();
			new Main().getAllJavaFile(direc, fileList);
			System.out.println(fileList.size());
			
			//���ն�Ӧ���ļ��У��и�֮��Ҳ�����ļ��н��зֿ� װ
			String saveDirz_one = direc.substring(direc.lastIndexOf("\\")+1);
			String saveDIR = "D:/z_methods/"+saveDirz_one;
			File saveDirectory = new File(saveDIR);
			if(!saveDirectory.isDirectory()){
				saveDirectory.mkdirs();
			}
			
			for (String FilePath : fileList) {
				//�õ�һ��java�ļ�
				//System.out.println(FilePath);
				//�ȹ����ļ���ŵ��ļ��У���ȡ����
				//System.out.println(FilePath);
				String[] packNames = FilePath.split("\\\\");
				
				String packName = packNames[packNames.length - 2];
				
				File f = new File(FilePath);// ���·������Ҫ���ɳ����﷨����·��
				List<String> imports_list = new ASTtoDOT().getImportsClass(FilePath);
				List<String> imports_list_whole = new ASTtoDOT().getImportsInAFile(FilePath);
				
				ASTGenerator astGenerator = new ASTGenerator(f);
				List<MyMethodNode> methodNodeList = astGenerator.getMethodNodeList();
				
				for (MyMethodNode m : methodNodeList) {//ÿ��m��һ������
					
					//�õ�������
					
					String method = new ASTtoDOT().getCarveredMethod(m);

					int nowNum = (method.split(";")).length;
					totalLineNum = totalLineNum + nowNum;
					//��this���д�����Ҫ���滻Ϊ   ��Ӧ��
//					method = new ASTtoDOT().deelThis(m, method);
//					
//					//�����������������滻���ķ�����
					method = new ASTtoDOT().changeBaseType(m,method);
//					
//					//�����ǻ������������滻���ķ�����
					//method = new ASTtoDOT().changeOtherType(m, method,imports_list);
//					
//					//����API����ķ�����
					method = new ASTtoDOT().deelMethodInvoke(m, method, imports_list_whole);
//					
//					//����������ֵ��ʽ������ �ķ�����
					method = new ASTtoDOT().deelNumber(m, method);
//					//System.out.println(method);
					
					List<String> countList = new ArrayList<String>();
					String[] lines = method.split("\n");
					//����̫����û������ ��
					for(String str:lines){
						if(str.trim().equals("{") || str.trim().equals("}")){
							continue;
						}
						countList.add(str);
					}
					if(countList.size() < 5){
						continue;
					}
//					
//					List<String> list = new Helper_reflection().getData(method);
//					for(String str:list){
//						String[] results = str.split("999");
//						results[0] = new Helper_reflection().deleteBlank(results[0]);
//						results[1] = new Helper_reflection().deleteBlank(results[1]);
//						
//						if(results[2].equals("}") || results[2].equals("{")){
//							continue;
//						}
//						if((results[0].equals("}") && results[1].equals("}")) || (results[0].equals("{") && results[1].equals("{")) || (results[0].equals("}") && results[1].equals("{"))){
//							continue;
//						}
//						if(deelCopy_input.contains(results[0]+" 999 "+results[1])){
//							continue;
//						}
//						if(deelCopy_output.contains(results[2])){
//							continue;
//						}
//						boolean sign = false;
//						for(String word:deelCopy_input){
//							if(word.startsWith(results[0]) || results[0].startsWith(word)){
//								sign = true;
//								break;
//							}
//						}
//						if(sign){
//							continue;
//						}
//						
//						deelCopy_input.add(results[0]+" 999 "+results[1]);
//						deelCopy_output.add(results[2]);
//						String writer_result = "";
//						if(results[0].trim().equals("}") || results[0].trim().equals("{")){
//							writer_result = results[1].trim();
//						}else{
//							writer_result = results[0]+" 999 "+results[1];
//						}
//						
//						if(writer_result.equals("}") || writer_result.equals("};") || writer_result.equals("{")){
//							continue;
//						}
//						
//						writer1.append(writer_result);
//						writer1.newLine();;
//						writer1.flush();
//						
//						writer2.append(results[2]);
//						writer2.newLine();;
//						writer2.flush();
//						
//						number++;
//						if(number == 100000){
//							return;
//						}
//					}
					
//					String method = new ASTtoDOT().getCarveredMethod(m);
					String name = new ASTtoDOT().getMethodName(m);
					if(name.equals("-1") || name.equals("main")){
						continue;
					}
					
					String annotation = new ASTtoDOT().getAnnotation(m);
					List<String> list = new ASTtoDOT().changeOtherType(m, method,imports_list);
					String list_string = "";
					for(String str:list){
						list_string = list_string + str + " ";
					}
					if(list_string.equals("") || list_string.equals(" ") || list_string.equals(null)){
						list_string = "no api";
					}
					
					BufferedReader reader = new BufferedReader(new FileReader(new File(FilePath)));
					//������ŵ��ļ�����Ϊ������,Ҫ��ȡ
					String line = null;
					while((line = reader.readLine()) != null){
						if(line.contains("package")){
							break;
						}
					}
					if(line == null){
						line = "";
					}
					String packsgeName = "";
					if(!line.contains("package")){
						packsgeName = "packageName";
					}else{
						//����ÿ��������ŵ��ļ���
						try{
							packsgeName = line.substring(line.indexOf("package")).split(" ")[1];
						}catch(Exception e){
							packsgeName = "packageName";
						}
						
					}
					
					try{
						String fileName = (index+1)+"&&"+packsgeName+"&&"+packName+"&&"+name+".java";
						
						//������ɣ�д�ļ�
						/*
						 * 
						 * BufferedWriter writer1 = new BufferedWriter(new FileWriter(new File("D:/162API.txt")));
							BufferedWriter writer2 = new BufferedWriter(new FileWriter(new File("D:/162annotation.txt")));
						BufferedWriter writer3 = new BufferedWriter(new FileWriter(new File("D:/162index.txt")));
		*/
						File file = new File(saveDIR+"/"+fileName);
						BufferedWriter writer = new BufferedWriter(new FileWriter(file));
						
						index++;
						nameWriter.append(index+"��"+name);
						nameWriter.newLine();
						nameWriter.flush();
						
						writer1.append(index+"��"+list_string);
						writer1.newLine();
						writer1.flush();
						
						writer2.append(index+"��"+annotation);
						writer2.newLine();
						writer2.flush();
						
						writer3.append(index+"��"+file.getAbsolutePath());
						writer3.newLine();
						writer3.flush();
						
						writer.append(method);
						writer.flush();
						writer.close();
					}catch(Exception e){
						continue;
					}
					
				}
				
			}
			
		}
		System.out.println("200����Ŀ������Ϊ��" + totalLineNum);
		
	}
}
