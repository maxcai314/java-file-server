package ax.xz.max.fileserver.util;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
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

	@Override
	public void setText(String text) {
		removeAll();
		getElement().setProperty("text", text); // the bozo devs who wrote this library decided to escape this text
		getElement().callJsFunction("requestContentUpdate");
	}

	public static CustomNotification create(int duration, Position position) {
		return new CustomNotification(null, duration, position);
	}

	public void addHorizontally(String text, Component... components) {
		HorizontalLayout layout = new HorizontalLayout();
		layout.add(new Div(new Text(text)));
		for (Component component : components) {
			layout.add(component);
		}
		add(layout);
	}

	public static CustomNotification show(String text, int duration, Position position, Component... components) {
		CustomNotification notification = create(duration, position);
		notification.addHorizontally(text, components);
		notification.open();
		return notification;
	}

	public static CustomNotification show(String text, Component... components) {
		return show(text, 5000, DEFAULT_POSITION, components);
	}

	public static CustomNotification show(String text) {
		return show(text, new Component[]{});
	}

	public static CustomNotification showError(String text, int duration, Position position, Component... components) {
		CustomNotification notification = create(duration, position);
		notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
		notification.addHorizontally(text, components);
		notification.open();
		return notification;
	}

	public static CustomNotification showError(String text, Component... components) {
		return showError(text, 5000, DEFAULT_POSITION, components);
	}

	public static CustomNotification showError(String text) {
		return showError(text, new Component[]{});
	}

	public static CustomNotification persistError(String text, Position position, Component... components) {
		CustomNotification notification = create(0, position);
		notification.addThemeVariants(NotificationVariant.LUMO_ERROR);

		Div statusText = new Div(new Text(text));

		Button closeButton = new Button(new Icon("lumo", "cross"));
		closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
		closeButton.getElement().setAttribute("aria-label", "Close");
		closeButton.addClickListener(event -> notification.close());

		Component[] newComponents = new Component[components.length + 1];
		System.arraycopy(components, 0, newComponents, 0, components.length);
		newComponents[components.length] = closeButton;

		notification.addHorizontally(text, newComponents);

		notification.open();
		return notification;
	}

	public static CustomNotification persistError(String text, Component... components) {
		return persistError(text, DEFAULT_POSITION, components);
	}

	public static CustomNotification persistError(String text) {
		return persistError(text, new Component[]{});
	}

	public static CustomNotification showSuccess(String text, int duration, Position position, Component... components) {
		CustomNotification notification = create(duration, position);
		notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
		notification.addHorizontally(text, components);
		notification.open();
		return notification;
	}

	public static CustomNotification showSuccess(String text, Component... components) {
		return showSuccess(text, 5000, DEFAULT_POSITION, components);
	}

	public static CustomNotification showSuccess(String text) {
		return showSuccess(text, new Component[]{});
	}
}
