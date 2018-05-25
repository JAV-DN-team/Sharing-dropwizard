package web;

import io.dropwizard.views.View;
import view.DropwizardView;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;

/**
 * Created by FRAMGIA\dinh.thanh on 18/05/2018.
 */
public abstract class AbstractResource {

    public DropwizardView getView(String viewName, Object data, List dataList) {
        return new DropwizardView(viewName, data, dataList);
    }

    public DropwizardView getView(String viewName, Object data) {
        return new DropwizardView(viewName, data, null);
    }

    public DropwizardView getView(String viewName, List dataList) {
        return new DropwizardView(viewName, null, dataList);
    }

}
