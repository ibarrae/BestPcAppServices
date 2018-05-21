package aynicode.ec.servicios.presenter;

import java.util.Timer;
import java.util.TimerTask;

import aynicode.ec.servicios.model.CheckDeviceStatus;
import aynicode.ec.servicios.services.CheckDeviceStatusService;
import aynicode.ec.servicios.view.CheckDeviceStatusView;

/**
 * Created by Esteban on 4/9/2018.
 */

public class CheckDeviceStatusPresenter implements CheckDeviceStatusPresenterInt {

    private CheckDeviceStatusView checkDeviceStatusView;
    private CheckDeviceStatus checkDeviceStatus;
    private long taskTime = 5 * 1000;

    public CheckDeviceStatusPresenter(CheckDeviceStatusView checkDeviceStatusView, CheckDeviceStatusService checkDeviceStatusService) {
        this.checkDeviceStatusView = checkDeviceStatusView;
        this.checkDeviceStatus = new CheckDeviceStatus(checkDeviceStatusService);
    }

    @Override
    public void checkDeviceStatusContinously() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                boolean servicesOk = checkDeviceStatus.areServicesOk();
                if (servicesOk) {
                    checkDeviceStatusView.hideStatusDialog();
                } else {
                    checkDeviceStatusView.showStatusDialog();
                }
            }
        }, 0, taskTime);
    }

    @Override
    public void checkDeviceStatusManually() {
        if (checkDeviceStatus.areServicesOk()) {
            checkDeviceStatusView.hideStatusDialog();
            checkDeviceStatusView.showServiceEnabledToast();
        } else {
            checkDeviceStatusView.showServiceErrorToast();
        }
    }
}
