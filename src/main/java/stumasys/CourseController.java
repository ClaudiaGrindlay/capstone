package stumasys;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Map;
import java.util.Iterator;

import stumasys.db.Database;
import stumasys.db.Course;

@Controller         // response functionality
public class CourseController {
    /*

    @Autowired      // links this with the CourseService
    CourseService cs;

    @RequestMapping(value = "/all")
    public Hashtable<String, Courses> getAll(){
        return cs.getAll();
    }
    @RequestMapping(value = "/{key}")       // something is wrong here...to tired to know what
    public Courses getCourse(@PathVariable("key") String key){
        return cs.getCourse(key);
    }
    */
    private Database db;

    @Autowired
    public void setDatabase(Database db) {
        this.db = db;
    }
    @RequestMapping(value = "/course/{year}/{courseCode}") // sorry for commenting out all of your code
    public String courseHandler(
            @PathVariable String year,
            @PathVariable String courseCode,
            Model model,
            HttpServletResponse servletRes
    ){

        // TODO: load more actual content into the Model (requires simultaneous work on the HTML template)
        /*
         * 1. Check if this course exists with the DB:
         *      if not, return error page. otherwise procede.
         * 2. load the last-used visible columns info from last visit
         * 3. put that vis-columns info into the JS and deliver the page
         * 4. client page then requests columns via REST api calls in the JS
         * */

        Course c = db.getCourse(courseCode, Integer.parseInt(year));

        if (c == null) {
            return "nope";// TODO: respond with a proper error page when course doesnt exist
        }

        model.addAttribute("courseCode", courseCode);
        model.addAttribute("year", year);

        return "course";
    }

    @RequestMapping(value = "/api/get_assessment/{code}/{year}/{aId}", produces = "application/json")
    @ResponseBody
    public String getAssessmentMarks(
            @PathVariable String code,
            @PathVariable String year,
            @PathVariable String aId
    ){
        Course c = db.getCourse(code, Integer.parseInt(year));
        if (c == null) {
            return "null";
        }

        Map<String,Integer> markTbl = c.getAssessment(Integer.parseInt(aId)).getWholeTable();

        Iterator<Map.Entry<String,Integer>> entryItr = markTbl.entrySet().iterator();

        if (!entryItr.hasNext()) {
            return "[]";
        }

        // encoding the mark table into JSON and returning it
        String ret = "[";
            Map.Entry<String,Integer> entry = entryItr.next();
            ret += "[\"" + entry.getKey() + "\",\"" + entry.getValue().toString() + "]";

            while (entryItr.hasNext()) {
                entry = entryItr.next();
                ret += ",[\"" + entry.getKey() + "\",\"" + entry.getValue().toString() + "]";
            }
        ret += "]";

        return ret;
    }
}
