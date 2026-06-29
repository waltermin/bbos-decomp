package net.rim.blackberry.api.pdap;

import javax.microedition.pim.RepeatRule;
import javax.microedition.pim.ToDo;

public interface BlackBerryToDo extends ToDo {
   int REMINDER = 20000927;

   RepeatRule getRepeat();

   void setRepeat(RepeatRule var1);
}
