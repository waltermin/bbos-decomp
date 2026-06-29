package net.rim.device.internal.io.tcp;

import java.util.Vector;
import javax.microedition.io.Connection;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.io.PortAssigner;
import net.rim.vm.DebugSupport;

public final class TcpUtils implements TcpConstants {
   private static PortAssigner _hpa = PortAssigner.getInstance(6);
   private static String RAW_TCP = "raw-tcp";
   private static String TRUE = "true";

   private TcpUtils() {
   }

   public static final int addToTcpConnectionDatabase(Connection conn, TcpConnectionIdentifier connId) {
      if (conn != null && connId != null) {
         synchronized (_hpa) {
            Vector database = TcpConnectionDatabase.getInstance().getTcpConnectionDatabase();
            int size = database.size();
            boolean maxedOut = size >= 30 && (!DeviceInfo.isSimulator() || StringUtilities.strEqualIgnoreCase(DebugSupport.getenv(RAW_TCP), TRUE, 1701707776));
            if (maxedOut) {
               throw new MaxConnectionException();
            }

            size--;

            while (size >= 0) {
               TcpConnectionIdentifier temp = (TcpConnectionIdentifier)database.elementAt(size);
               if (temp.equals(connId)) {
                  break;
               }

               size--;
            }

            int localPort = connId.getConnectionLocalPort();
            if (localPort == -1) {
               localPort = getUnusedPort(connId);
            }

            _hpa.registerConnection(localPort, conn, connId.getConnectionApn());
            database.addElement(
               new TcpUtils$InternalTcpConnectionIdentifier(
                  connId.getConnectionIpAddress(), localPort, connId.getConnectionDestinationPort(), connId.getConnectionApn(), null
               )
            );
            return localPort;
         }
      } else {
         throw new Object();
      }
   }

   public static final void removeFromTcpConnectionDatabase(Connection conn, TcpConnectionIdentifier connId) {
      synchronized (_hpa) {
         Vector database = TcpConnectionDatabase.getInstance().getTcpConnectionDatabase();
         int size = database.size();
         size--;

         while (size >= 0) {
            TcpConnectionIdentifier temp = (TcpConnectionIdentifier)database.elementAt(size);
            if (temp.equals(connId)) {
               database.removeElementAt(size);
               _hpa.deregisterConnection(connId.getConnectionLocalPort(), conn, connId.getConnectionApn());
               break;
            }

            size--;
         }
      }
   }

   public static final void logConnectionDatabase() {
      if (EventLogger.getMinimumLevel() == 5) {
         StringBuffer strbuf = (StringBuffer)(new Object());
         strbuf.append("TCP Connection Database:\n");
         synchronized (_hpa) {
            Vector database = TcpConnectionDatabase.getInstance().getTcpConnectionDatabase();

            for (int i = database.size() - 1; i >= 0; i--) {
               TcpConnectionIdentifier identifier = (TcpConnectionIdentifier)database.elementAt(i);
               strbuf.append("//");
               strbuf.append(identifier.getConnectionIpAddress());
               strbuf.append(':');
               strbuf.append(identifier.getConnectionDestinationPort());
               strbuf.append(';');
               strbuf.append(identifier.getConnectionLocalPort());
               strbuf.append('/');
               strbuf.append(identifier.getConnectionApn());
               strbuf.append('\n');
            }
         }

         EventLogger.logEvent(447071754022829032L, strbuf.toString().getBytes(), 5);
      }
   }

   private static final int getUnusedPort(TcpConnectionIdentifier connId) {
      int ephport = -1;
      boolean set = false;
      int dbSize = 0;
      Vector database = TcpConnectionDatabase.getInstance().getTcpConnectionDatabase();
      dbSize = database.size();
      TcpUtils$InternalTcpConnectionIdentifier ephemeralId = new TcpUtils$InternalTcpConnectionIdentifier(connId, null);

      do {
         ephport = _hpa.getUnusedPort(connId.getConnectionApn());
         ephemeralId.setConnectionLocalPort(ephport);

         for (int i = dbSize - 1; i >= 0; i--) {
            TcpConnectionIdentifier temp = (TcpConnectionIdentifier)database.elementAt(i);
            if (temp.equals(ephemeralId)) {
               set = false;
               break;
            }

            set = true;
         }
      } while (!set && dbSize != 0);

      return ephport;
   }

   public static final boolean isValidSequenceNumber(int segSeq, int rcvNxt, int rcvWnd, int segLen) {
      if (segLen == 0) {
         if (rcvWnd == 0) {
            if (rcvNxt == segSeq) {
               return true;
            }

            return false;
         }

         if (rcvWnd > 0) {
            if (seqLEQ(rcvNxt, segSeq) && seqLT(segSeq, rcvNxt + rcvWnd)) {
               return true;
            }

            return false;
         }
      } else if (segLen > 0) {
         if (rcvWnd == 0) {
            return false;
         }

         if (rcvWnd > 0) {
            int segSeqLimit = segSeq + segLen - 1;
            if ((!seqLEQ(rcvNxt, segSeq) || !seqLT(segSeq, rcvNxt + rcvWnd)) && (!seqLEQ(rcvNxt, segSeqLimit) || !seqLT(segSeqLimit, rcvNxt + rcvWnd))) {
               return false;
            }

            return true;
         }
      }

      return false;
   }

   public static final boolean isValidAck(int sndUna, int segAck, int sndNxt) {
      return seqLEQ(sndUna, segAck) && seqLEQ(segAck, sndNxt);
   }

   public static final boolean seqGT(int a, int b) {
      if (a == b) {
         return false;
      } else if (a > 0) {
         return b < a && b > a - 2147483648L;
      } else {
         return b < a ? true : b > a && b > a + 2147483648L;
      }
   }

   public static final boolean seqLEQ(int a, int b) {
      return !seqGT(a, b);
   }

   public static final boolean seqGEQ(int a, int b) {
      return a == b ? true : !seqLT(a, b);
   }

   public static final boolean seqLT(int a, int b) {
      return a == b ? false : !seqGT(a, b);
   }
}
