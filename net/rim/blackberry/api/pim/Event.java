package net.rim.blackberry.api.pim;

public interface Event extends PIMItem {
   int ALARM;
   int CLASS;
   int END;
   int LOCATION;
   int NOTE;
   int REVISION;
   int START;
   int SUMMARY;
   int UID;
   int CLASS_CONFIDENTIAL;
   int CLASS_PRIVATE;
   int CLASS_PUBLIC;

   RepeatRule getRepeat();

   void setRepeat(RepeatRule var1);
}
