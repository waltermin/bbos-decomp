package net.rim.device.apps.internal.secureemail.cache;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.container.VerticalFieldManager;

public class CachedErrorField extends CachedField {
   private String _errorString;
   private boolean _inlineField;
   private static final int INLINE_SPACER_HEIGHT = 4;

   public CachedErrorField(String errorString) {
      this._errorString = errorString;
      this._inlineField = false;
   }

   public CachedErrorField(String errorString, boolean inlineField) {
      this._errorString = errorString;
      this._inlineField = inlineField;
   }

   @Override
   public void fillManager(Manager manager, Object context) {
      Manager subManager = (Manager)(new Object(1152921504606846976L));
      subManager.add((Field)(new Object(Bitmap.getPredefinedBitmap(2), 36028797019029504L)));
      subManager.add((Field)(new Object(6)));
      subManager.add((Field)(new Object(this._errorString)));
      if (this._inlineField) {
         VerticalFieldManager vfm = (VerticalFieldManager)(new Object(1152921504606846976L));
         vfm.add((Field)(new Object(4)));
         vfm.add((Field)(new Object()));
         vfm.add(subManager);
         vfm.add((Field)(new Object()));
         vfm.add((Field)(new Object(4)));
         subManager = vfm;
      }

      manager.add(subManager);
   }

   @Override
   public void fillStringBuffer(StringBuffer stringBuffer, Object context) {
      stringBuffer.append(this._errorString);
      stringBuffer.append('\n');
   }
}
