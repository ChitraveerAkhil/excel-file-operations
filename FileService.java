package org.dummy.fileOperation;

import java.io.InputStream;
import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/fileService")
public class FileService {

	DuplicatesRemover duplicatesRemover = new DuplicatesRemover();

	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response uploadFile(@FormDataParam("columnHeading") String columnHeading,
			@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail) {
		// save it
		// duplicatesRemover.writeToFile(uploadedInputStream, uploadedFileLocation);

		String path = duplicatesRemover.removeDuplicates(columnHeading, uploadedInputStream, fileDetail);
		HashMap<String, String> obj = new HashMap<>();
		System.out.println(path);
		obj.put("path", path);
		ObjectMapper mapper = new ObjectMapper();
		String json = null;
		try {
			json = mapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Response.ok(json, MediaType.APPLICATION_JSON).build();
	}

	@POST
	@Path("/errorLog")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response errLog(@FormParam("error") String error, @FormParam("clazz") String clazz)
			throws JsonProcessingException {
		HashMap<String, String> obj = new HashMap<>();
		obj.put("status", "1");
		obj.put("error", error);
		obj.put("clazz", clazz);
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(obj);
		return Response.ok(json, MediaType.APPLICATION_JSON).build();
	}

	@GET
	@Path("/downloadFile")
	@Produces("application/vnd.ms-excel")
	public Response downloadFile(@QueryParam("fileName") String fileName) {
		System.out.println(fileName);
		Response response = duplicatesRemover.retriveFile(fileName);
		return response;
	}

	@GET
	@Path("/test")
	public Response test() throws JsonProcessingException {
		HashMap<String, String> obj = new HashMap<>();
		obj.put("test", "test");
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(obj);
		return Response.ok(json, MediaType.APPLICATION_JSON).build();
	}
}
