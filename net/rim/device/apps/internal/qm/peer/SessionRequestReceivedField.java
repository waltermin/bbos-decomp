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
      super._text = super._contact.getDisplayName() + " invites you to start " + super._application;
   }

   @Override
   protected final void addFields() {
      this.addRequestText();
      this._yes = new ButtonField("Yes", 12884967424L);
      this._no = new ButtonField("No", 12884967424L);
      this._buttons = new HorizontalFieldManager(12884901888L);
      this._yes.setChangeListener(this);
      this._no.setChangeListener(this);
      this._buttons.add(this._yes);
      this._buttons.add(this._no);
      this.add(this._buttons);
   }

   protected final void addRequestText() {
      RichTextField rtf = new RichTextField(36028797018963968L);
      rtf.setText(
         super._contact.getDisplayName()
            + " has invited you to start the application called \""
            + super._application
            + ".\" Would you like to accept the invitation?"
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
