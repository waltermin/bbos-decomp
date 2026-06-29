package net.rim.tid.awt.im.spi;

import net.rim.device.api.i18n.Locale;
import net.rim.tid.awt.im.repository.CustomWordsRepository;

public interface InputMethodDescriptor {
   Locale[] getAvailableLocales();

   Locale[] getDisplayLocales();

   boolean hasDynamicLocaleList();

   String getInputMethodDisplayName(Locale var1, Locale var2);

   InputMethod createInputMethod();

   long getInputMethodID();

   boolean forTestOnly();

   CustomWordsRepository getRepository(int var1);

   void disposeCache();
}
