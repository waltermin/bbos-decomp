package net.rim.device.internal.applicationcontrol;

import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;

final class CachedStackTraceResponse implements Persistable {
   private byte[] _traceHash;
   private long _responseReceivedMask;
   private static final long FLAG;

   CachedStackTraceResponse(byte[] traceHash) {
      this._traceHash = traceHash;
      this._responseReceivedMask = Integer.MIN_VALUE;
   }

   final boolean equals(CachedStackTraceResponse stackPerms) {
      return Arrays.equals(this._traceHash, stackPerms._traceHash);
   }

   final boolean equals(byte[] traceHash) {
      return Arrays.equals(this._traceHash, traceHash);
   }

   final void setAllowed(int allowFlag, int promptFlag, boolean allow) {
      if (allow) {
         this._responseReceivedMask |= Long.MIN_VALUE >>> allowFlag;
         this._responseReceivedMask &= Long.MIN_VALUE >>> promptFlag ^ -1;
      } else {
         this._responseReceivedMask &= Long.MIN_VALUE >>> allowFlag ^ -1;
         this._responseReceivedMask &= Long.MIN_VALUE >>> promptFlag ^ -1;
      }
   }

   final int isAllowed(int allowFlag, int promptFlag) {
      return ApplicationControlImpl.permissionMaskToTriState(this._responseReceivedMask, allowFlag, promptFlag);
   }

   final void reset(int allowFlag, int promptFlag) {
      this._responseReceivedMask |= Long.MIN_VALUE >>> allowFlag;
      this._responseReceivedMask |= Long.MIN_VALUE >>> promptFlag;
   }

   static final int responseToPermission(CachedStackTraceResponse response, int allowFlag, int promptFlag) {
      return response != null ? response.isAllowed(allowFlag, promptFlag) : 2;
   }
}
