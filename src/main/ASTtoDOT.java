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

import org.eclipse.jdt.core.dom.ASTNode;
import structure.MyASTNode;
import structure.MyMethodNode;

public class ASTtoDOT {

	@SuppressWarnings({ "static-access", "unused" })
	// �ڶ���������Դ�ļ�·�����������������и�֮����ļ�·��
	public static List<String> ASTtoDotParser(MyMethodNode m) throws FileNotFoundException,
			IOException {
		// �½�һ�����ڴ洢API���е��б�
		List<String> Seqlist = new LinkedList<String>();

		for (MyASTNode mn : m.nodeList) {
			ASTNode astNode = mn.astNode;
			int hashcode = astNode.hashCode();
			int nodeType = astNode.getNodeType();
			int lineNum = mn.lineNum;
			if (astNode.nodeClassForType(nodeType).toString().substring(
							astNode.nodeClassForType(nodeType).toString().lastIndexOf(".") + 1)
					.equals("MethodInvocation")) {
				Seqlist.add(astNode.toString().replace("new ", ""));
				// System.out.println("�����ڵ��ж�"+
				// astNode.toString().replace("new ", "").replace("()", ""));
			}
			String methodDeclaration = "class org.eclipse.jdt.core.dom.VariableDeclarationFragment";
			if(astNode.nodeClassForType(nodeType).toString().equals(methodDeclaration)){
				System.out.println(astNode.toString());
			}
		}
		return null;
	}
	
	/**
	 * �õ�ĳ�������ķ�����
	 * @param m
	 */
	@SuppressWarnings("static-access")
	public String getCarveredMethod(MyMethodNode m){
		String methodDeclaration = "class org.eclipse.jdt.core.dom.MethodDeclaration";
		String method = "";
		for (MyASTNode mn : m.nodeList) {
			ASTNode astNode = mn.astNode;
			int nodeType = astNode.getNodeType();
			
			if(astNode.nodeClassForType(nodeType).toString().equals(methodDeclaration)){
				method = astNode.toString();
				
			}
		}
		return method;
	}
	
	public String getAnnotation(MyMethodNode m){
		String methodDeclaration = "class org.eclipse.jdt.core.dom.MethodDeclaration";
		String method = "";
		for (MyASTNode mn : m.nodeList) {
			ASTNode astNode = mn.astNode;
			int nodeType = astNode.getNodeType();
			if(astNode.nodeClassForType(nodeType).toString().equals(methodDeclaration)){
				method = astNode.toString();
			}
		}
		//�Ѿ��õ�������
		String annotation = "";
		String[] annotations = method.split("\n");
		for(String line:annotations){
			line = line.trim();
			if(line.equals("/**") || line.equals("/*") || line.equals("*")){
				continue;
			}
			annotation = line.replace("*", "").trim();
			break;
		}
		if(annotation.startsWith("@") || annotation.startsWith("private") || annotation.startsWith("public")){
			annotation = "no annotation";
		}
		//System.out.println(annotation+"111111111");
		return annotation;
	}
	
	/**
	 * ���ص����ļ��е��������õ���
	 * @param file
	 * @return
	 * @throws IOException 
	 */
	public List<String> getImportsInAFile(String file) throws IOException{
		List<String> importsList = new ArrayList<String>();
		
		BufferedReader reader = new BufferedReader(new FileReader(new File(file)));
		String lineString = null;
		while((lineString = reader.readLine())!=null){
			if(lineString.contains("import")){
				lineString = lineString.substring(lineString.indexOf("import"));
				if(lineString.split(" ").length < 2 ){
					continue;
				}
				if(!importsList.contains(lineString.split(" ")[1]) && (lineString.split(" ")[1].startsWith("java") || lineString.split(" ")[1].startsWith("org"))){
					importsList.add(lineString.split(" ")[1].replace(";", ""));
				}
			}
		}
		return importsList;
	}
	
	/**
	 * ���ص����ļ��е��������õ���
	 * @param file
	 * @return
	 * @throws IOException 
	 */
	public List<String> getImportsClass(String file) throws IOException{
		List<String> importsList = new ArrayList<String>();
		
		BufferedReader reader = new BufferedReader(new FileReader(new File(file)));
		String lineString = null;
		while((lineString = reader.readLine())!=null){
			if(lineString.contains("import")){
				lineString = lineString.substring(lineString.indexOf("import"));
				if(lineString.split(" ").length < 2 ){
					continue;
				}
				if(!importsList.contains(lineString.split(" ")[1]) && (lineString.split(" ")[1].startsWith("java") || lineString.split(" ")[1].startsWith("org"))){
					lineString = lineString.split(" ")[1].replace(";", "");
					
					importsList.add(lineString.substring(lineString.lastIndexOf(".")+1));
				}
			}
		}
		//System.out.println(importsList);
		importsList.add("Object");
		return importsList;
	}
	
	/**
	 * �����ֻ����������͸�ʽ��
	 * @param m
	 */
	@SuppressWarnings("static-access")
	public String changeBaseType(MyMethodNode m,String carvedmethod){
		List<String> old_list = new ArrayList<String>();
		List<String> new_list = new ArrayList<String>();
		String methodDeclaration = "class org.eclipse.jdt.core.dom.VariableDeclarationFragment";
		for (MyASTNode mn : m.nodeList) {
			ASTNode astNode = mn.astNode;
			int nodeType = astNode.getNodeType();
			
			if(astNode.nodeClassForType(nodeType).toString().equals(methodDeclaration)){
				String line = astNode.toString();
				//System.out.println(line);
				if(!line.contains("=")){
					continue;
				}
				
				//�����������ݴ���
				String sign = line.split("=")[1];
				//boolean�����ݴ���
				if(sign.equals("false") || sign.equals("true")){
					old_list.add(line.split("=")[0]+"="+line.split("=")[1]);
					new_list.add("my_boolean=true");
				}
				//int�����ݴ���
				//System.out.println(sign.toCharArray()[0]);
				char ch = '-';
				if((ch = sign.toCharArray()[0]) >= '0' && (ch = sign.toCharArray()[0]) <= '9' && !sign.contains(".")){
					old_list.add(line);
					new_list.add("i=0");
				}
				//���������ݴ���
				//System.out.println(sign.toCharArray()[0]);
				if((ch = sign.toCharArray()[0]) >= '0' && (ch = sign.toCharArray()[0]) <= '9' && sign.contains(".")){
					old_list.add(line);
					new_list.add("j=1.0");
				}
				//�ַ��������ݴ���
				//System.out.println(sign.toCharArray()[0]);
				if((ch = sign.toCharArray()[0]) == '"'){
					old_list.add(line);
					new_list.add("str=\"\"");//��ʱ��Ϊ��str=""
				}
				//�ַ������ݴ���
				//System.out.println(sign.toCharArray()[0]);
				if((ch = sign.toCharArray()[0]) == '\''){
					old_list.add(line);
					new_list.add("my_char=''");//��ʱ��Ϊ��str=""
				}
				
				//����Ը����������͵��������ͽ��д���
				//int������
				if(sign.contains("new") && sign.contains("int")){
					old_list.add(line);
					new_list.add("ints=new int[1]");//��ʱ��Ϊ��str=""
				}
				//����������
				if(sign.contains("new") && sign.contains("double")){
					old_list.add(line);
					new_list.add("doubles=new double[1]");//��ʱ��Ϊ��str=""
				}
				if(sign.contains("new") && sign.contains("float")){
					old_list.add(line);
					new_list.add("floats=new float[1]");//��ʱ��Ϊ��str=""
				}
				//**********************����һ��ȫ������    ����  �������滻���⣬����Ҫ��Ҫ���ǣ�����ʱ�����ǣ����ڸ�ֵ�������У�������Ч�������ٿ���
			}
		}
//		System.out.println("old::::"+old_list);
//		System.out.println("new::::"+new_list);
		for(String str:old_list){
			if(carvedmethod.contains(str)){
				int index = old_list.indexOf(str);
				String newStr = new_list.get(index);
			
				carvedmethod = carvedmethod.replace(str, newStr);
			}
		}
//		System.out.println("****************************************************");
//		System.out.println(carvedmethod);
//		System.out.println("****************************************************");
		return carvedmethod;
	}
	
	/**
	 * �Էǻ����������ͽ��д���������
	 * @param m
	 * @param carvedmethod
	 * @return
	 */
	@SuppressWarnings("static-access")
	public List<String> changeOtherType(MyMethodNode m,String carvedmethod,List<String> imports){
		List<String> result = new ArrayList<String>();
		List<String> params = new ArrayList<String>();
		
		//System.out.println(imports);
		String declaration = "class org.eclipse.jdt.core.dom.SingleVariableDeclaration";
		for (MyASTNode mn : m.nodeList) {
			ASTNode astNode = mn.astNode;
			int nodeType = astNode.getNodeType();
			if(astNode.nodeClassForType(nodeType).toString().equals(declaration)){
				String line = astNode.toString();
				//System.out.println(line+"99999999999");
				params.add(line);
			}
		}
		//List<String> base_list = new ArrayList<String>();
		String declaration0 = "class org.eclipse.jdt.core.dom.VariableDeclarationStatement";//�����������������������ͣ�
		for (MyASTNode mn : m.nodeList) {
			ASTNode astNode = mn.astNode;
			int nodeType = astNode.getNodeType();
			
			if(astNode.nodeClassForType(nodeType).toString().equals(declaration0)){
				String line = astNode.toString();
				//System.out.println(line+"3333333333333333");
				//Ҫ�ų��������ͣ�����String��ʱ��Ҫ�ų�
				if(line.contains("=")){
					String new_str = line.split("=")[0];
					if(!params.contains(new_str)){
						//System.out.println(new_str+"5555555555555");
						params.add(new_str);
					}
				}else{
					String new_str = line.replace("\n", "").replace(";", "");
					if(!params.contains(new_str)){
						//System.out.println(new_str+"5555555555555");
						params.add(new_str);
					}
				}
			}
				
		}
		
		List<String> old_list = new ArrayList<String>();
		List<String> old_list1 = new ArrayList<String>();
		
		List<String> new_list = new ArrayList<String>();
		if(!params.isEmpty()){
			for(String str:params){
				if(str.split(" ").length < 2){
					continue;
				}
				String left = str.split(" ")[0];
				String right = str.split(" ")[1];
				old_list.add(right);
				old_list1.add(right);
				new_list.add(left);
				
			}
		}
		//System.out.println(old_list+"oooooo");
		//System.out.println(new_list+"nnnnnnn");
//		System.out.println(carvedmethod);
		List<String> methodList = new ArrayList<String>();
		//List<String> methodList1 = new ArrayList<String>();
		
		String declaration1 = "class org.eclipse.jdt.core.dom.MethodInvocation";
		for (MyASTNode mn : m.nodeList) {
			ASTNode astNode = mn.astNode;
			int nodeType = astNode.getNodeType();
			
			if(astNode.nodeClassForType(nodeType).toString().equals(declaration1)){
				String line = astNode.toString();
				line = line.substring(0,line.lastIndexOf("("));
				//System.out.println(line+"4444444444");
				if(line.contains("(")){ //|| line.contains(")")){
					line = line.substring(0, line.indexOf("("));
				}
				if(line.contains(")")){ //|| line.contains(")")){
					line = line.substring(0, line.indexOf(")"));
				}
				methodList.add(line);
			}
		}
		
//		System.out.println(methodList+"1111111111111");
//		System.out.println(old_list+"2222222222222");
//		System.out.println(new_list+"3333333333333");
		for(String method:methodList){//for(String method:methodList){
			boolean signal = false;
			for(String str:old_list){//(String str:old_list){
				if(method.split("\\.")[0].equals(str.trim())){
					
					int index = old_list.indexOf(str);
					String newStr = new_list.get(index);
					method = method.replace(str, newStr);
					signal = true;
				}
			}
			if(!result.contains(method)){
				result.add(method);
			}
			//System.out.println(method+"666666");
			if(signal){
				continue;
			}
			for(String str:old_list){
				if(str.length() == 1){
					continue;
				}
				if(method.contains(str.trim())){
					
					int index = old_list.indexOf(str);
					String newStr = new_list.get(index);
					method = method.replace(str, newStr);
					//System.out.println(method+"77777777777777777");
					//break;
				}
			}
			
			//System.out.println(method+"77777777777777777");
			if(!result.contains(method)){
				result.add(method);
			}
		}
		//��list������ϴ���ж�
		for(String str:result){
			if(str.contains("this")){
				continue;
			}
			if(!str.contains(".")){
				result.clear();
				break;
			}
			String left = str.split("\\.")[0];
			if(!imports.contains(left)){
				result.clear();
				break;
			}
		}
		
		//System.out.println(result);
		return result;
	}
	
	/**
	 * ��API���д����������滻Ϊ��Ӧ���࣬����Ƿ�ϵͳ�࣬ͬһ�滻ΪMy_Class
	 * @param m
	 * @param carvedmethod
	 * @param imports
	 * @return
	 */
	public String deelMethodInvoke(MyMethodNode m,String carvedmethod,List<String> imports){
		//System.out.println(imports);
		List<String> base_list = new ArrayList<String>();
		List<String> list = new ArrayList<String>();
		
//		String declaration0 = "class org.eclipse.jdt.core.dom.VariableDeclarationStatement";//�����������������������ͣ�
//		for (MyASTNode mn : m.nodeList) {
//			ASTNode astNode = mn.astNode;
//			int nodeType = astNode.getNodeType();
//			
//			if(astNode.nodeClassForType(nodeType).toString().equals(declaration0)){
//				String line = astNode.toString();
//				//Ҫ�ų��������ͣ�����String��ʱ��Ҫ�ų�
//				if(line.startsWith("int") || line.startsWith("boolean") || line.startsWith("char") || line.startsWith("float") || line.startsWith("double")){
//					continue;
//				}else{
//					if(line.contains("=")){
//						String new_str = line.split("=")[0];
//						if(!base_list.contains(new_str)){
//							base_list.add(new_str);
//						}
//					}else{
//						String new_str = line.replace("\n", "").replace(";", "");
//						if(!base_list.contains(new_str)){
//							base_list.add(new_str);
//						}
//					}
//				}
//			}
//				
//		}
//		//System.out.println(base_list);
//		String declaration1 = "class org.eclipse.jdt.core.dom.SingleVariableDeclaration";//��ȡ�����βεĶ�������
//		for (MyASTNode mn : m.nodeList) {
//			ASTNode astNode = mn.astNode;
//			int nodeType = astNode.getNodeType();
//			
//			if(astNode.nodeClassForType(nodeType).toString().equals(declaration1)){
//				String line = astNode.toString();
//				if(!base_list.contains(line)){
//					base_list.add(line);
//				}
//			}
//		}
		//System.out.println(base_list);
		
		//�����滻�������滻Ϊ��
//		if(!base_list.isEmpty()){
//			for(String str:base_list){
//				if(str.split(" ")[1].length() == 1){//�������淶����һ����ĸ
//					continue;
//				}
//				if(carvedmethod.contains(str.split(" ")[1])){
//					carvedmethod = carvedmethod.replace(str.split(" ")[1], str.split(" ")[0]);
//				}
//			}
//		}
		
		//�õ����еķ�������
		String declaration = "class org.eclipse.jdt.core.dom.MethodInvocation";
		for (MyASTNode mn : m.nodeList) {
			ASTNode astNode = mn.astNode;
			int nodeType = astNode.getNodeType();
			
			if(astNode.nodeClassForType(nodeType).toString().equals(declaration)){
				String line = astNode.toString();
				//line = line.substring(0,line.lastIndexOf("("));
				//System.out.println(line+"4444444444");
				list.add(line);
			}
		}
		
		//System.out.println(list);
		//System.out.println(carvedmethod);
		//System.out.println(carvedmethod);
		return carvedmethod;
	}

	/**
	 * �����߰�������ֺ��ַ������д���0����null
	 * @param m
	 * @param carvedmethod
	 * @return
	 */
	public String deelNumber(MyMethodNode m,String carvedmethod){
		String declaration = "class org.eclipse.jdt.core.dom.NumberLiteral";
		String declaration1= "class org.eclipse.jdt.core.dom.StringLiteral";
		String declaration2= "class org.eclipse.jdt.core.dom.CharacterLiteral";
		//����,�ַ������ַ�
		List<String> list = new ArrayList<String>();
		//�ַ�������
		List<String> list1 = new ArrayList<String>();
		//����ֵ���ô���ֱ�Ӵ���char
		List<String> list2 = new ArrayList<String>();
		
		for (MyASTNode mn : m.nodeList) {
			ASTNode astNode = mn.astNode;
			int nodeType = astNode.getNodeType();
			
			if(astNode.nodeClassForType(nodeType).toString().equals(declaration)){
				String line = astNode.toString();
				if(line.equals("0") || list.contains(line)){
					continue;
				}
				list.add(line);
			}
			
			if(astNode.nodeClassForType(nodeType).toString().equals(declaration1)){
				String line = astNode.toString();
				if(list1.contains(line)){
					continue;
				}
				list1.add(line);
			}
			
			if(astNode.nodeClassForType(nodeType).toString().equals(declaration2)){
				String line = astNode.toString();
				if(list2.contains(line)){
					continue;
				}
				list2.add(line);
			}
			
		}
		for(String str:list){
			if(str.contains(".")){
				carvedmethod = carvedmethod.replace(str, "0.0");
			}else{
				carvedmethod = carvedmethod.replace(str, "0");
			}
		}
		//org.eclipse.jdt.core.dom.BooleanLiteral��org.eclipse.jdt.core.dom.CharacterLiteral��org.eclipse.jdt.core.dom.TypeLiteral
		
		for(String str:list1){
			carvedmethod = carvedmethod.replace(str, "\"string\"");
		}
		
		for(String str:list2){
			carvedmethod = carvedmethod.replace(str, "'C'");
		}
		
		return carvedmethod;
	}
	
	
	/**
	 * this̫��,��this�滻Ϊԭ������
	 * @param m
	 * @param carvedmethod
	 * @return
	 */
	public String deelThis(MyMethodNode m,String carvedmethod){
		if(carvedmethod.contains("/**")){
			carvedmethod = "";
			return carvedmethod;
		}
		//class org.eclipse.jdt.core.dom.MethodDeclaration
		String declaration = "class org.eclipse.jdt.core.dom.MethodDeclaration";//�����ĸ�����������
		String line = "";
		for (MyASTNode mn : m.nodeList) {
			ASTNode astNode = mn.astNode;
			int nodeType = astNode.getNodeType();
			
			if(astNode.nodeClassForType(nodeType).toString().equals(declaration)){
				line = astNode.getParent().toString();
				break;
			}
		}
		//�õ�������
		List<String> line_list = new ArrayList<String>();
		String[] lines = line.split("\n");
		for(int i=1;i<lines.length;i++){
			if(lines[i].contains("{")){
				break;
			}
			if(!lines[i].contains(";")){
				break;
			}
			line_list.add(lines[i]);
		}
		//System.out.println(line_list);
		//�õ��������� ����䣬����Ҫ��һ������
		List<String> old_list = new ArrayList<String>();
		List<String> new_list = new ArrayList<String>();
		for(String str:line_list){
			str = str.replace(";", "");
			if(str.contains("=")){
				str = str.split("=")[0];
			}
			str = str.trim();
			//System.out.println(str);
			int index = str.split(" ").length-1;
			old_list.add("this."+str.split(" ")[index]);
			new_list.add(str.split(" ")[index-1]);
		}
//		System.out.println(old_list);
//		System.out.println(new_list);

		if(!old_list.isEmpty()){
			for(String str:old_list){
				int index = old_list.indexOf(str);
				String newStr = new_list.get(index);
			
				carvedmethod = carvedmethod.replace(str, newStr);
			}
		}
		//System.out.println(carvedmethod);
		//��=���߼��ϡ� ���ո�
		if(carvedmethod.contains("=")){
			carvedmethod = carvedmethod.replace("=", " = ");
		}
		if(carvedmethod.contains("! =")){
			carvedmethod = carvedmethod.replace("! =", "!=");
		}
		if(carvedmethod.contains("=  =")){
			carvedmethod = carvedmethod.replace("=  =", "==");
		}
		if(carvedmethod.contains("< =")){
			carvedmethod = carvedmethod.replace("< =", "<=");
		}
		if(carvedmethod.contains("> =")){
			carvedmethod = carvedmethod.replace("> =", ">=");
		}
		
		return carvedmethod;
	}
	
	/**
	 * �õ�������
	 * @param m
	 * @return
	 */
	public String getMethodName(MyMethodNode m){
		String method = "";
		String methodDeclaration = "class org.eclipse.jdt.core.dom.MethodDeclaration";
		for (MyASTNode mn : m.nodeList) {
			ASTNode astNode = mn.astNode;
			int nodeType = astNode.getNodeType();
			
			if(astNode.nodeClassForType(nodeType).toString().equals(methodDeclaration)){
				method = astNode.toString();
			}
		}
		//�Ѿ��õ�������
		String methodNameLine = "";
		String[] lines = method.split("\\n");
		for(int i=0;i<lines.length;i++){
			String lineNow = lines[i];
			if(lineNow.startsWith("public") || lineNow.startsWith("private")){
				methodNameLine = lineNow;
				break;
			}
		}
		if(methodNameLine.equals("")){
			return "-1";
		}
		String[] words = methodNameLine.split(" ");
		String name = "";
		for(String word:words){
			if(word.contains("(")){
				name = word;
			}
		}
		name = name.substring(0, name.indexOf("("));
		return name;
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		File f = new File("testdata/test.java");// ���·������Ҫ���ɳ����﷨����·��
		List<String> imports_list = new ASTtoDOT().getImportsClass("testdata/test.java");
		List<String> imports_list_whole = new ASTtoDOT().getImportsInAFile("testdata/test.java");
		
		ASTGenerator astGenerator = new ASTGenerator(f);
		List<MyMethodNode> methodNodeList = astGenerator.getMethodNodeList();
		
		for (MyMethodNode m : methodNodeList) {//ÿ��m��һ������
			//�õ�������
			String method = new ASTtoDOT().getCarveredMethod(m);
			
			String annotation = new ASTtoDOT().getAnnotation(m);
			System.out.println(annotation+"11111111111");
			//��this���д�����Ҫ���滻Ϊ   ��Ӧ��
			//method = new ASTtoDOT().deelThis(m, method);
			//�����������������滻���ķ�����
			//method = new ASTtoDOT().changeBaseType(m,method);
			//�����ǻ������������滻���ķ�����
			System.out.println(method);
			List<String> list = new ASTtoDOT().changeOtherType(m, method,imports_list);
			//����API����ķ�����
			//method = new ASTtoDOT().deelMethodInvoke(m, method, imports_list_whole);
			System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
			//����������ֵ��ʽ������ �ķ�����
			//method = new ASTtoDOT().deelNumber(m, method);
			//System.out.println(method);
//			int number = method.split("\n").length;
//			System.out.println(number);
			System.out.println("**********************************************");
			
			BufferedReader reader = new BufferedReader(new FileReader(new File("testdata/test.java")));
			//������ŵ��ļ�����Ϊ������,Ҫ��ȡ
			String line = null;
			while((line = reader.readLine()) != null){
				if(line.contains("package")){
					break;
				}
			}
			//����ÿ��������ŵ��ļ���
//			String packsgeName = line.substring(line.indexOf("package"), line.indexOf(";")).split(" ")[1];
//			//System.out.println(className);
//			String[] methodNames = method.substring(0, method.indexOf("\n")).split(" ");
//			String methodName = "";
//			for(String str:methodNames){
//				if(str.contains("(")){
//					methodName = str.substring(0, str.indexOf("("));
//				}
//			}
//			String fileName = packsgeName+"&&"+methodName+".java";
			//������ɣ�д�ļ�
//			BufferedWriter writer = new BufferedWriter(new FileWriter(new File("testdata/"+fileName)));
//			writer.append(method);
//			writer.flush();
//			writer.close();
		}
		
	}
}
