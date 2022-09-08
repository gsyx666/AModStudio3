import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class Patcher {
    /*
    * GOAL: easy patch functions.
    * ==FILES==
    * add File(with folder creation)
    * Delete File
    * List Files and Folders(res,src)
    * File Exists.
    * Find..
    *
    * ==SMALI==
    * list fields
    * list methods
    * list implementation
    * list src
    * list superclass
    * addAtMethodStart()
    * addAtMethodEnd()
    * addAfterInstruction()
    * insertField()
    * insertMethod()
    * insertImplements
    * changeExtends
    * addWhenValueIsTypeOf()
    * getReferences
    * getDeclarationClassFile
    * deleteMethod()
    * deleteField()
    *
    * */
    public static void main(String[] args){
        String[] br = new String[]{"abc","cde"};
        JsonObjectBuilder b = Json.createObjectBuilder();
        b.add("name","manoj");
        b.add("age",32);
        b.add("type","teacher");
        JsonObjectBuilder a = Json.createObjectBuilder();
        a.add("name","manoj");
        a.add("age",32);
        a.add("type","teacher");
        a.add("",b);
        JsonObject out = a.build();
        System.out.println(out);
    }
}
