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

package app.krista.extensions.krista.sdk.client_api;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Inject;
import javax.inject.Named;
import app.krista.extension.authorization.MustAuthenticateException;
import app.krista.extension.authorization.MustAuthorizeException;
import app.krista.extension.authorization.RequestAuthenticator;
import app.krista.extension.executor.Invoker;
import app.krista.extension.request.ProtoRequest;
import app.krista.extension.request.ProtoResponse;
import app.krista.extension.request.protos.http.HttpRequest;
import app.krista.extension.request.protos.http.HttpResponse;
import app.krista.extension.request.protos.http.HttpResponses;
import app.krista.extension.request.protos.ws.WsConnection;
import app.krista.extension.request.protos.ws.WsConnectionUpgradeRequest;
import app.krista.extension.request.protos.ws.WsEvent;
import app.krista.model.field.NamedField;
import org.jvnet.hk2.annotations.Contract;
import org.jvnet.hk2.annotations.Service;

@Service
@Contract
public class ClientApiRequestAuthenticator implements RequestAuthenticator {

    private final Map<String, String> connectionAuthorizations = new ConcurrentHashMap<>();

    private final RequestAuthenticator requestAuthenticator;

    @Inject
    public ClientApiRequestAuthenticator(@Named("Authentication") Invoker authenticationInvoker) {
        this(authenticationInvoker.getRequestAuthenticator());
    }

    public ClientApiRequestAuthenticator(RequestAuthenticator requestAuthenticator) {
        this.requestAuthenticator = requestAuthenticator;
    }

    @Override
    public String getScheme() {
        return requestAuthenticator.getScheme();
    }

    @Override
    public Set<String> getSupportedProtocols() {
        return requestAuthenticator.getSupportedProtocols();
    }

    @Override
    public String getAuthenticatedAccountId(ProtoRequest protoRequest) {
        try {
            String accountId;
            if (protoRequest instanceof HttpRequest) {
                HttpRequest httpRequest = (HttpRequest) protoRequest;
                httpRequest.bufferBody();
                accountId = requestAuthenticator.getAuthenticatedAccountId(httpRequest);
            } else if (protoRequest instanceof WsEvent) {
                WsEvent event = (WsEvent) protoRequest;
                accountId = connectionAuthorizations.get(event.getConnection().getConnectionId());
            } else {
                accountId = null;
            }
            return accountId;
        } catch (Exception cause) {
            throw new IllegalStateException("Failed to authenticate : Reason: " + cause.getMessage(), cause);
        }
    }

    @Override
    public boolean setServiceAuthorization(String s) {
        return requestAuthenticator.setServiceAuthorization(s);
    }

    @Override
    public Map<String, NamedField> getAttributeFields() {
        return requestAuthenticator.getAttributeFields();
    }

    @Override
    public ProtoResponse getMustAuthenticateResponse(MustAuthenticateException e, ProtoRequest protoRequest) {
        return requestAuthenticator.getMustAuthenticateResponse(e, protoRequest);
    }

    @Override
    public AuthorizationResponse getMustAuthenticateResponse(MustAuthenticateException e) {
        return requestAuthenticator.getMustAuthenticateResponse(e);
    }

    @Override
    public ProtoResponse getMustAuthorizeResponse(MustAuthorizeException e, ProtoRequest protoRequest) {
        return requestAuthenticator.getMustAuthorizeResponse(e, protoRequest);
    }

    @Override
    public AuthorizationResponse getMustAuthorizeResponse(MustAuthorizeException e) {
        return requestAuthenticator.getMustAuthorizeResponse(e);
    }

    public void acceptWsConnection(WsConnectionUpgradeRequest upgrade) {
        WsConnection connection = upgrade.getConnection();
        try {
            HttpRequest httpRequest = upgrade.getHttpRequest();
            String authenticatedAccountId = getAuthenticatedAccountId(httpRequest);
            if (authenticatedAccountId == null) {
                connection.reject(
                        (HttpResponse) getMustAuthenticateResponse(new MustAuthenticateException(), httpRequest));
            } else {
                connectionAuthorizations.put(connection.getConnectionId(), authenticatedAccountId);
                connection.accept();
            }
        } catch (RuntimeException cause) {
            System.err.println("Failed to authenticate: " + cause.getMessage());
            cause.printStackTrace(System.err);
            connection.reject(HttpResponses.createServerError(cause));
        }
    }

}
