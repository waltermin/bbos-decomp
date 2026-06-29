package net.rim.device.apps.internal.secureemail.cache;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.internal.ui.component.HorizontalSpacerField;
import net.rim.device.internal.ui.component.VerticalSpacerField;

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
      Manager subManager = new HorizontalFieldManager(1152921504606846976L);
      subManager.add(new BitmapField(Bitmap.getPredefinedBitmap(2), 36028797019029504L));
      subManager.add(new HorizontalSpacerField(6));
      subManager.add(new RichTextField(this._errorString));
      if (this._inlineField) {
         VerticalFieldManager vfm = new VerticalFieldManager(1152921504606846976L);
         vfm.add(new VerticalSpacerField(4));
         vfm.add(new SeparatorField());
         vfm.add(subManager);
         vfm.add(new SeparatorField());
         vfm.add(new VerticalSpacerField(4));
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
