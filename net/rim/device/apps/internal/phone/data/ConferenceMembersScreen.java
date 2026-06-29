package net.rim.device.apps.internal.phone.data;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.ObjectListField;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.internal.phone.api.verbs.DialVerb;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

final class ConferenceMembersScreen extends AppsMainScreen {
   ConferenceMembersScreen(PhoneCallModelImpl callLog) {
      super(0);
      this.setTitle((Field)(new Object(PhoneResources.getResourceBundle(), 6314)));
      this.add((Field)(new Object(callLog.getDateTimeString(4))));
      this.add((Field)(new Object(callLog.getDurationString(true))));
      this.add((Field)(new Object()));
      this.add(callLog.getField(new Object(58, 0)));
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      if (this.getLeafFieldWithFocus() instanceof Object) {
         ObjectListField callerIDField = (ObjectListField)this.getLeafFieldWithFocus();
         if (callerIDField.get(callerIDField.getSelectedIndex()) instanceof CallerIDInfo) {
            CallerIDInfo callerID = (CallerIDInfo)callerIDField.get(callerIDField.getSelectedIndex());
            Verb[] verbs = new Object[0];
            callerID.getVerbs(null, verbs);
            menu.add(verbs);

            for (int i = verbs.length - 1; i >= 0; i--) {
               if (verbs[i] instanceof DialVerb) {
                  menu.setDefault(verbs[i]);
                  return;
               }
            }
         }
      }
   }
}
