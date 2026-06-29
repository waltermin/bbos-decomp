package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.HorizontalFieldManager;

final class SessionRequestReceivedField extends SessionInfoField implements FieldChangeListener {
   private ButtonField _yes;
   private ButtonField _no;
   private HorizontalFieldManager _buttons;
   private SessionImpl _session;

   public SessionRequestReceivedField(PeerContact contact, String application, SessionImpl session) {
      super(contact, application);
      this._session = session;
   }

   @Override
   protected final void setText() {
      super._text = ((StringBuffer)(new Object()))
         .append(super._contact.getDisplayName())
         .append(" invites you to start ")
         .append(super._application)
         .toString();
   }

   @Override
   protected final void addFields() {
      this.addRequestText();
      this._yes = (ButtonField)(new Object("Yes", 12884967424L));
      this._no = (ButtonField)(new Object("No", 12884967424L));
      this._buttons = (HorizontalFieldManager)(new Object(12884901888L));
      this._yes.setChangeListener(this);
      this._no.setChangeListener(this);
      this._buttons.add(this._yes);
      this._buttons.add(this._no);
      this.add(this._buttons);
   }

   protected final void addRequestText() {
      RichTextField rtf = (RichTextField)(new Object(36028797018963968L));
      rtf.setText(
         ((StringBuffer)(new Object()))
            .append(super._contact.getDisplayName())
            .append(" has invited you to start the application called \"")
            .append(super._application)
            .append(".\" Would you like to accept the invitation?")
            .toString()
      );
      this.add(rtf);
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      SessionInfoField responseField = null;
      if (field == this._yes) {
         SessionManager.getInstance().sessionAccepted(super._contact, super._application, this._session.getId());
         this.delete(this._buttons);
      } else if (field == this._no) {
         SessionManager.getInstance().sessionRefused(this._session.getId(), 0);
         this.delete(this._buttons);
         responseField = new SessionRequestDeniedField(super._contact);
      }

      this._session = null;
      PeerApplication.getInstance().newObject(super._contact, responseField);
   }
}
