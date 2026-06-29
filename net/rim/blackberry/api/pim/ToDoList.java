package net.rim.blackberry.api.pim;

import java.util.Enumeration;

public interface ToDoList extends PIMList {
   ToDo createToDo();

   ToDo importToDo(ToDo var1);

   void removeToDo(ToDo var1);

   Enumeration items(long var1, long var3);
}
