package net.rim.blackberry.api.pdap;

import javax.microedition.pim.Event;

public interface BlackBerryEvent extends Event {
   int ATTENDEES;
   int ALLDAY;
   int FREE_BUSY;
   int FB_FREE;
   int FB_TENTATIVE;
   int FB_BUSY;
   int FB_OUT_OF_OFFICE;
}
