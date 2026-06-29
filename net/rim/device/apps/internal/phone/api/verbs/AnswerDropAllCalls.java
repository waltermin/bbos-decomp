package net.rim.device.apps.internal.phone.api.verbs;

import java.util.Vector;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.api.livecall.LiveCall;
import net.rim.vm.Array;

final class AnswerDropAllCalls extends AnswerDropCallConnector {
   public AnswerDropAllCalls(int callId, Object connectionContext) {
      super(callId, connectionContext);
   }

   @Override
   protected final void dropCalls() {
      Vector currentCalls = VoiceServices.getVoiceApplication().getCurrentCalls();
      if (currentCalls != null && currentCalls.size() > 0) {
         super._callIds = this.getAllCallIds(currentCalls);
         super._numCalls = super._callIds.length;
         ContextObject.put(super._connectionContext, 1714907342028355590L, this);
         PhoneUtilities.setPrivateFlag(super._connectionContext, 56);

         for (int callIndex = 0; callIndex < currentCalls.size(); callIndex++) {
            Object call = currentCalls.elementAt(callIndex);
            if (call instanceof LiveCall) {
               LiveCall liveCall = (LiveCall)call;
               liveCall.end(super._connectionContext);
            }
         }
      } else {
         this.answerCall();
      }
   }

   private final int[] getAllCallIds(Vector currentCalls) {
      int[] callIds = new int[0];
      int numCalls = currentCalls.size();

      for (int callIndex = 0; callIndex < numCalls; callIndex++) {
         Object call = currentCalls.elementAt(callIndex);
         if (call instanceof LiveCall) {
            LiveCall liveCall = (LiveCall)call;
            callIds = this.arrayAppend(callIds, liveCall.getCallIds());
         }
      }

      return callIds;
   }

   private final int[] arrayAppend(int[] a1, int[] a2) {
      if (a2 != null) {
         int len2 = a2.length;
         int len1;
         if (a1 == null) {
            len1 = 0;
            a1 = new int[len2];
         } else {
            len1 = a1.length;
            Array.resize(a1, len1 + len2);
         }

         System.arraycopy(a2, 0, a1, len1, len2);
      }

      return a1;
   }
}
