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

package app.krista.extensions.krista.sdk.client_api.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StringMatcherTest {

    @Test
    @DisplayName("create(null) yields matcher that always matches")
    void createWithNullSearchCriteriaAlwaysMatches() {
        StringMatcher matcher = StringMatcher.create(null);

        assertTrue(matcher.test("any"));
        assertTrue(matcher.test(""));
        assertTrue(matcher.test(null));
    }

    @Test
    @DisplayName("create(\"  \") yields matcher that always matches")
    void createWithBlankSearchCriteriaAlwaysMatches() {
        StringMatcher matcher = StringMatcher.create("   ");

        assertTrue(matcher.test("value"));
        assertTrue(matcher.test("another"));
    }

    @Test
    @DisplayName("matcher is case insensitive and returns false for null input")
    void matcherIsCaseInsensitiveAndHandlesNullInput() {
        StringMatcher matcher = StringMatcher.create("TeSt");

        assertTrue(matcher.test("this is a test value"));
        assertTrue(matcher.test("TEST"));
        assertTrue(matcher.test("pre-test-post"));

        assertFalse(matcher.test("no match here"));
        assertFalse(matcher.test(null));
    }
}
