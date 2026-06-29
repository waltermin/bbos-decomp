package javax.microedition.pim;

import java.util.Enumeration;

public interface EventList extends PIMList {
   int STARTING = 0;
   int ENDING = 1;
   int OCCURRING = 2;

   Event createEvent();

   Event importEvent(Event var1);

   void removeEvent(Event var1);

   Enumeration items(int var1, long var2, long var4, boolean var6);

   int[] getSupportedRepeatRuleFields(int var1);
}
