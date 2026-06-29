package net.rim.device.apps.internal.qm.peer;

final class SessionRequestDeniedField extends SessionInfoField {
   public SessionRequestDeniedField(PeerContact contact) {
      super(contact, null);
   }

   @Override
   protected final void setText() {
      super._text = "The invitation has been declined.";
   }
}
