package net.rim.device.apps.internal.messaging;

import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.collection.WritableSet;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.ui.accessibility.AccessibleContext;
import net.rim.device.api.ui.accessibility.AccessibleContextProxy;
import net.rim.device.api.util.StringMatch;
import net.rim.device.apps.api.framework.model.ActionProvider;
import net.rim.device.apps.api.framework.model.ColumnPaintProvider;
import net.rim.device.apps.api.framework.model.ColumnPainter;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.HotKeyProvider;
import net.rim.device.apps.api.framework.model.KeyProvider;
import net.rim.device.apps.api.framework.model.MatchProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.model.VisibilityControl;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.MessageIcons;
import net.rim.device.apps.api.messaging.NonpersistedUtilityFolders;
import net.rim.device.apps.api.messaging.messagelist.MessageListColumnPainter;
import net.rim.device.apps.api.messaging.util.AnonymousMessage;
import net.rim.device.apps.api.search.Match;
import net.rim.device.apps.api.search.SearchCriterion;
import net.rim.vm.Array;

final class AnonymousMessagesImpl$AnonymousMessageModel
   implements PersistableRIMModel,
   VerbProvider,
   ColumnPaintProvider,
   MatchProvider,
   KeyProvider,
   ActionProvider,
   HotKeyProvider,
   VisibilityControl,
   AnonymousMessage,
   AccessibleContextProxy {
   AnonymousMessagesImpl$AnonymousMessagePayload _payload = new AnonymousMessagesImpl$AnonymousMessagePayload();
   boolean _opened;
   boolean _saved;
   boolean _savedThenOrphaned;
   boolean _new;

   final void markSaved() {
      this._saved = true;
      PersistentObject.commit(this);
      NonpersistedUtilityFolders.addMessageToUtilityFolder(7175316403005034194L, this);
   }

   @Override
   public final Verb getVerbs(Object context, Verb[] verbs) {
      if (ContextObject.getFlag(context, 87)) {
         return null;
      }

      if (!ContextObject.getFlag(context, 5)) {
         Array.resize(verbs, 3);
         int verbCount = 0;
         Verb defaultVerb = null;
         verbs[verbCount++] = new AnonymousMessagesImpl$AnonymousMessageMarkVerb(this, !this._opened);
         verbs[verbCount++] = defaultVerb = new AnonymousMessagesImpl$AnonymousMessageOpenVerb(this);
         if (!this._saved) {
            verbs[verbCount++] = new AnonymousMessagesImpl$AnonymousMessageSaveVerb(this);
         }

         Array.resize(verbs, verbCount);
         return defaultVerb;
      } else {
         return null;
      }
   }

   @Override
   public final int match(Object criteria) {
      if (!(criteria instanceof Object)) {
         if (criteria instanceof Object[]) {
            SearchCriterion[] criteriaArray = (Object[])criteria;
            boolean notApplicable = true;

            for (int i = 0; i < criteriaArray.length; i++) {
               switch (Match.performMatch(this, criteriaArray[i])) {
                  case 0:
                  default:
                     return 0;
                  case 1:
                     notApplicable = false;
                     break;
                  case -1:
               }
            }

            return notApplicable ? -1 : 1;
         } else {
            return -1;
         }
      } else {
         boolean matched = false;
         boolean searchSubject = false;
         boolean searchBody = false;
         SearchCriterion criterion = (SearchCriterion)criteria;
         switch (criterion.getType()) {
            case 0:
            case 3:
            case 15:
            case 17:
            case 19:
            case 20:
            case 23:
            case 24:
            case 27:
               return -1;
            case 1:
               searchBody = true;
               break;
            case 2:
               searchSubject = true;
               break;
            case 4:
            case 5:
               Object value = criterion.getValue();
               if (!(value instanceof Object[])) {
                  return -1;
               }

               Object[] values = (Object[])value;
               String[] testWords = (Object[])values[2];
               matched = Match.nameStringMatch(this._payload._sender, testWords) == 1;
            case 6:
            case 7:
            case 8:
            case 10:
            case 13:
            case 14:
            case 16:
            case 18:
            case 25:
            case 26:
               break;
            case 9:
            case 12:
            default:
               matched = true;
               break;
            case 11:
               matched = this._saved;
               break;
            case 21:
               searchBody = true;
               searchSubject = true;
               break;
            case 22:
               matched = false;
               break;
            case 28:
               matched = !this._opened;
         }

         if (searchSubject || searchBody) {
            StringMatch matcher = (StringMatch)criterion.getValue();
            if (searchSubject) {
               String subject = this._payload._subject;
               if (subject != null && subject.length() > 0) {
                  matched = matcher.indexOf(subject) >= 0;
               } else {
                  matched = matcher.numStringsInPattern() == 0;
               }
            }

            if (!matched && searchBody) {
               String body = this._payload._body;
               if (body != null && body.length() > 0) {
                  matched = matcher.indexOf(body) >= 0;
               }
            }
         }

         return matched ? 1 : 0;
      }
   }

   final void markOpened(boolean markOpened) {
      boolean changed = false;
      if (markOpened && !this._opened) {
         if (this._new) {
            this._new = false;
            AnonymousMessagesImpl.adjustNewAndUnreadCount(false, true, true);
         } else {
            AnonymousMessagesImpl.adjustUnreadCount(false);
         }

         this._opened = true;
         PersistentObject.commit(this);
         changed = true;
      } else if (!markOpened && this._opened) {
         AnonymousMessagesImpl.adjustUnreadCount(true);
         this._opened = false;
         PersistentObject.commit(this);
         changed = true;
      }

      if (changed) {
         if (this._savedThenOrphaned) {
            ((CollectionListener)AnonymousMessagesImpl.getOrphanCollection()).elementUpdated(null, this, this);
            return;
         }

         ((CollectionListener)AnonymousMessagesImpl.getMainCollection()).elementUpdated(null, this, this);
         if (this._saved) {
            NonpersistedUtilityFolders.updateMessageInUtilityFolder(7175316403005034194L, this);
         }
      }
   }

   @Override
   public final boolean perform(long actionId, Object context) {
      if (actionId == 6099736323056465049L) {
         this.markOpened(true);
         AnonymousMessagesImpl$AnonymousMessageViewerScreen viewer = new AnonymousMessagesImpl$AnonymousMessageViewerScreen(context);
         viewer.setModel(this);
         viewer.go();
         return true;
      }

      if (actionId == -6225946334564270161L || actionId == 5803508244060051872L) {
         this.markOpened(true);
         return true;
      }

      if (actionId == -8629311385729242560L) {
         this.markOpened(false);
         return true;
      }

      if (actionId == 4951292880494466830L) {
         if (!this._opened) {
            AnonymousMessagesImpl.adjustUnreadCount(true);
         }

         return true;
      } else if (actionId == -3967872215949752466L || actionId == 6780594967363292755L) {
         this.markDeleted(context);
         return true;
      } else if (actionId == -8570780006855731756L) {
         this.markSaved();
         return false;
      } else if (actionId == -5544992959212130441L) {
         return !this._opened;
      } else if (actionId == 477896226347912237L) {
         return this._opened;
      } else if (actionId == 635678369939227345L) {
         return true;
      } else if (actionId == 278390328807340479L) {
         return !this._opened;
      } else if (actionId == 3103370408204507200L) {
         return this._saved;
      } else if (actionId == 3456946836994320775L) {
         return this._savedThenOrphaned;
      } else if (actionId == -1042102706756508802L) {
         return !this._saved;
      } else if (actionId == -6072303684925088654L) {
         return this._new;
      } else if (actionId == 5213547777258110094L && this._new) {
         this._new = false;
         PersistentObject.commit(this);
         AnonymousMessagesImpl.adjustNewAndUnreadCount(false, true, false);
         ((CollectionListener)AnonymousMessagesImpl.getMainCollection()).elementUpdated(null, this, this);
         return true;
      } else {
         return false;
      }
   }

   final void markDeleted(Object context) {
      if (ContextObject.getFlag(context, 52)) {
         if (!this._savedThenOrphaned) {
            this._saved = false;
            PersistentObject.commit(this);
            NonpersistedUtilityFolders.removeMessageFromUtilityFolder(7175316403005034194L, this);
            return;
         }

         ((WritableSet)AnonymousMessagesImpl.getOrphanCollection()).remove(this);
         if (!this._opened) {
            this._opened = true;
            if (this._new) {
               this._new = false;
               AnonymousMessagesImpl.adjustNewAndUnreadCount(false, true, true);
               return;
            }

            AnonymousMessagesImpl.adjustUnreadCount(false);
            return;
         }
      } else {
         if (this._saved) {
            this._savedThenOrphaned = true;
            ((WritableSet)AnonymousMessagesImpl.getMainCollection()).remove(this);
            ((WritableSet)AnonymousMessagesImpl.getOrphanCollection()).add(this);
            NonpersistedUtilityFolders.removeMessageFromUtilityFolder(7175316403005034194L, this);
            return;
         }

         ((WritableSet)AnonymousMessagesImpl.getMainCollection()).remove(this);
         if (!this._opened) {
            this._opened = true;
            if (this._new) {
               this._new = false;
               AnonymousMessagesImpl.adjustNewAndUnreadCount(false, true, true);
               return;
            }

            AnonymousMessagesImpl.adjustUnreadCount(false);
         }
      }
   }

   @Override
   public final int getKeys(Object context, int[] keyArray, int index, long keyRequested) {
      return 0;
   }

   @Override
   public final Object invokeHotkey(Object context, int hotkeyID) {
      switch (hotkeyID) {
         case 152:
            if (this._opened) {
               this.markOpened(false);
               return null;
            }

            this.markOpened(true);
            return null;
         default:
            return null;
      }
   }

   @Override
   public final int getKeys(Object context, Object[] keyArray, int index, long keyRequested) {
      return 0;
   }

   @Override
   public final int getKeys(Object context, long[] keyArray, int index, long keyRequested) {
      if (keyRequested == 92199951187614847L) {
         keyArray[index] = this._payload._time;
         return 1;
      } else {
         return 0;
      }
   }

   @Override
   public final void paint(ColumnPainter painter, Object context) {
      painter.drawIcon(1, MessageIcons.getIcons(), this._opened ? 2 : 1);
      painter.drawTime(2, this._payload._time);
      String sender = this._payload._sender;
      String subject = this._payload._subject;
      if (!this._opened) {
         painter.setEmphasis(true);
      }

      if (subject != null) {
         painter.drawText(4, subject, false);
      }

      if (sender != null) {
         if (painter instanceof Object) {
            MessageListColumnPainter listPainter = (MessageListColumnPainter)painter;
            if (listPainter.getLinesPerEntry() > 1) {
               painter.setEmphasis(false);
            }
         }

         painter.drawText(3, sender, false);
      }
   }

   @Override
   public final byte getVisibilityFlags() {
      return (byte)(this._new ? 13 : 5);
   }

   @Override
   public final AccessibleContext getAccessibleContext() {
      return (AccessibleContext)(new Object(this._payload._subject, 0, 4));
   }

   private AnonymousMessagesImpl$AnonymousMessageModel(String sender, String subject, String body) {
      this._payload._sender = sender;
      this._payload._subject = subject;
      this._payload._body = body;
      this._payload._time = System.currentTimeMillis();
      ObjectGroup.createGroupIgnoreTooBig(this._payload);
      this._opened = false;
      this._saved = false;
      this._savedThenOrphaned = false;
      this._new = true;
      AnonymousMessagesImpl.adjustNewAndUnreadCount(true, true, true);
   }

   AnonymousMessagesImpl$AnonymousMessageModel(String x0, String x1, String x2, AnonymousMessagesImpl$1 x3) {
      this(x0, x1, x2);
   }
}
