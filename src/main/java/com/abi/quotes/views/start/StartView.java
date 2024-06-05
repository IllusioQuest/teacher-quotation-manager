package com.abi.quotes.views.start;

import com.abi.processing.SortingType;
import com.abi.quotes.views.MainLayout;
import com.abi.quotes.views.beta_test.BetaTestView;
import com.abi.quotes.views.favorites.FavoritesView;
import com.abi.quotes.views.profil.ProfilView;
import com.abi.quotes.views.pushtest.PushTestView;
import com.abi.quotes.views.student_quote.StudentQuoteView;
import com.abi.quotes.views.teacher.TeacherView;
import com.abi.quotes.views.uploadtest.FileProcessorView;
import com.abi.quotes.views.users.UsersView;
import com.abi.quotes.views.zitate.ZitateView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;

import components.ErrorMessage;
import components.FavoriteButton;
import components.HasHelp;
import components.NewQuoteButton;
import components.NotLoggedInScreen;
import components.QuoteList;
import database.User;
import service.DataManager;

@PageTitle("Startseite")
@Route(value = "start", layout = MainLayout.class)
@JsModule("./insertatcursor.js")
@JsModule("./favoritebutton.js")
@CssImport(value = "themes/zitate-sammlung/favorite-button.css")
/**
 * Overview page for all quote-related pages, shows 3 most recent quotes. Also used as starting page.
 */
public class StartView extends VerticalLayout implements HasHelp {

	private H1 title;
	private QuoteList list;
	
    public StartView() {
        setSpacing(false);
        setSizeFull();
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
                
        if (DataManager.getLoggedIn() == null || !DataManager.getLoggedIn()) {
        	setJustifyContentMode(JustifyContentMode.CENTER);
        	add(new NotLoggedInScreen());
        } else {
        	
        	Html effect = new Html("<div class=\"area\" >\n"
        			+ "            <ul class=\"circles\" style=\"width:90%; height:90%\">\n"
        			+ "                    <li></li>\n"
        			+ "                    <li></li>\n"
        			+ "                    <li></li>\n"
        			+ "                    <li></li>\n"
        			+ "                    <li></li>\n"
        			+ "                    <li></li>\n"
        			+ "                    <li></li>\n"
        			+ "                    <li></li>\n"
        			+ "                    <li></li>\n"
        			+ "                    <li></li>\n"
        			+ "                    <li></li>\n"
        			+ "                    <li></li>\n"
        			+ "                    <li></li>\n"
        			+ "                    <li></li>\n"
        			+ "                    <li></li>\n"
        			+ "                    <li></li>\n"
        			+ "                    <li></li>\n"
        			+ "            </ul>\n"
        			+ "    </div >");
        	effect.getStyle().set("z-index",  "-1000");
        	add(effect);
        	
        	title = new H1("Willkommen, " + DataManager.getFirstName());
        	title.getStyle().set("margin-top", "10vh");
            title.getStyle().set("color", "var(--lumo-primary-text-color)");
            title.getStyle().set("font-size", "2em");
            title.getStyle().set("user-select",  "none");
            
            VerticalLayout buttonsLayout = createNavigationButtons();

            add(title, buttonsLayout);
            
            H2 newQuotesHeadline = new H2("Neueste Zitate");
            newQuotesHeadline.setId("newQuotesHeadline");
            newQuotesHeadline.getElement().setAttribute("onclick", "document.getElementById('newQuotesHeadline').scrollIntoView()");
            newQuotesHeadline.addClickListener(event -> {UI.getCurrent().getPage().executeJs("document.getElementById('newQuotesHeadline').scrollIntoView()");});
            newQuotesHeadline.getStyle().set("padding-left", "18px");
            newQuotesHeadline.getStyle().set("padding-top",  "90px");
            newQuotesHeadline.getStyle().set("user-select",  "none");
            setHorizontalComponentAlignment(Alignment.START, newQuotesHeadline);
            add(newQuotesHeadline);
            
            add(createQuoteList());
        	
            //If the user logged in for the first time, an introductory dialog is displayed and the cookie banner is shown
            if(DataManager.getFirstLogin()) {
            	Notification cookieNotification = new Notification();
                cookieNotification.setPosition(Position.BOTTOM_STRETCH);
                cookieNotification.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
                HorizontalLayout dialogContent = new HorizontalLayout();
                dialogContent.setWidthFull();
                Image cookieImg = new Image("images/cookie.png", "Cookies");
                cookieImg.setHeight("4em");
                Html cookieText = new Html("<span style=\"font-size: 8pt\">Diese Website verwendet Cookies. Durch die Nutzung der Website stimmst du der Verwendung von Cookies zu.<span>");
                cookieText.getStyle().setWidth("100%");
                Button agreeButton = new Button("Ok");
                agreeButton.addClickListener(event -> {
                    cookieNotification.close();
                });
                agreeButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
                dialogContent.add(cookieImg, cookieText, agreeButton);
                cookieNotification.add(dialogContent);
                cookieNotification.open();   
                
            	openHelp().addDialogCloseActionListener(e -> {DataManager.setFirstLogin(false); e.getSource().close();});
            }   
        }
    }
    
    private VerticalLayout createNavigationButtons() {
        Button alleZitateButton = createNavigationButton("Alle Lehrerzitate", ZitateView.class);
        alleZitateButton.setIcon(VaadinIcon.LIST.create());
        alleZitateButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        alleZitateButton.setWidth("80%");
        alleZitateButton.setMaxWidth("400px");
        
        Button favoritenButton = createNavigationButton("★", FavoritesView.class);
        favoritenButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_PRIMARY);
        
        HorizontalLayout firstRow = new HorizontalLayout(alleZitateButton, favoritenButton);
        firstRow.setWidth("85%");
        firstRow.setMaxWidth("450px");
        firstRow.setSpacing(false);
        firstRow.getThemeList().set("spacing-xs", true);
        
        Button zitateNachLehrerButton = createNavigationButton("Zitate nach Lehrer", TeacherView.class);
        Button schuelerZitateButton = createNavigationButton("Schülerzitate", StudentQuoteView.class);
        
        NewQuoteButton newQuoteButton = new NewQuoteButton();
        newQuoteButton.setText("Neues Zitat einreichen");
        newQuoteButton.setIcon(null);
        newQuoteButton.getStyle().set("background-color", "var(--lumo-success-color)");
        newQuoteButton.getStyle().set("color", "white");
        newQuoteButton.getStyle().set("font-weight", "bold");
        newQuoteButton.setQuoteCreationListener(() -> list.update());
        
      
        Button nutzerverwaltungButton = createNavigationButton("Nutzerverwaltung", UsersView.class);
        nutzerverwaltungButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        
        Button testsButton = createNavigationButton("Neues Zeug testen", FileProcessorView.class);
        testsButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);


        VerticalLayout buttonsLayout = new VerticalLayout(firstRow, new HorizontalLayout(zitateNachLehrerButton, schuelerZitateButton), newQuoteButton);
        buttonsLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);


        if (DataManager.isAdmin()) {
			nutzerverwaltungButton = createNavigationButton("Nutzerverwaltung", UsersView.class);
			nutzerverwaltungButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
			buttonsLayout.add(nutzerverwaltungButton);
		}
        if (DataManager.canTest()) {
			testsButton = createNavigationButton("Neues Zeug testen", PushTestView.class);
			testsButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
			buttonsLayout.add(testsButton);
		}


        buttonsLayout.setSpacing(true);
        buttonsLayout.getStyle().set("margin-top", "20px");

        return buttonsLayout;
    }

    private Button createNavigationButton(String caption, Class<? extends Component> navigationTarget) {
        Button button = new Button(caption);
        button.addClickListener(e -> {
            UI.getCurrent().navigate(navigationTarget);
        });
        return button;
    }
    
    private QuoteList createQuoteList() {
    	list = new QuoteList(3, true, -1, true);
    	list.setSortingType(SortingType.NEWEST);
    	list.setHeightFull();
    	list.setHeight("110%");
    	list.getList().setHeightFull();
    	return list;
    }
    
    private void openUserMenu(Component target) {
    	ContextMenu menu = new ContextMenu();
    	
    	boolean darkMode = DataManager.getDarkMode();
    	setDarkMode(darkMode);
    	Button darkModeButton = new Button((darkMode) ? "Light Mode" : "Dark Mode");
    	darkModeButton.setIcon((darkMode) ? VaadinIcon.SUN_O.create() : VaadinIcon.MOON_O.create());
    	darkModeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_CONTRAST);
    	darkModeButton.addClickListener(e -> {
    		setDarkMode(!DataManager.getDarkMode());
    		darkModeButton.setText((DataManager.getDarkMode()) ? "Light Mode" : "Dark Mode");
    	});
    	menu.addItem(darkModeButton);
    	
    	Button profileButton = new Button("Dein Profil");
    	profileButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
    	profileButton.addClickListener(e -> {
    		getUI().ifPresent(ui -> ui.navigate("profil"));
    	});
    	menu.addItem(profileButton);
    	
    	if (DataManager.isAdmin()) {
    		Button usersButton = new Button("Nutzerverwaltung");
    		usersButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
    		usersButton.addClickListener(e -> {
                UI.getCurrent().navigate(UsersView.class);
            });
    		menu.addItem(usersButton);
    	}
    	
    	menu.setOpenOnClick(true);
    	menu.setTarget(target);
    }
    
    private void setDarkMode(boolean dark) {
    	User.updateNumber(DataManager.getUserID(), "user_darkMode", (dark) ? "1" : "0");
    	DataManager.setDarkMode(dark);
    	getElement().executeJs("document.documentElement.setAttribute('theme', $0)", (dark) ? Lumo.DARK : Lumo.LIGHT);
    }

	/**
	 * Not used anymore, replaced by the static method getHelpPages() in MainLayout.java
	 * @return An array of Components, each including one "page" of the help dialog
	 */
	@Override
	public Component[] getPages() {
		return MainLayout.getHelpPages();
	}

}
