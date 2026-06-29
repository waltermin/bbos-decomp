package javax.bluetooth;

public interface DiscoveryListener {
   int INQUIRY_COMPLETED;
   int INQUIRY_TERMINATED;
   int INQUIRY_ERROR;
   int SERVICE_SEARCH_COMPLETED;
   int SERVICE_SEARCH_TERMINATED;
   int SERVICE_SEARCH_ERROR;
   int SERVICE_SEARCH_NO_RECORDS;
   int SERVICE_SEARCH_DEVICE_NOT_REACHABLE;

   void deviceDiscovered(RemoteDevice var1, DeviceClass var2);

   void servicesDiscovered(int var1, ServiceRecord[] var2);

   void serviceSearchCompleted(int var1, int var2);

   void inquiryCompleted(int var1);
}
