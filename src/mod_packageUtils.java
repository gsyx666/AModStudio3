import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class mod_packageUtils extends super_MenuInterface {
    Editor main;
    mod_packageUtils(Editor _mainWin) {
        super(_mainWin);
        main = _mainWin;
        //parsePackageAndMainActivity();
    }

    public static void doSomething(Node node) {
        System.out.println(node.getNodeName() + "  >>  " + node.getAttributes().getNamedItem("android:name"));
        NodeList nodeList = node.getChildNodes();
        for (int i = 0,len = nodeList.getLength(); i < len; i++) {
            Node currentNode = nodeList.item(i);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                doSomething(currentNode);
            }
        }
    }
}
