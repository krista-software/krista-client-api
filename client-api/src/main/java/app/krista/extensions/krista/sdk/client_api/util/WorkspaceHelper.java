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

package app.krista.extensions.krista.sdk.client_api.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.StreamSupport;
import app.krista.extensions.krista.sdk.client_api.util.matchers.ConversationExecutionMatcher;
import app.krista.ksdk.workspace.Conversation;
import app.krista.ksdk.workspace.ConversationExecution;
import app.krista.ksdk.workspace.Topic;
import app.krista.ksdk.workspace.Workspace;
import com.kristasoft.common.iterators.FilteringIterator;
import com.kristasoft.common.iterators.FlatteningIterator;
import com.kristasoft.common.iterators.Iterators;

public class WorkspaceHelper {

    private final Workspace workspace;

    public WorkspaceHelper(Workspace workspace) {
        this.workspace = workspace;
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public Conversation getConversation(String conversationId) {
        Iterable<Conversation> conversations = this::getConversations;
        return StreamSupport.stream(conversations.spliterator(), false)
                .filter(c -> Objects.equals(c.getConversationId(), conversationId))
                .findFirst().orElseThrow(NoSuchElementException::new);
    }

    public Iterator<Conversation> getConversations() {
        return getConversations(null);
    }

    public Iterator<Conversation> getConversations(String topicId) {
        Iterable<Topic> topics;
        if (topicId != null) {
            topics = () -> Iterators.iteratorOf(workspace.getTopic(topicId));
        } else {
            topics = workspace.getTopics();
        }
        return FlatteningIterator.create(
                Iterators.transform(topics.iterator(), topic -> topic.getConversations().iterator()));
    }

    public Iterator<ConversationExecution> getExecutions(String topicId, String conversationId) {
        return new FilteringIterator<>(workspace.getConversationExecutions().iterator(),
                new ConversationExecutionMatcher(topicId, conversationId));
    }

}
