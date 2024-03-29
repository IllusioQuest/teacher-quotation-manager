package com.abi.quotes.views.chat_detail;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import com.abi.quotes.views.MainLayout;
import com.abi.quotes.views.beta_test.SmView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.shared.Tooltip.TooltipPosition;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.UploadI18N;
import com.vaadin.flow.component.upload.receivers.MultiFileBuffer;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoIcon;

import schulmanager.api.ApiCall;
import schulmanager.api.MessagesRequest;
import schulmanager.api.SendMessageRequest;
import schulmanager.api.SetReadRequest;
import schulmanager.components.AttachmentDisplay;
import schulmanager.components.ChatOverviewBox;
import service.DataManager;

@PageTitle("Chat")
@Route(value = "chat", layout = MainLayout.class)
@CssImport(value = "themes/zitate-sammlung/schulmanager.css")
@CssImport(value = "themes/zitate-sammlung/file-viewer.css")
public class ChatDetailView extends SmView implements HasUrlParameter<String> {

	private int threadId = -1;
	private boolean canAnswer = false;
	private String subject = "Fehler 402";
	private boolean isPrivateChat = false;
	private String recipientString = "Fehler 403";
	private int unreadCount = 0;
	
	private Upload upload;
	
	private VerticalLayout innerLayout;
	
	//@Override
	public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {

	    Location location = event.getLocation();
	    QueryParameters queryParameters = location
	            .getQueryParameters();

	    Map<String, List<String>> parametersMap =
	            queryParameters.getParameters();
	    
	    if (parametersMap.containsKey("id"))
	    	threadId = Integer.parseInt(parametersMap.get("id").get(0));
	    if (parametersMap.containsKey("canAnswer"))
	    	canAnswer = Boolean.parseBoolean(parametersMap.get("canAnswer").get(0));
	    if (parametersMap.containsKey("subject"))
	    	subject = parametersMap.get("subject").get(0);
	    if (parametersMap.containsKey("isPrivateChat"))
	    	isPrivateChat = Boolean.parseBoolean(parametersMap.get("isPrivateChat").get(0));
	    if (parametersMap.containsKey("recipientString"))
	    	recipientString = parametersMap.get("recipientString").get(0);
	    if (parametersMap.containsKey("unreadCount"))
	    	unreadCount = Integer.parseInt(parametersMap.get("unreadCount").get(0));
	    
	    if (threadId == -1)
	    	add(new H1("Hier ist etwas schiefgelaufen! (Fehler 401)"));
	    else init();
	}
	
	public void init() {
		
		createHeader();
		createMessagesList();
		createMessageBar();
		
	}
	
	private void createHeader() {
		HorizontalLayout header = new HorizontalLayout();
		header.setWidthFull();
		header.setJustifyContentMode(JustifyContentMode.CENTER);
		header.setDefaultVerticalComponentAlignment(Alignment.CENTER);
		
		Button backButton = new Button(VaadinIcon.ARROW_BACKWARD.create());
		backButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY);
		backButton.addClickListener(e -> {
			getUI().ifPresent(ui -> ui.navigate("chats"));
		});
		header.add(backButton);
		
		Avatar avatar = new Avatar();
		avatar.addThemeVariants(AvatarVariant.LUMO_SMALL);
		avatar.setColorIndex(threadId % 6);
		if (!isPrivateChat) {
			avatar.setName(ChatOverviewBox.shortenSubject(subject));
		}
		header.add(avatar);
		
		VerticalLayout headerTextLayout = new VerticalLayout();
		headerTextLayout.setSpacing(false);
		headerTextLayout.setPadding(false);
		headerTextLayout.setWidth(null);
		headerTextLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
		Span subjectSpan = new Span(subject);
		subjectSpan.getStyle().set("font-weight", "bold");
		headerTextLayout.add(subjectSpan);
		
		Span recipientSpan = new Span(
				recipientString.length() > 80 
				? recipientString.substring(0, 80) + "... Klick!" 
				: recipientString);
		Tooltip tooltip = Tooltip.forComponent(recipientSpan)
				.withText(recipientString)
				.withPosition(TooltipPosition.BOTTOM_START)
				.withManual(true);
		recipientSpan.addClickListener(e -> {
			tooltip.setOpened(!tooltip.isOpened());
		});
		
		headerTextLayout.add(recipientSpan);
		header.add(headerTextLayout);
		
		header.addClassName("header-top");
		add(header);
	}
	
	private void createMessagesList() {
		ApiCall call = new ApiCall(DataManager.getSmSession());
		MessagesRequest request = new MessagesRequest(threadId);
		call.addRequest(request);
		call.execute();
		
		innerLayout = new VerticalLayout();
		innerLayout.setPadding(false);
		innerLayout.setSpacing(false);
		innerLayout.setWidth("100%");
		innerLayout.setMaxWidth("800px");
		innerLayout.getStyle().set("padding-top", "18px");
		innerLayout.getStyle().set("padding-bottom", "100px");
		
		LocalDate currDate = null;
		LocalDate date = null;
		
		JsonObject[] messages = request.getMessages();
		for (int i = 0; i < messages.length; i++) {
			JsonObject message = messages[i];

			if (unreadCount > 0) {
				ApiCall setReadCall = new ApiCall(session);
				setReadCall.addRequest(new SetReadRequest(message, DataManager.getSubscription()));
				setReadCall.execute();
				unreadCount--;
				DataManager.setSubscriptions(null);
			}
			
			innerLayout.addComponentAsFirst(createMessageBox(message));
			String time = message.get("createdAt").getAsString();
			date = LocalDateTime.parse(time.substring(0, time.length()-1)).plusHours(1).toLocalDate();
			if (currDate == null) {
				currDate = date;
			}
			if (!currDate.equals(date)) {
				Component dateDisplay = createDateDisplay(currDate);
				innerLayout.addComponentAtIndex(1, dateDisplay);
				innerLayout.setHorizontalComponentAlignment(Alignment.CENTER, dateDisplay);
				currDate = date;
			}
		}
		Component dateDisplay = createDateDisplay(date);
		innerLayout.addComponentAsFirst(dateDisplay);
		innerLayout.setHorizontalComponentAlignment(Alignment.CENTER, dateDisplay);
		
		add(innerLayout);
		
	}
	
	private Component createMessageBox(JsonObject message) {
		JsonObject sender = message.getAsJsonObject("sender");
		JsonArray attachments = message.getAsJsonArray("attachments");
		
		Div box = new Div();
		if (sender.get("id").getAsInt() == DataManager.getSmSession().getId())
			box.addClassName("self-bubble");
		else 
			box.addClassName("bubble");
		
		VerticalLayout bubbleLayout = new VerticalLayout();
		bubbleLayout.setSpacing(false);
		bubbleLayout.getStyle().setPadding("5px 20px 5px 20px");
		
		Span senderSpan = new Span(sender.get("firstname").getAsString() + " " + sender.get("lastname").getAsString());
		senderSpan.addClassName("sender");
		
		Html messageText = new Html("<span>" + message.get("text").getAsString().replaceAll("\\n", "<br/>") + "</span>");
		
		String time = message.get("createdAt").getAsString();
		LocalDateTime localDateTime = LocalDateTime.parse(time.substring(0, time.length()-1)).plusHours(1);
		Span timeSpan = new Span(localDateTime.format(DateTimeFormatter.ofPattern("kk:mm")));
		timeSpan.getStyle().set("font-size", "10px");

		bubbleLayout.add(senderSpan, messageText);
		
		for (int i = 0; i < attachments.size(); i++) {
			bubbleLayout.add(new AttachmentDisplay(attachments.get(i).getAsJsonObject()));
		}
		
		bubbleLayout.add(timeSpan);
		bubbleLayout.setHorizontalComponentAlignment(Alignment.END, timeSpan);
	
		box.add(bubbleLayout);
		return box;
	}
	
	private Component createDateDisplay(LocalDate date) {
		Span dateSpan = new Span(date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
		dateSpan.getStyle().set("background-color", "var(--lumo-contrast-50pct)");
		dateSpan.getStyle().set("border-radius", "20px");
		dateSpan.getStyle().set("padding-left", "5px").set("padding-right", "5px").set("margin-bottom", "10px");
		return dateSpan;
	}
	
	private void createMessageBar() {
		if (canAnswer) {
			VerticalLayout outerContainer = new VerticalLayout();
			outerContainer.setMargin(false);
			outerContainer.setPadding(false);
			outerContainer.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
			
			HorizontalLayout bar = new HorizontalLayout();
			/*bar.*/outerContainer.getStyle().set("position", "fixed").set("bottom", "70px");
			bar.setMaxHeight("30%");
			bar.setWidthFull();
			bar.setJustifyContentMode(JustifyContentMode.CENTER);
			
			Button attachmentButton = new Button(VaadinIcon.PAPERCLIP.create());
			attachmentButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ICON);
			attachmentButton.addClickListener(e -> {
				toggleUpload(outerContainer);
			});
			attachmentButton.setEnabled(false); //TODO
			
			TextArea area = new TextArea();
			area.setWidthFull();
			area.setMaxWidth("800px");

			Button sendButton = new Button(VaadinIcon.PAPERPLANE.create());
			sendButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ICON);
			
			sendButton.setEnabled(true);
			sendButton.addClickListener(e -> {
				if (area.getValue() != null && !area.getValue().equals("")) {
					JsonObject sentMessage = sendMessage(area.getValue());
					area.clear();
					innerLayout.add(createMessageBox(sentMessage));
				}
			});
			
			bar.add(attachmentButton, area, sendButton);
			
			outerContainer.add(bar);
			add(outerContainer);
		}
	}
	
	private JsonObject sendMessage(String text) {
		ApiCall call = new ApiCall(session);
		SendMessageRequest r = new SendMessageRequest(text, DataManager.getSubscription().getAsJsonObject("thread"));
		call.addRequest(r);
		call.execute();
		return r.getMessage();
	}
	
	private void toggleUpload(VerticalLayout container) {
		
		if (upload == null) {
		
			/*Dialog uploadDialog = new Dialog();
			uploadDialog.setHeaderTitle("Anhänge hochladen");
			Button closeButton = new Button(LumoIcon.CROSS.create(),
			        (e) -> uploadDialog.close());
			closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
			uploadDialog.getHeader().add(closeButton);*/
	
			MultiFileBuffer buffer = new MultiFileBuffer();
			Upload uploadComponent = new Upload(buffer);
			uploadComponent.setI18n(getUploadI18n());
	
			/*Button uploadButton = new Button("Anhängen", e -> {
				handleUploads(buffer);
				uploadDialog.close();
			});
			uploadButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);*/
			
			/*VerticalLayout layout = new VerticalLayout(uploadComponent, uploadButton);
			layout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);*/
			
			//uploadDialog.add(layout);
			
			container.addComponentAsFirst(uploadComponent);
			
			upload = uploadComponent;
			
			//uploadDialog.open();
			
		} else {
			container.remove(upload);
			upload = null;
		}
		
	}
	
	private void handleUploads(MultiFileBuffer buffer) {
		//buffer.
	}

	protected void initialise() {}
	
	public static UploadI18N getUploadI18n() {
		UploadI18N i = new UploadI18N();
		i.setDropFiles(new UploadI18N.DropFiles().setMany("Drag & Drop"));
        i.setAddFiles(new UploadI18N.AddFiles().setMany("Dateien auswählen..."));
        i.setError(new UploadI18N.Error().setTooManyFiles("Zu viele Dateien")
               .setFileIsTooBig("Datei zu groß")
               .setIncorrectFileType("Falscher Dateityp"));
        i.setUploading(new UploadI18N.Uploading().setStatus(new UploadI18N.Uploading.Status()
                .setConnecting("Verbinde...").setStalled("In der Warteschlange...")
                .setProcessing("Lädt..."))
                .setRemainingTime(new UploadI18N.Uploading.RemainingTime()
                        .setPrefix("Verbleibend: ")
                        .setUnknown("Zeit kann nicht abgeschätzt werden"))
                .setError(new UploadI18N.Uploading.Error()
                        .setServerUnavailable("Fehlgeschlagen")
                        .setUnexpectedServerError("Unerwarteter Fehler")
                        .setForbidden("Nicht autorisiert")));
        i.setUnits(new UploadI18N.Units().setSize(Arrays.asList("B", "kB", "MB", "GB", "TB",
                "PB", "EB", "ZB", "YB")));
        return i;
	}
	
}
