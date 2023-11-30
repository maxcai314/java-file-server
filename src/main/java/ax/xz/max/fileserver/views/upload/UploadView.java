package ax.xz.max.fileserver.views.upload;

import ax.xz.max.fileserver.util.*;
import ax.xz.max.fileserver.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.receivers.FileData;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

@PageTitle("Upload")
@Route(value = "upload", layout = MainLayout.class)
public class UploadView extends VerticalLayout {

    private static final int MEGA_BYTE = 2 << 19;

    private final AtomicInteger numFiles = new AtomicInteger(0);

    public UploadView(FileDataRepository fileDatabase, @Value("${ax.xz.max.fileserver.file-path}") Path uploadPath) {
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
        upload.setMaxFileSize(50 * MEGA_BYTE);

        Button submitButton = new Button("Submit");

        submitButton.setEnabled(false);

        upload.addSucceededListener(event -> submitButton.setEnabled(numFiles.incrementAndGet() > 0));
        upload.addFileRemoveListener(event -> submitButton.setEnabled(numFiles.decrementAndGet() > 0));

        // todo: give submitButton functionality
        submitButton.addClickListener(event -> {
            CustomNotification.show("Uploading... (testing)");
            String fileName = buffer.getFiles().iterator().next();
            Path filePath = uploadPath.resolve(fileName);
            FileDataEntity fileData = fileDatabase.findByPath(filePath.toString());
            if (fileData == null) fileData = new FileDataEntity();
            fileData.setFilePath(filePath);
            fileData.setUploadDate(Instant.now());
            fileData.setVisibility(FileVisibility.PUBLIC);
            fileDatabase.save(fileData);
            FileData data = buffer.getFileData(fileName);
            try {
                OutputStream dataFile = data.getOutputBuffer();
                InputStream file = new ByteArrayInputStream(((ByteArrayOutputStream) dataFile).toByteArray());
                Files.copy(file, filePath);
            } catch (IOException e) {
                Notification.show("Error uploading file");
                e.printStackTrace();
            }
        });

        Div container = new Div(
                header, text, upload, submitButton
        );
        container.getStyle().set("margin-left", "8px");

        add(container);

    }

}