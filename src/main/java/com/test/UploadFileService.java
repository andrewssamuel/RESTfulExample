package com.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

@Path("/file")
public class UploadFileService {

	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(
		@FormDataParam("file") InputStream uploadedInputStream,
		@FormDataParam("file") FormDataContentDisposition fileDetail) {
		
		System.out.println("File......."+fileDetail);

		String uploadedFileLocation = "/Users/andrewssamuel/workspace/upload/" + fileDetail.getFileName();

		// save it
		String test = convertToJson(uploadedInputStream, uploadedFileLocation);

		String output = "File uploaded to : " + test;

		return Response.status(200).entity(output).build();

	}
	
	private String convertToJson(InputStream uploadedInputStream,
		String uploadedFileLocation){
		//FileInputStream inp = new FileInputStream( file );
		Workbook workbook = null;
		try {
			workbook = WorkbookFactory.create( uploadedInputStream );
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Get the first Sheet.
		Sheet sheet = workbook.getSheetAt( 0 );

		    // Start constructing JSON.
		    JSONObject json = new JSONObject();

		    // Iterate through the rows.
		    JSONArray rows = new JSONArray();
		    for ( Iterator<Row> rowsIT = sheet.rowIterator(); rowsIT.hasNext(); )
		    {
		        Row row = rowsIT.next();
		        JSONObject jRow = new JSONObject();

		        // Iterate through the cells.
		        JSONArray cells = new JSONArray();
		        for ( Iterator<Cell> cellsIT = row.cellIterator(); cellsIT.hasNext(); )
		        {
		            Cell cell = cellsIT.next();
		            cells.put( cell.getStringCellValue() );
		        }
		        try {
					jRow.put( "cell", cells );
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        rows.put( jRow );
		    }

		    // Create the JSON.
		    try {
				json.put( "rows", rows );
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		// Get the JSON text.
		return json.toString();
	}

	// save uploaded file to new location
	private void writeToFile(InputStream uploadedInputStream,
		String uploadedFileLocation) {

		try {
			OutputStream out = new FileOutputStream(new File(
					uploadedFileLocation));
			int read = 0;
			byte[] bytes = new byte[1024];

			out = new FileOutputStream(new File(uploadedFileLocation));
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

}