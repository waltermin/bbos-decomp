package net.rim.tid.im;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.tid.im.ui.Lookup;

public class MultitapInputMethod extends SLInputMethod {
   public MultitapInputMethod(Locale[] locales) {
      super(ResourceBundle.getBundle(-6325836987686413173L, "net.rim.tid.im.options.fastEuropean.FastEuropeanOptions"), new SLInputMethod$IMOptions(3, 1, 5, 2));
      super.availableLocals = locales;
      this.createComposedText(2, 50);
   }

   @Override
   protected String getConvEngineName() {
      return "net.rim.tid.im.conv.europe.DirectEuropeanConv";
   }

   @Override
   public Object getViewComponent(int aComponent) {
      if (aComponent == 3) {
         if (SLInputMethod.lookup.get() == null) {
            SLInputMethod.lookup.set(Lookup.getInstance(this, this.getLocale(), 0));
         }

         return SLInputMethod.lookup.get();
      } else {
         return super.getViewComponent(aComponent);
      }
   }

   protected boolean onControlEnter() {
      return false;
   }

   @Override
   public void setDirectInputState(boolean state) {
   }
}
