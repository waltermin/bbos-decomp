package net.rim.device.internal.system;

public interface SE13NetworkTable {
   long SE13_GUID = -7927117593081548760L;

   NetworkInfo[] getPredefinedNetworkTable();

   String getNetworkName(int var1);

   String getCountryInitials(short var1);

   boolean is3DigitMNC(int var1);
}
