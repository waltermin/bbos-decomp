package net.rim.device.apps.api.vcal;

import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.EventInstance;
import net.rim.device.apps.api.utility.serialization.Converter;
import net.rim.device.apps.api.utility.serialization.SerializationException;
import net.rim.device.apps.internal.api.serialformats.ICalendarWriter;

final class EventToVCalBuilder implements Converter {
   private VCalToEventModelBuilder _builder = new VCalToEventModelBuilder();
   public static final String MIME_TYPE = "text/calendar";
   public static final String MIME_SUBTYPE = "calendar";
   private static String _notImplemented = "Not Implemented";

   @Override
   public final boolean canConvert(Object object) {
      return object instanceof EventInstance;
   }

   @Override
   public final Object convert(byte[] inputBytes, Object contextObject) throws SerializationException {
      throw new SerializationException(_notImplemented);
   }

   @Override
   public final Object convert(DataInput aDataInput, Object contextObject) throws SerializationException {
      throw new SerializationException(_notImplemented);
   }

   @Override
   public final void convert(Object inputObject, DataOutput aDataOutput, Object contextObject) throws SerializationException {
      throw new SerializationException(_notImplemented);
   }

   @Override
   public final byte[] convert(Object inputObject, Object contextObject) {
      try {
         EventInstance ee = (EventInstance)inputObject;
         Event event = ee.getEventInstance();
         synchronized (this._builder) {
            this._builder.setEvent(event);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ICalendarWriter iCalwriter = new ICalendarWriter(this._builder, outputStream, "utf-8");
            iCalwriter.encodeICalendar();
            return outputStream.toByteArray();
         }
      } finally {
         ;
      }
   }
}
