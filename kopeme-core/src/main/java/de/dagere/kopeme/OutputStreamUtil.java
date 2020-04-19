package de.dagere.kopeme;
import java.io.PrintStream;

public class OutputStreamUtil {
   private static final PrintStream oldOut = System.out;
   private static final PrintStream oldErr = System.err;
   
   public static void redirectToNullStream() {
      final java.io.PrintStream nullStream = new java.io.PrintStream(new java.io.OutputStream() {
         @Override public void write(int b) {}
     }) {
         @Override public void flush() {}
         @Override public void close() {}
         @Override public void write(int b) {}
         @Override public void write(byte[] b) {}
         @Override public void write(byte[] buf, int off, int len) {}
         @Override public void print(boolean b) {}
         @Override public void print(char c) {}
         @Override public void print(int i) {}
         @Override public void print(long l) {}
         @Override public void print(float f) {}
         @Override public void print(double d) {}
         @Override public void print(char[] s) {}
         @Override public void print(String s) {}
         @Override public void print(Object obj) {}
         @Override public void println() {}
         @Override public void println(boolean x) {}
         @Override public void println(char x) {}
         @Override public void println(int x) {}
         @Override public void println(long x) {}
         @Override public void println(float x) {}
         @Override public void println(double x) {}
         @Override public void println(char[] x) {}
         @Override public void println(String x) {}
         @Override public void println(Object x) {}
         @Override public java.io.PrintStream printf(String format, Object... args) { return this; }
         @Override public java.io.PrintStream printf(java.util.Locale l, String format, Object... args) { return this; }
         @Override public java.io.PrintStream format(String format, Object... args) { return this; }
         @Override public java.io.PrintStream format(java.util.Locale l, String format, Object... args) { return this; }
         @Override public java.io.PrintStream append(CharSequence csq) { return this; }
         @Override public java.io.PrintStream append(CharSequence csq, int start, int end) { return this; }
         @Override public java.io.PrintStream append(char c) { return this; }
     };
      System.setOut(nullStream);
      System.setErr(nullStream);
   }
   
   public static void resetStreams(){
      System.setOut(oldOut);
      System.setErr(oldErr);
   }
}
