package com.grid.webservice;

import java.net.UnknownHostException;

import java.util.LinkedList;
import java.util.StringTokenizer;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBAddress;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

@Path("GridMongoWebservice")//package name
public class GridMongoWebservice {
	private static final int DB_PORT = 20100;//port
	private static final String DB_ADDRESS = "192.168.3.146";//address where database is located
	private static String SHA1_KEY = "detailedInformation.identifier.sha1";//path of sha1 for getDetails()
	private static final String FILES = "files";//name of collection
	private static final String DATABASE = "gridMongo";//name of database
	DBCursor cursor = null;
	String jsonresult = null;

	Gson gson = new GsonBuilder().setPrettyPrinting().create();

	@GET
	@Path("/getDetails/{i}")//method name
	@Produces(MediaType.TEXT_XML)
	public String getDetails(@PathParam("i") String tempsha1)
			throws JSONException {
		//get sha1 from url
		String sha1 = tempsha1;

		try {
			//conect to database
			DBAddress address = new DBAddress(DB_ADDRESS, DB_PORT, DATABASE);
			DB db = Mongo.connect(address);
			DBCollection coll = db.getCollection(FILES);
			//query sha1
			DBObject query = new BasicDBObject(SHA1_KEY, sha1);
			cursor = coll.find(query);

		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		assert cursor != null;

		String result = "";
		if (cursor.hasNext()) {
			
			result = cursor.next().toString();
			JsonParser parser = new JsonParser();
			JsonElement jsonelement = parser.parse(result);

			jsonresult = gson.toJson(jsonelement);

		} else {

			jsonresult = "NO RESULT";

		}

		return  "<GETDETAILS>"
				+"<DETAILS>" + jsonresult + "<DETAILS>"
				+"</GETDETAILS>";
	}

	@GET
	@Path("/getMetadata/{path}/{sha1}")
	@Produces(MediaType.TEXT_XML)
	public String getMetadata(@PathParam("path") String temppath,@PathParam("sha1") String tempsha1
			) throws JSONException {
		String sha1 = tempsha1;
		String path = temppath;
		LinkedList<String> pathlist = new LinkedList<String>();
		String meta = null;
		JsonElement vsdt = null;

		
		//reads the path then tokenize it to get the metadata value from the url
		StringTokenizer st2 = new StringTokenizer(path, ".",false);
		while (st2.hasMoreElements()) {
			String data = st2.nextToken();
			pathlist.add(data);
		}
		meta = String.valueOf(pathlist.get(3));
	
		
		try {

			DBAddress address = new DBAddress(DB_ADDRESS, DB_PORT, DATABASE);
			DB db = Mongo.connect(address);
			DBCollection coll = db.getCollection(FILES);
			DBObject query = new BasicDBObject(SHA1_KEY, sha1);
			cursor = coll.find(query);

		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		assert cursor != null;

		String result = "";
		if (cursor.hasNext()) {
			result = cursor.next().toString();
			JsonParser parser = new JsonParser();
			JsonElement jsonelement = parser.parse(result);
			
			  //search database
			  vsdt = jsonelement.getAsJsonObject().get("detailedInformation").getAsJsonObject().get("ns2:metadata").getAsJsonObject().get("ns2:meta").getAsJsonArray(); 
			  for(JsonElement jsonelement1 :vsdt.getAsJsonArray() ){
			  if(jsonelement1.getAsJsonObject().get("name").toString().trim().replaceAll("\"","").equalsIgnoreCase(meta)){ 
				  if(jsonelement1.getAsJsonObject().get("name").toString().trim().replaceAll("\"","").equalsIgnoreCase("sha512")){ 
					  jsonresult = jsonelement1.getAsJsonObject().get("binaryValue").toString().trim().replaceAll("\"",""); 
				  break; 
				  }
				  if(jsonelement1.getAsJsonObject().get("name").toString().trim().replaceAll("\"","").equalsIgnoreCase("fileSize")){ 
					  jsonresult = jsonelement1.getAsJsonObject().get("numericValues").toString().trim().replaceAll("\"",""); 
				  break; 
				  }
				  if(jsonelement1.getAsJsonObject().get("name").toString().trim().replaceAll("\"","").equalsIgnoreCase(meta)){ 
					  jsonresult = jsonelement1.getAsJsonObject().get("value").toString().trim().replaceAll("\"",""); 
			          break; 
				  }
				  else
					  jsonresult = "NOT FOUND";
				  break; 
			  }
		
			  else
				  jsonresult = "NOT FOUND";
			  }
			 

		} else {
			jsonresult = "NO RESULT!";
		}
		return "<GETMETA>"
				+"<META>" + jsonresult + "<META>"+
			   "</GETMETA>";
	
	}
	@GET
	@Path("/getInformation/{path}/{sha1}")
	@Produces(MediaType.TEXT_XML)
	public String getInformation(@PathParam("path") String temppath,@PathParam("sha1") String tempsha1
			) throws JSONException {
		String sha1 = tempsha1;
		String path = temppath;
		LinkedList<String> pathlist = new LinkedList<String>();
		String info = null;

		//reads the path then tokenize it to get the metadata value from the url
		StringTokenizer st2 = new StringTokenizer(path, ".",false);
		while (st2.hasMoreElements()) {
			String data = st2.nextToken();
			pathlist.add(data);
		}
		info = String.valueOf(pathlist.get(2));
	
		
		try {

			DBAddress address = new DBAddress(DB_ADDRESS, DB_PORT, DATABASE);
			DB db = Mongo.connect(address);
			DBCollection coll = db.getCollection(FILES);
			DBObject query = new BasicDBObject(SHA1_KEY, sha1);
			cursor = coll.find(query);

		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		assert cursor != null;

		String result = "";
		if (cursor.hasNext()) {
			result = cursor.next().toString();
			JsonParser parser = new JsonParser();
			JsonElement jsonelement = parser.parse(result);
			
			 jsonresult = jsonelement.getAsJsonObject().get("detailedInformation").getAsJsonObject().get("information").getAsJsonObject().get(info).getAsString(); 
			
		} else {
			jsonresult = "NO RESULT!";
		}
		return "<GETINFORMATION>"+
				"<INFORMATION>" + jsonresult + "<INFORMATION>"+
				"</GETINFORMATION>";
	
	}
}
