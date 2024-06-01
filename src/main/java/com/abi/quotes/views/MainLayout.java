package com.abi.quotes.views;


import com.abi.quotes.views.beta_test.BetaTestView;
import com.abi.quotes.views.beta_test.BetaTestView.TimetableNav;
import com.abi.quotes.views.login.LoginView;
import com.abi.quotes.views.profil.ProfilView;
import com.abi.quotes.views.start.StartView;
import com.abi.quotes.views.users.UsersView;
import com.abi.quotes.views.zitate.ZitateView;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.BoxSizing;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FlexDirection;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.FontWeight;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.theme.lumo.LumoUtility.Height;
import com.vaadin.flow.theme.lumo.LumoUtility.ListStyleType;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Overflow;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;
import com.vaadin.flow.theme.lumo.LumoUtility.Whitespace;
import com.vaadin.flow.theme.lumo.LumoUtility.Width;

import components.HasHelp;
import components.NotLoggedInScreen;
import database.TableReceiver;
import database.User;
import jakarta.servlet.http.Cookie;
import service.DataManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;

import org.vaadin.lineawesome.LineAwesomeIcon;

/**
 * The main view is a top-level placeholder for other views.
 */
@CssImport(value = "themes/zitate-sammlung/footer.css")
public class MainLayout extends AppLayout {
	
	private HorizontalLayout userDisplay;
	private Button navButton;
	public TimetableNav timetableNav;
	
	private VerticalLayout moreButton;
	private boolean moreButtonHasUserMenu = false;
	private Registration moreReg;

	private Div layout;


    /**
     * A simple navigation item component, based on ListItem element.
     */
    public static class MenuItemInfo extends ListItem {

        private final Class<? extends Component> view;

        public MenuItemInfo(String menuTitle, Component icon, Class<? extends Component> view) {
            this.view = view;
            RouterLink link = new RouterLink();
            // Use Lumo classnames for various styling
            link.addClassNames(Display.FLEX, Gap.XSMALL, Height.MEDIUM, AlignItems.CENTER, Padding.Horizontal.SMALL,
                    TextColor.BODY);
            link.setRoute(view);

            Span text = new Span(menuTitle);
            // Use Lumo classnames for various styling
            text.addClassNames(FontWeight.MEDIUM, FontSize.MEDIUM, Whitespace.NOWRAP);

            if (icon != null) {
                link.add(icon);
            }
            link.add(text);
            add(link);
        }

        public Class<?> getView() {
            return view;
        }

    }

    public MainLayout() {
    	
    	getElement().executeJs("document.documentElement.setAttribute('lang', 'de-de')");
    	
    	DataManager.setMainLayout(this);
        addToNavbar(createHeaderContent());
        cookieLogin();
        //updateUserSpan();
		getElement().executeJs("document.documentElement.setAttribute('theme', $0)", (DataManager.getDarkMode()) ? Lumo.DARK : Lumo.LIGHT);
    }

    private Header header;
    
    private Component createHeaderContent() {
        header = new Header();
        header.addClassNames(BoxSizing.BORDER, Display.FLEX, FlexDirection.COLUMN, Width.FULL);

        layout = new Div();
        layout.addClassNames(Display.FLEX, AlignItems.CENTER, Padding.Horizontal.LARGE);

        /*HorizontalLayout homeLayout = new HorizontalLayout();
        H1 appName = new H1("Zitate-Sammlung");
        appName.addClassNames(Margin.Vertical.MEDIUM, Margin.End.AUTO, FontSize.LARGE);
        appName.addClickListener(e -> {
        	getUI().ifPresent(ui -> ui.navigate("start"));
        });
        Button homeButton = new Button(VaadinIcon.HOME.create());
        homeButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_ICON);
        homeButton.addClickListener(e -> {
        	getUI().ifPresent(ui -> ui.navigate("start"));
        });
        homeLayout.add(appName, homeButton);
        layout.add(homeLayout);*/

        /*Nav nav = new Nav();
        nav.addClassNames(Display.FLEX, Overflow.AUTO, Padding.Horizontal.MEDIUM, Padding.Vertical.XSMALL);

        // Wrap the links in a list; improves accessibility
        UnorderedList list = new UnorderedList();
        list.addClassNames(Display.FLEX, Gap.SMALL, ListStyleType.NONE, Margin.NONE, Padding.NONE);
        nav.add(list);

        for (MenuItemInfo menuItem : createMenuItems()) {
            list.add(menuItem);

        }*/

        //header.add(layout);
        //header.add(nav);
        
        layout.add(createNavButton("Stunden", VaadinIcon.CLOCK.create(), "test"));
        layout.add(createNavButton("Chats", VaadinIcon.CHAT.create(), "chats"));
        layout.add(createNavButton("Zitate", VaadinIcon.QUOTE_RIGHT.create(), "start"));
        moreButton = createNavButton("Mehr", VaadinIcon.ELLIPSIS_DOTS_H.create(), null);
        updateMoreButton();

        layout.add(moreButton);
        
        header.add(layout);
        return header;
    }

    private VerticalLayout createNavButton(String name, Icon icon, String target) {
    	
    	VerticalLayout layout = new VerticalLayout();
    	layout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
    	layout.setSpacing(false);
    	layout.setPadding(false);
    	layout.setMargin (false);
    	
    	layout.add(icon);
    	layout.add(new Span(name));
    	if (target != null)
	        layout.addClickListener(e -> {
	        	getUI().ifPresent(ui -> ui.navigate(target));
	        });
        
        return layout;
    }

    private MenuItemInfo[] createMenuItems() {
        return new MenuItemInfo[]{ //
                new MenuItemInfo("Start", LineAwesomeIcon.HOME_SOLID.create(), StartView.class), //

                new MenuItemInfo("Zitate", LineAwesomeIcon.BOOK_SOLID.create(), ZitateView.class), //

                new MenuItemInfo("Login", LineAwesomeIcon.LOCK_SOLID.create(), LoginView.class), //

                new MenuItemInfo("Profil", LineAwesomeIcon.USER.create(), ProfilView.class), //

        };
    }

	public void updateMoreButton() {
		if (DataManager.getLoggedIn() == null || !DataManager.getLoggedIn()) {
			//layout.remove(moreButton);
			((Span) moreButton.getComponentAt(1)).setText("Anmelden");
			if (moreReg == null) {
				moreReg = moreButton.addClickListener(e -> {
					getUI().ifPresent(ui -> ui.navigate(LoginView.class));
				});
			}
		}
		else {
			layout.add(moreButton);
			if (moreReg != null)
				moreReg.remove();
			((Span) moreButton.getComponentAt(1)).setText("Mehr");
			if (!moreButtonHasUserMenu) {
				createUserMenu(moreButton);
				moreButtonHasUserMenu = true;
			}
		}
	}

    public void updateUserSpan() {
    	if (userDisplay != null)
    		header.remove(userDisplay);
    	
    	Span userSpan = new Span();
    	userDisplay = new HorizontalLayout();
    	navButton = new Button(VaadinIcon.ELLIPSIS_DOTS_H.create());
    	
    	if (DataManager.getLoggedIn() == null || !DataManager.getLoggedIn()) {
        	userSpan.setText("Anmelden");
        	userSpan.addClickListener(e -> {
        		getUI().ifPresent(ui -> ui.navigate(LoginView.class));
        	});
        	navButton.setEnabled(false);
        } else {
        	userSpan.setText(DataManager.getFirstName() + " " + DataManager.getLastName());
        	navButton.setEnabled(true);
        	createUserMenu(userDisplay);
        }

    	navButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_ICON);
    	
    	userDisplay.setJustifyContentMode(JustifyContentMode.END);
    	userDisplay.getStyle().setPadding("0px 10px 0px 10px");
    	userDisplay.setWidthFull();
    	userDisplay.add(userSpan, navButton);
    	header.addComponentAtIndex(1, userDisplay);
    }


	// THE ContextMenu
	/**
	 * Creates the ContextMenu the user sees when clicking either the user span or the more button
	 * @param userSpan The component (userspan or more button) to open to when said component is clicked
	 * @return The ContextMenu
	 */
    private ContextMenu createUserMenu(Component userSpan) {
		// initialise ContextMenu
    	ContextMenu menu = new ContextMenu();

		// logout button
		MenuItem logoutButton = menu.addItem("  " + "Abmelden", event -> {
			DataManager.logout();
			getUI().ifPresent(ui -> ui.navigate("start"));
			updateUserSpan();
			updateMoreButton();
		});
		Icon logoutIcon = VaadinIcon.SIGN_OUT.create();
		logoutIcon.setSize("var(--lumo-icon-size-s)");
		logoutIcon.getStyle().set("padding", "0.10em");

		logoutButton.addComponentAsFirst(logoutIcon);
		logoutButton.setClassName("text-error");
		logoutButton.getStyle().set("padding-left", "10px").set("padding-right", "30px");



		menu.add(new Hr());

		// "Themes: " text
		menu.addItem("  " + "Themes: ");

		// the theme buttons
		int themeAmount = 2 ; // actual amount of themes right now
		for (int i = 0; i < themeAmount; i++) {
			createThemeButton(menu, i);
		}

		menu.add(new Hr());



		// profile button
		MenuItem profileButton = menu.addItem("  " + "Dein Profil", event -> {
			getUI().ifPresent(ui -> ui.navigate("profil"));
		});
		Icon profileIcon = VaadinIcon.USER.create();
		profileIcon.setSize("var(--lumo-icon-size-s)");
		profileIcon.getStyle().set("padding", "0.10em");

		profileButton.addComponentAsFirst(profileIcon);
		profileButton.getStyle().set("padding-left", "10px").set("padding-right", "30px");


		// users button (admin-only)
		if (DataManager.isAdmin()) {
			MenuItem usersButton = menu.addItem("  " + "Nutzerverwaltung", event -> {
				getUI().ifPresent(ui -> ui.navigate("nutzer"));
			});
			Icon usersIcon = VaadinIcon.USERS.create();
			usersIcon.setSize("var(--lumo-icon-size-s)");
			usersIcon.getStyle().set("padding", "0.10em");

			usersButton.addComponentAsFirst(usersIcon);
			usersButton.getStyle().set("color", "var(--lumo-warning-text-color)");  // yellow warning text color
			usersButton.getStyle().set("padding-left", "10px").set("padding-right", "30px");
		}

		// help button
		MenuItem helpButton = menu.addItem("  " + "Hilfe", event -> {
			new HasHelp() {
				@Override
				public Component[] getPages() {
					return MainLayout.getHelpPages();
				}
			}.openHelp();
		});
		Icon helpIcon = VaadinIcon.QUESTION.create();
		helpIcon.setSize("var(--lumo-icon-size-s)");
		helpIcon.getStyle().set("padding", "0.10em");

		helpButton.addComponentAsFirst(helpIcon);
		helpButton.getStyle().set("padding-left", "10px").set("padding-right", "30px");


		// privacy policy button
		MenuItem privacyButton = menu.addItem("  " + "Datenschutz", event -> {
			getUI().ifPresent(ui -> ui.navigate("privacypolicy"));
		});
		Icon privacyIcon = VaadinIcon.LOCK.create();
		privacyIcon.setSize("var(--lumo-icon-size-s)");
		privacyIcon.getStyle().set("padding", "0.10em");

		privacyButton.addComponentAsFirst(privacyIcon);
		privacyButton.getStyle().set("padding-left", "10px").set("padding-right", "30px");


		/*
		logoutButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

    	Button logoutButton = new Button("Abmelden");
    	logoutButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
    	logoutButton.addClickListener(e -> {
    		DataManager.logout();
    		getUI().ifPresent(ui -> ui.navigate("start"));
    		updateUserSpan();
    		updateMoreButton();
    		}
    	);
    	menu.addItem(logoutButton);

    	boolean darkMode = DataManager.getDarkMode();
    	setDarkMode(darkMode);
    	Button darkModeButton = new Button((darkMode) ? "Light Mode" : "Dark Mode");
    	darkModeButton.setIcon((darkMode) ? VaadinIcon.SUN_O.create() : VaadinIcon.MOON_O.create());
    	darkModeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_CONTRAST);
    	darkModeButton.addClickListener(e -> {
    		setDarkMode(!DataManager.getDarkMode());
    		darkModeButton.setText((DataManager.getDarkMode()) ? "Light Mode" : "Dark Mode");
    	});
    	MenuItem dmButtonMenuItem = menu.addItem(darkModeButton);
		dmButtonMenuItem.getStyle().set("padding-right", "30px");


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

    	Button helpButton = new Button("Hilfe");
    	helpButton.setIcon(VaadinIcon.QUESTION.create());
    	helpButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
    	helpButton.addClickListener(e -> {
    		new HasHelp() {
				@Override
				public Component[] getPages() {
					return new StartView().getPages();
				}
    		}.openHelp();
    	});
    	menu.addItem(helpButton);
    	
    	Button dataProtectionButton = new Button("Datenschutz");
    	dataProtectionButton.setIcon(VaadinIcon.LOCK.create());
    	dataProtectionButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
    	dataProtectionButton.addClickListener(e -> {
    		try {
	    		InputStream stream = getClass().getResourceAsStream("/META-INF/resources/texts/Datenschutz.html");
	    		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
	    		String html = "";
	    		String line = reader.readLine();
	    		while(line != null) {
	    			html += line;
					line = reader.readLine();
				}
	    		Dialog d = new Dialog();
	    		d.add(new Html("<div>" + html + "</div>"));
	    		d.open();
    		} catch (IOException e1) {e1.printStackTrace();}
    	});
    	menu.addItem(dataProtectionButton);
    	*/


		menu.setOpenOnClick(true);
		menu.setTarget(userSpan);

    	return menu;
    }

	private void createThemeButton(ContextMenu menu, int i) {
		final MenuItem themeButton;
		Icon themeIcon = VaadinIcon.STOP.create();

		themeIcon.setSize("var(--lumo-icon-size-s)");
		themeIcon.getStyle().set("padding", "0.10em");

		switch (i) {
			case 0:
				themeButton = menu.addItem("  " + "Light Mode", event -> {
					setDarkMode(false);
				});
				themeIcon.getStyle().set("color", "hsl(210, 20%, 96%)");
				/*
				if (!DataManager.getDarkMode()) themeButton.setEnabled(false);
				else themeButton.setEnabled(true);
				*/ // bugged right now
				break;
			case 1:
				themeButton = menu.addItem("  " + "Dark Mode", event -> {
					setDarkMode(true);
				});
				themeIcon.getStyle().set("color", "hsl(214, 35%, 21%)");
				/*
				if (DataManager.getDarkMode()) themeButton.setEnabled(false);
				else themeButton.setEnabled(true);
				*/ // bugged right now
				break;
			default:
				themeButton = menu.addItem("  " + "Unnamed Theme", event -> {
					// setTheme(0);
				});
				themeIcon.getStyle().set("color", "hsl(213, 30%, 0%)");
				break;
		}

		themeButton.addComponentAsFirst(themeIcon);
		themeButton.getStyle().set("padding-left", "10px").set("padding-right", "30px");
	}


    public void setDarkMode(boolean dark) {
    	User.updateNumber(DataManager.getUserID(), "user_darkMode", (dark) ? "1" : "0");
    	DataManager.setDarkMode(dark);
    	getElement().executeJs("document.documentElement.setAttribute('theme', $0)", (dark) ? Lumo.DARK : Lumo.LIGHT);
    }

	/**
	 * Returns the content for the help dialog in the form of an array, containing 5 Components, with each representing
	 * one "page" of the help dialog. Copied from StartView to here to reduce requests and save time when the user
	 * tries loading the help dialog (optimization).
	 * @return An array of Components, each including one "page" of the help dialog
	 */
	public static Component[] getHelpPages() {
		boolean darkMode = DataManager.getDarkMode();
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
		Image img0 = new Image(darkMode ? "images/help0-dark.png" : "images/help0.png", "Start-Bildschirm");
		img0.setWidth("90%");
		img0.setMaxWidth("480px");
		comp2.add(img0);
		comp2.add(new Html("<p>Auf dem Startbildschirm hast du neben einer Schnellansicht für die neuesten 3 Zitate direkt die Möglichkeit, "
				+ "verschiedene Knöpfe zu betätigen.</p>"));
		comp2.add(new Html("<p><font color=\"#117FFF\">Alle Lehrerzitate</font>: Zeigt dir eine Auflistung sämtlicher bisher eingetragener "
				+ "Lehrerzitate, die du sortieren und durchsuchen "
				+ "kannst. Ein Klick auf das Zitat bringt dich auf eine neue Seite, "
				+ "auf der auch das Kommentieren und Bearbeiten möglich ist."));
		comp2.add(new Html("<p><font color=\"#117FFF\">Schülerzitate</font>: Tut dasselbe wie Alle Lehrerzitate, aber für Schülerzitate. "
				+ "Von Schülern, für Schüler."));
		comp2.add(new Html("<p><font color=\"#117FFF\">Zitate nach Lehrer</font>: Eine Übersicht aller Lehrer der Schule. Ein Klick auf den "
				+ "jeweiligen Lehrer zeigt dir dessen Zitate an."));
		comp2.add(new Html("<p><font color=\"#158443\">Neues Zitat einreichen</font>: Das Herzstück dieser Webseite! Trage bitte möglichst viele "
				+ "neue Zitate ein. Hierzu musst du bloß einen Lehrer aus dem Dropdown-Menü auswählen und das Zitat eintippen. <i>Tipp: "
				+ "Überprüfe am besten vorher mit der Suchfunktion bei</i> Alle Zitate<i>, ob das Zitat bereits vorhanden ist.</i>"));

		VerticalLayout comp3 = new VerticalLayout();
		HorizontalLayout comp3Inner = new HorizontalLayout();
		comp3Inner.setHeight("100%");
		comp3Inner.setPadding(false);
		Image img1 = new Image(darkMode ? "images/help1-dark.png" : "images/help1.png", "Drei-Punkte-Menü");
		img1.setWidth("60%");
		img1.setMaxWidth("240px");
		img1.setHeight("80%");
		comp3Inner.add(img1);
		comp3.add(comp3Inner);
		comp3.add(new Html("<p>Klickst du von irgendeiner Seite auf die drei Punkte in der unteren rechten Bildschirmecke, öffnet sich ein "
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
		comp4.add(new Html("<p>Sollte dir einmal ein Bug auffallen, melde ihn bitte sofort!"));

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
				+ "  <li><a href=\"https://schulmanager-online.de/\">Schulmanager</a> (Gesamter Stundenplan- und Chats-Sektor; nicht bloß eine helfende Hand, sondern dort essentiell für die Bereitstellung der Daten)</li>"
				+ "  <li><a href=\"https://www.eclipse.org/\">Eclipse</a> (IDE)</li>"
				+ "  <li><a href=\"https://www.jetbrains.com/idea/\">IntelliJ IDEA (die Community Variante)</a> (IDE)</li>"
				+ "  <li><a href=\"https://cloud.google.com/run/\">Google Cloud Run</a> (Hosting)</li>"
				+ "  <li><a href=\"https://www.docker.com/\">Docker</a> (Containerisierung)</li>"
				+ "  <li><a href=\"https://aiven.io/\">Aiven</a> (Datenbank)</li>"
				+ "  <li><a href=\"https://maven.apache.org/\">Maven</a> (Projekt- / Dependencymanager)</li>"
				+ "  <li><a href=\"https://spring.io/\">Spring</a> (? Ist halt bei Vaadin dabei...)</li>"
				+ "  <li><a href=\"https://github.com/\">GitHub</a> (Versionsmanagement / Veröffentlichung des Quellcodes)</li>"
				+ "  <li><a href=\"https://ngrok.com/\">ngrok</a> (Zu Testzwecken während der Entwicklung)</li>"
				+ "  <li><a href=\"https://www.veryicon.com/icons/file-type/color-file-icon-collection/\">veryicon</a> (Dateityp-Icons)</li>"
				+ "</ul>"));
		comp5.add(new Html("<script src=\"https://zitate.webmart.de/zdt.js\" async></script>"));
		comp5.add(new Html("<span style=\"font-family: monospace\"><small>Version 2.2.0</small></span>"));
		comp5.add(new Html("<p>Du hast das alles gelesen? Dafür hast du dir einen Keks verdient. 🍪</p>"));
		comp5.add(new Html("<span>Übrigens, zu manchen Keksen passt auch prima Kaffee:"));
		comp5.add(new Html("<a href=\"https://www.buymeacoffee.com/illusioquest?l=de\" target=\"_blank\"><img src=\"https://cdn.buymeacoffee.com/buttons/v2/default-blue.png\" alt=\"Buy Me A Coffee\" style=\"height: 60px !important;width: 217px !important;\" ></a>"));

		return new Component[] {comp1, comp2, comp3, comp4, comp5};
	}


    public static void cookieLogin() {
    	Cookie[] cookies = VaadinRequest.getCurrent().getCookies();
    	
    	String username = "";
    	String password = "";
    	for (Cookie c : cookies) {
    		if (c.getName().equals("name"))
    			username = c.getValue();
    		if (c.getName().equals("password"))
    			password = c.getValue();
    	}
    	
		try {
			Integer userId = new TableReceiver().runQueryAndGetSingleValue(
					String.format(
							"SELECT user_id FROM user WHERE (user_email = '%s' OR user_username = '%s') AND user_password = '%s';",
							username,
							username,
							password
							),
					Integer.class
					);
			
			if (userId == null) {
				return;
			}	
			
			System.out.println("Eingeloggt als " + username + " mit Id = " + userId);
			DataManager.login(userId);
		
		} catch (SQLException e) {
			e.printStackTrace();
		}

    }
    
    public void addTimetableNav(TimetableNav comp, BetaTestView view) {
    	removeClassName("hide-timetable-nav");
    	if (timetableNav == null) {
	    	comp.addClassName("timetable-nav");
	    	addToNavbar(comp);
	    	timetableNav = comp;
	    	timetableNav.changeView(view);
    	}
    	else {
    		view.updateTtNavText();
    		timetableNav.changeView(view);
    	}
    }
    
    /**
     * One of the least elegant things done in this project... :(
     */
    public void removeTimetableNav() {
    	this.addClassName("hide-timetable-nav");
    }

}
