package net.rim.device.apps.internal.profiles.tunes;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.profiles.TuneManager;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.profiles.AlertConfiguration;
import net.rim.device.apps.internal.profiles.Profile;
import net.rim.device.apps.internal.profiles.Profiles;
import net.rim.device.internal.io.file.FileUtilities;

final class TuneManagerImpl$SetPhoneTuneVerb extends Verb {
   TuneManagerImpl$SetPhoneTuneVerb() {
      super(1573120, -8257849857544742476L, "net.rim.device.apps.internal.resource.Tunes", 8);
   }

   @Override
   public final Object invoke(Object context) {
      String tuneName = (String)ContextObject.get(context, 2765042845091913199L);
      if (tuneName != null) {
         tuneName = FileUtilities.getFileName(tuneName);
         TuneManager tuneManager = TuneManager.getTuneManager();
         if (tuneManager.isTuneAvailable(tuneName)) {
            Profiles profiles = Profiles.getInstance();
            int numProfiles = profiles.size();

            for (int i = 0; i < numProfiles; i++) {
               Profile profile = (Profile)profiles.getAt(i);
               AlertConfiguration config = (AlertConfiguration)profile.getConfiguration(
                  -2870941457036655797L, 2868625504212929964L + PhoneUtilities.getCurrentLineId()
               );
               config.setTuneName(tuneName, true);
               config.setTuneName(tuneName, false);
               profiles.commitChanges(profile, true);
            }

            ResourceBundle rb = ResourceBundle.getBundle(-8257849857544742476L, "net.rim.device.apps.internal.resource.Tunes");
            Dialog.alert(MessageFormat.format(rb.getString(9), new Object[]{FileUtilities.getDisplayName(tuneName)}));
         }
      }

      return null;
   }
}
