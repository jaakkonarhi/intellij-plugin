package fi.aalto.cs.apluscourses.intellij.notifications;

import static fi.aalto.cs.apluscourses.utils.PluginResourceBundle.getText;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;

public class ExerciseNotSelectedNotification extends Notification {

  /**
   * Constructs a notification that notifies the user that no exercise is selected. This should be
   * shown when the user uses the exercise submission button, but no exercise is selected.
   */
  public ExerciseNotSelectedNotification() {
    super(
        "A+",
        getText("notification.ExerciseNotSelectedNotification.title"),
        getText("notification.ExerciseNotSelectedNotification.content"),
        NotificationType.INFORMATION);
  }

}
