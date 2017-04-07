package com.sample.samsung.api.mongo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.Key;
import java.util.Base64;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class MongoDB {

	public static String secure(boolean isEncrypt, String text, String key) {
		try {
			key = "Bar12345Bar12345"; // 128 bit key
			// Create key and cipher
			Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
			Cipher cipher = Cipher.getInstance("AES");

			// encrypt the text
			if (isEncrypt) {
				cipher.init(Cipher.ENCRYPT_MODE, aesKey);
				byte[] encrypted = cipher.doFinal(text.getBytes());

				Base64.Encoder encoder = Base64.getEncoder();
				String encryptedText = encoder.encodeToString(encrypted);

				// the encrypted String
				System.out.println("encrypted:" + encryptedText);
				return encryptedText;
			} else {
				// decrypt the text
				Base64.Decoder decoder = Base64.getDecoder();
				byte[] encryptedTextByte = decoder.decode(text);
				cipher.init(Cipher.DECRYPT_MODE, aesKey);
				String decrypted = new String(cipher.doFinal(encryptedTextByte));
				System.err.println("decrypted:" + decrypted);
				return decrypted;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) throws FileNotFoundException, IOException {
		MongoClient client = new MongoClient("localhost", 27017);
		DB db = client.getDB("test");
		DBCollection collection = db.getCollection("buyerprop");
		BufferedReader col = new BufferedReader(
				new FileReader("C:\\Users\\d.dandu\\Documents\\source_files\\buyerprop_mcvisid_columns.txt"));
		try {
			String columnLine = col.readLine();
			String[] columns = columnLine.split("\\|");

			BufferedReader br = new BufferedReader(new FileReader(
					"C:\\Users\\d.dandu\\Documents\\source_files\\buyerprop_mcvisid_20170404_224715Z.txt"));
			try {
				//StringBuilder sb = new StringBuilder();
				String line;
				line = br.readLine();

				while (line != null) {
					line = br.readLine();

					if (line != null) {
						System.out.println(line);
						String[] row = line.split("\\|");

						DBObject document = new BasicDBObject();
						document.put(columns[0], secure(true, row[0], ""));
						document.put(columns[1], row[1]);
						document.put(columns[2], row[2]);
						document.put(columns[3], row[3]);
						collection.insert(document);

					}

				}
				List<DBObject> documents = collection.find().toArray();

				for (DBObject document : documents) {
					System.out.println(secure(false, document.get(columns[0]).toString(), ""));
					System.out.println(document.get(columns[1]));
					System.out.println(document.get(columns[2]));
					System.out.println(document.get(columns[3]));
				}

			} finally {
				br.close();
			}
		} finally {
			col.close();
		}
	}

}
