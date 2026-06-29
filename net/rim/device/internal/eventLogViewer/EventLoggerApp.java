package net.rim.device.internal.eventLogViewer;

import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.UiApplication;

public final class EventLoggerApp extends UiApplication {
   public static final void main(String[] args) {
      if (args != null && args.length != 0) {
         if (args != null && args.length == 1 && args[0].equals("Execute")) {
            new EventLoggerApp().enterEventDispatcher();
         }
      } else {
         ApplicationDescriptor appDescrip = ApplicationDescriptor.currentApplicationDescriptor();
         args = new String[]{"Execute"};
         appDescrip = new ApplicationDescriptor(appDescrip, args);
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         ar.put(-1723378108824417453L, appDescrip);
      }
   }

   private EventLoggerApp() {
      new EventLoggerContents();
   }
}
