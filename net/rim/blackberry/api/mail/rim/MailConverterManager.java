package net.rim.blackberry.api.mail.rim;

import java.util.Vector;
import net.rim.device.api.system.ApplicationRegistry;

public class MailConverterManager {
   private Vector _converters = new Vector();
   public static final long ID = 6928350173436200986L;
   private static MailConverterManager _instance;

   public static MailConverterManager getInstance() {
      return _instance;
   }

   private MailConverterManager() {
   }

   public synchronized void register(MailConverter c) {
      if (!this._converters.contains(c)) {
         this._converters.addElement(c);
      }
   }

   public synchronized void unregister(MailConverter c) {
      if (this._converters.contains(c)) {
         this._converters.removeElement(c);
      }
   }

   public MailConverter getConverter(Object element) {
      MailConverter[] array;
      synchronized (this) {
         array = new MailConverter[this._converters.size()];
         this._converters.copyInto(array);
      }

      for (int i = array.length - 1; i >= 0; i--) {
         if (array[i].canConvert(element)) {
            return array[i];
         }
      }

      return null;
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      synchronized (ar) {
         _instance = (MailConverterManager)ar.get(6928350173436200986L);
         if (_instance == null) {
            _instance = new MailConverterManager();
            ar.put(6928350173436200986L, _instance);
         }
      }
   }
}
