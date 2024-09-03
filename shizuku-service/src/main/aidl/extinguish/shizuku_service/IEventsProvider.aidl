// IEventsProvider.aidl
package extinguish.shizuku_service;

import extinguish.shizuku_service.IEventsListener;

// Declare any non-default types here with import statements

interface IEventsProvider {
    boolean isRunning() = 0;
    void launch(String filter) = 1;
    void stop() = 2;
    oneway void registerListener(IEventsListener listener) = 3;
    oneway void unregisterListener(IEventsListener listener) = 4;

    void destroy() = 16777114;
}