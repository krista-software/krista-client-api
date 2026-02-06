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
import java.util.Objects;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.validation.constraints.Max;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import app.krista.extensions.krista.sdk.client_api.ClientApiConstants;
import app.krista.extensions.krista.sdk.client_api.util.ConversationExecutionHelper;
import app.krista.extensions.krista.sdk.client_api.util.WorkspaceHelper;
import app.krista.ksdk.workspace.ConversationExecution;
import app.krista.ksdk.workspace.Workspace;
import com.kristasoft.common.io.Pages;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("executions")
public final class ConversationExecutionResource {

    private final WorkspaceHelper workspace;
    private final ConversationExecutionHelper executionHelper;
    private final String conversationId;
    private final String topicId;

    @Inject
    public ConversationExecutionResource(Workspace workspace) {
        this(new WorkspaceHelper(workspace), null, null);
    }

    public ConversationExecutionResource(WorkspaceHelper workspace,
            String conversationId, String topicId) {
        this.workspace = workspace;
        this.executionHelper = new ConversationExecutionHelper(workspace);
        this.conversationId = conversationId;
        this.topicId = topicId;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Get list of all running conversation executions.",
            description = "",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "OK",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                                    array = @ArraySchema(schema = @Schema(implementation = ConversationExecutionDto.class)))),
                    @ApiResponse(responseCode = "401",
                            description = ClientApiConstants.ERR_UNAUTHORIZED,
                            content = @Content(mediaType = MediaType.APPLICATION_JSON))
            }
    )
    public List<ConversationExecutionDto> getExecutions(
            @QueryParam("pageIndex") @DefaultValue(ClientApiConstants.DEFAULT_PAGE_INDEX) int pageIndex,
            @QueryParam("pageSize") @DefaultValue(ClientApiConstants.DEFAULT_PAGE_SIZE) @Max(ClientApiConstants.MAX_PAGE_SIZE) int pageSize) {
        return Pages.streamPage(workspace.getExecutions(topicId, conversationId), pageIndex, pageSize)
                .map(ConversationExecutionDto::create)
                .collect(Collectors.toList());
    }

    @GET
    @Path("{executionId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Find conversation execution by Id.",
            description = "",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "OK",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                                    schema = @Schema(implementation = ConversationExecutionDto.class))),
                    @ApiResponse(responseCode = "401",
                            description = ClientApiConstants.ERR_UNAUTHORIZED,
                            content = @Content(mediaType = MediaType.APPLICATION_JSON)),
                    @ApiResponse(responseCode = "404",
                            description = ClientApiConstants.EXECUTION_NOT_FOUND,
                            content = @Content(mediaType = MediaType.APPLICATION_JSON)),
            }
    )
    public ConversationExecutionDto getExecution(@PathParam("executionId") String executionId) {
        try {
            return ConversationExecutionDto.create(workspace.getWorkspace().getConversationExecution(executionId));
        } catch (NoSuchElementException cause) {
            throw new NotFoundException(String.format("Execution with executionId: %s not found.", executionId), cause);
        }
    }

    @DELETE
    @Path("{executionId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Terminate the conversation execution by Id.",
            description = "",
            responses = {
                    @ApiResponse(responseCode = "201",
                            description = "NO CONTENT",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON)),
                    @ApiResponse(responseCode = "401",
                            description = ClientApiConstants.ERR_UNAUTHORIZED,
                            content = @Content(mediaType = MediaType.APPLICATION_JSON)),
                    @ApiResponse(responseCode = "404",
                            description = ClientApiConstants.EXECUTION_NOT_FOUND,
                            content = @Content(mediaType = MediaType.APPLICATION_JSON)),
            }
    )
    public void terminate(@PathParam("executionId") String executionId) {
        try {
            workspace.getWorkspace().getConversationExecution(executionId).terminate();
        } catch (NoSuchElementException cause) {
            throw new NotFoundException(String.format(ClientApiConstants.ERR_ARG_EXECUTION_NOT_FOUND, executionId),
                    cause);
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Execute conversation with provided conversation id.",
            description = "",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                                    schema = @Schema(implementation = ConversationExecutionDto.class))),
                    @ApiResponse(responseCode = "401",
                            description = ClientApiConstants.ERR_UNAUTHORIZED,
                            content = @Content(mediaType = MediaType.APPLICATION_JSON)),
                    @ApiResponse(
                            responseCode = "404",
                            description = ClientApiConstants.CONVERSATION_NOT_FOUND,
                            content = @Content(mediaType = MediaType.APPLICATION_JSON)),
            }
    )
    public ConversationExecutionDto execute() {
        try {
            // Hack until platform creation time crash wont fix for new created conversation
            // @Ravindra @Hiren Soni @Tom Burger.
            ConversationExecution executionInfo = workspace.getConversation(conversationId).execute();
            long creationTime = System.currentTimeMillis();
            try {
                creationTime = executionInfo.getCreationTime();
            } catch (Exception cause) {
                System.out.println("Execution: " + executionInfo.getExecutionId() + ", Error in creation time");
            }
            return ConversationExecutionDto.create(executionInfo.getExecutionId(), executionInfo.getConversation(),
                    executionInfo.getInitiatorId(), executionInfo.getInitiatorName(), creationTime);
        } catch (NoSuchElementException cause) {
            throw new NotFoundException(
                    String.format(ClientApiConstants.ERR_ARG_CONVERSATION_NOT_FOUND, conversationId),
                    cause);
        }
    }

    @GET
    @Path("{executionId}/messages")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "List all message of conversation execution.",
            description = "",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                                    array = @ArraySchema(arraySchema = @Schema(implementation = MessageDto.class)))),
                    @ApiResponse(responseCode = "401",
                            description = ClientApiConstants.ERR_UNAUTHORIZED,
                            content = @Content(mediaType = MediaType.APPLICATION_JSON)),
                    @ApiResponse(
                            responseCode = "404",
                            description = ClientApiConstants.EXECUTION_NOT_FOUND,
                            content = @Content(mediaType = MediaType.APPLICATION_JSON)),
            }
    )
    public List<MessageDto> getMessages(
            @PathParam("executionId") String executionId,
            @QueryParam("offsetId") String offsetId,
            @QueryParam("limit") int limit) {
        try {
            return this.executionHelper.getMessages(executionId, offsetId, limit);
        } catch (NoSuchElementException cause) {
            throw new NotFoundException(
                    String.format(ClientApiConstants.ERR_ARG_CONVERSATION_NOT_FOUND, conversationId),
                    cause);
        } catch (MessageDtoFactory.UnknownMessageType cause) {
            return null;
        }
    }

    @GET
    @Path("{executionId}/messages/{messageId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Find message inside",
            description = "",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                                    schema = @Schema(implementation = MessageDto.class))),
                    @ApiResponse(responseCode = "401",
                            description = ClientApiConstants.ERR_UNAUTHORIZED,
                            content = @Content(mediaType = MediaType.APPLICATION_JSON)),
                    @ApiResponse(
                            responseCode = "404",
                            description = ClientApiConstants.EXECUTION_NOT_FOUND,
                            content = @Content(mediaType = MediaType.APPLICATION_JSON)),
            }
    )
    public MessageDto getMessage(@PathParam("executionId") String executionId,
            @PathParam("messageId") String messageId) {
        try {
            return executionHelper.getMessage(executionId, messageId);
        } catch (NoSuchElementException cause) {
            throw new NotFoundException(
                    String.format(ClientApiConstants.ERR_ARG_EXECUTION_NOT_FOUND, executionId),
                    cause);
        }
    }

    @PUT
    @Path("{executionId}/messages/{messageId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Submit message using message id.",
            description = "Submit current step with the data provided by user.",
            responses = {
                    @ApiResponse(
                            responseCode = "202",
                            description = "Accepted"),
                    @ApiResponse(responseCode = "401",
                            description = ClientApiConstants.ERR_UNAUTHORIZED,
                            content = @Content(mediaType = MediaType.APPLICATION_JSON)),
                    @ApiResponse(
                            responseCode = "404",
                            description = ClientApiConstants.EXECUTION_NOT_FOUND,
                            content = @Content(mediaType = MediaType.APPLICATION_JSON)),
            }
    )
    public Response submitMessage(
            @PathParam("executionId") String executionId,
            @PathParam("messageId") String messageId,
            @RequestBody(required = true, content = @Content(schema = @Schema(implementation = MessageAnswerDto.class)))
            MessageAnswerDto messageAnswerDto) {
        try {
            Objects.requireNonNull(messageAnswerDto);
            executionHelper.submitMessage(executionId, messageId, messageAnswerDto);
            return Response.accepted().build();
        } catch (NoSuchElementException cause) {
            throw new NotFoundException(
                    String.format(ClientApiConstants.ERR_ARG_EXECUTION_NOT_FOUND, executionId),
                    cause);
        }
    }

}
