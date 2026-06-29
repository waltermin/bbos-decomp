package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.VerticalFieldManager;

class SessionInfoField extends VerticalFieldManager implements MessengerMessage {
   protected PeerContact _contact;
   protected String _application;
   protected String _text;
   protected long _time;

   protected void setText() {
      throw null;
   }

   protected void addFields() {
      RichTextField rtf = (RichTextField)(new Object(36028797018963968L));
      rtf.setText(this._text);
      this.add(rtf);
   }

   @Override
   public String getSender() {
      return this._contact.getDisplayName();
   }

   @Override
   public long getTime() {
      return this._time;
   }

   @Override
   public boolean isIncoming() {
      return true;
   }

   @Override
   public boolean isSystem() {
      return true;
   }

   @Override
   public String getText() {
      return this._text;
   }

   @Override
   public Field getField(Object context) {
      return this;
   }

   public SessionInfoField(PeerContact contact, String application) {
      this._contact = contact;
      this._application = application;
      this._time = System.currentTimeMillis();
      this.setText();
      this.addFields();
   }
}
