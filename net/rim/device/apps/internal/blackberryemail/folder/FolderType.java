package net.rim.device.apps.internal.blackberryemail.folder;

public interface FolderType {
   int SUBTREE = 0;
   int DELETED = 1;
   int INBOX = 2;
   int OUTBOX = 3;
   int SENT = 4;
   int OTHER = 5;
   int ORPHAN = 80;
   int UNFILED = 81;
   int FILED = 82;
   int INVALID = -1;
}
