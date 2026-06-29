package net.rim.device.apps.api.idlescreen;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;

public class IdleScreenManager {
   private static final long KEY = -8431154864071828033L;
   public static final long GUID_IDLESCREEN_DISPLAYED = -8246942408779309615L;
   public static final long GUID_IDLESCREEN_UNDISPLAYED = 6946990846586544061L;

   public static void register(IdleScreenManager manager) {
      ApplicationRegistry.getApplicationRegistry().put(-8431154864071828033L, manager);
   }

   public static IdleScreenManager getInstance() {
      return (IdleScreenManager)ApplicationRegistry.getApplicationRegistry().get(-8431154864071828033L);
   }

   public void show() {
      throw null;
   }

   public void hook(Application _1) {
      throw null;
   }

   public void unhook() {
      throw null;
   }
}
