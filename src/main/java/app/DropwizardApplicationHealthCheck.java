package app;

import com.codahale.metrics.health.HealthCheck;
import service.PilotService;

/**
 * Created by FRAMGIA\dinh.thanh on 18/05/2018.
 */
public class DropwizardApplicationHealthCheck extends HealthCheck {

    private static final String HEALTHY = "The Dropwizard Service is healthy for read and write";
    private static final String UNHEALTHY = "The Dropwizard Service is not healthy. ";
    private static final String MESSAGE_PLACEHOLDER = "{}";

    private final PilotService pilotService;

    public DropwizardApplicationHealthCheck(PilotService pilotService) {
        this.pilotService = pilotService;
    }

    @Override
    public Result check() throws Exception {
        String mySqlHealthStatus = pilotService.performHealthCheck();

        if (mySqlHealthStatus == null) {
            return Result.healthy(HEALTHY);
        } else {
            return Result.unhealthy(UNHEALTHY + MESSAGE_PLACEHOLDER, mySqlHealthStatus);
        }
    }
}
