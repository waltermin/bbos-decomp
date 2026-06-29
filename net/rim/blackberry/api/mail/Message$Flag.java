package net.rim.blackberry.api.mail;

public interface Message$Flag {
   int OPENED = 1;
   int FILED = 2;
   int DELETED = 262144;
   int MOVED = 4;
   int SAVED_THEN_ORPHANED = 8;
   int SAVED = 16;
   int REPLY_ALLOWED = 32;
   int REQUEST_READ_ACK = 64;
   int PRIORITY = 128;
   int BODY_TRUNCATED = 4096;
}
