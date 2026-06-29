package net.rim.blackberry.api.mail;

public class NoSuchServiceException extends MessagingException {
   public NoSuchServiceException() {
   }

   public NoSuchServiceException(String message) {
      super(message);
   }
}
