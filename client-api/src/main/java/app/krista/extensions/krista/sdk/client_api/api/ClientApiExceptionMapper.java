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

import java.util.NoSuchElementException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import app.krista.extension.authorization.AuthorizationException;
import app.krista.extensions.krista.sdk.client_api.ClientApiConstants;

public final class ClientApiExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable cause) {
        // handle NoSuchElement -> 404
        cause.printStackTrace(System.err);
        final String message = cause.getMessage();
        if (cause instanceof AuthorizationException) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(message == null ? ClientApiConstants.ERR_UNAUTHORIZED : message).build();
        } else if (cause instanceof NoSuchElementException || cause instanceof NotFoundException) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(message == null ? ClientApiConstants.NOT_FOUND : message).build();
        } else if (cause instanceof IllegalArgumentException || cause instanceof IllegalStateException) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(message == null ? ClientApiConstants.ERR_BAD_REQUEST : message).build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(message == null ? ClientApiConstants.ERR_SOMETHING_WENT_WRONG : message).build();
    }

}
