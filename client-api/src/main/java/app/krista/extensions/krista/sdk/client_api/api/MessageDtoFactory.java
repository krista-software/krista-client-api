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

import java.util.ArrayList;
import app.krista.ksdk.message.*;

public class MessageDtoFactory {

    public static MessageDto createFromInformMessage(InformMessage message) {
        MessageDto messageDto = new MessageDto(
                message.getMessageId(),
                message.getRichTextMessage(),
                message.getCreationTime());
        messageDto.setFields(message.getFields() != null ? new ArrayList<>(message.getFields().values()) : null);
        messageDto.setFlowId(message.getFlowId());
        return messageDto;
    }

    public static MessageDto createFromAskMessage(AskMessage message) {
        MessageDto messageDto = createFromInformMessage(message);
        messageDto.setPaths(message.getPaths());
        messageDto.setSideConversations(message.getSidebars());
        return messageDto;
    }

    public static MessageDto createFromAnsweredMessage(AnsweredMessage message) {
        MessageDto messageDto = createFromAskMessage(message);
        messageDto.setSubmitType(message.getSubmitType());
        messageDto.setSubmitValue(message.getSubmitValue());
        return messageDto;
    }

    public static MessageDto createFromAuthMessage(AuthRequest message) {
        MessageDto messageDto = new MessageDto(message.getMessageId(), "", message.getCreationTime());
        messageDto.setAuthenticationMethod(message.getAuthenticationMethod());
        messageDto.setAuthenticationMeta(message.getValues());
        return messageDto;
    }

    public static MessageDto createFromErrorMessage(ErrorMessage message) {
        return new MessageDto("", message.getTextMessage(), 0);
    }

    public static MessageDto create(Message message) {
        if (message instanceof AuthRequest) {
            return createFromAuthMessage((AuthRequest) message);
        } else if (message instanceof AnsweredMessage) {
            return createFromAnsweredMessage((AnsweredMessage) message);
        } else if (message instanceof AskMessage) {
            return createFromAskMessage((AskMessage) message);
        } else if (message instanceof ErrorMessage) {
            return createFromErrorMessage((ErrorMessage) message);
        } else {
            return createFromInformMessage((InformMessage) message);
        }
    }

    public static class UnknownMessageType extends RuntimeException {

        public UnknownMessageType() {
            super("Unknown message Type");
        }

    }

}
