import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.ShrikeBTMethod;
import com.ibm.wala.dataflow.IFDS.LocalPathEdges;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.cha.CHACallGraph;
import com.ibm.wala.ipa.callgraph.impl.AllApplicationEntrypoints;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.ClassHierarchyFactory;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.util.CancelException;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;


public class startClass {

    //ArrayList<String> class_result=new ArrayList<String>();
    public static void main(String args[]) throws IOException, InvalidClassFileException, ClassHierarchyException, CancelException {
        String kind=args[0];
        String targetPath=args[1];
        String infoPath =args[2];

        //生成分析域对象scope
        SomeClass someClass = new SomeClass();
        AnalysisScope scope = someClass.loadClass(targetPath);


        //生成类层次关系对象
        ClassHierarchy cha = ClassHierarchyFactory.makeWithRoot(scope);

        //生成进入点
        Iterable<Entrypoint> eps = new AllApplicationEntrypoints(scope, cha);

        //利用CHA算法构建调用图
        CHACallGraph cg = new CHACallGraph(cha);
        cg.init(eps);

        String[] part=targetPath.split("\\\\");
        String dirName="";
        for(int i=0;i<part.length;i++){
            if(part[i].equals("Data")){
                dirName=part[i+1];
                break;
            }
        }
        dirName=dirName.substring(2);

        graphSelect gs=new graphSelect(cg,infoPath,dirName,kind);

    }



}
