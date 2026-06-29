package net.rim.tid.itie;

import java.util.Vector;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.util.Arrays;
import net.rim.tid.awt.im.repository.CustomWordsRepository;
import net.rim.tid.awt.im.spi.InputMethod;
import net.rim.tid.awt.im.spi.InputMethodDescriptor;

public final class IMManager {
   private Vector _descriptors = new Vector();
   private Vector _imethods = new Vector();
   private long _availableIMs;
   private long _latestIMID;

   IMManager() {
      this.addIMDescriptor("Minimal IM", "net.rim.tid.im.MinimalIMDescriptor");
   }

   public final InputMethod getInputMethod(Locale locale) {
      return this.getInputMethod(locale, null);
   }

   private final InputMethodDescriptor getDescriptorForLocale(Locale locale) {
      InputMethodDescriptor descript = null;
      int size = this._descriptors.size();

      for (int i = 0; i < size; i++) {
         InputMethodDescriptor descriptor = (InputMethodDescriptor)this._descriptors.elementAt(i);

         try {
            Locale[] lc = descriptor.getAvailableLocales();
            int len = lc.length;

            for (int j = 0; j < len; j++) {
               if (lc[j].equals(locale)) {
                  descript = descriptor;
                  break;
               }
            }

            if (descript != null) {
               return descript;
            }
         } catch (Exception var9) {
         }
      }

      return descript;
   }

   public final synchronized InputMethod getInputMethod(Locale locale, String name) {
      InputMethodDescriptor descript = null;
      int index = -1;
      int size = this._descriptors.size();
      if (name != null) {
         for (int i = 0; i < size; i++) {
            InputMethodDescriptor imd = (InputMethodDescriptor)this._descriptors.elementAt(i);
            if (imd.getInputMethodDisplayName(locale, locale).equals(name)) {
               InputMethod im = (InputMethod)this._imethods.elementAt(i);
               if (im != null) {
                  this._latestIMID = imd.getInputMethodID();
                  return im;
               }

               descript = imd;
               index = i;
            }
         }
      } else {
         for (int i = 0; i < size; i++) {
            InputMethodDescriptor descriptor = (InputMethodDescriptor)this._descriptors.elementAt(i);

            try {
               Locale[] lc = descriptor.getAvailableLocales();
               int len = lc.length;

               for (int j = 0; j < len; j++) {
                  if (lc[j].equals(locale)) {
                     descript = descriptor;
                     index = i;
                     break;
                  }
               }

               if (descript != null) {
                  break;
               }
            } catch (Exception var12) {
            }
         }
      }

      if (descript != null) {
         try {
            InputMethod m = (InputMethod)this._imethods.elementAt(index);
            if (m == null) {
               m = descript.createInputMethod();
               this._imethods.setElementAt(m, index);
            }

            this._latestIMID = descript.getInputMethodID();
            return m;
         } catch (Throwable ex) {
            System.err.println("Loading of " + descript.getInputMethodDisplayName(locale, locale) + " has failed");
         }
      }

      return null;
   }

   final void forceLocaleReRegister(Locale locale) {
      Locale[] dispLocales = Locale.getAvailableInputLocales();
      if (Arrays.getIndex(dispLocales, locale) == -1) {
         int descNo = this._descriptors.size();

         for (int i = 0; i < descNo; i++) {
            InputMethodDescriptor imd = (InputMethodDescriptor)this._descriptors.elementAt(i);
            dispLocales = imd.getDisplayLocales();
            if (dispLocales != null) {
               for (int j = 0; j < dispLocales.length; j++) {
                  Locale.addInputLocaleInternal(dispLocales[j]);
               }
            }
         }
      }
   }

   public final synchronized boolean addIMDescriptor(String imName, String className) {
      InputMethodDescriptor desc = null;

      try {
         desc = (InputMethodDescriptor)Class.forName(className).newInstance();
         if ((desc.getInputMethodID() & this._availableIMs) != 0 && !desc.forTestOnly()) {
            System.err.println("ERROR: duplicated input method identifier " + className);
            return false;
         }
      } catch (Throwable ex) {
         System.err.println("Loading of " + className + " descriptor has failed");
         return false;
      }

      this._descriptors.insertElementAt(desc, 0);
      this._imethods.insertElementAt(null, 0);
      this._availableIMs = this._availableIMs | desc.getInputMethodID();
      Locale[] inputLocales = desc.getDisplayLocales();
      if (inputLocales != null) {
         int len = inputLocales.length;

         for (int i = 0; i < len; i++) {
            Locale.addInputLocaleInternal(inputLocales[i]);
         }
      }

      return true;
   }

   public final void dispose() {
      int size = this._imethods.size();

      for (int i = 0; i < size; i++) {
         InputMethodDescriptor desc = (InputMethodDescriptor)this._descriptors.elementAt(i);

         try {
            desc.disposeCache();
         } catch (Exception var7) {
         }

         InputMethod im = (InputMethod)this._imethods.elementAt(i);

         try {
            if (im != null) {
               im.dispose();
            }
         } catch (Throwable var6) {
         }

         this._imethods.setElementAt(null, i);
      }
   }

   public final long getAvailableInputMethodIDs() {
      return this._availableIMs;
   }

   public final long getLatestRequestedInputMethodID() {
      return this._latestIMID;
   }

   public final long getInputMethodIDForLocale(Locale locale) {
      InputMethodDescriptor desc = this.getDescriptorForLocale(locale);
      return desc == null ? 0 : desc.getInputMethodID();
   }

   public final CustomWordsRepository getRepository(int type) {
      switch (type) {
         case 1:
            return this.findAddressBookRepository();
         case 6:
            return this.findYOMIAddressBookRepository();
         default:
            return null;
      }
   }

   private final CustomWordsRepository findAddressBookRepository() {
      int size = this._imethods.size();

      for (int i = 0; i < size; i++) {
         InputMethodDescriptor desc = (InputMethodDescriptor)this._descriptors.elementAt(i);
         long id = desc.getInputMethodID();
         if (id == 4096 || id == 8192) {
            return desc.getRepository(1);
         }
      }

      return null;
   }

   private final CustomWordsRepository findYOMIAddressBookRepository() {
      int size = this._imethods.size();

      for (int i = 0; i < size; i++) {
         InputMethodDescriptor desc = (InputMethodDescriptor)this._descriptors.elementAt(i);
         if (desc.getInputMethodID() == 512) {
            return desc.getRepository(6);
         }
      }

      return null;
   }
}
