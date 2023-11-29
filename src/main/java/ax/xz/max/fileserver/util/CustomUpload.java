package ax.xz.max.fileserver.util;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DomEvent;
import com.vaadin.flow.component.upload.Receiver;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.shared.Registration;

public class CustomUpload extends Upload {

	public CustomUpload() {
		super();
	}

	public CustomUpload(Receiver receiver) {
		super(receiver);
	}

	public Registration addFileRemoveListener(ComponentEventListener<CustomUpload.FileRemoveEvent> listener) {
		return super.addListener(CustomUpload.FileRemoveEvent.class, listener);
	}

	@DomEvent("file-remove")
	public static class FileRemoveEvent extends ComponentEvent<Upload> {
		public FileRemoveEvent(Upload source, boolean fromClient) {
			super(source, fromClient);
		}
	}
}
