package ax.xz.max.fileserver.views.upload;

import ax.xz.max.fileserver.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.concurrent.atomic.AtomicInteger;

@PageTitle("Upload")
@Route(value = "upload", layout = MainLayout.class)
public class UploadView extends VerticalLayout {

    private static final int MEGA_BYTE = 2 << 19;

    private final AtomicInteger numFiles = new AtomicInteger(0);

    public UploadView() {
        H1 header = new H1("Upload Files");
        header.getStyle().set("margin-bottom", "2px");

        Span text = new Span("Max file size: 50 MB");

        MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
        CustomUpload upload = new CustomUpload(buffer);

        Button chooseFileButton = new Button("Upload files...");
        chooseFileButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        upload.setUploadButton(chooseFileButton);
        upload.setDropLabel(new Span("Drop files here"));

        upload.setMaxFiles(15);
        upload.setMaxFileSize(50 * 1024 * 1024);

        Button submitButton = new Button("Submit", e -> {
            Notification.show("Upload finished");
        });

        submitButton.setEnabled(false);

        upload.addSucceededListener(event -> submitButton.setEnabled(numFiles.incrementAndGet() > 0));
        upload.addFileRemoveListener(event -> submitButton.setEnabled(numFiles.decrementAndGet() > 0));

        Div container = new Div(
                header, text, upload, submitButton
        );
        container.getStyle().set("margin-left", "8px");

        add(container);

    }

}