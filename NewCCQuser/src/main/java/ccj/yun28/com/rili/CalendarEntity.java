package ccj.yun28.com.rili;


/**
 * Created by ycx on 16/11/13.
 */

public class CalendarEntity {

    String name;
    boolean hasSignIn;//是否已签到

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isHasSignIn() {
        return hasSignIn;
    }

    public void setHasSignIn(boolean hasSignIn) {
        this.hasSignIn = hasSignIn;
    }
}
