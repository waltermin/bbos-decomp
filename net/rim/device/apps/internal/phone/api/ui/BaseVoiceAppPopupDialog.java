package net.rim.device.apps.internal.phone.api.ui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Comparator;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.ApplicationKeyInvocableVerb;
import net.rim.device.apps.api.framework.verb.ConditionalVerb;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.phone.PhoneEventListener;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.api.ui.ButtonContainer;
import net.rim.device.apps.api.utility.general.Copyable;
import net.rim.device.internal.ui.component.PopupDialog;

public class BaseVoiceAppPopupDialog extends PopupDialog implements PhoneEventListener, Comparator {
   protected char _initialInputChar;
   protected int _flags;
   protected Object _context;
   protected Verb[] _actions;
   protected ButtonField[] _buttons;
   protected ButtonContainer _buttonContainer;
   protected boolean _shouldClose;
   protected Verb _verbOnClose;
   protected UiApplication _voiceUiApp;
   public static final int LISTEN_FOR_PHONE_EVENTS = 2;
   static final int BUTTON_STYLE = 0;

   protected BaseVoiceAppPopupDialog(char initialInputChar, int flags, boolean globalStatus, Verb[] actions, Object context) {
      super(new VerticalFieldManager(), globalStatus ? 33554432 : 0);
      this._initialInputChar = initialInputChar;
      this._flags = flags;
      this._actions = actions;
      if ((this._flags & 2) != 0) {
         this.startListening();
      }

      if (this._context instanceof ContextObject) {
         this._context = ContextObject.clone(context);
      } else {
         this._context = new ContextObject();
      }

      this.addFields();
      this.addButtons();
      this._voiceUiApp = VoiceServices.getUiApplication();
   }

   protected BaseVoiceAppPopupDialog(int flags, boolean globalStatus, Verb[] actions, Object context) {
      this('@', flags, globalStatus, actions, context);
   }

   protected void addFields() {
      throw null;
   }

   protected final char getInitialInputChar() {
      return this._initialInputChar;
   }

   protected Font getButtonFont() {
      return Font.getDefault();
   }

   protected void addButtons() {
      if (this._actions != null && this._actions.length > 0) {
         Arrays.sort(this._actions, this);
         this._buttons = new ButtonField[this._actions.length];
         this._buttonContainer = new ButtonContainer(0, this.getButtonFont());
         String number = String.valueOf(this._initialInputChar);

         for (int i = 0; i < this._actions.length; i++) {
            Verb verb = this._actions[i];
            if (!(verb instanceof ApplicationKeyInvocableVerb) && (!(verb instanceof ConditionalVerb) || ((ConditionalVerb)verb).canInvoke(number))) {
               this._buttons[i] = this.createButton(verb);
               this._buttonContainer.addButton(this._buttons[i]);
            }
         }

         this._buttonContainer.addButton(new CancelButton());
         this.add(this._buttonContainer);
         this._buttonContainer.setFocus();
      }
   }

   private ButtonField createButton(Verb verb) {
      String label = verb.toString().trim();
      ButtonField button = new ButtonField(label, 0);
      if (verb instanceof Copyable) {
         button.setCookie(((Copyable)verb).copy());
         return button;
      } else {
         button.setCookie(verb);
         return button;
      }
   }

   protected void updateButtons(Object context) {
      boolean updated = false;
      if (this._buttonContainer != null) {
         for (int i = 0; i < this._actions.length; i++) {
            Verb verb = this._actions[i];
            if (!(verb instanceof ApplicationKeyInvocableVerb) && verb instanceof ConditionalVerb) {
               if (((ConditionalVerb)verb).canInvoke(context)) {
                  if (this._buttons[i] == null) {
                     this._buttons[i] = this.createButton(verb);
                     int index = this.getButtonInsertIndex(i);
                     this._buttonContainer.insertButtonAt(this._buttons[i], index);
                     updated = true;
                  }
               } else if (this._buttons[i] != null) {
                  this._buttonContainer.deleteButton(this._buttons[i]);
                  this._buttons[i] = null;
                  updated = true;
               }
            }
         }
      }

      if (updated) {
         this._voiceUiApp.relayout();
      }
   }

   private int getButtonInsertIndex(int i) {
      int index = 0;

      for (int j = 0; j < i; j++) {
         if (this._buttons[j] != null) {
            index++;
         }
      }

      return index;
   }

   protected void invokeVerbAndClose(Verb verb, Object context) {
      this.close(0);
      if (verb != null) {
         verb.invoke(context);
      }
   }

   protected void onButtonClicked(ButtonField button) {
      if (button instanceof CancelButton) {
         this.close(-1);
      } else {
         Object cookie = button.getCookie();
         if (cookie instanceof Verb) {
            this.invokeVerbAndClose((Verb)cookie, this._context);
         } else {
            this.invokeVerbAndClose(null, this._context);
         }
      }
   }

   protected void onContinuationAction() {
      Field focus = this.getLeafFieldWithFocus();
      if (focus instanceof ButtonField) {
         this.onButtonClicked((ButtonField)focus);
      }
   }

   protected boolean onEnterKey() {
      this.onContinuationAction();
      return true;
   }

   protected boolean onEscapeKey() {
      this.close(-1);
      return true;
   }

   @Override
   public boolean trackwheelClick(int status, int time) {
      this.onContinuationAction();
      return super.trackwheelClick(status, time);
   }

   protected void startListening() {
      VoiceServices.addPhoneEventListener(this);
   }

   protected void stopListening() {
      VoiceServices.removePhoneEventListener(this);
   }

   @Override
   protected void close(int reason) {
      this.stopListening();
      super.close(reason);
   }

   @Override
   protected boolean keyDown(int keycode, int time) {
      if (Keypad.key(keycode) == 21) {
         int count = this._actions.length;

         for (int idx = 0; idx < count; idx++) {
            if (this._actions[idx] instanceof ApplicationKeyInvocableVerb) {
               ContextObject.setFlag(this._context, 102);
               this.invokeVerbAndClose(this._actions[idx], this._context);
               return true;
            }
         }

         return false;
      } else {
         return super.keyDown(keycode, time);
      }
   }

   @Override
   protected boolean keyUp(int keycode, int time) {
      return Keypad.key(keycode) == 21 ? true : super.keyUp(keycode, time);
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      switch (key) {
         case '\n':
            return this.onEnterKey();
         case '\u001b':
            return this.onEscapeKey();
         default:
            return super.keyChar(key, status, time);
      }
   }

   protected void onEvent(int eventId, int callId, Object context) {
   }

   @Override
   public void phoneEventNotify(int eventId, int callId, Object context) {
      this.onEvent(eventId, callId, context);
   }

   @Override
   public int compare(Object o1, Object o2) {
      int i1 = ((Verb)o1).getOrdering();
      int i2 = ((Verb)o2).getOrdering();
      return i1 - i2;
   }
}
