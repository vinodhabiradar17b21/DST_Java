package com.increasingly.importbulkdata.util;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Shreehari.Padaki
 *
 */
public class FileUtil
{
	/**
	 * Appends contents to a file. Will create the file if it doesn't exist.
	 * 
	 * @param path
	 * @param filename
	 * @param content
	 * @throws Exception
	 */
	public static void appendFile(String path, String fileName, String content) throws Exception
	{
		try
		{
			// Create folders
			File files = new File(path);
			if (!files.exists())
			{
				files.mkdirs();
			}
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(path + fileName, true)));
			out.println(content);
			out.close();
		}
		catch (Exception e)
		{
			throw new Exception(e.getMessage());
		}
	}

	/**
	 * Copies an uploaded file to a destination specified
	 * 
	 * @return Boolean
	 * @throws Exception
	 */
	public static Boolean uploadFile(InputStream uploadedInputStream, String uploadedFileLocation) throws Exception
	{
		OutputStream out = null;
		try
		{
			out = new FileOutputStream(new File(uploadedFileLocation));
			int read = 0;
			byte[] bytes = new byte[1024];
			out = new FileOutputStream(new File(uploadedFileLocation));
			while ((read = uploadedInputStream.read(bytes)) != -1)
			{
				out.write(bytes, 0, read);
			}

			return true;
		}
		catch (Exception e)
		{
			throw new Exception(e.getMessage());
		}
		finally
		{
			if (out != null)
			{
				out.flush();
				out.close();
			}
		}
	}

	/**
	 * Decompress an uploaded Gzip file & copies to a destination specified
	 * 
	 * @return Boolean
	 * @throws Exception
	 */
	public static Boolean uploadGZipedFileAndDecompress(GZIPInputStream uploadedGZIPInputStream, String uploadedFileLocation) throws Exception
	{
		OutputStream out = null;
		try
		{
			out = new FileOutputStream(new File(uploadedFileLocation));
			int read = 0;
			byte[] bytes = new byte[1024];
			while ((read = uploadedGZIPInputStream.read(bytes)) != -1)
			{
				out.write(bytes, 0, read);
			}

			return true;
		}
		catch (Exception e)
		{
			throw new Exception(e.getMessage());
		}
		finally
		{
			if (out != null)
			{
				out.flush();
				out.close();
				uploadedGZIPInputStream.close();
			}
		}
	}

	/**
	 * Decompress an uploaded Zip file & copies to a destination specified
	 * 
	 * @return Boolean
	 * @throws Exception
	 */
	public static Boolean uploadZipedFileAndDecompress(ZipInputStream uploadedZipInputStream, String uploadFileLocation) throws Exception
	{
		OutputStream out = null;
		try
		{
			// now write zip file in extracted file
			byte[] buff = new byte[1024];
			while ((uploadedZipInputStream.getNextEntry()) != null)
			{
				out = new FileOutputStream(uploadFileLocation);
				int l = 0;
				while ((l = uploadedZipInputStream.read(buff)) > 0)
				{
					out.write(buff, 0, l);
				}
			}

			return true;
		}
		catch (Exception e)
		{
			throw new Exception(e.getMessage());
		}
		finally
		{
			if (out != null)
			{
				out.flush();
				out.close();
				uploadedZipInputStream.close();
			}
		}
	}

	/**
	 * Decompress a Zip file & copies to a destination specified
	 * 
	 * @return Boolean
	 * @throws Exception
	 */
	public static Boolean uploadZipedFileAndDecompress(ZipInputStream uploadedZipInputStream, ZipEntry uploadedZipInput, String uploadFileLocation) throws Exception
	{
		OutputStream out = null;
		try
		{
			// now write extracted file
			byte[] buff = new byte[1024];
			if (uploadedZipInput != null)
			{
				out = new FileOutputStream(uploadFileLocation);
				int l = 0;
				while ((l = uploadedZipInputStream.read(buff)) > 0)
				{
					out.write(buff, 0, l);
				}
			}

			return true;
		}
		catch (Exception e)
		{
			throw new Exception(e.getMessage());
		}
		finally
		{
			if (out != null)
			{
				out.flush();
				out.close();
				uploadedZipInputStream.close();
			}
		}
	}

	/**
	 * Deletes an file from specified specified
	 * 
	 * @return Boolean
	 * @throws Exception
	 */
	public static void deleteFile(String fileLocation) throws Exception
	{
		try
		{
			File f = new File(fileLocation);

			if (f.exists())
			{
				f.delete();
			}
		}
		catch (Exception e)
		{
			throw new Exception(e.getMessage());
		}
	}

	/**
	 * Parses the XML content to DOM, which will make the content easier to iterate.
	 * 
	 * @param xmlPath - URL / Absolute path of the XML file
	 *
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static Document xmlToDoc(String xmlPath) throws ParserConfigurationException, SAXException, IOException
	{
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = null;
			builder = factory.newDocumentBuilder();
			return builder.parse(xmlPath);
		}
		catch (Exception e)
		{
			return null;
		}
	}

	/**
	 * Returns the content/value of tag given from Element. If tag not found, returns attribute's value.
	 * 
	 * @param tagName
	 * @param element
	 * @return
	 */
	public static String getString(String name, Element element)
	{
		NodeList list = element.getElementsByTagName(name);
		if (list != null && list.getLength() > 0)
		{
			NodeList subList = list.item(0).getChildNodes();
			if (subList != null && subList.getLength() > 0)
			{
				return subList.item(0).getNodeValue();
			}
		}
		else if (element.hasAttribute(name))
		{
			return element.getAttribute(name);
		}
		else if (!element.hasAttribute(name))
		{
			NodeList nl = element.getElementsByTagName("*");
			for (int i = 0; i < nl.getLength(); i++)
			{
				Element innerElement = (Element) nl.item(i);
				if (innerElement.hasAttribute(name))
				{
					return innerElement.getAttribute(name);
				}
			}
		}
		return null;
	}
}