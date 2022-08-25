package com.data;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.HashMap;

import com.exceptions.InternalServerErrorException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.services.customers.CustomerEntity;

/**
 * This is the database layer. The responsibility of the database layer is to store domain objects.
 * @author idtaleb
 */
public class JsonDataBase {
	
	private Gson gson = new GsonBuilder().create();
	private static HashMap<String, CustomerEntity> database;
	
	public JsonDataBase() {
		database = new HashMap<String, CustomerEntity>();
	}
	
	/**
	 * Save the data into the json data base
	 * @param customer The customer to save into the database
	 * @param db The database where the data are modified
	 * @throws InternalServerErrorException When an exception is raised during the data save.
	 */
	public void addCustomerData(CustomerEntity customer) throws InternalServerErrorException {
		try (Writer writer = new FileWriter(DataBaseInfo.DATA_BASE_PATH)) {
			database.put(customer.getUsername(), customer);
			gson.toJson(database, writer);
			writer.close();
		} catch (JsonIOException e) {
			throw new InternalServerErrorException("An internal error occured when registering a user.");
		} catch (IOException e) {
			throw new InternalServerErrorException("An internal error occured.");
		}
	}
	
	/**
	 * Delete A customer from JSON data
	 * @param customer The customer to delete
	 * @param db The database where the data are modified
	 * @throws InternalServerErrorException When an exception is raised during the data save.
	 */
	public void deleteCustomerData(CustomerEntity customer) throws InternalServerErrorException {
		try (Writer writer = new FileWriter(DataBaseInfo.DATA_BASE_PATH)) {
			database.remove(customer.getUsername());
			gson.toJson(database, writer);
			writer.close();
		} catch (JsonIOException e) {
			throw new InternalServerErrorException("An internal error occured when registering a user.");
		} catch (IOException e) {
			throw new InternalServerErrorException("An internal error occured.");
		}
	}
	
	/**
	 * Read data from JSON data base and construct a hashMap (key, values) matching the JSON data.
	 * @return returns the hashMap (key, values) matching the JSON data.
	 */
	public HashMap<String, CustomerEntity> readJSONData() {
		try (Reader reader = new FileReader(DataBaseInfo.DATA_BASE_PATH)){
			Type listType = new TypeToken<HashMap<String, CustomerEntity>>(){}.getType();
			database = gson.fromJson(reader, listType);
		    if (database == null || database.isEmpty()) 
		    	return new HashMap<String, CustomerEntity>();
		} catch (JsonSyntaxException e) {
			System.out.println("Json syntax error in "+DataBaseInfo.DATA_BASE_PATH);
			e.printStackTrace();
		} catch (JsonIOException e) {
			System.out.println("Json I/O error");
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			System.out.println("No database found: " + DataBaseInfo.DATA_BASE_PATH);
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return database;	
	}

}
