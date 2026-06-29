package net.rim.tid.data;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.OTASyncCapableSyncItem;
import net.rim.device.api.util.DataBuffer;
import net.rim.tid.awt.im.InputContext;
import net.rim.tid.im.SLControlObject;

public class IMOptionsDataBaseSyncItem extends OTASyncCapableSyncItem {
   protected int _notification = 110;
   private static final int DB_VERSION = 0;

   @Override
   public String getSyncName() {
      return "Input Method options";
   }

   @Override
   public String getSyncName(Locale locale) {
      return null;
   }

   @Override
   public int getSyncVersion() {
      return 0;
   }

   protected String getKey() {
      return "InputMethodOptionsDataKey";
   }

   @Override
   public synchronized boolean setSyncData(DataBuffer buffer, int version) {
      try {
         buffer.readShort();
         buffer.readByte();
         int length = buffer.readInt();
         byte[] configdata = new byte[length];

         for (int i = 0; i < configdata.length; i++) {
            configdata[i] = buffer.readByte();
         }

         InputContext context = InputContext.getInstance();
         synchronized (context) {
            LearningData data = LearningDataManager.getLearningData(this.getKey());
            if (data == null) {
               data = new LearningData(configdata);
            } else {
               data.setData(configdata);
            }

            LearningDataManager.setLearningData(this.getKey(), data);
            SLControlObject cObject = (SLControlObject)context.getInputMethodControlObject();
            cObject.actionPerformed(this._notification, null);
         }
      } finally {
         return true;
      }

      return true;
   }

   @Override
   public synchronized boolean getSyncData(DataBuffer buffer, int version) {
      DataBuffer temp = (DataBuffer)(new Object(buffer.isBigEndian()));
      synchronized (InputContext.getInstance()) {
         LearningData lData = LearningDataManager.getLearningData(this.getKey());
         if (lData == null) {
            return true;
         }

         byte[] data = lData.getData();
         temp.writeInt(data.length);

         for (int i = 0; i < data.length; i++) {
            temp.writeByte(data[i]);
         }
      }

      temp.writeByte(0);
      int l = temp.getLength();
      temp.rewind();
      buffer.writeShort(l);
      buffer.writeByte(0);
      buffer.write(temp, l);
      return true;
   }
}
