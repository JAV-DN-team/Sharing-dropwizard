package service;

import dao.PilotDao;
import model.Pilot;
import org.skife.jdbi.v2.sqlobject.CreateSqlObject;

import java.util.List;

/**
 * Created by FRAMGIA\dinh.thanh on 18/05/2018.
 */
public abstract class PilotService {

    @CreateSqlObject
    abstract PilotDao pilotDao();

    public void createTable() {
        pilotDao().createTable();
    }

    public List<Pilot> getPilots() {
        return pilotDao().getPilots();
    }

    public Pilot getPilot(Long id) {
        return pilotDao().getPilot(id);
    }

    public void createPilot(Pilot pilot) {
        pilotDao().createPilot(pilot);
    }

    public Pilot editPilot(Pilot pilot) {
        pilotDao().editPilot(pilot);
        return pilotDao().getPilot(pilot.getId());
    }

    public void deletePilot(Long id) {
        pilotDao().deletePilot(id);
    }

    public String performHealthCheck() {

        try {
            pilotDao().getPilots();
        } catch (Exception ex) {
            return ex.getCause().getLocalizedMessage();
        }

        return null;
    }
}
