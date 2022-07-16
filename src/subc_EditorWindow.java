import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.util.Objects;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

public class subc_EditorWindow extends RSyntaxTextArea {
    String currentFilePath = "";
    String docText = "";
    Document document;
    StyledDocument sd;
    String rAPIs = "L((javax|java|android|dalvik)+.*);";
    String rInterestings = "\"(.*)\"|-?0x[a-f\\d]{1,8}|#.*\\R"; //String,Constants,Comments
    String rKW = "\\R\\s?\\.\\w*\\s";
    String rMethods = "\\.method.*\\s([\\w\\d]*)\\(.*\\R|\\s(constructor)\\R|\\.end\\s(method)\\R"; //|\\.end method";
    String rSpecialChars = "\\{|}|->|";
    String rLineNo = "\\.line\\w\\d*\\R";
    String rOperations = "return-void|add-double |add-double/2addr |add-float |add-float/2addr |add-int |add-int/2addr |add-int/lit16 |add-int/lit8 |add-long |add-long/2addr |aget |aget-boolean |aget-byte |aget-char |aget-object |aget-short |aget-wide |and-int |and-int/2addr |and-int/lit16 |and-int/lit8 |and-long |and-long/2addr |aput |aput-boolean |aput-byte |aput-char |aput-object |aput-short |aput-wide |array-length |check-cast |cmp-long |cmpg-double |cmpg-float |cmpl-double |cmpl-float |const |const-class |const-string |const-string-jumbo |const-wide |const-wide/16 |const-wide/32 |const-wide/high16 |const/16 |const/4 |const/high16 |div-double |div-double/2addr |div-float |div-float/2addr |div-int |div-int/2addr |div-int/lit16 |div-int/lit8 |div-long |div-long/2addr |double-to-float |double-to-int |double-to-long |execute-inline |fill-array-data |filled-new-array |filled-new-array/range |float-to-double |float-to-int |float-to-long |goto |goto/16 |goto/32 |if-eq |if-eqz |if-ge |if-gez |if-gt |if-gtz |if-le |if-lez |if-lt |if-ltz |if-ne |if-nez |iget |iget-boolean |iget-byte |iget-char |iget-object |iget-object-quick |iget-quick |iget-short |iget-wide |iget-wide-quick |instance-of |int-to-byte |int-to-char |int-to-double |int-to-float |int-to-long |int-to-short |invoke-direct |invoke-direct-empty |invoke-direct/range |invoke-interface |invoke-interface/range |invoke-static |invoke-static/range |invoke-super |invoke-super-quick |invoke-super-quick/range |invoke-super/range |invoke-virtual |invoke-virtual-quick |invoke-virtual-quick/range |invoke-virtual/range |iput |iput-boolean |iput-byte |iput-char |iput-object |iput-object-quick |iput-quick |iput-short |iput-wide |iput-wide-quick |long-to-double |long-to-float |long-to-int |monitor-enter |monitor-exit |move |move-exception |move-object |move-object/16 |move-object/from16 |move-result |move-result-object |move-result-wide |move-wide |move-wide/16 |move-wide/from16 |move/16 |move/from16 |mul-double |mul-double/2addr |mul-float |mul-float/2addr |mul-int |mul-int/2addr |mul-int/lit8 |mul-int/lit16 |mul-long |mul-long/2addr |neg-double |neg-float |neg-int |neg-long |new-array |new-instance |nop |not-int |not-long |or-int |or-int/2addr |or-int/lit16 |or-int/lit8 |or-long |or-long/2addr |rem-double |rem-double/2addr |rem-float |rem-float/2addr |rem-int |rem-int/2addr |rem-int/lit16 |rem-int/lit8 |rem-long |rem-long/2addr |return |return-object |return-void |return-wide |rsub-int |rsub-int/lit8 |sget |sget-boolean |sget-byte |sget-char |sget-object |sget-short |sget-wide |shl-int |shl-int/2addr |shl-int/lit8 |shl-long |shl-long/2addr |shr-int |shr-int/2addr |shr-int/lit8 |shr-long |shr-long/2addr |sparse-switch |sput |sput-boolean |sput-byte |sput-char |sput-object |sput-short |sput-wide |sub-double |sub-double/2addr |sub-float |sub-float/2addr |sub-int |sub-int/2addr |sub-int/lit16 |sub-int/lit8 |sub-long |sub-long/2addr |throw |throw-verification-error |ushr-int |ushr-int/2addr |ushr-int/lit8 |ushr-long |ushr-long/2addr |xor-int |xor-int/2addr |xor-int/lit16 |xor-int/lit8 |xor-long |xor-long/2addr";
    String rRegisters = "v\\d{1,2}|p\\d{1,2}";
    subc_EditorWindow(){
        this.setFont(new Font("Segoe UI",Font.PLAIN,14));
        this.setCurrentLineHighlightColor(Color.BLACK);
        this.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        this.setBackground(new Color(39, 39, 40));
        this.setForeground(Color.GRAY);
    }
    public void setFile(String filepath,boolean highlight){
        if(highlight){
            //determine if smali or xml.
            if(filepath.endsWith(".xml")){
                this.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
            }else if(filepath.endsWith(".smali")){
                //this.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
                this.setSyntaxEditingStyle("text/smali");
            }else if(filepath.endsWith(".java")){
                this.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
            }
        }
        this.setText(utils.file_get_contents(filepath));
        currentFilePath = filepath;
    }
    public void setData(String Data,String type){
            if(Objects.equals(type, "xml")){
                this.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
            }else if(Objects.equals(type, "smali")){
                //this.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
                this.setSyntaxEditingStyle("text/smali");
            }else if(Objects.equals(type, "java")){
                this.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
            }
        this.setText(Data);
        currentFilePath = "";
    }
    /*
    public static void HighlightSyntax(String docText,StyledDocument sd,String regex,int group,Color color,Color back,Boolean bold,Boolean italic){
        long startTime = System.currentTimeMillis();
        SimpleAttributeSet sat = new SimpleAttributeSet();
        StyleConstants.setForeground(sat, color);
        if(back!=null){
            StyleConstants.setBackground(sat,back);
        }
        if(bold){
            StyleConstants.setBold(sat,true);
        }
        if(italic){
            StyleConstants.setItalic(sat,true);
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(docText);
        while (matcher.find()) {
            //System.out.println(matcher.group(1));
            sd.setCharacterAttributes(matcher.start(group), matcher.end(group)- matcher.start(group),sat,false);
        }
        System.out.println((System.currentTimeMillis()-startTime) + " ms\t:\t" + regex);
    }*/
}
