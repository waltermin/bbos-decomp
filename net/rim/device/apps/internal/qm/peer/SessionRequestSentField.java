package net.rim.device.apps.internal.qm.peer;

final class SessionRequestSentField extends SessionInfoField {
   public SessionRequestSentField(PeerContact contact, String application, SessionImpl session) {
      super(contact, application);
   }

   @Override
   protected final void setText() {
      super._text = ((StringBuffer)(new Object("You have invited ")))
         .append(super._contact.getDisplayName())
         .append(" to start the application called \"")
         .append(super._application)
         .append(".\"")
         .toString();
   }
}
