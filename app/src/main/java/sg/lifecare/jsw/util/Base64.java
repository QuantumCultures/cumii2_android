package sg.lifecare.jsw.util;

public class Base64 {
	static char[] map1 = new char[64];
	static{
		int  i  = 0;
		for(char c = 'A'; c <= 'Z'; c++) map1[i++] = c;   
		for(char c = 'a'; c <= 'z'; c++) map1[i++] = c;
		for(char c = '0'; c <= '9'; c++) map1[i++] = c;
		
		map1[i++] = '+';
		map1[i++] = '/';
	}
	static byte[] map2 = new byte[128];
	static {
      for(int i=0; i<map2.length; i++) map2[i] = -1;
      for(int i=0; i<64; i++) map2[map1[i]] =(byte)i;
	}


	public final static String base64Encode(String str){
		return base64Encode(str.getBytes());
	}
	public final static String base64Encode(byte[] in) 
	{
		int     iLen      = in.length;
		int     oDataLen  = (iLen*4 + 2 ) / 3;	// output length without padding
		int     oLen      = ((iLen + 2) /3)*4;	// output length including padding
		char[]  out       = new char[oLen];
		
		int     ip        = 0;
		int     op        = 0;
		int     i0;
		int     i1;
		int     i2;
		int     o0;
		int     o1;
		int     o2;
		int     o3;
		while(ip <iLen)
		{
			i0 = in[ip++] & 0xff;
			i1 = ip < iLen ? in[ip++] & 0xff : 0;
			i2 = ip < iLen ? in[ip++] & 0xff : 0;
			o0 = i0 >>> 2;
			o1 = ( ( i0 & 3 ) << 4 ) | ( i1 >>> 4 );
			o2 = ( ( i1 & 0xf ) << 2 ) | ( i2 >>> 6 );
			o3 = i2 & 0x3F;	
			out[op++] = map1[o0];
			out[op++] = map1[o1];
			out[op] = op < oDataLen ? map1[o2] : '=';
			op++;
			out[op] = op < oDataLen ? map1[o3] : '=';
			op++;
		}
		return new String(out);
	}
	
	public static byte[] base64Decode(byte[] in, int iOff, int iLen) {
		   if (iLen%4 != 0) throw new IllegalArgumentException ("Length of Base64 encoded input string is not a multiple of 4.");
		   while (iLen > 0 && in[iOff+iLen-1] == '=') iLen--;
		   int oLen = (iLen*3) / 4;
		   byte[] out = new byte[oLen];
		   int ip = iOff;
		   int iEnd = iOff + iLen;
		   int op = 0;
		   while (ip < iEnd) {
		      int i0 = in[ip++];
		      int i1 = in[ip++];
		      int i2 = ip < iEnd ? in[ip++] : 'A';
		      int i3 = ip < iEnd ? in[ip++] : 'A';
		      if (i0 > 127 || i1 > 127 || i2 > 127 || i3 > 127)
		         throw new IllegalArgumentException("Illegal character in Base64 encoded data.");
		      int b0 = map2[i0];
		      int b1 = map2[i1];
		      int b2 = map2[i2];
		      int b3 = map2[i3];
		      if (b0 < 0 || b1 < 0 || b2 < 0 || b3 < 0)
		         throw new IllegalArgumentException("Illegal character in Base64 encoded data.");
		      int o0 = ( b0       <<2) | (b1>>>4);
		      int o1 = ((b1 & 0xf)<<4) | (b2>>>2);
		      int o2 = ((b2 &   3)<<6) |  b3;
		      out[op++] = (byte)o0;
		      if (op<oLen) out[op++] = (byte)o1;
		      if (op<oLen) out[op++] = (byte)o2; 
		   }
		   return out;
	}

	public static final int byteArrayToInt_Little(byte byt[], int nBeginPos)
	{
		return (0xff & byt[nBeginPos]) | (0xff & byt[nBeginPos+1])<<8 | (0xff & byt[nBeginPos+2])<<16 | (0xff & byt[nBeginPos+3])<<24;
	}
	
	static final int byteArrayToInt(byte byt[])
	{
		if(byt.length == 1) 	return 0xff & byt[0];
		else if(byt.length == 2)return (0xff & byt[0]) << 8 | 0xff & byt[1];
		else if(byt.length == 4)
			return (0xff & byt[0]) << 24 | (0xff & byt[1]) << 16 | (0xff & byt[2]) << 8 | 0xff & byt[3];
		else return 0;
	}
	
	static final byte[] intToByteArray(int value) {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value
        };
	}

	static final byte[] shortToByteArray(short value) {
        return new byte[] {
                (byte)(value >>> 8),
                (byte)value
        };
	}
	
	static byte[] intToByteArray(int value, int arrayLength)  
    {
	    byte[] bTemp = new byte[arrayLength];
	    for (int i = 0; i < arrayLength; i++)  
	    {  
	    	bTemp[i] = (byte)((value >> (i * 8)) & 0xff);     
	    }
	    return bTemp;  
	}
}
