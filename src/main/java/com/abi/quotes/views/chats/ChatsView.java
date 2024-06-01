package com.abi.quotes.view.chats;

import com.abi.quotes.views.MainLayout;
import com.abi.quotes.views.beta_test.SmView;
import com.google.gson.JsonObject;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import schulmanager.api.ApiCall;
import schulmanager.api.ReloadMessagesRequest;
import schulmanager.api.Request;
import schulmanager.api.SubscriptionsRequest;
import schulmanager.components.ChatOverviewBox;
import schulmanager.components.NewChatButton;
import service.DataManager;

@PageTitle("Chats")
@Route(value = "chats", layout = MainLayout.class)
/**
 * Page that shows the most recent 30 chats you're in
 */
public class ChatsView extends SmView {

	public ChatsView() {
		super();
	}

	@Override
	protected void initialise() {
		this.setHorizontalComponentAlignment(Alignment.START);
		
		/*TextField searchField = new TextField();
   	 	searchField.setValueChangeMode(ValueChangeMode.EAGER);
   	 	searchField.setWidth("30%");
   	 	searchField.setMaxWidth("200px");
        searchField.setPlaceholder("Suche");
        searchField.setClearButtonVisible(true);
        add(searchField);*/
		
		add(new NewChatButton());
		
		JsonObject[] subs;
		ApiCall call = new ApiCall(DataManager.getSmSession());
		
		//Checks whether subscriptions have already been loaded during this session
		if (DataManager.getSubscriptions() == null || DataManager.getSubscriptions().length == 0) {
			//Gains subscriptions from API
			SubscriptionsRequest subscriptionsRequest = new SubscriptionsRequest();
			call.addRequest(subscriptionsRequest);
			call.execute();
			subs = DataManager.setSubscriptions(subscriptionsRequest.getSubscriptions());
		}
		else {
			//If subscriptions have already been loaded, a reload-messages request is sent
			subs = DataManager.getSubscriptions();
			String lastMessageTimestamp = subs[0].getAsJsonObject("thread").get("lastMessageTimestamp").getAsString();
			ReloadMessagesRequest reloadRequest = new ReloadMessagesRequest(lastMessageTimestamp);
			call.addRequest(reloadRequest);
			call.execute();
			
			//Checks whether something's changed
			if (!reloadRequest.getResultString().equals("{\"status\":200,\"data\":[]}")) {
				SubscriptionsRequest subscriptionsRequest = new SubscriptionsRequest();
				call.removeAllRequests();
				call.addRequest(subscriptionsRequest);
				call.execute();
				subs = DataManager.setSubscriptions(subscriptionsRequest.getSubscriptions());
			}
		}
		
		VerticalLayout innerLayout = new VerticalLayout();
		innerLayout.setPadding(false);
		innerLayout.setSpacing(false);
		innerLayout.setWidth("100%");
		innerLayout.setMaxWidth("800px");
		innerLayout.setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
		
		for (int i = 0; i < subs.length; i++) {
			ChatOverviewBox box = new ChatOverviewBox(subs[i]);
			if (box.getUnreadCount() > 0)
				innerLayout.addComponentAsFirst(box);
			else
				innerLayout.add(box);
			if (i == 0) 
				box.getStyle().set("border-top-left-radius", "15px").set("border-top-right-radius", "15px");
			if ( i == subs.length-1)
				box.getStyle().set("border-bottom-left-radius", "15px").set("border-bottom-right-radius", "15px");
		}
		
		add(innerLayout);
	}
	
}
