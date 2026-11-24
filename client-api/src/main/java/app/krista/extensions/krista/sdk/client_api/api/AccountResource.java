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
import app.krista.extension.authorization.AuthorizationException;
import app.krista.extensions.krista.sdk.client_api.ClientApiConstants;
import app.krista.extensions.krista.sdk.client_api.util.matchers.AccountMatcher;
import app.krista.ksdk.accounts.AccountManager;
import app.krista.ksdk.accounts.AccountProvider;
import com.kristasoft.common.io.Pages;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("accounts")
public final class AccountResource {

    private final AccountProvider accountProvider;
    private final AccountManager accountManager;

    @Inject
    public AccountResource(AccountProvider accountProvider, AccountManager accountManager) {
        this.accountProvider = accountProvider;
        this.accountManager = accountManager;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Get list of all accounts.",
            description = "Search account by name, or email address.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON,
                                    array = @ArraySchema(schema = @Schema(implementation = AccountDto.class)))),
                    @ApiResponse(responseCode = "401",
                            description = ClientApiConstants.ERR_UNAUTHORIZED,
                            content = @Content(mediaType = MediaType.APPLICATION_JSON))
            }
    )
    public List<AccountDto> getAccounts(
            @QueryParam("pageIndex") @DefaultValue(ClientApiConstants.DEFAULT_PAGE_INDEX) int pageIndex,
            @QueryParam("pageSize") @DefaultValue(ClientApiConstants.DEFAULT_PAGE_SIZE) @Max(ClientApiConstants.MAX_PAGE_SIZE) int pageSize,
            @QueryParam("search") String search)
            throws AuthorizationException {
        int offSet = pageIndex * pageSize;
        return Pages.streamPage(accountManager.getAccounts(offSet, pageSize).iterator(), pageIndex, pageSize,
                        AccountMatcher.create(search))
                .map(AccountDto::create)
                .collect(Collectors.toList());
    }

    @GET
    @Path("{accountId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Find account by id",
            description = "",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON,
                                    schema = @Schema(implementation = AccountDto.class))),
                    @ApiResponse(responseCode = "401",
                            description = ClientApiConstants.ERR_UNAUTHORIZED,
                            content = @Content(mediaType = MediaType.APPLICATION_JSON)),
                    @ApiResponse(responseCode = "404",
                            description = "User with given id not found.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON))
            }
    )
    public AccountDto getAccount(@PathParam("accountId") String accountId)
            throws NoSuchElementException, AuthorizationException {
        try {
            return AccountDto.create(accountProvider.getAccount(accountId));
        } catch (NoSuchElementException cause) {
            throw new NotFoundException(String.format(ClientApiConstants.ERR_ARG_ACCOUNT_NOT_FOUND, accountId), cause);
        }
    }

}
