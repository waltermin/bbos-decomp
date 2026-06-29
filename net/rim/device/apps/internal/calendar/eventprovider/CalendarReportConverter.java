package net.rim.device.apps.internal.calendar.eventprovider;

import java.util.TimeZone;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.calendar.ota.OTACalendarSyncDataManager;
import net.rim.device.apps.api.framework.model.Recur;
import net.rim.device.apps.api.framework.model.Recur$Handle;
import net.rim.device.apps.api.sync.OTASyncData;
import net.rim.device.apps.internal.calendar.sync.CalendarSupportCollection;
import net.rim.device.apps.internal.calendar.sync.CalendarSyncConverter;
import net.rim.vm.Array;

public class CalendarReportConverter implements SyncConverter {
   private CalendarSupportCollection _parentCollection;
   private CalendarSyncConverter _syncConverter;
   private Comparator _eventComparator;
   private int[] _recurrenceInstances;
   private long[] _inclusions;
   private static final int RECURRENCE_INSTANCES = 100;
   private static final int INSTANCE_UID = 101;
   private static final int INSTANCE_PART = 102;
   private static final int INSTANCE_OUTSIDE_OF_TIMEFRAME = 103;
   private static final int INSTANCE_IS_INCLUDE_DATE = 104;
   private static final int REPORT_START_DATE = 105;
   private static final int REPORT_END_DATE = 106;
   private static final int HASH_DATA = 107;

   public Comparator getComparator() {
      return this._eventComparator;
   }

   public void endTransaction() {
      synchronized (this._inclusions) {
         Array.resize(this._inclusions, 0);
      }

      synchronized (this._recurrenceInstances) {
         Array.resize(this._recurrenceInstances, 0);
      }
   }

   @Override
   public SyncObject convert(DataBuffer data, int version, int UID) {
      return null;
   }

   @Override
   public boolean convert(SyncObject object, DataBuffer buffer, int version) {
      TimeZone tz = TimeZone.getDefault();
      Recur$Handle handle = (Recur$Handle)(new Object());
      Object record = object;
      if (!(object instanceof CalendarReportTimeRecord)) {
         if (object instanceof CalendarReportDetailedRecord) {
            CalendarReportDetailedRecord detailedRecord = (CalendarReportDetailedRecord)object;
            record = detailedRecord._o;
         }

         if (record instanceof EventPart) {
            EventPart ep = (EventPart)record;
            EventImpl event = ep._event;
            buffer.writeShort(4);
            buffer.writeByte(101);
            buffer.writeInt(event.getUID());
            buffer.writeShort(4);
            buffer.writeByte(102);
            buffer.writeInt((int)(ep._handle / 1000));
            Recur r = event.getReadOnlyRecurrence();
            if (r.getInclusionCount() > 0) {
               synchronized (this._inclusions) {
                  r.getInclusions(this._inclusions);
                  if (Arrays.binarySearch(this._inclusions, ep._handle, 0, this._inclusions.length) > 0) {
                     buffer.writeShort(1);
                     buffer.writeByte(104);
                     buffer.writeByte(1);
                  }
               }
            }

            this._syncConverter.convert(event, buffer, version);
            OTASyncData syncData = OTACalendarSyncDataManager.getInstance().get(event);
            if (syncData != null) {
               byte[] hashData = syncData.getChecksum();
               buffer.writeShort(hashData.length);
               buffer.writeByte(107);
               buffer.write(hashData);
            }

            return true;
         } else {
            if (record instanceof EventImpl) {
               EventImpl e = (EventImpl)record;
               long targetStartTime = System.currentTimeMillis() - (long)86400000 * this._parentCollection.getReportRangeStart();
               long targetEndTime = System.currentTimeMillis() + (long)86400000 * this._parentCollection.getReportRangeEnd();
               long startDate = e.getStartDate(tz);
               long endDate = startDate + e.getDuration(tz);
               if ((endDate <= targetStartTime || endDate >= targetEndTime) && (startDate <= targetStartTime || startDate >= targetEndTime)) {
                  buffer.writeShort(4);
                  buffer.writeByte(101);
                  buffer.writeInt(e.getUID());
                  buffer.writeShort(1);
                  buffer.writeByte(103);
                  buffer.writeByte(0);
                  return true;
               }

               this._syncConverter.convert(e, buffer, version);
               OTASyncData syncData = OTACalendarSyncDataManager.getInstance().get(e);
               if (syncData != null) {
                  byte[] hashData = syncData.getChecksum();
                  buffer.writeShort(hashData.length);
                  buffer.writeByte(107);
                  buffer.writeByteArray(hashData);
               }

               if (e.isRecurring()) {
                  synchronized (this._recurrenceInstances) {
                     int count;
                     for (count = 0; e.getHandleAfterTime(handle, targetStartTime, tz) && targetStartTime < targetEndTime; count++) {
                        if (count >= this._recurrenceInstances.length) {
                           Array.resize(this._recurrenceInstances, this._recurrenceInstances.length + 10);
                        }

                        this._recurrenceInstances[count] = (int)(handle._handle / 1000);
                        targetStartTime = handle._handle + e.getInstanceDuration() + 1;
                     }

                     buffer.writeShort(4);
                     buffer.writeByte(101);
                     buffer.writeInt(e.getUID());
                     buffer.writeShort(count * 4);
                     buffer.writeByte(100);

                     for (int i = 0; i < count; i++) {
                        buffer.writeInt(this._recurrenceInstances[i]);
                     }

                     return true;
                  }
               }
            }

            return true;
         }
      } else {
         CalendarReportTimeRecord timeRec = (CalendarReportTimeRecord)object;
         buffer.writeShort(4);
         buffer.writeByte(105);
         buffer.writeInt(timeRec._startDate);
         buffer.writeShort(4);
         buffer.writeByte(106);
         buffer.writeInt(timeRec._endDate);
         return true;
      }
   }

   public CalendarReportConverter(CalendarSupportCollection parentCollection) {
      this._parentCollection = parentCollection;
      this._syncConverter = CalendarSyncConverter.getInstance();
      this._eventComparator = new CalendarReportConverter$EventComparator();
      this._recurrenceInstances = new int[0];
      this._inclusions = new long[0];
   }
}
