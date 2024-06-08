package hu.czsoft.greesdk.serialization;

import com.google.gson.*;
import hu.czsoft.greesdk.net.packets.packs.Pack;
import hu.czsoft.greesdk.net.packets.packs.clientbound.BindResponsePack;
import hu.czsoft.greesdk.net.packets.packs.clientbound.DeviceResponsePack;
import hu.czsoft.greesdk.net.packets.packs.clientbound.ResultPack;
import hu.czsoft.greesdk.net.packets.packs.serverbound.BindRequestPack;
import hu.czsoft.greesdk.net.packets.packs.serverbound.ChangeOptionRequestPack;
import hu.czsoft.greesdk.net.packets.packs.serverbound.StatusResponsePack;

import java.lang.reflect.Type;

public class PackDeserializer implements JsonDeserializer<Pack> {

    @Override
    public Pack deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        String packType = jsonObject.get("t").getAsString();

        if (packType.equalsIgnoreCase(BindRequestPack.TYPE)) {
            return jsonDeserializationContext.deserialize(jsonObject, BindRequestPack.class);
        } else if (packType.equalsIgnoreCase(BindResponsePack.TYPE)) {
            return jsonDeserializationContext.deserialize(jsonObject, BindResponsePack.class);
        } else if (packType.equalsIgnoreCase(ChangeOptionRequestPack.TYPE)) {
            return jsonDeserializationContext.deserialize(jsonObject, ChangeOptionRequestPack.class);
        } else if (packType.equalsIgnoreCase(ResultPack.TYPE)) {
            return jsonDeserializationContext.deserialize(jsonObject, ResultPack.class);
        } else if (packType.equalsIgnoreCase(StatusResponsePack.TYPE)) {
            return jsonDeserializationContext.deserialize(jsonObject, StatusResponsePack.class);
        } else if (packType.equalsIgnoreCase(DeviceResponsePack.TYPE)) {
            return jsonDeserializationContext.deserialize(jsonObject, DeviceResponsePack.class);
        }

        return null;
    }
}
