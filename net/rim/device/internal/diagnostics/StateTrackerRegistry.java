package net.rim.device.internal.diagnostics;

import net.rim.device.api.system.ApplicationRegistry;

public class StateTrackerRegistry {
   private static final long ID;
   private static StateTracker _instance = null;

   private StateTrackerRegistry() {
   }

   public static void setStateTracker(StateTracker tracker) {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      ar.put(-5485257615414184727L, tracker);
      _instance = null;
   }

   public static StateTracker getStateTracker() {
      if (_instance == null) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         _instance = (StateTracker)ar.get(-5485257615414184727L);
      }

      return _instance;
   }
}
