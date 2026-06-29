package net.rim.blackberry.api.phone;

public class AbstractPhoneListener implements PhoneListener {
   protected AbstractPhoneListener() {
   }

   @Override
   public void callInitiated(int callid) {
   }

   @Override
   public void callWaiting(int callid) {
   }

   @Override
   public void callIncoming(int callId) {
   }

   @Override
   public void callAnswered(int callId) {
   }

   @Override
   public void callConnected(int callId) {
   }

   @Override
   public void callConferenceCallEstablished(int callId) {
   }

   @Override
   public void conferenceCallDisconnected(int callId) {
   }

   @Override
   public void callDisconnected(int callId) {
   }

   @Override
   public void callDirectConnectConnected(int callId) {
   }

   @Override
   public void callDirectConnectDisconnected(int callId) {
   }

   @Override
   public void callEndedByUser(int callId) {
   }

   @Override
   public void callFailed(int callId, int reason) {
   }

   @Override
   public void callResumed(int callId) {
   }

   @Override
   public void callHeld(int callId) {
   }

   @Override
   public void callAdded(int callId) {
   }

   @Override
   public void callRemoved(int callId) {
   }
}
