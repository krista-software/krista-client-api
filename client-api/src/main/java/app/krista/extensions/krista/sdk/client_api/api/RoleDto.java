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
import java.util.Set;
import java.util.stream.Collectors;
import app.krista.extensions.krista.sdk.client_api.ClientApiConstants;
import app.krista.ksdk.authorization.Role;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = ClientApiConstants.ROLE_DTO)
public final class RoleDto {

    public String id;

    public String name;

    public RoleDto(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public static RoleDto create(Role role) {
        return new RoleDto(role.getRoleId(), role.getName());
    }

    public static List<RoleDto> createList(Set<Role> roles) {
        return roles.stream().map(RoleDto::create).collect(Collectors.toList());
    }

}
