package au.csiro.fhir.transforms.helper;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import au.csiro.fhir.transforms.helper.atomio.Entry;

public class FeedClient {

	final Logger logger = Logger.getLogger(FeedClient.class.getName());


	String syndCientID;
	String syndSecret;
	String realm;
	String authServer;


	String feed;

	Client client = null;

	public FeedClient(String feedName) {
		feed = feedName;
		Feature feature = new LoggingFeature(logger, Level.INFO, null, null);
		client = ClientBuilder.newBuilder().register(feature).build();

	}

	public FeedClient(String feedName, String cid, String pwd, String realm, String auth) {
		syndCientID = cid;
		syndSecret = pwd;
		this.realm = realm;
		authServer = auth;

		feed = feedName;
		Feature feature = new LoggingFeature(logger, Level.INFO, null, null);
		client = ClientBuilder.newBuilder().register(feature).build();

	}

	public void checkResponse(Response response) {
		if (response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL) == false) {
			throw new RuntimeException("Failed : HTTP error code : " + response.getStatus() + " : " + response.toString());
		}
	}

	public void deleteEntryFromFeed(String id) {

		System.out.println("delete "  + id);

		WebTarget webTarget = client.target(feed);

		WebTarget deleteEntryTarget = webTarget.path("entry/" + id);

		Invocation.Builder invocationBuilder = deleteEntryTarget.request();

		Response response = invocationBuilder.header(HttpHeaders.AUTHORIZATION, "Bearer " + getToken()).delete();

		checkResponse(response);

		logger.info(response.toString());
	}

	public String searchEntryByIdentifierAndVersion( String contentItemIdentifier, String contentItemVersion) {

		WebTarget webTarget = client.target(feed);

		WebTarget findEntryTarget = webTarget.path("");

		Invocation.Builder invocationBuilder = findEntryTarget.request();

		Response response = invocationBuilder.header(HttpHeaders.AUTHORIZATION, "Bearer " + getToken()).get();

		JsonObject jobj = new Gson().fromJson(response.readEntity(String.class), JsonObject.class);

		for(JsonElement e  : (JsonArray) jobj.get("entries")) {
			String identifier = e.getAsJsonObject().get("contentItemIdentifier").toString().replaceAll("\"", "");
			String version = e.getAsJsonObject().get("contentItemVersion").toString().replaceAll("\"", "");
			String id = e.getAsJsonObject().get("id").toString().replaceAll("\"", "");
			if(identifier.equals(contentItemIdentifier) && version.equals(contentItemVersion) ) {
				System.out.println("FOUND" + id +"\t" + identifier + "\t" + version);
				return id;
			}

		}

		return null;
	}
	
	

	public void createEntryToNHSFeed(File entryFile, File bodyFile) throws IOException {

		WebTarget webTarget = client.target(feed);

		WebTarget createEntryTarget = webTarget.path("entry/");

		createEntryTarget.register(MultiPartFeature.class);

		final FileDataBodyPart entryFilePart = new FileDataBodyPart("entry", entryFile, MediaType.APPLICATION_JSON_TYPE);
		final FileDataBodyPart bodyFilePart = new FileDataBodyPart("file", bodyFile);

		FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
		final FormDataMultiPart multiPartEntity = (FormDataMultiPart) formDataMultiPart.bodyPart(bodyFilePart).bodyPart(entryFilePart);
		Response response = createEntryTarget.request()
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + getToken())
				//.header(HttpHeaders.CONTENT_TYPE, "multipart/form-data")
				.post(Entity.entity(multiPartEntity, multiPartEntity.getMediaType()));

		checkResponse(response);
		logger.info(response.toString());
		formDataMultiPart.close();
	}

	/**
	 *
	 * Delete exiting one and recreate
	 */
	public void updateEntryToNHSFeed(Entry entry ,File entryFile, File bodyFile) throws IOException {

		String id = searchEntryByIdentifierAndVersion(entry.getContentItemIdentifier(), entry.getContentItemVersion());
		if(id!=null) {
			deleteEntryFromFeed(id);
		}
		createEntryToNHSFeed(entryFile, bodyFile);
	}

	public void deleteEntryFromNHSFeed(Entry entry ,File entryFile, File bodyFile) throws IOException {

		String id = searchEntryByIdentifierAndVersion(entry.getContentItemIdentifier(), entry.getContentItemVersion());
		if(id!=null) {
			deleteEntryFromFeed(id);
		}
	}

	private String getToken() {
		String token = "";
		Logger logger = Logger.getLogger(FeedClient.class.getName());
		Feature feature = new LoggingFeature(logger, Level.INFO, null, null);
		Client client = ClientBuilder.newBuilder().register(feature).build();
		WebTarget webTarget = client.target(authServer);
		WebTarget getTokenTarget = webTarget.path("auth/realms/" + realm + "/protocol/openid-connect/token");
		System.out.println(getTokenTarget.getUri());
		Form form = new Form();
		form.param("client_id", syndCientID);
		form.param("client_secret", syndSecret);
		form.param("grant_type", "client_credentials");
		Entity<Form> entity = Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED);
		Invocation.Builder invocationBuilder = getTokenTarget.request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.post(entity);
		checkResponse(response);

		JsonObject jobj = new Gson().fromJson(response.readEntity(String.class), JsonObject.class);
		token = jobj.get("access_token").toString().replaceAll("\"", "");
		return token;
	}

}