package net.rim.device.apps.api.browser;

public interface OTAStatusReportService {
   int SUCCESS;
   int INSUFFICIENT_MEMORY;
   int USER_CANCELLED;
   int LOSS_OF_SERVICE;
   int JAR_SIZE_MISMATCH;
   int ATTRIBUTE_MISMATCH;
   int INVALID_DESCRIPTOR;
   int INVALID_JAR;
   int INCOMPATIBLE_PROFILE;
   int AUTHENTICATION_FAILURE;
   int AUTHORIZATION_FAILURE;
   int PUSH_REGISTRATION_FAILURE;
   int DELETION_NOTIFICATION;
   int PACKAGE_NOT_SUPPORTED;
   int MAX_ATTEMPTS;

   String getStatusMessage(int var1);

   void sendReport(String var1, String var2, int var3, String var4);

   void resendReport(String var1);

   boolean hasReportQueued(String var1);

   void addDeleteNotifyApp(String var1, String var2, String var3);

   void appDeleted(String var1);

   void sendAllReports();
}
