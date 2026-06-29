package net.rim.device.apps.api.addressbook;

import net.rim.device.apps.api.framework.verb.Verb;

public interface SelectionListener {
   long CONTEXT_ID = 1021178189941494075L;

   boolean select(Object var1, int var2);

   boolean select(Object var1);

   boolean canSelect(Object var1, int var2);

   boolean canSelect(Object var1);

   Object[] getMatches(Object var1);

   Verb getVerbs(Verb[] var1, Object var2, Object var3);

   boolean hasSelectedObject();
}
