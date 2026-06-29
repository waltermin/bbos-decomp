package net.rim.device.apps.internal.qm.peer;

final class SessionRequestSentField extends SessionInfoField {
   public SessionRequestSentField(PeerContact contact, String application, SessionImpl session) {
      super(contact, application);
   }

   @Override
   protected final void setText() {
      super._text = "You have invited " + super._contact.getDisplayName() + " to start the application called \"" + super._application + ".\"";
   }
}
