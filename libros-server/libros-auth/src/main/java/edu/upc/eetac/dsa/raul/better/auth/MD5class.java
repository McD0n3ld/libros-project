package edu.upc.eetac.dsa.raul.better.auth;

import java.security.*;
import java.math.*;

public class MD5class {

	public static String GetMD5(String mensaje) {
		String md5 = null;
		try {
			MessageDigest m = MessageDigest.getInstance("MD5");
			m.update(mensaje.getBytes(), 0, mensaje.length());
			md5 = new BigInteger(1, m.digest()).toString(16);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (md5);
	}

}
