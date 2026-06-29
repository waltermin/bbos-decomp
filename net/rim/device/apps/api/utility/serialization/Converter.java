package net.rim.device.apps.api.utility.serialization;

import java.io.DataInput;
import java.io.DataOutput;

public interface Converter {
   boolean canConvert(Object var1);

   Object convert(byte[] var1, Object var2);

   Object convert(DataInput var1, Object var2);

   void convert(Object var1, DataOutput var2, Object var3);

   byte[] convert(Object var1, Object var2);
}
