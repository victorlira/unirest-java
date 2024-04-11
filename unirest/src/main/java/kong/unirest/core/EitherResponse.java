package kong.unirest.core;

class EitherResponse<S, F> extends BaseResponse<Either<S, F>> {
    private Either<S,F> either;

    EitherResponse(ObjectMapper objectMapper, RawResponse r, Class<S> successClass, Class<F> failureClass) {
        super(r);
        try {
            if (r.getStatus() < 400) {
                either = Either.success(objectMapper.readValue(r.getContentAsString(), successClass));
            } else {
                either = Either.failure(objectMapper.readValue(r.getContentAsString(), failureClass));
            }
        }catch (Exception e){
            either = Either.failure(null);
        }
    }

    @Override
    public Either<S, F> getBody() {
        return either;
    }

    @Override
    protected String getRawBody() {
        return null;
    }
}
