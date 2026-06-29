package net.rim.tid.im.ui;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.ui.XYRect;
import net.rim.tid.im.SLInputMethod;

public class Toolbar {
   SLInputMethod iInputMethod;

   public Toolbar(SLInputMethod im, Locale locale) {
      this.iInputMethod = im;
   }

   public void setLocale(Locale l) {
      this.iInputMethod.setLocale(l);
   }

   public void setLocaleIMCallback(Locale l) {
   }

   public void actionPerformed(int action) {
      this.iInputMethod.actionPerformed(this, action, null);
   }

   public void actionPerformedIMCallback(String action) {
   }

   public void setVisible(boolean isVisible) {
   }

   public boolean isVisible() {
      return false;
   }

   public void setBounds(XYRect bounds) {
   }
}
