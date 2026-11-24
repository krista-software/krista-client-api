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

package app.krista.extensions.krista.sdk.client_api.util.matchers;

import java.util.function.Predicate;
import app.krista.extensions.krista.sdk.client_api.util.StringMatcher;
import app.krista.ksdk.accounts.Account;

public class AccountMatcher<T extends Account> implements Predicate<T> {

    private final StringMatcher stringMatcher;

    private AccountMatcher(StringMatcher stringMatcher) {
        this.stringMatcher = stringMatcher;
    }

    public static <T extends Account> AccountMatcher<T> create(String searchCriteria) {
        return new AccountMatcher<>(StringMatcher.create(searchCriteria));
    }

    @Override
    public boolean test(T account) {
        return personNameMatch(account) || primaryEmailAddressMatch(account);
    }

    public boolean personNameMatch(T account) {
        if (account == null || account.getPerson() == null) {
            return false;
        }
        return stringMatcher.test(account.getPerson().getPersonName());
    }

    public boolean primaryEmailAddressMatch(T account) {
        if (account == null) {
            return false;
        }
        return stringMatcher.test(account.getPrimaryEmailAddress());
    }

}
