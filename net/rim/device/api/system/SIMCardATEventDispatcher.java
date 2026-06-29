package net.rim.device.api.system;

import net.rim.device.internal.system.EventDispatcher;
import net.rim.vm.Message;

final class SIMCardATEventDispatcher extends EventDispatcher {
   @Override
   public final void dispatch(Message message, Object listener) {
      SIMCardATListener atListener = (SIMCardATListener)listener;
      int event = message.getEvent();
      int subMessage = message.getSubMessage();
      int data0 = message.getData0();
      int data1 = message.getData1();
      Object object0 = message.getObject0();
      Object object1 = message.getObject1();
      switch (event) {
         case 2322:
         default:
            atListener.atDisplayText((byte[])object0, data0, (data1 & 1) != 0, (data1 & 2) != 0, (data1 & 4) != 0);
            return;
         case 2325:
         case 2326:
            Object[] items = (Object[])object0;
            int[] ids = (int[])object1;
            byte[] title = null;
            if (items != null && items.length != ids.length) {
               title = (byte[])items[items.length - 1];
               Object[] oldItems = items;
               items = new Object[oldItems.length - 1];
               System.arraycopy(oldItems, 0, items, 0, items.length);
            }

            if (event == 2325) {
               atListener.atSelectItem(title, items, ids, data0, (data1 & 1) != 0);
               return;
            }

            atListener.atSetUpMenu(title, items, ids, (data1 & 1) != 0);
            return;
         case 2328:
            atListener.atGetInkey((byte[])object0, data0 & 0xFF, data0 >> 8 & 0xFF, (subMessage & 1) != 0, data1 & 0xFF);
            return;
         case 2329:
            atListener.atGetInput(
               (byte[])object0,
               (byte[])object1,
               data0 & 0xFF,
               data0 >> 8 & 0xFF,
               data0 >> 16 & 0xFF,
               data0 >> 24 & 0xFF,
               (subMessage & 2) != 0,
               (subMessage & 1) != 0,
               data1 & 0xFF
            );
            return;
         case 2330:
            atListener.atSessionEnd();
         case 2321:
         case 2323:
         case 2324:
         case 2327:
         case 2331:
         case 2332:
         case 2333:
         case 2334:
         case 2335:
         case 2342:
            return;
         case 2336:
            atListener.atSetUpCall((byte[])object0, (byte[])object1, subMessage == 1, data0);
            return;
         case 2337:
            atListener.atPlayTone((byte[])object0);
            return;
         case 2338:
            atListener.atDisplayAlphaID((byte[])object0);
            return;
         case 2339:
            atListener.atTimeout();
            return;
         case 2340:
            atListener.atCallControl(subMessage, data0);
            return;
         case 2341:
            byte[] bearerList = null;
            byte[] browserMessage = null;
            byte[] alphaIDMessage = null;
            if (object1 != null) {
               Object[] parms = (Object[])object1;
               bearerList = (byte[])parms[0];
               browserMessage = (byte[])parms[1];
               alphaIDMessage = (byte[])parms[2];
            }

            atListener.atLaunchBrowser(data0, (byte[])object0, bearerList, browserMessage, data1, alphaIDMessage);
            return;
         case 2343:
            atListener.atSetUpIdleModeText((byte[])object0, data1);
      }
   }
}
