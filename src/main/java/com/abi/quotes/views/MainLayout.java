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
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
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
	
	private VerticalLayout moreButton;
	
	public TimetableNav timetableNav;
	
	private boolean moreButtonHasUserMenu = false;
	private Registration moreReg;

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
        
    }

    private Header header;
    
    private Component createHeaderContent() {
        header = new Header();
        header.addClassNames(BoxSizing.BORDER, Display.FLEX, FlexDirection.COLUMN, Width.FULL);

        Div layout = new Div();
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
    
    public void updateMoreButton() {
    	if (DataManager.getLoggedIn() == null || !DataManager.getLoggedIn()) {
        	((Span) moreButton.getComponentAt(1)).setText("Anmelden");
        	if (moreReg == null)
	        	moreReg = moreButton.addClickListener(e -> {
	        		getUI().ifPresent(ui -> ui.navigate(LoginView.class));
	        	});
        } else {
        	if (moreReg != null)
        		moreReg.remove();
        	((Span) moreButton.getComponentAt(1)).setText("Mehr");
        	if (!moreButtonHasUserMenu) {
        		createUserMenu(moreButton);
        		moreButtonHasUserMenu = true;
        	}
        }
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
    
    private ContextMenu createUserMenu(Component userSpan) {
    	ContextMenu menu = new ContextMenu();
    	
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
    	
    	
    	menu.setOpenOnClick(true);
    	menu.setTarget(userSpan);
        	
    	return menu;
    }
    
    public void setDarkMode(boolean dark) {
    	User.updateNumber(DataManager.getUserID(), "user_darkMode", (dark) ? "1" : "0");
    	DataManager.setDarkMode(dark);
    	getElement().executeJs("document.documentElement.setAttribute('theme', $0)", (dark) ? Lumo.DARK : Lumo.LIGHT);
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
