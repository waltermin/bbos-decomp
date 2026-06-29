package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.apps.internal.qm.peer.common.HintPollingThread$HintProvider;
import net.rim.device.apps.internal.qm.peer.common.HintPopup;
import net.rim.device.apps.internal.qm.resource.QmResources;

class MessageField extends HorizontalFieldManager implements HintPollingThread$HintProvider {
   private static SimpleDateFormat _dowFormat = (SimpleDateFormat)(new Object("E"));
   private static SimpleDateFormat _timeFormat = (SimpleDateFormat)(new Object(7));
   private static SimpleDateFormat _dateFormat = (SimpleDateFormat)(new Object(56));
   private static final int HINT_OVERLAP = 2;

   MessageField() {
      super(0);
   }

   void onFontChanged() {
      this.update();
   }

   void update() {
      throw null;
   }

   MessengerMessage getMessage() {
      throw null;
   }

   @Override
   public Object getHint() {
      long msgTime = this.getMessage().getTime();
      long curTime = System.currentTimeMillis();
      Object[] args = new Object[]{QmResources.getString(this.getMessage().isIncoming() ? 37 : 38), null, null};
      long minutes = (curTime - msgTime) / 60000;
      int patternID;
      if (minutes == 0) {
         patternID = 33;
      } else if (minutes == 1) {
         patternID = 35;
      } else if (minutes > 1 && minutes <= 30) {
         patternID = 34;
         args[1] = Long.toString(minutes);
      } else {
         args[1] = _timeFormat.formatLocal(msgTime);
         long days = (DateFormat.alignToMidnight(curTime) - DateFormat.alignToMidnight(msgTime)) / 86400000;
         if (days == 0) {
            patternID = 36;
         } else if (days >= 1 && days <= 5) {
            patternID = 32;
            args[2] = _dowFormat.formatLocal(msgTime);
         } else {
            patternID = 32;
            args[2] = _dateFormat.formatLocal(msgTime);
         }
      }

      return MessageFormat.format(QmResources.getString(patternID), args);
   }

   @Override
   public void getHintPosition(XYRect rect) {
      Screen screen = this.getScreen();
      rect.x = 0;
      rect.y = -rect.height + 2;
      HintPopup.transformToScreen(this, rect);
      if (rect.y < 0) {
         rect.y = rect.y + rect.height + this.getExtent().height - 2;
         if (screen == null || rect.y + rect.height > screen.getTop() + screen.getHeight()) {
            rect.y = 0;
         }
      }
   }
}
