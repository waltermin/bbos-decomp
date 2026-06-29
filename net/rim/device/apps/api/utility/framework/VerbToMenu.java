package net.rim.device.apps.api.utility.framework;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.apps.api.framework.verb.DefaultVerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;

public interface VerbToMenu {
   void clear();

   void addVerb(Verb var1);

   void addVerbs(Verb[] var1);

   void getMenu(Menu var1, Object var2);

   void getMenu(Menu var1, Field var2, Object var3);

   Object invoke(Object var1, Object var2);

   void setDefaultVerb(Verb var1);

   void setDefaultVerbPriority(int var1);

   Verb getDefaultVerb();

   int getDefaultVerbPriority();

   Verb getFirstVerbAt(int var1);

   Verb[] getVerbs();

   int size();

   void coalesce(long var1, DefaultVerbProvider var3);
}
