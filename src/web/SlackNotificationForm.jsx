import React from 'react';
import PropTypes from 'prop-types';
import lodash from 'lodash';

import {Input} from 'components/bootstrap';
import FormsUtils from 'util/FormsUtils';

const DEFAULT_MSG = `# --- [Event Definition] ---------------------------  
**ID:**                   \${event_definition_id}  
**Type:**                 \${event_definition_type}  
**Title:**                \${event_definition_title}  
**Description:**          \${event_definition_description}  
# --- [Event] --------------------------------------  
**Event:**                \${event}  
# --- [Event Detail] -------------------------------  
**Timestamp:**            \${event.timestamp}  
**Message:**              \${event.message}  
**Source:**               \${event.source}  
**Key:**                  \${event.key}  
**Priority:**             \${event.priority}  
**Alert:**                \${event.alert}  
**Timestamp Processing:** \${event.timestamp}  
**TimeRange Start:**      \${event.timerange_start}  
**TimeRange End:**        \${event.timerange_end}  
\${if event.fields}
**Fields:**  
\${foreach event.fields field}  \${field.key}: \${field.value}  
\${end}
\${end}
\${if backlog}
# --- [Backlog] ------------------------------------  
**Messages:**  
\${foreach backlog message}
\`\`\`
\${message}  
\`\`\`
\${end}
\${end}`;

class SlackNotificationForm extends React.Component {
  static propTypes = {
    config: PropTypes.object.isRequired,
    validation: PropTypes.object.isRequired,
    onChange: PropTypes.func.isRequired,
  };

  static defaultConfig = {
    color: '#FF0000',
    custom_message: DEFAULT_MSG,
    webhook_url: '',
    channel: '',
    user_name: '',
    add_backlog_items: 5,
    notify_channel: false,
    link_names: false,
    icon_url: '',
    icon_emoji: '',
    graylog_url: '',
    proxy_url: '',
  };

  propagateChange = (key, value) => {
    const { config, onChange } = this.props;
    const nextConfig = lodash.cloneDeep(config);
    nextConfig[key] = value;
    onChange(nextConfig);
  };

  handleChange = (event) => {
    const { name } = event.target;
    this.propagateChange(name, FormsUtils.getValueFromInput(event.target));
  };

  handleBodyTemplateChange = (nextValue) => {
    this.propagateChange('body_template', nextValue);
  };

  handleRecipientsChange = (key) => {
    return nextValue => this.propagateChange(key, nextValue === '' ? [] : nextValue.split(','));
  };

  render() {
    const { config, validation } = this.props;

    return (
      <React.Fragment>
        <Input id="notification-color"
               name="color"
               label="Color"
               type="text"
               bsStyle={validation.errors.color ? 'error' : null}
               help={lodash.get(validation, 'errors.color[0]', 'Color to use for Slack custom message')}
               value={config.color || ''}
               onChange={this.handleChange}/>
        <Input id="notification-custom-message"
               name="cutom-message"
               label="Custom Message"
               type="textarea"
               bsStyle={validation.errors.cutom_message ? 'error' : null}
               help={lodash.get(validation, 'errors.custom_message[0]',
                   'Custom message to be appended below the alert title. The following properties are available for template building: "stream", "check_result", "stream_url", "alert_condition", "backlog", "backlog_size".See http://docs.graylog.org/en/2.3/pages/streams/alerts.html#email-alert-notification for more details.')}
               value={config.custom_message || ''}
               onChange={this.handleChange}/>
        <Input id="notification-webhook-url"
               name="webhook_url"
               label="Webhook URL"
               type="text"
               bsStyle={validation.errors.webhook_url ? 'error' : null}
               help={lodash.get(validation, 'errors.webhook_url[0]',
                   'Slack "Incoming Webhook" URL')}
               value={config.webhook_url || ''}
               onChange={this.handleChange}
               required />
        <Input id="notification-channel"
               name="channel"
               label="Channel"
               type="text"
               bsStyle={validation.errors.channel ? 'error' : null}
               help={lodash.get(validation, 'errors.channel[0]',
                   'Slack channel')}
               value={config.channel || ''}
               onChange={this.handleChange}
               required />
        <Input id="notification-user-name"
               name="user_name"
               label="Channel"
               type="text"
               bsStyle={validation.errors.user_name ? 'error' : null}
               help={lodash.get(validation, 'errors.user_name[0]',
                   'Slack username')}
               value={config.user_name || ''}
               onChange={this.handleChange}
               required />
        <Input id="notification-add-backlogs"
               name="backlog_items"
               label="Backlog Items"
               type="number"
               bsStyle={validation.errors.backlog_items ? 'error' : null}
               help={lodash.get(validation, 'errors.backlog_items[0]',
                   'Number of backlog item descriptions to attach')}
               value={config.backlog_items || ''}
               onChange={this.handleChange}
               required />
        <Input id="notification-notify-channel"
               name="notify_channel"
               label="Notify Channel"
               type="checkbox"
               bsStyle={validation.errors.notify_channel ? 'error' : null}
               help={lodash.get(validation, 'errors.notify_channel[0]',
                   'Notify all users in channel by adding @channel to the message.')}
               value={config.notify_channel || ''} />
        <Input id="notification-link-names"
               name="link_names"
               label="Link Names"
               type="checkbox"
               bsStyle={validation.errors.link_names ? 'error' : null}
               help={lodash.get(validation, 'errors.link_names[0]',
                   'Find and link channel names and user names')}
               value={config.link_names || ''} />
        <Input id="notification-icon-url"
               name="icon_url"
               label="Icon URL"
               type="text"
               bsStyle={validation.errors.icon_url ? 'error' : null}
               help={lodash.get(validation, 'errors.icon_url[0]',
                   'Image to use as the icon for this message')}
               value={config.icon_url || ''}
               onChange={this.handleChange}/>
        <Input id="notification-icon-emoji"
               name="icon_emoji"
               label="Icon URL"
               type="text"
               bsStyle={validation.errors.icon_emoji ? 'error' : null}
               help={lodash.get(validation, 'errors.icon_emoji[0]',
                   'Emoji to use as the icon for this message (overrides Icon URL)')}
               value={config.icon_emoji || ''}
               onChange={this.handleChange}/>
        <Input id="notification-graylog-url"
               name="graylog_url"
               label="Graylog URL"
               type="text"
               bsStyle={validation.errors.graylog_url ? 'error' : null}
               help={lodash.get(validation, 'errors.graylog_url[0]',
                   'URL to your Graylog web interface. Used to build links in alarm notification.')}
               value={config.graylog_url || ''}
               onChange={this.handleChange}/>
        <Input id="notification-proxy-url"
               name="proxy_url"
               label="Proxy"
               type="text"
               bsStyle={validation.errors.proxy_url ? 'error' : null}
               help={lodash.get(validation, 'errors.proxy_url[0]',
                   'Please insert the proxy information in the follwoing format: <ProxyAddress>:<Port>')}
               value={config.proxy_url || ''}
               onChange={this.handleChange}/>
      </React.Fragment>
    );
  }
}

export default SlackNotificationForm;
