package mbpcm.customViews;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.io.File;
import java.util.*;

public class FileSystemModel implements TreeModel {
    private final File root;
    private String cachedDirpath = "";
    private File[] list = {};
    private String[] listS = {};
    HashMap<String,String[]> cache = new HashMap<>();
    private final Vector<TreeModelListener> listeners = new Vector<>();
    public FileSystemModel(File rootDirectory) {
        root = rootDirectory;
    }
    public Object getRoot() {
        return root;
    }
    public Object getChild(Object parent, int index) {
        //msg("getChild");
        File directory = (File) parent;
        updateCache2(directory);
        return new TreeFile(directory, listS[index]);
    }

    public int getChildCount(Object parent) {
        //msg("getChildCount");
        File file = (File) parent;
        if (file.isDirectory()) {
            updateCache2(file);
            if (listS != null)
                return listS.length;
        }
        return 0;
    }
    private void updateCache2(File parent){
        String key = parent.getAbsolutePath();
        if(!cache.containsKey(key)){
                int folen=0,filen=0,total=0;
                listS = parent.list();
                File[] folders = parent.listFiles(File::isDirectory);
                File[] files = parent.listFiles(File::isFile);
                if(folders!=null){
                    folen = folders.length;
                }
                if(files!=null){
                    Arrays.sort(files, Comparator.comparingLong(File::length).reversed());
                    filen=files.length;
                }

                listS = new String[folen + filen];

                if(folders!=null){
                    for(int i=0;i<folen;i++){
                        listS[i] = folders[i].getName();
                    }
                }
                if(files!=null){
                    for(int i=0;i<filen;i++){
                        listS[i+folen] = files[i].getName();
                    }
                }
                //cachedDirpath = parent.getAbsolutePath();
                cache.put(key,listS);
        }else{
            listS = cache.get(key);
        }
    }

    public boolean isLeaf(Object node) {
        //msg("isLeaf");
        File file = (File) node;
        return file.isFile();
    }

    public int getIndexOfChild(Object parent, Object child) {
        //msg("getIndexOfChild");
        File directory = (File) parent;
        File file = (File) child;
        String[] children = directory.list();
        for (int i = 0; i < Objects.requireNonNull(children).length; i++) {
            if (file.getName().equals(children[i])) {
                return i;
            }
        }
        return -1;

    }

    public void valueForPathChanged(TreePath path, Object value) {
        //msg("Value For Path Changed");
        File oldFile = (File) path.getLastPathComponent();
        String fileParentPath = oldFile.getParent();
        String newFileName = (String) value;
        File targetFile = new File(fileParentPath, newFileName);
        oldFile.renameTo(targetFile);
        File parent = new File(fileParentPath);
        int[] changedChildrenIndices = {getIndexOfChild(parent, targetFile)};
        Object[] changedChildren = {targetFile};
        fireTreeNodesChanged(path.getParentPath(), changedChildrenIndices, changedChildren);

    }

    private void fireTreeNodesChanged(TreePath parentPath, int[] indices, Object[] children) {
        //msg("Fire Tree Node Changed");
        TreeModelEvent event = new TreeModelEvent(this, parentPath, indices, children);
        Iterator<TreeModelListener> iterator = listeners.iterator();
        TreeModelListener listener = null;
        while (iterator.hasNext()) {
            listener = iterator.next();
            listener.treeNodesChanged(event);
        }
    }

    public void addTreeModelListener(TreeModelListener listener) {
        listeners.add(listener);
    }

    public void removeTreeModelListener(TreeModelListener listener) {
        listeners.remove(listener);
    }

    private static class TreeFile extends File {
        public TreeFile(File parent, String child) {
            super(parent, child);
        }

        public String toString() {
            return getName();
        }
    }
    private void msg(String text){
        System.out.println(System.currentTimeMillis() + " " + text);
    }
}
