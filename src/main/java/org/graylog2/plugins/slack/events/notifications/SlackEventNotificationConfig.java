package org.graylog2.plugins.slack.events.notifications;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import javax.validation.constraints.NotBlank;
import org.graylog.events.contentpack.entities.EventNotificationConfigEntity;
import org.graylog.events.event.EventDto;
import org.graylog.events.notifications.EventNotificationConfig;
import org.graylog.events.notifications.EventNotificationExecutionJob;
import org.graylog.scheduler.JobTriggerData;
import org.graylog2.contentpacks.EntityDescriptorIds;
import org.graylog2.plugin.rest.ValidationResult;

@AutoValue
@JsonTypeName(SlackEventNotificationConfig.TYPE_NAME)
@JsonDeserialize(builder = SlackEventNotificationConfig.Builder.class)
public abstract class SlackEventNotificationConfig implements EventNotificationConfig {
  public static final String TYPE_NAME = "slack-notification-v3";

  // Default values
  public static final String DEFAULT_MESSAGE = "TODO, This is default custom message";

  private static final String CK_COLOR = "color";
  private static final String CK_CUSTOM_MESSAGE = "custom_message";
  private static final String CK_WEBHOOK_URL = "webhook_url";
  private static final String CK_CHANNEL = "channel";
  private static final String CK_USER_NAME = "user_name";
  private static final String CK_ADD_BLITEMS = "backlog_items";
  private static final String CK_NOTIFY_CHANNEL = "notify_channel";
  private static final String CK_LINK_NAMES = "link_names";
  private static final String CK_ICON_URL = "icon_url";
  private static final String CK_ICON_EMOJI = "icon_emoji";
  private static final String CK_GRAYLOG2_URL = "graylog2_url";
  private static final String CK_PROXY_ADDRESS = "proxy_address";

  // // TODO: What this?
  // private static final String CK_SHORT_MODE = "short_mode";
  // private static final String CK_ADD_DETAILS = "add_details";

  @JsonProperty(CK_COLOR)
  @NotBlank
  public abstract String color();

  @JsonProperty(CK_CUSTOM_MESSAGE)
  public abstract String customMessage();

  @JsonProperty(CK_WEBHOOK_URL)
  @NotBlank
  public abstract String webhookUrl();

  @JsonProperty(CK_CHANNEL)
  @NotBlank
  public abstract String channel();

  @JsonProperty(CK_USER_NAME)
  public abstract String userName();

  @JsonProperty(CK_ADD_BLITEMS)
  public abstract String addBacklogItems();

  @JsonProperty(CK_NOTIFY_CHANNEL)
  @NotBlank
  public abstract boolean notifyChannel();

  @JsonProperty(CK_LINK_NAMES)
  public abstract boolean linksName();

  @JsonProperty(CK_ICON_URL)
  public abstract String iconUrl();

  @JsonProperty(CK_ICON_EMOJI)
  public abstract String iconEmoji();

  @JsonProperty(CK_GRAYLOG2_URL)
  public abstract String graylog2Url();

  @JsonProperty(CK_PROXY_ADDRESS)
  public abstract String proxyAddress();
  //
  // // TODO: What this?
  // @JsonProperty(CK_SHORT_MODE)
  // public abstract String shortMode();
  //
  // // TODO: What this?
  // @JsonProperty(CK_ADD_DETAILS)
  // public abstract String addDetails();

  public JobTriggerData toJobTriggerData(EventDto eventDto) {
    return EventNotificationExecutionJob.Data.builder().eventDto(eventDto).build();
  }

  public static Builder builder() {
    return Builder.create();
  }

  @AutoValue.Builder
  public static abstract class Builder implements EventNotificationConfig.Builder<Builder> {
    @JsonCreator
    public static Builder create() {
      return new AutoValue_SlackEventNotificationConfig.Builder()
          .type(TYPE_NAME)
          .color("")
          .customMessage("")
          .webhookUrl("")
          .channel("")
          .userName("")
          .addBacklogItems("")
          .notifyChannel("")
          .linksName("")
          .iconUrl("")
          .iconEmoji("")
          .graylog2Url("")
          .proxyAddress("");
          // .shortMode()
          // .addDetails();
    }

    @JsonProperty(CK_COLOR)
    public abstract Builder color(String color);

    @JsonProperty(CK_CUSTOM_MESSAGE)
    public abstract Builder customMessage(String customMessage);

    @JsonProperty(CK_WEBHOOK_URL)
    public abstract Builder webhookUrl(String webhookUrl);

    @JsonProperty(CK_CHANNEL)
    public abstract Builder channel(String channel);

    @JsonProperty(CK_USER_NAME)
    public abstract Builder userName(String userName);

    @JsonProperty(CK_ADD_BLITEMS)
    public abstract Builder addBacklogItems(String addBacklogItems);

    @JsonProperty(CK_NOTIFY_CHANNEL)
    public abstract Builder notifyChannel(String notifyChannel);

    @JsonProperty(CK_LINK_NAMES)
    public abstract Builder linksName(String linksName);

    @JsonProperty(CK_ICON_URL)
    public abstract Builder iconUrl(String iconUrl);

    @JsonProperty(CK_ICON_EMOJI)
    public abstract Builder iconEmoji(String iconEmoji);

    @JsonProperty(CK_GRAYLOG2_URL)
    public abstract Builder graylog2Url(String graylog2Url);

    @JsonProperty(CK_PROXY_ADDRESS)
    public abstract Builder proxyAddress(String proxyAddress);

    public abstract SlackEventNotificationConfig build();
  }

  public ValidationResult validate() {
    ValidationResult validation = new ValidationResult();

    if (webhookUrl().isEmpty()) {
      validation.addError(CK_WEBHOOK_URL, "Webhook URL cannot be empty.");
    }

    return null;
  }

  public EventNotificationConfigEntity toContentPackEntity(EntityDescriptorIds entityDescriptorIds) {
    return null;
  }
}
