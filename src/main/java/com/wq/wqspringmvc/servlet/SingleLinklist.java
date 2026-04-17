package com.wq.wqspringmvc.servlet;

//单向链表
public class SingleLinklist {
    private Node head = new Node(666,null);//头指针

    //节点类（内部类）
    private static class Node{
        int value;//值
        Node next;//下一个节点的指针
        public Node(int value, Node next){
            this.value = value;
            this.next = next;
        }
    }
    public void addFirst(int value){
        //1 链表为null
        head = new Node(value,null);
        //2 链表非null
        head = new Node(value,head);
    }
    public void loop(){
        Node pointer = head;//指针 （node类型） 初始值指向head
        while(pointer != null){
            System.out.println(pointer.value);
            pointer = pointer.next;
        }
    }
    public void loop2(){
        for (Node p = head; p != null; p = p.next){
            System.out.println(p.value);
        }
    }
    private Node findLast(){
        Node p;
        for (p = head; p.next != null; p = p.next){
        }
        return p;
    }
    public void addLast(int value){
        Node last = findLast();
        last.next = new Node(value ,null);
    }



    public Node findNode(int index){
        int i = -1;
        for (Node p = head; p.next != null; p = p.next,i++){
            if(i == index){
                return p;
            }
        }return null;

    }
    public  void  insert(int index,int value) throws IllegalAccessException {
        Node prev = findNode(index - 1);
        if (prev == null){//不合法
            throw new IllegalAccessException(String.format("不合法",index));
        }
        prev.next = new Node(value, prev.next);
    }
    public void removeFirst(){
        if (head == null){
            return;
        }
        head = head.next;
    }
    public void removeNode(int index){
        if (index == 0){
            removeFirst();
        }
        Node prev = findNode(index - 1);
        if (prev == null){
            return;
        }
        Node removedNode = prev.next;
        if (removedNode == null){
            return;
        }
        prev.next = removedNode.next;
    }

}


