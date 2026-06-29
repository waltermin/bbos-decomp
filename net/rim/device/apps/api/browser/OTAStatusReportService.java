package net.rim.device.apps.api.browser;

public interface OTAStatusReportService {
   int SUCCESS = 900;
   int INSUFFICIENT_MEMORY = 901;
   int USER_CANCELLED = 902;
   int LOSS_OF_SERVICE = 903;
   int JAR_SIZE_MISMATCH = 904;
   int ATTRIBUTE_MISMATCH = 905;
   int INVALID_DESCRIPTOR = 906;
   int INVALID_JAR = 907;
   int INCOMPATIBLE_PROFILE = 908;
   int AUTHENTICATION_FAILURE = 909;
   int AUTHORIZATION_FAILURE = 910;
   int PUSH_REGISTRATION_FAILURE = 911;
   int DELETION_NOTIFICATION = 912;
   int PACKAGE_NOT_SUPPORTED = 913;
   int MAX_ATTEMPTS = 5;

   String getStatusMessage(int var1);

   void sendReport(String var1, String var2, int var3, String var4);

   void resendReport(String var1);

   boolean hasReportQueued(String var1);

   void addDeleteNotifyApp(String var1, String var2, String var3);

   void appDeleted(String var1);

   void sendAllReports();
}
