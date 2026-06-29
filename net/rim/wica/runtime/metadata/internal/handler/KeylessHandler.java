package net.rim.wica.runtime.metadata.internal.handler;

import net.rim.wica.runtime.event.EventListener;
import net.rim.wica.runtime.event.EventService;
import net.rim.wica.runtime.metadata.Wiclet;
import net.rim.wica.runtime.metadata.WicletRuntime;
import net.rim.wica.runtime.metadata.component.DataCollection;
import net.rim.wica.runtime.metadata.internal.component.KeylessDataCollection;
import net.rim.wica.runtime.service.ServiceProvider;
import net.rim.wica.runtime.service.Serviceable;
import net.rim.wica.runtime.util.LongVector;

public class KeylessHandler implements Serviceable, EventListener {
   private int _inScript;
   private LongVector _tempRefHandles;
   private LongVector _zeroRefHandles;
   private Wiclet _wiclet;
   static Class class$net$rim$wica$runtime$event$EventService;
   static Class class$net$rim$wica$runtime$metadata$WicletRuntime;

   public boolean isInScript() {
      return this._inScript > 0;
   }

   public void addTempReference(long handle) {
      this._tempRefHandles.addElement(handle);
   }

   public void addZeroReference(long handle) {
      this._zeroRefHandles.addElement(handle);
   }

   @Override
   public void setServices(ServiceProvider provider) {
      this._tempRefHandles = new LongVector();
      this._zeroRefHandles = new LongVector();
      EventService eventService = (EventService)provider.getService(
         class$net$rim$wica$runtime$event$EventService == null
            ? (class$net$rim$wica$runtime$event$EventService = class$("net.rim.wica.runtime.event.EventService"))
            : class$net$rim$wica$runtime$event$EventService
      );
      eventService.addListener(602, this);
      eventService.addListener(603, this);
      this._wiclet = ((WicletRuntime)provider.getService(
            class$net$rim$wica$runtime$metadata$WicletRuntime == null
               ? (class$net$rim$wica$runtime$metadata$WicletRuntime = class$("net.rim.wica.runtime.metadata.WicletRuntime"))
               : class$net$rim$wica$runtime$metadata$WicletRuntime
         ))
         .getWiclet();
   }

   @Override
   public void handleEvent(Object sender, int event, int eventParam, Object data) {
      switch (event) {
         case 602:
         default:
            this._inScript++;
            return;
         case 603:
            this._inScript--;
            if (this._inScript == 0) {
               for (int i = this._tempRefHandles.size() - 1; i >= 0; i--) {
                  long handle = this._tempRefHandles.elementAt(i);
                  DataCollection dc = this._wiclet.getDataCollection((int)(handle >> 32));
                  dc.removeReference(handle, false);
               }

               for (int i = this._zeroRefHandles.size() - 1; i >= 0; i--) {
                  long handle = this._zeroRefHandles.elementAt(i);
                  KeylessDataCollection dc = (KeylessDataCollection)this._wiclet.getDataCollection((int)(handle >> 32));
                  if (!dc.hasReferences(handle)) {
                     dc.remove(handle);
                  }
               }

               this._tempRefHandles.removeAllElements();
               this._zeroRefHandles.removeAllElements();
            }
         case 601:
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (Throwable var3) {
         throw new Object(x1.getMessage());
      }
   }
}
