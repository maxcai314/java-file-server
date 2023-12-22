package ax.xz.max.fileserver.util;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

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

	public static CustomNotification showError(String text, int duration, Position position) {
		CustomNotification notification = new CustomNotification(text, duration, position);
		notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
		notification.open();
		return notification;
	}

	public static CustomNotification showError(String text) {
		return showError(text, 5000, DEFAULT_POSITION);
	}

	public static CustomNotification persistError(String text, Position position) {
		CustomNotification notification = new CustomNotification();
		notification.addThemeVariants(NotificationVariant.LUMO_ERROR);

		Div statusText = new Div(new Text(text));

		Button closeButton = new Button(new Icon("lumo", "cross"));
		closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
		closeButton.getElement().setAttribute("aria-label", "Close");
		closeButton.addClickListener(event -> notification.close());

		HorizontalLayout layout = new HorizontalLayout(statusText, closeButton);
		layout.setAlignItems(FlexComponent.Alignment.CENTER);

		notification.add(layout);
		notification.setPosition(position);

		notification.open();
		return notification;
	}

	public static CustomNotification persistError(String text) {
		return persistError(text, DEFAULT_POSITION);
	}

	@Override
	public void setText(String text) {
		removeAll();
		getElement().setProperty("text", text); // the bozo devs who wrote this library decided to escape this text
		getElement().callJsFunction("requestContentUpdate");
	}
}
