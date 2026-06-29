package net.rim.plazmic.internal.mediaengine.event;

public interface EventTypes {
   int MEDIA_UPDATE = 20;
   int MEDIA_CHANGED = 21;
   int EXCEPTION = 22;
   int INFO = 23;
   int REQUEST_MEDIA_UPDATE = 24;
   int FIRST_EVENT = 100;
   int BEGIN = 100;
   int END = 101;
   int REPEAT = 102;
   int FOCUS_IN = 103;
   int FOCUS_OUT = 104;
   int ACTIVATE = 105;
   int ACCESS_KEY = 106;
   int CUSTOM_EVENT = 107;
   int FIRST_DO = 200;
   int DO_START = 200;
   int DO_END = 201;
   int DO_REPEAT = 202;
   int DO_START_ANIMATE = 203;
   int DO_END_ANIMATE = 204;
   int DO_EVALUATE_BEHAVIOR = 205;
   int DO_START_ACTION = 206;
   int DO_START_MEDIA_OBJECT = 207;
   int DO_END_MEDIA_OBJECT = 208;
   int DO_START_HYPERLINK = 209;
   String CUSTOM_EVENT_NAME_ONVISIBLE = "onVisible";
   String CUSTOM_EVENT_NAME_ONINVISIBLE = "onInvisible";
   String CUSTOM_EVENT_NAME_ONOBSCURED = "onObscured";
   String CUSTOM_EVENT_NAME_ONEXPOSED = "onExposed";
}
