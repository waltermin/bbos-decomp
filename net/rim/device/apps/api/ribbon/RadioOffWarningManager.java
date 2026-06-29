package net.rim.device.apps.api.ribbon;

import net.rim.device.api.system.ApplicationRegistry;

public class RadioOffWarningManager {
   protected static final long KEY;
   public static final int RADIO_OFF_SCHEDULED;
   public static final int RADIO_OFF_CANCELLED;
   public static final int RADIO_OFF_POINT_OF_NO_RETURN;

   public static RadioOffWarningManager getInstance() {
      return (RadioOffWarningManager)ApplicationRegistry.getApplicationRegistry().get(2322924269272447401L);
   }

   public void addListener(RadioOffWarningManager$Listener _1) {
      throw null;
   }

   public void removeListener(RadioOffWarningManager$Listener _1) {
      throw null;
   }
}
