package javax.microedition.lcdui;

import net.rim.device.api.ui.text.NumericTextFilter;

class MIDPEditField$1 extends NumericTextFilter {
   @Override
   public boolean validate(char character) {
      return character != '.' && character != '-' ? super.validate(character) : true;
   }
}
