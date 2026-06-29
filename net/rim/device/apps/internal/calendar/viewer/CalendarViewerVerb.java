package net.rim.device.apps.internal.calendar.viewer;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.utility.props.CharProp;
import net.rim.device.internal.i18n.CommonResource;

class CalendarViewerVerb extends Verb implements CharProp {
   private int _displayStringId;
   private ResourceBundle _rb;
   private int _hotkeyResourceId;

   public CalendarViewerVerb(int displayStringId, int order, int hotkeyResourceId) {
      this(null, displayStringId, order, hotkeyResourceId);
   }

   public CalendarViewerVerb(ResourceBundle rb, int displayStringId, int ordering, int hotkeyResourceId) {
      super(ordering);
      this._rb = rb;
      this._displayStringId = displayStringId;
      this._hotkeyResourceId = hotkeyResourceId;
   }

   @Override
   public String toString() {
      if (this._rb == null) {
         String displayString = CalendarApp._rb.getString(this._displayStringId);
         return displayString;
      } else {
         return CommonResource.getString(this._displayStringId);
      }
   }

   @Override
   public char getChar() {
      if (this._hotkeyResourceId <= 0) {
         return '\u0000';
      }

      String str = CalendarApp._rb.getString(this._hotkeyResourceId);
      return str.length() > 0 ? Character.toLowerCase(str.charAt(0)) : '\u0000';
   }

   @Override
   public void setChar(char c) {
   }
}
