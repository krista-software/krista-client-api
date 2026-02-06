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

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import app.krista.extensions.krista.sdk.client_api.api.MessageAnswerDto;
import app.krista.extensions.krista.sdk.client_api.api.MessageDto;
import app.krista.extensions.krista.sdk.client_api.api.MessageDtoFactory;
import app.krista.ksdk.message.Message;
import app.krista.ksdk.message.MessageAnswer;
import app.krista.ksdk.message.MessageProcessor;

public class ConversationExecutionHelper {

    private final WorkspaceHelper workspace;

    public ConversationExecutionHelper(WorkspaceHelper workspaceHelper) {
        this.workspace = workspaceHelper;
    }

    public MessageDto getMessage(String executionId, String messageId) throws NoSuchElementException {
        return getMessages(executionId, messageId, 1).stream().findFirst().get();
    }

    public List<MessageDto> getMessages(String executionId, String offsetId, int limit) {

        final int messageCount = getMessageCount(executionId, offsetId);
        // if limit is not provided send all message.
        limit = limit > 0 ? Math.min(limit, messageCount) : messageCount;
        MessageProcessor messageProcessor = getMessageProcessor(executionId);

        if (offsetId != null) {
            Iterable<Message> messagesSince = messageProcessor.getMessagesSince(offsetId);
            return StreamSupport.stream(messagesSince.spliterator(), false)
                    .limit(limit)
                    .map(MessageDtoFactory::create)
                    .collect(Collectors.toList());
        } else {
            // if no offset is there provide message from
            Iterable<Message> messages = messageProcessor.getMessages();
            return StreamSupport.stream(messages.spliterator(), false)
                    .skip(messageCount - limit).limit(limit)
                    .map(MessageDtoFactory::create)
                    .collect(Collectors.toList());
        }
    }

    public int getMessageCount(String executionId, String offsetId) {
        MessageProcessor messageProcessor = getMessageProcessor(executionId);
        if (offsetId != null) {
            return messageProcessor.getTotalMessagesSince(offsetId);
        } else {
            return messageProcessor.getTotalMessages();
        }
    }

    public void submitMessage(String executionId, String messageId, MessageAnswerDto messageAnswerDto) {
        MessageProcessor messageProcessor = getMessageProcessor(executionId);
        MessageAnswer messageAnswer = messageAnswerDto.toMessageAnswer(messageId);
        messageProcessor.submitAnswer(messageAnswer);
    }

    private MessageProcessor getMessageProcessor(String executionId) {
        return workspace.getWorkspace().getConversationExecution(executionId).getProcessor(MessageProcessor.class);
    }

}
