/**
 * The MIT License
 *
 * Copyright for portions of unirest-java are held by Kong Inc (c) 2013.
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package kong.unirest.core;

import org.assertj.core.api.AbstractAssert;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static kong.unirest.core.EitherTest.EitherAsserts.assertEither;
import static org.junit.jupiter.api.Assertions.*;

class EitherTest {

    @Test
    void success() {
        assertEither(Either.success(42))
                .isSuccess()
                .valueIs(42)
                .failureThrows();
    }

    @Test
    void mapSuccess() {
        assertEither(Either.success(42).map(String::valueOf))
                .isSuccess()
                .valueIs("42")
                .failureThrows();;
    }

    @Test
    void equalities() {
        assertEither(Either.success(42)).isEqualTo(Either.success(42));
        assertEither(Either.failure(42)).isEqualTo(Either.failure(42));
        assertEither(Either.success(42)).isNotEqualTo(Either.success(-1));
        assertEither(Either.failure(42)).isNotEqualTo(Either.failure(-1));
        assertEither(Either.success(42)).isNotEqualTo(Either.failure(42));
    }

    @Test
    void getOrElse() {
        assertEquals(42, Either.success(42).getOrElse(-3));
        assertEquals(-3, Either.failure(42).getOrElse(-3));
        assertNull(Either.failure(42).getOrElse(null));
    }

    @Test
    void getOrElseFunction() {
        assertEquals(42, Either.success(42).getOrElseGet(() -> -3));
        assertEquals(-3, Either.failure(42).getOrElseGet(() -> -3));
    }

    @Test
    void getOrElseThrow() {
        assertDoesNotThrow(() -> Either.success(42).getOrElseThrow(() -> new TestException()));
        assertThrows(TestException.class, () -> Either.failure(42).getOrElseThrow(() -> new TestException()));
    }

    @Test
    void failure() {
        assertEither(Either.failure(42))
                .isFailure()
                .failValueIs(42)
                .successThrows();
    }


    public static class EitherAsserts<S, F> extends AbstractAssert<EitherAsserts<S, F>, Either<S, F>>{

        protected EitherAsserts(Either<S, F> either, Class<?> selfType) {
            super(either, selfType);
        }

        public static <S, F> EitherAsserts<S, F> assertEither(Either<S, F> either){
            return new EitherAsserts<>(either, EitherAsserts.class);
        }

        public EitherAsserts<S, F> isFailure() {
            assertFalse(actual.isSuccess(),  "isSuccess() returned true. Expected false");
            assertTrue(actual.isFailure(),   "isFailure() returned false. Expected true");
            return this;
        }

        public EitherAsserts<S, F> isSuccess() {
            assertTrue(actual.isSuccess(),  "isSuccess() returned false. Expected true");
            assertFalse(actual.isFailure(), "isFailure() returned true. Expected false");
            return this;
        }

        public EitherAsserts<S, F> valueIs(Object value) {
            assertEquals(value, actual.get(), "Incorrect expected success value");
            return this;
        }

        public EitherAsserts<S, F> failValueIs(Object value) {
            assertEquals(value, actual.getFailValue(), "Incorrect expected failure value");
            return this;
        }

        public EitherAsserts<S, F> successThrows() {
            var ex = assertThrows(NoSuchElementException.class, actual::get);
            assertEquals("No successful value on failure state", ex.getMessage());
            return this;
        }

        public EitherAsserts<S, F>  failureThrows() {
            var ex = assertThrows(NoSuchElementException.class, actual::getFailValue);
            assertEquals("No failure value on success state", ex.getMessage());
            return this;
        }
    }

    public static final class TestException extends RuntimeException {}

}