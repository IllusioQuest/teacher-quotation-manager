package schulmanager.api;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import service.DataManager;

public class UploadFileRequest {
    
    private final String CALL_URL = "https://login.schulmanager-online.de/api/upload-file?scope=messenger&";
    
    private String fileName, mimeType, response;
    private InputStream fileData;
    
    public UploadFileRequest(String fileName, String mimeType, InputStream fileData) {
    	
    	this.fileName = fileName;
    	this.mimeType = mimeType;
    	this.fileData = fileData;
    	
    }
    
    public void execute() {
    	
        try {
            mimeType = mimeType.replace("/", "%2F");
            String url = CALL_URL + "name=" + fileName + "&type=" + mimeType;

            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/octet-stream");
            con.setRequestProperty("Authorization", "Bearer " + DataManager.getSmSession().getAuthToken());
            con.setDoOutput(true);

            // Write to server
            try (OutputStream os = con.getOutputStream();
                 BufferedInputStream bis = new BufferedInputStream(fileData);
                 BufferedOutputStream bos = new BufferedOutputStream(os)) {
                
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = bis.read(buffer)) != -1) {
                    bos.write(buffer, 0, bytesRead);
                }
                bos.flush();
            }

            // Get response from server
            int responseCode = con.getResponseCode();
            System.out.println("Response Code: " + responseCode);
            
            try (InputStream is = con.getInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                this.response = response.toString();
                System.out.println("Response after uploading file: " + response.toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public String getFileName() {
    	return fileName;
    }
    
    public String getResponse() {
    	return response;
    }
    
}
