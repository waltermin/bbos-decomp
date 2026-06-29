package net.rim.device.apps.internal.phone.data;

import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.api.util.WeakReferenceUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.phone.resource.PhoneResources;
import net.rim.vm.WeakReference;

class TimeModel implements RIMModel, PaintProvider, ConversionProvider {
   private long _time;
   private int _format;
   public static final int TIMESTAMP_FORMAT = 1;
   public static final int DURATION_FORMAT = 2;
   private static WeakReference _bufferWR = (WeakReference)(new Object(null));

   @Override
   public int paint(Graphics g, int x, int y, int width, int height, Object context) {
      StringBuffer _buffer = WeakReferenceUtilities.getStringBuffer(_bufferWR);
      _buffer.setLength(0);
      switch (this._format) {
         case 0:
            break;
         case 1:
         default:
            _buffer.append(PhoneResources.getString(162));
            DateFormat dateFormat = DateFormat.getInstance(55);
            dateFormat.formatLocal(_buffer, this._time);
            break;
         case 2:
            _buffer.append(CommonResources.getString(2003));
            DateTimeUtilities.formatElapsedTime(this._time, _buffer, false);
      }

      return g.drawText(_buffer, 0, _buffer.length(), x, y, 64, width);
   }

   public long getTime() {
      return this._time;
   }

   public void setFormat(int format) {
      this._format = format;
   }

   public int getFormat() {
      return this._format;
   }

   public void setTime(long time) {
      this._time = time;
   }

   @Override
   public boolean convert(Object context, Object target) {
      if (ContextObject.getFlag(context, 19)) {
         return true;
      }

      if (!ContextObject.getFlag(context, 19) && target instanceof Object) {
         StringBuffer result = (StringBuffer)target;
         result.setLength(0);
         StringBuffer _buffer = WeakReferenceUtilities.getStringBuffer(_bufferWR);
         _buffer.setLength(0);
         switch (this._format) {
            case 0:
               break;
            case 1:
            default:
               DateFormat df = DateFormat.getInstance(63);
               df.formatLocal(_buffer, this._time);
               result.append(PhoneResources.getString(162));
               result.append(_buffer);
               return true;
            case 2:
               DateTimeUtilities.formatElapsedTime(this._time, _buffer, true);
               result.append(CommonResources.getString(2003));
               result.append(_buffer);
               return true;
         }
      }

      return false;
   }

   public TimeModel() {
      this._time = 0;
      this._format = 1;
   }

   public TimeModel(long time, int format) {
      this._time = time;
      this._format = format;
   }
}
