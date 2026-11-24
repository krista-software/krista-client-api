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

package app.krista.extensions.krista.sdk.client_api.api;

import java.util.Objects;
import app.krista.extensions.krista.sdk.client_api.ClientApiConstants;
import app.krista.ksdk.workspace.Conversation;
import app.krista.ksdk.workspace.ConversationExecution;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = ClientApiConstants.CONVERSATION_EXECUTION_DTO)
public final class ConversationExecutionDto {

    public String executionId;

    public String initiatorId;

    public String initiatorName;

    public ConversationInfoDto conversation;

    public long startTime;

    public ConversationExecutionDto(String executionId, ConversationInfoDto conversation, String initiatorId,
            String initiatorName, long startTime) {
        this.executionId = executionId;
        this.conversation = conversation;
        this.initiatorId = initiatorId;
        this.initiatorName = initiatorName;
        this.startTime = startTime;
    }

    public static ConversationExecutionDto create(ConversationExecution execution) {
        Objects.requireNonNull(execution);
        return new ConversationExecutionDto(
                execution.getExecutionId(),
                ConversationInfoDto.create(execution.getConversation()),
                execution.getInitiatorId(),
                execution.getInitiatorName(),
                execution.getCreationTime());
    }

    public static ConversationExecutionDto create(String executionId, Conversation conversation, String initiatorId,
            String initiatorName, long creationTime) {
        return new ConversationExecutionDto(
                executionId,
                ConversationInfoDto.create(conversation),
                initiatorId,
                initiatorName,
                creationTime);
    }

}
