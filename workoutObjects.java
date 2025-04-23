import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class workoutObjects
{
    private List<Workout> workouts;

    public workoutObjects()
    {
        workouts = new ArrayList<>();
    }

    public static void main(String[] args)
    {
        //workoutObjects workoutObjects = new workoutObjects();
        //workoutObjects.readFromCSV(0, 100, 1);
        // if you want like 5 accounts worth of workouts
        workoutObjects[] Ai = new workoutObjects[5];
        for (int i = 0; i < Ai.length; i++)
        {
            Ai[i] = new workoutObjects();
            Ai[i].readFromCSV(i+100, 200, 5);
        }
    }

    public void readFromCSV(int n, int m, int k)
    {
        for (int i = n; i < m; i+=k)
        {
            ReadCSVWithHeaders readCSVWithHeaders = new ReadCSVWithHeaders("C:\\Users\\isaac\\OneDrive - University of Bath\\computer science\\programming 2 - java\\GroupProject\\WorkoutObjects\\src\\main\\resources\\factivity" + i + "_metrics.csv");
            workouts.add(readCSVWithHeaders.getWorkout());

            /*double initialElevation = workouts.get(i-100).getSplits().get(15).getAltitude();
            for (Split split : workouts.get(i-100).getSplits())
            {
                System.out.println("Distance: " + split.getDistance() + " Speed: " + split.getSpeed() + " HeartRate: " + split.getHeartRate() + " TotalDistance: " + split.getSumDuration() + " ElevationGain: " + (split.getAltitude() - initialElevation));
            }*/
        }
    }
}
