package net.rim.device.apps.internal.blackberryemail.email;

import java.util.Enumeration;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.RealtimeClockListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.UIConstants;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;
import net.rim.device.internal.proxy.Proxy;
import net.rim.tid.awt.im.InputContext;
import net.rim.tid.im.SLControlObject;
import net.rim.tid.im.spellcheck.SpellCheckUtilities;

public class SpellCheckOnSendAgent implements FieldChangeListener, UIConstants, RealtimeClockListener {
   private Enumeration _fields;
   private Field _currentField;
   private ResourceBundleFamily _rbf;
   private EmailSendVerb _invokee;
   private Object _callerContext;
   private int _fieldSpellCheckState;
   private boolean _isIteratingThroughFields;
   private SLControlObject _controlObject;
   private boolean _userInteractionDetected;
   private Application _app;
   public static final int SPELL_CHECK_COMPLETE;
   public static final int SPELL_CHECK_SPAWNED;
   private static final int SPELL_CHECK_FIELDS_COMPLETED;
   private static final int SPELL_CHECK_FIELDS_SPAWNED;
   private static final int NO_SPELL_CHECKABLE_FIELDS;
   private static final int SPELL_CHECK_FIELD_COMPLETED;
   private static final int SPELL_CHECK_FIELD_SPAWNED;
   private static final int SPELL_CHECK_FIELD_ERROR;
   private static final int IDLE_TIME_OUT;
   private static final Integer ABORT_REASON_IDLE = (Integer)(new Object(16));

   SpellCheckOnSendAgent(EmailSendVerb invokee) {
      this._invokee = invokee;
      this._rbf = EmailResources.getResourceBundle();
      this._app = Application.getApplication();
   }

   public void init(Enumeration fields, Object callerContext) {
      this._fields = fields;
      this._callerContext = callerContext;
      this._controlObject = null;
   }

   public int invokeSpellCheck() {
      this._userInteractionDetected = false;
      switch (this.spellCheckFields(false)) {
         case 1:
            Proxy.getInstance().addRealtimeClockListener(this);
            return 1;
         default:
            return 0;
      }
   }

   @Override
   public void fieldChanged(Field field, int context) {
      switch (context & 0xFF) {
         case 42:
            SLControlObject co = this.getControlObject();
            if (co != null) {
               co.actionPerformed(1024, this);
            }

            int spellCheckBitFields = context >> 16;
            int userInteraction = spellCheckBitFields & 4;
            if (userInteraction == 4) {
               this._userInteractionDetected = true;
            }

            if (this._isIteratingThroughFields) {
               if ((spellCheckBitFields & 3) == 0) {
                  this._fieldSpellCheckState = 0;
                  this._currentField = null;
               } else {
                  this._fieldSpellCheckState = 2;
               }

               this.handleTermination(spellCheckBitFields);
               return;
            } else {
               Proxy.getInstance().removeRealtimeClockListener(this);
               this._fieldSpellCheckState = 1;
               int abortReason = spellCheckBitFields & 56;
               switch (abortReason) {
                  case 8:
                  case 16:
                  case 32:
                     if (!this._userInteractionDetected) {
                        this.fireSpellCheckComplete();
                        return;
                     }

                     this.fireSpellCheckCancelled();
                     return;
                  default:
                     if (this._app != null) {
                        this._app.invokeLater(new SpellCheckOnSendAgent$SpellCheckCallbackHandler(this, spellCheckBitFields), 20, false);
                     }
               }
            }
      }
   }

   @Override
   public void clockUpdated() {
      if (this._userInteractionDetected) {
         Proxy.getInstance().removeRealtimeClockListener(this);
      } else {
         if (DeviceInfo.getIdleTime() >= 300) {
            if (this._app != null) {
               this._app.invokeLater(new SpellCheckOnSendAgent$DelayedStop(this, null), 20, false);
            }

            Proxy.getInstance().removeRealtimeClockListener(this);
         }
      }
   }

   public static boolean isSpellCheckOnSendEnabled() {
      if (!SpellCheckUtilities.isSpellCheckAvailable()) {
         return false;
      }

      byte[] options = SpellCheckUtilities.getIMProperties();
      return options == null ? false : options[7] != 0;
   }

   private void handleTermination(int spellCheckBitFields) {
      int terminationReason = spellCheckBitFields & 3;
      switch (terminationReason) {
         case 1:
         default:
            this.spellCheckCancelled();
            return;
         case 2:
            this.spellCheckAborted(spellCheckBitFields);
         case 0:
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private int spellCheckFields(boolean reSpellCheckCurrentField) {
      boolean foundSpellCheckableField = false;
      boolean resuming = true;
      this._isIteratingThroughFields = true;
      boolean var7 = false /* VF: Semaphore variable */;

      byte var4;
      label84: {
         try {
            var7 = true;
            if (this._fields == null) {
               var7 = false;
            } else {
               if (!reSpellCheckCurrentField) {
                  this._currentField = null;
                  resuming = false;
               }

               if (this._currentField == null && this._fields.hasMoreElements()) {
                  this._currentField = (Field)this._fields.nextElement();
               }

               while (this._currentField != null) {
                  if (SpellCheckUtilities.isSpellCheckable(this._currentField)) {
                     switch (this.spellCheckField(this._currentField, resuming)) {
                        case 1:
                           var4 = 1;
                           var7 = false;
                           break label84;
                        default:
                           resuming = false;
                     }
                  }

                  if (this._fields.hasMoreElements()) {
                     this._currentField = (Field)this._fields.nextElement();
                  } else {
                     this._currentField = null;
                  }
               }

               var7 = false;
            }
         } finally {
            if (var7) {
               this._isIteratingThroughFields = false;
            }
         }

         this._isIteratingThroughFields = false;
         if (foundSpellCheckableField) {
            return 0;
         }

         return 2;
      }

      this._isIteratingThroughFields = false;
      return var4;
   }

   private int spellCheckField(Field field, boolean resuming) {
      field.setFocus();
      if (this.initSpellCheckEngine()) {
         this._fieldSpellCheckState = 1;
         SLControlObject co = this.getControlObject();
         if (co != null) {
            if (resuming) {
               co.actionPerformed(60, this);
            } else {
               co.actionPerformed(41, this);
            }
         } else {
            this._fieldSpellCheckState = 2;
         }
      } else {
         this._fieldSpellCheckState = 2;
      }

      return this._fieldSpellCheckState;
   }

   private void fireSpellCheckComplete() {
      this._invokee.asyncPerformInvocation(this._callerContext, false);
   }

   private void fireSpellCheckCancelled() {
      this._invokee.asyncPerformInvocation(this._callerContext, true);
   }

   private boolean initSpellCheckEngine() {
      if (SpellCheckUtilities.activateSpellCheckIM()) {
         SLControlObject co = this.getControlObject();
         if (co != null) {
            co.actionPerformed(768, this);
         }

         return true;
      } else {
         return false;
      }
   }

   private SLControlObject getControlObject() {
      if (this._controlObject == null) {
         InputContext ic = InputContext.getInstance();
         this._controlObject = (SLControlObject)ic.getInputMethodControlObject();
      }

      return this._controlObject;
   }

   private void spellCheckAborted(int spellCheckBitFields) {
      int abortReason = spellCheckBitFields & 56;
      switch (abortReason) {
         case 8:
         case 16:
         case 32:
            if ((spellCheckBitFields & 4) == 0) {
               this.fireSpellCheckComplete();
               return;
            }
            break;
         default:
            this.fireSpellCheckCancelled();
      }
   }

   private void spellCheckCancelled() {
      String str = this._rbf.getString(156);
      if (Dialog.ask(3, str, -1) != -1) {
         this.fireSpellCheckComplete();
      } else {
         this.fireSpellCheckCancelled();
      }
   }

   private void resumeSpellCheck(boolean resumeCurrentField) {
      switch (this.spellCheckFields(resumeCurrentField)) {
         case 1:
            if (!this._userInteractionDetected) {
               Proxy.getInstance().addRealtimeClockListener(this);
               return;
            }
            break;
         default:
            String str = this._rbf.getString(153);
            if (Dialog.ask(3, str, 4) != -1) {
               this.fireSpellCheckComplete();
               return;
            }

            this.fireSpellCheckCancelled();
      }
   }
}
