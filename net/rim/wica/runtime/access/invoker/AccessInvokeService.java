package net.rim.wica.runtime.access.invoker;

public interface AccessInvokeService {
   int MESSAGE_ERROR_OCCURED = 0;
   int MESSAGE_SENT = 1;
   int MESSAGE_SAVED_DRAFT = 2;

   boolean browserLoadUrl(String var1);

   void executeApplication(String var1);

   void play(String var1);

   int sendEmail(long var1);

   int saveMessageAsDraft(long var1);

   void startCall(String var1);
}
