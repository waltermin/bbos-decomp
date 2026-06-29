package net.rim.device.apps.internal.phone;

import java.util.Vector;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.Phone;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.internal.commonmodels.body.BodyModel;
import net.rim.device.apps.internal.phone.api.PhoneCallInitialData;
import net.rim.device.apps.internal.phone.api.PhoneCallModel;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.api.iConferenceCall;
import net.rim.device.apps.internal.phone.api.livecall.LiveCall;
import net.rim.device.apps.internal.phone.data.CallerIDInfo;
import net.rim.device.apps.internal.phone.resource.PhoneResources;
import net.rim.vm.Array;

public final class ConferenceCall extends StandardCall implements iConferenceCall, ReadableList {
   private Vector _initialMembers = null;
   private Vector _members;
   private boolean _held;
   public static final int MIN_MEMBER_COUNT;
   public static final int MAX_MEMBER_COUNT;
   private static ContextObject _callerIDContext = (ContextObject)(new Object());

   final void disconnect() {
      this.onCallDisconnected(null);
   }

   public final void addMember(LiveCall newMemberCall) {
      String callNotes = ((StandardCall)newMemberCall).getNotes();
      String existingNotes = this.getNotes();
      if (callNotes != null && callNotes.length() > 0) {
         StringBuffer buf = (StringBuffer)(new Object());
         if (existingNotes != null && existingNotes.length() > 0) {
            buf.append(existingNotes);
            buf.append('\n');
         }

         buf.append(callNotes);
         existingNotes = buf.toString();
         this.setNotes(existingNotes);
      }

      if (existingNotes != null && existingNotes.length() > 0) {
         ((StandardCall)newMemberCall).setNotes(existingNotes);
      }

      this.internalAddMember(newMemberCall);
   }

   public final LiveCall removeMember(int callId) {
      for (int i = this._members.size() - 1; i >= 0; i--) {
         LiveCall call = (LiveCall)this._members.elementAt(i);
         if (call.getCallId() == callId) {
            this._members.removeElementAt(i);
            ((StandardCall)call).removedFromConference(this);
            return call;
         }
      }

      return null;
   }

   public final LiveCall getMember(int index) {
      return (LiveCall)(index >= 0 && index < this.memberCount() ? this._members.elementAt(index) : null);
   }

   public final boolean isMemberId(int callId) {
      return this.findCallId(callId) != -1;
   }

   public final int memberCount() {
      return this._members.size();
   }

   public final void transferMembers(Vector destination) {
      for (int i = this._members.size() - 1; i >= 0; i--) {
         LiveCall call = this.getMember(i);
         ((StandardCall)call).removedFromConference(this);
         this._members.removeElementAt(i);
         destination.addElement(call);
      }
   }

   @Override
   public final Vector getMembers() {
      return this._members;
   }

   @Override
   public final int size() {
      return this.memberCount();
   }

   @Override
   public final Object getAt(int index) {
      return this.getMember(index);
   }

   @Override
   public final int getAt(int index, int count, Object[] elements, int destIndex) {
      return 0;
   }

   @Override
   public final int getIndex(Object element) {
      return -1;
   }

   @Override
   public final CallerIDInfo getDisplayCallerIDInfo() {
      return this.getMember(0).getDisplayCallerIDInfo();
   }

   @Override
   public final int[] getCallIds() {
      int count = this.memberCount();
      int[] ids = new int[count];

      for (int i = 0; i < count; i++) {
         ids[i] = this.getMember(i).getCallId();
      }

      return ids;
   }

   @Override
   protected final void onConstruction(PhoneCallInitialData data, Object context) {
      if (context instanceof Object) {
         this._initialMembers = (Vector)context;
      }
   }

   @Override
   public final void end(Object context) {
      this.doEndCall(context);
   }

   @Override
   protected final PhoneCallModel createPhoneCallModel(PhoneCallInitialData data) {
      this.combineMemberCallNotes();
      ContextObject context = null;
      String notes = this.getNotes();
      if (notes != null && notes.length() > 0) {
         Object var4;
         if (!(data._context instanceof Object)) {
            var4 = new Object();
            data._context = var4;
         } else {
            var4 = data._context;
         }

         ContextObject.put(var4, -3212039960712815826L, notes);
      }

      return (PhoneCallModel)PhoneUtilities.createPhoneCallModel(data);
   }

   private final void internalAddMember(LiveCall callToAdd) {
      if (callToAdd != null) {
         RIMModel callerIDInfo = callToAdd.getCallerIDInfo();
         this._members.addElement(callToAdd);
         this.getPhoneCall().addCallerIDInfo(callerIDInfo);
         ((StandardCall)callToAdd).addedToConference();
      }
   }

   @Override
   public final String getStatusString() {
      if (this.getFlag(4)) {
         return PhoneResources.getString(305);
      } else if (this.isMuted()) {
         return PhoneResources.getString(6026);
      } else {
         return this.isHeld() ? PhoneResources.getString(301) : PhoneResources.getString(113);
      }
   }

   @Override
   public final boolean isActive() {
      return !this.isHeld();
   }

   @Override
   public final boolean isHeld() {
      return this._held;
   }

   @Override
   protected final boolean canHold() {
      return RadioInfo.getNetworkType() == 5 ? false : super.canHold();
   }

   private final void combineMemberCallNotes() {
      StandardCall callA = (StandardCall)this._initialMembers.elementAt(0);
      StandardCall callB = (StandardCall)this._initialMembers.elementAt(1);
      BodyModel notesModelA = callA.getNotesModel();
      BodyModel notesModelB = callB.getNotesModel();
      if (notesModelA == notesModelB && notesModelA != null) {
         this.setNotesModel(notesModelA);
      } else {
         String notesA = notesModelA == null ? null : notesModelA.getText();
         String notesB = notesModelB == null ? null : notesModelB.getText();
         if (notesA != null && notesA.length() > 0) {
            if (notesB != null && notesB.length() > 0) {
               callA.setNotes(((StringBuffer)(new Object())).append(notesA).append("\n\n").append(notesB).toString());
               callB.setNotesModel(notesModelA);
               super._notesModel = notesModelA;
            } else {
               callB.setNotesModel(notesModelA);
               super._notesModel = notesModelA;
            }
         } else {
            if (notesB != null && notesB.length() > 0) {
               callA.setNotesModel(notesModelB);
               super._notesModel = notesModelB;
            }
         }
      }
   }

   private final int findCallId(int callId) {
      Vector members = this.getMembers();

      for (int i = members.size() - 1; i >= 0; i--) {
         LiveCall member = (LiveCall)members.elementAt(i);
         if (member.getCallId() == callId) {
            return i;
         }
      }

      return -1;
   }

   @Override
   public final void phoneEventNotify(int eventId, int callId, Object context) {
      super.phoneEventNotify(eventId, callId, context);
      switch (eventId) {
         case 1003:
            if (this.matchCallId(callId)) {
               this._held = true;
               return;
            }
            break;
         case 1004:
            if (this.matchCallId(callId)) {
               this._held = false;
            }
            break;
         case 3001:
            this.setFlag(2);
            return;
      }
   }

   @Override
   protected final boolean matchCallId(int callId) {
      return this.findCallId(callId) != -1;
   }

   public ConferenceCall(Vector members) {
      super((PhoneCallInitialData)(new Object(0, (byte)4, 0, null, null)), members);
      int size = members.size();
      if (size != 2) {
         throw new Object();
      }

      this._members = (Vector)(new Object(2));
      StandardCall callA = (StandardCall)members.elementAt(0);
      StandardCall callB = (StandardCall)members.elementAt(1);
      this.internalAddMember(callA);
      this.internalAddMember(callB);
   }

   private final void doEndCall(Object context) {
      boolean answerDropCurrent = PhoneUtilities.getPrivateFlag(context, 56);
      if (answerDropCurrent) {
         this.setFlag(16384);
      }

      synchronized (this) {
         this.setEnding();

         for (int i = this._members.size() - 1; i >= 0; i--) {
            LiveCall member = (LiveCall)this._members.elementAt(i);
            member.setEnding();
            member.end(context);
         }
      }

      this.setFlag(4);
      VoiceServices.broadcastEvent(3003, 0, null);
   }

   @Override
   protected final Field getCallerIDField(Object context) {
      Vector members = this.getMembers();
      int count = members.size();
      boolean smallScreen = Display.getHeight() < Display.getWidth();
      boolean dualMode = PhoneUtilities.getPrivateFlag(context, 21);
      if (smallScreen && dualMode) {
         return (Field)(new Object("..."));
      }

      VerticalFieldManager vfm = (VerticalFieldManager)(new Object());
      ContextObject contextObject = ContextObject.castOrCreate(context);
      contextObject.setFlag(59);
      contextObject.setFlag(106);

      for (int i = 0; i < count; i++) {
         if (dualMode && i == 1) {
            vfm.add((Field)(new Object("...")));
            break;
         }

         LiveCall call = this.getMember(i);
         Field callerIDField = call.getField(context);
         if (callerIDField != null) {
            vfm.add(callerIDField);
         }
      }

      return vfm.getFieldCount() > 0 ? vfm : null;
   }

   @Override
   public final String longestStatusString() {
      String conferenceString = PhoneResources.getString(113);
      String superClassLongest = LiveCall.getLongestStatusString();
      return PhoneUtilities.getLongestString(conferenceString, superClassLongest);
   }

   @Override
   protected final Verb getSystemVerbs(Object context, Verb[] verbs) {
      Verb defaultVerb = super.getSystemVerbs(context, verbs);
      if (!PhoneUtilities.getPrivateFlag(context, 21) && !ApplicationManager.getApplicationManager().isSystemLocked() && !this.isHeld()) {
         int flags = Phone.getInstance().getNetworkFeatures();
         if ((flags & 8) != 0) {
            Array.resize(verbs, verbs.length + 1);
            verbs[verbs.length - 1] = new ManageConferenceVerb(422, this);
         }

         if ((flags & 32) != 0) {
            Array.resize(verbs, verbs.length + 1);
            verbs[verbs.length - 1] = new ManageConferenceVerb(423, this);
         }
      }

      return defaultVerb;
   }

   static {
      _callerIDContext.setFlag(58, 59, 106);
      _callerIDContext.setFlag(26);
   }
}
