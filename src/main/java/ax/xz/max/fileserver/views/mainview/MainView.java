package ax.xz.max.fileserver.views.mainview;

import ax.xz.max.fileserver.util.CustomNotification;
import ax.xz.max.fileserver.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Home")
@Route(value = "", layout = MainLayout.class)
public class MainView extends VerticalLayout {

    public MainView() {
        H1 header = new H1("My File Server");
        header.getStyle().set("margin-bottom", "2px");

        add(
                header,
                new Span("Upload files on the 'upload' page"),
                new Span("hope it works"),
                new Button("Click me", e -> CustomNotification.show("Java << JavaScript")),
                new Span("< is supposed to work")
        );
    }

}
