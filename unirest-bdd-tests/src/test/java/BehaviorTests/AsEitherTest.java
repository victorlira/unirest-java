package BehaviorTests;

import kong.unirest.core.Unirest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AsEitherTest extends BddTest {

    @Test
    void success() {
        Unirest.get(MockServer.GET)
                .header("foo", "bar")
                .asEither(RequestCapture.class, Foo.class)
                .getBody()
                .get()
                .assertHeader("foo", "bar");
    }

    @Test
    void failure() {
        MockServer.setJsonAsResponse(new Foo("I'm a error"));
        var value = Unirest.get(MockServer.ERROR_RESPONSE)
                .header("foo", "bar")
                .asEither(RequestCapture.class, Foo.class)
                .getBody()
                .getFailValue()
                .getBar();

        assertEquals("I'm a error", value);
    }

    @Test
    void failureToParse() {
        MockServer.setStringResponse("not json");
        var value = Unirest.get(MockServer.ERROR_RESPONSE)
                .header("foo", "bar")
                .asEither(RequestCapture.class, Foo.class)
                .getBody()
                .getFailValue()
                .getBar();

        assertEquals("I'm a error", value);
    }



}
