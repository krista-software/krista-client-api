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

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.validation.constraints.Max;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import app.krista.extensions.krista.sdk.client_api.ClientApiConstants;
import app.krista.extensions.krista.sdk.client_api.util.WorkspaceHelper;
import app.krista.extensions.krista.sdk.client_api.util.matchers.ConversationMatcher;
import app.krista.ksdk.workspace.Workspace;
import com.kristasoft.common.io.Pages;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("conversations")
public final class ConversationResource {

    private final WorkspaceHelper workspace;
    private final String topicId;

    @Inject
    public ConversationResource(Workspace workspace) {
        this(new WorkspaceHelper(workspace), null);
    }

    public ConversationResource(WorkspaceHelper workspace, String topicId) {
        this.workspace = workspace;
        this.topicId = topicId;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Get all conversation",
            description = "",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON,
                                    array = @ArraySchema(schema = @Schema(implementation = TopicDto.class)))),
                    @ApiResponse(responseCode = "401",
                            description = ClientApiConstants.ERR_UNAUTHORIZED,
                            content = @Content(mediaType = MediaType.APPLICATION_JSON))
            }
    )
    public List<ConversationInfoDto> getConversations(
            @QueryParam("pageIndex") @DefaultValue(ClientApiConstants.DEFAULT_PAGE_INDEX) int pageIndex,
            @QueryParam("pageSize") @DefaultValue(ClientApiConstants.DEFAULT_PAGE_SIZE) @Max(ClientApiConstants.MAX_PAGE_SIZE) int pageSize,
            @QueryParam("search") String search) {
        return Pages.streamPage(workspace.getConversations(topicId), pageIndex, pageSize,
                        ConversationMatcher.create(search))
                .map(ConversationInfoDto::create)
                .collect(Collectors.toList());
    }

    @GET
    @Path("{conversationId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Find conversation by id",
            description = "",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON,
                                    schema = @Schema(implementation = ConversationInfoDto.class))),
                    @ApiResponse(responseCode = "401",
                            description = ClientApiConstants.ERR_UNAUTHORIZED,
                            content = @Content(mediaType = MediaType.APPLICATION_JSON)),
                    @ApiResponse(
                            responseCode = "404",
                            description = ClientApiConstants.CONVERSATION_NOT_FOUND,
                            content = @Content(mediaType = MediaType.APPLICATION_JSON))
            }
    )
    public ConversationInfoDto getConversation(@PathParam("conversationId") String conversationId) {
        try {
            return ConversationInfoDto.create(workspace.getConversation(conversationId));
        } catch (NoSuchElementException cause) {
            throw new NotFoundException(
                    String.format(ClientApiConstants.ERR_ARG_CONVERSATION_NOT_FOUND, conversationId),
                    cause);
        }
    }

    @Path("{conversationId}/executions")
    public ConversationExecutionResource handleExecutions(@PathParam("conversationId") String conversationId) {
        return new ConversationExecutionResource(workspace, conversationId, topicId);
    }

}
