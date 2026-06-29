package net.rim.device.apps.api.idlescreen;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.container.MainScreen;

public class IdleScreenOptionsProvider {
   private static final long KEY;

   public static void register(IdleScreenOptionsProvider provider) {
      ApplicationRegistry.getApplicationRegistry().put(4954117474933020986L, provider);
   }

   public static IdleScreenOptionsProvider getInstance() {
      return (IdleScreenOptionsProvider)ApplicationRegistry.getApplicationRegistry().get(4954117474933020986L);
   }

   public void populateMainScreen(MainScreen _1) {
      throw null;
   }

   public void save() {
      throw null;
   }
}
