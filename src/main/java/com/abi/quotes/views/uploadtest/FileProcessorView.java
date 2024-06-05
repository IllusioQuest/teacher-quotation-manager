package com.abi.quotes.views.uploadtest;

import com.vaadin.flow.component.button.Button;

import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.abi.quotes.views.MainLayout;
import com.abi.quotes.views.chat_detail.ChatDetailView;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.component.upload.receivers.MultiFileBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoIcon;

import schulmanager.api.UploadFileRequest;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;

/**
 * Component which uses Vaadin's built-in Upload component for uploading files.
 * Allows uploading and removing multiple files. The files are displayed along 
 * with a potential preview image above the upload button. For each uploaded file, 
 * a {@link UploadFileRequest} is pepared, but not sent. Those requests can be
 * accessed via {@link #getUploadFileRequests()} and should be executed in a
 * later process, e.g. before sending a message. The execution can also be done by
 * FileProcessorView, using {@link #uploadAllFilesToSmServer()}.
 * <br/><br/>Warning: Every uploaded file is stored in memory until either it's uploaded
 * to the Schulmanager server or the file list gets cleared.
 */
@SuppressWarnings("serial")
@PageTitle("File-Processor")
@Route(value="file-processor", layout= MainLayout.class)
@CssImport(value = "themes/zitate-sammlung/file-viewer.css")
/**
 * Test page.
 */
public class FileProcessorView extends VerticalLayout {

	private ArrayList<UploadFileRequest> uploadRequests;
	
	private VerticalLayout fileList;
	
	/**
	 * Constructor which creates the Upload component and the file list;
	 * directly usable as functionality is added by the constructor.
	 */
    public FileProcessorView() {
    	setSpacing(false);
    	setWidth("50%");
    	setDefaultHorizontalComponentAlignment(Alignment.CENTER);
    	
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setMaxFiles(5);
        upload.setMaxFileSize(67108864);
        upload.setI18n(ChatDetailView.getUploadI18n());
        
        fileList = new VerticalLayout();
        fileList.setSpacing(false);
        fileList.setWidth("90%");
        fileList.setMaxWidth("372.78px"); //== max width of the upload component
        fileList.getStyle()
        	.setPaddingLeft("8px").setPaddingRight("8px").setPaddingBottom("0");
        fileList.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        
        uploadRequests = new ArrayList<UploadFileRequest>();

        upload.addSucceededListener(event -> {
        	UploadFileRequest request = new UploadFileRequest(event.getFileName(), event.getMIMEType(), buffer.getInputStream());
        	uploadRequests.add(request);
        	
        	upload.clearFileList();
        	
            String mimeType = event.getMIMEType();
            String originalFileName = event.getFileName();
            InputStream inputStream = buffer.getInputStream();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            byte[] bufferArray = new byte[8192]; // Buffer size = 8 KiB
            int bytesRead;
            try {
                while ((bytesRead = inputStream.read(bufferArray)) != -1) {
                    outputStream.write(bufferArray, 0, bytesRead);
                }
                byte[] processedFileBytes = outputStream.toByteArray();

                // Receiving a data URL by encoding the file to Base64  
                String base64Data = Base64.getEncoder().encodeToString(processedFileBytes);
                String dataURL = "data:" + mimeType + ";base64," + base64Data;

                HorizontalLayout l = new HorizontalLayout();
                l.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
                l.setSpacing(false);
                l.setWidthFull();
                
                if (mimeType.startsWith("image")) {
                	Image imgPreview = new Image(dataURL, originalFileName);
                	imgPreview.setWidth("20px");
                	imgPreview.setHeight("20px");
                	imgPreview.getStyle().setPaddingRight("6px");
                	l.add(imgPreview);
                }
                
                // Creating the download link
                Anchor downloadLink = new Anchor();
                downloadLink.setText(originalFileName);
                downloadLink.setHref(dataURL);
                downloadLink.getElement().setAttribute("download", originalFileName);
                downloadLink.getStyle().set("white-space", "nowrap");
                downloadLink.getStyle().set("overflow", "hidden");
                downloadLink.getStyle().set("text-overflow", "ellipsis");
                l.add(downloadLink);
                
                Button removeButton = new Button(LumoIcon.CROSS.create());
                removeButton.getStyle().setPaddingLeft("4px");
                removeButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_CONTRAST);
                removeButton.addClickListener(e -> {
                	uploadRequests.remove(request);
                	fileList.remove(l);
                });
                l.add(removeButton);
                
                fileList.add(l);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        add(fileList, upload);
    }
    
    /**
     * Removes every uploaded file from the file list along with the
     * respective UploadFileRequests.
     */
    public void clearFileList() {
    	uploadRequests.clear();
    	fileList.removeAll();
    }
    /**
     * @return Prepared UploadFileRequests of every file currently 
     * displayed in the file list.
     */
    public UploadFileRequest[] getUploadFileRequests() {
    	return uploadRequests.toArray(new UploadFileRequest[uploadRequests.size()]);
    }
    
    private ArrayList<String> smFileHandles;
    
    /**
     * Uploads every file currently displayed in the file list to the
     * Schulmanager server. This removes every uploaded file from
     * the component along with their respective UploadFileRequests.
     * The server's responses
     */
    public void uploadAllFilesToSmServer() {
    	for (UploadFileRequest r : uploadRequests) {
    		r.execute();
    		smFileHandles.add(r.getResponse());
    	}
    	clearFileList();
    }
    
    /**
     * Returns a String array which contains the file handle of every
     * file which has ever been uploaded to the Schulmanager server by this
     * component. A file handle is the server's response to an upload.
     * If {@link #uploadAllFilesToSmServer()} hasn't been called
     * yet, the method returns <code>null</code>.
     * @return Array of the server's responses to each upload, or <code>null</code> if nothing 
     * has been uploaded to the Schulmanager server.
     */
    public String[] getSmFileHandles() {
    	return smFileHandles.toArray(new String[smFileHandles.size()]);
    }
    
    /**
     * Deletes the file handles of all previously to Schulmanager uploaded files.
     */
    public void clearHistory() {
    	smFileHandles.clear();
    }
    
}