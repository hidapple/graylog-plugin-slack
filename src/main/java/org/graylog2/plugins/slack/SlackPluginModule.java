package org.graylog2.plugins.slack;

import org.graylog2.plugins.slack.callback.SlackAlarmCallback;
import org.graylog2.plugins.slack.events.notifications.SlackEventNotification;
import org.graylog2.plugins.slack.events.notifications.SlackEventNotificationConfig;
import org.graylog2.plugins.slack.output.SlackMessageOutput;
import org.graylog2.plugin.PluginModule;

public class SlackPluginModule extends PluginModule {
    @Override
    protected void configure() {
        addAlarmCallback(SlackAlarmCallback.class);
        addMessageOutput(SlackMessageOutput.class);
        addNotificationType(SlackEventNotificationConfig.TYPE_NAME,
                            SlackEventNotificationConfig.class,
                            SlackEventNotification.class,
                            SlackEventNotification.Factory.class);
    }
}
