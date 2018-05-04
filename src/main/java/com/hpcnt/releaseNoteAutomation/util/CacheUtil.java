package com.hpcnt.releaseNoteAutomation.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

/**
 * Caching util for use account in jiraLogin from cache
 * 
 * @author owen151128
 *
 */
public class CacheUtil {

	private static final String HOME = "user.home";
	private static final String PATH = ".credentials/local_cache";

	/**
	 * Single-tone instance of {@link CacheUtil}
	 */
	private static CacheUtil instance;

	/**
	 * private constructor
	 */
	private CacheUtil() {
	}

	/**
	 * Single-tone pattern
	 * 
	 * @return instance
	 */
	public static synchronized CacheUtil getInstance() {
		if (instance == null)
			instance = new CacheUtil();
		return instance;
	}

	/**
	 * Save cache method
	 * 
	 * @param cache
	 */
	public void save(HashMap<String, String> cache) {
		File localFile = null;
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;

		try {
			localFile = new File(System.getProperty(HOME), PATH);
			if (!localFile.exists()) {
				localFile.createNewFile();
			}
			fos = new FileOutputStream(localFile);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(cache);
			oos.close();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("Error : local_cache file not found...\n");
		} catch (IOException e) {
			System.out.println("Error : cache IO Faild...\n");
		} finally {
			try {
				if (fos != null)
					fos.close();
				if (oos != null)
					oos.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Critical Error! please send this log to developer\n");
			}
		}
	}

	/**
	 * load cache method
	 * 
	 * @return HashMap cache
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, String> load() {
		File localFile = null;
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		HashMap<String, String> cache = new HashMap<>();

		try {
			localFile = new File(System.getProperty("user.home"), ".credentials/local_cache");
			if (!localFile.exists()) {
				System.out.println("Error : local_cache is null...\n");
				return null;
			}
			fis = new FileInputStream(localFile);
			ois = new ObjectInputStream(fis);
			cache = (HashMap<String, String>) ois.readObject();
			ois.close();
			fis.close();

			return cache;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("Error : cache temlate is IllegalState...\n");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("Error : local_cache file not found...\n");
		} catch (IOException e) {
			System.out.println("Error : cache IO Faild...\n");
		}
		return null;
	}
}
