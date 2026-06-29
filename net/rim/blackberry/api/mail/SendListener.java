package net.rim.blackberry.api.mail;

import net.rim.blackberry.api.mail.event.EventListener;

public interface SendListener extends EventListener {
   boolean sendMessage(Message var1);
}
