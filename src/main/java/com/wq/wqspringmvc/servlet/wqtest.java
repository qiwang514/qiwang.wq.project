package com.wq.wqspringmvc.servlet;

public class wqtest {
    public static void main(String[] args) {
        Room room = new Room();
        //System.out.println("Room="+room);
        Room room1 = new Room("wq", 20);
        //System.out.println("Room1="+room1);
        String name = room.getRoomName(1, "x");
        System.out.println(name);
    }
}
class Room{
    private String roomName;
    private int roomCapacity;

    public static void main(String[] args) {
        System.out.println("222");
    }
    public Room(){
        this.roomName = "<UNK>";
        this.roomCapacity = 100;
    }
    public Room(String roomName, int roomCapacity){
        this.roomName = roomName;
        this.roomCapacity = roomCapacity;
    }

    public int getByid(int roomId){
        return this.roomCapacity;
    }
    public String getRoomName(int roomId,String roomName){
        int i = this.getByid(roomId);
        return roomName+i;

    }

    @Override
    public String toString() {
        return "room{" +
                "roomName='" + roomName + '\'' +
                ", roomCapacity=" + roomCapacity +
                '}';
    }
}
