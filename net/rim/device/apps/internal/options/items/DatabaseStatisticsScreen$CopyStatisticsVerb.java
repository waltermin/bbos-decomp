package net.rim.device.apps.internal.options.items;

import net.rim.device.api.system.Clipboard;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.options.resources.OptionsResources;

final class DatabaseStatisticsScreen$CopyStatisticsVerb extends Verb {
   private final DatabaseStatisticsScreen this$0;

   DatabaseStatisticsScreen$CopyStatisticsVerb(DatabaseStatisticsScreen _1) {
      super(196672, OptionsResources.getResourceBundle(), 1886);
      this.this$0 = _1;
   }

   @Override
   public final Object invoke(Object parameter) {
      StringBuffer sb = (StringBuffer)(new Object());
      sb.append(OptionsResources.getString(1887));
      sb.append('\n');
      sb.append('\n');
      sb.append(OptionsResources.getString(1950));
      sb.append(':');
      sb.append(' ');
      DatabaseStatisticsScreen.appendSizeInKB(this.this$0._totalSize, sb);
      sb.append('\n');
      int numStatsListItems = this.this$0._statsListItems.length;

      for (int i = 0; i < numStatsListItems; i++) {
         sb.append('\n');
         sb.append(this.this$0._statsListItems[i]._name);
         sb.append(':');
         sb.append(' ');
         sb.append(this.this$0._statsListItems[i]._count);
         sb.append('/');
         DatabaseStatisticsScreen.appendSizeInKB(this.this$0._statsListItems[i]._size, sb);
      }

      Clipboard.getClipboard().put(sb);
      return null;
   }
}
