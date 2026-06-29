package net.rim.device.api.smartcard;

public interface ReaderStatus {
   int CARD_UNKNOWN = -1;
   int CARD_ABSENT = 0;
   int CARD_REMOVED = 1;
   int CARD_PRESENT = 2;
   int CARD_INSERTED = 3;
}
