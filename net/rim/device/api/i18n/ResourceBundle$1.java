package net.rim.device.api.i18n;

import java.util.Enumeration;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.util.LongEnumeration;
import net.rim.vm.WeakReference;

class ResourceBundle$1 implements GlobalEventListener {
   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 3542951112318790170L) {
         Enumeration values = ResourceBundle._table.elements();

         while (values.hasMoreElements()) {
            WeakReference ref = (WeakReference)values.nextElement();
            ResourceBundleFamily family = (ResourceBundleFamily)ref.get();
            if (family != null) {
               family.onModuleLoad();
            }
         }

         RIMGlobalMessagePoster.postGlobalEvent(-7464003439710973532L);
         RIMGlobalMessagePoster.postGlobalEvent(7207871974803693937L);
      } else {
         if (guid == -1270659756336956134L) {
            boolean doClean = false;
            Enumeration values = ResourceBundle._table.elements();

            while (values.hasMoreElements()) {
               WeakReference ref = (WeakReference)values.nextElement();
               ResourceBundleFamily family = (ResourceBundleFamily)ref.get();
               if (family == null) {
                  doClean = true;
                  break;
               }
            }

            if (doClean) {
               LongEnumeration keys = ResourceBundle._table.keys();

               while (keys.hasMoreElements()) {
                  long key = keys.nextElement();
                  WeakReference ref = (WeakReference)ResourceBundle._table.get(key);
                  ResourceBundleFamily family = (ResourceBundleFamily)ref.get();
                  if (family == null) {
                     ResourceBundle._table.remove(key);
                  }
               }
            }
         }
      }
   }
}
