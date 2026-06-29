package net.rim.device.apps.internal.qm.peer;

final class SessionEndedField extends SessionInfoField {
   public SessionEndedField(PeerContact contact, String application) {
      super(contact, application);
   }

   @Override
   protected final void setText() {
      super._text = "Your session for the application \"" + super._application + "\" has ended.";
   }
}
