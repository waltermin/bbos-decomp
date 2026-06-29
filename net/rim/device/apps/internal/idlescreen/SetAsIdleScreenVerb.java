package net.rim.device.apps.internal.idlescreen;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;

final class SetAsIdleScreenVerb extends Verb {
   private ScreenClearer _clearer;

   SetAsIdleScreenVerb(ScreenClearer clearer) {
      super(565280, 8585934785835124063L, "net.rim.device.apps.internal.resource.IdleScreen", 4);
      this._clearer = clearer;
   }

   @Override
   public final Object invoke(Object context) {
      Object filenameObj = ContextObject.get(context, 250);
      if (filenameObj instanceof Object) {
         String filename = (String)filenameObj;
         String scaleFPString = (String)ContextObject.get(context, -354255994408004207L);
         if (scaleFPString != null) {
            IdleScreenOptions.setIdleScreenAttribute(2349937757985153567L, scaleFPString);
            IdleScreenOptions.setIdleScreenAttribute(2550679879375249665L, (String)ContextObject.get(context, 3140958043548578060L));
            IdleScreenOptions.setIdleScreenAttribute(-7340185234772503578L, (String)ContextObject.get(context, -2884842616108923462L));
            IdleScreenOptions.setIdleScreenAttribute(-3942144983241110768L, (String)ContextObject.get(context, 918046155976436963L));
         }

         this._clearer.clearScreen();
         IdleScreenOptions.setIdleScreenFilename(filename);
      }

      return null;
   }
}
