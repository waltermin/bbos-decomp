package net.rim.device.api.system;

import net.rim.device.internal.applicationcontrol.ApplicationControl;

public final class DirectConnect {
   public static final int SERVICE_PHONE_ONLY = 1;
   public static final int SERVICE_PRIVATE_SILENT = 2;
   public static final int SERVICE_CALL_ALERT_SILENT = 3;
   public static final int SERVICE_GROUP_SILENT = 4;
   public static final int CALL_TYPE_NONE = 0;
   public static final int CALL_TYPE_PRIVATE = 1;
   public static final int CALL_TYPE_ALERT = 2;
   public static final int CALL_TYPE_GROUP = 3;
   public static final int GROUP_CALL_TYPE_LOCAL_AREA = 1;
   public static final int GROUP_CALL_TYPE_WIDE_AREA = 2;
   public static final int GROUP_CALL_TYPE_SELECTED_AREA = 3;
   public static final int TALK_STATUS_CAN_TALK = 1;
   public static final int TALK_STATUS_CANNOT_TALK = 2;
   public static final int TALK_STATUS_PUSH_TO_TALK = 3;
   public static final int ID_TYPE_URBAN = 1;
   public static final int ID_TYPE_FLEET = 2;
   public static final int ID_TYPE_MEMBER = 0;
   public static final int PROFILE_TYPE_NONE = 0;
   public static final int PROFILE_TYPE_TONE = 1;
   public static final int PROFILE_TYPE_VIBRATE = 2;
   public static final int PROFILE_TYPE_VIBRATE_TONE = 3;

   private static final void assertPermission() {
      if (ApplicationControl.isPhoneAllowed(true) == 1) {
         throw new ControlledAccessException();
      }
   }

   private DirectConnect() {
   }

   public static final boolean isSupported() {
      return RadioInfo.areWAFsSupported(8);
   }

   public static final int getId(int type) {
      assertPermission();
      return getIdImpl(type);
   }

   private static final native int getIdImpl(int var0);

   public static final String getUFMI() {
      assertPermission();
      StringBuffer sb = new StringBuffer();
      sb.append(getId(1));
      sb.append('*');
      sb.append(getId(2));
      sb.append('*');
      sb.append(getId(0));
      return sb.toString();
   }

   public static final void enableService(int service, boolean enable) {
      assertPermission();
      enableServiceImpl(service, enable);
   }

   private static final native void enableServiceImpl(int var0, boolean var1);

   public static final void queryService(int service) {
      assertPermission();
      queryServiceImpl(service);
   }

   private static final native void queryServiceImpl(int var0);

   public static final int startPrivateCall(int memberId, int fleetId, int urbanId) {
      assertPermission();
      return startPrivateCallImpl(memberId, fleetId, urbanId);
   }

   private static final native int startPrivateCallImpl(int var0, int var1, int var2);

   public static final int startCallAlert(int memberId, int fleetId, int urbanId) {
      assertPermission();
      return startCallAlertImpl(memberId, fleetId, urbanId);
   }

   private static final native int startCallAlertImpl(int var0, int var1, int var2);

   public static final int startGroupCall(int groupCallType, int selectedArea) {
      assertPermission();
      return startGroupCallImpl(groupCallType, selectedArea);
   }

   private static final native int startGroupCallImpl(int var0, int var1);

   public static final void stopCall(int callType, int callId) {
      assertPermission();
      stopCallImpl(callType, callId);
   }

   private static final native void stopCallImpl(int var0, int var1);

   public static final void clearCallAlert(int callId) {
      assertPermission();
      clearCallAlertImpl(callId);
   }

   private static final native void clearCallAlertImpl(int var0);

   public static final int getCallState(int callType, int callId) {
      assertPermission();
      return getCallStateImpl(callType, callId);
   }

   private static final native int getCallStateImpl(int var0, int var1);

   public static final int getCallId(int callType, int callId, int type) {
      assertPermission();
      return getCallIdImpl(callType, callId, type);
   }

   private static final native int getCallIdImpl(int var0, int var1, int var2);

   public static final int getActiveCallType() {
      assertPermission();
      return getActiveCallTypeImpl();
   }

   private static final native int getActiveCallTypeImpl();

   public static final void queryTalkGroupId() {
      assertPermission();
      queryTalkGroupIdImpl();
   }

   private static final native void queryTalkGroupIdImpl();

   public static final void setTalkGroupId(int talkGroupId) {
      assertPermission();
      setTalkGroupIdImpl(talkGroupId);
   }

   private static final native void setTalkGroupIdImpl(int var0);

   public static final boolean profileUpdate(int outOfHolsterType, int inHolsterType, byte outOfHolsterVolume, byte inHolsterVolume) {
      assertPermission();
      return profileUpdateImpl(outOfHolsterType, inHolsterType, outOfHolsterVolume, inHolsterVolume);
   }

   private static final native boolean profileUpdateImpl(int var0, int var1, byte var2, byte var3);
}
