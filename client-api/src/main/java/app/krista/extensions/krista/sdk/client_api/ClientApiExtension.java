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
import java.util.concurrent.ExecutionException;
import javax.inject.Inject;
import javax.inject.Named;
import app.krista.extension.authorization.RequestAuthenticator;
import app.krista.extension.executor.Invoker;
import app.krista.extension.impl.anno.*;
import app.krista.extension.request.ProtoRequest;
import app.krista.extension.request.ProtoResponse;
import app.krista.extension.request.protos.http.HttpProtocol;

@Java(version = Java.Version.JAVA_21)
@Extension(
        implementingDomainIds = "catEntryDomain_c2394eb0-02d9-4916-a3dc-909b1c631ea0",
        requireWorkspaceAdminRights = true,
        version="3.5.8",
        name="Client API")
@Dependency(name = "Authentication", domainId = "catEntryDomain_db053e8f-a194-4dde-aa6f-701ef7a6b3a7",
        description = "Extension dependency for authentication.")
@Domain(id = "catEntryDomain_db053e8f-a194-4dde-aa6f-701ef7a6b3a7",
        name = "Authentication",
        ecosystemId = "catEntryEcosystem_d3b05047-07b0-4b06-95a3-9fb8f7f608d9",
        ecosystemName = "Krista",
        ecosystemVersion = "279b35ac-aeee-4136-840d-6884f52d0737")
@Domain(id = "catEntryDomain_c2394eb0-02d9-4916-a3dc-909b1c631ea0",
        name = "SDK",
        ecosystemId = "catEntryEcosystem_d3b05047-07b0-4b06-95a3-9fb8f7f608d9",
        ecosystemName = "Krista",
        ecosystemVersion = "41d20d96-9154-4f8e-a3ad-a435674fba5d")
@StaticResource(path = "docs", file = "docs")
public final class ClientApiExtension {

    private final Invoker invoker;
    private final Invoker authenticatorInvoker;
    private final RequestAuthenticator requestAuthenticator;

    @Inject
    public ClientApiExtension(@Named("self") Invoker invoker,
            @Named("Authentication") Invoker authenticatorInvoker,
            ClientApiRequestAuthenticator requestAuthenticator) {
        this.invoker = invoker;
        this.authenticatorInvoker = authenticatorInvoker;
        this.requestAuthenticator = requestAuthenticator;
    }

    @InvokerRequest(InvokerRequest.Type.AUTHENTICATOR)
    public RequestAuthenticator getRequestAuthenticator() {
        return requestAuthenticator;
    }

    @ApiRequest(pathElement = "authn", protocol = HttpProtocol.PROTOCOL_NAME)
    public ProtoResponse forward(ProtoRequest request) throws ExecutionException, InterruptedException {
        return authenticatorInvoker.handleApiRequest("authn", request).get();
    }

    @InvokerRequest(InvokerRequest.Type.CUSTOM_TABS)
    public Map<String, String> getCustomTabs() {
        return Map.of("Documentation", "rest/docs/");
    }

    @InvokerRequest(InvokerRequest.Type.VALIDATE_ATTRIBUTES)
    public void validAttributes(Map<String, String> attributes) {
        String authentication = invoker.getBindings().get("Authentication");
        if (authentication == null) {
            throw new IllegalStateException("Authentication dependency is not mapped yet");
        }
    }

}
