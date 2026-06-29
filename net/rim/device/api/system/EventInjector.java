package net.rim.device.api.system;

import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.system.InternalServices;

public final class EventInjector {
   private EventInjector() {
   }

   private static final void assertPermission() {
      ApplicationControl.assertEventInjectorAllowed(true);
   }

   public static final void invokeEvent(EventInjector$Event e) {
      switch (e.getDevice()) {
         case 2:
         case 27:
            try {
               ApplicationControl.assertIdleTimerPermitted(true, CommonResource.getBundle(), 10166);
               InternalServices.resetIdleTime();
            } catch (ControlledAccessException var2) {
            }
         default:
            e.post();
      }
   }
}
