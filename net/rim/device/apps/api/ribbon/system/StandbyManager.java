package net.rim.device.apps.api.ribbon.system;

import net.rim.device.api.system.ApplicationRegistry;

public class StandbyManager {
   protected static StandbyManager _cachedManager;
   protected static final long GUID = -4691466650299662765L;

   public static StandbyManager getInstance() {
      if (_cachedManager != null) {
         return _cachedManager;
      }

      ApplicationRegistry reg = ApplicationRegistry.getApplicationRegistry();
      synchronized (reg) {
         _cachedManager = (StandbyManager)reg.get(-4691466650299662765L);
         if (_cachedManager == null) {
            throw new RuntimeException("Standby Manager not initialized");
         }
      }

      return _cachedManager;
   }

   public boolean isEnteringStandby() {
      throw null;
   }

   public boolean isInStandby() {
      throw null;
   }

   public boolean handleKeyDown(int _1) {
      throw null;
   }

   public boolean handleKeyRepeat(int _1) {
      throw null;
   }
}
