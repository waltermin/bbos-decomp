package net.rim.blackberry.api.pdap;

import javax.microedition.pim.Event;

public interface BlackBerryEvent extends Event {
   int ATTENDEES = 20000927;
   int ALLDAY = 20000928;
   int FREE_BUSY = 20000929;
   int FB_FREE = 0;
   int FB_TENTATIVE = 1;
   int FB_BUSY = 2;
   int FB_OUT_OF_OFFICE = 3;
}
