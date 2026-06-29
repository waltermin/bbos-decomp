package net.rim.device.api.ui.component;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.util.EmoticonStringPattern;

class SmileySymbolScreen extends SymbolScreen {
   private static SymbolScreen _smileyScreen;
   private static EmoticonStringPattern _smileyFacility;

   public static void show(EmoticonStringPattern smileyFacility, Field field) {
      _smileyFacility = smileyFacility;
      Application.getApplication().invokeLater(new SmileySymbolScreen$1(field));
   }

   private SmileySymbolScreen() {
   }

   protected static SymbolScreen getSymbolScreen() {
      if (_smileyScreen == null) {
         _smileyScreen = new SmileySymbolScreen();
      }

      return _smileyScreen;
   }

   @Override
   protected SymbolScreen$SymbolField getSymbolField() {
      return new SmileySymbolScreen$SmileySymbolField(this);
   }
}
