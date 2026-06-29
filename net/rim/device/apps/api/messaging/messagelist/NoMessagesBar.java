package net.rim.device.apps.api.messaging.messagelist;

import net.rim.device.api.ui.Graphics;
import net.rim.device.apps.api.framework.model.KeyProvider;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.ui.CommonResources;

public final class NoMessagesBar implements RIMModel, PaintProvider, KeyProvider {
   private int _noMessagesStringId;

   public NoMessagesBar(int noMessagesStringId) {
      this._noMessagesStringId = noMessagesStringId;
   }

   @Override
   public final int paint(Graphics g, int x, int y, int width, int height, Object context) {
      return g.drawText(CommonResources.getString(this._noMessagesStringId), x, y, 4, width);
   }

   @Override
   public final int getKeys(Object context, Object[] keyArray, int index, long keyRequested) {
      return 0;
   }

   @Override
   public final int getKeys(Object context, int[] keyArray, int index, long keyRequested) {
      return 0;
   }

   @Override
   public final int getKeys(Object context, long[] keyArray, int index, long keyRequested) {
      if (keyRequested == 92199951187614847L) {
         keyArray[index] = Long.MAX_VALUE;
         return 1;
      } else {
         return 0;
      }
   }
}
