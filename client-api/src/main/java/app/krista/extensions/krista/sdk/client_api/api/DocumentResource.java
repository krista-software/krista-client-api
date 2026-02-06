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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("docs")
public class DocumentResource {

    public String DIR_PATH = "/docs/openapi/";

    @GET
    @Path("{subPath:.*}")
    public Response customTabs(@PathParam("subPath") String subPath) throws IOException {
        System.out.println("Subpath: " + subPath);
        if (subPath == null || subPath.trim().isEmpty() || subPath.contains("index.html")) {
            subPath = DIR_PATH + "index.html";
        } else if (!subPath.contains(DIR_PATH)) {
            subPath = DIR_PATH + subPath;
        }
        String mimeType = Files.probeContentType(java.nio.file.Path.of(subPath));
        System.out.println("Subpath return: " + subPath + ", mimeType: " + mimeType);
        InputStream inputStream = getClass().getResourceAsStream(subPath);
        String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        return Response.ok(content).header("Content-type", mimeType)
                .header("Cache-Control", "private, must-revalidate").build();
    }

}
