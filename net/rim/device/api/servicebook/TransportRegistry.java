package net.rim.device.api.servicebook;

import java.util.Hashtable;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.StringUtilities;

public final class TransportRegistry {
   private Hashtable _table = (Hashtable)(new Object());
   private static final long ID;

   private TransportRegistry() {
   }

   public static final TransportRegistry getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      synchronized (ar) {
         TransportRegistry tr = (TransportRegistry)ar.get(-1022418719669777680L);
         if (tr == null) {
            tr = new TransportRegistry();
            ar.put(-1022418719669777680L, tr);
         }

         return tr;
      }
   }

   public final synchronized void register(String cid, Transport t) {
      this._table.put(StringUtilities.toLowerCase(cid, 1701707776), t);
   }

   public final Transport get(String cid) {
      return (Transport)this._table.get(StringUtilities.toLowerCase(cid, 1701707776));
   }
}
