package net.rim.device.apps.api.utility.serialization;

import java.io.DataInput;
import java.io.DataOutput;

public class BaseConverter implements Converter {
   private static String _notImplemented = "Not Implemented";

   @Override
   public Object convert(byte[] inputBytes, Object contextObject) throws SerializationException {
      throw new SerializationException(_notImplemented);
   }

   @Override
   public Object convert(DataInput aDataInput, Object contextObject) throws SerializationException {
      throw new SerializationException(_notImplemented);
   }

   @Override
   public void convert(Object inputObject, DataOutput aDataOutput, Object contextObject) throws SerializationException {
      throw new SerializationException(_notImplemented);
   }

   @Override
   public byte[] convert(Object inputObject, Object contextObject) throws SerializationException {
      throw new SerializationException(_notImplemented);
   }

   @Override
   public boolean canConvert(Object _1) {
      throw null;
   }
}
