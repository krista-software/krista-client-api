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
import java.util.Map;
import java.util.Objects;
import app.krista.extensions.krista.sdk.client_api.ClientApiConstants;
import app.krista.ksdk.accounts.Account;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = ClientApiConstants.ACCOUNT_DTO)
public final class AccountDto {

    public String accountId;

    public String primaryEmailAddress;

    public String inboxId;

    public PersonDto person;

    public List<RoleDto> roles;

    public Map<String, Object> customAttributes;

    public AccountDto(String accountId, String primaryEmailAddress, String inboxId, PersonDto person,
            List<RoleDto> roles, Map<String, Object> customAttributes) {
        this.accountId = accountId;
        this.primaryEmailAddress = primaryEmailAddress;
        this.inboxId = inboxId;
        this.person = person;
        this.roles = roles;
        this.customAttributes = customAttributes;
    }

    @SuppressWarnings("deprecation")
    public static AccountDto create(Account account) {
        Objects.requireNonNull(account);
        return new AccountDto(
                account.getAccountId(),
                account.getPrimaryEmailAddress(),
                account.getInboxId(),
                account.getPerson() != null ? PersonDto.create(account.getPerson()) : null,
                RoleDto.createList(account.getRoles()),
                account.getCustomAttributes());
    }

}
