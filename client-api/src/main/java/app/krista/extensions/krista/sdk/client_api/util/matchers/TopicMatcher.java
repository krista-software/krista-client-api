/*
 * Client Api Extension for Krista
 * Copyright (C) 2025 Krista Software
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>. 
 */

package app.krista.extensions.krista.sdk.client_api.util.matchers;

import java.util.function.Predicate;
import app.krista.extensions.krista.sdk.client_api.util.StringMatcher;
import app.krista.ksdk.workspace.Topic;

public class TopicMatcher implements Predicate<Topic> {

    private final StringMatcher stringMatcher;

    private TopicMatcher(StringMatcher stringMatcher) {
        this.stringMatcher = stringMatcher;
    }

    public static TopicMatcher create(String searchCriteria) {
        return new TopicMatcher(StringMatcher.create(searchCriteria));
    }

    @Override
    public boolean test(Topic topic) {
        return topicNameMatch(topic);
    }

    public boolean topicNameMatch(Topic topic) {
        if (topic == null) {
            return false;
        }
        return stringMatcher.test(topic.getTopicName());
    }

}
