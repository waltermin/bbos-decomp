package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.ActiveAutoTextEditField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.util.StringPattern;
import net.rim.device.api.util.StringPatternContainer;
import net.rim.device.apps.internal.qm.resource.QmResources;
import net.rim.device.apps.internal.smileys.Smileys;
import net.rim.device.internal.system.InternalServices;

final class NewMessageField extends Manager {
   private SeparatorField _separator;
   private ActiveAutoTextEditField _editField;
   private Object _replyHint;
   private Object _typeHint;
   private boolean _startTyping;
   private VerticalFieldManager _vfm;
   private int _maxHeight;
   private static Tag TAG = Tag.create("bbmessenger-newmessage");

   NewMessageField() {
      super(0);
      this.setTag(TAG);
      this._separator = (SeparatorField)(new Object());
      this.add(this._separator);
      this.onLocaleChanged();
      StringPattern[] smileys = new Object[]{Smileys.getSmileyFacility()};
      StringPatternContainer patterns = (StringPatternContainer)(new Object(smileys));
      this._editField = (ActiveAutoTextEditField)(new Object(null, null, 1000000, 4503599627370496L | (PeerData.isAutoCap() ? 0 : 524288), patterns));
      this._vfm = (VerticalFieldManager)(new Object(1153220571769602048L));
      this._vfm.add(this._editField);
      int height = Font.getDefault().getHeight();
      this._maxHeight = Display.getHeight();
      this._maxHeight = this._maxHeight * 4 / 10;
      int maxLines = this._maxHeight / height;
      this._maxHeight = maxLines * height;
      this.add(this._vfm);
   }

   final void onLocaleChanged() {
      this.removeStartTypingHintIfExists();
      this.removeReplyHintIfExists();
      ResourceBundle _rb = ResourceBundle.getBundle(1758158344049992104L, "net.rim.device.apps.internal.resource.Message");
      String notification = InternalServices.isReducedFormFactor() ? QmResources.getString(68) : QmResources.format(67, _rb.getString(148).toUpperCase());
      this._replyHint = PersistentContent.encode(notification, true, true);
      this._typeHint = PersistentContent.encode(QmResources.getString(66), true, true);
   }

   final void removeReplyHintIfExists() {
      String replyHint = this.getReplyHint();
      if (replyHint != null && this._editField != null && replyHint.equals(this._editField.getText())) {
         this._editField.clear(Integer.MIN_VALUE);
      }
   }

   @Override
   protected final void onFocus(int direction) {
      this.removeReplyHintIfExists();
      PeerApplication application = PeerApplication.getInstance();
      if (application != null
         && application._conversationScreen != null
         && application._conversationScreen.getConversation().size() == 0
         && this._editField.getTextLength() == 0) {
         this._editField.setText(this.getTypeHint());
         this._editField.select(true);
         this._startTyping = true;
      }

      super.onFocus(direction);
   }

   @Override
   protected final void onUnfocus() {
      if (this._editField.getTextLength() == 0) {
         this._editField.insert(this.getReplyHint(), Integer.MIN_VALUE, true, true);
      }

      super.onUnfocus();
   }

   final String getText() {
      return this.isEmpty() ? "" : this._editField.getText();
   }

   final void clear() {
      this._editField.setText("");
   }

   final boolean isEmpty() {
      return this._editField.getTextLength() == 0
         || this.getReplyHint().equals(this._editField.getText())
         || this.getTypeHint().equals(this._editField.getText());
   }

   @Override
   public final boolean isFocus() {
      return this._editField.isFocus();
   }

   final Field getEditField() {
      return this._editField;
   }

   @Override
   protected final void sublayout(int width, int height) {
      this.layoutChild(this._separator, width, height);
      this.setPositionChild(this._separator, 0, 0);
      int separatorHeight = this._separator.getHeight();
      this.layoutChild(this._vfm, width, this._maxHeight - separatorHeight);
      this.setPositionChild(this._vfm, 0, separatorHeight);
      height = this._vfm.getHeight() + separatorHeight;
      this.setExtent(width, height);
   }

   @Override
   protected final boolean trackwheelRoll(int amount, int status, int time) {
      this.removeStartTypingHintIfExists();
      return super.trackwheelRoll(amount, status, time);
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      this.removeStartTypingHintIfExists();
      return super.trackwheelClick(status, time);
   }

   @Override
   protected final boolean keyDown(int keycode, int time) {
      this.removeStartTypingHintIfExists();
      return super.keyDown(keycode, time);
   }

   @Override
   public final int processKeyEvent(int event, char key, int keycode, int time) {
      this.removeStartTypingHintIfExists();
      return super.processKeyEvent(event, key, keycode, time);
   }

   final void removeStartTypingHintIfExists() {
      if (this._startTyping) {
         if (this.getTypeHint().equals(this._editField.getText())) {
            this._editField.setText(null);
         }

         this._startTyping = false;
      }
   }

   private final String getTypeHint() {
      try {
         return PersistentContent.decodeString(this._typeHint);
      } finally {
         ;
      }
   }

   private final String getReplyHint() {
      try {
         return PersistentContent.decodeString(this._replyHint);
      } finally {
         ;
      }
   }

   final void lock() {
      this._replyHint = PersistentContent.reEncode(this._replyHint, true, true);
      this._typeHint = PersistentContent.reEncode(this._typeHint, true, true);
      this._editField.clear(Integer.MIN_VALUE);
   }
}
