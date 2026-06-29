package net.rim.tid.itie;

import java.util.Vector;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntHashtable;
import net.rim.tid.awt.im.InputContext;
import net.rim.tid.awt.im.spi.InputMethod;

public class LingDataRegistry {
   private IMContext _context;
   private int _idCounter;
   private IntHashtable _store = new IntHashtable();
   private Vector _lingDataRegistryListeners;

   LingDataRegistry(IMContext context) {
      this._context = context;
   }

   public static int registerLingData(int locale, LinguisticData aData) {
      LingDataRegistry reg = InputContext.getInstance(false).getLingDataRegistry();
      int result = reg.addData(locale, aData);
      if (result != -1) {
         IMManager manager = InputContext.getInstance(false).getInputMethodsManager();
         if (manager != null) {
            manager.forceLocaleReRegister(Locale.get(locale));
         }
      }

      return result;
   }

   private synchronized boolean isValid(int locale, LinguisticData aData, LinguisticData[] unloadData) {
      if (aData.getType() >> 4 != 1 && !this.hasDataFor(locale, (byte)1)) {
         return false;
      }

      for (LinguisticData data = (LinguisticData)this._store.get(locale); data != null; data = data._next) {
         if (data.getType() == aData.getType()) {
            if (data.getVersion() < aData.getVersion()) {
               unloadData[0] = data;
               return true;
            }

            return false;
         }
      }

      return true;
   }

   private synchronized int addData(int locale, LinguisticData aData) {
      int imLocale = this._context.getLocale().getCode();
      aData.setID(this._idCounter++);
      LinguisticData[] unloadData = new LinguisticData[]{null};
      if (!this.isValid(locale, aData, unloadData)) {
         this._idCounter--;
         return -1;
      }

      if (imLocale == locale || (locale & 65535) == 0 && locale == (imLocale & -65536)) {
         InputMethod im = this._context.getInputMethod();
         if (im != null && (im.loadLinguisticData(aData) & 2) != 0) {
            this._idCounter--;
            return -1;
         }
      }

      LinguisticData data = (LinguisticData)this._store.get(locale);
      if (data != null) {
         data.addToChain(aData);
      } else {
         this._store.put(locale, aData);
         this.fireLingDataRegistration(locale, true);
      }

      if (unloadData[0] != null) {
         this.unload(locale, unloadData[0].getName(), unloadData[0].getType());
      }

      return aData.getID();
   }

   public synchronized int unloadLingData(int locale, String name, int type) {
      if (locale != 0) {
         return this.unload(locale, name, type);
      }

      int ret = 0;
      IntEnumeration e = this._store.keys();

      while (e.hasMoreElements()) {
         locale = e.nextElement();
         ret |= this.unload(locale, name, type);
      }

      return ret;
   }

   private int unload(int locale, String name, int type) {
      int ret = 0;
      if (name == null && type == 0) {
         for (LinguisticData data = (LinguisticData)this._store.get(locale); data != null; data = data._next) {
            ret |= this.unload(data, locale, type);
         }

         this.update(locale, null);
         return ret;
      } else {
         LinguisticData ld = (LinguisticData)this._store.get(locale);
         LinguisticData toRemove = this.find(ld, name, type);
         ret = this.unload(toRemove, locale, type);
         this.removeFromStore(locale, ld, toRemove);
         return ret;
      }
   }

   private int unload(LinguisticData ld, int locale, int type) {
      if (ld != null && (type == 0 || ld.getType() == type)) {
         if (ld.getCodFileName() != null) {
            int handle = CodeModuleManager.getModuleHandle(ld.getCodFileName());
            int res = CodeModuleManager.deleteModuleEx(handle, true);
            if (res != 0 && res != 6) {
               return 2;
            }
         }

         return this.unloadFromIM(ld, locale);
      } else {
         return 4;
      }
   }

   private int unloadFromIM(LinguisticData ld, int locale) {
      if (this._context.getLocale().getCode() != locale) {
         return 1;
      }

      InputMethod im = this._context.getInputMethod();
      return im != null ? im.unloadLinguisticData(ld.getID()) : 1;
   }

   private synchronized void removeFromStore(int locale, LinguisticData first, LinguisticData toRemove) {
      if (first != null && toRemove != null) {
         if (toRemove == first) {
            this.update(locale, first._next);
         } else {
            while (first._next != null) {
               if (first._next == toRemove) {
                  first._next = toRemove._next;
                  return;
               }
            }
         }
      }
   }

   private LinguisticData find(LinguisticData first, String name, int type) {
      while (first != null) {
         if (first.getType() == type && (name == null || name.equals(first.getName()))) {
            return first;
         }

         first = first._next;
      }

      return null;
   }

   synchronized LinguisticData getLingData(int locale) {
      return (LinguisticData)this._store.get(locale);
   }

   synchronized void update(int locale, LinguisticData aData) {
      if (aData != null) {
         this._store.put(locale, aData);
         this.fireLingDataRegistration(locale, true);
      } else {
         this._store.remove(locale);
         this.fireLingDataRegistration(locale, false);
      }
   }

   public synchronized int[] getAvailableLocals() {
      int[] result = new int[this._store.size()];
      this._store.keysToArray(result);
      return result;
   }

   public synchronized String[] getLingDataNames(int locale) {
      String[] result = new String[0];

      for (LinguisticData first = (LinguisticData)this._store.get(locale); first != null; first = first._next) {
         Arrays.add(result, first.getName());
      }

      return result;
   }

   public boolean hasDataFor(int locale, byte dataType) {
      synchronized (this._store) {
         for (LinguisticData data = (LinguisticData)this._store.get(locale); data != null; data = data._next) {
            if (data.getType() >> 4 == dataType) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean hasDataFor(int locale, byte dataType, byte dataFormat) {
      synchronized (this._store) {
         int searchType = (255 & dataType) << 4 | dataFormat;

         for (LinguisticData data = (LinguisticData)this._store.get(locale); data != null; data = data._next) {
            int type = data.getType();
            if (data.getType() == searchType) {
               return true;
            }
         }

         return false;
      }
   }

   public synchronized void addLingDataRegistryListener(LingDataRegistryListener l) {
      if (this._lingDataRegistryListeners == null) {
         this._lingDataRegistryListeners = new Vector();
      }

      this._lingDataRegistryListeners.addElement(l);
   }

   public synchronized void removeLingDataRegistryListener(LingDataRegistryListener l) {
      if (this._lingDataRegistryListeners != null) {
         this._lingDataRegistryListeners.removeElement(l);
      }
   }

   private synchronized void fireLingDataRegistration(int locale, boolean register) {
      if (this._lingDataRegistryListeners != null) {
         int size = this._lingDataRegistryListeners.size();

         for (int i = 0; i < size; i++) {
            LingDataRegistryListener l = (LingDataRegistryListener)this._lingDataRegistryListeners.elementAt(i);
            l.registered(locale, register);
         }
      }
   }
}
