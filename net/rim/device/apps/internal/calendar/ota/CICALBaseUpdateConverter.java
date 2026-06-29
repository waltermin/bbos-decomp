package net.rim.device.apps.internal.calendar.ota;

import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.calendar.caldb.CalendarService;
import net.rim.device.apps.api.calendar.caldb.CalendarServiceManager;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.EventUtilities;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.RecurUtilities;
import net.rim.device.apps.api.calendar.ota.CICALConfiguration;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.sync.OTASyncData;
import net.rim.vm.Array;

class CICALBaseUpdateConverter extends CICALBaseConverter {
   private static final byte[] COMPONENT_HEADER = new byte[]{1, 1, 1};

   protected byte[] convert(byte[] header, Event event, Object context) {
      DataBuffer dataBuffer = new DataBuffer(true);
      ContextObject contextObject = ContextObject.castOrCreate(context);
      CalendarService calendarService = CalendarServiceManager.getInstance().findCalendarService(event);
      long[] exclusionDates = RecurUtilities.getDeleteDates(event);
      long[] exclusionDatesToMerge = (long[])contextObject.get(-8188970212168295222L);
      if (exclusionDatesToMerge != null) {
         if (exclusionDates != null && exclusionDates.length != 0) {
            for (int i = 0; i < exclusionDatesToMerge.length; i++) {
               if (Arrays.binarySearch(exclusionDates, exclusionDatesToMerge[i], 0, exclusionDates.length) < 0) {
                  Array.resize(exclusionDates, exclusionDates.length + 1);
                  exclusionDates[exclusionDates.length - 1] = exclusionDatesToMerge[i];
               }
            }
         } else {
            exclusionDates = exclusionDatesToMerge;
         }
      }

      if (exclusionDates != null) {
         contextObject.put(-8188970212168295222L, exclusionDates);
      } else {
         contextObject.remove(-8188970212168295222L);
      }

      dataBuffer.write(header);
      dataBuffer.write(COMPONENT_HEADER);
      this.convertEventHeader(calendarService, event, dataBuffer, contextObject);
      this.convertEventBody(calendarService, event, dataBuffer, contextObject);
      dataBuffer.writeByte(0);
      contextObject.remove(-8188970212168295222L);
      CICALConfiguration cicalConfig = calendarService.getCICALConfiguration();
      if (!cicalConfig.supportsRecurrenceOptimization()) {
         Event[] relatedEvents = RecurUtilities.locateRelatedEvents(event);
         if (relatedEvents != null) {
            for (Event relatedEvent : relatedEvents) {
               OTASyncData syncData = super._otaSyncDataManager.get(relatedEvent);
               if (syncData == null) {
                  syncData = new OTASyncData(0, 1);
                  super._otaSyncDataManager.add(relatedEvent, syncData);
               }

               EventUtilities.incrementDeviceSequence(syncData, relatedEvent);
               dataBuffer.write(COMPONENT_HEADER);
               this.convertEventHeader(calendarService, relatedEvent, dataBuffer, contextObject);
               this.convertEventBody(calendarService, relatedEvent, dataBuffer, contextObject);
               dataBuffer.writeByte(0);
            }
         }
      }

      dataBuffer.writeShort(0);
      return dataBuffer.toArray();
   }
}
