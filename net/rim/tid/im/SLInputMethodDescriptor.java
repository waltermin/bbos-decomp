package net.rim.tid.im;

import net.rim.device.api.i18n.Locale;
import net.rim.tid.awt.im.repository.CustomWordsRepository;
import net.rim.tid.awt.im.spi.InputMethod;
import net.rim.tid.awt.im.spi.InputMethodDescriptor;

public class SLInputMethodDescriptor implements InputMethodDescriptor {
   String nameOfInputMethod = "";

   @Override
   public Locale[] getDisplayLocales() {
      return this.getAvailableLocales();
   }

   @Override
   public boolean hasDynamicLocaleList() {
      return false;
   }

   @Override
   public synchronized String getInputMethodDisplayName(Locale inputLocale, Locale displayLanguage) {
      return this.nameOfInputMethod;
   }

   @Override
   public boolean forTestOnly() {
      return false;
   }

   @Override
   public CustomWordsRepository getRepository(int type) {
      return null;
   }

   @Override
   public void disposeCache() {
   }

   @Override
   public long getInputMethodID() {
      throw null;
   }

   @Override
   public InputMethod createInputMethod() {
      throw null;
   }

   @Override
   public Locale[] getAvailableLocales() {
      throw null;
   }
}
