package net.rim.device.apps.internal.mms.ui;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.XYRect;

final class VideoPresentationElementField$MMSMediaPlayerDialog$MMSMediaPlayerDialogManager extends Manager {
   private final VideoPresentationElementField$MMSMediaPlayerDialog this$0;
   private static final int BANNER;
   private static final int BROWSER;

   VideoPresentationElementField$MMSMediaPlayerDialog$MMSMediaPlayerDialogManager(VideoPresentationElementField$MMSMediaPlayerDialog _1) {
      super(0);
      this.this$0 = _1;
   }

   @Override
   protected final void sublayout(int width, int height) {
      int count = this.getFieldCount();

      for (int i = 0; i < count; i++) {
         Field field = this.getField(i);
         switch (i) {
            case -1:
               break;
            case 0:
            default:
               this.setPosition(0, 0);
               this.layoutChild(field, width, height);
               break;
            case 1:
               XYRect rect = this.getField(0).getExtent();
               if (field instanceof BrowserPresentationElementField) {
                  BrowserPresentationElementField bpef = (BrowserPresentationElementField)field;
                  bpef.setHeight(Display.getHeight() - rect.Y2());
               }

               this.setPositionChild(field, 0, rect.Y2());
               this.layoutChild(field, width, height - rect.Y2());
         }
      }

      this.setExtent(width, height);
   }
}
