package com.szhq.iemp.reservation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wanghao
 * @date 2019/12/9
 */
public class Tree {

    private Object data;
    private List<Tree> childs = new ArrayList<>();

    public Tree(Object data) {
        this.data = data;
        childs = new ArrayList();
        childs.clear();
    }

    public Tree() {
        childs = new ArrayList();
        childs.clear();
    }
    /**
     * 添加子树
     * @param tree
     */
    public void addNode(Tree tree){
        childs.add(tree);
    }


    public boolean isEmpty() {
        if (childs.isEmpty() && data == null) {
            return true;
        }
        return false;
    }

    public List<Tree> getChilds() {
        return childs;
    }
    /**
     * 返回递i个子树
     * @param i
     * @return
     */
    public Tree getChild(int i) {
        return childs.get(i);
    }


    /**
     * 获得根结点的数据
     * @return
     */
    public Object getRootData() {
        return data;
    }


    /**
         * 先根遍历
         * @param root 要的根结点
         */
    public void preOrder(Tree root) {
        if(!root.isEmpty()) {
            visit(root);
            for(Tree child : root.getChilds()) {
                if(child != null) {
                    preOrder(child);
                }
            }
        }
    }
    /**
     * 后根遍历
     * @param root 树的根结点
     */
    public void postOrder(Tree root) {
        if(!root.isEmpty()) {
            for(Tree child : root.getChilds()) {
                if(child != null) {
                    preOrder(child);
                }
            }
            visit(root);
        }
    }

    public void visit(Tree tree) {
        System.out.print("\t" + tree.getRootData());
    }

    public static void main(String[] args) {
        Tree root = new Tree("A");
        root.addNode(new Tree("B"));
        root.addNode(new Tree("C"));
        root.addNode(new Tree("D"));
        Tree t = null;
        t = root.getChild(0);
        t.addNode(new Tree("L"));
        t.addNode(new Tree("E"));
        t = root.getChild(1);
        t.addNode(new Tree("F"));
        t = root.getChild(2);
        t.addNode(new Tree("I"));
        t.addNode(new Tree("H"));

        System.out.println("data:" + root.getRootData());

        Tree tree = new Tree();
        System.out.println("前根遍历：");
        tree.preOrder(root);
        System.out.println("\n后根遍历：");
        tree.postOrder(root);
    }


}
