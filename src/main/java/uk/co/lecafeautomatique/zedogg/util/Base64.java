package uk.co.lecafeautomatique.zedogg.util;

import java.io.PrintStream;

public class Base64 {
  private String lineSeparator = System.getProperty("line.separator");

  private int lineLength = 72;

  static final char[] valueToChar = new char[64];

  static final int[] charToValue = new int[256];
  static final int IGNORE = -1;
  static final int PAD = -2;
  private static final boolean debug = false;

  public String encode(byte[] b) {
    int outputLength = (b.length + 2) / 3 * 4;

    if (this.lineLength != 0) {
      int lines = (outputLength + this.lineLength - 1) / this.lineLength - 1;
      if (lines > 0) {
        outputLength += lines * this.lineSeparator.length();
      }

    }

    StringBuffer sb = new StringBuffer(outputLength);

    int linePos = 0;

    int len = b.length / 3 * 3;
    int leftover = b.length - len;
    for (int i = 0; i < len; i += 3) {
      linePos += 4;
      if (linePos > this.lineLength) {
        if (this.lineLength != 0) {
          sb.append(this.lineSeparator);
        }
        linePos = 4;
      }

      int combined = b[(i + 0)] & 0xFF;
      combined <<= 8;
      combined |= b[(i + 1)] & 0xFF;
      combined <<= 8;
      combined |= b[(i + 2)] & 0xFF;

      int c3 = combined & 0x3F;
      combined >>>= 6;
      int c2 = combined & 0x3F;
      combined >>>= 6;
      int c1 = combined & 0x3F;
      combined >>>= 6;
      int c0 = combined & 0x3F;

      sb.append(valueToChar[c0]);
      sb.append(valueToChar[c1]);
      sb.append(valueToChar[c2]);
      sb.append(valueToChar[c3]);
    }

    switch (leftover) {
    case 0:
    default:
      break;
    case 1:
      linePos += 4;
      if (linePos > this.lineLength) {
        if (this.lineLength != 0) {
          sb.append(this.lineSeparator);
        }
        linePos = 4;
      }

      sb.append(encode(new byte[] { b[len], 0, 0 }).substring(0, 2));

      sb.append("==");
      break;
    case 2:
      linePos += 4;
      if (linePos > this.lineLength) {
        if (this.lineLength != 0) {
          sb.append(this.lineSeparator);
        }
        linePos = 4;
      }

      sb.append(encode(new byte[] { b[len], b[(len + 1)], 0 }).substring(0, 3));

      sb.append("=");
    }

    if (outputLength != sb.length()) {
      System.out.println("oops: minor program flaw: output length mis-estimated");
      System.out.println("estimate:" + outputLength);
      System.out.println("actual:" + sb.length());
    }
    return sb.toString();
  }

  public byte[] decode(String s) {
    byte[] b = new byte[s.length() / 4 * 3];

    int cycle = 0;

    int combined = 0;

    int j = 0;

    int len = s.length();
    int dummies = 0;
    for (int i = 0; i < len; i++) {
      int c = s.charAt(i);
      int value = c <= 255 ? charToValue[c] : -1;

      switch (value) {
      case -1:
        break;
      case -2:
        value = 0;
        dummies++;
      default:
        switch (cycle) {
        case 0:
          combined = value;
          cycle = 1;
          break;
        case 1:
          combined <<= 6;
          combined |= value;
          cycle = 2;
          break;
        case 2:
          combined <<= 6;
          combined |= value;
          cycle = 3;
          break;
        case 3:
          combined <<= 6;
          combined |= value;

          b[(j + 2)] = (byte) combined;
          combined >>>= 8;
          b[(j + 1)] = (byte) combined;
          combined >>>= 8;
          b[j] = (byte) combined;
          j += 3;
          cycle = 0;
        }
        break;
      }
    }

    if (cycle != 0) {
      throw new ArrayIndexOutOfBoundsException("Input to decode not an even multiple of 4 characters; pad with =.");
    }
    j -= dummies;
    if (b.length != j) {
      byte[] b2 = new byte[j];
      System.arraycopy(b, 0, b2, 0, j);
      b = b2;
    }
    return b;
  }

  public void setLineLength(int length) {
    this.lineLength = (length / 4 * 4);
  }

  public void setLineSeparator(String lineSeparator) {
    this.lineSeparator = lineSeparator;
  }

  public static void show(byte[] b) {
    for (int i = 0; i < b.length; i++) {
      System.out.print(Integer.toHexString(b[i] & 0xFF) + " ");
    }
    System.out.println();
  }

  public static void display(byte[] b) {
    for (int i = 0; i < b.length; i++) {
      System.out.print((char) b[i]);
    }
    System.out.println();
  }

  public static void main(String[] args) {
  }

  static {
    for (int i = 0; i <= 25; i++) {
      valueToChar[i] = (char) (65 + i);
    }
    for (int i = 0; i <= 25; i++) {
      valueToChar[(i + 26)] = (char) (97 + i);
    }
    for (int i = 0; i <= 9; i++)
      valueToChar[(i + 52)] = (char) (48 + i);
    valueToChar[62] = '+';
    valueToChar[63] = '/';

    for (int i = 0; i < 256; i++) {
      charToValue[i] = -1;
    }

    for (int i = 0; i < 64; i++) {
      charToValue[valueToChar[i]] = i;
    }

    charToValue[61] = -2;
  }
}
