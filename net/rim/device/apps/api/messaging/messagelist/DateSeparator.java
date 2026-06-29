package net.rim.device.apps.api.messaging.messagelist;

import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.accessibility.AccessibleContext;
import net.rim.device.api.ui.accessibility.AccessibleContextProxy;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.WeakReferenceUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.KeyProvider;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.vm.WeakReference;

public final class DateSeparator implements RIMModel, PaintProvider, KeyProvider, AccessibleContextProxy {
   private long _date;
   private boolean _showArrow;
   private static WeakReference _stringBufferWR = (WeakReference)(new Object(null));
   private static DeleteMultipleItemsVerb _deletePriorVerb = new DeleteMultipleItemsVerb(612096, 1, 1100);
   private static RangeActionVerb _markPriorOpenedVerb = new RangeActionVerb(
      611984, -6225946334564270161L, true, 9180, new int[]{1352, 9042, -804651005, 3001, 3006, 3009, -804651005, 3002}, 1, 0, 1300
   );
   private static DateFormat _dateFormat = DateFormat.getInstance(40);
   private static int _currentLocaleCode = Locale.getDefault().getCode();

   @Override
   public final int paint(Graphics graphics, int x, int y, int width, int height, Object context) {
      StringBuffer _stringBuffer = WeakReferenceUtilities.getStringBuffer(_stringBufferWR);
      synchronized (_stringBuffer) {
         _stringBuffer.setLength(0);
         int localeCode = Locale.getDefault().getCode();
         if (_currentLocaleCode != localeCode) {
            _currentLocaleCode = localeCode;
            _dateFormat = DateFormat.getInstance(40);
         }

         _dateFormat.formatLocal(_stringBuffer, this._date);
         if (this._showArrow) {
            _stringBuffer.append(' ');
            _stringBuffer.append('▲');
         }

         return graphics.drawText(_stringBuffer, 0, _stringBuffer.length(), x, y, 4, width);
      }
   }

   public final void setShowArrow(boolean state) {
      this._showArrow = state;
   }

   public final void setDate(long date) {
      this._date = DateFormat.alignToMidnight(date);
   }

   public final long getDate() {
      return this._date;
   }

   public final Verb getDefaultMenuVerbs(Verb[] verbs, int selectedIndex, DateSortedSeparatedMessageArray dssma, ContextObject context) {
      synchronized (_deletePriorVerb) {
         _deletePriorVerb.setParameters(selectedIndex, Integer.MAX_VALUE, dssma, context);
      }

      Arrays.add(verbs, _deletePriorVerb);
      synchronized (_markPriorOpenedVerb) {
         _markPriorOpenedVerb.setParameters(selectedIndex, Integer.MAX_VALUE, dssma, context);
         _markPriorOpenedVerb.setConfirm(MessageListOptions.getOptions().getConfirmMarkPriorOpened());
      }

      Arrays.add(verbs, _markPriorOpenedVerb);
      return null;
   }

   public final Verb getContextMenuVerbs(Verb[] verbs, int selectedIndex, DateSortedSeparatedMessageArray dssma, ContextObject context) {
      VerbRepository verbRepository = VerbRepository.getVerbRepository(-7881764549058890736L);
      Arrays.append(verbs, verbRepository.getVerbs(null));
      synchronized (_markPriorOpenedVerb) {
         _markPriorOpenedVerb.setParameters(selectedIndex, Integer.MAX_VALUE, dssma, context);
         _markPriorOpenedVerb.setConfirm(MessageListOptions.getOptions().getConfirmMarkPriorOpened());
      }

      Arrays.add(verbs, _markPriorOpenedVerb);
      return null;
   }

   @Override
   public final int getKeys(Object context, long[] keyArray, int index, long keyRequested) {
      if (keyRequested == 92199951187614847L) {
         keyArray[index] = this.getDate();
         return 1;
      } else {
         return 0;
      }
   }

   @Override
   public final int getKeys(Object context, int[] keyArray, int index, long keyRequested) {
      return 0;
   }

   @Override
   public final int getKeys(Object context, Object[] keyArray, int index, long keyRequested) {
      return 0;
   }

   @Override
   public final AccessibleContext getAccessibleContext() {
      return (AccessibleContext)(new Object(_dateFormat.formatLocal(this._date), 0, 4));
   }

   public DateSeparator(long date) {
      this.setDate(date);
   }
}
