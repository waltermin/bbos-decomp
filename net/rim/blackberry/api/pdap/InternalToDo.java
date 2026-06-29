package net.rim.blackberry.api.pdap;

import javax.microedition.pim.ToDoList;

public interface InternalToDo extends BlackBerryToDo {
   void initialize(int var1);

   void initialize(int var1, Object var2, ToDoList var3);

   boolean isInternalModel(Object var1);
}
