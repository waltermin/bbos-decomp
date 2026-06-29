package net.rim.blackberry.api.pim;

import java.util.Enumeration;

public interface EventList extends PIMList {
   int STARTING;
   int ENDING;
   int OCCURRING;

   Event createEvent();

   Event importEvent(Event var1);

   void removeEvent(Event var1);

   Enumeration items(int var1, long var2, long var4, boolean var6);

   int[] getSupportedRepeatRuleFields(int var1);

   int[] getSupportedRepeatRuleFrequencies();
}
