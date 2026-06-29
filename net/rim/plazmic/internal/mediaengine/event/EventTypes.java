package net.rim.plazmic.internal.mediaengine.event;

public interface EventTypes {
   int MEDIA_UPDATE;
   int MEDIA_CHANGED;
   int EXCEPTION;
   int INFO;
   int REQUEST_MEDIA_UPDATE;
   int FIRST_EVENT;
   int BEGIN;
   int END;
   int REPEAT;
   int FOCUS_IN;
   int FOCUS_OUT;
   int ACTIVATE;
   int ACCESS_KEY;
   int CUSTOM_EVENT;
   int FIRST_DO;
   int DO_START;
   int DO_END;
   int DO_REPEAT;
   int DO_START_ANIMATE;
   int DO_END_ANIMATE;
   int DO_EVALUATE_BEHAVIOR;
   int DO_START_ACTION;
   int DO_START_MEDIA_OBJECT;
   int DO_END_MEDIA_OBJECT;
   int DO_START_HYPERLINK;
   String CUSTOM_EVENT_NAME_ONVISIBLE;
   String CUSTOM_EVENT_NAME_ONINVISIBLE;
   String CUSTOM_EVENT_NAME_ONOBSCURED;
   String CUSTOM_EVENT_NAME_ONEXPOSED;
}
