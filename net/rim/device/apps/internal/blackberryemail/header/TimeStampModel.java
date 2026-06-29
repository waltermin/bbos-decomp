package net.rim.device.apps.internal.blackberryemail.header;

import java.util.Date;
import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.component.DateField;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.apps.api.framework.model.ColumnPaintProvider;
import net.rim.device.apps.api.framework.model.ColumnPainter;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.KeyProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.transmission.rim.RIMMessagingOutgoingMessage;
import net.rim.device.apps.api.ui.CommonResources;

public final class TimeStampModel implements PersistableRIMModel, FieldProvider, ColumnPaintProvider, KeyProvider, ConversionProvider {
   private long _timeStamp;
   private static DateFormat _dateFormat = DateFormat.getInstance(54);

   public final long getRawDate() {
      return this._timeStamp;
   }

   @Override
   public final void paint(ColumnPainter painter, Object context) {
      painter.drawTime(2, this._timeStamp);
   }

   @Override
   public final Field getField(Object context) {
      long timeStamp = this._timeStamp;
      if (ContextObject.getFlag(context, 0)) {
         return null;
      }

      String label = null;
      if (ContextObject.getFlag(context, 24)) {
         label = CommonResources.getString(2000);
      }

      DateField dateField = new DateField(label, timeStamp, 1161928703861588022L);
      dateField.setTag(Tag.create("message-time"));
      dateField.setEditable(false);
      dateField.setCookie(this);
      Font gFont = (Font)ContextObject.get(context, 77);
      if (gFont != null) {
         Font cFont = dateField.getFont();
         int style = cFont.getStyle() & -3;
         style |= gFont.getStyle() & 7168;
         cFont = cFont.derive(style);
         dateField.setFont(cFont);
      }

      return dateField;
   }

   @Override
   public final boolean grabDataFromField(Field field, Object context) {
      return true;
   }

   @Override
   public final boolean validate(Field field, Object context) {
      return true;
   }

   @Override
   public final boolean convert(Object context, Object target) {
      if (target instanceof RIMMessagingOutgoingMessage) {
         RIMMessagingOutgoingMessage outgoingTransmission = (RIMMessagingOutgoingMessage)target;
         this._timeStamp = System.currentTimeMillis();
         outgoingTransmission.setDate(this._timeStamp);
         return true;
      }

      if (ContextObject.getFlag(context, 43) && ContextObject.getFlag(context, 19)) {
         if (target instanceof byte[]) {
            ConverterUtilities.writeNetworkMessageDate(this._timeStamp, (byte[])target, 36);
            return true;
         }

         if (target instanceof SyncBuffer) {
            return true;
         }
      } else if (target instanceof StringBuffer && ContextObject.getFlag(context, 70)) {
         StringBuffer stringBuffer = (StringBuffer)target;
         if (ContextObject.getFlag(context, 24)) {
            stringBuffer.append(CommonResources.getString(2000));
         } else {
            stringBuffer.append(CommonResources.getString(2001));
         }

         stringBuffer.append(_dateFormat.formatLocal(null, this._timeStamp));
         stringBuffer.append('\n');
         return true;
      }

      return false;
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
      return 0;
   }

   @Override
   public final int getOrder(Object context) {
      int order = 2700;
      if (context != null && ContextObject.getFlag(context, 24)) {
         order = 15100;
      }

      return order;
   }

   @Override
   public final String toString() {
      Date date = new Date(this._timeStamp);
      return date.toString();
   }

   public TimeStampModel(Object initialData) {
      this._timeStamp = System.currentTimeMillis();
   }

   @Override
   public final boolean equals(Object object) {
      if (this == object) {
         return true;
      } else {
         return !(object instanceof TimeStampModel) ? false : ((TimeStampModel)object)._timeStamp == this._timeStamp;
      }
   }

   public TimeStampModel(long timestamp) {
      this._timeStamp = timestamp;
   }
}
