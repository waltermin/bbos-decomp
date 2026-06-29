package net.rim.blackberry.api.mail.event;

public interface ViewListenerExtended extends ViewListener {
   void newMessage(MessageEvent var1);

   void forward(MessageEvent var1);

   void reply(MessageEvent var1);
}
