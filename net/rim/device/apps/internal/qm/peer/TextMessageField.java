package net.rim.device.apps.internal.qm.peer;

import net.rim.blackberry.api.blackberrymessenger.MessengerContact;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.ActiveRichTextField;
import net.rim.device.api.util.StringPattern;
import net.rim.device.api.util.StringPatternContainer;
import net.rim.device.apps.internal.qm.resource.QmResources;
import net.rim.device.apps.internal.smileys.Smileys;

final class TextMessageField extends MessageField {
   private TextMessage _message;
   private int _statusId;
   int _namelength;
   private boolean _locked;
   private int _cachedWidth = -1;
   private int _cachedHeight = -1;
   private TextMessageField$UnreachableTimer _unreachableTimer = new TextMessageField$UnreachableTimer(this, null);
   public static final String _buzzString = QmResources.getString(9) + '\u200b';
   private static final String _buzzStringInternal = QmResources.getString(9);
   private static StringPattern[] _smileys = new StringPattern[]{Smileys.getSmileyFacility()};
   private static StringPatternContainer _patterns = new StringPatternContainer(_smileys);
   private static StringBuffer _sBuffer = new StringBuffer();
   private static QMTimer _timer = new QMTimer();

   public TextMessageField(TextMessage message) {
      this._message = message;
      this._statusId = this.getStatusResourceId();
      Field field = getStatusBitmapField(this._statusId);
      if (field != null) {
         this.add(field);
      }

      this.setCookie(this._message);
      String name = this.formatDisplayName(message.getSender());
      this._namelength = name != null ? name.length() : 0;
      ActiveRichTextField editField = new ActiveRichTextField(null, null, null, null, null, null, 0, _patterns);
      this.setText(editField, this._message.getText(), name);
      this.add(editField);
      this._locked = PeerApplication.isDeviceLocked();
      if (!message.isIncoming()) {
         ((MessageFieldLookup)PeerApplication.getInstance()._messageFieldLookup).add(message.hashCode(), this);
         this._unreachableTimer.schedule();
      }
   }

   @Override
   public final MessengerMessage getMessage() {
      return this._message;
   }

   private final String formatDisplayName(String displayName) {
      String formatted = null;
      if (Utils.isValidString(displayName)) {
         _sBuffer.setLength(0);
         _sBuffer.append(displayName);
         if (_sBuffer.length() > 4) {
            _sBuffer.insert(2, '\u200b');
         }

         if (_sBuffer.length() > 0) {
            _sBuffer.append(": ");
         }

         formatted = _sBuffer.toString();
      }

      return formatted;
   }

   @Override
   protected final void subpaint(Graphics graphics) {
      if (this._locked) {
         this.update();
         this._locked = false;
      }

      super.subpaint(graphics);
   }

   @Override
   protected final void sublayout(int maxWidth, int maxHeight) {
      super.sublayout(maxWidth, maxHeight);
      if (!this._locked) {
         this._cachedWidth = this.getWidth();
         this._cachedHeight = this.getHeight();
      } else {
         if (this._cachedWidth != -1) {
            this.setExtent(this._cachedWidth, this._cachedHeight);
            this.setVirtualExtent(this._cachedWidth, this._cachedHeight);
         }
      }
   }

   private final void setText(ActiveRichTextField editField, String text, String name) {
      if (text.equals(_buzzStringInternal) || text.equals("<ding>")) {
         editField.setText(
            _buzzString,
            new int[]{0, _buzzString.length()},
            new byte[]{0},
            new Font[]{this.getFont().derive(1)},
            new int[]{16711680, -804651007, 32768, -804651007},
            null
         );
      } else if (name != null && name.length() != 0) {
         editField.setText(name, text, new Font[]{Font.getDefault(), null}, new int[]{getDisplayNameColor(this._message), -1});
      } else {
         editField.setText(text, new int[]{0, text.length()}, new byte[]{0}, new Font[]{this.getFont().derive(1)}, new int[]{40960, -804651005, 1, 2}, null);
      }
   }

   public final void updateStatus(int state) {
      switch (state) {
         case 9:
            break;
         case 10:
         default:
            this.onMessageStateDelivered();
            break;
         case 11:
            this.onMessageStateRead();
      }

      this._message.setState(state);
      int statusId = this.getStatusResourceId();
      if (statusId != this._statusId) {
         this._statusId = statusId;
         Field field = this.getField(0);
         if (field instanceof PeerIconField) {
            PeerIconField peerIconField = (PeerIconField)field;
            peerIconField.setId(statusId);
         }
      }
   }

   private final void onMessageStateDelivered() {
      MessengerContact obj = this._message.getRecipient();
      if (obj instanceof PeerContact) {
         PeerContact contact = (PeerContact)obj;
         if (contact.isAvailable() && contact.isUnreachable()) {
            contact.setPresenceStatus(8);
         }
      }
   }

   private final void onMessageStateRead() {
      if (this._unreachableTimer.isScheduled()) {
         _timer.cancel(this._unreachableTimer);
      }

      MessengerContact obj = this._message.getRecipient();
      if (obj instanceof MessengerContact) {
         PeerContact contact = (PeerContact)obj;
         if (contact.isAvailable() && (contact.isUnreachable() || contact.isBusy())) {
            contact.setPresenceStatus(16384);
         }
      }
   }

   private static final Field getStatusBitmapField(int id) {
      return id > 0 ? new PeerIconField(id) : null;
   }

   private final int getStatusResourceId() {
      return this._message.getState();
   }

   private static final int getDisplayNameColor(TextMessage message) {
      return message.isIncoming() ? 16711680 : 255;
   }

   @Override
   final void update() {
      Object obj = this.getCookie();
      if (obj instanceof TextMessage) {
         TextMessage cookie = (TextMessage)obj;
         ActiveRichTextField field = null;
         String text = null;
         int size = this.getFieldCount();

         for (int index = 0; index < size; index++) {
            Object f = this.getField(index);
            if (f instanceof ActiveRichTextField) {
               field = (ActiveRichTextField)f;
               text = cookie.getText();
            }
         }

         if (field != null) {
            if (this._namelength == 0) {
               this.setText(field, text, null);
            } else if (text.equals(_buzzString)) {
               this.setText(field, "<ding>", "dummy");
            } else {
               this.setText(field, text, this.formatDisplayName(cookie.getSender()));
            }
         }
      }

      this._locked = PeerApplication.isDeviceLocked();
   }

   static final QMTimer access$100() {
      return _timer;
   }

   static final TextMessage access$200(TextMessageField x0) {
      return x0._message;
   }
}
