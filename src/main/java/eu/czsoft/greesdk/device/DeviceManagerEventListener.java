package eu.czsoft.greesdk.device;

public interface DeviceManagerEventListener {
    enum Event {
        DEVICE_LIST_UPDATED,
        DEVICE_STATUS_UPDATED
    }

    void onEvent(Event event);
}
