package net.rim.blackberry.api.phone.phonelogs;

public interface PhoneLogListener {
   void callLogAdded(CallLog var1);

   void callLogUpdated(CallLog var1, CallLog var2);

   void callLogRemoved(CallLog var1);

   void reset();
}
