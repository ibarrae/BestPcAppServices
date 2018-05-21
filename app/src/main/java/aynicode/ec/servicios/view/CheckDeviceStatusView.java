package aynicode.ec.servicios.view;

/**
 * Created by Esteban on 4/9/2018.
 */

public interface CheckDeviceStatusView {

    void showStatusDialog();

    void hideStatusDialog();

    void showServiceEnabledToast();

    void showServiceErrorToast();

    void askPermissions();
}
