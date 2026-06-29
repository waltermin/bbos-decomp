package net.rim.wica.runtime.metadata.internal.util;

import net.rim.wica.runtime.event.EventService;

class PersistenceListener$1 implements Runnable {
   private final PersistenceListener this$0;

   PersistenceListener$1(PersistenceListener this$0) {
      this.this$0 = this$0;
   }

   @Override
   public void run() {
      EventService eventService = (EventService)this.this$0
         ._app
         .getRuntime()
         .getService(
            PersistenceListener.class$net$rim$wica$runtime$event$EventService == null
               ? (PersistenceListener.class$net$rim$wica$runtime$event$EventService = PersistenceListener.class$("net.rim.wica.runtime.event.EventService"))
               : PersistenceListener.class$net$rim$wica$runtime$event$EventService
         );
      eventService.dispatchEvent(this, 605, 107);
   }
}
