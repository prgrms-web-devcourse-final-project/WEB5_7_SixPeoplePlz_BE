package me.jinjjahalgae.global.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class ApiResponseSerializer extends JsonSerializer<ApiResponse<?>> {

    @Override
    public void serialize(ApiResponse<?> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();

        gen.writeBooleanField("success", value.isSuccess());

        if (value.isSuccess()) {
            // 성공 응답일 경우
            gen.writeObjectField("result", value.getResult());
        } else {
            // 실패 응답일 경우
            gen.writeObjectField("error", value.getError());
        }

        gen.writeEndObject();
    }
}