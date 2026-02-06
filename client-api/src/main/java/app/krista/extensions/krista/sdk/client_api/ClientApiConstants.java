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

final public class ClientApiConstants {

    public static final String DEFAULT_PAGE_INDEX = "0";
    public static final String DEFAULT_PAGE_SIZE = "20";
    public static final int MAX_PAGE_SIZE = 50;

    public static final String ACCOUNT_DTO = "Account";
    public static final String TOPIC_DTO = "Topic";
    public static final String CONVERSATION_INFO_DTO = "ConversationInfo";
    public static final String ROLE_DTO = "Role";
    public static final String PERSON_DTO = "Person";
    public static final String CONVERSATION_EXECUTION_DTO = "ConversationExecution";
    public static final String MESSAGE_SUBMIT_DTO = "MessageSubmit";

    public static final String ERR_ARG_CONVERSATION_NOT_FOUND = "Conversation with id: %s not found.";
    public static final String ERR_ARG_EXECUTION_NOT_FOUND = "Execution with id: %s not found.";
    public static final String ERR_ARG_TOPIC_NOT_FOUND = "Topic with id: %s not found.";
    public static final String ERR_ARG_ACCOUNT_NOT_FOUND = "Account with id: %s not found.";
    public static final String ERR_UNAUTHORIZED = "User not authorized.";
    public static final String ERR_SOMETHING_WENT_WRONG = "Something went wrong, Please contact admin for help.";
    public static final String ERR_BAD_REQUEST = "Bad Request.";

    public static final String TOPIC_NOT_FOUND = "Topic with given id not found.";
    public static final String EXECUTION_NOT_FOUND = "Execution with given id not found.";
    public static final String CONVERSATION_NOT_FOUND = "Conversation with given id not found.";
    public static final String NOT_FOUND = "Couldn’t find resource.";

}
