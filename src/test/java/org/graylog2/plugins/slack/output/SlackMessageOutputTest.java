package org.graylog2.plugins.slack.output;

import com.floreysoft.jmte.Engine;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.graylog2.plugin.configuration.Configuration;
import org.graylog2.plugin.outputs.MessageOutputConfigurationException;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertThat;

public class SlackMessageOutputTest {
    private static final ImmutableMap<String, Object> VALID_CONFIG_SOURCE = ImmutableMap.<String, Object>builder()
            .put("webhook_url", "https://www.example.org/")
            .put("channel", "#test_channel")
            .put("user_name", "test_user_name")
            .put("notify_channel", true)
            .put("link_names", true)
            .put("icon_url", "http://example.com")
            .put("icon_emoji", "test_icon_emoji")
            .put("graylog2_url", "http://graylog2.example.com")
            .put("color", "#FF0000")
            .build();

    @Test
    public void testGetAttributes() throws MessageOutputConfigurationException {
        SlackMessageOutput output = new SlackMessageOutput(null, new Configuration(VALID_CONFIG_SOURCE), Engine.createEngine());

        final Map<String, Object> attributes = output.getConfiguration();
        assertThat(attributes.keySet(), hasItems("webhook_url", "channel", "user_name",
                "notify_channel", "link_names", "icon_url", "icon_emoji", "graylog2_url", "color"));
    }

    @Test
    public void checkConfigurationSucceedsWithValidConfiguration() throws MessageOutputConfigurationException {
        new SlackMessageOutput(null, new Configuration(VALID_CONFIG_SOURCE), Engine.createEngine());
    }

    @Test(expected = MessageOutputConfigurationException.class)
    public void checkConfigurationFailsIfApiTokenIsMissing() throws MessageOutputConfigurationException {
        new SlackMessageOutput(null, validConfigurationWithout("webhook_url"), Engine.createEngine());
    }

    @Test(expected = MessageOutputConfigurationException.class)
    public void checkConfigurationFailsIfChannelIsMissing() throws MessageOutputConfigurationException {
        new SlackMessageOutput(null, validConfigurationWithout("channel"), Engine.createEngine());
    }

    @Test
    public void checkConfigurationWorksWithCorrectChannelNotations() throws MessageOutputConfigurationException {
        new SlackMessageOutput(null, validConfigurationWithValue("channel", "#valid_channel"),
                Engine.createEngine());
    }

    @Test
    public void checkConfigurationWorksWithCorrectDirectMessageNotations() throws MessageOutputConfigurationException {
        new SlackMessageOutput(null, validConfigurationWithValue("channel", "@john"),
                Engine.createEngine());
    }

    @Test
    public void checkConfigurationWorksWithCorrectProxyAddress() throws MessageOutputConfigurationException {
        new SlackMessageOutput(null, validConfigurationWithValue("proxy_address", "http://127.0.0.1:1080"),
                Engine.createEngine());
    }

    @Test(expected = MessageOutputConfigurationException.class)
    public void checkConfigurationFailsIfIconUrlIsInvalid() throws MessageOutputConfigurationException {
        new SlackMessageOutput(null, validConfigurationWithValue("icon_url", "Definitely$$Not#A!!URL"),
                Engine.createEngine());
    }

    @Test(expected = MessageOutputConfigurationException.class)
    public void checkConfigurationFailsIfIconUrlIsNotHttpOrHttps() throws MessageOutputConfigurationException {
        new SlackMessageOutput(null, validConfigurationWithValue("icon_url", "ftp://example.net"),
                Engine.createEngine());
    }

    @Test(expected = MessageOutputConfigurationException.class)
    public void checkConfigurationFailsIfGraylog2UrlIsInvalid() throws MessageOutputConfigurationException {
        new SlackMessageOutput(null, validConfigurationWithValue("graylog2_url", "Definitely$$Not#A!!URL"),
                Engine.createEngine());
    }

    @Test(expected = MessageOutputConfigurationException.class)
    public void checkConfigurationFailsIfGraylog2UrlIsNotHttpOrHttps() throws MessageOutputConfigurationException {
        new SlackMessageOutput(null, validConfigurationWithValue("graylog2_url", "ftp://example.net"),
                Engine.createEngine());
    }

    @Test(expected = MessageOutputConfigurationException.class)
    public void checkConfigurationFailsIfProxyAddressIsInvalid() throws MessageOutputConfigurationException {
        new SlackMessageOutput(null, validConfigurationWithValue("proxy_address", "Definitely$$Not#A!!URL"),
                Engine.createEngine());
    }

    @Test(expected = MessageOutputConfigurationException.class)
    public void checkConfigurationFailsIfProxyAddressIsMissingAPort() throws MessageOutputConfigurationException {
        new SlackMessageOutput(null, validConfigurationWithValue("proxy_address", "127.0.0.1"),
                Engine.createEngine());
    }

    @Test(expected = MessageOutputConfigurationException.class)
    public void checkConfigurationFailsIfProxyAddressHasWrongFormat() throws MessageOutputConfigurationException {
        new SlackMessageOutput(null, validConfigurationWithValue("proxy_address", "vpn://127.0.0.1"),
                Engine.createEngine());
    }

    private Configuration validConfigurationWithout(final String key) {
        return new Configuration(Maps.filterEntries(VALID_CONFIG_SOURCE, new Predicate<Map.Entry<String, Object>>() {
            @Override
            public boolean apply(Map.Entry<String, Object> input) {
                return key.equals(input.getKey());
            }
        }));
    }

    private Configuration validConfigurationWithValue(String key, String value) {
        Map<String, Object> confCopy = Maps.newHashMap(VALID_CONFIG_SOURCE);
        confCopy.put(key, value);

        return new Configuration(confCopy);
    }

}
