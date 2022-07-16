import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.Objects;

public class LIb_apkFunctions {
    static String parsePackageAndMainActivity(String manifest){
        String PackageName = "";
        String ApplicationClass ="";
        String MainActivity = "";

        File inputFile = new File(manifest);
        if(!inputFile.exists()){return null;}
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputFile);
            doc.getDocumentElement().normalize();

            PackageName = doc.getDocumentElement().getAttribute("package");
            ApplicationClass =  doc.getElementsByTagName("application").item(0).getAttributes().getNamedItem("android:name").getNodeValue();

            NodeList nList = doc.getElementsByTagName("action");
            Node nNode;
            for (int temp = 0; temp < nList.getLength(); temp++) {
                nNode = nList.item(temp);
                if(Objects.equals(nNode.getAttributes().getNamedItem("android:name").getNodeValue(), "android.intent.action.MAIN")){
                    MainActivity = nNode.getParentNode().getParentNode().getAttributes().getNamedItem("android:name").getNodeValue();
                    break;
                }
            }
            return PackageName + ";" + MainActivity + ";" + ApplicationClass;
            /*
            nList = doc.getElementsByTagName("activity");
            System.out.println("\n----------Activities------------------");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                nNode = nList.item(temp);
                System.out.println(nNode.getAttributes().getNamedItem("android:name").getNodeValue());
            }


            nList = doc.getElementsByTagName("service");
            System.out.println("\n----------Services------------------");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                nNode = nList.item(temp);
                System.out.println(nNode.getAttributes().getNamedItem("android:name").getNodeValue());
            }

            nList = doc.getElementsByTagName("provider");
            System.out.println("\n----------provider------------------");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                nNode = nList.item(temp);
                System.out.println(nNode.getAttributes().getNamedItem("android:name").getNodeValue());
            }

            nList = doc.getElementsByTagName("uses-permission");
            System.out.println("\n----------uses-permission------------------");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                nNode = nList.item(temp);
                System.out.println(nNode.getAttributes().getNamedItem("android:name").getNodeValue());
            }

            nList = doc.getElementsByTagName("package");
            System.out.println("\n----------Queries------------------");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                nNode = nList.item(temp);
                System.out.println(nNode.getAttributes().getNamedItem("android:name").getNodeValue());
            }

            nList = doc.getElementsByTagName("receiver");
            System.out.println("\n----------BCR------------------");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                nNode = nList.item(temp);
                System.out.println(nNode.getAttributes().getNamedItem("android:name").getNodeValue());
            }
            System.out.println("----------------------------");*/
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }

    }
}
