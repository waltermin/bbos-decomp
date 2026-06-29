package net.rim.device.internal.util;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.WeakReferenceUtilities;
import net.rim.vm.WeakReference;

public final class StringUtilitiesInternal {
   private static final long SCRATCH_KEY = -307649977564900118L;
   private static WeakReference _scratchBufferWR;

   private StringUtilitiesInternal() {
   }

   public static final StringBuffer getScratchBuffer() {
      return WeakReferenceUtilities.getStringBuffer(_scratchBufferWR);
   }

   static {
      ApplicationRegistry reg = ApplicationRegistry.getApplicationRegistry();
      _scratchBufferWR = (WeakReference)reg.get(-307649977564900118L);
      if (_scratchBufferWR == null) {
         _scratchBufferWR = new WeakReference(null);

         try {
            reg.put(-307649977564900118L, _scratchBufferWR);
            return;
         } catch (IllegalArgumentException var2) {
         }
      }
   }
}
