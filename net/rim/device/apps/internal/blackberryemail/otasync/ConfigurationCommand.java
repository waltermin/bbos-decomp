package net.rim.device.apps.internal.blackberryemail.otasync;

import net.rim.device.api.util.Persistable;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.transmission.rim.RIMMessagingFolderManagement;
import net.rim.device.apps.api.transmission.rim.otasync.OTAFMConfiguration;

final class ConfigurationCommand extends RIMMessagingFolderManagement implements Recognizer, Persistable {
   ConfigurationCommand(OTAFMConfiguration configuration) {
      this.addConfiguration(configuration);
   }

   @Override
   public final boolean recognize(Object o) {
      return o instanceof ConfigurationCommand;
   }
}
