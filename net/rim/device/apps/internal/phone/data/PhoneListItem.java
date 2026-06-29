package net.rim.device.apps.internal.phone.data;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;

public interface PhoneListItem extends PersistableRIMModel, VerbProvider {
   boolean canSpeedDial();

   char getSpeedDialKey();

   int paint(Graphics var1, int var2, int var3, int var4, int var5, Object var6, PhoneListView var7, int var8);

   @Override
   Verb getVerbs(Object var1, Verb[] var2);

   Field getHintField();

   CallerIDInfo getCallerIDInfo();

   boolean isLongRunningDelete();
}
