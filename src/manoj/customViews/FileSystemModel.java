package manoj.customViews;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.io.File;
import java.util.Iterator;
import java.util.Objects;
import java.util.Vector;

public class FileSystemModel implements TreeModel {
    private final File root;
    private String cachedDirpath = "";
    private String[] list = {};
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
        if(!cachedDirpath.equals(directory.getAbsolutePath())){
            list = directory.list();
            cachedDirpath = directory.getAbsolutePath();
        }
        return new TreeFile(directory, list[index]);
    }

    public int getChildCount(Object parent) {
        //msg("getChildCount");
        File file = (File) parent;
        if (file.isDirectory()) {
            if(!cachedDirpath.equals(file.getAbsolutePath())){
                list = file.list();
                cachedDirpath = file.getAbsolutePath();
            }
            if (list != null)
                return list.length;
        }
        return 0;
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
