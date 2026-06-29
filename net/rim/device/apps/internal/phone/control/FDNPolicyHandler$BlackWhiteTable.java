package net.rim.device.apps.internal.phone.control;

import net.rim.vm.Array;

class FDNPolicyHandler$BlackWhiteTable {
   private NumberPattern[] _patterns;
   private boolean _reject;
   private final FDNPolicyHandler this$0;

   public FDNPolicyHandler$BlackWhiteTable(FDNPolicyHandler _1, String policy) {
      this.this$0 = _1;
      if (policy != null) {
         int len = policy.length();
         if (len > 0) {
            char lastChar = policy.charAt(len - 1);
            if (lastChar == 'r') {
               len--;
               this._reject = true;
            }
         }

         if (len > 0) {
            char lastChar = policy.charAt(len - 1);
            if (lastChar != ';') {
               policy = ((StringBuffer)(new Object())).append(policy.substring(0, len)).append(';').toString();
               len++;
            }
         }

         int start = 0;

         for (int next = policy.indexOf(59, start); next >= start; next = policy.indexOf(59, start)) {
            NumberPattern pattern = new NumberPattern(policy.substring(start, next));
            if (this._patterns == null) {
               this._patterns = new NumberPattern[]{pattern};
            } else {
               int count = this._patterns.length;
               Array.resize(this._patterns, count + 1);
               this._patterns[count] = pattern;
            }

            start = next + 1;
         }
      }
   }

   @Override
   public String toString() {
      String result = "";
      if (this._patterns != null) {
         int count = this._patterns.length;

         for (int idx = 0; idx < count; idx++) {
            result = ((StringBuffer)(new Object())).append(result).append(this._patterns[idx].toString()).append(';').toString();
         }
      }

      if (this._reject) {
         result = ((StringBuffer)(new Object())).append(result).append('r').toString();
      }

      return result;
   }

   public boolean isRejected(String number) {
      if (number != null && this.this$0._myCommandHandler.isEmergencyNumber(number)) {
         return false;
      }

      if (this._patterns != null) {
         int count = this._patterns.length;

         for (int idx = 0; idx < count; idx++) {
            switch (this._patterns[idx].test(number)) {
               case -1:
                  return true;
               case 1:
                  return false;
            }
         }
      }

      return this._reject;
   }
}
