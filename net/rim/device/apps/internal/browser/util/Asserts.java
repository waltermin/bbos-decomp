package net.rim.device.apps.internal.browser.util;

import net.rim.device.apps.internal.browser.common.UserAbortException;

public final class Asserts {
   public static final void productionAssert(boolean value) {
      if (!value) {
         throw new RuntimeException();
      }
   }

   public static final void productionStateAssert(boolean value) {
      if (!value) {
         throw new IllegalStateException();
      }
   }

   public static final void productionArgumentAssert(boolean value) {
      if (!value) {
         throw new IllegalArgumentException();
      }
   }

   public static final void productionUserAbortAssert(boolean value) throws UserAbortException {
      if (!value) {
         throw new UserAbortException();
      }
   }
}
