package net.rim.device.apps.internal.lbs;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;

final class MapScreen$1 implements Runnable {
   private final MapScreen this$0;

   MapScreen$1(MapScreen this$0) {
      this.this$0 = this$0;
   }

   @Override
   public final void run() {
      if (!this.this$0._init) {
         int latitude = (int)LBSOptions.getLong(5352172232924914325L, 4527776);
         int longitude = (int)LBSOptions.getLong(9116370231748750706L, -9677355);
         int zoom = LBSOptions.getInt(4041863254631303397L, 15);
         Object object = ApplicationRegistry.getApplicationRegistry().get(2809568335828852197L);
         if (object instanceof Object) {
            Verb verb = (Verb)object;
            object = verb.invoke(null);
            if (object instanceof Object) {
               ContextObject context = (ContextObject)object;
               latitude = ContextObject.get(context, -200747095229876690L);
               longitude = ContextObject.get(context, 6606581876924152793L);
               zoom = ContextObject.get(context, 581052036187634982L);
            }
         }

         this.this$0._mapField.setFirstTimeLaunch(false);
         if (latitude == 4527776 && longitude == -9677355 && zoom == 15) {
            this.this$0._mapField.setFirstTimeLaunch(true);
            int mcc = this.this$0.getMCC();
            if (mcc != -1) {
               new DefaultMapView();
               DefaultMapView var9 = this.this$0.locateDefaultMapView(mcc);
               if (var9 != null) {
                  latitude = var9.getLatitude();
                  longitude = var9.getLongitude();
                  zoom = var9.getZoomLevel();
               }
            }
         }

         this.this$0._mapField._rotation = LBSOptions.getInt(-8669706351812607009L, 0);
         this.this$0._mapField.moveTo(latitude, longitude, zoom);
         this.this$0._mapField.update(true);
         if (DeviceInfo.isSimulator()) {
            this.this$0._mapField._marker._latitude = 4542126;
            this.this$0._mapField._marker._longitude = -7569237;
         }
      }
   }
}
