package net.rim.device.apps.internal.phone.control;

import net.rim.device.internal.callcontrol.CallEventHandler;

class FDNPolicyHandler$FDNEventHandler extends CallEventHandler {
   private final FDNPolicyHandler this$0;

   public FDNPolicyHandler$FDNEventHandler(FDNPolicyHandler _1, int order) {
      super(order);
      this.this$0 = _1;
   }

   @Override
   public void callIncoming(int callId) {
      String number = this.getNumber(callId);
      if (this.this$0._incomingTable != null && this.this$0.isMyLine(callId) && this.this$0._incomingTable.isRejected(number)) {
         System.out.println("FDNPolicy rejected callIncoming.");
         this.rejectCall(callId);
      } else {
         super.callIncoming(callId);
      }
   }

   @Override
   public void callWaiting(int callId) {
      String number = this.getNumber(callId);
      if (this.this$0._incomingTable != null && this.this$0.isMyLine(callId) && this.this$0._incomingTable.isRejected(number)) {
         System.out.println("FDNPolicy rejected callWaiting.");
         this.rejectCall(callId);
      } else {
         super.callWaiting(callId);
      }
   }

   private String getNumber(int callId) {
      try {
         return this.this$0._myCommandHandler.getCallPhoneNumber(callId);
      } finally {
         ;
      }
   }

   private void rejectCall(int callId) {
      try {
         this.this$0._myCommandHandler.rejectCall(callId);
      } finally {
         return;
      }
   }
}
