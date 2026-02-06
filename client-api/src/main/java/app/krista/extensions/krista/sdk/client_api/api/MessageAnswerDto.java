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

import java.util.Map;
import app.krista.extensions.krista.sdk.client_api.ClientApiConstants;
import app.krista.ksdk.message.MessageAnswer;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = ClientApiConstants.MESSAGE_SUBMIT_DTO)
public class MessageAnswerDto {

    public Map<String, Object> values;

    @Schema(allowableValues = {"SUBMIT, PATH, SIDEBAR"})
    public String submitType;

    public String submitValue;

    public MessageAnswerDto() {

    }

    @Override
    public String toString() {
        return "MessageAnswerDto{" +
                "values=" + values +
                ", submitType=" + submitType +
                ", submitValue='" + submitValue +
                '}';
    }

    public MessageAnswer toMessageAnswer(String messageId) {
        MessageAnswer.SubmitType type;
        switch (submitType.toLowerCase()) {
            case "submit":
                type = MessageAnswer.SubmitType.SUBMIT;
                break;
            case "path":
                type = MessageAnswer.SubmitType.PATH;
                break;
            case "sidebar":
                type = MessageAnswer.SubmitType.SIDEBAR;
                break;
            default:
                throw new IllegalArgumentException("Submit type is invalid");
        }
        return MessageAnswer.create(messageId, values != null ? values : Map.of(), type, submitValue);
    }

}

