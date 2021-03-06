package stumasys.db;

import java.util.Map;

public interface Assessment {
    public int getMarkCap();
    public int getStudentMark(String id);
    public int getUncappedStudentMark(String id);
    public Map<String, Integer> getWholeTable();
}
