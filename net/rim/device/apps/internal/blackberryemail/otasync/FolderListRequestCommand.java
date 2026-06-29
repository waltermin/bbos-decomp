package net.rim.device.apps.internal.blackberryemail.otasync;

import net.rim.device.api.util.Persistable;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.transmission.rim.RIMMessagingFolderManagement;

final class FolderListRequestCommand extends RIMMessagingFolderManagement implements Recognizer, Persistable {
   FolderListRequestCommand() {
      this.addFolderListRequest();
   }

   @Override
   public final boolean recognize(Object o) {
      return o instanceof FolderListRequestCommand;
   }
}
