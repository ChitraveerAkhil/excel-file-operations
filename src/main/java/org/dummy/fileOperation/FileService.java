package org.dummy.fileOperation;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

@Path("/file")
public class FileService {

	DuplicatesRemover duplicatesRemover = new DuplicatesRemover();

	@POST
	@Path("/remover")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces()
	public Response removeDuplicates(@QueryParam("columnIdentifier") String columnHeading,
			@FormDataParam("file") FileInputStream excelFile,
			@FormDataParam("file") FormDataContentDisposition fileDetail)
			throws EncryptedDocumentException, InvalidFormatException, IOException {

		String filePath = duplicatesRemover.removeDuplicates(columnHeading, excelFile, fileDetail);
		HashMap<String, String> entity = new HashMap<>();
		entity.put("filePath", filePath);
		entity.put("status", "success");
		Response resp = Response.ok(entity, MediaType.APPLICATION_JSON).build();

		return resp;

	}

	@GET
	@Path("/downloadFile")
	@Produces("application/vnd.ms-excel")
	public Response downloadFile(@QueryParam("fileName") String fileName) {
		Response response = duplicatesRemover.retriveFile(fileName);
		return response;
	}

	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail) {

		String uploadedFileLocation = "/home/akhil" + fileDetail.getFileName();

		// save it
		duplicatesRemover.writeToFile(uploadedInputStream, uploadedFileLocation);

		String output = "File uploaded to : " + uploadedFileLocation;

		return Response.status(200).entity(output).build();

	}

}
