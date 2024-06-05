package schulmanager.components;

import java.util.Base64;

import com.google.gson.JsonObject;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class AttachmentDisplay extends VerticalLayout {

	private String requestUrl, fileType, fileName;
	private int id;

	private int screenWidth;
	
	public AttachmentDisplay (JsonObject attachment, int screenWidth) {
		setPadding(false);
		setSpacing(false);
		setMargin (false);
		getStyle().set("padding-bottom", "4px").set("padding-top", "4px");

		this.screenWidth = screenWidth;
		
		id = attachment.get("id").getAsInt();
		
		String fileString = attachment.get("file").getAsString();
		
		requestUrl = "https://login.schulmanager-online.de/download-file/" 
								+ Base64.getEncoder().encodeToString(fileString.getBytes());
		
		String[] parts = fileString.split(",");
        fileType = parts[4].replaceAll("\"", "");
        fileName = parts[6].replaceAll("\"\\]", "");
		
        switch (fileType) {
        
        case "image/apng": 
        case "image/avif":
        case "image/gif":
        case "image/jpeg":
        case "image/png":
        case "image/svg":
        case "image/webp":
        	image();
        	break;
        case "application/pdf":
        	pdf();
        	break;
        default:
        	other();
        	break;
        
        }
        	
	}
	
	private void image() {
		Image img = new Image(requestUrl, fileName);
		img.addClassName("chat-image");
		img.setId("myImg" + id);
		add(img);
		System.out.println("----F; " + fileName);
		add(new Html(
				String.format(
				"""
				<div id="%s" class="modal">
				  <span class="close" id="%s">&times;</span>
				  <img class="modal-content" id="%s">
				  <div id="%s"></div>
				</div>
				""",
				"modal" + id,
				"close" + id,
				"a" + id,
				"caption" + id
				)));
		add(new Html(
				String.format(
				"""
				<script>
				// Get the modal
				var modal = document.getElementById("%s");
				
				// Get the image and insert it inside the modal - use its "alt" text as a caption
				var img = document.getElementById("%s");
				var modalImg = document.getElementById("%s");
				var captionText = document.getElementById("%s");
				img.onclick = function(){
				  modal.style.display = "block";
				  modalImg.src = this.src;
				  captionText.innerHTML = this.alt;
				}
				
				// Get the <span> element that closes the modal
				var span = document.getElementById("%s");
				
				// When the user clicks on <span> (x), close the modal
				span.onclick = function() {
				  modal.style.display = "none";
				}
				</script>
				""",
				"modal" + id,
				"myImg" + id,
				"a" + id,
				"caption" + id,
				"close" + id
				)));
	}
	
	private void pdf() {
		other();
	}
	
	private void other() {
		
		HorizontalLayout layout = new HorizontalLayout();
		layout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
		
		String type = fileType.split("/")[1];
		if (type.equals("vnd.openxmlformats-officedocument. wordprocessingml.document") || type.equals("msword"))
			type = "doc";
		
		String imageUrl = "images/" + type + ".svg";


		Anchor a = new Anchor(requestUrl);
		a.setWidthFull();
		
		Image img = new Image(imageUrl, "kein Icon");
		img.setHeight("45px");
		Span fileNameSpan = new Span(fileName.substring(1));
		fileNameSpan.getStyle()
		    .set("width", "70%")
		    .set("overflow", "hidden")
		    .set("-webkit-line-clamp", "2")
		    .set("word-break", "break-all")
		    .set("line-clamp", "2")
		    .set("display", "-webkit-box")
		    .set("-webkit-box-orient", "vertical");
		layout.add(img, fileNameSpan);

		a.add(layout);
		
		add(a);
	}

	/**
	 * Originally called "shortenFileName". This method was originally supposed to just shorten the given file name
	 * and to create a second line with \n if nessesary to show atleast a little more of the file name before shortening
	 * it with "...", but due to some weird bugs when putting \n into a span, a workaround of creating a VerticalLayout
	 * with one or two spans is a seemingly nessesary workaround...
	 * @param name The file name
	 * @return The VerticalLayout containing either 1 or 2 Spans
	 */
	private VerticalLayout createFileNameSpan(String name) {
		// figured out emperialistically by using 150, 250, 350, 450 and 550 px as screen width
		int newCharCount = (screenWidth - 100) / 10;
		VerticalLayout layout = new VerticalLayout();
		layout.setPadding(false);
		layout.setSpacing(false);
		layout.setMargin(false);

		// debugging using console output
		// System.out.println("Screen width: " + screenWidth);
		// System.out.println("newCharCount: " + newCharCount);
		// System.out.println("name Length: " + name.length());
		// System.out.println("name: " + name);

		if (newCharCount < 5 || name.length() < 5) {
			layout.add(new Span(name));
			return layout;
		}

		// if the filename is longer than the first line and therefore a second line is needed
		if (name.length() > newCharCount) {
			layout.add(new Span(name.substring(0, newCharCount - 1)));

			// if the filename is even longer than 2 lines
			if (name.length() > newCharCount * 2) {
				layout.add(new Span(name.substring(newCharCount - 1, newCharCount * 2 - 1) + " ..."));
			}

			// if the filename is not longer than the second line
			else {
				layout.add(new Span(name.substring(newCharCount - 1, name.length())));
			}
		}
		// filename longer than 4 characters but also not long enough for a second line
		else {
			layout.add(new Span(name));
		}

		return layout;
	}

	
}
