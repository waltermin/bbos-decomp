package net.rim.wica.runtime.access.invoker;

public interface AccessInvokeService {
   int MESSAGE_ERROR_OCCURED;
   int MESSAGE_SENT;
   int MESSAGE_SAVED_DRAFT;

   boolean browserLoadUrl(String var1);

   void executeApplication(String var1);

   void play(String var1);

   int sendEmail(long var1);

   int saveMessageAsDraft(long var1);

   void startCall(String var1);
}
