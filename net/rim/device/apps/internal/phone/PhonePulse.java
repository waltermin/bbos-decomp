package net.rim.device.apps.internal.phone;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Phone;
import net.rim.device.api.ui.UiApplication;
import net.rim.vm.Memory;

final class PhonePulse implements Runnable {
   private int _maxInvokeCount = 900;
   private static final long PHONE_PULSE_GUID;

   public static final void begin() {
      System.out.println("pulse on");
      Memory.resetLastIdle();
      PhonePulse phonePulse = new PhonePulse();
      setInstance(phonePulse);
      UiApplication.getUiApplication().invokeLater(phonePulse, 1000, false);
   }

   @Override
   public final void run() {
      if (Phone.getInstance().isActive()) {
         Memory.resetLastIdle();
         if (getInstance() != this) {
            System.out.println("pulse replaced");
         } else if (--this._maxInvokeCount > 0) {
            UiApplication.getUiApplication().invokeLater(this, 1000, false);
         } else {
            clearInstance();
            System.out.println("pulse off after timeout");
         }
      } else {
         clearInstance();
         System.out.println("pulse off");
      }
   }

   static final PhonePulse getInstance() {
      return (PhonePulse)ApplicationRegistry.getApplicationRegistry().get(7374499159191503780L);
   }

   private static final void setInstance(PhonePulse phonePulse) {
      ApplicationRegistry.getApplicationRegistry().replace(7374499159191503780L, phonePulse);
   }

   private static final void clearInstance() {
      ApplicationRegistry.getApplicationRegistry().remove(7374499159191503780L);
   }
}
