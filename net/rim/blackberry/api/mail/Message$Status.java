package net.rim.blackberry.api.mail;

public interface Message$Status {
   int TX_COMPOSING = Integer.MAX_VALUE;
   int TX_RETRIEVING_KEY = 1073741823;
   int TX_COMPRESSING = 536870911;
   int TX_ENCRYPTING = 268435455;
   int TX_PENDING = 134217727;
   int TX_SENDING = 67108863;
   int TX_SENT = 33554431;
   int TX_MAILBOXED = 8388607;
   int TX_DELIVERED = 4194303;
   int TX_READ = 2097151;
   int TX_ERROR = 8191;
   int TX_GENERAL_FAILURE = 16383;
   int RX_RECEIVING = 4095;
   int RX_RECEIVED = 2047;
   int RX_ERROR = 1;
}
