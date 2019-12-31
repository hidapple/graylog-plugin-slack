package org.graylog2.plugins.slack.events.notifications;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.floreysoft.jmte.Engine;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.graylog.events.notifications.EventNotification;
import org.graylog.events.notifications.EventNotificationContext;
import org.graylog.events.notifications.EventNotificationException;
import org.graylog.events.notifications.EventNotificationModelData;
import org.graylog.events.notifications.EventNotificationService;
import org.graylog.events.processor.EventDefinitionDto;
import org.graylog.scheduler.JobTriggerDto;
import org.graylog2.jackson.TypeReferences;
import org.graylog2.plugin.MessageSummary;
import org.graylog2.plugins.slack.SlackClient;
import org.graylog2.plugins.slack.SlackClient.SlackClientException;
import org.graylog2.plugins.slack.SlackMessage;

public class SlackEventNotification implements EventNotification {

  public interface Factory extends EventNotification.Factory {
    @Override
    SlackEventNotification create();
  }

  private final Engine templateEngine;
  private static final String UNKNOWN = "<unknown>";

  private final EventNotificationService notificationCallbackService;
  private final ObjectMapper objMapper;

  @Inject
  public SlackEventNotification(EventNotificationService notificationCallbackService,
                                Engine templateEngine,
                                ObjectMapper objMapper) {
    this.notificationCallbackService = notificationCallbackService;
    this.templateEngine = templateEngine;
    this.objMapper = objMapper;
  }

  @Override
  public void execute(EventNotificationContext ctx) throws EventNotificationException {
    SlackEventNotificationConfig config = (SlackEventNotificationConfig) ctx.notificationConfig();
    ImmutableList<MessageSummary> backlog = notificationCallbackService.getBacklogForEvent(ctx);
    Map<String, Object> model = getModel(ctx, backlog);
    SlackMessage msg = createSlackMessage(config, buildMessage(config, model));

    // Add custom message if exists
    String customMessage = buildCustomMessage(config, model);
    msg.setCustomMessage(customMessage);

    SlackClient slackClient = new SlackClient(config);
    try {
      slackClient.send(msg);
    } catch (SlackClientException ex) {
      throw new RuntimeException("Failed to send message to Slack.", ex);
    }
  }

  private Map<String, Object> getModel(EventNotificationContext ctx, ImmutableList<MessageSummary> backlog) {
    Optional<EventDefinitionDto> definitionDto = ctx.eventDefinition();
    Optional<JobTriggerDto> jobTriggerDto = ctx.jobTrigger();
    EventNotificationModelData modelData = EventNotificationModelData.builder()
        .eventDefinitionId(definitionDto.map(EventDefinitionDto::id).orElse(UNKNOWN))
        .eventDefinitionType(definitionDto.map(d -> d.config().type()).orElse(UNKNOWN))
        .eventDefinitionTitle(definitionDto.map(EventDefinitionDto::title).orElse(UNKNOWN))
        .eventDefinitionDescription(definitionDto.map(EventDefinitionDto::description).orElse(UNKNOWN))
        .jobDefinitionId(jobTriggerDto.map(JobTriggerDto::jobDefinitionId).orElse(UNKNOWN))
        .jobTriggerId(jobTriggerDto.map(JobTriggerDto::id).orElse(UNKNOWN))
        .event(ctx.event())
        .backlog(backlog)
        .build();
    return objMapper.convertValue(modelData, TypeReferences.MAP_STRING_OBJECT);
  }

  private static SlackMessage createSlackMessage(SlackEventNotificationConfig config, String message) {
    String color = config.color();
    String emoji = config.iconEmoji();
    String url = config.iconUrl();
    String user = config.userName();
    String channel = config.channel();
    boolean linkNames = config.linksName() || config.notifyChannel();
    return new SlackMessage(color, emoji, url, message, user, channel, linkNames);
  }

  private String buildMessage(SlackEventNotificationConfig config, Map<String, Object> model) {
    String graylogUrl = config.graylog2Url();
    String title;
    if (!StringUtils.isEmpty(graylogUrl)) {
      title = "<" + graylogUrl + "|" + model.get("event_definition_title");
    } else {
      title =  "_" + model.get("event_definition_title") + "_";
    }

    String audience = config.notifyChannel() ? "@channel" : "";
    String description = model.get("event_definition_description").toString();
    return String.format("%s*Alert for Graylog stream %s*:\n> %s \n", audience, title, description);
  }

  private String buildCustomMessage(SlackEventNotificationConfig config, Map<String, Object> model) {
    String template;
    if (Strings.isNullOrEmpty(config.customMessage())) {
      template = SlackEventNotificationConfig.DEFAULT_MESSAGE;
    } else {
      template = config.customMessage();
    }
    return templateEngine.transform(template, model);
  }
}
