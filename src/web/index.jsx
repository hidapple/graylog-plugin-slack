import { PluginManifest, PluginStore } from 'graylog-web-plugin/plugin';

import SlackNotificationForm from './SlackNotificationForm';
import SlackNotificationSummary from './SlackNotificationSummary';

PluginStore.register(new PluginManifest({}, {
  eventNotificationTypes: [
    {
      type: 'slack-notification-v2',
      displayName: 'Slack Notification',
      formComponent: SlackNotificationForm,
      summaryComponent: SlackNotificationSummary,
      defaultConfig: SlackNotificationForm.defaultConfig
    }
  ]
}));
