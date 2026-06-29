package net.rim.device.apps.internal.sms;

final class ThreadInclusionFilter implements InclusionFilter {
   long _addressHash;

   ThreadInclusionFilter(long addressHash) {
      this._addressHash = addressHash;
   }

   @Override
   public final boolean include(Object item) {
      SMSModel message = (SMSModel)item;
      return Storage.getAddressHash(message._payload.getFirstAddress()) == this._addressHash;
   }
}
