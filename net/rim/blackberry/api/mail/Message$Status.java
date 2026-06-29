package net.rim.blackberry.api.mail;

public interface Message$Status {
   int TX_COMPOSING;
   int TX_RETRIEVING_KEY;
   int TX_COMPRESSING;
   int TX_ENCRYPTING;
   int TX_PENDING;
   int TX_SENDING;
   int TX_SENT;
   int TX_MAILBOXED;
   int TX_DELIVERED;
   int TX_READ;
   int TX_ERROR;
   int TX_GENERAL_FAILURE;
   int RX_RECEIVING;
   int RX_RECEIVED;
   int RX_ERROR;
}
