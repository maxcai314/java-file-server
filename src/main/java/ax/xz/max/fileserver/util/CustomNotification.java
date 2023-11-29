package ax.xz.max.fileserver.util;

import com.vaadin.flow.component.notification.Notification;

public class CustomNotification extends Notification {

	private static final Position DEFAULT_POSITION = Position.BOTTOM_START;

	public CustomNotification() {
		super();
	}

	public CustomNotification(String text) {
		super(text);
	}

	public CustomNotification(String text, int duration) {
		super(text, duration);
	}

	public CustomNotification(String text, int duration, Position position) {
		super(text, duration, position);
	}

	public static CustomNotification show(String text, int duration, Position position) {
		CustomNotification notification = new CustomNotification(text, duration, position);
		notification.open();
		return notification;
	}

	public static CustomNotification show(String text) {
		return show(text, 5000, DEFAULT_POSITION);
	}

	@Override
	public void setText(String text) {
		removeAll();
		getElement().setProperty("text", text); // the bozo devs who wrote this library decided to escape this text
		getElement().callJsFunction("requestContentUpdate");
	}
}
