package javax.wireless.messaging;

import javax.microedition.io.Connection;

public interface MessageConnection extends Connection {
   String TEXT_MESSAGE;
   String BINARY_MESSAGE;
   String MULTIPART_MESSAGE;

   Message newMessage(String var1);

   Message newMessage(String var1, String var2);

   void send(Message var1);

   Message receive();

   void setMessageListener(MessageListener var1);

   int numberOfSegments(Message var1);
}
