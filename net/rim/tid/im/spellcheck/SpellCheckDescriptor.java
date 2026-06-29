package net.rim.tid.im.spellcheck;

import java.util.Vector;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.InvokableAction;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntHashtable;
import net.rim.tid.awt.im.InputContext;
import net.rim.tid.awt.im.InvokableActionProducer;
import net.rim.tid.awt.im.repository.CustomWordsRepository;
import net.rim.tid.awt.im.spi.InputMethod;
import net.rim.tid.awt.im.spi.InputMethodDescriptor;
import net.rim.tid.im.conv.europe.repository.AddressBookDataRepository;
import net.rim.tid.itie.LingDataRegistry;
import net.rim.tid.itie.LingDataRegistryListener;
import net.rim.tid.util.Utils;
import net.rim.vm.WeakReference;

public class SpellCheckDescriptor implements InputMethodDescriptor, InvokableActionProducer, SpellCheckConstants, LingDataRegistryListener {
   Locale[] _locales;
   IntHashtable _possiblySupportedLocalesMap = (IntHashtable)(new Object());
   IntHashtable _currentlySupportedLocalesMap = (IntHashtable)(new Object());
   String[] _supportedLocaleNames = new String[]{"en", "", "en", "US", "en", "GB", "en", "NL", "de", "", "es", "", "fr", "", "it", "", "pt", "", "pt", "BR"};
   private WeakReference _spellCheckIM = (WeakReference)(new Object(null));
   private InvokableAction[] _invokableActionCache;
   private InvokableAction[] _runningInvokableActionCache;
   private AddressBookDataRepository _addrBookRepository;
   static final Locale[] EMPTY_LOCALES = new Object[0];

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public SpellCheckDescriptor() {
      InputContext ic = InputContext.getInstance();
      ic.setInvokableActionProducer(this);
      LingDataRegistry reg = ic.getLingDataRegistry();
      if (reg != null) {
         Vector lingDataSupportedLocales = (Vector)(new Object());

         for (int i = 0; i < this._supportedLocaleNames.length; i += 2) {
            Locale locale = Locale.get(this._supportedLocaleNames[i], this._supportedLocaleNames[i + 1]);
            if (locale != null) {
               this._possiblySupportedLocalesMap.put(locale.getCode(), locale);
               if (reg.hasDataFor(locale.getCode(), (byte)1)) {
                  lingDataSupportedLocales.addElement(locale);
                  this._currentlySupportedLocalesMap.put(locale.getCode(), locale);
               }
            }
         }

         this.generateAvailableLocales();
         reg.addLingDataRegistryListener(this);
      }

      try {
         this._addrBookRepository = (AddressBookDataRepository)(new Object());
      } catch (Throwable var7) {
         Utils.reportException(e);
         return;
      }
   }

   @Override
   public Locale[] getAvailableLocales() {
      synchronized (this._currentlySupportedLocalesMap) {
         return this._locales;
      }
   }

   @Override
   public Locale[] getDisplayLocales() {
      return EMPTY_LOCALES;
   }

   @Override
   public boolean hasDynamicLocaleList() {
      return true;
   }

   @Override
   public String getInputMethodDisplayName(Locale inputLocale, Locale displayLanguage) {
      return "";
   }

   @Override
   public InputMethod createInputMethod() {
      SpellCheckInputMethodVariant eng = new SpellCheckInputMethodVariant(this.getAvailableLocales());
      this._spellCheckIM.set(eng);
      return eng;
   }

   @Override
   public boolean forTestOnly() {
      return false;
   }

   @Override
   public long getInputMethodID() {
      return 8192;
   }

   @Override
   public CustomWordsRepository getRepository(int type) {
      return type == 1 ? this._addrBookRepository : null;
   }

   @Override
   public void disposeCache() {
      this._addrBookRepository = (AddressBookDataRepository)(new Object());
   }

   @Override
   public void registered(int code, boolean register) {
      synchronized (this._currentlySupportedLocalesMap) {
         if (register) {
            if (!this._currentlySupportedLocalesMap.containsKey(code) && this._possiblySupportedLocalesMap.containsKey(code)) {
               this._currentlySupportedLocalesMap.put(code, Locale.get(code));
               this.generateAvailableLocales();
            }
         } else if (this._currentlySupportedLocalesMap.containsKey(code)) {
            this._currentlySupportedLocalesMap.remove(code);
            this.generateAvailableLocales();
         }
      }
   }

   @Override
   public InvokableAction[] getIMActions(Object comp) {
      if (!(comp instanceof Object)) {
         return null;
      }

      Field field = (Field)comp;
      InputContext ic = InputContext.getInstance();
      Locale currLocale = ic.getLocale();
      synchronized (this._currentlySupportedLocalesMap) {
         int code = currLocale.getCode();
         if (!this._currentlySupportedLocalesMap.containsKey(code)) {
            return null;
         }
      }

      if (!SpellCheckUtilities.isSpellCheckable(field)) {
         return null;
      }

      if (!SpellCheckUtilities.isSpellCheckVariant(currLocale)) {
         if (SpellCheckUtilities.isSpellCheckAvailable(currLocale)) {
            if (this._invokableActionCache == null) {
               this._invokableActionCache = new Object[]{new SpellCheckAction(6, false)};
            }

            return this._invokableActionCache;
         } else {
            return null;
         }
      } else {
         if (this._runningInvokableActionCache == null) {
            this._runningInvokableActionCache = new Object[]{new SpellCheckAction(7, true), new SpellCheckAction(8, false)};
         }

         return this._runningInvokableActionCache;
      }
   }

   private void generateAvailableLocales() {
      synchronized (this._currentlySupportedLocalesMap) {
         int size = this._currentlySupportedLocalesMap.size();
         this._locales = new Object[size];
         IntEnumeration enumer = this._currentlySupportedLocalesMap.keys();

         for (int i = 0; i < size && enumer.hasMoreElements(); i++) {
            int code = enumer.nextElement();
            this._locales[i] = Locale.get(code, "SpellCheck");
         }
      }
   }
}
