package de.dagere.kopeme;

import java.io.PrintStream;

public class OutputStreamUtil {
   private static final PrintStream oldOut = System.out;
   private static final PrintStream oldErr = System.err;

   public static void redirectToNullStream() {
      final java.io.PrintStream nullStream = new java.io.PrintStream(new java.io.OutputStream() {
         @Override
         public void write(final int b) {
         }
      }) {
         @Override
         public void flush() {
         }

         @Override
         public void close() {
         }

         @Override
         public void write(final int b) {
         }

         @Override
         public void write(final byte[] b) {
         }

         @Override
         public void write(final byte[] buf, final int off, final int len) {
         }

         @Override
         public void print(final boolean b) {
         }

         @Override
         public void print(final char c) {
         }

         @Override
         public void print(final int i) {
         }

         @Override
         public void print(final long l) {
         }

         @Override
         public void print(final float f) {
         }

         @Override
         public void print(final double d) {
         }

         @Override
         public void print(final char[] s) {
         }

         @Override
         public void print(final String s) {
         }

         @Override
         public void print(final Object obj) {
         }

         @Override
         public void println() {
         }

         @Override
         public void println(final boolean x) {
         }

         @Override
         public void println(final char x) {
         }

         @Override
         public void println(final int x) {
         }

         @Override
         public void println(final long x) {
         }

         @Override
         public void println(final float x) {
         }

         @Override
         public void println(final double x) {
         }

         @Override
         public void println(final char[] x) {
         }

         @Override
         public void println(final String x) {
         }

         @Override
         public void println(final Object x) {
         }

         @Override
         public java.io.PrintStream printf(final String format, final Object... args) {
            return this;
         }

         @Override
         public java.io.PrintStream printf(final java.util.Locale l, final String format, final Object... args) {
            return this;
         }

         @Override
         public java.io.PrintStream format(final String format, final Object... args) {
            return this;
         }

         @Override
         public java.io.PrintStream format(final java.util.Locale l, final String format, final Object... args) {
            return this;
         }

         @Override
         public java.io.PrintStream append(final CharSequence csq) {
            return this;
         }

         @Override
         public java.io.PrintStream append(final CharSequence csq, final int start, final int end) {
            return this;
         }

         @Override
         public java.io.PrintStream append(final char c) {
            return this;
         }
      };
      System.setOut(nullStream);
      System.setErr(nullStream);
   }

   public static void resetStreams() {
      System.setOut(oldOut);
      System.setErr(oldErr);
   }
}
