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

import java.util.Objects;
import java.util.function.Predicate;
import app.krista.ksdk.workspace.ConversationExecution;

public class ConversationExecutionMatcher implements Predicate<ConversationExecution> {

    private final String topicId;
    private final String conversationId;

    public ConversationExecutionMatcher(String topicId, String conversationId) {
        this.topicId = topicId;
        this.conversationId = conversationId;
    }

    @Override
    public boolean test(ConversationExecution execution) {
        if (execution == null) {
            return false;
        }
        if (conversationId != null) {
            if (execution.getConversation() == null) {
                return false;
            }
            return Objects.equals(conversationId, execution.getConversation().getConversationId());
        }
        if (topicId != null) {
            if (execution.getConversation() == null || execution.getConversation().getTopic() == null) {
                return false;
            }
            return Objects.equals(topicId, execution.getConversation().getTopic().getTopicId());
        }
        return true;
    }

}
