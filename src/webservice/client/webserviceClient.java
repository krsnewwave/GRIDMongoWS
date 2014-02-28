package webservice.client;
import javax.ws.rs.core.MediaType;  
import com.sun.jersey.api.client.Client;  
import com.sun.jersey.api.client.ClientResponse;  
import com.sun.jersey.api.client.WebResource;  
import com.sun.jersey.api.client.config.ClientConfig;  
import com.sun.jersey.api.client.config.DefaultClientConfig;  
  
public class webserviceClient {  
    static final String REST_URI = "http://192.168.3.146:8080/GridMongoWS";  
    static final String GET_DETAILS = "GridMongoWebservice/getDetails/";  
    static final String GET_META = "GridMongoWebservice/getMeta/"; 
 
  
    public static void main(String[] args) {  
  
        String sha1 =  "1FF2C2AA31E0F702FC955C663BFF937EC9C5FE21";
        String path =  "detailedinformation.ns2:metadata.ns2:meta.filedescription";
  
        ClientConfig config = new DefaultClientConfig();  
        Client client = Client.create(config);  
        WebResource service = client.resource(REST_URI);  
  
       // WebResource addService = service.path("rest").path(GET_DETAILS+sha1);  
       // System.out.println("Response: " + getResponse(addService));  
       // System.out.println(" " + getOutputAsXML(addService));  
       // System.out.println("---------------------------------------------------");  
 
        WebResource subService = service.path("rest").path(GET_META+path+"/"+sha1);  
        System.out.println("Response: " + getResponse(subService));  
        System.out.println(" " + getOutputAsXML(subService));  
        System.out.println("---------------------------------------------------");  
    }  
  
    private static String getResponse(WebResource service) {  
        return service.accept(MediaType.TEXT_XML).get(ClientResponse.class).toString();  
    }  
  
    private static String getOutputAsXML(WebResource service) {  
        return service.accept(MediaType.TEXT_XML).get(String.class);  
    }  
}  

