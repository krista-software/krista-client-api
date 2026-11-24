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
import app.krista.ksdk.message.MessageAnswer.SubmitType;
import app.krista.model.field.NamedValuedField;

public class MessageDto {

    public String messageId;

    public String userMessage;

    public long creationTime;

    public String flowId;

    public List<String> paths;

    public List<String> sideConversations;

    public List<NamedValuedField> fields;

    public SubmitType submitType;

    public String submitValue;

    public String authenticationMethod;

    public Map<String, Object> authenticationMeta;

    public MessageDto(String messageId, String userMessage, long creationTime) {
        this.messageId = messageId;
        this.userMessage = userMessage;
        this.creationTime = creationTime;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    public void setPaths(List<String> paths) {
        this.paths = paths;
    }

    public void setSideConversations(List<String> sideConversations) {
        this.sideConversations = sideConversations;
    }

    public void setSubmitValue(String submitValue) {
        this.submitValue = submitValue;
    }

    public void setSubmitType(SubmitType submitType) {
        this.submitType = submitType;
    }

    public void setFields(List<NamedValuedField> fields) {
        this.fields = fields;
    }

    public void setAuthenticationMethod(String authenticationMethod) {
        this.authenticationMethod = authenticationMethod;
    }

    public void setAuthenticationMeta(Map<String, Object> authenticationMeta) {
        this.authenticationMeta = authenticationMeta;
    }

}
