package net.rim.device.apps.internal.blackberryemail.folder;

import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;

public interface PreverifierListener {
   boolean excludeMessage(EmailMessageModel var1);
}
