package net.rim.blackberry.api.pdap;

import javax.microedition.pim.ToDoList;

public interface BlackBerryToDoList extends ToDoList {
   String LIST_NAME = "ToDo List";

   void initialize(int var1);
}
