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

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class Either<S, F> {

    public static <S, F> Either<S, F> failure(F failure) {
        return new Either.Failure<>(failure);
    }

    public static <S, F> Either<S, F> success(S success) {
        return new Either.Success<>(success);
    }

    public abstract boolean isSuccess();
    public abstract S get();
    public abstract boolean isFailure();
    public abstract F getFailValue();

    public <V> Either<V, F> map(Function<S, V> mapper){
        Objects.requireNonNull(mapper, "mapper is null");
        if(isSuccess()){
            return success(mapper.apply(get()));
        } else {
            return (Either<V, F>) this;
        }
    }

    public S getOrElse(S alternative) {
        if(isSuccess()){
            return get();
        } else {
            return alternative;
        }
    }

    public S getOrElseGet(Supplier<S> alternative) {
        if(isSuccess()){
            return get();
        } else {
            return alternative.get();
        }
    }

    public <X extends Throwable> S getOrElseThrow(Supplier<X> o) throws X {
        if(isSuccess()){
            return get();
        } else {
            throw o.get();
        }
    }

    private static class Success<S, F> extends Either<S, F> {
        private final S value;

        public Success(S success) {
            this.value = success;
        }

        @Override
        public boolean isFailure() {
            return false;
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public S get() {
            return value;
        }

        @Override
        public F getFailValue() {
            throw new NoSuchElementException("No failure value on success state");
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {return true;}
            if (!(o instanceof Success)) {return false;}
            Success<?, ?> success1 = (Success<?, ?>) o;
            return Objects.equals(value, success1.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }

    private static final class Failure<S, F> extends Either<S, F>{

        private final F value;

        public Failure(F value){
            this.value = value;
        }

        @Override
        public boolean isFailure() {
            return true;
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public S get() {
            throw new NoSuchElementException("No successful value on failure state");
        }
        @Override
        public F getFailValue() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {return true;}
            if (!(o instanceof Failure)) {return false;}
            Failure<?, ?> failure = (Failure<?, ?>) o;
            return Objects.equals(value, failure.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }
}
