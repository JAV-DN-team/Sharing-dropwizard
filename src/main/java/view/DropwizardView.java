package view;

import io.dropwizard.views.View;

import java.util.List;

/**
 * Created by FRAMGIA\dinh.thanh on 18/05/2018.
 */
public class DropwizardView extends View {

    private final Object data;
    private final List dataList;

    public DropwizardView(String templateName, Object data, List dataList) {
        super(templateName);
        this.data = data;
        this.dataList = dataList;
    }

    public Object getData() {
        return data;
    }

    public List getDataList() {
        return dataList;
    }
}
