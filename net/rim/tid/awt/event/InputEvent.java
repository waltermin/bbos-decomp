package net.rim.tid.awt.event;

import net.rim.tid.awt.Event;
import net.rim.tid.itie.IComponent;

public class InputEvent extends ComponentEvent {
   protected long _when;
   protected int _modifiers;
   public static final int SHIFT_MASK = 1;
   public static final int CTRL_MASK = 2;
   public static final int ALT_MASK = 8;
   public static final int LEFT_MASK = 16;
   public static final int RIGHT_MASK = 32;
   public static final int NOT_FROM_KEYPAD_MASK = 32768;

   public InputEvent(IComponent source, int eventID, long aWhen, int modif, int eMask) {
      super(source, eventID, eMask | Event.INPUT_EVENT_MASK);
      this._when = aWhen;
      this._modifiers = modif;
   }

   public int getModifiers() {
      return this._modifiers;
   }

   public long getWhen() {
      return this._when;
   }

   public void setModifiers(int aModif) {
      this._modifiers = aModif;
   }
}
