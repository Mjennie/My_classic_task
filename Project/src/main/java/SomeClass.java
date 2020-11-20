import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.util.config.AnalysisScopeReader;

import java.io.File;
import java.io.IOException;

public class SomeClass {

    public AnalysisScope loadClass(String targetPath) throws IOException, InvalidClassFileException {
        File exclusionFile=new File("src/main/resources/exclusion.txt");

        ClassLoader classLoader=SomeClass.class.getClassLoader();

        AnalysisScope scope= AnalysisScopeReader.readJavaScope(
                "scope.txt",
                exclusionFile,
                classLoader
        );

        //获取target目录下的类文件
        String dirPath=targetPath+"\\classes\\net\\mooctest";
        File dir=new File(dirPath);
        String[] dirlist=dir.list();
        String dirfile="";
        for(int i=0;i<dirlist.length;i++) {
            dirfile=dirPath+"\\"+dirlist[i];
            File file = new File(dirfile);
            scope.addClassFileToScope(ClassLoaderReference.Application, file);
        }

        //获取test-class目录下类文件
        String testDirPath=targetPath+"\\test-classes\\net\\mooctest";
        File testfile=new File(testDirPath);
        String[] filelist=testfile.list();
        String listFilePath="";

        for(int i=0;i<filelist.length;i++){
            listFilePath=testDirPath+"\\"+filelist[i];
            File temp=new File(listFilePath);
            scope.addClassFileToScope(ClassLoaderReference.Application,temp);
        }
        return scope;
    }
}