package technikum.RestAPI;

public class Data {
    private String time;
    private int value;

    public Data(String time, int value) {
        this.time = time;
        this.value = value;
    }

    public String getTime() {
        return time;
    }

    public int getValue() {
        return value;
    }
}
