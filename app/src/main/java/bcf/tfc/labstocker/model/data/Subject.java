package bcf.tfc.labstocker.model.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bcf.tfc.labstocker.adapters.ItemFeed;
import bcf.tfc.labstocker.utils.Utils;

public class Subject {

    private String id;
    private String name;
    private String career;
    private int year;
    private int semester;
    private ArrayList<Practice> practices;


    /**
     * Empty constructor for Firebase
     */
    public Subject() {
        this.practices = new ArrayList<>();
    }

    public Subject(String id,String name, String career, int year, int semester) {
        this.name = name;
        this.career = career;
        this.year = year;
        this.semester = semester;
        this.practices = new ArrayList<>();
        if (id == null) {
            this.id = Utils.generateId("S",name, career);
        } else {
            this.id = id;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCareer() {
        return career;
    }

    public void setCareer(String career) {
        this.career = career;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public void addPractice(Practice practice) {
        practices.add(practice);
    }

    public void addAllPractices(List<Practice> practices) {
        this.practices.addAll(practices);
    }

    public Practice getPractice(String id) {
        for (int i = 0; i < practices.size(); i++) {
            if (practices.get(i).getId().equals(id)) {
                return practices.get(i);
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return name;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("name", name);
        map.put("career", career);
        map.put("year", year);
        map.put("semester", semester);

        List<Map<String, Object>> practices = new ArrayList<>();
        for (int i = 0; i < this.practices.size(); i++) {
            practices.add(this.practices.get(i).toMap());

        }
        map.put("practices", practices);
        return map;
    }

    public static Subject fromMap(Map<String, Object> map) {
        String id = (String) map.get("id");
        String name = (String) map.get("name");
        String career = (String) map.get("career");
        int year = (int) (long) map.get("year");
        int semester = (int) (long) map.get("semester");
        Subject subject = new Subject(id, name, career, year, semester);

        return subject;
    }

    public void removeItem(String id) {
        for (int i = 0; i < practices.size(); i++) {
            if (practices.get(i).getId().equals(id)) {
                practices.remove(i);
            }
        }
    }

    public ArrayList<ItemFeed> getPracticesFeed() {
        ArrayList<ItemFeed> practicesFeed = new ArrayList<>();
        for (int i = 0; i < practices.size(); i++) {
            Practice p = practices.get(i);
            practicesFeed.add(new ItemFeed(p.getId(), p.getName(),null, this.name));
        }
        return practicesFeed;
    }
}
