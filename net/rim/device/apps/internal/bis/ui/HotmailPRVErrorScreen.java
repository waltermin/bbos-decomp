package net.rim.device.apps.internal.bis.ui;

import java.util.Hashtable;
import net.rim.device.api.ui.Field;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.api.ui.Button;
import net.rim.device.apps.internal.bis.event.LinkEvent;

public final class HotmailPRVErrorScreen extends UserSettingsScreen {
   public HotmailPRVErrorScreen() {
      super(31);
   }

   @Override
   public final void refresh(Hashtable screenParams) {
      this.setTitle(ApplicationResources.getString(151));
      this.addContentField((Field)(new Object(ApplicationResources.getString(146))));
      this.addContentLineBreak();
      this.addContentField((Field)(new Object(ApplicationResources.getString(147))));
      this.addContentLineBreak();
      Button ok = new Button(ApplicationResources.getString(39));
      this.addContentField(ok);
      LinkEvent okEvent = new LinkEvent(39, 7);
      this.attachEventToField(ok, okEvent);
      this.setDefaultEvent(okEvent);
      this.setCloseEvent(okEvent);
      this.addContentLineBreak();
      this.addContentField((Field)(new Object(ApplicationResources.getString(137))));
      this.setHelp("index.wml");
   }
}
