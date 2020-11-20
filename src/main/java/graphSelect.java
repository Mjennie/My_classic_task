import com.ibm.wala.classLoader.ShrikeBTMethod;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.cha.CHACallGraph;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

public class graphSelect {
    private ArrayList<String> class_result; //储存被选择测试类
    private ArrayList<String> method_result; //储存被选择测试方法
    private ArrayList<CGNode> father;  //储存调用节点
    private ArrayList<CGNode> son;    //储存被调用节点
    private ArrayList<String> result; // 依赖类结果
    private ArrayList<String> methodresult;//依赖方法结果
    private ArrayList<CGNode> alreadyClass;
    private ArrayList<CGNode> alreayMethod;
    private File infotxt;
    private CHACallGraph cg;

    public graphSelect(CHACallGraph cg,String infoPath,String dirName,String kind) throws IOException {
        this.class_result=new ArrayList<String>();
        this.method_result=new ArrayList<String>();
        this.father=new ArrayList<CGNode>();
        this.son=new ArrayList<CGNode>();
        this.result=new ArrayList<String>();
        this.methodresult=new ArrayList<String>();
        this.alreadyClass=new ArrayList<CGNode>();
        this.alreayMethod=new ArrayList<CGNode>();
        this.infotxt=new File(infoPath); //建立change_info文件
        this.cg=cg;

        getRelation();
        if(kind.equals("-c")){
            getSelectClass();
            printSelectClass(dirName);
        }
        if(kind.equals("-m")){
            getSelectMethod();
            printSelectMethod(dirName);
        }
        //printClassDot(dirName);
        //printMethodDot(dirName);


    }

    public void getRelation(){
        for(CGNode node:cg) {
            if (node.getMethod() instanceof ShrikeBTMethod) {
                ShrikeBTMethod method = (ShrikeBTMethod) node.getMethod();
                if ("Application".equals(method.getDeclaringClass().getClassLoader().toString())) {
                   // String classInnerName = method.getDeclaringClass().getName().toString();
                    //String signature = method.getSignature();
                   // String mark = classInnerName + " " + signature;
                    Iterator<CGNode> preNodes = cg.getPredNodes(node);
                    if (preNodes.hasNext()) {
                        while (preNodes.hasNext()) {
                            CGNode n = preNodes.next();
                            ShrikeBTMethod premethod = (ShrikeBTMethod) n.getMethod();
                            if ("Application".equals(premethod.getDeclaringClass().getClassLoader().toString())) {
                                father.add(node);
                                son.add(n);
                            }
                        }
                    } else {
                        father.add(node);
                        son.add(node);
                    }
                }
            }
        }
    }

    public void getSelectClass() throws IOException {
        for(CGNode node:cg) {
            if (node.getMethod() instanceof ShrikeBTMethod) {
                ShrikeBTMethod method = (ShrikeBTMethod) node.getMethod();
                if ("Application".equals(method.getDeclaringClass().getClassLoader().toString())) {
                    String classInnerName = method.getDeclaringClass().getName().toString();
                    String signature = method.getSignature();
                    String mark = classInnerName + " " + signature;
                    boolean isChange = isInfo(mark);
                    if (isChange) {
                        //找类级变化测试方法
                        for (int i = 0; i < father.size(); i++) {
                            CGNode n = father.get(i);
                            if (node.getMethod().getDeclaringClass().toString().equals(n.getMethod().getDeclaringClass().toString())) {
                                    alreadyClass.add(n);
                                    if (son.get(i).getMethod().getDeclaringClass().toString().contains("Test")) {
                                        String classname = son.get(i).getMethod().getDeclaringClass().getName().toString();
                                        String classsign = son.get(i).getMethod().getSignature();
                                        if (!classsign.contains("<init>")) {
                                            String selectTestClass = classname + " " + classsign;
                                            if (!class_result.contains(selectTestClass)) {
                                                class_result.add(selectTestClass);
                                            }
                                        }
                                        if(!alreadyClass.contains(son.get(i))) {
                                            findClasstest(son.get(i));
                                        }
                                    }/*else {
                                        if (!alreadyClass.contains(son.get(i))) {
                                            findClass(son.get(i));
                                        }
                                    }*/
                                }
                            }
                        }
                    }
                }
            }


    }

    public void getSelectMethod() throws IOException {
        for(CGNode node:cg) {
            if (node.getMethod() instanceof ShrikeBTMethod) {
                ShrikeBTMethod method = (ShrikeBTMethod) node.getMethod();
                if ("Application".equals(method.getDeclaringClass().getClassLoader().toString())) {
                    String classInnerName = method.getDeclaringClass().getName().toString();
                    String signature = method.getSignature();
                    String mark = classInnerName + " " + signature;
                    boolean isChange = isInfo(mark);
                    if (isChange) {
                        for(int i=0;i<father.size();i++){
                            CGNode n=father.get(i);
                            if(node.getMethod().getDeclaringClass().toString().equals(n.getMethod().getDeclaringClass().toString())&&
                                    node.getMethod().getSignature().equals(n.getMethod().getSignature())){
                                alreayMethod.add(n);
                                if(son.get(i).getMethod().getDeclaringClass().toString().contains("Test")) {
                                    String classname = son.get(i).getMethod().getDeclaringClass().getName().toString();
                                    String classsign = son.get(i).getMethod().getSignature();
                                    if((!classsign.contains("<init>"))&&(!classsign.contains("initialize"))) {
                                        String selectTestClass = classname + " " + classsign;
                                        if (!method_result.contains(selectTestClass)) {
                                            method_result.add(selectTestClass);
                                        }
                                    }
                                }else{
                                    if(!alreayMethod.contains(son.get(i))) {
                                         findMethod(son.get(i));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void printSelectClass(String dirName) throws IOException {
        /**String outPath=dirName+"class.txt";
        BufferedWriter bw=new BufferedWriter(new FileWriter(outPath));
        for(String s:class_result){
            bw.write(s);
            bw.newLine();
            bw.flush();
        }
        bw.close();*/
        for(String s:class_result){
            System.out.println(s);
        }
    }

    public void printSelectMethod(String dirName) throws IOException {
        /**String outmePath=dirName+"method.txt";
        BufferedWriter bw=new BufferedWriter(new FileWriter(outmePath));
        for(String s:method_result){
            bw.write(s);
            bw.newLine();
            bw.flush();
        }
        bw.close();*/
        for(String s:method_result){
            System.out.println(s);
        }
    }

    public void printClassDot(String dirName) throws IOException {
        for(int i=0;i<father.size();i++){
            String fa=father.get(i).getMethod().getDeclaringClass().getName().toString();
            String so=son.get(i).getMethod().getDeclaringClass().getName().toString();
            if(fa.contains("Test")&&so.contains("Test")){
                continue;
            }else {
                String n = "\t"+'"' + fa + '"' + " -> " + '"' + so + '"' + ';';
                if (!result.contains(n)) {
                    result.add(n);
                }
            }
        }
        String dotName="class-"+dirName+"-cfa.dot";
        BufferedWriter bw=new BufferedWriter(new FileWriter(dotName));
        bw.write("digraph "+dirName.toLowerCase()+"_class {");
        bw.newLine();
        for(String s:result){
            bw.write(s);
            bw.newLine();
            bw.flush();
        }
        bw.write("}");
        bw.newLine();
        bw.close();
    }

    public void printMethodDot(String dirName) throws IOException {
        for(int i=0;i<father.size();i++){
            String fa=father.get(i).getMethod().getSignature();
            String so=son.get(i).getMethod().getSignature();
            if(fa.contains("Test")||so.contains("java/util/Collection")) {
                continue;
            }else if(fa.equals(so)){
                continue;
            }else {
                String n = "\t" + '"' + fa + '"' + " -> " + '"' + so + '"' + ';';
                if (!methodresult.contains(n)) {
                    methodresult.add(n);
                }
            }

        }
        String dotName="method-"+dirName+"-cfa.dot";
        BufferedWriter bw=new BufferedWriter(new FileWriter(dotName));
        bw.write("digraph "+dirName.toLowerCase()+"_class {");
        bw.newLine();
        for(String s:methodresult){
            bw.write(s);
            bw.newLine();
            bw.flush();
        }
        bw.write("}");
        bw.newLine();
        bw.close();
    }

    public void findMethod(CGNode node){
        for(int i=0;i<father.size();i++){
            CGNode n=father.get(i);
            if(node.getMethod().getDeclaringClass().toString().equals(n.getMethod().getDeclaringClass().toString())&&
                    node.getMethod().getSignature().equals(n.getMethod().getSignature())){
                alreayMethod.add(n);
                if(son.get(i).getMethod().getDeclaringClass().toString().contains("Test")) {
                    String classname = son.get(i).getMethod().getDeclaringClass().getName().toString();
                    String classsign = son.get(i).getMethod().getSignature();
                    if(!classsign.contains("<init>")) {
                        String selectTestClass = classname + " " + classsign;
                        if (!method_result.contains(selectTestClass)) {
                            method_result.add(selectTestClass);
                        }
                    }
                }else{
                    if(!alreayMethod.contains(son.get(i))) {
                        findMethod(son.get(i));
                    }
                }
            }
        }

    }

    public void findClass(CGNode node){
        for (int i = 0; i < father.size(); i++) {
            CGNode n = father.get(i);
            if (node.getMethod().getDeclaringClass().toString().equals(n.getMethod().getDeclaringClass().toString())) {
                alreadyClass.add(n);
                if (son.get(i).getMethod().getDeclaringClass().toString().contains("Test")) {
                    String classname = son.get(i).getMethod().getDeclaringClass().getName().toString();
                    String classsign = son.get(i).getMethod().getSignature();
                    if((!classsign.contains("<init>"))&&(!classsign.contains("initialize"))) {
                        String selectTestClass = classname + " " + classsign;
                        if (!class_result.contains(selectTestClass)) {
                            class_result.add(selectTestClass);
                        }
                    }
                }else{
                    /*if(!alreadyClass.contains(son.get(i))){
                        findClass(son.get(i));
                    }*/
                }
            }
        }
    }

    public void findClasstest(CGNode node){
        for (CGNode n:cg) {
            if (node.getMethod().getDeclaringClass().toString().equals(n.getMethod().getDeclaringClass().toString())) {
                alreadyClass.add(n);
                String classname = n.getMethod().getDeclaringClass().getName().toString();
                String classsign = n.getMethod().getSignature();
                if ((!classsign.contains("<init>")) && (!classsign.contains("initialize"))) {
                    String selectTestClass = classname + " " + classsign;
                    if (!class_result.contains(selectTestClass)) {
                        class_result.add(selectTestClass);
                    }
                }

            }
        }
    }

    public boolean isInfo(String mark) throws IOException {
        boolean flag=false;
        InputStreamReader read=new InputStreamReader(new FileInputStream(infotxt),"UTF-8");
        BufferedReader bufferedReader=new BufferedReader(read);
        String line="";
        while((line=bufferedReader.readLine())!=null){
            if(line.equals(mark)){
                flag=true;
                break;
            }
        }
        return flag;
    }
}
