package net.rim.device.internal.browser.util;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.util.Arrays;
import net.rim.vm.Array;

public final class TimeLogger {
   private StringBuffer _tempSB = new StringBuffer();
   private long[] _totalTime = new long[14];
   private long[] _startTime = new long[4];
   private long[] _timerIds = new long[4];
   private static final long SINGLETON_REGISTRATION_KEY;
   public static final long EVENTLOGGER_GUID;
   private static TimeLogger _instance;
   public static boolean _loggingEnabled;
   public static final int TIMER_WTP_WAIT_INVOKE_RESPONSE;
   public static final int TIMER_WTP_WAIT_SEG_RESPONSE;
   public static final int TIMER_BROWSER_FETCH;
   public static final int TIMER_BROWSER_RENDER;
   public static final int TIMER_BROWSER_START_WAP_STACK;
   public static final int TIMER_BROWSER_ENTER_BROWSER;
   public static final int TIMER_WSP_HEADER_ENCODE;
   public static final int TIMER_BROWSER_JAVASCRIPT_RUN;
   public static final int TIMER_BROWSER_JAVASCRIPT_DESERIALIZE;
   public static final int TIMER_BROWSER_JAVASCRIPT_DOCUMENT_CREATE;
   public static final int TIMER_BROWSER_GZIP;
   public static final int TIMER_BROWSER_STYLE_SHEET_FETCH;
   public static final int TIMER_BROWSER_STYLE_SHEET_PROCESS;
   public static final int TIMER_PIPE_READ_WAIT;
   private static final int NUM_TIMERS;
   private static final String TIMER_WTP_WAIT_INVOKE_RESPONSE_DESCRIPTION;
   private static final String TIMER_WTP_WAIT_SEG_RESPONSE_DESCRIPTION;
   private static final String TIMER_BROWSER_FETCH_DESCRIPTION;
   private static final String TIMER_BROWSER_RENDER_DESCRIPTION;
   private static final String TIMER_BROWSER_START_WAP_STACK_DESCRIPTION;
   private static final String TIMER_BROWSER_ENTER_BROWSER_DESCRIPTION;
   private static final String TIMER_WSP_HEADER_ENCODE_DESCRIPTION;
   private static final String TIMER_BROWSER_JAVASCRIPT_RUN_DESCRIPTION;
   private static final String TIMER_BROWSER_JAVASCRIPT_DESERIALIZE_DESCRIPTION;
   private static final String TIMER_BROWSER_JAVASCRIPT_DOCUMENT_CREATE_DESCRIPTION;
   private static final String TIMER_BROWSER_GZIP_DESCRIPTION;
   private static final String TIMER_BROWSER_STYLE_SHEET_FETCH_DESCRIPTION;
   private static final String TIMER_BROWSER_STYLE_SHEET_PROCESS_DESCRIPTION;
   private static final String TIMER_PIPE_READ_WAIT_DESCRIPTION;

   private TimeLogger() {
   }

   public static final TimeLogger getInstance() {
      return _instance;
   }

   public final void startLogging() {
      this._tempSB.setLength(0);
      Arrays.fill(this._totalTime, 0);
      Arrays.fill(this._startTime, 0);
      _loggingEnabled = true;
      Dialog.alert("Time based Logging Started");
   }

   public final synchronized void startTimer(int timerId, int uniqueId) {
      long id = timerId << 32 | uniqueId;
      int count = this._timerIds.length;

      for (int i = 0; i < count; i++) {
         if (this._timerIds[i] == -1) {
            this._timerIds[i] = id;
            this._startTime[i] = System.currentTimeMillis();
            return;
         }
      }

      int originalLen = this._timerIds.length;
      Array.resize(this._timerIds, this._timerIds.length << 1);
      Array.resize(this._startTime, this._startTime.length << 1);
      this._timerIds[originalLen] = id;
      this._startTime[originalLen] = System.currentTimeMillis();
   }

   public final synchronized void stopTimer(int timerId, int uniqueId) {
      long id = timerId << 32 | uniqueId;
      int count = this._timerIds.length;

      for (int i = 0; i < count; i++) {
         if (this._timerIds[i] == id) {
            long elapsedTime = System.currentTimeMillis() - this._startTime[i];
            String description = "";
            switch (timerId) {
               case -1:
                  break;
               case 0:
               default:
                  description = "WTP Invoke waiting";
                  break;
               case 1:
                  description = "WTP Segmented Response waiting";
                  break;
               case 2:
                  description = "Browser fetching page";
                  break;
               case 3:
                  description = "Browser rendering page";
                  break;
               case 4:
                  description = "Browser start wap stack";
                  break;
               case 5:
                  description = "Browser enter browser";
                  break;
               case 6:
                  description = "WSP Header Encode";
                  break;
               case 7:
                  description = "JavaScript Run";
                  break;
               case 8:
                  description = "JavaScript Deserialize";
                  break;
               case 9:
                  description = "JavaScript Document Create";
                  break;
               case 10:
                  description = "Browser gzip";
                  break;
               case 11:
                  description = "Style Sheet fetching";
                  break;
               case 12:
                  description = "Style Sheet processing";
                  break;
               case 13:
                  description = "Pipe read waiting";
            }

            this._tempSB.append(description);
            this._tempSB.append(" took: ");
            this._tempSB.append(elapsedTime);
            this._tempSB.append(" ms\n");
            this._totalTime[timerId] = this._totalTime[timerId] + elapsedTime;
            this._timerIds[i] = -1;
            return;
         }
      }
   }

   private final void appendSummary(StringBuffer sb, int timerId, String description) {
      sb.append(description);
      sb.append(" = ");
      sb.append(this._totalTime[timerId]);
      sb.append(" ms\n");
   }

   public final String getText() {
      StringBuffer sb = new StringBuffer("----Summary----\n");
      this.appendSummary(sb, 0, "WTP Invoke waiting");
      this.appendSummary(sb, 1, "WTP Segmented Response waiting");
      this.appendSummary(sb, 13, "Pipe read waiting");
      this.appendSummary(sb, 2, "Browser fetching page");
      this.appendSummary(sb, 3, "Browser rendering page");
      this.appendSummary(sb, 4, "Browser start wap stack");
      this.appendSummary(sb, 5, "Browser enter browser");
      this.appendSummary(sb, 6, "WSP Header Encode");
      this.appendSummary(sb, 7, "JavaScript Run");
      this.appendSummary(sb, 8, "JavaScript Deserialize");
      this.appendSummary(sb, 9, "JavaScript Document Create");
      this.appendSummary(sb, 11, "Style Sheet fetching");
      this.appendSummary(sb, 12, "Style Sheet processing");
      this.appendSummary(sb, 10, "Browser gzip");
      sb.append("\n----Details----\n");
      sb.append(this._tempSB);
      return sb.toString();
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _instance = (TimeLogger)ar.getOrWaitFor(1163022020978190440L);
      if (_instance == null) {
         _instance = new TimeLogger();
         ar.put(1163022020978190440L, _instance);
      }
   }
}
