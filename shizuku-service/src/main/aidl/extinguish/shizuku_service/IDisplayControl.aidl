// IDisplayControl.aidl
package extinguish.shizuku_service;

import extinguish.shizuku_service.result.UnitResult;

// Declare any non-default types here with import statements

interface IDisplayControl {

    UnitResult setPowerModeToSurfaceControl(int mode) = 2;
    UnitResult setBrightnessToSurfaceControl(float brightness) = 4;
    UnitResult setBrightnessToSetting(int brightness) = 5;
    UnitResult setBrightnessModeToSetting(int mode) = 6;

    void destroy() = 16777114;
}