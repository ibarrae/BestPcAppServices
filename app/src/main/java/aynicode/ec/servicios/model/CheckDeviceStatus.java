package aynicode.ec.servicios.model;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrength;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.List;

/**
 * Created by Esteban on 4/9/2018.
 */

public class CheckDeviceStatus implements CheckDeviceStatusInt {

    private Service service;
    private TelephonyManager tm;
    private List<CellInfo> cellInfoList;
    private CellInfo cellInfo;
    private boolean phoneHasSignal;
    private int phoneSignalLevel;
    private boolean doesNotHasPermission;
    private String TAG = CheckDeviceStatus.class.getSimpleName();

    public CheckDeviceStatus(Service service) {
        this.service = service;
    }

    @Override
    public boolean areServicesOk() {
        if (!phoneHasActiveSignal()) {
            return true;
        }
        return isGpsOn() && isInternetOn();
    }

    public boolean getDoesNotHasPermission() {
        return doesNotHasPermission;
    }

    private boolean isGpsOn() {
        LocationManager locationManager = (LocationManager) service.getSystemService(Context.LOCATION_SERVICE);
        boolean isLocationOn = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isLocationOn;
    }

    private boolean isInternetOn() {
        ConnectivityManager connectivityManager = (ConnectivityManager) service.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isNetworkOn = networkInfo != null && networkInfo.isConnectedOrConnecting();
        return isNetworkOn;
    }

    private boolean phoneHasActiveSignal() {
        tm = (TelephonyManager) service.getSystemService(Context.TELEPHONY_SERVICE);
        boolean hasNetwork = tm.getNetworkType() != TelephonyManager.NETWORK_TYPE_UNKNOWN;
        if (!hasNetwork) {
            return false;
        }
        if (ActivityCompat.checkSelfPermission(service, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            doesNotHasPermission = true;
        } else {
            doesNotHasPermission = false;
            cellInfoList = tm.getAllCellInfo();
            return doesPhoneHasSignal();
        }
        return true;
    }

    private boolean doesPhoneHasSignal() {
        if (cellInfoList == null || cellInfoList.isEmpty()) {
            return false;
        }
        handleCellInfo();
        return phoneHasSignal;
    }

    private void handleCellInfo() {
        cellInfo = cellInfoList.get(0);
        CellSignalStrengthCdma cellSignalStrengthCdma;
        if (cellInfo instanceof CellInfoCdma) {
            getCdmaSignal();
        } else if (cellInfo instanceof CellInfoWcdma) {
            getWcdmaSignal();
        } else if (cellInfo instanceof CellInfoGsm) {
            getGsmSignal();
        } else if (cellInfo instanceof CellInfoLte) {
            getLteSignal();
        }
        doesPhoneHasEnoughSignal();
    }

    private void getCdmaSignal() {
        CellInfoCdma cellInfoCdma = (CellInfoCdma) cellInfo;
        phoneSignalLevel = cellInfoCdma.getCellSignalStrength().getLevel();
    }

    private void getWcdmaSignal() {
        CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) cellInfo;
        phoneSignalLevel = cellInfoWcdma.getCellSignalStrength().getLevel();

    }

    private void getGsmSignal() {
        CellInfoGsm cellInfoGsm = (CellInfoGsm) cellInfo;
        phoneSignalLevel = cellInfoGsm.getCellSignalStrength().getLevel();
    }

    private void getLteSignal() {
        CellInfoLte cellInfoLte = (CellInfoLte) cellInfo;
        phoneSignalLevel = cellInfoLte.getCellSignalStrength().getLevel();
    }

    private void doesPhoneHasEnoughSignal() {
        phoneHasSignal = true;
        if (CellSignalStrength.SIGNAL_STRENGTH_NONE_OR_UNKNOWN == phoneSignalLevel
                || CellSignalStrength.SIGNAL_STRENGTH_POOR == phoneSignalLevel) {
            phoneHasSignal = false;
        }
    }
}
