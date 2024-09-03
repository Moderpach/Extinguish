// IEventsListener.aidl
package extinguish.shizuku_service;

// Declare any non-default types here with import statements
import extinguish.shizuku_service.result.EventResult;

interface IEventsListener {
    void onEvent(in EventResult event);
}