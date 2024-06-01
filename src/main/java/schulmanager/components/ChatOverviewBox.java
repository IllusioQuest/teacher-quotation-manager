package schulmanager.components;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.JsonObject;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.html.Hr;

import service.CalendarCalc;
import service.DataManager;

/**
 * A "box" that represents one chat in the chat overview on the chats page.
 */
public class ChatOverviewBox extends HorizontalLayout implements CalendarCalc {

	private JsonObject subscription;
	
	String subject;
	private int subscriptionId;
	private int unreadCount;
	
	public ChatOverviewBox(JsonObject subscription) {
		this.subscription = subscription;
		
		subscriptionId = subscription.get("id").getAsInt();
		
		unreadCount = subscription.get("unreadCount").getAsInt();
		
		JsonObject thread = subscription.getAsJsonObject("thread");
		
		subject = thread.get("subject").getAsString();
		String senderString = thread.get("senderString").getAsString();
		String recipientString = thread.get("recipientString").getAsString();
		String lastMessageTime = thread.get("lastMessageTimestamp").getAsString();
		LocalDateTime lastMessageTimestamp = LocalDateTime.parse(lastMessageTime.substring(0, lastMessageTime.length()-1)).plusHours(1);
		boolean isPrivateChat = thread.get("isPrivateChat").getAsBoolean();
		boolean allowAnswers = thread.get("allowAnswers").getAsBoolean();
		
		// The subject of a private chat should be the name of the other person
		if (isPrivateChat) {
			String userFirstName = DataManager.getFirstName();
			if (senderString.contains(userFirstName))
				subject = recipientString;
			else 
				subject = senderString;
		}
		
		Avatar iconAvatar = new Avatar();
		iconAvatar.getStyle().set("margin-right", "8px");
		iconAvatar.getStyle().set("margin-left", "5px");
		iconAvatar.addThemeVariants(AvatarVariant.LUMO_LARGE);
		iconAvatar.setColorIndex(subscriptionId % 6);
		if (!isPrivateChat) {
			iconAvatar.setName(shortenSubject(subject));
		}
		
		VerticalLayout textPart = new VerticalLayout();
		textPart.setPadding(false);
		textPart.setSpacing(false);
		textPart.setWidth("70%");
		Span subjectSpan = new Span(subject);
		Span senderSpan = new Span("Von: " + senderString);
		Span recipientSpan = new Span("An: " + recipientString);
		subjectSpan.getStyle().set("text-overflow", "ellipsis").set("white-space", "nowrap").set("overflow-x", "hidden").set("width", "100%").set("font-weight", "bold");
		senderSpan.getStyle().set("text-overflow", "ellipsis").set("white-space", "nowrap").set("overflow-x", "hidden").set("width", "100%").set("color", "var(--lumo-contrast-80pct)").set("font-size", "14px");
		recipientSpan.getStyle().set("text-overflow", "ellipsis").set("white-space", "nowrap").set("overflow-x", "hidden").set("width", "100%").set("color", "var(--lumo-contrast-80pct)").set("font-size", "14px");
		textPart.add(subjectSpan, senderSpan, recipientSpan);

		// VerticalLayout to the right that contains the context menu button and the timestamp of last message
		VerticalLayout optionPart = new VerticalLayout();
		optionPart.setPadding(false);
		optionPart.setSpacing(false);

		// the date at the top right
		Span lastMessageSpan = new Span(beautifyLocalDateTime(lastMessageTimestamp) + "  ");
		lastMessageSpan.getStyle().set("padding-right", "5px");
		lastMessageSpan.getStyle().set("font-size", "9.5px");

		// ContextMenu button
		Button menuButton = new Button(VaadinIcon.ELLIPSIS_DOTS_V.create());
		menuButton.getStyle().set("padding-bottom", "10px").set("padding-top", "10px");
		menuButton.addThemeVariants(/*ButtonVariant.LUMO_SMALL, */ButtonVariant.LUMO_TERTIARY);
		createChatOptionsMenu(menuButton);
		menuButton.setEnabled(false);  // disabled for now

		// both get added to VerticalLayout
		optionPart.add(lastMessageSpan, menuButton);

		// unread messages count if there are any
		if (unreadCount > 0) {
			Span unreadMessagesInfo = new Span(unreadCount + "");
			unreadMessagesInfo.getStyle().set("border-radius", "10px");
			unreadMessagesInfo.getStyle().set("background-color", "var(--lumo-primary-color)");
			unreadMessagesInfo.getStyle().set("width", "45px");
			unreadMessagesInfo.getStyle().set("text-align", "center");
			unreadMessagesInfo.getStyle().set("font-weight", "bold");
			optionPart.add(unreadMessagesInfo);
		}
		optionPart.setDefaultHorizontalComponentAlignment(Alignment.END);
		optionPart.setWidthFull();
		
		setSpacing(false); setPadding(false); setMargin(false);
		setDefaultVerticalComponentAlignment(Alignment.START);
		setVerticalComponentAlignment(Alignment.CENTER, iconAvatar);
		setWidthFull();
		
		addClassName("hover-effect");
		getStyle().set("box-shadow", "var(--lumo-contrast-50pct) 0px 1px 4px");
		getStyle().set("padding-bottom", "4px").set("padding-top", "4px");
		
		add(iconAvatar, textPart, optionPart);
		
		addClickListener(e -> {
			DataManager.setSubscription(subscription);
			getUI().ifPresent(ui -> ui.navigate(
					"chat?id=" + subscriptionId + 
					"&canAnswer=" + allowAnswers +
					"&subject=" + subject +
					"&isPrivateChat=" + isPrivateChat +
					"&recipientString=" + recipientString +
					"&unreadCount=" + unreadCount
					));
		});

	}

	private ContextMenu createChatOptionsMenu(Component button) {

		// the older solution using regular buttons instead of menuItems inside context menu
		/*
		ContextMenu menu = new ContextMenu();
		menu.setDefaultHorizontalComponentAlignment(Alignment.END);
		//Button hideButton = new Button("Verbergen");
		//hideButton.setIcon(VaadinIcon.EYE_SLASH.create());
		//hideButton.addClickListener(e -> {
		//	getUI().ifPresent(ui -> ui.navigate("chats"));
		//});
		menu.addItem(hideButton);
		*/

		// initialise context menu
		ContextMenu menu = new ContextMenu();

		// Hide button
		MenuItem hideButton = menu.addItem("Verbergen", event -> {
			getUI().ifPresent(ui -> ui.navigate("start")); // placeholder action
		});
		hideButton.addComponentAsFirst(createIcon(VaadinIcon.EYE_SLASH));

		// Add to News button
		MenuItem addNewsButton = menu.addItem("zu News hinz.", event -> {
			getUI().ifPresent(ui -> ui.navigate("klo")); // placeholder action
		});
		addNewsButton.addComponentAsFirst(createIcon(VaadinIcon.NEWSPAPER));

		// finish and return context menu
		menu.setOpenOnClick(true);
		menu.setTarget(button);
		return menu;
	}

	/**
	 * Copied from https://vaadin.com/docs/latest/components/context-menu#styling-menu-items
	 * @param vaadinIcon the icon to work with
	 * @return the processed icon
	 */
	private Component createIcon(VaadinIcon vaadinIcon) {
		Icon icon = vaadinIcon.create();
		icon.getStyle().set("color", "var(--lumo-secondary-text-color)")
				.set("margin-inline-end", "var(--lumo-space-s")
				.set("padding", "var(--lumo-space-xs");
		return icon;
	}

	public String beautifyLocalDateTime(LocalDateTime time) {
		if (time.toLocalDate().equals(LocalDate.now())) {
			return time.format(DateTimeFormatter.ofPattern("kk:mm"));
		}
		else if (time.toLocalDate().equals(LocalDate.now().minusDays(1))) {
			return "Gestern";
		}
		else {
			return time.format(DateTimeFormatter.ofPattern("dd.MM.yy"));
		}
	}
	
	public static String shortenSubject(String subject) {
		String[] splitted = subject.split(" ");
		if (splitted.length > 1)
			return splitted[0] + " " + splitted[1];
		else
			return subject;
	}
	
	public int getUnreadCount() {
		return unreadCount;
	}
	
}
