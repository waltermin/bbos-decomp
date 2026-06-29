package net.rim.device.apps.api.vcal;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.InputStream;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.Factory;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.transmission.Parameters;
import net.rim.device.apps.api.transmission.TransmissionService;
import net.rim.device.apps.api.transmission.TransmissionServiceListener;
import net.rim.device.apps.api.transmission.rim.CMIMEContentType;
import net.rim.device.apps.api.transmission.rim.RIMMessagingIncomingMessage;
import net.rim.device.apps.api.utility.serialization.Converter;
import net.rim.device.apps.internal.api.serialformats.ICalendarReader;

final class VCalToEventConverter implements Factory, TransmissionServiceListener, Converter {
   private VCalToEventModelBuilder _builder = new VCalToEventModelBuilder();
   private static String _notImplemented = "Not Implemented";

   @Override
   public final Object createInstance(Object context) {
      try {
         byte[] data = (byte[])ContextObject.get(context, 8849067667159082262L);
         String encoding = (String)ContextObject.get(context, 253);
         if (encoding == null) {
            encoding = "";
         }

         synchronized (this._builder) {
            this._builder.reset();
            ((ICalendarReader)(new Object(this._builder, (InputStream)(new Object(data)), encoding))).parseIt();
            return this._builder.getEventModel();
         }
      } finally {
         ;
      }
   }

   @Override
   public final boolean canConvert(Object parameters) {
      if (parameters instanceof Object) {
         String string = (String)parameters;
         if (StringUtilities.compareToIgnoreCase(string, "text/x-vcalendar") == 0
            || StringUtilities.compareToIgnoreCase(string, "text/calendar") == 0
            || StringUtilities.compareToIgnoreCase(string, "x-vcalendar") == 0
            || StringUtilities.compareToIgnoreCase(string, "calendar") == 0) {
            return true;
         }
      }

      if (!(parameters instanceof Parameters)) {
         return false;
      }

      Parameters cmimeParameters = (Parameters)parameters;
      String type = CMIMEContentType.getBaseType(cmimeParameters.getFirst((byte)1));
      return this.canConvert(type);
   }

   @Override
   public final Object convert(byte[] inputBytes, Object contextObject) {
      throw new Object(_notImplemented);
   }

   @Override
   public final Object convert(DataInput aDataInput, Object contextObject) {
      throw new Object(_notImplemented);
   }

   @Override
   public final void convert(Object inputObject, DataOutput aDataOutput, Object contextObject) {
      throw new Object(_notImplemented);
   }

   @Override
   public final byte[] convert(Object inputObject, Object contextObject) {
      throw new Object(_notImplemented);
   }

   @Override
   public final boolean receiveObject(TransmissionService aTransmissionService, Object transmissionObject, Object contextObject) {
      if (transmissionObject instanceof RIMMessagingIncomingMessage) {
         RIMMessagingIncomingMessage message = (RIMMessagingIncomingMessage)transmissionObject;
         Object ticket = PersistentContent.getTicket();
         ticket.hashCode();
         Object iCalendarAttachment = this.getAttachment(message);
         ticket = null;
         if (iCalendarAttachment == null) {
            return false;
         }
      }

      return false;
   }

   @Override
   public final void statusChanged(TransmissionService aTransmissionService, int statusInt, Object contextObject) {
   }

   private final Object getAttachment(RIMMessagingIncomingMessage message) {
      return null;
   }
}
