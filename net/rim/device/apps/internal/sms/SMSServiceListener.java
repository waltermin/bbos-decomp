package net.rim.device.apps.internal.sms;

import net.rim.device.api.system.SMSPacketHeader;

public interface SMSServiceListener {
   boolean smsMessageReceived(SMSPacketHeader var1, byte[] var2, byte[] var3, int[] var4);
}
