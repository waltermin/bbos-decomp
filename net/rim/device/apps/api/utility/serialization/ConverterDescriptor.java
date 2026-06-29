package net.rim.device.apps.api.utility.serialization;

public interface ConverterDescriptor {
   boolean canConvert(byte[] var1, Object var2);

   boolean canConvert(Object var1, Object var2);

   Object getContext();

   Converter createConverterInstance(String var1);
}
