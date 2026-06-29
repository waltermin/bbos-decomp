package net.rim.tid.awt.im.spi;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.ui.XYRect;
import net.rim.device.internal.ui.StringBufferGap;
import net.rim.tid.awt.Event;
import net.rim.tid.awt.im.repository.CustomDictionary;
import net.rim.tid.awt.im.repository.CustomWordsRepository;
import net.rim.tid.itie.LinguisticData;

public interface InputMethod {
   void setInputMethodContext(InputMethodContext var1);

   boolean setLocale(Locale var1, int var2);

   boolean setLocale(Locale var1);

   Locale getLocale();

   void setCompositionEnabled(boolean var1);

   boolean isCompositionEnabled();

   void reconvert();

   void dispatchEvent(Event var1);

   void notifyClientWindowChange(XYRect var1);

   void activate();

   void deactivate(boolean var1);

   void hideWindows();

   void removeNotify();

   void endComposition();

   void reset(int var1);

   void dispose();

   Object getControlObject();

   int loadLinguisticData(LinguisticData var1);

   int unloadLinguisticData(int var1);

   int actionPerformed(Object var1, int var2, Object var3);

   boolean isCorrectWord(StringBufferGap var1, int var2, int var3);

   byte[] getIMProperties(byte var1);

   void setIMProperties(byte var1, byte[] var2);

   int setListener(InputModeChangeListener var1);

   InputModeChangeListener getListener();

   CustomWordsRepository getRepository(int var1);

   CustomDictionary getCustomDictionary(int var1);

   int setTextInputStyle(int var1);
}
