package com.increasingly.recommender.utils;


import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;


public class AESBouncyCastle
{
	private static final Logger logger = LogManager.getLogger(AESBouncyCastle.class.getName());

	public static AESBouncyCastle instance = null;
	private final String AES = "AES";
	private final String AESString = AES + "/" + Mode.CBC.name() + "/" + Padding.PKCS5PADDING.name();

	private String keyStr;
	private Cipher encryptCipher = null;
	private Cipher decryptCipher = null;
	
	byte[] iv = {65, 105, 114, 70, 114, 97, 109, 101, 66, 101, 103, 105, 110, 105, 110, 103};

	
	public static AESBouncyCastle getInstance(String Keystr) throws Exception
	{
		if (instance == null)
		{
			try
			{
				Security.addProvider(new BouncyCastleProvider());
				instance = new AESBouncyCastle();
				instance.keyStr = Keystr;
				instance.inititialize();
				logger.info("Initialized AES");

			}
			catch (InvalidKeyException e)
			{
				throw new Exception(e);
			}
			catch (NoSuchAlgorithmException e)
			{
				throw new Exception(e);
			}
			catch (NoSuchProviderException e)
			{
				throw new Exception(e);
			}
			catch (NoSuchPaddingException e)
			{
				throw new Exception(e);
			}
		}
		return instance;
	}

	private void inititialize() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException
	{
		// generate the key
		generateKey();

	}

	private void generateKey() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException
	{

		IvParameterSpec ips = new IvParameterSpec(iv);
		SecretKeySpec key = new SecretKeySpec(keyStr.getBytes("UTF-8"), "AES");
		
		encryptCipher = Cipher.getInstance(AESString, "BC");
		encryptCipher.init(Cipher.ENCRYPT_MODE, key, ips);

		// Initialize the decryption cipher
		decryptCipher = Cipher.getInstance(AESString, "BC");
		decryptCipher.init(Cipher.DECRYPT_MODE, key, ips);
		
		logger.info("generate key..");

	}

	public String decrypt(String encyptedText) throws Exception
	{

		byte[] cryptedbytes = null;
		if (decryptCipher == null)
		{
			System.out.println("decryptCipher is null");
			return null;
		}
		try
		{
			byte[] base64Decoded = Base64.decodeBase64(encyptedText.getBytes());
			cryptedbytes = decryptCipher.doFinal(base64Decoded);

		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			throw e;
		}

		if (cryptedbytes != null)
		{
			return new String(cryptedbytes, "UTF-8");
		}
		else
		{
			return null;
		}
	}

	/*
	 * input UTF-8 string return Base64 Encoded String
	 */
	public String encrypt(String plainText)
	{
		byte[] cryptedbytes = null;
		if (encryptCipher == null)
		{
			System.out.println("encryptCipher is null");
			return null;
		}

		try
		{
			cryptedbytes = encryptCipher.doFinal(plainText.getBytes("UTF-8"));
		}
		catch (IllegalBlockSizeException e)
		{
			logger.error("Problem", e);
		}
		catch (BadPaddingException e)
		{
			logger.error("Problem", e);
		}
		catch (UnsupportedEncodingException e)
		{
			logger.error("Problem", e);
		}
		if (cryptedbytes != null)
		{
			return new String (Base64.encodeBase64(cryptedbytes));
		}
		else
		{
			return null;
		}
	}

	/*
	 * 
	 */
	private enum Padding
	{
		PKCS5PADDING("PKCS5Padding"), NOPADDING("NoPadding");

		private String description;

		/**
		 * Initialize the Padding enum with the description used for Sun's libraries.
		 * 
		 * @param description
		 *            the description used for Sun's libraries.
		 */
		Padding(String description)
		{
			this.description = description;
		}

		public String toString()
		{
			return this.description;
		}
	} 

	private enum Mode
	{
		ECB("ECB"), CBC("CBC");

		private String description;

		/**
		 * Initialize the Mode enum with the description of the mode to be used.
		 * 
		 * @param description
		 *            the description of the mode used.
		 */
		Mode(String description)
		{
			this.description = description;
		} 

		public String toString()
		{
			return this.description;
		}
	}
}
