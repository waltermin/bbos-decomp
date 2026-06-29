package javax.bluetooth;

public interface ServiceRecord {
   int NOAUTHENTICATE_NOENCRYPT;
   int AUTHENTICATE_NOENCRYPT;
   int AUTHENTICATE_ENCRYPT;

   DataElement getAttributeValue(int var1);

   RemoteDevice getHostDevice();

   int[] getAttributeIDs();

   boolean populateRecord(int[] var1);

   String getConnectionURL(int var1, boolean var2);

   void setDeviceServiceClasses(int var1);

   boolean setAttributeValue(int var1, DataElement var2);
}
