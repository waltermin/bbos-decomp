package net.rim.plazmic.internal.contentpreview.dispatcher.io;

final class StreamConstants {
   public static final String rcsid = "$Id: //depot/dev/pbaldwin/advancedgraphics/src/net/rim/plazmic/internal/contentpreview/message/StreamConstants.java#1 $";
   public static final byte DISPATCHER_MESSAGE_ID = 76;
   public static final byte DISPATCHER_MESSAGE_WITH_VERSION_ID = 77;
   public static final byte OPEN_SESSION_ID = 1;
   public static final byte ENUMERATE_DEVICES_ID = 2;
   public static final byte GET_VALID_DEVICE_ID = 3;
   public static final byte WAIT_FOR_SESSION_READY_ID = 4;
   public static final byte PUSH_FILE_ID = 5;
   public static final byte RAISE_WINDOW_ID = 6;
   public static final byte CLOSE_SESSION_ID = 7;
   public static final byte SHUTDOWN_DISPATCHER_SERVICE_ID = 8;
   public static final byte GET_RECENT_SESSION_ID = 9;
   public static final byte GET_SPECIFIC_SESSION_ID = 10;
   public static final byte GET_CONTROL_PANEL_PORT_ID = 11;
   public static final byte GET_PLAYBACK_COMMAND_PORT_ID = 12;
   public static final byte SESSION_READY_ID = 13;
   public static final byte LOG_MESSAGE_ID = 14;
   public static final byte GET_SESSION_PROGRESS_ID = 15;
   public static final byte GET_SERVER_VERSION_ID = 16;
   public static final byte DEQUEUE_THEME_REGISTRATION_REQUEST_ID = 17;
   public static final byte DEQUEUE_THEME_ACTIVATION_REQUEST_ID = 18;
   public static final byte VOID_MESSAGE_ID = 65;
   public static final byte SESSION_OK_ID = 66;
   public static final byte SESSION_PORT_ID = 67;
   public static final byte DEVICE_TYPE_ID = 68;
   public static final byte DISPATCHER_SERVICE_FAILURE_ID = 69;
   public static final byte NO_SUCH_SESSION_ID = 70;
   public static final byte INVALID_DEVICE_ID = 71;
   public static final byte TIMEOUT_EXPIRY_ID = 72;
   public static final byte FILE_PUSH_FAILURE_ID = 73;
   public static final byte SESSION_PROGRESS_ID = 74;
   public static final byte THEME_REQUEST_ID = 75;
   public static final byte SERVER_PROPERTIES_ID = 76;

   private StreamConstants() {
   }
}
