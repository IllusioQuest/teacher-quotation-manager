package com.abi.quotes.views.start;

import com.abi.processing.SortingType;
import com.abi.quotes.views.MainLayout;
import com.abi.quotes.views.profil.ProfilView;
import com.abi.quotes.views.student_quote.StudentQuoteView;
import com.abi.quotes.views.teacher.TeacherView;
import com.abi.quotes.views.users.UsersView;
import com.abi.quotes.views.zitate.ZitateView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;

import components.ErrorMessage;
import components.HasHelp;
import components.NewQuoteButton;
import components.NotLoggedInScreen;
import components.QuoteList;
import database.User;
import service.DataManager;

@PageTitle("Start")
@Route(value = "start", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class StartView extends VerticalLayout implements HasHelp {

	private H1 title;
	
    public StartView() {
        setSpacing(false);
        setSizeFull();
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
        
        if (DataManager.getLoggedIn() == null || !DataManager.getLoggedIn()) {
        	setJustifyContentMode(JustifyContentMode.CENTER);
        	add(new NotLoggedInScreen());
        } else {
        	
        	add(new Html("<div class=\"area\" >\n"
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
        			+ "    </div >"));
        	
        	title = new H1("Willkommen, " + DataManager.getFirstName());
        	title.getStyle().set("margin-top", "10vh");
            title.getStyle().set("color", "var(--lumo-primary-text-color)");
            title.getStyle().set("font-size", "2em");
            
            VerticalLayout buttonsLayout = createNavigationButtons();

            add(title, buttonsLayout);
            
            H2 newQuotesHeadline = new H2("Neueste Zitate");
            newQuotesHeadline.getStyle().set("padding-left", "18px");
            newQuotesHeadline.getStyle().set("padding-top",  "90px");
            setHorizontalComponentAlignment(Alignment.START, newQuotesHeadline);
            add(newQuotesHeadline);
            
            add(createQuoteList());
        	
            //If the user logged in for the first time, an introductory dialog is displayed
            if(DataManager.getFirstLogin()) {
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
        
        Button zitateNachLehrerButton = createNavigationButton("Zitate nach Lehrer", TeacherView.class);
        Button schuelerZitateButton = createNavigationButton("Schülerzitate", StudentQuoteView.class);
        
        NewQuoteButton newQuoteButton = new NewQuoteButton();
        newQuoteButton.setText("Neues Zitat einreichen");
        newQuoteButton.setIcon(null);
        newQuoteButton.getStyle().set("background-color", "var(--lumo-success-color)");
        newQuoteButton.getStyle().set("color", "white");
        newQuoteButton.getStyle().set("font-weight", "bold");
        
        Button nutzerverwaltungButton = createNavigationButton("Nutzerverwaltung", UsersView.class);
        nutzerverwaltungButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);

        VerticalLayout buttonsLayout = new VerticalLayout(alleZitateButton, new HorizontalLayout(zitateNachLehrerButton, schuelerZitateButton), newQuoteButton);
        buttonsLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        
        if (DataManager.isAdmin())
        	buttonsLayout.add(nutzerverwaltungButton);
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
    	QuoteList list = new QuoteList(3, true, -1, true);
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

	@Override
	public Component[] getPages() {
		VerticalLayout comp1 = new VerticalLayout();
		comp1.setPadding(false);
		comp1.add(new Paragraph("Herzlich willkommen, " + DataManager.getFirstName()));
		comp1.add(new Html("<p>Schön, dass du hierher gefunden hast. "
				+ "(<i>Scrolle herunter, um alles zu lesen.</i>) Diese Website dient dazu, all die <b>lustigen und denkwürdigen Sätze, Weisheiten und Sprüche</b>, "
				+ "denen wir im Laufe unserer Oberstufe begegnen, festzuhalten.</p>"));
		comp1.add(new Html("<p>So soll eine digitale Sammlung der besten Lehrer- und Schülerzitate entstehen. Alles hier im Web. Anstelle "
				+ "kleiner privater Sammlungen und Notizzettel gibt es eine <b>zentrale Sammelstelle</b>, an der jeder teilhaben "
				+ "und in vielerlei Erinnerungen an die kleinen Momente schwelgen kann."));
		comp1.add(new Html("<p>Wenn du erfahren möchtest, wie du mitmachen kannst und wie die Website bedient werden kann, "
				+ "klicke dich durch die nächsten Seiten. Vielen Dank für dein Interesse!"));
		
		VerticalLayout comp2 = new VerticalLayout();
		comp2.setPadding(false);
		Image img0 = new Image("images/help0.png", "Start-Bildschirm");
		img0.setWidth("60%");
		comp2.add(img0);
		comp2.add(new Html("<p>Auf dem Startbildschirm hast du neben einer Schnellansicht für die neuesten 3 Zitate direkt die Möglichkeit, "
				+ "verschiedene Knöpfe zu betätigen.</p>"));
		comp2.add(new Html("<p><font color=\"#117FFF\">Alle Zitate</font>: Zeigt dir eine Auflistung sämtlicher bisher eingetragener "
				+ "Zitate, die du sortieren <span style=\"font-family: icon-font;\">filter</span> und durchsuchen "
				+ "<span style=\"font-family: icon-font;\">search</span> kannst. Ein Klick auf das Zitat bringt dich auf eine neue Seite, "
				+ "auf der auch das Kommentieren und Bearbeiten möglich ist."));
		comp2.add(new Html("<p><font color=\"#117FFF\">Zitate nach Lehrer</font>: Eine Übersicht aller Lehrer der Schule. Ein Klick auf den "
				+ "jeweiligen Lehrer zeigt dir dessen Zitate an."));
		comp2.add(new Html("<p><font color=\"#158443\">Neues Zitat einreichen</font>: Das Herzstück dieser Webseite! Trage bitte möglichst viele "
				+ "neue Zitate ein. Hierzu musst du bloß einen Lehrer aus dem Dropdown-Menü auswählen und das Zitat eintippen. <i>Tipp: "
				+ "Überprüfe am besten vorher mit der Suchfunktion bei</i> Alle Zitate<i>, ob das Zitat bereits vorhanden ist.</i>"));
		
		VerticalLayout comp3 = new VerticalLayout();
		HorizontalLayout comp3Inner = new HorizontalLayout();
		comp3Inner.setHeight("100%");
		comp3Inner.setPadding(false);
		Image img1 = new Image("images/help1.png", "Drei-Punkte-Menü");
		img1.setWidth("40%");
		img1.setHeight("70%");
		comp3Inner.add(img1);
		comp3.add(comp3Inner);
		comp3Inner.add(new Html("<p>Klickst du von irgendeiner Seite auf die drei Punkte in der oberen rechten Bildschirmecke, öffnet sich ein "
				+ "kleines Menü.</p>"));
		comp3.add(new Html("<p>Hier kannst du dich abmelden (Du wirst beim nächsten Besuch mit dem selben Gerät automatisch wieder eingeloggt.), "
				+ "zwischen Dark Mode und Light Mode wechseln, diese Hilfe jederzeit wieder öffnen oder auf dein Profil zugreifen. Am besten "
				+ "änderst du dein Passwort und gibst eine E-Mail-Adresse an. Diese wird benötigt, solltest du einmal deine Anmeldedaten vergessen.</p>"
				));
		comp3.add(new Html("<p><font color=\"red\"><u>ACHTUNG</u>: Verwende <b>auf keinen Fall</b> ein Passwort, das du auch auf anderen Websites benutzt! "
				+ "Deine Daten sind nicht geschützt.</font></p>"));
		
		VerticalLayout comp4 = new VerticalLayout();
		comp4.setPadding(false);
		comp4.add(new H3("Fragen, Feedback, Fehler?"));
		comp4.add(new Html("<p>Nur her damit! Gib gerne all deine Verbesserungsvorschläge an mich, Jona, weiter. Entweder persönlich oder per "
				+ "Mail an <a href=\"mailto:illusioquest@gmail.com\">illusioquest@gmail.com</a>.</p>"));
		comp4.add(new Html("<p>Sollte dir einmal ein Bug auffallen, melde ihn bitte sofort! <span style=\"font-family: icon-font;\">bug bug bug wer rechnet denn damit dass hier ein geheimer text steht</span>"));
		
		VerticalLayout comp5 = new VerticalLayout();
		comp5.setPadding(false);
		comp5.setSpacing(false);
		Html html0 = new Html("<p>Entwickler: <b>Jona Richartz</b>  <small>(Ja, das hab ich gemacht)</small></p>");
		html0.getElement().setProperty("margin", "0px");
		Html html1 = new Html("<p>Unter Verwendung von <a href=\"https://vaadin.com/\"><b>Vaadin 24</b></a></p>");
		html1.getElement().setProperty("margin", "0px");
		comp5.add(html0, html1);
		comp5.add(new Html("<p>GitHub-Repo: <a href = \"https://github.com/IllusioQuest/teacher-quotation-manager\">teacher-quotation-manager</a></p>"));
		comp5.add(new Html("<p>Helfende Hände:</p>"));
		comp5.add(new Html("<ul>\r\n"
				+ "  <li><a href=\"https://www.eclipse.org/\">Eclipse</a> (IDE)</li>"
				+ "  <li><a href=\"https://cloud.google.com/run/\">Google Cloud Run</a> (Hosting)</li>"
				+ "  <li><a href=\"https://www.docker.com/\">Docker</a> (Containerisierung)</li>"
				+ "  <li><a href=\"https://aiven.io/\">Aiven</a> (Datenbank)</li>"
				+ "  <li><a href=\"https://maven.apache.org/\">Maven</a> (Projekt- / Dependencymanager)</li>"
				+ "  <li><a href=\"https://spring.io/\">Spring</a> (? Ist halt bei Vaadin dabei...)</li>"
				+ "  <li><a href=\"https://github.com/\">GitHub</a> (Versionsmanagement / Veröffentlichung des Quellcodes)</li>"
				+ "  <li><a href=\"https://ngrok.com/\">ngrok</a> (Zu Testzwecken während der Entwicklung)</li>"
				+ "</ul>"));
		comp5.add(new Html("<script src=\"https://zitate.webmart.de/zdt.js\" async></script>"));
		comp5.add(new Html("<span style=\"font-family: monospace\"><small>Version 1.0</small></span>"));
		comp5.add(new Html("<p>Du hast das alles gelesen? Dafür hast du dir einen Keks verdient. 🍪</p>"));
		
		return new Component[] {comp1, comp2, comp3, comp4, comp5};
	}

}
