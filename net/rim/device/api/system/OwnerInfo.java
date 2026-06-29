package net.rim.device.api.system;

import net.rim.device.internal.deviceoptions.Owner;

public final class OwnerInfo {
   private OwnerInfo() {
   }

   public static final String getOwnerName() {
      return Owner.getOwnerName();
   }

   public static final String getOwnerInformation() {
      return Owner.getOwnerInfo();
   }
}
