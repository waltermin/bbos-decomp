package net.rim.device.apps.internal.browser.util;

import net.rim.device.apps.internal.browser.common.UserAbortException;

public final class Asserts {
   public static final void productionAssert(boolean value) {
      if (!value) {
         throw new Object();
      }
   }

   public static final void productionStateAssert(boolean value) {
      if (!value) {
         throw new Object();
      }
   }

   public static final void productionArgumentAssert(boolean value) {
      if (!value) {
         throw new Object();
      }
   }

   public static final void productionUserAbortAssert(boolean value) {
      if (!value) {
         throw new UserAbortException();
      }
   }
}
