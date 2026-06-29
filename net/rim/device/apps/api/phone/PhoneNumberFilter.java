package net.rim.device.apps.api.phone;

public class PhoneNumberFilter {
   private PhoneNumberFilter _nextFilter;

   final void setNextFilter(PhoneNumberFilter filter) {
      this._nextFilter = filter;
   }

   protected final PhoneNumberFilter getNextFilter() {
      return this._nextFilter;
   }

   public int startCall(String _1, int _2) {
      throw null;
   }
}
