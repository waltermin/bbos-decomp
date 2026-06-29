package net.rim.blackberry.api.pdap;

import javax.microedition.pim.RepeatRule;
import javax.microedition.pim.ToDo;

public interface BlackBerryToDo extends ToDo {
   int REMINDER;

   RepeatRule getRepeat();

   void setRepeat(RepeatRule var1);
}
