package net.rim.blackberry.api.mail.rim;

public interface MailConverter {
   boolean canConvert(Object var1);

   Object convert(Object var1, Object var2);
}
