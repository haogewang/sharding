package com.szhq.iemp.reservation;

/**
 * 双向列表
 * @author wanghao
 * @date 2020/1/3
 */
public class DoubleLinkList {
    //头结点
    private Node head;
    //尾节点
    private Node tail;
    //个数
    private int size;


    //链表头新增节点
    public void addHead(Object data){
        Node node = new Node(data);
        if(size == 0){
            head = node;
            tail = node;
            size ++;
        }else {
            node.next = head;
            head = node;
            size ++;
        }
    }

    //链表尾新增节点
    public void addTail(Object data){
        Node node = new Node(data);
        if(size == 0){
            head = node;
            tail = node;
            size ++;
        }else {
            tail.next = node;
            tail = node;
            size ++;
        }
    }

    //删除头部节点，成功返回true，失败返回false
    public boolean deleteHead(){
        if(size == 0){
            return false;
        }
        if(head.next == null){
            head = null;
            tail = null;
        }else{
            head = head.next;
        }
        size--;
        return true;
    }



    private class Node {

        private Object data;

        private Node next;

        public Node(Object data) {
            this.data = data;
        }
    }
}
