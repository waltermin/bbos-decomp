package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Screen;

final class DocViewTextDisplayField$DocViewPlayScreen$EmbeddedScreenInfo {
   final Screen _screen;
   final Field _imageField;
   boolean _hasBeenInit;
   int _origRotationValue;
   int _origScale;
   int _origTopX;
   int _origTopY;
   private final DocViewTextDisplayField$DocViewPlayScreen this$1;

   DocViewTextDisplayField$DocViewPlayScreen$EmbeddedScreenInfo(DocViewTextDisplayField$DocViewPlayScreen _1, Screen scrInfo, Field imgFld) {
      this.this$1 = _1;
      this._origScale = 65536;
      if (scrInfo != null && imgFld != null) {
         this._screen = scrInfo;
         this._imageField = imgFld;
      } else {
         throw new Object("Null parameters for the rendered screen!");
      }
   }
}
